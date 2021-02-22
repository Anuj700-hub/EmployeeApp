package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.order.adapter.viewholder.VendorListItemHolder;
import com.hungerbox.customer.order.adapter.viewholder.VendorListItemHolderRounded;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

class VendorListAdapter extends RecyclerView.Adapter<VendorListItemHolderRounded> {
    private ArrayList<Vendor> vendors = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;
    private boolean showSocialDistancingFeature = false;
    private long ocassionId = -1;
    private Ocassion ocassion;
    private String location;

    public VendorListAdapter(Activity activity, ArrayList<Vendor> vendors, long ocassionId, Ocassion ocassion, String location) {
        this.vendors = vendors;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.showSocialDistancingFeature = AppUtils.isSocialDistancingActive(null);
        this.ocassionId = ocassionId;
        this.ocassion = ocassion;
        this.location = location;
    }

    @NonNull
    @Override
    public VendorListItemHolderRounded onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vendor_list_horizontal, parent, false);
        return new VendorListItemHolderRounded(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorListItemHolderRounded vendorListItemHolder, int position) {
        final Vendor vendor = vendors.get(position);

        vendorListItemHolder.ivVendor.post(new Runnable() {
            @Override
            public void run() {

                int width = vendorListItemHolder.ivVendor.getMeasuredWidth();
                int height = 3 * width / 4;
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
            vendorListItemHolder.ivStar.setVisibility(View.INVISIBLE);
            vendorListItemHolder.tvRating.setVisibility(View.INVISIBLE);
            vendorListItemHolder.tvRating.setText(vendor.getRatingText());
        } else {
            vendorListItemHolder.ivStar.setVisibility(View.VISIBLE);
            vendorListItemHolder.tvRating.setVisibility(View.VISIBLE);
            vendorListItemHolder.tvRating.setText(vendor.getRatingText());
        }
        vendorListItemHolder.tvVendorTime.setText(String.format("%d pending orders", queuedOrders));
        if (vendor.isBuffetAvailable() || vendor.isRestaurant()) {
            vendorListItemHolder.tvVendorTime.setText(vendor.getAvgOrderTimeText());
        } else if (queuedOrders == 0) {
            vendorListItemHolder.tvVendorTime.setText("No pending orders");
        }

        if (vendor.getMinimumOrderAmount() > 0) {
            vendorListItemHolder.tvMinOrderAmount.setText(vendor.getMinimumOrderAmountStr());
            vendorListItemHolder.tvMinOrderAmount.setVisibility(View.VISIBLE);
        } else {
            vendorListItemHolder.tvMinOrderAmount.setVisibility(View.GONE);
        }

        final long currentTime = DateTimeUtil.adjustCalender(activity).getTimeInMillis();
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

        vendorListItemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ocassion != null && ocassion.isPreorder()) {

                } else if (!isVendorTakingOrder || currentTime > vendorEndTime) {
                    AppUtils.showToast("This vendor is not available right now", false, 2);
                    return;
                }


                try {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(CleverTapEvent.PropertiesNames.getVendor_id(), vendorId);
                    map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                    CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVendor_click(), map, activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LogoutTask.updateTime();

                Intent intent;
                if (vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)) {
                    long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
                    intent = new Intent(activity, PaymentActivity.class);
                    intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, vendor.getSdkType());
                    intent.putExtra(ApplicationConstants.BIG_BASKET, true);
                    intent.putExtra(ApplicationConstants.OCASSION_ID, MainApplication.selectedOcassionId);
                    intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);

                } else {
                    intent = new Intent(activity, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ApplicationConstants.VENDOR_ID, vendorId);
                    intent.putExtra(ApplicationConstants.VENDOR_NAME, vendorName);
                    intent.putExtra(ApplicationConstants.OCASSION_ID, ocassionId);
                    intent.putExtra(ApplicationConstants.LOCATION, location);
                }

                activity.startActivity(intent);

            }
        });

        LogoutTask.updateTime();

        if (!vendor.isVendorTakingOrder()) {
            vendorListItemHolder.tvVendorTime.setText("This vendor is not available right now");
            vendorListItemHolder.tvVendorTime.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }

        if (vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)) {
            vendorListItemHolder.tvVendorTime.setText("");

//            vendorListItemHolder.tvCheckMenu.setText("");
//            vendorListItemHolder.tvCuisines.setText("");
//            vendorListItemHolder.tvVendorTiming.setText("");
//            vendorListItemHolder.tvMinOrderAmount.setText("");
//            vendorListItemHolder.vView.setVisibility(View.GONE);
        }
        if (showSocialDistancingFeature) {
            if (vendor.isCoronaSafe()) {
                showCoronaSafeBadge(true, vendorListItemHolder);
            } else {
                showCoronaSafeBadge(false, vendorListItemHolder);
            }
            vendorListItemHolder.tvDeliveryType.setVisibility(View.VISIBLE);
            //for delivery type label
            int deskDeliveryType = SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0);
            vendorListItemHolder.tvDeliveryType.setText(vendor.getDeliveryType(deskDeliveryType));

            vendorListItemHolder.tvVendorTime.setVisibility(View.INVISIBLE);
        } else {
            showCoronaSafeBadge(false, vendorListItemHolder);
            vendorListItemHolder.tvDeliveryType.setVisibility(View.INVISIBLE);
            vendorListItemHolder.tvVendorTime.setVisibility(View.VISIBLE);
        }
    }

    private void showCoronaSafeBadge(boolean show, VendorListItemHolderRounded vendorListItemHolder) {
        if (show) {
            vendorListItemHolder.ivTick.setVisibility(View.VISIBLE);
            vendorListItemHolder.tvCoronaSafe.setVisibility(View.VISIBLE);
        } else {
            vendorListItemHolder.ivTick.setVisibility(View.INVISIBLE);
            vendorListItemHolder.tvCoronaSafe.setVisibility(View.INVISIBLE);
        }
    }

    private void setVendorImage(final ImageView imageView, String image, final VendorListItemHolderRounded holder, final int position) {

        if (activity == null) {
            return;
        }

        if (image != null && image.isEmpty()) {
            image = null;
        }

        ImageHandling.loadRemoteImage(image, imageView, R.drawable.default_vendor_image, R.drawable.default_vendor_image, activity);
    }


    public void updateVendorList(ArrayList<Vendor> vendors, long occasionId ){
        this.vendors = vendors;
        setOccasionId(occasionId);
        notifyDataSetChanged();
    }

    public void setOccasionId(long occasionId) {
        this.ocassionId = occasionId;
        ocassion = MainApplication.getOcassionForId(ocassionId);
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }
}
