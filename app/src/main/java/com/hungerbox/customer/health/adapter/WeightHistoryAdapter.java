package com.hungerbox.customer.health.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.health.viewholder.WeightHistoryViewHolder;
import com.hungerbox.customer.model.CalorieData;

import java.util.ArrayList;

/**
 * Created by peeyush on 22/6/16.
 */
public class WeightHistoryAdapter extends RecyclerView.Adapter<WeightHistoryViewHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    ArrayList<CalorieData> calorieDataArrayList = new ArrayList<>();

    FoodItemListener mListener;

    public WeightHistoryAdapter(Activity activty, ArrayList<CalorieData> calorieDataArrayList, FoodItemListener listener) {
        this.activity = activty;
        this.calorieDataArrayList = calorieDataArrayList;
        layoutInflater = LayoutInflater.from(activty);
        mListener = listener;
    }

    @Override
    public WeightHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeightHistoryViewHolder(layoutInflater.inflate(R.layout.weight_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WeightHistoryViewHolder holder, final int position) {
        final CalorieData calorieData = calorieDataArrayList.get(position);
        holder.tvDate.setText(calorieData.getDate());
        holder.tvWeight.setText("" + calorieData.getWeight());

    }

    @Override
    public int getItemCount() {
        return calorieDataArrayList.size();
    }

    public interface FoodItemListener {
        void onFoodItemSelected(int position, long slotId);
    }

}
