package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.FeatureSearchVendorMenu;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.activity.GlobalSearchActivity;
import com.hungerbox.customer.order.adapter.SearchDishesAdapter;
import com.hungerbox.customer.order.adapter.SearchVendorAdapter;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.ArrayList;

public class SearchDishesFragment extends Fragment implements VendorValidationListener {


    private boolean isRunning = false;
    private RecyclerView rvVendors;
    private long locationId;
    private ArrayList<Object> productArrayList;
    private SearchDishesAdapter adapter;
    private AppCompatImageView ivEmpty;
    private TextView tvEmpty;
    private TextView tvEmptySub;
    private TextView tvCount;
    GenericPopUpFragment cartCancelDialog;
    private ShimmerFrameLayout shimmerFrameLayout;


    public SearchDishesFragment() {
    }


    public static SearchDishesFragment newInstance() {
        SearchDishesFragment fm = new SearchDishesFragment();
        fm.productArrayList = new ArrayList<>();
        return fm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_vendor, container, false);
        rvVendors = view.findViewById(R.id.rv_vendors);
        ivEmpty = view.findViewById(R.id.iv_empty);
        tvEmpty = view.findViewById(R.id.tv_empty);
        tvEmptySub = view.findViewById(R.id.tv_empty_sub);
        tvCount = view.findViewById(R.id.tv_count);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        rvVendors.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchDishesAdapter(getActivity(),productArrayList,this);
        rvVendors.setAdapter(adapter);
        setEmptyRv();
        tvEmpty.setText("Search for anything");
        tvEmptySub.setVisibility(View.GONE);
        return view;
    }

    public void setProducts(ArrayList<Object> productArrayList,int dishQuantity){

        this.productArrayList = productArrayList;
        finishLoading();
        if (productArrayList == null || productArrayList.size() == 0) {
            setEmptyRv();
        } else {
            showList();
            if (dishQuantity>-1){
                tvCount.setText(String.format("%d %s found", dishQuantity,dishQuantity==1?"Dish":"Dishes"));
            } else {
                tvCount.setText(String.format("%d %s found", productArrayList.size() ,productArrayList.size()==1?"Dish":"Dishes"));
            }
            if (productArrayList.size() == 0){
                tvCount.setVisibility(View.GONE);
            }
            adapter.setProducts(productArrayList);
        }
        //adapter.notifyDataSetChanged();

    }
    public void setLoading(){
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        rvVendors.setVisibility(View.GONE);
        ivEmpty.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        tvEmptySub.setVisibility(View.GONE);
        tvCount.setVisibility(View.GONE);
    }

    private void finishLoading(){
        tvCount.setVisibility(View.VISIBLE);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        rvVendors.setVisibility(View.VISIBLE);
    }

    private void setEmptyRv(){
        tvCount.setVisibility(View.GONE);
        rvVendors.setVisibility(View.GONE);
        ivEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvEmptySub.setVisibility(View.VISIBLE);
        if (getActivity()!=null && getActivity() instanceof GlobalSearchActivity) {
            tvEmpty.setText("No search result for \"" + ((GlobalSearchActivity) getActivity()).searchInputText + "\"");
        }

    }

    private void showList(){
        tvCount.setVisibility(View.VISIBLE);
        rvVendors.setVisibility(View.VISIBLE);
        ivEmpty.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        tvEmptySub.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
        if (getActivity() instanceof GlobalSearchActivity)
            ((GlobalSearchActivity) getActivity()).setUpCart();
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void validateAndAddProduct(Vendor vendor, Product product, boolean isBuffetVendor) {
        if(getActivity()!=null) {
            MainApplication mainApplication = (MainApplication) getActivity().getApplication();
            if (!mainApplication.isVendorValid(vendor.getId())) {
                showCancelCartProduct(vendor, product, isBuffetVendor);
            }
        }
    }

    public void showCancelCartProduct(Vendor vendor, Product product, boolean isBuffet) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            cartCancelDialog = GenericPopUpFragment.newInstance("Cannot be added", "OK", new GenericPopUpFragment.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    cartCancelDialog.dismiss();
                }

                @Override
                public void onNegativeInteraction() {

                }
            });
//            CartCancelDialog cartCancelDialog = CartCancelDialog.newInstance(vendor, product, new CartCancelDialog.OnFragmentInteractionListener() {
//                @Override
//                public void onFragmentInteraction(Vendor vendor, Product product, boolean isBuffet) {
//
//                }
//            }, isBuffet);
            cartCancelDialog.setCancelable(false);

            fragmentManager.beginTransaction()
                    .add(cartCancelDialog, "cart_cancel")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                    .commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}


