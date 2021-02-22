package com.hungerbox.customer.offline.activityOffline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.event.OrderClear;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.PrivateKeyUpdate;
import com.hungerbox.customer.navmenu.DrawerOpenCloseListener;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.adapterOffline.OccasionChooserAdapterOffline;
import com.hungerbox.customer.offline.fragmentOffline.FreeOrderErrorHandleDialogOffline;
import com.hungerbox.customer.offline.modelOffline.OcassionOffline;
import com.hungerbox.customer.offline.modelOffline.OcassionReposneOffline;
import com.hungerbox.customer.offline.modelOffline.OrderOffline;
import com.hungerbox.customer.offline.modelOffline.ServerStatus;
import com.hungerbox.customer.prelogin.activity.MainActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class GlobalActivityOffline extends AppCompatActivity implements DrawerOpenCloseListener {

    public final static int SCREEN_REQUEST_CODE = 5234;
    public OcassionReposneOffline ocassionReposne = new OcassionReposneOffline();
    public ImageView ivLogo, ivOcassions, ivSearch, iv_back;
    public TextView tvTitle;
    public OccasionChooserAdapterOffline occasionChooserAdapter;
    public TextView spLocation;
    public Handler handler;
    public Runnable runnable;
    public long activityStartTime = 0;
    protected FrameLayout rlBaseContainer;
    protected CoordinatorLayout clBaseContainer;
    protected Fragment fragment;
    protected Toolbar toolbar;

    protected TextView tvVendorName;
    public boolean pictureInPictureRequired = false;
    long selectedOcasionId;
    private boolean isRunning = false, isRunningPip = false;
    private long locationId;
    private RecyclerView rvOccasions;
    public ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;

    private boolean doubleBackToExitPressedOnce;
    private OrderOffline currentOrder;
    public static DisplayMetrics displayMetrics;
    public DialogFragment dialogFragment;
    public boolean deskRefRequired = false;
    public boolean showrating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_global_offline);
        toolbar = findViewById(R.id.tb_global);
        spLocation = findViewById(R.id.tv_location);
        rvOccasions = findViewById(R.id.rv_occasions);
        rvOccasions.setVisibility(View.GONE);
        clBaseContainer = findViewById(R.id.cl_base);
        mDrawerLayout = findViewById(R.id.dl_base_drawer);
        ivSearch = findViewById(R.id.iv_search);
        iv_back = findViewById(R.id.iv_back);

        tvTitle = toolbar.findViewById(R.id.tv_toolbar_title);
        rlBaseContainer = findViewById(R.id.rl_base_container);
        tvVendorName = findViewById(R.id.tv_vendor_name);

        ivLogo = findViewById(R.id.logo);
        ivOcassions = findViewById(R.id.iv_ocassion);

        
        long locationIdPermanent = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID_PERMANENT, -1);

        if (locationIdPermanent != -1) {
            SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, locationIdPermanent);
            SharedPrefUtil.putString(ApplicationConstants.LOCATION_NAME, SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME_PERMANENT, "India T, BLR"));
        }

        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 1);

        spLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent locationIntent = new Intent(getApplicationContext(), LocationChangeActivityOffline.class);
                startActivity(locationIntent);


            }
        });

        ivOcassions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvOccasions.getVisibility() == View.GONE) {
                    if (ocassionReposne != null && ocassionReposne.getOcassions().size() > 0)
                        hide_Show_Fade_Anim(rvOccasions, View.VISIBLE);
                } else
                    hide_Show_Fade_Anim(rvOccasions, View.GONE);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout
                , R.string.drawer_open
                , R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                LogoutTask.updateTime();
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                LogoutTask.updateTime();

                try{
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getNav_bar_click(), null, getApplicationContext());
                }catch (Exception exp){
                    exp.printStackTrace();
                }
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }
        };


        setSupportActionBar(toolbar);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        tvTitle.setVisibility(View.INVISIBLE);
        ivSearch.setVisibility(View.INVISIBLE);
        spLocation.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.menu_new);

        if (AppUtils.getConfig(getApplicationContext()).isShow_location()) {
            spLocation.setVisibility(View.VISIBLE);
        } else {
            spLocation.setVisibility(View.INVISIBLE);
        }

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(getApplicationContext(), HistoryActivityOffline.class);
                startActivity(locationIntent);
            }
        });

