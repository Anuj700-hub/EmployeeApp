package com.hungerbox.customer.bluetooth;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.bluetooth.Model.ViolationByDay;

import java.util.ArrayList;

class BluetoothViolationAdapter extends RecyclerView.Adapter<BluetoothViolationViewHolder> {

    Activity activity;
    LayoutInflater inflater;
    ArrayList<ViolationByDay> violationList;

    public BluetoothViolationAdapter(Activity activity, ArrayList<ViolationByDay> violationByDays) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.violationList = violationByDays;
    }

    @NonNull
    @Override
    public BluetoothViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BluetoothViolationViewHolder(inflater.inflate(R.layout.violation_by_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViolationViewHolder holder, int position) {
        ViolationByDay violationByDay = violationList.get(position);
        holder.tv_total_violations.setText(violationByDay.getTotalViolations() + "");
        holder.tv_user_percentage.setText(violationByDay.getBetterThanUserPercentage() + "% Users");
        holder.tv_day.setText(violationByDay.getViolationDayText());
        holder.tv_user_percentage.setTextColor(ContextCompat.getColor(activity, violationByDay.getUserViolationRangeColor()));
        holder.violation_text.setOnClickListener(null);
        holder.violation_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.rv_violation_hour.getVisibility() == View.VISIBLE) {
                    holder.violation_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down_arrow, 0);
                    holder.rv_violation_hour.setVisibility(View.GONE);
                } else {
                    holder.violation_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_up_arrow, 0);
                    holder.rv_violation_hour.setVisibility(View.VISIBLE);
                    setViolationByHourAdapter(holder, violationByDay);
                }
            }
        });


    }

    private void setViolationByHourAdapter(BluetoothViolationViewHolder holder, ViolationByDay violationByDay) {
        ViolationByHourAdapter violationByHourAdapter = new ViolationByHourAdapter(activity, violationByDay.getViolationByHours());
        holder.rv_violation_hour.setLayoutManager(new LinearLayoutManager(activity));
        holder.rv_violation_hour.setAdapter(violationByHourAdapter);
        violationByHourAdapter.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return violationList.size();
    }
}
