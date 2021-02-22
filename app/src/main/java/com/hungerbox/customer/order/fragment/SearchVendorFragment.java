package com.hungerbox.customer.order.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hungerbox.customer.R;
import com.hungerbox.customer.contest.activity.ContestDetailActivity;
import com.hungerbox.customer.contest.activity.RewardActivity;
import com.hungerbox.customer.contest.adapter.ContestListAdapter;
import com.hungerbox.customer.contest.model.Banner;
import com.hungerbox.customer.contest.model.ContestList;
import com.hungerbox.customer.contest.model.ContestListHeader;
import com.hungerbox.customer.contest.model.ContestResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.SearchVendorModel;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.GlobalSearchActivity;
import com.hungerbox.customer.order.adapter.SearchVendorAdapter;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static com.hungerbox.customer.contest.activity.ContestActivity.REQCODEORDER;

public class SearchVendorFragment extends Fragment {


    private boolean isRunning = false;
    private RecyclerView rvVendors;
    private long locationId;
    private ArrayList<Vendor> vendorArrayList;
    private SearchVendorAdapter adapter;
    private AppCompatImageView ivEmpty;
    private TextView tvEmpty;
    private TextView tvEmptySub;
    private TextView tvCount;
    private ShimmerFrameLayout shimmerFrameLayout;


    public SearchVendorFragment() {
    }


    public static SearchVendorFragment newInstance() {
        SearchVendorFragment fm = new SearchVendorFragment();
        fm.vendorArrayList = new ArrayList<>();
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
        adapter = new SearchVendorAdapter(getActivity(),vendorArrayList);
        rvVendors.setAdapter(adapter);
        setEmptyRv();
        tvEmpty.setText("Search for anything");
        tvEmptySub.setVisibility(View.GONE);
        return view;
    }

    public void setVendors(ArrayList<Vendor> vendors){

        vendorArrayList = vendors;
        finishLoading();
        if (vendorArrayList == null || vendorArrayList.size() == 0) {
            setEmptyRv();
        } else {
            showList();
            tvCount.setText(String.format("%d %s found", vendorArrayList.size(), vendorArrayList.size()==1?"Vendor":"Vendors"));
            adapter.setVendors(vendorArrayList);
        }

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
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
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

