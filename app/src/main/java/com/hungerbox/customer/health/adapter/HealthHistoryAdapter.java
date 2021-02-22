package com.hungerbox.customer.health.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.health.AddHealthItemActivity;
import com.hungerbox.customer.health.viewholder.HealthHistoryViewHolder;
import com.hungerbox.customer.model.CalorieData;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FoodItemData;
import com.hungerbox.customer.model.FoodItemDataResponse;
import com.hungerbox.customer.navmenu.PaginationHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.EventUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by peeyush on 22/6/16.
 */
public class HealthHistoryAdapter extends RecyclerView.Adapter<HealthHistoryViewHolder> {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private ArrayList<CalorieData> calorieDataArrayList;
    private PaginationHandler PaginationHandler;
    private boolean deviceLinked;

    public HealthHistoryAdapter(Activity activty, ArrayList<CalorieData> foodItemDataArrayList, PaginationHandler paginationHandler, boolean deviceLinked) {
        this.activity = activty;
        this.calorieDataArrayList = foodItemDataArrayList;
        layoutInflater = LayoutInflater.from(activty);
        this.PaginationHandler = paginationHandler;
        this.deviceLinked = deviceLinked;
    }

    @Override
    public HealthHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HealthHistoryViewHolder(layoutInflater.inflate(R.layout.health_history_item_new, parent, false));
    }

    @Override
    public void onBindViewHolder(final HealthHistoryViewHolder holder, final int position) {

        if (position == calorieDataArrayList.size() - 1 && PaginationHandler != null)
            PaginationHandler.onLastItemReached();

        final CalorieData calorieData = calorieDataArrayList.get(position);

        Typeface mTypeFace = Typeface.createFromAsset(activity.getAssets(),
                "healthdate.ttf");

        holder.tvDate.setTypeface(mTypeFace);

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(calorieData.getDate());
            if (DateUtils.isToday(date.getTime())) {
                holder.tvDate.setText("TODAY");
            } else {
                holder.tvDate.setText(calorieData.getDate().split("-")[1] + "-" + calorieData.getDate().split("-")[2]);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        holder.tvCalConsumed.setText((int) calorieData.getCalorieIntake() + "");
        holder.tvMaxCalConsumed.setText("/" + (int) calorieData.getIdealCalorieIntake() + "");

        if ((int) calorieData.getIdealCalorieIntake() >= (int) calorieData.getCalorieIntake()) {
            holder.pbCalsConsumedLessConsumed.setMax((int) calorieData.getIdealCalorieIntake());
            holder.pbCalsConsumedLessConsumed.setProgress((int) calorieData.getCalorieIntake());
            holder.pbCalsConsumedLessConsumed.setVisibility(View.VISIBLE);
            holder.pbCalsConsumedOverConsumed.setVisibility(View.GONE);
        } else {
            holder.pbCalsConsumedOverConsumed.setMax((int) calorieData.getCalorieIntake());
            holder.pbCalsConsumedOverConsumed.setProgress((int) calorieData.getIdealCalorieIntake());
            holder.pbCalsConsumedLessConsumed.setVisibility(View.GONE);
            holder.pbCalsConsumedOverConsumed.setVisibility(View.VISIBLE);
        }

        if (deviceLinked) {
            holder.connectDeviceBox.setVisibility(View.VISIBLE);
            holder.link.setVisibility(View.INVISIBLE);
            holder.stepsTaken.setText((int) calorieData.getSteps() + "");
            holder.burnText.setText("  Calories Burnt = ");

            Spannable word = new SpannableString((int) calorieData.getCalorieBurned() + " cal");
            word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.burnText.append(word);

            holder.stepsProgressBar.setProgress((int) calorieData.getSteps());

        } else {
            holder.connectDeviceBox.setVisibility(View.INVISIBLE);
            holder.link.setVisibility(View.VISIBLE);
        }

        if (calorieData.getFoodItemData() == null) {
            holder.dishBox.setVisibility(View.GONE);
            holder.viewDishes.setVisibility(View.VISIBLE);
        } else {
            holder.pb.setVisibility(View.GONE);
            holder.dishBox.setVisibility(View.VISIBLE);
            holder.viewDishes.setVisibility(View.GONE);
            setUpHealthList(calorieData, holder.dishRecycleView, holder);
        }

        holder.viewDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dishBox.setVisibility(View.VISIBLE);
                holder.viewDishes.setVisibility(View.GONE);

                getItemNutritionInfo(calorieData, holder.dishRecycleView, holder);
            }
        });

        holder.addDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timestamp = "";
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    timestamp = (sdf.parse(calorieData.getDate()).getTime() / 1000) + "";
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity, AddHealthItemActivity.class);
                intent.putExtra("timestamp", timestamp);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                try {
                    EventUtil.FbEventLog(activity, EventUtil.HEALTH_DISH_ADD, "");
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return calorieDataArrayList.size();
    }


    private void getItemNutritionInfo(final CalorieData caloriedata, final RecyclerView dishRecycleView, final HealthHistoryViewHolder holder) {

        String url = UrlConstant.ITEM_INTAKE + caloriedata.getDate();
        SimpleHttpAgent<FoodItemDataResponse> calorieDataResponseSimpleHttpAgent =
                new SimpleHttpAgent<FoodItemDataResponse>(activity,
                        url, new ResponseListener<FoodItemDataResponse>() {
                    @Override
                    public void response(FoodItemDataResponse responseObject) {
                        holder.pb.setVisibility(View.GONE);
                        if (responseObject == null) {
                            double updatedcal = 0.0;
                            double idealcal = caloriedata.getIdealCalorieIntake();
                            caloriedata.setCalorieIntake(updatedcal);
                            holder.tvCalConsumed.setText((int) updatedcal + "");

                            if ((int) idealcal >= (int) updatedcal) {
                                holder.pbCalsConsumedLessConsumed.setMax((int) idealcal);
                                holder.pbCalsConsumedLessConsumed.setProgress((int) updatedcal);
                                holder.pbCalsConsumedLessConsumed.setVisibility(View.VISIBLE);
                                holder.pbCalsConsumedOverConsumed.setVisibility(View.GONE);
                            } else {
                                holder.pbCalsConsumedOverConsumed.setMax((int) updatedcal);
                                holder.pbCalsConsumedOverConsumed.setProgress((int) idealcal);
                                holder.pbCalsConsumedLessConsumed.setVisibility(View.GONE);
                                holder.pbCalsConsumedOverConsumed.setVisibility(View.VISIBLE);
                            }
                            caloriedata.setFoodItemData(new ArrayList<FoodItemData>());
                            setUpHealthList(caloriedata, dishRecycleView, holder);
                        } else {
                            double updatedcal = 0.0;
                            for (FoodItemData fd : responseObject.getFoodItemData()) {
                                updatedcal = updatedcal + fd.getCalorie();
                            }
                            double idealcal = caloriedata.getIdealCalorieIntake();
                            caloriedata.setCalorieIntake(updatedcal);
                            holder.tvCalConsumed.setText((int) updatedcal + "");
                            if (idealcal >= updatedcal) {
                                holder.pbCalsConsumedLessConsumed.setMax((int) idealcal);
                                holder.pbCalsConsumedLessConsumed.setProgress((int) updatedcal);
                                holder.pbCalsConsumedLessConsumed.setVisibility(View.VISIBLE);
                                holder.pbCalsConsumedOverConsumed.setVisibility(View.GONE);
                            } else {
                                holder.pbCalsConsumedOverConsumed.setMax((int) updatedcal);
                                holder.pbCalsConsumedOverConsumed.setProgress((int) idealcal);
                                holder.pbCalsConsumedLessConsumed.setVisibility(View.GONE);
                                holder.pbCalsConsumedOverConsumed.setVisibility(View.VISIBLE);
                            }
                            caloriedata.setFoodItemData(responseObject.getFoodItemData());
                            setUpHealthList(caloriedata, dishRecycleView, holder);
                        }
                    }
                }, new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        holder.pb.setVisibility(View.GONE);
                    }
                }, FoodItemDataResponse.class);

        calorieDataResponseSimpleHttpAgent.get();

    }


    private void setUpHealthList(final CalorieData calorieData, final RecyclerView dishRecycleView, final HealthHistoryViewHolder holder) {

        FoodItemDetailAdapter foodItemDetailAdapter = new FoodItemDetailAdapter(activity,
                calorieData.getFoodItemData(), holder.pb, new FoodItemUpdate() {
            @Override
            public void updateFoodItem() {
                getItemNutritionInfo(calorieData, dishRecycleView, holder);
            }
        });
        dishRecycleView.setLayoutManager(new LinearLayoutManager(activity
                , LinearLayoutManager.VERTICAL, false));
        dishRecycleView.setAdapter(foodItemDetailAdapter);

    }

    public void updateLinked(boolean linked) {
        this.deviceLinked = linked;
    }

    interface FoodItemUpdate {

        void updateFoodItem();
    }
}
