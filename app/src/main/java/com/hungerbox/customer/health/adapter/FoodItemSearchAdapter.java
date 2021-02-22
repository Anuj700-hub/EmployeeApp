package com.hungerbox.customer.health.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.health.viewholder.FoodHealthDetailViewHolder;
import com.hungerbox.customer.model.FoodNutritionInfo;

import java.util.ArrayList;

/**
 * Created by manas on 4/12/17.
 */
public class FoodItemSearchAdapter extends RecyclerView.Adapter<FoodHealthDetailViewHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    ArrayList<FoodNutritionInfo> foodItemDataArrayList = new ArrayList<>();

    FoodItemListener mListener;

    public FoodItemSearchAdapter(Activity activty, ArrayList<FoodNutritionInfo> foodItemDataArrayList, FoodItemListener listener) {
        this.activity = activty;
        this.foodItemDataArrayList = foodItemDataArrayList;
        layoutInflater = LayoutInflater.from(activty);
        mListener = listener;

    }

    @Override
    public FoodHealthDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoodHealthDetailViewHolder(layoutInflater.inflate(R.layout.food_health_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FoodHealthDetailViewHolder holder, final int position) {
        final FoodNutritionInfo foodItemData = foodItemDataArrayList.get(position);
        holder.tvItemName.setText(foodItemData.getName());
        holder.tvCal.setText(foodItemData.getCalorie() + " cals");
        holder.tvPack.setText("(Per " + foodItemData.getMeasureName() + ")");
        holder.tvQuantity.setText(foodItemData.getQuantity() + "");

        holder.tvAddItem.setSelected(foodItemData.isAdded());
        if (foodItemData.isAdded()) {
            holder.tvAddItem.setText("ADDED");
            holder.ivIncQty.setVisibility(View.GONE);
            holder.ivDecQty.setVisibility(View.GONE);
        } else {
            holder.tvAddItem.setText("ADD");
            holder.ivIncQty.setVisibility(View.VISIBLE);
            holder.ivDecQty.setVisibility(View.VISIBLE);
        }
        holder.tvAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null && !foodItemData.isAdded()) {
                    mListener.onFoodItemSelected(position, foodItemData.getNutritionId(), foodItemData.getName(), foodItemData.getQuantity());
                    foodItemData.setAdded(true);
                    notifyItemChanged(position);
                }
            }
        });
        holder.ivIncQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodItemDataArrayList.get(position).setQuantity(foodItemData.getQuantity() + 1);
                foodItemData.setAdded(false);
                notifyItemChanged(position);
            }
        });
        holder.ivDecQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foodItemData.getQuantity() > 1) {
                    foodItemDataArrayList.get(position).setQuantity(foodItemData.getQuantity() - 1);
                    foodItemData.setAdded(false);
                }
                notifyItemChanged(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return foodItemDataArrayList.size();
    }

    public interface FoodItemListener {
        void onFoodItemSelected(int position, long nutritionId, String name, int quantity);
    }

}
