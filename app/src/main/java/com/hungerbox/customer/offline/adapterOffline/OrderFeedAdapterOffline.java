package com.hungerbox.customer.offline.adapterOffline;

import android.content.Intent;
import android.os.Handler;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.activityOffline.GlobalActivityOffline;
import com.hungerbox.customer.offline.activityOffline.MenuActivityOffline;
import com.hungerbox.customer.offline.adapterOffline.viewHolderOffline.BannerOfflineViewHolder;
import com.hungerbox.customer.offline.listenersOffline.VendorValidationListener;
import com.hungerbox.customer.offline.modelOffline.OcassionOffline;
import com.hungerbox.customer.offline.modelOffline.VendorOffline;
import com.hungerbox.customer.order.adapter.viewholder.VendorHeaderItemHolder;
import com.hungerbox.customer.order.adapter.viewholder.VendorListItemHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.SharedPrefUtil;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderFeedAdapterOffline extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static int LOADING_VIEW = 1006;
    public static int RECOMMENDED_VIEW = 1007;
    public static int VENDOR_LIST_VIEW = 1008;
    public static int VENDOR_LIST_HEADER = 1009;
    private final String location;
    private final FragmentManager fm;
    public GlobalActivityOffline activity;
    LayoutInflater inflater;
    ArrayList<VendorOffline> vendors;
    long ocassionId;
    OcassionOffline ocassion;
    boolean isRecommendationAvail;
    VendorValidationListener vendorValidationListener;
    private Handler handler;
    private Runnable runnable;
    private View.OnTouchListener touch;
    private int next = 1;

    public OrderFeedAdapterOffline(GlobalActivityOffline activity, ArrayList<VendorOffline> vendors,
                                   VendorValidationListener vendorValidationListener, long ocassionId,
                                   boolean isRecommendationAvail, ArrayList<Object> baseBanners,
                                   boolean isLoading, String location, FragmentManager fm) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.vendors = vendors;
        this.vendorValidationListener = vendorValidationListener;
        this.ocassionId = ocassionId;
        this.isRecommendationAvail = isRecommendationAvail;
        this.location = location;
        this.fm = fm;
        ocassion = MainApplicationOffline.getOcassionForId(ocassionId, 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return RECOMMENDED_VIEW;
        } else if (position == 1) {
            return VENDOR_LIST_HEADER;
        } else {
            return VENDOR_LIST_VIEW;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VENDOR_LIST_HEADER) {
            return new VendorHeaderItemHolder(inflater.inflate(R.layout.vendor_header_item_offline, parent, false));
        } else if (viewType == RECOMMENDED_VIEW) {
            View view = inflater.inflate(R.layout.banner_offline, parent, false);
            return new BannerOfflineViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.vendor_list_item_offline, parent, false);
            return new VendorListItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        position -= 2;

        if (holder instanceof VendorHeaderItemHolder) {
            setVendorHeader(holder, position);
        } else if (holder instanceof VendorListItemHolder) {
            changeVendorListItem(holder, position);
        }else if(holder instanceof BannerOfflineViewHolder){
            showBanneroffline(holder);
        }
    }

    private void showBanneroffline(final RecyclerView.ViewHolder holder){

        final BannerOfflineViewHolder vendorListItemHolder = (BannerOfflineViewHolder) holder;
        vendorListItemHolder.ivVendor.post(new Runnable() {
            @Override
            public void run() {

                int width = vendorListItemHolder.ivVendor.getMeasuredWidth();
                int height = width / 2;
                ViewGroup.LayoutParams lp = vendorListItemHolder.ivVendor.getLayoutParams();
                lp.height = height;
                vendorListItemHolder.ivVendor.setLayoutParams(lp);
                vendorListItemHolder.ivVendor.requestLayout();
                vendorListItemHolder.ivVendor.setVisibility(View.VISIBLE);

                vendorListItemHolder.ivVendor.setImageResource(R.drawable.offline_banner);
            }
        });
    }


    private void changeVendorListItem(final RecyclerView.ViewHolder holder, final int position) {
        final VendorOffline vendor = vendors.get(position);

        final VendorListItemHolder vendorListItemHolder = (VendorListItemHolder) holder;
        vendorListItemHolder.ivVendor.post(new Runnable() {
            @Override
            public void run() {

                int width = vendorListItemHolder.ivVendor.getMeasuredWidth();
                int height = width / 2;
                ViewGroup.LayoutParams lp = vendorListItemHolder.ivVendor.getLayoutParams();
                lp.height = height;
                vendorListItemHolder.ivVendor.setLayoutParams(lp);
                vendorListItemHolder.ivVendor.requestLayout();
                vendorListItemHolder.ivVendor.setVisibility(View.VISIBLE);
                if (vendor == null)
                    return;
                if (vendor.getImageSrc().equalsIgnoreCase(""))
                    setVendorImage(vendorListItemHolder.ivVendor, vendor.getLogoImageSrc(), vendorListItemHolder, position);
                else
                    setVendorImage(vendorListItemHolder.ivVendor, vendor.getImageSrc(), vendorListItemHolder, position);
            }
        });

        final String vendorName = vendor.getVendorName();
        vendorListItemHolder.tvVendorName.setText(vendorName);
        long queuedOrders = vendor.getQueuedOrders();

        if (vendor.getRating() == 0) {
            vendorListItemHolder.tvRating.setVisibility(View.INVISIBLE);
            vendorListItemHolder.tvRating.setText(vendor.getRatingText());
        } else {
            vendorListItemHolder.tvRating.setVisibility(View.VISIBLE);
            vendorListItemHolder.tvRating.setText(vendor.getRatingText());
        }


        if (vendor.getMinimumOrderAmount() > 0) {
            vendorListItemHolder.tvMinOrderAmount.setText(vendor.getMinimumOrderAmountStr());
            vendorListItemHolder.tvMinOrderAmount.setVisibility(View.VISIBLE);
        } else {
            vendorListItemHolder.tvMinOrderAmount.setVisibility(View.GONE);
        }

        final long currentTime = Calendar.getInstance().getTimeInMillis();
        long vendorStartTime = DateTimeUtil.getTodaysTimeFromString(vendor.getStartTime());
        final long vendorEndTime = DateTimeUtil.getTodaysTimeFromString(vendor.getEndTime());

        if (!AppUtils.getConfig(activity).isHide_timings()) {
            String timingText = "";
            if (currentTime < vendorStartTime) {
                timingText = "starts at " + vendor.getStartTime();
                vendorListItemHolder.vView.setVisibility(View.GONE);
                vendorListItemHolder.tvVendorTiming.setTextColor(ContextCompat.getColor(activity, R.color.text_medium));
            } else if (currentTime > vendorEndTime) {
                timingText = "closes at " + vendor.getEndTime();
                if (ocassion != null && ocassion.isPreorder())
                    vendorListItemHolder.vView.setVisibility(View.GONE);
                else
                    vendorListItemHolder.vView.setVisibility(View.VISIBLE);
                vendorListItemHolder.tvVendorTiming.setTextColor(ContextCompat.getColor(activity, R.color.red));
            } else {
                timingText = "order till " + vendor.getEndTime();
                vendorListItemHolder.vView.setVisibility(View.GONE);
                vendorListItemHolder.tvVendorTiming.setTextColor(ContextCompat.getColor(activity, R.color.text_medium));
            }
            vendorListItemHolder.tvVendorTiming.setText(timingText);
        }
        if (!vendor.isBuffetAvailable()) {
            vendorListItemHolder.tvCheckMenu.setText("Show Menu");
        } else {
            if (vendor.getMenu() != null)
                vendorListItemHolder.tvCheckMenu.setText(String.format("Book Buffet\n(₹%.1f)", vendor.getMenu().getPrice()));
        }

        vendorListItemHolder.tvCuisines.setText(Html.fromHtml(vendor.getDesc()));
        final boolean isVendorTakingOrder = vendor.isVendorTakingOrder();
        final long vendorId = vendor.getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ocassion != null && ocassion.isPreorder()) {

                } else if (!isVendorTakingOrder || currentTime > vendorEndTime) {
                    AppUtils.showToast("This vendor is not available right now", false, 2);
                    return;
                }


                try {
                    HashMap<String,Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getVendor_id(),vendorId);
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVendor_click(), map, activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LogoutTask.updateTime();

                Intent intent;
                if(vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)){
                    AppUtils.showToast("This vendor is not available right now", true, 0);
                }else{
                    intent = new Intent(activity, MenuActivityOffline.class);
                    intent.putExtra(ApplicationConstants.VENDOR_ID, vendorId);
                    intent.putExtra(ApplicationConstants.VENDOR_NAME, vendorName);
                    intent.putExtra(ApplicationConstants.OCASSION_ID, ocassionId);
                    intent.putExtra(ApplicationConstants.LOCATION, location);
                    intent.putExtra("vendorObject",  vendor);
                    activity.startActivity(intent);
                }
            }
        });

        LogoutTask.updateTime();

        if (!vendor.isVendorTakingOrder()) {
            vendorListItemHolder.tvVendorTime.setText("This vendor is not available right now");
            vendorListItemHolder.tvVendorTime.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }

        if(vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)){
            vendorListItemHolder.tvVendorTime.setText("");
        }
    }

    private void setVendorImage(final ImageView imageView, String image, final VendorListItemHolder holder, final int position) {

        //imageView.setImageResource(R.drawable.vendor_offline);
        imageView.setImageResource(R.drawable.default_vendor_image);

    }

    private void setVendorHeader(RecyclerView.ViewHolder holder, int pos) {
        if (pos == -2)
            return;
        if (activity != null) {
            final VendorHeaderItemHolder vendorHeaderItemHolder = (VendorHeaderItemHolder) holder;
            vendorHeaderItemHolder.gifExpressCheckout.setVisibility(View.GONE);

            vendorHeaderItemHolder.vendorText.setText(AppUtils.getConfig(activity).getVendor_header());
        }
    }

    public int getItemCount() {
        return vendors.size() + 2;
    }

    public void replaceVendors(ArrayList<VendorOffline> newVendors) {
        this.vendors = newVendors;
    }

    public void setOcassionId(long ocassionId) {
        this.ocassionId = ocassionId;
        ocassion = MainApplicationOffline.getOcassionForId(ocassionId, 1);
    }


}
