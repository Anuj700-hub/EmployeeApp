package com.hungerbox.customer.order.adapter;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hungerbox.customer.HBMixpanel;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.adapter.viewholder.VendorSearchItemViewHolder;
import com.hungerbox.customer.order.listeners.VendorSelectedListener;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by sandipanmitra on 12/18/17.
 */

public class VendorSearchListAdapter extends RecyclerView.Adapter<VendorSearchItemViewHolder> {


    List<Vendor> vendors;
    LayoutInflater layoutInflater;
    Activity activity;
    VendorSelectedListener vendorSelectedListener;
    private boolean showSocialDistancingFeature = false;

    public VendorSearchListAdapter(Activity activity, List<Vendor> vendors, VendorSelectedListener vendorSelectedListener) {
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
        this.vendors = vendors;
        this.vendorSelectedListener = vendorSelectedListener;
        this.showSocialDistancingFeature = AppUtils.isSocialDistancingActive(null);
    }


    @Override
    public VendorSearchItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VendorSearchItemViewHolder(layoutInflater.inflate(R.layout.vendor_list_search, parent, false),activity);
    }

    @Override
    public void onBindViewHolder(VendorSearchItemViewHolder holder, int position) {
        final Vendor vendor = vendors.get(position);
        long queuedOrders = vendor.getQueuedOrders();
        holder.tvVendorName.setText(vendor.getVendorName());


        final VendorSearchItemViewHolder vendorListItemHolder = (VendorSearchItemViewHolder) holder;
        vendorListItemHolder.ivVendorLogo.post(new Runnable() {
            @Override
            public void run() {

                int width = vendorListItemHolder.ivVendorLogo.getMeasuredWidth();
                int height = 3*width / 4;
                ViewGroup.LayoutParams lp = vendorListItemHolder.ivVendorLogo.getLayoutParams();
                lp.height = height;
                vendorListItemHolder.ivVendorLogo.setLayoutParams(lp);
                vendorListItemHolder.ivVendorLogo.requestLayout();
                vendorListItemHolder.ivVendorLogo.setVisibility(View.VISIBLE);
                if(vendor==null )
                    return;
                if (vendor.getImageSrc().equalsIgnoreCase(""))
                    setVendorImage(vendorListItemHolder.ivVendorLogo, vendor.getLogoImageSrc(), vendorListItemHolder, position);
                else
                    setVendorImage(vendorListItemHolder.ivVendorLogo, vendor.getImageSrc(), vendorListItemHolder, position);
            }
        });


        holder.tvVendorTime.setText(String.format("%d pending orders", queuedOrders));
        if (vendor.isBuffetAvailable() || vendor.isRestaurant()) {
            holder.tvVendorTime.setText(vendor.getAvgOrderTimeText());
        } else if (queuedOrders == 0) {
            holder.tvVendorTime.setText("No pending orders");
        }
        if (!vendor.isVendorTakingOrder()) {
            holder.tvVendorTime.setText("This vendor is not available right now");
            holder.tvVendorTime.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }

        if (vendor.getRating() == 0) {
            holder.ivStar.setVisibility(View.INVISIBLE);
            holder.tvRating.setVisibility(View.INVISIBLE);
            holder.tvRating.setText(vendor.getRatingText());
        } else {
            holder.ivStar.setVisibility(View.VISIBLE);
            holder.tvRating.setVisibility(View.VISIBLE);
            holder.tvRating.setText(vendor.getRatingText());
        }

        if(vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)){
            vendorListItemHolder.tvVendorTime.setText("");
        }

        if (showSocialDistancingFeature){

            if (vendor.isCoronaSafe()) {
                showCoronaSafeBadge(true,vendorListItemHolder);
            }
            else{
                showCoronaSafeBadge(false,vendorListItemHolder);
            }

            vendorListItemHolder.tvDeliveryType.setVisibility(View.VISIBLE);

            int deskDeliveryType = SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0);
            vendorListItemHolder.tvDeliveryType.setText(vendor.getDeliveryType(deskDeliveryType));

            vendorListItemHolder.tvVendorTime.setVisibility(View.INVISIBLE);
        }
        else{
            showCoronaSafeBadge(false,vendorListItemHolder);
            vendorListItemHolder.tvDeliveryType.setVisibility(View.INVISIBLE);
            vendorListItemHolder.tvVendorTime.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EventUtil.FbEventLog(activity, EventUtil.MixpanelEvent.SEARCH_SUCCESS, "");

                HBMixpanel.getInstance().addEvent(activity, EventUtil.MixpanelEvent.SEARCH_SUCCESS);

                if (vendorSelectedListener != null)
                    vendorSelectedListener.onVendorSelected(vendor);
            }
        });
    }

    private void showCoronaSafeBadge(boolean show, VendorSearchItemViewHolder holder){
        if (show){
            holder.ivTick.setVisibility(View.VISIBLE);
            holder.tvCoronaSafe.setVisibility(View.VISIBLE);
        } else{
            holder.ivTick.setVisibility(View.INVISIBLE);
            holder.tvCoronaSafe.setVisibility(View.INVISIBLE);
        }
    }

    private void setVendorImage(final ImageView imageView, String image, final VendorSearchItemViewHolder holder, final int position) {

        if(activity == null){
            return;
        }

        if(image != null && image.isEmpty()){
            image = null;
        }

        ImageHandling.loadRemoteImage(image, imageView, R.drawable.default_vendor_image, R.drawable.default_vendor_image, activity);
    }

    @Override
    public int getItemCount() {
        if (vendors != null) {
            return vendors.size();
        } else {
            return 0;
        }
    }

    public void updateList(RealmResults<Vendor> vendorResult) {
        this.vendors = vendorResult;
    }
}
