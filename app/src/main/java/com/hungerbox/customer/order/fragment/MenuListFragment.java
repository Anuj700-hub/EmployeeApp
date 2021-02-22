package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Category;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.AddToCardLisenter;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.adapter.MenuListAdapter;
import com.hungerbox.customer.order.listeners.VegNonVegUpdateListener;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;


public class MenuListFragment extends Fragment implements VendorValidationListener,
        CartCancelDialog.OnFragmentInteractionListener, VegNonVegUpdateListener {

    Category category;
    RecyclerView rvMenuList;
    MenuListAdapter menuListAdapter;
    Vendor vendor;
    Realm realm;
    long occassionId;
    boolean isRunning = false;
    private long categoryId;
    private String categoryName;
    private OnFragmentInteractionListener mListener;
    private MenuFragment menuFragment;
    private TextView tvNoMenuItems;
    private int position;


    public MenuListFragment() {
    }

    public static MenuListFragment newInstance(int pos ,long categoryId,
                                               String categoryName, Vendor vendor,
                                               long occassionId, MenuFragment menuFragment) {
        MenuListFragment fragment = new MenuListFragment();
        fragment.categoryId = categoryId;
        fragment.categoryName = categoryName;
        fragment.vendor = vendor;
        fragment.occassionId = occassionId;
        fragment.menuFragment = menuFragment;
        fragment.position = pos;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());

        Realm.init(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getCategoryAndUpdateUi();
        isRunning = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
//        if (category != null)
//            category.removeChangeListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_list, container, false);
        tvNoMenuItems = view.findViewById(R.id.tv_no_menu_items_found);
        rvMenuList = view.findViewById(R.id.rv_menu_list);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getCategoryAndUpdateUi() {

        List<Product> realmProducts = new ArrayList<>();
//        if (category != null) {
//            category.removeChangeListeners();
//        }
        int isVeg = 0;

        if (categoryId == 0 && categoryName == null) {
            RealmResults<Product> productList = realm.where(Product.class).equalTo("recommeded", 1)
                    .equalTo(Product.VENDOR_ID, vendor.getId())
                    .findAll().sort("sortOrder", Sort.DESCENDING);
            realmProducts = new RealmList<>();
            realmProducts.addAll(productList);
        } else {
            category = realm.where(Category.class).equalTo("id", categoryId)
                    .findFirst();
            if (category != null)
                realmProducts = category.getProducts(realm, vendor.getId());
        }


        ArrayList<Product> products = new ArrayList<>();
        if (realmProducts == null || realmProducts.isEmpty()) {
            setupMenuList(new ArrayList<Product>());
            return;
        }

        if (MenuFragment.IS_VEG_ONLY) {
            isVeg = 1;
            for (Product product : realmProducts) {
                if (product.isProductVeg())
                    products.add(product);
            }
        } else {
            isVeg = 0;
            products.addAll(realmProducts);
        }


        setupMenuList(products);

    }

    private void setupMenuList(List<Product> products) {
        if (products == null)
            tvNoMenuItems.setVisibility(View.VISIBLE);
        else {
            if (products.size() > 0)
                tvNoMenuItems.setVisibility(View.GONE);
            else
                tvNoMenuItems.setVisibility(View.VISIBLE);

        }
        if (products == null && getActivity() == null) {

            return;
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showCancelCartProduct(Vendor vendor, Product product, boolean isBuffet) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CartCancelDialog cartCancelDialog = CartCancelDialog.newInstance(vendor, product, this, isBuffet);
        cartCancelDialog.setCancelable(false);
        fragmentManager.beginTransaction()
                .add(cartCancelDialog, "cart_cancel")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }

    @Override
    public void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffetVendor) {
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        if (!mainApplication.isVendorValid(vendor.getId())) {
            showCancelCartProduct(vendor, product, isBuffetVendor);
        }
    }

    @Override
    public void onFragmentInteraction(Vendor vendor, Product product, boolean isbuffet) {
        if (getActivity() == null) {
            return;
        }
        MainApplication mainApplication = (MainApplication) getActivity().getApplication();
        mainApplication.clearOrder();
        HashMap<String , Object> map = new HashMap<>();
        try{
            map.put(CleverTapEvent.PropertiesNames.getSource(),ApplicationConstants.ADD_ITEM_SOURCE_NORMAL);
            map.put(CleverTapEvent.PropertiesNames.is_bookmarked(), product.isBookmarked()?"Yes":"No");
            map.put(CleverTapEvent.PropertiesNames.is_trending(),product.isTrendingItem()?"Yes":"No");
            map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
            map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendor.getId());
            map.put(CleverTapEvent.PropertiesNames.getMenu_item_id(),product.getId());

        }catch (Exception e){
            e.printStackTrace();
        }

        //TODO order
        if (!product.isConfigurable()) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.copy(product);
            mainApplication.getCart().addProductToCart(product.clone(), null,
                    mainApplication,
                    this,
                    (AppCompatActivity) getActivity(),
                    vendor.clone(), occassionId,map);
            int orderQty = mainApplication.getOrderQuantityForProduct(product.getId());
            MenuActivity activity = (MenuActivity) getActivity();
            activity.setUpCart();
            if (activity instanceof AddToCardLisenter) {
                AddToCardLisenter addToCardLisenter = (AddToCardLisenter) activity;
                Product productClone = product.clone();
                productClone.quantity = orderQty;
                ((AddToCardLisenter) activity).addToCart(vendor.clone(), productClone);
            }
            if (menuListAdapter != null) {
                menuListAdapter.notifyDataSetChanged();
            }
        } else {
            mainApplication.getCart().addProductToCart(product.clone(), null,
                    mainApplication,
                    this,
                    (AppCompatActivity) getActivity(),
                    vendor.clone(), occassionId,map);
        }

    }

    @Override
    public void onUpdate() {
        if (getActivity() != null && isRunning)
            getCategoryAndUpdateUi();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
