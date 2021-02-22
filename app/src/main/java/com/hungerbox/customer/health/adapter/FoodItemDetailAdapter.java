package com.hungerbox.customer.health.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.health.viewholder.FoodItemDetailViewHolder;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FoodItemData;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by peeyush on 22/6/16.
 */
public class FoodItemDetailAdapter extends RecyclerView.Adapter<FoodItemDetailViewHolder> {

    Activity activity;
    LayoutInflater layoutInflater;
    ArrayList<FoodItemData> foodItemDataArrayList;
    ProgressBar pb;
    HealthHistoryAdapter.FoodItemUpdate foodItemUpdate;

    public FoodItemDetailAdapter(Activity activty, ArrayList<FoodItemData> foodItemDataArrayList, ProgressBar pb, HealthHistoryAdapter.FoodItemUpdate foodItemUpdate) {
        this.activity = activty;
        this.foodItemDataArrayList = foodItemDataArrayList;
        layoutInflater = LayoutInflater.from(activty);
        this.pb = pb;
        this.foodItemUpdate = foodItemUpdate;
    }


    @Override
    public FoodItemDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoodItemDetailViewHolder(layoutInflater.inflate(R.layout.food_detail_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FoodItemDetailViewHolder holder, final int position) {
        final FoodItemData foodItemData = foodItemDataArrayList.get(position);

        holder.dishName.setText(foodItemData.getName());
        holder.dishCalories.setText((int) foodItemData.getCalorie() + " cal");
        holder.dishQty.setText(foodItemData.getQuantity() + "");
        holder.dishAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCalorieEaten(foodItemData.getCalorie_intake_id(), foodItemData.getQuantity() + 1);
            }
        });
        holder.dishRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCalorieEaten(foodItemData.getCalorie_intake_id(), foodItemData.getQuantity() - 1);
            }
        });
        holder.dishDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCalorieEaten(foodItemData.getCalorie_intake_id(), 0);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodItemDataArrayList.size();
    }

    private void updateCalorieEaten(long nutritionId, int quantity) {
        pb.setVisibility(View.VISIBLE);
        String url = UrlConstant.CALORIE_UPDATE;
        SimpleHttpAgent<FoodUpdateInfo> calorieEated = new SimpleHttpAgent<FoodUpdateInfo>(activity,
                url, new ResponseListener<FoodUpdateInfo>() {
            @Override
            public void response(FoodUpdateInfo responseObject) {
                pb.setVisibility(View.GONE);
                foodItemUpdate.updateFoodItem();
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                pb.setVisibility(View.GONE);
                AppUtils.showToast("Some error occured.", false, 0);
            }
        }, FoodUpdateInfo.class);

        FoodUpdateInfo foodUpdateInfo = new FoodUpdateInfo();
        foodUpdateInfo.setId(nutritionId);
        foodUpdateInfo.setQuantity(quantity);

        calorieEated.post(foodUpdateInfo, new HashMap<String, JsonSerializer>());

    }
}
