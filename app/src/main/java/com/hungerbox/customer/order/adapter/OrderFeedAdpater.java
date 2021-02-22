package com.hungerbox.customer.order.adapter;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.Config;
import com.hungerbox.customer.bluetooth.Util;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.HomeBannerItem;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Order;
import com.hungerbox.customer.model.OrderResponse;
import com.hungerbox.customer.model.Product;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.GlobalSearchActivity;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.activity.OrderDetailNewActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.order.adapter.viewholder.ProductItemViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.RecommendedListViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.RecommendedLoaderHolder;
import com.hungerbox.customer.order.adapter.viewholder.VendingMachineViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.VendorHeaderItemHolder;
import com.hungerbox.customer.order.adapter.viewholder.VendorListItemHolderRounded;
import com.hungerbox.customer.order.adapter.viewholder.VendorListViewHolder;
import com.hungerbox.customer.order.fragment.GeneralDialogFragment;
import com.hungerbox.customer.order.listeners.UpdateCartListener;
import com.hungerbox.customer.order.listeners.VendorValidationListener;
import com.hungerbox.customer.spaceBooking.SpaceBookingActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.BackgroundOrder;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.OrderUtil;
import com.hungerbox.customer.util.SharedPrefUtil;
import com.hungerbox.customer.util.view.GenericFragmentWithTitle;
import com.rd.animation.type.AnimationType;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmResults;
import pl.droidsonroids.gif.GifDrawable;

public class OrderFeedAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements UpdateCartListener {

    public static int LOADING_VIEW = 1006;
    public static int RECOMMENDED_VIEW = 1007;
    public static int VENDOR_LIST_VIEW = 1008;
    public static int VENDOR_LIST_HEADER = 1009;
    private final String location;
    private final FragmentManager fm;
    public RecommendationPagerAdapter recommendationPagerAdapter;
    public ArrayList<Object> baseBanners;
    public GlobalActivity activity;
    LayoutInflater inflater;
    ArrayList<Object> vendorList;
    MainApplication mainApplication;
    long ocassionId;
    Ocassion ocassion;
    VendorValidationListener vendorValidationListener;
    private Handler handler;
    private Runnable runnable;
    private View.OnTouchListener touch;
    private int next = 1;
    private boolean showSocialDistancingFeature = false,isVendingMachineAvailable,isOneVMVendor;
    private HorizontalBookmarkAdapter bookmarkAdapter;
    private HorizontalBookmarkAdapter trendingAdapter;
    public VendorHeaderItemHolder vendorHeaderItemHolder;
    private VendorListAdapter vendorListAdapter;
    private VendingMachineListAdapter vendingMachineAdapter;
    RecyclerView rvVendorListView,rvVendingMachineList;
    OnVendorHeaderClick onVendorHeaderClick;
    private Vendor slotBookingVendor;
    private Order ongoingSlotBookingOrder;
    private CountDownTimer countDownTimer;
    private String apiTag;
    private GlobalActivity.SlotBookingOrderSuccessListener slotBookingOrderSuccessListener;

