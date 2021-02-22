package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Ocassion;
import com.hungerbox.customer.order.activity.GlobalActivity;
import com.hungerbox.customer.order.adapter.viewholder.OccasionViewHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.CleverTapEvent;
import com.hungerbox.customer.util.DateTimeUtil;
import com.hungerbox.customer.util.ImageHandling;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by manas on 17/11/16.
 */

public class OccasionChooserAdapter extends RecyclerView.Adapter<OccasionViewHolder> {

    private final GlobalActivity.OccasionChangeInterface occasionChangeInterface;
    Activity activity;
    LayoutInflater inflater;
    ArrayList<Ocassion> ocassionArrayList;
    long selectedOccasion;

    public OccasionChooserAdapter(Activity activity, ArrayList<Ocassion> ocassionArrayList,
                                  long selectedOccasion, GlobalActivity.OccasionChangeInterface occasionChangeInterface) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.ocassionArrayList = ocassionArrayList;
        this.selectedOccasion = selectedOccasion;
        this.occasionChangeInterface = occasionChangeInterface;
    }

    @Override
    public OccasionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new OccasionViewHolder(inflater.inflate(R.layout.occasion_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(OccasionViewHolder holder, final int position) {

        final Ocassion occassion = ocassionArrayList.get(position);
        if (AppUtils.getConfig(activity).isHide_timings()) {
            holder.tvOccasionTime.setVisibility(View.GONE);
            holder.tvOccasion.setText(occassion.getName());
        }
        else {
            int indexOfn = -1;
            if (occassion.toString().contains("\n")){
                indexOfn = occassion.toString().indexOf("\n");
            }
            holder.tvOccasionTime.setVisibility(View.VISIBLE);
            holder.tvOccasionTime.setText(indexOfn==-1?"":occassion.toString().substring(indexOfn+1));
            holder.tvOccasion.setText(occassion.getName());
        }

        if (occassion.id == selectedOccasion) {
            holder.tvOccasion.setSelected(true);

            ImageHandling.loadLocalImage(activity, holder.ivOccasion, getImageString(occassion.name, true));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.colorPrimary, null));
//            } else {
//                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
//            }
        } else {
            holder.tvOccasion.setSelected(false);

            ImageHandling.loadLocalImage(activity, holder.ivOccasion, getImageString(occassion.name, false));

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.warm_grey, null));
//            } else {
//                holder.tvOccasion.setTextColor(activity.getResources().getColor(R.color.warm_grey));
//            }
        }

        holder.rlOccasion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = DateTimeUtil.adjustCalender(activity);
                if (calendar.getTimeInMillis() <
                        DateTimeUtil.getTodaysTimeFromString(occassion.endTime)) {

//                    try{
//                        HashMap<String,Object> map = new HashMap<>();
//                        String privious_occassion_name = "";
//                        for(Ocassion occ : ocassionArrayList){
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

                    if (MainApplication.isCartCreated() && MainApplication.selectedOcassionId != occassion.id) {
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
//        return isSelected?R.drawable.occasion_active:R.drawable.occasion_inactive;
        if (oName.toLowerCase().contains("breakfast")) {
            if (isSelected)
                return R.drawable.ic_breakfast_active;
            else
                return R.drawable.ic_breakfast_inactive;
        } else if (oName.toLowerCase().contains("lunch")) {
            if (isSelected)
                return R.drawable.ic_lunch_active;
            else
                return R.drawable.ic_lunch_inactive;
        } else if (oName.toLowerCase().contains("dinner")) {
            if (isSelected)
                return R.drawable.ic_dinner_active;
            else
                return R.drawable.ic_dinner_inactive;
        } else if (oName.toLowerCase().contains("evening snacks")) {
            if (isSelected)
                return R.drawable.ic_snacks_active;
            else
                return R.drawable.ic_snacks_inactive;
        }else if (oName.toLowerCase().contains("snacks")) {
            if (isSelected)
                return R.drawable.ic_snacks_active;
            else
                return R.drawable.ic_snacks_inactive;
        }else {
            if (isSelected)
                return R.drawable.ic_snacks_active;
            else
                return R.drawable.ic_snacks_inactive;
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

    public ArrayList<Ocassion> getOccassionResponse() {
        return ocassionArrayList;
    }

    public void setOcassionArrayList(ArrayList<Ocassion> ocassionArrayList) {
        this.ocassionArrayList = ocassionArrayList;
    }
}
