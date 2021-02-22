package com.hungerbox.customer.health;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.health.adapter.FoodItemSearchAdapter;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.FoodNutritionInfo;
import com.hungerbox.customer.model.HealthSearchResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AddHealthItemActivity extends ParentActivity {

    FoodItemSearchAdapter foodItemSearchAdapter;
    private ImageView ivBack;
    private EditText etSearch;
    private RecyclerView rvSearch;
    private ArrayList<FoodNutritionInfo> foodItemList = new ArrayList<FoodNutritionInfo>();
    private ArrayList<FoodNutritionInfo> addedItemList = new ArrayList<FoodNutritionInfo>();
    private TextView ivNoItems;
    private String timestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_health_item);

        timestamp = getIntent().getStringExtra("timestamp");
        ivBack = findViewById(R.id.iv_back);
        etSearch = findViewById(R.id.et_search);
        rvSearch = findViewById(R.id.rv__health_search);
        ivNoItems = findViewById(R.id.iv_no_item);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        foodItemSearchAdapter = new FoodItemSearchAdapter(AddHealthItemActivity.this,
                foodItemList, new FoodItemSearchAdapter.FoodItemListener() {
            @Override
            public void onFoodItemSelected(int position, long nutritionId, String name, int quantity) {
                addCalorieEaten(nutritionId, name, quantity);
            }

        });
        rvSearch.setLayoutManager(new LinearLayoutManager(AddHealthItemActivity.this
                , LinearLayoutManager.VERTICAL, false));
        rvSearch.setAdapter(foodItemSearchAdapter);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    getSearchData(s.toString());
                } else {
                    foodItemList.clear();
                    foodItemSearchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void addCalorieEaten(long nutritionId, final String name, int quantity) {
        String url = UrlConstant.CALORIE_EATEN;
        SimpleHttpAgent<FoodNutritionInfo> calorieEated = new SimpleHttpAgent<FoodNutritionInfo>(AddHealthItemActivity.this,
                url, new ResponseListener<FoodNutritionInfo>() {
            @Override
            public void response(FoodNutritionInfo responseObject) {
                AppUtils.showToast(name + "Added", true,    1);
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        }, FoodNutritionInfo.class);

        FoodNutritionInfo foodNutritionInfo = new FoodNutritionInfo();
        foodNutritionInfo.setNutritionId(nutritionId);
        foodNutritionInfo.setDate(timestamp);
        foodNutritionInfo.setQuantity(quantity);

        calorieEated.post(foodNutritionInfo, new HashMap<String, JsonSerializer>());

    }

    private void getSearchData(String s) {
        String url = UrlConstant.SEARCH_ITEM + s;

        SimpleHttpAgent<HealthSearchResponse> foodSearchAgent = new SimpleHttpAgent<HealthSearchResponse>(AddHealthItemActivity.this, url, new ResponseListener<HealthSearchResponse>() {
            @Override
            public void response(HealthSearchResponse responseObject) {

                if (responseObject != null) {
                    if (responseObject.getSearchResponse().size() > 0) {
                        foodItemList.clear();
                        foodItemList.addAll(responseObject.getSearchResponse());
                        foodItemSearchAdapter.notifyDataSetChanged();
                        ivNoItems.setVisibility(View.GONE);

                    } else {
                        foodItemList.clear();
                        foodItemSearchAdapter.notifyDataSetChanged();
                        ivNoItems.setVisibility(View.VISIBLE);
                    }
                }

            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                if (errorCode == ContextErrorListener.NO_NET_CONNECTION) {
                    AppUtils.showToast("Please check your network.", true,    0);
                } else {
                    AppUtils.showToast(error, true,    0);
                }
            }
        }, HealthSearchResponse.class);

        foodSearchAgent.get();
    }
}