//        try {
//
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getOffline_mode(), null, getApplicationContext());
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }

        createBaseContainer();
        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        spLocation.setText(locationName);
        getOccassionList();
        Intent intent = getIntent();

        checkForPrivateKey();

        boolean showDialog = intent.getBooleanExtra("showDialog", true);
        if (showDialog) {
            showOfflineDialog();
        }
    }

    private void checkForPrivateKey(){
        String certificate = SharedPrefUtil.getString(ApplicationConstants.OFFLINE_CERTIFICATE,"");

        String privateKey = SharedPrefUtil.getString(ApplicationConstants.OFFLINE_PRIVATE_KEY,"");


        if(certificate==null || certificate.isEmpty() || privateKey == null || privateKey.isEmpty() || isKeyExpired()){

            ArrayList<String> keyList = AppUtils.getCertificate();
            if(keyList == null)
                return;

            PrivateKeyUpdate privateKeyUpdate = new PrivateKeyUpdate();
            privateKeyUpdate.setPublic_key(keyList.get(0));

            String url = UrlConstant.KEY_REFRESH;

            SimpleHttpAgent<PrivateKeyUpdate> simpleHttpAgent = new SimpleHttpAgent<PrivateKeyUpdate>(this, url,
                    new ResponseListener<PrivateKeyUpdate>() {
                        @Override
                        public void response(PrivateKeyUpdate responseObject) {
                            if(responseObject!=null) {
                                try {
                                    if (responseObject.getCertificate() != null && !responseObject.getCertificate().isEmpty()) {
                                        SharedPrefUtil.putString(ApplicationConstants.OFFLINE_CERTIFICATE, responseObject.getCertificate());
                                        SharedPrefUtil.putString(ApplicationConstants.OFFLINE_PRIVATE_KEY, keyList.get(1));
                                        MainApplication.setHungerBoxOfflineNull();
                                    }
                                    else{
                                        cleverTapEvent("Certificate key api response error ");
                                    }
                                } catch (Exception exp) {
                                    exp.printStackTrace();
                                }
                            }
                            else{
                                cleverTapEvent("Certificate key api error");
                            }
                        }
                    }, new ContextErrorListener() {
                @Override
                public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                    //todo clevertap in global activity as well
                    if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                        cleverTapEvent("No Internet");
                    } else {
                        cleverTapEvent(error==null?"no error message":error);
                    }
                }
            },PrivateKeyUpdate.class);

            simpleHttpAgent.post(privateKeyUpdate,new HashMap<>());

        }
    }

    private void cleverTapEvent(String errorMessage){
//        try{
//            HashMap<String,Object> place_order_map = new HashMap<>();
//            place_order_map.put(CleverTapEvent.PropertiesNames.getSource(),"GlobalActivityOffline");
//            place_order_map.put(CleverTapEvent.PropertiesNames.getMessage(),errorMessage==null?"":errorMessage);
//            CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getCertificate_error(),place_order_map,GlobalActivityOffline.this);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
    }

    private boolean isKeyExpired(){
        try {
            MainApplication mainApplication = (MainApplication) getApplication();
            HungerBoxOffline.EmployeeApp employeeApp = mainApplication.getHungerBoxOffline();

            Calendar calendar =  Calendar.getInstance();


            int random = new Random().nextInt(60*24);
            return (employeeApp.getCertExpiresAt().getTime() - calendar.getTimeInMillis()) < (172800000 + random*60*1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;

    }

    private void checkServerStatus() {

        final String url = UrlConstant.CHECK_SERVER_STATUS;
        SimpleHttpAgent<ServerStatus> simpleHttpAgent = new SimpleHttpAgent<ServerStatus>(this
                , url,
                new ResponseListener<ServerStatus>() {
                    @Override
                    public void response(ServerStatus responseObject) {
                        if (responseObject != null && !responseObject.getStatus()) {

                            //online
                            showOnlineDialog();

                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                }, ServerStatus.class);

        simpleHttpAgent.getWithoutHeader();

    }

    private void showOnlineDialog(){
        try{
            GenericPopUpFragment popUpFragment = GenericPopUpFragment
                    .newInstance("We are back online", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            Intent intent = new Intent(GlobalActivityOffline.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onNegativeInteraction() {

                        }
                    });
            popUpFragment.setCancelable(false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(popUpFragment, "hungerbox_online")
                    .commitAllowingStateLoss();
        }catch (Exception exp){
            Intent intent = new Intent(GlobalActivityOffline.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void showOfflineDialog() {
        GenericPopUpFragment popUpFragment = GenericPopUpFragment
                .newInstance("This is Hungerbox offline!\n" + "Keep ordering!", "OK", true, new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {

                    }

                    @Override
                    public void onNegativeInteraction() {

                    }
                });
        popUpFragment.setCancelable(false);
        popUpFragment.show(getSupportFragmentManager(), "wallet_linking");
    }

    @Override
    protected void onStart() {
        super.onStart();
        String locationName = SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR");
        spLocation.setText(locationName);
        LogoutTask.updateTime();
    }

    @Override
    public void onBackPressed() {
        showBackPressedMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkServerStatus();
    }

    protected void showBackPressedMessage() {

        View.OnClickListener positiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        AppUtils.showToast("Press back again to exit", true, 2);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 10000);
    }


    public void getOccassionList() {

        showrating = false;

        pictureInPictureRequired = false;
        try {
            handler.removeCallbacks(runnable);
        } catch (Exception exp) {
        }
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String url = UrlConstantsOffline.OCCASION + "?locationId=" + locationId;
        SimpleHttpAgent<OcassionReposneOffline> ocassionReposneSimpleHttpAgent = new SimpleHttpAgent<OcassionReposneOffline>(
                this,
                url,
                new ResponseListener<OcassionReposneOffline>() {
                    @Override
                    public void response(OcassionReposneOffline responseObject) {
                        if (responseObject != null && responseObject.getOcassions().size() > 0) {


                            long timeDifference = 0;

                            SharedPrefUtil.putLong(ApplicationConstants.TIME_DIFFERENCE, timeDifference);

                            setOcassion(responseObject);

                        } else {
                            setOcassionNotAvailable();
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        if (occasionChooserAdapter == null) {

                            String message = "";
                            if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                                message = "Please check your internet connection.";
                            } else {
                                message = "Something went wrong";
                            }

                            FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                                    message,
                                    new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveButtonClicked() {
                                            getOccassionList();
                                        }

                                        @Override
                                        public void onNegativeButtonClicked() {
                                            finish();
                                        }
                                    }, "RETRY", "CANCEL");

                            orderErrorHandleDialog.setCancelable(false);
                            dialogFragment = orderErrorHandleDialog;
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(orderErrorHandleDialog, "no_internet")
                                    .commitAllowingStateLoss();
                        } else {

                            String message = "";
                            if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                                message = "Please check your internet connection.";
                            } else {
                                message = "Something went wrong";
                            }

                            FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                                    message,
                                    new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveButtonClicked() {
                                            getOccassionList();
                                        }

                                        @Override
                                        public void onNegativeButtonClicked() {
                                        }
                                    }, "RETRY", "CANCEL");

                            orderErrorHandleDialog.setCancelable(false);
                            dialogFragment = orderErrorHandleDialog;
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(orderErrorHandleDialog, "no_internet")
                                    .commitAllowingStateLoss();

                        }
                    }
                },
                OcassionReposneOffline.class

        );
        ocassionReposneSimpleHttpAgent.get();
    }

    protected void createBaseContainer() {

        if (fragment == null) {
            fragment = OrderFeedFragmentOffline.newInstance(this, "Home", "Home", selectedOcasionId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.beginTransaction()
                        .add(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.rl_base_container, fragment, "vendor")
                        .commit();
            }
        }
    }

    public void setOcassion(OcassionReposneOffline ocassionReposne) {
        this.ocassionReposne = ocassionReposne;
        setOcassionView(false);
    }

    public void setOcassionNotAvailable() {
        if (fragment instanceof OrderFeedFragmentOffline) {
            OrderFeedFragmentOffline vendorListFragment = (OrderFeedFragmentOffline) fragment;
            vendorListFragment.updateViewWithSelectedOcassion(0);
        }
    }


    private void setOcassionView(boolean showDrawer) {

        Calendar calendar = Calendar.getInstance();

        //
        ArrayList<OcassionOffline> ocasionList = new ArrayList<>();
        for (int i = 0; i < ocassionReposne.getOcassions().size(); i++) {
            if (calendar.getTimeInMillis() <
                    DateTimeUtil.getTodaysTimeFromString(ocassionReposne.getOcassions().get(i).startTime) || calendar.getTimeInMillis() >
                    DateTimeUtil.getTodaysTimeFromString(ocassionReposne.getOcassions().get(i).endTime)) {
                ocasionList.add(ocassionReposne.getOcassions().get(i));
            }
        }

        ocassionReposne.getOcassions().removeAll(ocasionList);
        //

        int currentOccasionIndex = 0;
        for (int i = 0; i < ocassionReposne.getOcassions().size(); i++) {
            if (calendar.getTimeInMillis() >
                    DateTimeUtil.getTodaysTimeFromString(ocassionReposne.getOcassions().get(i).startTime)
                    &&
                    calendar.getTimeInMillis() <
                            DateTimeUtil.getTodaysTimeFromString(ocassionReposne.getOcassions().get(i).endTime)) {
                ocassionReposne.getOcassions().get(i).isCurrentOccasion = true;
                currentOccasionIndex = i;
                break;
            }
        }


        if(ocassionReposne.getOcassions().size()>0) {
            if (rvOccasions.getAdapter() == null) {
                occasionChooserAdapter = new OccasionChooserAdapterOffline(GlobalActivityOffline.this,
                        ocassionReposne.getOcassions(), ocassionReposne.getOcassions()
                        .get(currentOccasionIndex).id, new OccasionChangeInterface() {
                    @Override
                    public void onOccasionChanged(int currentOccasionIndex, final View view) {

                        if (currentOccasionIndex == -1) {

                            FreeOrderErrorHandleDialogOffline orderErrorHandleDialog = FreeOrderErrorHandleDialogOffline.newInstance(new OrderOffline(),
                                    "Your cart will get cleared if you change the occasion.",
                                    new FreeOrderErrorHandleDialogOffline.OnFragmentInteractionListener() {
                                        @Override
                                        public void onPositiveButtonClicked() {
                                            MainApplicationOffline.clearOrder(1);
                                            MainApplicationOffline.bus.post(new OrderClear());
                                            view.performClick();
                                        }

                                        @Override
                                        public void onNegativeButtonClicked() {

                                        }
                                    }, "OK", "CANCEL");


                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .add(orderErrorHandleDialog, "cart_clear")
                                    .commitAllowingStateLoss();
                        } else {

                            HBMixpanel.getInstance().addEvent(GlobalActivityOffline.this, EventUtil.MixpanelEvent.CHANGE_OCCASION);

                            EventUtil.FbEventLog(GlobalActivityOffline.this, EventUtil.HOME_OCCASION_CHANGE, EventUtil.SCREEN_HOME);
                            hide_Show_Fade_Anim(rvOccasions, View.GONE);
                            setSelectedOcassion(ocassionReposne.getOcassions().get(currentOccasionIndex).id, ocassionReposne.getOcassions().get(currentOccasionIndex).startTime);
                            MainApplicationOffline.addOcassion(ocassionReposne.getOcassions().get(currentOccasionIndex));
                        }
                    }
                });
                rvOccasions.setLayoutManager(new LinearLayoutManager(GlobalActivityOffline.this, LinearLayoutManager.HORIZONTAL, false));
                rvOccasions.setAdapter(occasionChooserAdapter);
            } else if (rvOccasions.getAdapter() != null && rvOccasions.getAdapter() instanceof OccasionChooserAdapterOffline) {
                OccasionChooserAdapterOffline occasionChooserAdapter = (OccasionChooserAdapterOffline) rvOccasions.getAdapter();
                occasionChooserAdapter.setSelectedOccasion(ocassionReposne.getOcassions().get(currentOccasionIndex).id);
                ((OccasionChooserAdapterOffline) rvOccasions.getAdapter()).setOcassionArrayList(ocassionReposne.getOcassions());
                rvOccasions.getAdapter().notifyDataSetChanged();
                rvOccasions.getLayoutManager().smoothScrollToPosition(rvOccasions, null, 0);
            }
            handleCart(ocassionReposne.getOcassions().get(currentOccasionIndex).id);
            setSelectedOcassion(ocassionReposne.getOcassions().get(currentOccasionIndex).id, ocassionReposne.getOcassions().get(currentOccasionIndex).startTime);
            MainApplicationOffline.addOcassion(ocassionReposne.getOcassions().get(currentOccasionIndex));
        }else{
            try {
                ((OrderFeedFragmentOffline) fragment).shimmerView.stopShimmer();
                ((OrderFeedFragmentOffline) fragment).shimmerView.setVisibility(View.GONE);
                ((OrderFeedFragmentOffline) fragment).tvNoVendorFound.setVisibility(View.VISIBLE);
                ((OrderFeedFragmentOffline) fragment).rvOrderFeed.setVisibility(View.GONE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //getUserDetailsFromServer(false);
    }

    private void handleCart(long occassionId) {
        if (MainApplicationOffline.isCartCreated() && MainApplicationOffline.selectedOcassionId != occassionId) {
            MainApplicationOffline.clearOrder(1);
            MainApplicationOffline.bus.post(new OrderClear());
        }
    }

    public void setSelectedOcassion(long ocassionId, String startTime) {

        this.selectedOcasionId = ocassionId;

        Calendar calendar = Calendar.getInstance();
        if (calendar.getTimeInMillis() < DateTimeUtil.getTodaysTimeFromString(startTime))
            SharedPrefUtil.putBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE, true);
        else
            SharedPrefUtil.putBoolean(ApplicationConstants.IS_PREORDER_AVAILABLE, false);
        

        if (fragment instanceof OrderFeedFragmentOffline) {
            OrderFeedFragmentOffline orderFeedFragment = (OrderFeedFragmentOffline) fragment;
            orderFeedFragment.updateViewWithSelectedOcassion(ocassionId);
        }
    }

    private void hide_Show_Fade_Anim(final View view, int visible) {

        if (visible == View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.bounce_in);
            view.startAnimation(animation);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void closeDrawer() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawers();
    }

    @Override
    public void openDrawer() {
        if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public interface OccasionChangeInterface {
        void onOccasionChanged(int occasionId, View view);
    }
}
