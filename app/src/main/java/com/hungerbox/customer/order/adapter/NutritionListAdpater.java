package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Nutrition;
import com.hungerbox.customer.model.NutritionItem;
import com.hungerbox.customer.order.adapter.viewholder.NutritionItemViewHolder;


/**
 * Created by sandipanmitra on 12/4/17.
 */

public class NutritionListAdpater extends RecyclerView.Adapter<NutritionItemViewHolder> {


    Nutrition nutrition;
    Activity activity;
    LayoutInflater inflater;
    int count = 1;

    public NutritionListAdpater(Activity activity, Nutrition nutrition, int count) {
        this.nutrition = nutrition;
        this.activity = activity;
        this.count = count;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public NutritionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrition_list_item, parent, false);
        rootView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        return new NutritionItemViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(NutritionItemViewHolder holder, int position) {
        NutritionItem nutritionItem = nutrition.getNutritionItems().get(position);

        holder.tvProteins.setText(String.format("%.2f gm", (nutritionItem.getProtein() * count)));
        holder.tvCarbs.setText(String.format("%.2f gm", (nutritionItem.getCarbs() * count)));
        holder.tvFats.setText(String.format("%.2f gm", (nutritionItem.getFats() * count)));
        holder.tvFibre.setText(String.format("%.2f gm", (nutritionItem.getFibre() * count)));

        holder.tvName.setText(nutritionItem.getName());

    }

    @Override
    public int getItemCount() {
        return nutrition.getNutritionItems().size();
    }
}
