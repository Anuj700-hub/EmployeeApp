package com.hungerbox.customer.bluetooth;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.Model.ViolationByHour;

import java.util.ArrayList;

class ViolationByHourAdapter extends RecyclerView.Adapter<ViolationByHourViewHolder> {

    Activity activity;
    ArrayList<ViolationByHour> violationByHours;
    LayoutInflater inflater;

    public ViolationByHourAdapter(Activity activity, ArrayList<ViolationByHour> violationByHours) {
        this.activity = activity;
        this.violationByHours = violationByHours;
        this.inflater = LayoutInflater.from(activity);

    }

    @NonNull
    @Override
    public ViolationByHourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViolationByHourViewHolder(inflater.inflate(R.layout.violation_by_hour, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationByHourViewHolder holder, int position) {

        ViolationByHour violationByHour = violationByHours.get(position);
        holder.tv_time_range.setText(violationByHour.getTimeRange());
        holder.tv_violation_count.setText(violationByHour.getViolationsCount()+" Violations");

    }

    @Override
    public int getItemCount() {
        return violationByHours.size();
    }
}
