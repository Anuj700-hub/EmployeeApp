package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.navmenu.fragment.OnItemSelectedListener;
import com.hungerbox.customer.order.adapter.viewholder.LocationViewHolder;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.ImageHandling;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;

/**
 * Created by manas on 17/11/16.
 */

public class LocationChooserAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    public long selectedLocation;
    Activity activity;
    LayoutInflater inflater;
    ArrayList<Location> locationArrayList;
    OnItemSelectedListener itemSelectedListener;

    public LocationChooserAdapter(Activity activity, ArrayList<Location> locationArrayList,
                                  @NonNull OnItemSelectedListener listener, long selectedLocation) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.locationArrayList = locationArrayList;
        this.itemSelectedListener = listener;
        this.selectedLocation = selectedLocation;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new LocationViewHolder(inflater.inflate(R.layout.location_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, final int position) {

        Location location = locationArrayList.get(position);
        holder.tvLocationName.setText(location.toString());
        if (location.id == selectedLocation) {
            holder.cbLocation.setChecked(true);
        } else
            holder.cbLocation.setChecked(false);

        holder.cbLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelectedListener.onItemSelectedListener(position);
            }
        });

        long defaultLocationId = SharedPrefUtil.getLong(ApplicationConstants.DEFAULT_LOCATION_ID, 0);
        if (defaultLocationId != 0 && defaultLocationId == location.id) {
            holder.isDefault.setVisibility(View.VISIBLE);
        } else {
            holder.isDefault.setVisibility(View.GONE);
        }
//        createImageViews(getLocationCapacityCount(location), holder.llDensity, activity);
//        holder.llDensity.setVisibility(View.VISIBLE);
    }

    private int getLocationCapacityCount(Location location) {
        double capacity = location.getCapacity();
        capacity = (capacity > 100) ? 100 : capacity;
        double count = 5.0 * capacity / 100.0;


        return (int) Math.ceil(count);
    }


    @Override
    public int getItemCount() {
        return locationArrayList.size();
    }

    public void setSelectedLocation(long locationId) {
        selectedLocation = locationId;
        notifyDataSetChanged();
    }

    public void createImageViews(int count, LinearLayout parentView, Context context) {
        parentView.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    50, ViewGroup.LayoutParams.MATCH_PARENT
            ));

            ImageHandling.loadLocalImage(activity, imageView, R.drawable.density_active);
            parentView.addView(imageView);
        }
        for (int i = 0; i < (5 - count); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    50, ViewGroup.LayoutParams.MATCH_PARENT
            ));
            ImageHandling.loadLocalImage(activity, imageView, R.drawable.density_inactive);

            parentView.addView(imageView);
        }
    }

    public void updateLocations(ArrayList<Location> locations) {
        locationArrayList = locations;
    }
}
