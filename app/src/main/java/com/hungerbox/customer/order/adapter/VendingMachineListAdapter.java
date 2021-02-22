package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.model.Vendor;
import com.hungerbox.customer.order.activity.MenuActivity;
import com.hungerbox.customer.order.adapter.viewholder.VendingMachineViewHolder;
import com.hungerbox.customer.order.adapter.viewholder.VendorListItemHolderRounded;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;
import java.util.HashMap;

class VendingMachineListAdapter extends RecyclerView.Adapter<VendingMachineViewHolder> {

    private ArrayList<Vendor> vendors = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;
    private long ocassionId = -1;
    private Ocassion ocassion;
    private String location;

    public VendingMachineListAdapter(Activity activity, ArrayList<Vendor> vendors, long ocassionId, Ocassion ocassion, String location) {
        this.vendors = vendors;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.ocassionId = ocassionId;
        this.ocassion = ocassion;
        this.location = location;
    }

    @NonNull
    @Override
    public VendingMachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VendingMachineViewHolder(inflater.inflate(R.layout.vending_machine_layout, parent, false),activity);
    }

    @Override
    public void onBindViewHolder(@NonNull VendingMachineViewHolder holder, int position) {

        Vendor vendor = vendors.get(position);


        holder.ivVMImage.post(new Runnable() {
            @Override
            public void run() {

                int width = holder.ivVMImage.getMeasuredWidth();
                int height = 3 * width / 4;
                ViewGroup.LayoutParams lp = holder.ivVMImage.getLayoutParams();
                lp.height = height;
                holder.ivVMImage.setLayoutParams(lp);
                holder.ivVMImage.requestLayout();
                holder.ivVMImage.setVisibility(View.VISIBLE);
                if (vendor == null)
                    return;
                if (vendor.getImageSrc().equalsIgnoreCase(""))
                    setVendorImage(holder.ivVMImage, vendor.getLogoImageSrc());
                else
                    setVendorImage(holder.ivVMImage, vendor.getImageSrc());
            }
        });


        holder.tvVMName.setText(vendor.getVendorName());
        if(vendor.getDesc().length()>0) {
            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.tvDesc.setText(Html.fromHtml(vendor.getDesc()));
        }else{
            holder.tvDesc.setVisibility(View.GONE);
        }


        final long currentTime = DateTimeUtil.adjustCalender(activity).getTimeInMillis();
        long vendorStartTime = DateTimeUtil.getTodaysTimeFromString(vendor.getStartTime());
        final long vendorEndTime = DateTimeUtil.getTodaysTimeFromString(vendor.getEndTime());
        final boolean isVendorTakingOrder = vendor.isVendorTakingOrder();


        holder.itemView.setOnClickListener(v -> {

            if (!isVendorTakingOrder || currentTime > vendorEndTime) {
                AppUtils.showToast("This vendor is not available right now", false, 2);
                return;
            }

            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put(CleverTapEvent.PropertiesNames.getVendor_id(), vendor.getId());
                map.put(CleverTapEvent.PropertiesNames.getUserId(), SharedPrefUtil.getLong(ApplicationConstants.PREF_USER_ID));
                CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getVendor_click(), map, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(activity, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ApplicationConstants.VENDOR_ID, vendor.getId());
            intent.putExtra(ApplicationConstants.VENDOR_NAME, vendor.getVendorName());
            intent.putExtra(ApplicationConstants.OCASSION_ID, ocassionId);
            intent.putExtra(ApplicationConstants.LOCATION, location);
            activity.startActivity(intent);

        });

    }

    private void setVendorImage(final ImageView imageView, String image) {

        if (activity == null) {
            return;
        }

        if (image != null && image.isEmpty()) {
            image = null;
        }

        ImageHandling.loadRemoteImage(image, imageView, R.drawable.ic_vm_new, R.drawable.ic_vm_new, activity);
    }

    public void updateVendorList(ArrayList<Vendor> vendors, long occasionId) {
        this.vendors = vendors;
        setOccasionId(occasionId);
        notifyDataSetChanged();
    }
    public void setOccasionId(long occasionId) {
        this.ocassionId = occasionId;
        ocassion = ((MainApplication) activity.getApplication()).getOcassionForId(ocassionId);
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }
}
