package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.SearchVendorModel;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.activity.GlobalSearchActivity;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.activity.PaymentActivity;
import com.hungerbox.customer.order.adapter.viewholder.SearchVendorViewHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;

public class SearchVendorAdapter extends RecyclerView.Adapter<SearchVendorViewHolder> {
    ArrayList<Vendor> vendorArrayList;
    LayoutInflater inflater;
    Activity activity;

    public SearchVendorAdapter(Activity activity,ArrayList<Vendor> vendorArrayList) {
        this.vendorArrayList = vendorArrayList;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public SearchVendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_vendor_item,parent,false);
        return new SearchVendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchVendorViewHolder holder, int position) {
        Vendor vendor = vendorArrayList.get(position);
        holder.tvVendorName.setText(vendor.getVendorName());
        holder.tvVendorDescription.setText(vendor.getDesc());
        if (vendor.getRating()>0){
            holder.tvVendorRating.setText(String.valueOf(vendor.getRating()));
            holder.tvVendorRating.setVisibility(View.VISIBLE);
            holder.ivStar.setVisibility(View.VISIBLE);
        } else{
            holder.tvVendorRating.setVisibility(View.GONE);
            holder.ivStar.setVisibility(View.GONE);
        }
        if (AppUtils.isSocialDistancingActive(null)){
            if (vendor.isCoronaSafe()) {
                showCoronaSafeBadge(true,holder);
            } else{
                showCoronaSafeBadge(false,holder);
            }
            holder.tvDeliveryType.setVisibility(View.VISIBLE);

            //for delivery type label
            int deskDeliveryType = SharedPrefUtil.getInt(ApplicationConstants.LOCATION_DESK_ORDERING_ENABLED, 0);
            holder.tvDeliveryType.setText(vendor.getDeliveryType(deskDeliveryType));
        } else{
            showCoronaSafeBadge(false,holder);
            holder.tvDeliveryType.setVisibility(View.INVISIBLE);
        }
        holder.tvViewMenu.setOnClickListener((v)->{
            moveToVendorDetails(vendor);
        });

    }
    private void showCoronaSafeBadge(boolean show, SearchVendorViewHolder vendorListItemHolder){
        if (show){
            vendorListItemHolder.ivTick.setVisibility(View.VISIBLE);
            vendorListItemHolder.tvCoronaSafe.setVisibility(View.VISIBLE);
        } else{
            vendorListItemHolder.ivTick.setVisibility(View.GONE);
            vendorListItemHolder.tvCoronaSafe.setVisibility(View.GONE);
        }
    }

    private void moveToVendorDetails(Vendor vendor){
        Intent intent;
        long locationId = SharedPrefUtil.getLong(ApplicationConstants.LOCATION_ID, 11);
        if(vendor.getSdkType() != null && vendor.getSdkType().equals(ApplicationConstants.BIG_BASKET)){
            intent = new Intent(activity, PaymentActivity.class);
            intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, vendor.getSdkType());
            intent.putExtra(ApplicationConstants.BIG_BASKET, true);
            intent.putExtra(ApplicationConstants.OCASSION_ID, MainApplication.selectedOcassionId);
            intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);

        }else{
            intent = new Intent(activity, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ApplicationConstants.VENDOR_ID, vendor.getId());
            intent.putExtra(ApplicationConstants.VENDOR_NAME, vendor.getVendorName());
            if (activity!=null && activity instanceof GlobalSearchActivity){
                intent.putExtra(ApplicationConstants.OCASSION_ID, ((GlobalSearchActivity) activity).occasionId);
            }
            intent.putExtra(ApplicationConstants.LOCATION, SharedPrefUtil.getString(ApplicationConstants.LOCATION_NAME, "India T, BLR"));
        }

        activity.startActivity(intent);
    }

    public void setVendors(ArrayList<Vendor> vendors){
        vendorArrayList = vendors;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (vendorArrayList!=null){
            return vendorArrayList.size();
        }
        return 0;
    }
}