    public OrderFeedAdpater(String apiTag, GlobalActivity activity, GlobalActivity.SlotBookingOrderSuccessListener slotBookingOrderSuccessListener,ArrayList<Object> vendorList, Vendor slotBookingVendor,
                            VendorValidationListener vendorValidationListener, long ocassionId, ArrayList<Object> baseBanners,
                            String location, FragmentManager fm, boolean isVendingMachineAvailable, OnVendorHeaderClick onVendorHeaderClick) {

        this.apiTag = apiTag;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.vendorList = vendorList;
        this.slotBookingOrderSuccessListener = slotBookingOrderSuccessListener;
        this.slotBookingVendor = slotBookingVendor;
        this.mainApplication = (MainApplication) activity.getApplication();
        this.vendorValidationListener = vendorValidationListener;
        this.ocassionId = ocassionId;
         if (baseBanners != null && baseBanners.size() == 0) {
            baseBanners.add(new HomeBannerItem());
        }
        this.baseBanners = baseBanners;
        this.location = location;
        this.fm = fm;
        this.isVendingMachineAvailable = isVendingMachineAvailable;
        this.showSocialDistancingFeature = AppUtils.isSocialDistancingActive(null);
        this.onVendorHeaderClick = onVendorHeaderClick;
        ocassion = mainApplication.getOcassionForId(ocassionId);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (baseBanners != null && baseBanners.size() > 0)
                return RECOMMENDED_VIEW;
            else
                return LOADING_VIEW;
        } else if (position == 1) {
            return VENDOR_LIST_HEADER;
        } else {
            return VENDOR_LIST_VIEW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VENDOR_LIST_HEADER) {
            return new VendorHeaderItemHolder(inflater.inflate(R.layout.vendor_header_item, parent, false));
        } else if (viewType == VENDOR_LIST_VIEW) {
            View view = inflater.inflate(R.layout.vendor_list_layout, parent, false);
            return new VendorListViewHolder(view);
        } else if (viewType == RECOMMENDED_VIEW)
            return new RecommendedListViewHolder(inflater.inflate(R.layout.recommended_rv_viewholder, parent, false));
        else
            return new RecommendedLoaderHolder(inflater.inflate(R.layout.recommended_loader_viewholder, parent, false));
    }

    private void changeVendorListItem(final RecyclerView.ViewHolder holder, final int position) {

        VendorListViewHolder viewHolder = (VendorListViewHolder) holder;

        ArrayList<Vendor> normalVendor = (ArrayList<Vendor>) vendorList.get(0);
        ArrayList<Vendor> vendingMachineVendor = (ArrayList<Vendor>) vendorList.get(1);

        if(normalVendor.size()>0){
            viewHolder.cvVendorList.setVisibility(View.VISIBLE);
        }else{
            viewHolder.cvVendorList.setVisibility(View.GONE);
        }

        if(vendingMachineVendor.size()>0){
            viewHolder.cvVendingList.setVisibility(View.VISIBLE);
        }else{
            viewHolder.cvVendingList.setVisibility(View.GONE);
        }
        viewHolder.tvVendorListTitle.setText(AppUtils.getConfig(activity).getVendor_header());
        if (vendorListAdapter == null) {
            vendorListAdapter = new VendorListAdapter(activity, normalVendor, ocassionId, ocassion, location);
            viewHolder.rvVendorList.setAdapter(vendorListAdapter);
            rvVendorListView = viewHolder.rvVendorList;
            vendorListAdapter.notifyDataSetChanged();
        } else {
            vendorListAdapter.updateVendorList(normalVendor,ocassionId);
        }

        isOneVMVendor = vendingMachineVendor.size() == 1;
        rvVendingMachineList = viewHolder.rvVendingList;

        if(isOneVMVendor){
            viewHolder.tvVendingListTitle.setText("Vending Machine");
        }else{
            viewHolder.tvVendingListTitle.setText("Vending Machines");
        }

        if (vendingMachineAdapter == null) {
            vendingMachineAdapter = new VendingMachineListAdapter(activity, vendingMachineVendor, ocassionId, ocassion, location);
            viewHolder.rvVendingList.setAdapter(vendingMachineAdapter);
            vendingMachineAdapter.notifyDataSetChanged();
        } else {
            vendingMachineAdapter.updateVendorList(vendingMachineVendor,ocassionId);
        }

    }

    private void changeRecommendationItemView(final RecyclerView.ViewHolder holder, int position) {
        final RecommendedListViewHolder recommendedListItemHolder = (RecommendedListViewHolder) holder;

        if (activity != null && activity.getApplicationContext() != null) {
            recommendationPagerAdapter = new RecommendationPagerAdapter(fm
                    , LayoutInflater.from(activity), baseBanners,
                    new RecommendationPagerAdapter.ClickInterface() {
                        @Override
                        public void OnAddToCart(Vendor vendor, Product product, boolean isBuffet) {
                            HashMap<String, Object> map = new HashMap<>();

                            mainApplication.getCart().addProductToCart(product, recommendedListItemHolder,
                                    mainApplication, vendorValidationListener, activity, vendor, ocassionId, map);
                            recommendationPagerAdapter.onOrderItemChanged();

                            try {
                                JSONObject jo = new JSONObject();
                                jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                                HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.RECOMMENDED_ADDITEM, jo);

                                Bundle bundle = new Bundle();
                                bundle.putString(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                                EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.RECOMMENDED_ADDITEM, bundle);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        }

                        @Override
                        public void OnCheckout(Vendor vendor, Product product, boolean isBuffet) {
                            EventUtil.FbEventLog(activity, EventUtil.HOME_RECOMMENDED_ADDITEM, EventUtil.SCREEN_HOME);

                            try {
                                JSONObject jo = new JSONObject();
                                jo.put(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Home");
                                HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.CHECKOUT_RECOMMENDED, jo);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                            HashMap<String, Object> map = new HashMap<>();


                            mainApplication.getCart().addProductToCart(product, recommendedListItemHolder,
                                    mainApplication, vendorValidationListener, activity, vendor, ocassionId, map);

                        }

                        @Override
                        public void GoToOrderReview() {
                            EventUtil.FbEventLog(activity, EventUtil.HOME_CHECKOUT_NOITEM, EventUtil.SCREEN_HOME);

                            HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.CHECKOUT_NOITEM);

//                            Intent intent = new Intent(activity, OrderReviewActivity.class);
//                            Intent intent = new Intent(activity, OrderReviewAndPaymentActivity.class);
                            Intent intent = new Intent(activity, BookmarkPaymentActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Recommended Home");
                            intent.putExtra("occasion_id", ocassionId);
                            activity.startActivity(intent);
                        }
                    }, mainApplication, ocassionId);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            final Point screen = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(screen);
            final int padding = (int) (screen.x / 20.0);

            //
            float elevation = AppUtils.convertDpToPixel(5, activity);
            float radius = AppUtils.convertDpToPixel(8, activity);
            ;
            double cos45 = Math.cos(Math.toRadians(45));
            int horizontalPadding = (int) (elevation + (1 - cos45) * radius);
            int verticalPadding = (int) (elevation * 1.5 + (1 - cos45) * radius);
            //

            double desiredWidth = AppUtils.getWidthImageBG(activity) - 2 * padding - 4 * horizontalPadding;
            double desiredHeight = desiredWidth * ApplicationConstants.ASPECT_RATIO.MARKET_BANNER + 2 * verticalPadding;

            recommendedListItemHolder.vpRecommendations.getLayoutParams().height = (int) desiredHeight;
            recommendedListItemHolder.vpRecommendations.setClipToPadding(false);
            recommendedListItemHolder.vpRecommendations.setPadding(padding, 0, padding, 0);
            recommendedListItemHolder.vpRecommendations.setAdapter(recommendationPagerAdapter);

            recommendedListItemHolder.pageIndicatorView.setCount(baseBanners.size());
            recommendedListItemHolder.pageIndicatorView.setSelection(0);
            recommendedListItemHolder.pageIndicatorView.setAnimationType(AnimationType.WORM);
            recommendedListItemHolder.pageIndicatorView.setViewPager(recommendedListItemHolder.vpRecommendations);
            recommendedListItemHolder.vpRecommendations.setCurrentItem(0);

            try {
                if (AppUtils.getConfig(activity).getBanner_auto_move_time() != 0) {
                    try {
                        if (handler != null) {
                            handler.removeCallbacks(runnable);
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    handler = new Handler(activity.getMainLooper());
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (activity != null) {

                                if (!activity.isRunning()) {
                                    if (baseBanners.size() > 1) {
                                        handler.postDelayed(runnable, AppUtils.getConfig(activity).getBanner_auto_move_time());
                                    }
                                    return;
                                }

                                try {
                                    if (recommendedListItemHolder.vpRecommendations.getCurrentItem() >= baseBanners.size() - 1) {
                                        next = -1;
                                    } else if (recommendedListItemHolder.vpRecommendations.getCurrentItem() <= 0) {
                                        next = 1;
                                    }
                                    recommendedListItemHolder.vpRecommendations.setCurrentItem(recommendedListItemHolder.vpRecommendations.getCurrentItem() + next, true);
                                    if (baseBanners.size() > 1) {
                                        handler.postDelayed(runnable, AppUtils.getConfig(activity).getBanner_auto_move_time());
                                    }
                                } catch (Exception exp) {
                                    exp.printStackTrace();
                                }
                            }

                        }
                    };
                    if (baseBanners.size() > 1) {
                        handler.postDelayed(runnable, AppUtils.getConfig(activity).getBanner_auto_move_time());
                    }

                    touch = new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    try {
                                        if (handler != null) {
                                            handler.removeCallbacks(runnable);
                                        }
                                    } catch (Exception exp) {
                                        exp.printStackTrace();
                                    }

                                    return false;

                                case MotionEvent.ACTION_UP:
                                    try {
                                        if (baseBanners.size() > 1) {
                                            try {
                                                if (handler != null) {
                                                    handler.removeCallbacks(runnable);
                                                }
                                            } catch (Exception exp) {
                                                exp.printStackTrace();
                                            }
                                            handler.postDelayed(runnable, AppUtils.getConfig(activity).getBanner_auto_move_time());
                                        }
                                    } catch (Exception exp) {
                                        exp.printStackTrace();
                                    }
                                    return false;
                            }

                            return false;
                        }
                    };
                    recommendedListItemHolder.vpRecommendations.setOnTouchListener(touch);
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        } else {
            return;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        position -= 2;

        if (holder instanceof VendorHeaderItemHolder) {
            setVendorHeader(holder);
        } else if (holder instanceof VendorListViewHolder) {
            changeVendorListItem(holder, position);
        } else if (holder instanceof RecommendedListViewHolder) {
            changeRecommendationItemView(holder, position);
        } else {
            RecommendedLoaderHolder recommendedLoaderHolder = (RecommendedLoaderHolder) holder;
        }
    }

    private void setVendorHeader(RecyclerView.ViewHolder holder) {
        if (activity != null) {
            vendorHeaderItemHolder = (VendorHeaderItemHolder) holder;

            vendorHeaderItemHolder.vendorText.setText(AppUtils.getConfig(activity).getVendor_header());

            vendorHeaderItemHolder.rlSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToSearchScreen();
                }
            });
            vendorHeaderItemHolder.etSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToSearchScreen();
                }
            });
            vendorHeaderItemHolder.btFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToSearchScreen();
                }
            });

            if(isVendingMachineAvailable){
                vendorHeaderItemHolder.cvVendingListHeader.setVisibility(View.VISIBLE);
            }else{
                vendorHeaderItemHolder.cvVendingListHeader.setVisibility(View.GONE);
            }

            vendorHeaderItemHolder.cvVendingListHeader.setOnClickListener(v -> {
                if(isOneVMVendor){
                    VendingMachineViewHolder vmViewHolder =(VendingMachineViewHolder) rvVendingMachineList.findViewHolderForAdapterPosition(0);
                    if(vmViewHolder!=null){
                        vmViewHolder.itemView.performClick();
                    }
                }else {
                    onVendorHeaderClick.onClick(rvVendorListView.getHeight());
                }

            });

            if (slotBookingVendor!=null){
                setSlotBookingVendor();
            } else{
                vendorHeaderItemHolder.cvSlot.setVisibility(View.GONE);
            }

            Config.SpaceManagement spaceManagement = AppUtils.getSpaceConfig(activity);
            if(spaceManagement!=null && spaceManagement.getOn_dashboard()!=null){
                Config.SpaceType spaceType = AppUtils.getSpaceType(activity,spaceManagement.getOn_dashboard());
                if(spaceType!=null) {
                    vendorHeaderItemHolder.cvSpaceBooking.setVisibility(View.VISIBLE);
                    vendorHeaderItemHolder.tvSpaceTitle.setText(spaceType.getName());
                    vendorHeaderItemHolder.tvSpaceSubtitle.setText(spaceType.getDescription());
                    ImageHandling.loadRemoteImage(spaceType.getIcon_url(), vendorHeaderItemHolder.ivSpaceBooking, R.drawable.ic_table_booking_icon_big, R.drawable.ic_table_booking_icon_big, activity);
                    vendorHeaderItemHolder.btSpaceBook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mainApplication.getCart().getOrderProducts().size()>0){
                                showRemoveCartItemDialog(spaceType.getKey());
                            }else{
                                navigateToSpaceBookActivity(spaceType.getKey());
                            }
                        }
                    });

                }else{
                    vendorHeaderItemHolder.cvSpaceBooking.setVisibility(View.GONE);
                }
            }else{
                vendorHeaderItemHolder.cvSpaceBooking.setVisibility(View.GONE);
            }

