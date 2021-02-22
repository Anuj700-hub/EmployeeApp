package com.hungerbox.customer.order.activity;


import android.content.Intent;
import android.database.StaleDataException;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Guest;
import com.hungerbox.customer.model.GuestListResponse;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.ProductResponse;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.GuestMenuListAdapter;
import com.hungerbox.customer.order.fragment.CartCancelDialog;
import com.hungerbox.customer.order.fragment.FreeOrderErrorHandleDialog;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GuestOrderActivity extends ParentActivity {


    Button placeOrder;
    //    Spinner spGuestCount;
    EditText etGuestCount;
    int guestCount = 0;
    Toolbar toolbar;
    ArrayList<Guest> guests;
    String cafe;
    List<Product> products;
    RadioGroup rgCafe;
    RadioButton rbReg, rbGda;
    ProgressBar pbLoad;
    long locationId;
    Cart cart;
    RecyclerView rvMenuList;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_order);
        rvMenuList = findViewById(R.id.rl_guest_menu_list);
        toolbar = findViewById(R.id.tb_global);


        rgCafe = findViewById(R.id.rg_order_place);
        rbReg = findViewById(R.id.rb_reg);
        rbGda = findViewById(R.id.rb_gda);
        pbLoad = findViewById(R.id.pb);
        iv_back = findViewById(R.id.iv_back);

