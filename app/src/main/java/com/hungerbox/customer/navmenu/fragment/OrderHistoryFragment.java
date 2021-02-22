package com.hungerbox.customer.navmenu.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.BookedSlot;
import com.hungerbox.customer.model.BookingGuest;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.CompanyResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.MenuOptionResponse;
import com.hungerbox.customer.model.OptionItem;
import com.hungerbox.customer.model.OptionItemResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderGuestInfo;
import com.hungerbox.customer.model.OrderOptionItem;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderSubProduct;
import com.hungerbox.customer.model.OrdersReponse;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.SlotList;
import com.hungerbox.customer.model.SlotListResponse;
import com.hungerbox.customer.model.SubProduct;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.VendorResponse;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.navmenu.OnReorderInterface;
import com.hungerbox.customer.navmenu.PaginationHandler;
import com.hungerbox.customer.navmenu.listeners.UpdateListener;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.spaceBooking.SpaceBookingActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;


public class OrderHistoryFragment extends Fragment implements PaginationHandler, UpdateListener, OnReorderInterface, VendorValidationListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    int currentPage = 1;
    boolean pageOver = false;
    boolean pageOverForArchived = false;
    private HistoryFragment.OnListFragmentInteractionListener mListener;
    private OrderHistoryViewAdapter historyAdapter;
    private View view;
    private RecyclerView recyclerView;
    private TextView tvNoBookings;
    private String historyType = "";
    private ProgressBar pbHistory;
    private ArrayList<Order> orders = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout moreHistory;
    TextView olderOrderHistoryText;
    private String value = "";
    Realm realm;
    boolean loadingArchivedOrders = false;
    Activity activity;


    public OrderHistoryFragment() {
    }

    public static OrderHistoryFragment newInstance(int columnCount, String historyType, String value,Activity activity) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        fragment.historyType = historyType;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        fragment.value = value;
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_history_list_food, container, false);
        recyclerView = view.findViewById(R.id.list);
        tvNoBookings = view.findViewById(R.id.tv_no_bookings);
        moreHistory = view.findViewById(R.id.view_more_order_history);
        olderOrderHistoryText = view.findViewById(R.id.older_order_history_text);
        refreshLayout = view.findViewById(R.id.srl_history);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(() -> {

            currentPage = 1;
            moreHistory.setVisibility(View.GONE);
            loadingArchivedOrders = false;
            pageOver = false;
            pageOverForArchived = false;
            getOrdersDetailFromServer(currentPage, false);

        });

        SpannableString content = new SpannableString(olderOrderHistoryText.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        olderOrderHistoryText.setText(content);

        olderOrderHistoryText.setOnClickListener(v ->{
            moreHistory.setVisibility(View.GONE);
            currentPage = 1;
            loadingArchivedOrders = true;
            getOrdersDetailFromServer(currentPage, false);
        });

        pbHistory = view.findViewById(R.id.pb_history);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();

        currentPage = 1;
        getOrdersDetailFromServer(currentPage, false);
        refreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                                .getDisplayMetrics()));
        refreshLayout.setRefreshing(true);
    }

    private void getOrdersDetailFromServer(final int page, final boolean showDialog) {
        //page, page_len, with_items=bool, with_vendor=bool, with_user=bool, with_rating=bool
        String url;

        if(loadingArchivedOrders)
            url = UrlConstant.LAST_ARCHIVED_ORDER + page + "/10/1/1/0/1";
        else
            url = UrlConstant.LAST_ORDER + page + "/10/1/1/0/1";

        if (showDialog) {
            pbHistory.setVisibility(View.VISIBLE);
        }

        SimpleHttpAgent<OrdersReponse> orderSimpleHttpAgent = new SimpleHttpAgent<>(
                getActivity(),
                url,
                responseObject -> {
                    if (responseObject != null) {

                        if(loadingArchivedOrders && responseObject.orders != null){
                            for(Order order : responseObject.orders)
                                order.setArchived(true);
                        }

                        setuplastOrderList(responseObject.orders, page);
                    }
                    pbHistory.setVisibility(View.GONE);
                    refreshLayout.setRefreshing(false);
                },
                (errorCode, error, errorResponse) -> {
                    if (getActivity() != null) {
                        Snackbar.make(recyclerView, "Unable to get your order history", Snackbar.LENGTH_LONG)
                                .setAction("Retry", view -> getOrdersDetailFromServer(page, showDialog))
                                .show();
                        pbHistory.setVisibility(View.GONE);
                    }
                    refreshLayout.setRefreshing(false);
                },
                OrdersReponse.class
        );

        HashMap<String, Object> orderHistoryRequest = new HashMap<>();
        if (historyType.equalsIgnoreCase(ApplicationConstants.HISTORY_TYPE_GUEST_ORDER))
            orderHistoryRequest.put("type", ApplicationConstants.HISTORY_TYPE_GUEST_ORDER);
        else if(historyType.equalsIgnoreCase(ApplicationConstants.CURRENT_BOOKING)){
            orderHistoryRequest.put("type","space_booking");
        }

        orderSimpleHttpAgent.post(orderHistoryRequest, new HashMap<String, JsonSerializer>());
    }

    private void setuplastOrderList(ArrayList<Order> ordersList, int page) {
        if (getActivity()!=null && AppUtils.getConfig(getActivity()).getReordering() != null && AppUtils.getConfig(getActivity()).getReordering().isReordering_enabled()) {
            checkForReorder(ordersList);
        }
        if (page == 1 && !loadingArchivedOrders) {
            this.orders.clear();
        }
        if (this.orders != null) {
            this.orders.addAll(ordersList);
        } else {
            this.orders = new ArrayList<>();
            this.orders.addAll(ordersList);
        }

        if(value != null && value.equals("last_order") && this.orders.size() >0){
            Intent intent = new Intent(getActivity(), OrderDetailNewActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Shortcut");
            intent.putExtra(ApplicationConstants.BOOKING_ID, this.orders.get(0).getId());
            getActivity().startActivity(intent);
            value = null;
        }

        if (currentPage == page - 1) {
            currentPage = page;
        }

        if(loadingArchivedOrders){
            pageOverForArchived = ordersList.size() < 10;
        }
        else{
            pageOver = ordersList.size() < 10;
        }

        if (recyclerView.getAdapter() == null) {
            historyAdapter = new OrderHistoryViewAdapter(getActivity(),
                    this.orders, this, mListener, historyType,this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(historyAdapter);
        } else {
            if (recyclerView.getAdapter() instanceof OrderHistoryViewAdapter) {
                historyAdapter.udpateOrders(this.orders);
                historyAdapter.notifyDataSetChanged();
            }
        }
        if (orders.size() == 0 && loadingArchivedOrders)
            tvNoBookings.setVisibility(View.VISIBLE);
        else
            tvNoBookings.setVisibility(View.GONE);

        if (orders.size() == 0 && !loadingArchivedOrders){
            moreHistory.setVisibility(View.VISIBLE);
        }
    }

    /* Checking if the order is eligible for re-ordering by checking loaction id and  vendor is available or not */
    private void checkForReorder(ArrayList<Order> orders){
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID,0);
        ArrayList<Order> modifiedOrders = new ArrayList<>();
        for (Order order:orders){
            if ((order.getLocationId()==locationId)){
                modifiedOrders.add(order);
            }
        }


        for(Order order : modifiedOrders){
            Vendor vendorRealmResultsLocal = AppUtils.getVendorById(getContext(),order.getVendorId());
            if(vendorRealmResultsLocal != null && !vendorRealmResultsLocal.isBigBasketVendor()){
                order.setReorderAvailable(true);
            }
        }

//        for (Order order:orders){
//            if ((order.getLocationId()==locationId)){
//                if (vendorRealmResults.size()>0 && vendorRealmResults.isValid() ){
//                    for (Vendor vendor:vendorRealmResults){
//                        if(vendor.isValid()) {
//                            if (order.getVendorId() == vendor.getId() && vendor.getActive() == 1) {
//                                order.setReorderAvailable(true);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HistoryFragment.OnListFragmentInteractionListener) {
            mListener = (HistoryFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void update() {
        currentPage = 1;
        getOrdersDetailFromServer(currentPage, false);
    }

    @Override
    public void onLastItemReached() {

        if(loadingArchivedOrders) {

            if (pageOverForArchived)
                noMoreMessage();
            else
                getOrdersDetailFromServer(currentPage + 1, true);

        }
        else {

            if (pageOver)
                moreHistory.setVisibility(View.VISIBLE);
            else
                getOrdersDetailFromServer(currentPage + 1, true);

        }
    }

    private void noMoreMessage(){
        AppUtils.showToast("No more entries to show", true, 2);
    }

    @Override
    public void updateData() {
        update();
    }

    @Override
    public void getMenu(final Order order) {
        if(order.reorderType>1){
            handleSpaceBookingReorder(order);
        }else {
            pbHistory.setVisibility(View.VISIBLE);
            final long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
            final long occasionId = MainApplication.selectedOcassionId;
            final List<Product> products = new ArrayList<>();
            String url = UrlConstant.VENDOR_MENU_GET + "?vendorId=" + order.getVendorId() + "&occasionId=" + occasionId + "&locationId=" + locationId;
            if (getActivity() != null) {
                SimpleHttpAgent<VendorResponse> categoryHttpAgent = new SimpleHttpAgent<>(
                        getActivity(),
                        url,
                        new ResponseListener<VendorResponse>() {
                            @Override
                            public void response(VendorResponse responseObject) {
                                LogoutTask.updateTime();
                                if (responseObject != null && responseObject.vendor != null &&
                                        responseObject.vendor.menu != null &&
                                        responseObject.vendor.menu.products != null &&
                                        responseObject.vendor.menu.products.size() > 0) {
                                    products.addAll(responseObject.vendor.menu.products);
                                    checkItemsAndOrder(products, order, responseObject.vendor);
                                } else {
                                    pbHistory.setVisibility(View.GONE);
                                    AppUtils.showToast("All Items are not available", true, 0);
                                }
                            }
                        },
                        new ContextErrorListener() {
                            @Override
                            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                                pbHistory.setVisibility(View.GONE);
                                AppUtils.showToast(error, true, 0);
                            }
                        },
                        VendorResponse.class
                );
                categoryHttpAgent.get();
            }
        }
    }

    //Verifying whether all the items in previous order are present in the current menu
    private void checkItemsAndOrder(List<Product> products,Order order,Vendor vendor){
        boolean allProductsAvailable=true;
        HashMap<Product,Integer> productQuantityHashMap = new HashMap<>();
        //All products from previous order
        for (OrderProduct orderProduct:order.getProducts())
        {
            boolean check = false;
            // All products in the menu
            for (Product product: products)
            {
                if (orderProduct.getId()==product.getId()){
                    //If the product is customizable checking for sub products
                   if(checkForSubProducts(orderProduct,product)){
                       if(productQuantityHashMap.containsKey(product)) {
                           productQuantityHashMap.put(product, productQuantityHashMap.get(product)+orderProduct.getQuantity());
                       }
                       else{
                           productQuantityHashMap.put(product, orderProduct.getQuantity());
                       }

                       check=true;
                       break;
                   }
                }
            }
            if (!check){
                productQuantityHashMap.clear();
                allProductsAvailable=false;
                break;
            }

        }
        AppUtils.HbLog("test",productQuantityHashMap+" ");
        if (allProductsAvailable && !productQuantityHashMap.isEmpty()){
            //AppUtils.showToast("All products are available",true,1);
            if (getActivity()!=null) {
                MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                Cart cart = mainApplication.getCart();
                AppUtils.HbLog("test", "" + cart);
                if (cart.getOrderProducts().size() == 0) {
                    createCart(productQuantityHashMap, vendor, order);
                } else {
                    showCartClearPopUp(productQuantityHashMap, vendor, order);
                }
            }
        }
        else {
            pbHistory.setVisibility(View.GONE);
            //event
//            try{
//                if (getActivity()!=null) {
//                    HashMap map = new HashMap<>();
//                    map.put(CleverTapEvent.PropertiesNames.getOrderId(), order.getOrderId());
//                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getItems_not_available(), map, getActivity());
//                }
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            AppUtils.showToast("All Items are not available",true,0);
        }
    }

    //Verifying whether all the sub products in previous order are present in the current menu
    private boolean checkForSubProducts(OrderProduct orderProduct,Product product){

        RealmList<SubProduct> subProducts = new RealmList<>();

        if (orderProduct.getSubProducts()!=null && orderProduct.getSubProducts().size()>0)
        {
            boolean allSubProductsAvailable=true;
            // For all the sub products of product in the previous order
            for (OrderSubProduct orderSubProduct:orderProduct.getSubProducts())
            {
                boolean check2 = false;
                //  For all the sub products of product in menu
                for (SubProduct subProduct : product.getOptionResponse().getSubProducts())
                {
                    if (subProduct.getId()==orderSubProduct.getId()){
                        //checking for menuoption items
                        if(checkForOptionItems(orderSubProduct,subProduct)){
                            subProducts.add(subProduct);
                            check2=true;
                            break;
                        }
                        else
                            return false;
                    }
                }
                if (!check2){
                    subProducts.clear();
                    allSubProductsAvailable=false;
                    break;

                }
            }
            if (allSubProductsAvailable){
                MenuOptionResponse menuOptionResponse =new MenuOptionResponse();
                menuOptionResponse.setSubProducts(subProducts);
                product.setOptionResponse(menuOptionResponse);
            }
            return allSubProductsAvailable;
        }
        return true;

    }

    //Verifying whether all the optional items in previous order are present in the current menu
    private boolean checkForOptionItems(OrderSubProduct orderSubProduct, SubProduct subProduct){

        RealmList<OptionItem> optionItems = new RealmList<>();
        if (orderSubProduct.getOrderOptionItems()!=null && orderSubProduct.getOrderOptionItems().size()>0){
            boolean allOptionItemsAvailable=true;

            int size = orderSubProduct.getOrderOptionItems().size();
            //Checking the number of option items is in the range of minselect and maxselect of the subProduct
            if(size<subProduct.getMinimumSelection() || size>subProduct.getMaximumSelection()){
                return false;
            }

            // For all the menu options of sub-product in the previous order
            for (OrderOptionItem orderOptionItem : orderSubProduct.getOrderOptionItems())
            {
                boolean check2 = false;
                //  For all the menu options of sub-product in menu
                for (OptionItem optionItem : subProduct.getMenuOptionsItems())
                {
                    if (orderOptionItem.getId()==optionItem.getId()){
                        optionItems.add(optionItem);
                        check2=true;
                        break;
                    }
                }
                if (!check2){
                    optionItems.clear();
                    allOptionItemsAvailable=false;
                    break;

                }
            }
            if (allOptionItemsAvailable){
                OptionItemResponse optionItemResponse = new OptionItemResponse();
                optionItemResponse.setMenuOptionsItems(optionItems);
                subProduct.setOptionItemResponse(optionItemResponse);
            }
            return allOptionItemsAvailable;
        }
        return true;

    }

    private void createCart(HashMap<Product,Integer> productQuantityHashMap,Vendor vendor,Order order){
        if (getActivity()!=null) {
            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
            Cart cart = mainApplication.getCart();
            cart.showedCartErrorPopup = false;

            for (Product orderProduct : productQuantityHashMap.keySet()) {
                int qty = productQuantityHashMap.get(orderProduct);
                orderProduct.setRecommendationType(ApplicationConstants.RecommendationType.REORDER);

                for (int i = 0; i < qty; i++) {
                    cart.addProductToCart(orderProduct, null, mainApplication, this, (AppCompatActivity) getActivity(), vendor, MainApplication.selectedOcassionId, new HashMap<String, Object>());

                    if(cart.showedCartErrorPopup == true)
                        break;
                }

                if(cart.showedCartErrorPopup == true)
                    break;

            }
            if(cart.showedCartErrorPopup == true){

                // Resetting Cart
                mainApplication.clearOrder();
                pbHistory.setVisibility(View.GONE);
            }
            else {
                // Adding option items to the products in cart
                for (OrderProduct orderProduct : cart.getOrderProducts()) {
                    RealmList<SubProduct> subProducts = orderProduct.getProduct().getOptionResponse().getSubProducts();
                    if (subProducts.size() > 0) {
                        ArrayList<OrderSubProduct> orderSubProducts = new ArrayList<>();

                        for (SubProduct subProduct : subProducts) {
                            OrderSubProduct orderSubProduct = new OrderSubProduct();
                            orderSubProduct.copy(subProduct);
                            for (OptionItem optionItem : subProduct.getMenuOptionsItems()) {
                                OrderOptionItem orderOptionItem = new OrderOptionItem();
                                orderOptionItem.copy(optionItem);
                                orderSubProduct.getOrderOptionItems().add(orderOptionItem);

                            }
                            orderSubProducts.add(orderSubProduct);

                        }

                        orderProduct.setSubProducts(orderSubProducts);

                    }
                }
                pbHistory.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), BookmarkPaymentActivity.class);
                intent.putExtra("PrevOrder", order);
                intent.putExtra(ApplicationConstants.FROM_REORDER, true);
                startActivity(intent);
            }
        }

    }


    //Handling space booking reorder => reorder_type = 2 and reorder_type = 3
    private void handleSpaceBookingReorder(Order order){

        pbHistory.setVisibility(View.VISIBLE);
        String url = UrlConstant.LIST_LOCATION_WITH_ADDRESS+"?type="+order.getLocation().getType();
        SimpleHttpAgent<CompanyResponse> companyResponseSimpleHttpAgent = new SimpleHttpAgent<CompanyResponse>(
                getActivity(),
                url,
                new ResponseListener<CompanyResponse>() {
                    @Override
                    public void response(CompanyResponse responseObject) {
                            if(responseObject!=null && responseObject.companyResponse!=null && responseObject.companyResponse.locationResponse!=null
                            && responseObject.companyResponse.locationResponse.locations!=null && responseObject.companyResponse.locationResponse.locations.size()>0
                            ){
                                boolean locationAvailable = false;
                                for (Location location : responseObject.companyResponse.locationResponse.locations){
                                    if(location.id == order.getLocationId()){
                                        locationAvailable = true;
                                        if(order.reorderType == 2){
                                            navigateToBookingActivity(location,order);
                                        }else {
                                            getSlotList(order, location);
                                        }
                                        break;
                                    }
                                }

                                if(!locationAvailable){
                                    pbHistory.setVisibility(View.GONE);
                                    AppUtils.showToast("No locations available",true,0);
                                }

                            }else{
                                pbHistory.setVisibility(View.GONE);
                                AppUtils.showToast("No locations available",true,0);
                            }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.showToast(error, true, 0);
                        pbHistory.setVisibility(View.GONE);
                    }
                },
                CompanyResponse.class
        );
        companyResponseSimpleHttpAgent.get();

    }

    private void navigateToBookingActivity(Location location,Order order) {
        Intent intent = new Intent(getActivity(), SpaceBookingActivity.class);
        intent.putExtra(ApplicationConstants.SpaceBooking.AUTO_SELECT_LOCATION_ID,location.id);
        intent.putExtra(ApplicationConstants.SpaceBooking.SPACE_TYPE,order.getLocation().getType());
        startActivity(intent);

    }

    private void getSlotList(Order order, Location location){
        String url = UrlConstant.LIST_LOCATION_SLOT+ "?location_id="+order.getLocationId();

        SimpleHttpAgent<SlotListResponse> slotListResponseSimpleHttpAgent = new SimpleHttpAgent<SlotListResponse>(
                getActivity(),
                url,
                new ResponseListener<SlotListResponse>() {
                    @Override
                    public void response(SlotListResponse responseObject) {
                            if(responseObject!=null && responseObject.getSlotLists()!=null && responseObject.getSlotLists().size()>0 && order.getBookedSlotData().getBookedSlots().size()>0 ){
                                BookedSlot bookedSlot = order.getBookedSlotData().getBookedSlots().get(0);
                                boolean slotAvailable = false;
                                for(SlotList slotList : responseObject.getSlotLists()){
                                    if (slotList.getDate().equalsIgnoreCase(bookedSlot.getDate()) && slotList.getSlot_start_time().equalsIgnoreCase(bookedSlot.getEndTime())){
                                     slotAvailable = true;
                                     getSlotSpaces(order,slotList,location);
                                     break;
                                    }
                                }

                                if(!slotAvailable){
                                    AppUtils.showToast("No time slots are available",true,0);
                                }

                            }else{
                                AppUtils.showToast("No time slots are available",true,0);
                            }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.showToast(error, true, 0);
                        pbHistory.setVisibility(View.GONE);
                    }
                },
                SlotListResponse.class
        );
        slotListResponseSimpleHttpAgent.get();
    }

    private void getSlotSpaces(Order order , SlotList slotList,Location location){

        String url = UrlConstant.LIST_SPACES+ "?location_id="+order.getLocationId()+"&occasion_id="+MainApplication.selectedOcassionId;

        SimpleHttpAgent<VendorResponse> slotResponseAgent = new SimpleHttpAgent<VendorResponse>(getActivity(),
                url,
                new ResponseListener<VendorResponse>() {
                    @Override
                    public void response(VendorResponse responseObject) {
                        if(responseObject!=null && responseObject.vendor!=null && responseObject.vendor.spaces!=null && responseObject.vendor.spaces.size()>0){
                            boolean spaceAvailable = false;
                            for(Product product : responseObject.vendor.spaces){
                                if(product.getSlotId()==slotList.getSlot_id()
                                        && product.getId() == order.getProducts().get(0).getId()
                                        && product.getDate().equalsIgnoreCase(slotList.getDate())
                                        && product.getMaxQty()>=order.getQuantity()){
                                    spaceAvailable = true;
                                    createBookingCart(product,order,responseObject.vendor,location);
                                    break;
                                }
                            }

                            if(!spaceAvailable){
                                pbHistory.setVisibility(View.GONE);
                                showReorderDialog(order,location);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        AppUtils.showToast(error, true, 0);
                        pbHistory.setVisibility(View.GONE);
                    }
                },
                VendorResponse.class);
        slotResponseAgent.get();
    }

    private void showReorderDialog(Order order, Location location) {
        if (getActivity()!=null) {
            FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                    "Spaces are not available for the current slot\n" +
                            "        Do you want to rebook for other time slots? ",
                    new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                            mainApplication.clearOrder();
                            navigateToBookingActivity(location,order);
                        }

                        @Override
                        public void onNegativeButtonClicked() {

                        }
                    }, "OK", "CANCEL");

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(orderErrorHandleDialog, "cart_clear")
                    .commitAllowingStateLoss();
        }
    }


    private void createBookingCart(Product product, Order order, Vendor vendor, Location location){
        if (getActivity()!=null) {
            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
            Cart cart = mainApplication.getCart();
            cart.showedCartErrorPopup = false;

            vendor.setStartTime("00:00:00");
            vendor.setEndTime("23:59:59");
            Config.SpaceType config = AppUtils.getSpaceType(activity, order.getLocation().getType());
            if(config!=null) {
                vendor.cart_error = config.getCart_error();
            }

            if(cart.getOrderProducts().size() == 0){
                for (int i=0;i<order.getQuantity();i++){
                    cart.addProductToCart(product,null,mainApplication,this,(AppCompatActivity) getActivity(),vendor,MainApplication.selectedOcassionId,new HashMap<String, Object>());
                    if(cart.showedCartErrorPopup){
                        break;
                    }
                }

                if(cart.showedCartErrorPopup){
                    // Resetting Cart
                    mainApplication.clearOrder();
                    pbHistory.setVisibility(View.GONE);
                }else{
                    DbHandler.getDbHandler(getActivity()).createVendor(vendor);
                    if(order.getOrderGuestInfo().size()>0) {
                        ArrayList<BookingGuest> bookingGuests = new ArrayList<>();
                        for (OrderGuestInfo orderGuestInfo : order.getOrderGuestInfo()){
                            bookingGuests.add(new BookingGuest(orderGuestInfo.getName(),orderGuestInfo.getEmail(),orderGuestInfo.getPhoneNo()));
                        }
                            cart.setSpaceGuests(bookingGuests);
                    }

                    String spaceLocationName = location.getName()+","+location.getAddressLine1()+","+location.getCityName();

                    Intent intent = new Intent(getActivity(),BookmarkPaymentActivity.class);
                    intent.putExtra(ApplicationConstants.SPACE_LOCATION_NAME,spaceLocationName);
                    intent.putExtra(ApplicationConstants.FROM_SPACE_BOOKING,true);
                    intent.putExtra(ApplicationConstants.FROM_SPACE_BOOKING_REORDER,true);
                    intent.putExtra(ApplicationConstants.SPACE_LOCATION_ID,location.id);
                    intent.putExtra(ApplicationConstants.SpaceBooking.SPACE_TYPE,order.getLocation().getType());
                    startActivity(intent);

                }
            }else{
             showCartClearPopUp(product,order,vendor,location);
            }
        }
    }

    private void showCartClearPopUp(Product product, Order order, Vendor vendor, Location location) {
        if (getActivity()!=null) {
            FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                    "You have products in your cart.\n" +
                            "        Do you want to remove them and add this? ",
                    new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                            mainApplication.clearOrder();
                            createBookingCart(product,order,vendor,location);
                        }

                        @Override
                        public void onNegativeButtonClicked() {

                        }
                    }, "OK", "CANCEL");

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(orderErrorHandleDialog, "cart_clear")
                    .commitAllowingStateLoss();
        }
    }


    private void showCartClearPopUp(final HashMap<Product,Integer> productQuantityHashMap, final Vendor vendor, final Order order) {
        if (getActivity()!=null) {
            FreeOrderErrorHandleDialog orderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(new Order(),
                    "You have products in your cart.\n" +
                            "        Do you want to remove them and add this? ",
                    new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveButtonClicked() {
                            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
                            mainApplication.clearOrder();
                            createCart(productQuantityHashMap, vendor, order);
                        }

                        @Override
                        public void onNegativeButtonClicked() {

                        }
                    }, "OK", "CANCEL");

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(orderErrorHandleDialog, "cart_clear")
                    .commitAllowingStateLoss();
        }
    }


    @Override
    public void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffet) {
    }

}