//            if (bookmarkItems!=null && bookmarkItems.size()>0){
//                vendorHeaderItemHolder.gifExpressCheckout.setVisibility(View.GONE);
//                setBookmarkItems(bookmarkItems);
//            } else{
//                vendorHeaderItemHolder.layoutFavourites.setVisibility(View.GONE);
//                vendorHeaderItemHolder.layoutTrending.setVisibility(View.GONE);
//            }
//
//            if (trendingItems!=null && trendingItems.size()>0){
//                vendorHeaderItemHolder.gifExpressCheckout.setVisibility(View.GONE);
//                setTrendingItems(trendingItems);
//            } else{
//                vendorHeaderItemHolder.layoutTrending.setVisibility(View.GONE);
//            }
        }
    }


    private void slotBookingHandling(Vendor vendor){
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0);
        if (locationId != 0) {
            BackgroundOrder backgroundOrder = new BackgroundOrder(apiTag, activity, vendor.getId(), ocassionId, locationId, new BackgroundOrder.BackgroundOrderPlaceListener() {
                @Override
                public void onSuccess(@NotNull OrderResponse orderResponse) {
                    SharedPrefUtil.putLong(Util.BLUETOOTH_LAST_ORDER_ID, orderResponse.order.getId());
                    activateSlotBookingView(orderResponse.order);
                    slotBookingOrderSuccessListener.onOrderPlaced();
                }

                @Override
                public void onError(@NotNull String error) {
                    AppUtils.showToast(error,true,0);
                    toggleSlotBookButton(true);
                }

                @Override
                public void onFreeQuantityExhausted() {
                    AppUtils.showToast(AppUtils.getConfig(MainApplication.appContext).getCongestion_msg(),true,0);
                    toggleSlotBookButton(true);
                }
            });
            //eta dialog click listener
            GenericFragmentWithTitle.OnFragmentInteractionListener etaConfirmListener = new GenericFragmentWithTitle.OnFragmentInteractionListener() {
                @Override
                public void onPositiveInteraction() {
                    backgroundOrder.getProductFromVendor(true);
                }

                @Override
                public void onNegativeInteraction() {
                    toggleSlotBookButton(true);
                }
            };

            //get Eta from server
            if(isCurrentOccasionOrder(vendor) && AppUtils.isSocialDistancingActive(null)) {
                backgroundOrder.getEtaFromServer(new BackgroundOrder.GetEtaListener() {
                    @Override
                    public void onEtaReceived(@NotNull String eta) {

                        showEtaPopUp(eta, etaConfirmListener,"You can enter the cafe at: ");
                    }

                    @Override
                    public void onError(@NotNull String error) {
                        AppUtils.showToast(error, true, 0);
                        toggleSlotBookButton(true);
                    }
                });
            }else{

                Ocassion currentOccasion = MainApplication.getOcassionForId(ocassionId);
                long vendorStartTime = DateTimeUtil.getTodaysTimeFromStringNew(vendor.getStartTime(), activity);
                String time = "";

                if(currentOccasion.getPreOrderDay() == Ocassion.PreOrderDayType.TODAY){
                    time = DateTimeUtil.getDateStringCustom(vendorStartTime, "HH:mm");
                }else{
                    long nextDayTime = AppUtils.getNextWorkingDayTime(currentOccasion.getPreOrderDay(), vendorStartTime);
                    time = DateTimeUtil.getDateStringCustom(nextDayTime, "dd MMM yyyy, hh:mm aa");
                }

                String etaText = String.format(activity.getString(R.string.preorder_message), currentOccasion == null ? " " : currentOccasion.name)+"\n" +"You can enter the cafe at: ";
                showEtaPopUp(time, etaConfirmListener,etaText);
            }
            //backgroundOrder.getProductFromVendor(true);
        }
    }

    private boolean isCurrentOccasionOrder(Vendor vendor){

        long currentTime = DateTimeUtil.adjustCalender(activity).getTimeInMillis();
        Ocassion currentOccasion = MainApplication.getOcassionForId(ocassionId);

        return currentOccasion!=null &&
                currentTime > DateTimeUtil.getTodaysTimeFromStringNew(vendor.getStartTime(), activity) &&
                (currentOccasion.getPreOrderDay() == Ocassion.PreOrderDayType.TODAY);
    }


    private void showEtaPopUp(String eta, GenericFragmentWithTitle.OnFragmentInteractionListener mListener,String text){
        if(activity==null)
            return;
        String title = "Entry Time Confirmation";
        String body = text+eta;
        SpannableString spannableTitle = new SpannableString(title);
        SpannableString spannableBody = new SpannableString(body);

        spannableTitle.setSpan(new StyleSpan(Typeface.BOLD),0,title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableTitle.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.colorAccent)),0
                ,title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        spannableBody.setSpan(new StyleSpan(Typeface.BOLD),body.length()-eta.length(),body.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableBody.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.colorAccent)),body.length()-eta.length()
                ,body.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        GenericFragmentWithTitle generalDialogFragment = GenericFragmentWithTitle.newInstance(spannableTitle,
                spannableBody,"CONFIRM","CANCEL", mListener);
        generalDialogFragment.show(activity.getSupportFragmentManager(), "dialog");
    }
    public void toggleSlotBookButton(boolean enabled){
        if (vendorHeaderItemHolder!=null){
            if (enabled){
                vendorHeaderItemHolder.btBookSlot.setEnabled(true);
            } else{
                vendorHeaderItemHolder.btBookSlot.setEnabled(false);
            }
        }
    }
    public void setBookmarkItems(ArrayList<Object> bookmarkItems, ArrayList<Vendor> vendors) {
        if (bookmarkItems.size() > 1 && vendorHeaderItemHolder != null && vendorHeaderItemHolder.rvFavourites != null && vendorHeaderItemHolder != null && activity!=null) {
            vendorHeaderItemHolder.layoutFavourites.setVisibility(View.VISIBLE);
            if (bookmarkAdapter == null) {
                bookmarkAdapter = new HorizontalBookmarkAdapter(activity, bookmarkItems, vendors, ocassionId, true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                vendorHeaderItemHolder.rvFavourites.setAdapter(bookmarkAdapter);
                vendorHeaderItemHolder.rvFavourites.setLayoutManager(layoutManager);
//                notifyDataSetChanged();
            } else {
                bookmarkAdapter.updateBookmarkList(bookmarkItems,vendors,ocassionId);
                bookmarkAdapter.notifyDataSetChanged();
            }
        }

    }

    public void setTrendingItems(ArrayList<Object> trendingItems, ArrayList<Vendor> vendors) {
        if (trendingItems.size() > 1 && vendorHeaderItemHolder != null && vendorHeaderItemHolder.rvTrending != null && vendorHeaderItemHolder != null && activity!=null) {
            vendorHeaderItemHolder.layoutTrending.setVisibility(View.VISIBLE);
            if (trendingAdapter == null) {
                trendingAdapter = new HorizontalBookmarkAdapter(activity, trendingItems, vendors, ocassionId, false);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                vendorHeaderItemHolder.rvTrending.setAdapter(trendingAdapter);
                vendorHeaderItemHolder.rvTrending.setLayoutManager(layoutManager);
//                notifyDataSetChanged();
            } else {
                trendingAdapter.updateBookmarkList(trendingItems,vendors,ocassionId);
                trendingAdapter.notifyDataSetChanged();
            }
        }

    }

    private void setSlotBookingVendor(){
        if (vendorHeaderItemHolder!=null && slotBookingVendor!=null) {
            vendorHeaderItemHolder.cvSlot.setVisibility(View.VISIBLE);
            if (ongoingSlotBookingOrder == null) {
                vendorHeaderItemHolder.btBookSlot.setOnClickListener((v) -> {
                    vendorHeaderItemHolder.btBookSlot.setEnabled(false);
                    slotBookingHandling(slotBookingVendor);
                });
            }

        }
    }

    public void activateSlotBookingView(Order order){
        //remove order from open
        ongoingSlotBookingOrder = order;
        if (vendorHeaderItemHolder!=null && order!=null && activity!=null) {
            vendorHeaderItemHolder.cvSlot.setVisibility(View.VISIBLE);

            vendorHeaderItemHolder.tvSlotTime.setText(DateTimeUtil.getDateStringCustom(order.getUserEntryTime()*1000,"HH:mm aa"));
            vendorHeaderItemHolder.btBookSlot.setVisibility(View.GONE);
            vendorHeaderItemHolder.qr.setVisibility(View.VISIBLE);
            switch (order.getOrderStatus()){
                case OrderUtil.PRE_ORDER : {//start timer, QR disabled, you can enter cafe in 00:00
                    vendorHeaderItemHolder.qr.setImageDrawable(activity.getResources().getDrawable(R.drawable.qr_disabled));
                    vendorHeaderItemHolder.qr.setEnabled(false);
                    vendorHeaderItemHolder.tvSlotBookingHeader.setText(activity.getResources().getString(R.string.slot_booking_title_active));
                    vendorHeaderItemHolder.tvSlotBookingSubTitle.setText(activity.getResources().getString(R.string.slot_booking_sub_title_active));
                    startTimer(order.getPreOrderDeliveryTime(),vendorHeaderItemHolder.tvSlotTime);
                }
                break;
                default ://end timer, show button , SHOW QR and move to detail on click
                    vendorHeaderItemHolder.tvSlotBookingHeader.setText(activity.getResources().getString(R.string.slot_booking_title_confirmed));
                    vendorHeaderItemHolder.tvSlotBookingSubTitle.setText(activity.getResources().getString(R.string.slot_booking_sub_title_confirmed));
                    vendorHeaderItemHolder.tvSlotTime.setVisibility(View.GONE);
                    vendorHeaderItemHolder.qr.setEnabled(true);
                    vendorHeaderItemHolder.qr.setImageDrawable(activity.getResources().getDrawable(R.drawable.qr_enabled));
                    vendorHeaderItemHolder.qr.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, OrderDetailNewActivity.class);
                        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Slot booking");
                        intent.putExtra(ApplicationConstants.FROM_SLOT_BOOKING, true);
                        intent.putExtra(ApplicationConstants.BOOKING_ID, order.getId());
                        intent.putExtra(ApplicationConstants.IS_ORDER_ARCHIVED, order.isArchived());
                        intent.putExtra(ApplicationConstants.IS_NEW_ORDER, true);
                        activity.startActivity(intent);
                    });
                    break;
            }

        }
    }

    public void deActivateSlotBookingView(){
        ongoingSlotBookingOrder = null;
        if (vendorHeaderItemHolder!=null && activity!=null) {
            if (slotBookingVendor!=null) {
                vendorHeaderItemHolder.cvSlot.setVisibility(View.VISIBLE);
                vendorHeaderItemHolder.tvSlotBookingHeader.setText(activity.getResources().getString(R.string.slot_booking_title_inactive));
                vendorHeaderItemHolder.tvSlotBookingSubTitle.setText(activity.getResources().getString(R.string.slot_booking_sub_title_inactive));
                vendorHeaderItemHolder.tvSlotTime.setVisibility(View.GONE);
                vendorHeaderItemHolder.qr.setVisibility(View.GONE);
                vendorHeaderItemHolder.btBookSlot.setText(activity.getResources().getString(R.string.slot_booking_button_inactive));
                vendorHeaderItemHolder.btBookSlot.setVisibility(View.VISIBLE);
                vendorHeaderItemHolder.btBookSlot.setEnabled(true);
                vendorHeaderItemHolder.btBookSlot.setOnClickListener((v) -> {
                    if (slotBookingVendor != null) {
                        toggleSlotBookButton(false);
                        slotBookingHandling(slotBookingVendor);
                    }
                });
            } else{
                vendorHeaderItemHolder.cvSlot.setVisibility(View.GONE);
            }

        }
    }
    public void startTimer(long time, TextView tvTimer){

        Calendar now = DateTimeUtil.adjustCalender(MainApplication.appContext);
        Calendar estTime = Calendar.getInstance();
        estTime.setTimeInMillis(time * 1000);
        tvTimer.setVisibility(View.VISIBLE);
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }

        if (now.compareTo(estTime) < 0) {
            countDownTimer = new CountDownTimer(estTime.getTimeInMillis() - now.getTimeInMillis(), 1000) {

                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(DateTimeUtil.getTimeLeftCustom(time).toString());
                }

                public void onFinish() {

                }
            }.start();
        }
    }

    private void moveToSearchScreen() {
        Intent intent = new Intent(activity, GlobalSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstants.OCASSION_ID, ocassionId);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return vendorList.size() + 1;
    }

    public void replaceVendors(ArrayList<Object> vendorList,boolean isVendingMachineAvailable) {
        this.vendorList = vendorList;
        this.isVendingMachineAvailable = isVendingMachineAvailable;
        if (baseBanners != null && baseBanners.size() == 0) {
            baseBanners.add(new HomeBannerItem());
        }
    }

    public void setSlotBookingVendor(Vendor slotBookingVendor){
        this.slotBookingVendor = slotBookingVendor;
        if (slotBookingVendor == null && vendorHeaderItemHolder!=null){
            vendorHeaderItemHolder.cvSlot.setVisibility(View.GONE);

        }
    }

    public void setOcassionId(long ocassionId) {
        this.ocassionId = ocassionId;
        ocassion = mainApplication.getOcassionForId(ocassionId);
    }

    public CountDownTimer getCountDownTimer(){
        return countDownTimer;
    }

    @Override
    public void onOrderItemChanged() {
        if (recommendationPagerAdapter != null)
            recommendationPagerAdapter.onOrderItemChanged();
    }

    @Override
    public void onBannerAdded(ArrayList<Object> banners) {
        if (recommendationPagerAdapter != null)
            recommendationPagerAdapter.onBannerAdded(banners);
        else {

        }
    }

    private void showRemoveCartItemDialog(String spaceTypeKey){
        if(activity==null)
            return;
        GeneralDialogFragment generalDialogFragment = GeneralDialogFragment.newInstance("Replace cart item?",
                "All the previous items in the cart will be discarded.", new GeneralDialogFragment.OnDialogFragmentClickListener() {
                    @Override
                    public void onPositiveInteraction(GeneralDialogFragment dialog) {
                        mainApplication.clearOrder();
                        navigateToSpaceBookActivity(spaceTypeKey);
                    }

                    @Override
                    public void onNegativeInteraction(GeneralDialogFragment dialog) {
                        notifyDataSetChanged();
                    }
                });
        generalDialogFragment.show(activity.getSupportFragmentManager(), "dialog");
    }

    private void navigateToSpaceBookActivity(String spaceTypeKey){

        Intent intent = new Intent(activity, SpaceBookingActivity.class);
        intent.putExtra("type", spaceTypeKey);

        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 0); //remove this later
        intent.putExtra("locationId", locationId);

        activity.startActivity(intent);
    }


    public interface OnVendorHeaderClick{
        void onClick(int position);
    }

}
