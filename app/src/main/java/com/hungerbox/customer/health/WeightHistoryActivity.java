package com.hungerbox.customer.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.health.adapter.WeightHistoryAdapter;
import com.hungerbox.customer.model.DistinctCalorieDataResponse;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;

public class WeightHistoryActivity extends ParentActivity {

    TextView tvCurWeight;
    Button tvUpdateHealth;
    ImageView ivBack;
    RecyclerView rvWeight;
    private WeightHistoryAdapter weightHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);
        tvCurWeight = findViewById(R.id.tv_cur_weight);
        tvUpdateHealth = findViewById(R.id.btn_update_health);
        ivBack = findViewById(R.id.iv_back);
        rvWeight = findViewById(R.id.rv_weight);

        tvUpdateHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeightHistoryActivity.this, HealthDetailsActivity.class);
                startActivity(intent);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getWeightHistory();
    }

    private void getWeightHistory() {
        String url = UrlConstant.WEIGHT_TRACKER;
        final SimpleHttpAgent<DistinctCalorieDataResponse> weightHistoryAgent = new SimpleHttpAgent<DistinctCalorieDataResponse>(WeightHistoryActivity.this, url, new ResponseListener<DistinctCalorieDataResponse>() {
            @Override
            public void response(DistinctCalorieDataResponse responseObject) {
                if (responseObject != null || responseObject.getCalorieData() != null) {
                    tvCurWeight.setText("Current Weight: " + responseObject.getCalorieData().get(0).getWeight() + "kgs");
                    weightHistoryAdapter = new WeightHistoryAdapter(WeightHistoryActivity.this,
                            responseObject.getCalorieData(), new WeightHistoryAdapter.FoodItemListener() {
                        @Override
                        public void onFoodItemSelected(int position, long slotId) {

                        }
                    });
                    rvWeight.setLayoutManager(new LinearLayoutManager(WeightHistoryActivity.this
                            , LinearLayoutManager.VERTICAL, false));
                    rvWeight.setAdapter(weightHistoryAdapter);
                }
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

            }
        }, DistinctCalorieDataResponse.class);

        weightHistoryAgent.get();

    }
}
