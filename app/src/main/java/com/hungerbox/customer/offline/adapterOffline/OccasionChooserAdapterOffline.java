package com.hungerbox.customer.offline.adapterOffline;

import android.app.Activity;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.offline.MainApplicationOffline;
import com.hungerbox.customer.offline.activityOffline.GlobalActivityOffline;
import com.hungerbox.customer.offline.adapterOffline.viewHolderOffline.OccasionViewHolderOffline;
import com.hungerbox.customer.offline.modelOffline.OcassionOffline;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.ImageHandling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OccasionChooserAdapterOffline extends RecyclerView.Adapter<OccasionViewHolderOffline> {

    private final GlobalActivityOffline.OccasionChangeInterface occasionChangeInterface;
    Activity activity;
    LayoutInflater inflater;
    ArrayList<OcassionOffline> ocassionArrayList;
    long selectedOccasion;

    public OccasionChooserAdapterOffline(Activity activity, ArrayList<OcassionOffline> ocassionArrayList,
                                  long selectedOccasion, GlobalActivityOffline.OccasionChangeInterface occasionChangeInterface) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.ocassionArrayList = ocassionArrayList;
        this.selectedOccasion = selectedOccasion;
        this.occasionChangeInterface = occasionChangeInterface;
    }

    @Override
    public OccasionViewHolderOffline onCreateViewHolder(ViewGroup parent, int viewType) {

        return new OccasionViewHolderOffline(inflater.inflate(R.layout.occasion_list_item_offline, parent, false));

    }

    @Override
    public void onBindViewHolder(OccasionViewHolderOffline holder, final int position) {

        final OcassionOffline occassion = ocassionArrayList.get(position);
        if (AppUtils.getConfig(activity).isHide_timings())
            holder.tvOccasion.setText(occassion.getName());
        else
            holder.tvOccasion.setText(occassion.toString());

        if (occassion.id == selectedOccasion) {
            holder.tvOccasion.setSelected(true);

            ImageHandling.loadLocalImage(activity, holder.ivOccasion, getImageString(occassion.name, true));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.colorPrimary, null));
            } else {
                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            }
        } else {
            holder.tvOccasion.setSelected(false);
            ImageHandling.loadLocalImage(activity, holder.ivOccasion, getImageString(occassion.name, false));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.warm_grey, null));
            } else {
                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.warm_grey));
            }
        }

        holder.rlOccasion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (calendar.getTimeInMillis() <
                        DateTimeUtil.getTodaysTimeFromString(occassion.endTime)) {

//                    try{
//                        HashMap<String,Object> map = new HashMap<>();
//                        String privious_occassion_name = "";
//                        for(OcassionOffline occ : ocassionArrayList){
//                            if(occ.id == selectedOccasion){
//                                privious_occassion_name = occ.name;
//                            }
//                        }
//
//                        map.put(CleverTapEvent.PropertiesNames.getPrevious_ocassion(),privious_occassion_name);
//                        map.put(CleverTapEvent.PropertiesNames.getNew_occasion(),occassion.getName());
//                        map.put(CleverTapEvent.PropertiesNames.is_change(),selectedOccasion != occassion.id);
//                        CleverTapEvent.pushUserEvents(CleverTapEvent.EventNames.getOcassion_change(),map,activity);
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }

                    if (MainApplicationOffline.isCartCreated() && MainApplicationOffline.selectedOcassionId != occassion.id) {
                        occasionChangeInterface.onOccasionChanged(-1, v);
                    } else {
                        selectedOccasion = occassion.id;
                        occasionChangeInterface.onOccasionChanged(position, null);
                        notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private int getImageString(String oName, boolean isSelected) {
        if (oName.toLowerCase().contains("breakfast")) {
            if (isSelected)
                return R.drawable.breakfast_active;
            else
                return R.drawable.breakfast_inactive;
        } else if (oName.toLowerCase().contains("lunch")) {
            if (isSelected)
                return R.drawable.lunch_active;
            else
                return R.drawable.lunch_inactive;
        } else if (oName.toLowerCase().contains("dinner")) {
            if (isSelected)
                return R.drawable.breakfast_active;
            else
                return R.drawable.breakfast_inactive;
        } else {
            if (isSelected)
                return R.drawable.lunch_active;
            else
                return R.drawable.lunch_inactive;
        }

    }


    @Override
    public int getItemCount() {
        return ocassionArrayList.size();
    }

    public void setSelectedOccasion(long newSelectedOccasion) {
        this.selectedOccasion = newSelectedOccasion;
    }

    public long getSelectedOccassion() {
        return selectedOccasion;
    }

    public ArrayList<OcassionOffline> getOccassionResponse() {
        return ocassionArrayList;
    }

    public void setOcassionArrayList(ArrayList<OcassionOffline> ocassionArrayList) {
        this.ocassionArrayList = ocassionArrayList;
    }
}
