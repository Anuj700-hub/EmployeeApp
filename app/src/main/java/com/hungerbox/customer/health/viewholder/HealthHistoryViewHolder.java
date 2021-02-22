package com.hungerbox.customer.health.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;

/**
 * Created by manas dadheech on 12/7/17.
 */

public class HealthHistoryViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMaxCalConsumed, tvCalConsumed, link, tvDate, burnText, stepsTaken, viewDishes, addDishes;
    public ProgressBar pbCalsConsumedLessConsumed, pbCalsConsumedOverConsumed, stepsProgressBar, pb;
    public RelativeLayout connectDeviceBox;
    public View dishBox;
    public RecyclerView dishRecycleView;

    public HealthHistoryViewHolder(View itemView) {
        super(itemView);
        tvDate = itemView.findViewById(R.id.tv_health_date);
        link = itemView.findViewById(R.id.link);
        tvCalConsumed = itemView.findViewById(R.id.tv_cals_consumed_start_label);
        tvMaxCalConsumed = itemView.findViewById(R.id.tv_cals_consumed_end_label);
        pbCalsConsumedLessConsumed = itemView.findViewById(R.id.pb_cals_consumed_less_consume);
        pbCalsConsumedOverConsumed = itemView.findViewById(R.id.pb_cals_consumed_over_consume);
        connectDeviceBox = itemView.findViewById(R.id.connectDeviceBox);
        burnText = itemView.findViewById(R.id.burnText);
        stepsTaken = itemView.findViewById(R.id.stepsTaken);
        stepsProgressBar = itemView.findViewById(R.id.progressBar);
        viewDishes = itemView.findViewById(R.id.viewDishes);
        dishBox = itemView.findViewById(R.id.dishBox);
        dishRecycleView = itemView.findViewById(R.id.dishRecycle);
        pb = itemView.findViewById(R.id.pb);
        addDishes = itemView.findViewById(R.id.addDishes);
    }
}
