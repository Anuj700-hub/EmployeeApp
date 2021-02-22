package com.hungerbox.customer.navmenu.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.event.CartItemAddedEvent;
import com.hungerbox.customer.model.BookMarkMenu;
import com.hungerbox.customer.model.BookMarkMenuData;
import com.hungerbox.customer.model.BookMarkMenuResponse;
import com.hungerbox.customer.model.Cart;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.OcassionReposne;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.TrendingMenuItem;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.model.db.DbHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.adapter.MenuListAdapter;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.fragment.NoNetFragment;
import com.hungerbox.customer.order.listeners.RetryListener;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.ErrorPopFragment;
import com.hungerbox.customer.util.view.RoundedRectangle;
import com.squareup.otto.Subscribe;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class BookMarkActivity extends ParentActivity {

    long locationId, occasionId;
    Realm realm;
    RecyclerView rlMenuList, rvTrendingList;
    ImageView ivBack, ivBookmark, ivTrending;
    MenuListAdapter menuListAdapter, trendingListAdapter;
    ProgressBar pb;
    TextView tvBookmarkTitle, tvTrendingTitle;
    //HbCardView cvMenu;
    CardView cvTrending;
    CardView cvMenu;
    int expressCheckoutAction = 1;
    boolean isRunning = false;
    boolean bookmarkPopulated, trendingPopulated;
    private boolean fromShortcut;
    private ConstraintSet constraintMatchParent, constraintHalf;
    private ConstraintLayout baseCl;
    private RelativeLayout rlNoBookmark;
    List<Product> trendingProducts;
    List<Product> bookmarkProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        rvTrendingList = findViewById(R.id.rl_trending_list);
        ivBookmark = findViewById(R.id.iv_bookmark);
        tvBookmarkTitle = findViewById(R.id.tv_bookmark_title);
        tvTrendingTitle = findViewById(R.id.tv_trending_title);
        cvTrending = findViewById(R.id.cv_trending);
        rlMenuList = findViewById(R.id.rl_menu_list);
        cvMenu = findViewById(R.id.cv_menu);
        ivBack = findViewById(R.id.iv_back);
        pb = findViewById(R.id.pb);
        baseCl = findViewById(R.id.cl_base);
        rlNoBookmark = findViewById(R.id.rl_no_bookmark);

        Intent intent = getIntent();
        fromShortcut = intent.getBooleanExtra("fromShortcut", false);

        realm = Realm.getInstance(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());

        if (fromShortcut) {
            String applicationId = BuildConfig.APPLICATION_ID;
            initialiseConstraints();

            if (BuildConfig.FLAVOR.equalsIgnoreCase("common")) {
                String cId = SharedPrefUtil.getString(ApplicationConstants.PREF_UNIFIED_COMPANY_ID, "");
                if (cId == null || cId.equals("")) {
                    showErrorDialog("Oops! something went wrong. Please try again");
                } else {
                    String url = UrlConstant.CONFIG_URL_GET + cId + "/android?unified=true";
                    getConfigFromServer(url);
                }

            } else {
                String url = UrlConstant.CONFIG_URL_GET + applicationId + "/android";
                getConfigFromServer(url);
            }

        } else {
            initiate();
        }
    }

    private void getConfigFromServer(final String url) {

        pb.setVisibility(View.VISIBLE);
        SimpleHttpAgent<Config> configSimpleHttpAgent = new SimpleHttpAgent<Config>(
                getApplicationContext(),
                url,
                new ResponseListener<Config>() {
                    @Override
                    public void response(Config responseObject) {
                        pb.setVisibility(View.GONE);
                        if (responseObject != null) {
                            try {
                                MainApplication mainApplication = (MainApplication) getApplication();
                                mainApplication.setConfig(responseObject);
                                getOccasionAndVendor();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            showErrorDialog("Oops! something went wrong. Please try again");
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pb.setVisibility(View.GONE);
                        if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                            showNoNetFragment(new RetryListener() {
                                @Override
                                public void onRetry() {
                                    getConfigFromServer(url);
                                }
                            });
                        } else {
                            if (error != null && !error.equals("")) {
                                AppUtils.showToast(error, true, 0);
                            }
                        }

                    }
                },
                Config.class
        );
        configSimpleHttpAgent.get();

    }

    private void getOccasionAndVendor() {

        pb.setVisibility(View.VISIBLE);
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        String url = UrlConstant.OCCASION_LIST + "?locationId=" + locationId;
        SimpleHttpAgent<OcassionReposne> ocassionReposneSimpleHttpAgent = new SimpleHttpAgent<OcassionReposne>(
                this,
                url,
                new ResponseListener<OcassionReposne>() {
                    @Override
                    public void response(OcassionReposne responseObject) {
                        if (responseObject != null && responseObject.getOcassions().size() > 0) {


                            long timeDifference = 0;

                            try {
                                timeDifference = Calendar.getInstance().getTimeInMillis() - (responseObject.getOcassions().get(0).getTimeStamp() * 1000);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }

                            if (responseObject.getOcassions().get(0).getTimeStamp() == 0) {
                                timeDifference = 0;
                            }

                            SharedPrefUtil.putLong(ApplicationConstants.TIME_DIFFERENCE, timeDifference);

                            Calendar calendar = DateTimeUtil.adjustCalender(BookMarkActivity.this);

                            int currentOccasionIndex = -1;
                            int currentPosition = 0;
                            for (Ocassion ocassion : responseObject.getOcassions()){
                                if (calendar.getTimeInMillis() > DateTimeUtil.getTodaysTimeFromString(ocassion.startTime)
                                    && calendar.getTimeInMillis() < DateTimeUtil.getTodaysTimeFromString(ocassion.endTime)) {
                                    ocassion.isCurrentOccasion = true;
                                    ocassion.id = ocassion.occasionId;
                                    currentOccasionIndex = currentPosition;
                                    break;
                                }
                                currentPosition++;
                            }
                            if (currentOccasionIndex == -1){
                                AppUtils.showToast("No active occasion", false, 0);
                                return;
                            }

                            MainApplication.addOcassion(responseObject.getOcassions().get(currentOccasionIndex));
                            occasionId = MainApplication.selectedOcassionId;

                            String url = UrlConstant.GET_VENDOR_LIST + "?locationId=" + locationId + "&occasionId=" + occasionId;
                            if (occasionId == 0) {
                                AppUtils.showToast("Some error occurred.", false, 0);
                                return;
                            }

                            SimpleHttpAgent<OcassionReposne> venSimpleHttpAgent = new SimpleHttpAgent<>(
                                    BookMarkActivity.this,
                                    url,
                                    new ResponseListener<OcassionReposne>() {
                                        @Override
                                        public void response(final OcassionReposne responseObject) {

                                            if (responseObject != null && responseObject.getOcassions() != null && responseObject.getOcassions().size() > 0 && responseObject.getOcassions().get(0).vendors.vendors.size() > 0) {
                                                rlNoBookmark.setVisibility(View.GONE);
                                                for (Vendor vendor : responseObject.getOcassions().get(0).vendors.vendors) {
                                                    final long currentTime = DateTimeUtil.adjustCalender(BookMarkActivity.this).getTimeInMillis();
                                                    long vendorStartTime = DateTimeUtil.getTodaysTimeFromString(vendor.getStartTime());
                                                    final long vendorEndTime = DateTimeUtil.getTodaysTimeFromString(vendor.getEndTime());

                                                    if (currentTime < vendorStartTime) {
                                                        vendor.setActive(0);
                                                    } else if (currentTime > vendorEndTime) {
                                                        vendor.setActive(0);
                                                    } else {
                                                        vendor.setActive(1);
                                                    }
                                                }
                                                if (!DbHandler.isStarted())
                                                    DbHandler.start(context);
                                                DbHandler dbHandler = DbHandler.getDbHandler(context);
                                                boolean vendorStored = dbHandler.createVendors(responseObject.getOcassions().get(0).vendors.vendors);
                                                initiate();

                                            } else {
                                                maximiseBookmarkView();
                                                rlNoBookmark.setVisibility(View.VISIBLE);
                                                //AppUtils.showToast("Some error occured.", false, 0);
                                            }
                                        }
                                    },
                                    new ContextErrorListener() {
                                        @Override
                                        public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                                            AppUtils.showToast("Some error occured.", false, 0);
                                        }
                                    },
                                    OcassionReposne.class
                            );
                            venSimpleHttpAgent.get();

                        } else {
                            pb.setVisibility(View.GONE);
                            AppUtils.showToast("Some error occured.", false, 0);
                        }

                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        pb.setVisibility(View.GONE);
                        AppUtils.showToast("Some error occured.", false, 0);
                    }
                },
                OcassionReposne.class

        );
        ocassionReposneSimpleHttpAgent.get();
        return;
    }

    private void initiate() {

        initialiseConstraints();


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        isRunning = true;
        locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        occasionId = MainApplication.selectedOcassionId;


        expressCheckoutAction = getIntent().getIntExtra(ApplicationConstants.EXPRESS_CHECKOUT_ACTION, 0);
        String source = getIntent().getStringExtra(CleverTapEvent.PropertiesNames.getSource());
        if (source == null)
            source = "NA";

//        try {
//            if (fromShortcut) {
//                HashMap<String, Object> map = new HashMap<>();
//                map.put(CleverTapEvent.PropertiesNames.getScreen_name(), "Bookmark");
//                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getShortcut(), map, getApplicationContext());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (expressCheckoutAction == ApplicationConstants.BOOKMARK_FROM_NAVBAR) {
            maximiseBookmarkView();
        }

        if (occasionId != -1 && locationId > 0) {
            fetchBookmarkMenu();
        } else {
            showErrorDialog("Oops! something went wrong. Please try again");
        }
    }

    private void startOnboarding() {

        if (expressCheckoutAction != ApplicationConstants.BOOKMARK_FROM_MAINACTIVITY ||
                !AppUtils.getConfig(getApplicationContext()).isCoach_mark_visible()) {
            return;
        }

        boolean tutorial_bookmark__bookmark_header = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_BOOKMARK_HEADER, true);
        boolean tutorial_bookmark__trending_header = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_TRENDING_HEADER, true);
        boolean tutorial_bookmark__add = SharedPrefUtil.getBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_ADD, true);

        final ArrayList<SimpleTarget> targets = new ArrayList();


        try {

            if (menuListAdapter != null) {

                if (tutorial_bookmark__bookmark_header) {
                    int[] oneLocation = new int[2];
                    tvBookmarkTitle.getLocationInWindow(oneLocation);
                    int oneY = oneLocation[1];
                    int oneX = oneLocation[0];
                    int height = tvBookmarkTitle.getHeight() + 10;
                    int width = tvBookmarkTitle.getWidth() + 10;
                    SimpleTarget simpleTarget1 = new SimpleTarget.Builder(this)
                            .setPoint(oneX, oneY)
                            .setDuration(250)
                            .setShape(new RoundedRectangle(height, width, 5))
                            .setTitle(AppUtils.getConfig(this).getTutorial_text()[4])
                            .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                @Override
                                public void onStarted(SimpleTarget target) {
                                }

                                @Override
                                public void onEnded(SimpleTarget target) {
                                    SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_BOOKMARK_HEADER, false);
                                }
                            })
                            .build();

                    targets.add(simpleTarget1);
                }

                if (tutorial_bookmark__add) {
                    int[] oneLocation = new int[2];
                    final ProductItemViewHolder holder = (ProductItemViewHolder) rlMenuList.findViewHolderForAdapterPosition(0);
                    holder.rvContiner.getLocationInWindow(oneLocation);
                    int oneX = (int) (oneLocation[0] + holder.rvContiner.getWidth() / 2f);
                    float circleRadius = (float) (1.25 * holder.rvContiner.getWidth() / 2f);
                    int oneY = (int) (oneLocation[1] + holder.rvContiner.getHeight() / 2f);
                    SimpleTarget simpleTarget2 = new SimpleTarget.Builder(this)
                            .setPoint(oneX, oneY)
                            .setDuration(250)
                            .setShape(new Circle(circleRadius))
                            .setTitle(AppUtils.getConfig(this).getTutorial_text()[5])
                            .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                @Override
                                public void onStarted(SimpleTarget target) {

                                }

                                @Override
                                public void onEnded(SimpleTarget target) {
                                    SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_ADD, false);
                                }
                            })
                            .build();
                }
            }


            if (trendingListAdapter != null) {

                if (tutorial_bookmark__trending_header) {
                    int[] oneLocation = new int[2];
                    tvTrendingTitle.getLocationInWindow(oneLocation);
                    int oneY = oneLocation[1];
                    int oneX = oneLocation[0];
                    int height = tvTrendingTitle.getHeight() + 10;
                    int width = tvTrendingTitle.getWidth() + 10;
                    SimpleTarget simpleTarget3 = new SimpleTarget.Builder(BookMarkActivity.this)
                            .setPoint(oneX, oneY)
                            .setDuration(250)
                            .setShape(new RoundedRectangle(height, width, 5))
                            .setTitle(AppUtils.getConfig(BookMarkActivity.this).getTutorial_text()[6])
                            .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                                @Override
                                public void onStarted(SimpleTarget target) {
                                }

                                @Override
                                public void onEnded(SimpleTarget target) {
                                    SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_TRENDING_HEADER, false);
                                }
                            })
                            .build();
                    targets.add(simpleTarget3);
                }
            }

            if (targets.size() > 0) {
                Spotlight.with(this)
                        .setOverlayColor(R.color.transBGG)
                        .setDuration(100L)
                        .setAnimation(new DecelerateInterpolator(1f))
                        .setTargets(targets.toArray(new SimpleTarget[targets.size()]))
                        .setClosedOnTouchedOutside(true)
                        .setOnSpotlightStateListener(new OnSpotlightStateChangedListener() {
                            @Override
                            public void onStarted() {
                            }

                            @Override
                            public void onEnded() {
                            }
                        })
                        .start();
            }

        } catch (Exception exp) {
            exp.printStackTrace();
            SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_BOOKMARK_HEADER, false);
            SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_ADD, false);
            SharedPrefUtil.putBoolean(ApplicationConstants.Tutorial.TUTORIAL_BOOKMARK_TRENDING_HEADER, false);
        }
    }

    private void fetchBookmarkMenu() {
        String url = UrlConstant.BOOKMARK_FETCH + "?occasionId=" + occasionId + "&locationId=" + locationId;

        pb.setVisibility(View.VISIBLE);
        SimpleHttpAgent<BookMarkMenuResponse> menuResponseSimpleHttpAgent = new SimpleHttpAgent<BookMarkMenuResponse>(getApplicationContext(), url, new ResponseListener<BookMarkMenuResponse>() {
            @Override
            public void response(BookMarkMenuResponse responseObject) {
                pb.setVisibility(View.GONE);
                if (responseObject != null && responseObject.getBookMarkMenus() != null) {
                    cvMenu.setVisibility(View.VISIBLE);
                    setBookmarkMenu(responseObject.getBookMarkMenus());
                } else {
                    rlNoBookmark.setVisibility(View.VISIBLE);
                    maximiseBookmarkView();
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                pb.setVisibility(View.GONE);
                if (errorCode == ContextErrorListener.NO_NET_CONNECTION || errorCode == ContextErrorListener.TIMED_OUT) {
                    showErrorPopFragment(ApplicationConstants.NO_NET_STRING,ApplicationConstants.NO_INTERNET_IMAGE);

                } else {
                    if (error != null && !error.equals("")) {
                        showErrorPopFragment(error,ApplicationConstants.SOME_WRONG);
                    }else{
                        showErrorPopFragment(ApplicationConstants.SOME_WRONG,ApplicationConstants.SOME_WRONG);
                    }
                }

            }
        }, BookMarkMenuResponse.class);

        menuResponseSimpleHttpAgent.get();
    }

    private void setBookmarkMenu(final BookMarkMenuData bookMarkMenuData) {

        //setting bookmarkData
        setBookMarkMenuData(bookMarkMenuData);

        //setting Trending Menu data
        if (expressCheckoutAction == ApplicationConstants.BOOKMARK_FROM_MAINACTIVITY) {
            cvTrending.setVisibility(View.VISIBLE);
            minimiseBookmarkView();
            setTrendingMenuData(bookMarkMenuData);
            if (bookMarkMenuData.getUserBookmarks() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus().size() > 0){
                if (bookmarkProducts!=null && bookmarkProducts.size()==0 && bookMarkMenuData.getTrendingMenu() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus().size() > 0 && trendingProducts!=null && trendingProducts.size()>0){
                    adjustNoBookmarkView();
                }
            } else{
                if (bookMarkMenuData.getTrendingMenu() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus().size() > 0 && trendingProducts!=null && trendingProducts.size()>0){
                        adjustNoBookmarkView();
                }
            }
        } else {
            cvTrending.setVisibility(View.GONE);
            maximiseBookmarkView();
        }


    }

    public void showNoNetFragment(RetryListener retryListener) {
        NoNetFragment fragment = NoNetFragment.newInstance(retryListener);
        fragment.setCancelable(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(fragment, "exit").commit();
    }

    private void setTrendingMenuData(BookMarkMenuData bookMarkMenuData) {
        if (bookMarkMenuData.getTrendingMenu() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus() != null && bookMarkMenuData.getTrendingMenu().getTrendingMenus().size() > 0) {

            final List<TrendingMenuItem> trendingMenuItems = bookMarkMenuData.getTrendingMenu().getTrendingMenus();
            List<Vendor> realmVendors = AppUtils.getAllActiveVendor(getApplicationContext());
            HashMap<Long, String> vendorMap = new HashMap<>();
            final List<Vendor> vendors = new ArrayList<>();
            HashMap<Long, Vendor> vendorHashMap = new HashMap<>();

            for (Vendor vendor : realmVendors) {
                vendorMap.put(vendor.getId(), vendor.getVendorName());
                vendorHashMap.put(vendor.getId(), vendor);
            }

            trendingProducts = new ArrayList<>();
            for (TrendingMenuItem bookMarkMenu : trendingMenuItems) {
                if (vendorHashMap.get(bookMarkMenu.getVendorId())!=null
                        &&((vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine()
                        && AppUtils.getConfig(BookMarkActivity.this).isVendingMachineEnabled())
                        || !vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine())
                ) {
                    bookMarkMenu.setVendorName(vendorMap.get(bookMarkMenu.getVendorId()));
                    bookMarkMenu.setTrendingItem(true);
                    Product itemToAdd = bookMarkMenu.copy();
                    itemToAdd.setRecommendationType(ApplicationConstants.RecommendationType.BOOKMARK_TRENDING);
                    trendingProducts.add(itemToAdd);
                    vendors.add(vendorHashMap.get(bookMarkMenu.getVendorId()));
                }
            }

            if (trendingProducts.size() > 0) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(TrendingMenuItem.class);
                        realm.copyToRealmOrUpdate(trendingMenuItems);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        //RealmResults<TrendingMenuItem> realmTrending = realm.where(TrendingMenuItem.class).findAll().sort("sortOrder", Sort.DESCENDING);

                        if (trendingListAdapter == null) {
                            trendingListAdapter = new MenuListAdapter(BookMarkActivity.this, trendingProducts, vendors, occasionId, true, expressCheckoutAction, null);
                            rvTrendingList.setAdapter(trendingListAdapter);
                            rvTrendingList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                @Override
                                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                                    rlMenuList.removeOnLayoutChangeListener(this);
                                    trendingPopulated = true;
                                    if (trendingPopulated && bookmarkPopulated) {
                                        startOnboarding();
                                    }
                                }
                            });
                        } else {
                            trendingListAdapter.changeProducts(trendingProducts, vendors);
                            trendingListAdapter.notifyDataSetChanged();
                        }
                    }

                });
            } else {
                trendingPopulated = true;
                maximiseBookmarkView();
                cvTrending.setVisibility(View.GONE);
            }

        } else {
            trendingPopulated = true;
            maximiseBookmarkView();
            cvTrending.setVisibility(View.GONE);
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(TrendingMenuItem.class);
                }
            });
        }
    }

    private void setBookMarkMenuData(BookMarkMenuData bookMarkMenuData) {

        if (bookMarkMenuData.getUserBookmarks() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus() != null && bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus().size() > 0) {

            final List<BookMarkMenu> bookMarkMenus = bookMarkMenuData.getUserBookmarks().getUserBookmarkMenus();

            List<Vendor> realmVendors = AppUtils.getAllActiveVendor(getApplicationContext());
            HashMap<Long, String> vendorMap = new HashMap<>();
            final List<Vendor> vendors = new ArrayList<>();
            HashMap<Long, Vendor> vendorHashMap = new HashMap<>();

            for (Vendor vendor : realmVendors) {
                vendorMap.put(vendor.getId(), vendor.getVendorName());
                vendorHashMap.put(vendor.getId(), vendor);
            }

            bookmarkProducts = new ArrayList<>();
            for (BookMarkMenu bookMarkMenu : bookMarkMenus) {
                if (vendorHashMap.get(bookMarkMenu.getVendorId())!=null
                        &&((vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine()
                        && AppUtils.getConfig(BookMarkActivity.this).isVendingMachineEnabled())
                        || !vendorHashMap.get(bookMarkMenu.getVendorId()).isVendingMachine())
                ){
                    bookMarkMenu.setVendorName(vendorMap.get(bookMarkMenu.getVendorId()));
                    Product itemToAdd = bookMarkMenu.copy();
                    itemToAdd.setRecommendationType(ApplicationConstants.RecommendationType.BOOKMARK);
                    bookmarkProducts.add(itemToAdd);
                    vendors.add(vendorHashMap.get(bookMarkMenu.getVendorId()));
                }
            }
            if (bookmarkProducts.size() > 0) {
                showNoBookmark(false);
                if (menuListAdapter == null) {
                    rlMenuList.setVisibility(View.VISIBLE);
                    menuListAdapter = new MenuListAdapter(BookMarkActivity.this, bookmarkProducts, vendors, occasionId, true, expressCheckoutAction, rlNoBookmark);
                    rlMenuList.setAdapter(menuListAdapter);
                    rlMenuList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                            rlMenuList.removeOnLayoutChangeListener(this);
                            bookmarkPopulated = true;
                            if (trendingPopulated && bookmarkPopulated) {
                                startOnboarding();
                            }
                        }
                    });
                } else {
                    menuListAdapter.changeProducts(bookmarkProducts, vendors);
                    menuListAdapter.notifyDataSetChanged();
                    bookmarkPopulated = true;
                }
            } else {
                showNoBookmark(true);
            }
        } else {
            showNoBookmark(true);
        }
    }

    private void showNoBookmark(boolean status){
        if (status){
            rlNoBookmark.setVisibility(View.VISIBLE);
            cvMenu.setVisibility(View.GONE);
        } else{
            rlNoBookmark.setVisibility(View.GONE);
            cvMenu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        MainApplication.bus.register(this);
        if (menuListAdapter != null) {
            menuListAdapter.notifyDataSetChanged();
        }
        if (trendingListAdapter != null) {
            trendingListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        MainApplication.bus.unregister(this);
    }

    @Subscribe
    public void onCartItemAddedEvent(CartItemAddedEvent cartItemAddedEvent) {
        if (isRunning) {
            Intent intent = new Intent(this, BookmarkPaymentActivity.class);
            intent.putExtra(ApplicationConstants.FROM_BOOKMARK, true);
            startActivity(intent);
        }
    }

    private void showErrorDialog(String message) {
        ErrorPopFragment errorHandleDialog = ErrorPopFragment.Companion.newInstance(message, "Go Home", ApplicationConstants.GENERAL_ERROR,new ErrorPopFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                Intent intent = AppUtils.getHomeNavigationIntent(BookMarkActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onNegativeInteraction() {

            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        errorHandleDialog.setCancelable(false);
        fragmentManager.beginTransaction()
                .add(errorHandleDialog, "bookmark_error")
                .commitAllowingStateLoss();
    }

    private void showErrorPopFragment(String msg,String imageType) {

        ErrorPopFragment errorPopFragment = ErrorPopFragment.Companion.newInstance(msg, "Retry",true,imageType, new ErrorPopFragment.OnFragmentInteractionListener() {
            @Override
            public void onPositiveInteraction() {
                fetchBookmarkMenu();
            }

            @Override
            public void onNegativeInteraction() {
                finish();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        errorPopFragment.setCancelable(false);
        fragmentManager.beginTransaction()
                .add(errorPopFragment, "Error Popup")
                .commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        try {
            Spotlight.with(this).closeSpotlight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fromShortcut) {
            Intent intent = AppUtils.getHomeNavigationIntent(BookMarkActivity.this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    private void initialiseConstraints() {
        constraintMatchParent = new ConstraintSet();
        constraintMatchParent.clone(baseCl);
        maximiseBookmarkView();
    }

    private void adjustNoBookmarkView() {

        constraintMatchParent.connect(R.id.cv_menu, ConstraintSet.BOTTOM, baseCl.getId(), ConstraintSet.BOTTOM, 0);
        constraintMatchParent.connect(R.id.cv_trending, ConstraintSet.TOP, R.id.horizontal_guide_top, ConstraintSet.BOTTOM, 0);
        constraintMatchParent.applyTo(baseCl);
    }

    private void maximiseBookmarkView() {

        constraintMatchParent.connect(R.id.cv_menu, ConstraintSet.BOTTOM, baseCl.getId(), ConstraintSet.BOTTOM, 0);
        constraintMatchParent.connect(R.id.cv_trending, ConstraintSet.TOP, baseCl.getId(), ConstraintSet.BOTTOM, 0);
        cvTrending.setVisibility(View.GONE);
        constraintMatchParent.applyTo(baseCl);
    }

    private void minimiseBookmarkView() {

        constraintMatchParent.connect(R.id.cv_menu, ConstraintSet.BOTTOM, R.id.horizontal_guide4, ConstraintSet.TOP, 0);
        constraintMatchParent.connect(R.id.cv_trending, ConstraintSet.TOP, R.id.horizontal_guide3, ConstraintSet.TOP, 0);
        constraintMatchParent.connect(R.id.cv_trending, ConstraintSet.BOTTOM, baseCl.getId(), ConstraintSet.BOTTOM, 0);
        constraintMatchParent.applyTo(baseCl);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        MainApplication mainApplication = (MainApplication)getApplication();
        if(mainApplication.getCart()!=null){
            Gson gson = new Gson();
            String cart = gson.toJson(mainApplication.getCart());
            String selectedOcassionMap = gson.toJson(MainApplication.selectedOcassionMap);
            outState.putString("cart",cart);
            outState.putString("selectedOcassionMap",selectedOcassionMap);
            outState.putLong("selectedOcassionId",MainApplication.selectedOcassionId);
        }

    }
}


