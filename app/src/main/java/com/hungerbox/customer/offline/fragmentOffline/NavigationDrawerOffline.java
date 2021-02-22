package com.hungerbox.customer.offline.fragmentOffline;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.event.NavigationDrawerLocationChangeEvent;
import com.hungerbox.customer.event.OnWalletUpdate;
import com.hungerbox.customer.navmenu.DrawerOpenCloseListener;
import com.hungerbox.customer.offline.adapterOffline.NavigationAdapterOffline;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

public class NavigationDrawerOffline extends Fragment implements DrawerOpenCloseListener {


    NavigationAdapterOffline navigationAdapter;
    TextView tvUserMessage, tvVersion, tvQATestBanner, tvWalletName, tvWalletBalance;
    ImageView ivLogo;
    Config config;
    private OnFragmentInteractionListener mListener;
    private RecyclerView rvNavigationList;
    private long locationId;
    private ProgressBar pbWallet;
    private View walletBar;

    public NavigationDrawerOffline() {
    }

    public static NavigationDrawerOffline newInstance(String param1, String param2) {
        NavigationDrawerOffline fragment = new NavigationDrawerOffline();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer_offline, container, false);

        rvNavigationList = view.findViewById(R.id.rv_drawer);
        walletBar = view.findViewById(R.id.rl_wallet1);
        tvUserMessage = view.findViewById(R.id.tv_nav_user_message);
        tvVersion = view.findViewById(R.id.tv_version);
        pbWallet = view.findViewById(R.id.pbWallet);
        ivLogo = view.findViewById(R.id.iv_user_profile);
        tvQATestBanner = view.findViewById(R.id.tv_qa_test_banner);
        tvWalletName = view.findViewById(R.id.tv_wallet1_name);
        tvWalletBalance = view.findViewById(R.id.tv_wallet1_balance);


        String versionName = AppUtils.getVersionName();
        tvVersion.setText("v" + versionName);
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);


        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"))
            tvQATestBanner.setVisibility(View.VISIBLE);
        else
            tvQATestBanner.setVisibility(View.GONE);

        setupNavigationList();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        String userName = SharedPrefUtil.getString(ApplicationConstants.PREF_NAME, "");
        if (userName.length() == 0)
            userName = SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, "");
        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        tvUserMessage.setText("Hi " + userName);
        MainApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.bus.unregister(this);
    }

    @Subscribe
    public void onNavigationDrawerLocationChangedEvent(NavigationDrawerLocationChangeEvent navigationDrawerLocationChangeEvent) {
        String userName = SharedPrefUtil.getString(ApplicationConstants.PREF_NAME, "");
        if (userName.length() == 0)
            userName = SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, "");
        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        tvUserMessage.setText("Hi " + userName);
    }

    @Subscribe
    public void onWalletUpdate(OnWalletUpdate walletUpdate) {
        pbWallet.setVisibility(View.GONE);
        if (walletUpdate.getUser().getCurrentWallets().getWallets().size() > 0) {

            if (AppUtils.getConfig(getContext()) != null && !AppUtils.getConfig(getContext()).getWallet_present()) {
                walletBar.setVisibility(View.GONE);
            }else{
                walletBar.setVisibility(View.VISIBLE);
            }
                tvWalletName.setText("Hungerbox Wallet");
            tvWalletBalance.setText("₹ " + String.format("%.2f", walletUpdate.getUser().getCurrentWalletBalance(true)));
        }
    }


    private void setupNavigationList() {
        if (navigationAdapter == null) {
            navigationAdapter = new NavigationAdapterOffline(getActivity(), this);
            rvNavigationList.setAdapter(navigationAdapter);
        } else {
            navigationAdapter.notifyDataSetChanged();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void closeDrawer() {
        if (getActivity() != null && getActivity() instanceof DrawerOpenCloseListener) {
            DrawerOpenCloseListener drawerOpenCloseListener = (DrawerOpenCloseListener) getActivity();
            drawerOpenCloseListener.closeDrawer();
        }
    }

    @Override
    public void openDrawer() {
        if (getActivity() != null && getActivity() instanceof DrawerOpenCloseListener) {
            DrawerOpenCloseListener drawerOpenCloseListener = (DrawerOpenCloseListener) getActivity();
            drawerOpenCloseListener.openDrawer();
        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