//        spGuestCount = (Spinner)findViewById(R.id.sp_guest_count);
        etGuestCount = findViewById(R.id.et_guest_count);
        etGuestCount.setEnabled(false);
        placeOrder = findViewById(R.id.bt_place_order);


        rvMenuList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 8);

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleClickOfPlaceOrder();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cart = new Cart();
        cart.setOrderProducts(new ArrayList<OrderProduct>());
        getGuestMenu();

        LogoutTask.updateTime();
        rgCafe.setVisibility(View.INVISIBLE);
        rbGda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cafe = "gda";
            }
        });

        rbReg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cafe = "reg";
            }
        });


    }

    private void getGuestMenu() {
        String url = UrlConstant.GUEST_MENU;
        SimpleHttpAgent<ProductResponse> productResponseSimpleHttpAgent = new SimpleHttpAgent<ProductResponse>(
                this,
                url,
                new ResponseListener<ProductResponse>() {
                    @Override
                    public void response(ProductResponse responseObject) {
                        if (responseObject != null) {
                            if (responseObject.products != null && responseObject.products.size() > 0) {
                                ArrayList<Product> productsList = new ArrayList<>();
                                productsList.addAll(responseObject.products);
                                products = productsList;
                                setupProducts();
                            } else {
                                showErrorDialog(null, "No Menu found for your account please add guest", false);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        showErrorDialog(null, "No Menu found for your account please add guest", false);
                    }
                },
                ProductResponse.class
        );

        productResponseSimpleHttpAgent.get();
    }

    void addProductsToCart(Product product) {
         if (cart.getVendorId() == product.getVendorId() || cart.getVendorId() == 0) {
            if (cart.getQuantityIfProductExistsInCart(product.getId()) >= guests.size())
                return;
            cart.setVendorId(product.getVendorId());
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.copy(product);
            OrderProduct orderProductAdded = cart.getFirstProductMathcing(orderProduct);
            if (orderProductAdded == null) {
                cart.getOrderProducts().add(orderProduct);
            } else {
                orderProductAdded.addQuantity();
            }
            rvMenuList.getAdapter().notifyDataSetChanged();
        } else {
            showVendorCartClearError(product);
        }
        updateGuestCount();
    }

    private void updateGuestCount() {
        int guestCount = 0;
        for (OrderProduct orderProduct : cart.getOrderProducts()) {
            guestCount += orderProduct.getQuantity();
        }
        etGuestCount.setText(String.format("%d", guestCount));
    }

    void removeProductsToCart(Product product) {
//        find product if exists
//        if qty greater than 1 then decrease otherwise delete the order product
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.copy(product);
        OrderProduct orderProductAdded = cart.getFirstNonFreeProductMathcing(orderProduct);
        if (orderProductAdded == null) {
            AppUtils.logException(new StaleDataException());
        } else if (orderProductAdded.getQuantity() > 1) {
            orderProductAdded.setQuantity(orderProductAdded.getQuantity() - 1);
        } else {
            if (cart.getOrderProducts().size() > 1) {
                ArrayList<OrderProduct> orderProducts = new ArrayList<>();
                boolean itemRemoved = false;
                for (OrderProduct temp : cart.getOrderProducts()) {
                    if (orderProduct.getId() == product.getId() && !itemRemoved){
                        itemRemoved = true;
                    } else{
                        orderProducts.add(temp);
                    }
                }
                cart.setOrderProducts(orderProducts);
            } else {
                clearCart();
            }
        }
        rvMenuList.getAdapter().notifyDataSetChanged();
        updateGuestCount();
    }

    private void clearCart() {
        cart.setOrderProducts(new ArrayList<OrderProduct>());
        cart.setVendorId(0);
    }

    private void showVendorCartClearError(Product product) {
        //TODO show vendor id error
        CartCancelDialog cartCancelDialog = CartCancelDialog.newInstance(new Vendor(), product, new CartCancelDialog.OnFragmentInteractionListener() {
            @Override
            public void onFragmentInteraction(Vendor vendor, Product product, boolean isBuffet) {
                clearCart();
                addProductsToCart(product);
            }
        }, false);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(cartCancelDialog, "vendor error")
                .commitAllowingStateLoss();
    }


    private void setupProducts() {
        if (rvMenuList.getAdapter() == null) {
            GuestMenuListAdapter guestMenuListAdapter = new GuestMenuListAdapter(this, products, new GuestMenuListAdapter.CartListener() {
                @Override
                public void addProduct(Product product) {
                    //TODO add to cart and perform vendor check and guest count check
                    addProductsToCart(product);
                }

                @Override
                public void removeProduct(Product product) {
                    //TODO remove from cart and perform vendor check and guest count check
                    removeProductsToCart(product);
                }
            }, cart);
            rvMenuList.setAdapter(guestMenuListAdapter);
        } else {
            if (rvMenuList.getAdapter() instanceof GuestMenuListAdapter) {
                GuestMenuListAdapter guestMenuListAdapter = (GuestMenuListAdapter) rvMenuList.getAdapter();
                guestMenuListAdapter.changeProducts(products, cart);
                guestMenuListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getGuestCount() {
        String url = UrlConstant.GUEST_LIST;
        SimpleHttpAgent<GuestListResponse> guestListResponseSimpleHttpAgent = new SimpleHttpAgent<GuestListResponse>(
                this,
                url,
                new ResponseListener<GuestListResponse>() {
                    @Override
                    public void response(GuestListResponse responseObject) {
                        if (responseObject != null) {
                            guests = responseObject.getGuests();
//                            setupGuestCount();
                            pbLoad.setVisibility(View.GONE);
                            etGuestCount.setHint("Max (" + guests.size() + ")");
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        //TODO show no geust count abb
                        showErrorDialog(null, "No guest found for your account.Please add guest", false);
                        pbLoad.setVisibility(View.GONE);
                    }
                },
                GuestListResponse.class
        );

        guestListResponseSimpleHttpAgent.get();
    }


    private void showErrorDialog(Order order, String error, boolean clearOrder) {
        FreeOrderErrorHandleDialog freeOrderErrorHandleDialog = null;
        if (clearOrder) {
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(order, error, null);
        } else {
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(order, error, new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                @Override
                public void onPositiveButtonClicked() {
                    navigateToAddGuestActivity();
                }

                @Override
                public void onNegativeButtonClicked() {
                    finish();
                }
            }, "ADD GUEST","CANCEL");
        }
        freeOrderErrorHandleDialog.setCancelable(false);
        freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "free_order_error");
    }

    private void navigateToAddGuestActivity(){

        Intent intent = new Intent(this, AddGuestActivity.class);
        startActivity(intent);
    }

    private void handleClickOfPlaceOrder() {

        if (guests == null) {
            AppUtils.showToast("Please wait while we load your guests", false, 2);
            return;
        }
        try {
//            String guestCountStr = etGuestCount.getText().toString();
            guestCount = 0;
            for (OrderProduct orderProduct : cart.getOrderProducts()) {
                guestCount += orderProduct.getQuantity();
            }

        } catch (Exception e) {
            guestCount = 0;
        }

        if (guestCount <= 0) {
            AppUtils.showToast("Please enter no of Guests", false, 2);
        } else if (guestCount > guests.size()) {
            AppUtils.showToast("You can place an order for a maximum of " + guests.size() + " Guests", false, 2);
        } else {
            placeOrder();
        }

    }

    private void placeOrder() {
        Order order = new Order();
        order.setId(Calendar.getInstance().getTimeInMillis());
        order.setOrderStatus(OrderUtil.NEW);
        order.setVendorId(cart.getVendorId());
        order.setCreatedAt((new Date().getTime() / 1000l));
        order.setQuantity(guestCount);
        order.setPrice(0);
        order.setCafe(cafe);
        order.setLocationId(locationId);
        order.setProducts(cart.getOrderProducts());
//        startOrderView(order);
        sendOrderToServer(order);
    }

    private void sendOrderToServer(final Order order) {
        String url = UrlConstant.GUEST_ORDER;
        SimpleHttpAgent<OrderResponse> orderReponseProductSimpleHttpAgent = new SimpleHttpAgent<OrderResponse>(
                this,
                url,
                new ResponseListener<OrderResponse>() {
                    @Override
                    public void response(OrderResponse responseObject) {
                        showPreOrderDialog(responseObject.getOrder());
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (error == null || error.isEmpty())
                            error = "Error in placing order";
                        placeOrderError(order, error, false);
                    }
                },
                OrderResponse.class
        );

        orderReponseProductSimpleHttpAgent.post(order, new HashMap<String, JsonSerializer>());
    }

    private void placeOrderError(Order order, String error, boolean clearOrder) {
        FreeOrderErrorHandleDialog freeOrderErrorHandleDialog = null;
        if (clearOrder) {
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(order, error, null);
        } else {
            freeOrderErrorHandleDialog = FreeOrderErrorHandleDialog.newInstance(order, error, new FreeOrderErrorHandleDialog.OnFragmentInteractionListener() {
                @Override
                public void onPositiveButtonClicked() {
                    finish();
                }

                @Override
                public void onNegativeButtonClicked() {
                    finish();
                }
            }, "cancel");
        }
        freeOrderErrorHandleDialog.setCancelable(false);
        freeOrderErrorHandleDialog.show(getSupportFragmentManager(), "free_order_error");
    }


    private void showPreOrderDialog(Order order) {
        Intent intent = new Intent(this, OrderSuccessActivity.class);
        intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
        intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
        intent.putExtra(ApplicationConstants.PAYMENT_ORDER_TYPE, ApplicationConstants.ORDER_TYPE_FOOD);
        startActivity(intent);
    }

    private void startOrderView(Order order) {
//        Intent intent = null;
//        intent = new Intent(this, OrderViewActivity.class);
//        intent.putExtra(ApplicationConstants.ORDER, order);
//        intent.putExtra(ApplicationConstants.LOGOUT_ALLOWED, true);
//        startActivity(intent);
//        finish();

    }

//    private void setupGuestCount() {
//        final ArrayList<String> guestCounts = new ArrayList<>();
//        guestCounts.add("Click here to select");
//
//        for(int i=1; i<=guests.size(); i++)
//            guestCounts.add(String.format("%d",i));
//
//        ArrayAdapter<String> guestCountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, guestCounts);
//
//        guestCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spGuestCount.setAdapter(guestCountAdapter);
//        spGuestCount.setSelection(0);
//        spGuestCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                if(position==0){
//                    guestCount = 0;
//                    tvQuantity.setText("Quantity "+0);
//                }else{
//                    guestCount = Integer.parseInt(guestCounts.get(position).toString());
//                    tvQuantity.setText("Quantity "+guestCount);
//                    if(position>10){
//                        rgCafe.setVisibility(View.INVISIBLE);
//                    }else{
//                        rgCafe.setVisibility(View.INVISIBLE);
//                    }
//                }
//
////                    if(position==timeList.size()-1)
////                        startTimePickerDialog(currentTimeCalendar);
////                    else{
////                        Calendar calendar = Calendar.getInstance();
////                        String time = .toString();
////                        if(time.contains("Breakfast")){
////                            calendar.set(Calendar.HOUR_OF_DAY, 9);
////                        }else if(time.contains("Lunch")){
////                            calendar.set(Calendar.HOUR_OF_DAY, 13);
////                        }else if(time.contains("Snacks")){
////                            calendar.set(Calendar.HOUR_OF_DAY, 18);
////                        }else{
////                            calendar.set(Calendar.HOUR_OF_DAY, 20);
////                        }
////                        selectedTime = calendar;
////                    }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
////        spGuestCount.performClick();
//    }


    @Override
    protected void onResume() {
        super.onResume();
        getUserDetailsFromServer();
        getGuestCount();
    }

    public void getUserDetailsFromServer() {
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);

        String url = UrlConstant.USER_DETAIL;
        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<UserReposne>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        if (responseObject != null) {
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION ||
                                errorCode == ContextErrorListener.TIMED_OUT) {
//                            showNoNetFragment(new RetryListener() {
//                                @Override
//                                public void onRetry() {
//                                    getUserDetailsFromServer();
//                                }
//                            });
                        }
                    }
                },
                UserReposne.class
        );
        userSimpleHttpAgent.get();
    }
}
