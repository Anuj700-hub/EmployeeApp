package com.hungerbox.customer.navmenu.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.hungerbox.customer.navmenu.adapter.NavigationAdapter;
import com.hungerbox.customer.prelogin.fragment.VersionFragment;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.squareup.otto.Subscribe;

public class NavigationDrawer extends Fragment implements DrawerOpenCloseListener {


    NavigationAdapter navigationAdapter;
    TextView tvUserMessage, tvVersion, tvQATestBanner, tvWalletName, tvWalletBalance, tvUpdateText;
    ImageView ivLogo,ivCompanyLogo;
    Config config;
    private OnFragmentInteractionListener mListener;
    private RecyclerView rvNavigationList;
    private long locationId;
    private ProgressBar pbWallet;
    private View walletBar;

    public NavigationDrawer() {
    }

    public static NavigationDrawer newInstance(String param1, String param2) {
        NavigationDrawer fragment = new NavigationDrawer();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        rvNavigationList = view.findViewById(R.id.rv_drawer);
        walletBar = view.findViewById(R.id.rl_wallet1);
        tvUserMessage = view.findViewById(R.id.tv_nav_user_message);
        tvVersion = view.findViewById(R.id.tv_version);
        pbWallet = view.findViewById(R.id.pbWallet);
        ivLogo = view.findViewById(R.id.iv_user_profile);
        tvQATestBanner = view.findViewById(R.id.tv_qa_test_banner);
        tvWalletName = view.findViewById(R.id.tv_wallet1_name);
        tvWalletBalance = view.findViewById(R.id.tv_wallet1_balance);
        tvUpdateText = view.findViewById(R.id.tv_update);

        ivCompanyLogo = view.findViewById(R.id.company_logo);

        String versionName = AppUtils.getVersionName();
        tvVersion.setText("v" + versionName);
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);


        if (BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"))
            tvQATestBanner.setVisibility(View.VISIBLE);
        else
            tvQATestBanner.setVisibility(View.GONE);


        config = AppUtils.getConfig(getActivity());

        ivCompanyLogo.setVisibility(View.GONE);

        if (config.getBranding()!=null){
            if (config.getBranding().getLogo()!=null && config.getBranding().isNavigation_logo_enabled()&& !(config.getBranding().getLogo().equals(""))){
            ivCompanyLogo.setVisibility(View.VISIBLE);

            ImageHandling.loadRemoteImage(config.getBranding().getLogo(), ivCompanyLogo, -1, -1, getActivity().getApplicationContext());
            }
        }

        setupNavigationList();
        tvUpdateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
                checkAndShowDialog(config.getApp_update_cafe().getTitle(), config.getApp_update_cafe().getSoft_desc(), config.getApp_update_cafe().getUpdate_redirect_url(), false);
            }
        });
        enableUpdateText();
        return view;
    }

    private void checkAndShowDialog(String title, String desc, String redirectURL, boolean isHard) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        VersionFragment versionFragment = VersionFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("desc", desc);
        bundle.putBoolean("isHard", isHard);
        bundle.putString("redirectURL", redirectURL);
        versionFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(versionFragment, "version")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commitAllowingStateLoss();

    }


    @Override
    public void onResume() {
        super.onResume();
        String userName = SharedPrefUtil.getString(ApplicationConstants.PREF_NAME, "");
        if (userName.length() == 0)
            userName = SharedPrefUtil.getString(ApplicationConstants.PREF_USER_NAME, "");
        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        tvUserMessage.setText(userName);
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
        tvUserMessage.setText(userName);
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
        }else{
            walletBar.setVisibility(View.GONE);
        }
    }

    private void setupNavigationList() {
        if (navigationAdapter == null) {
            navigationAdapter = new NavigationAdapter(getActivity(), this, config);
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

    private void enableUpdateText(){
        if (AppUtils.isCafeApp() && config.getApp_update_cafe() != null && config.getApp_update_cafe().getSoft_version() >= BuildConfig.VERSION_CODE && config.getApp_update_cafe().update_redirect_url != null &&!config.getApp_update_cafe().update_redirect_url.equals("")) {
            tvUpdateText.setVisibility(View.VISIBLE);
            tvUpdateText.setText(config.getApp_update_cafe().getSoft_desc());
        }
        else{
            tvUpdateText.setVisibility(View.GONE);
        }
    }
}
