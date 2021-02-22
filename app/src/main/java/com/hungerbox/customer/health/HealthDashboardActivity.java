package com.hungerbox.customer.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hungerbox.customer.R;
import com.hungerbox.customer.health.adapter.HealthHistoryAdapter;
import com.hungerbox.customer.model.CalorieData;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.ListCalorieDataResponse;
import com.hungerbox.customer.navmenu.PaginationHandler;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ImageHandling;

import java.util.ArrayList;

public class HealthDashboardActivity extends ParentActivity implements PaginationHandler {

    boolean pageOver, isLinked = false;
    private ImageView ivBack, ivProfile, healthBanner;
    private View tvNoData;
    private RecyclerView rvHealthHistory;
    private HealthHistoryAdapter healthHistoryAdapter;
    private int currentPage;
    private ArrayList<CalorieData> calorieDataList = new ArrayList<>();
    private ProgressBar loading;
    private int DETAILS_UPDATE = 1;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_dashboard);


        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getHealthHistory(currentPage);
            }
        });

        tvNoData = findViewById(R.id.tv_no_data);
        loading = findViewById(R.id.loading);

        healthBanner = findViewById(R.id.healthBanner);

        isLinked = getIntent().getBooleanExtra("linked", false);

        ivProfile = findViewById(R.id.iv_profile);
        ivBack = findViewById(R.id.iv_back);
        rvHealthHistory = findViewById(R.id.rv_health_history);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AppUtils.getHomeNavigationIntent(HealthDashboardActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HealthDashboardActivity.this, HealthDetailsActivity.class);
                intent.putExtra("isRegister", false);
                startActivityForResult(intent, DETAILS_UPDATE);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        try {
            if (AppUtils.getConfig(this).getHealthBannner().getImage() != null && !AppUtils.getConfig(this).getHealthBannner().getImage().equals("")) {
                ImageHandling.loadRemoteImage(AppUtils.getConfig(this).getHealthBannner().getImage(), healthBanner, -1,-1, getApplicationContext());
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPage = 1;
        getHealthHistory(currentPage);
    }

    private void getHealthHistory(final int currentPage) {

        tvNoData.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        String url = UrlConstant.HEALTH_HISTORY + "?page=" + currentPage;
        SimpleHttpAgent<ListCalorieDataResponse> calorieDataResponseSimpleHttpAgent =
                new SimpleHttpAgent<ListCalorieDataResponse>(HealthDashboardActivity.this,
                        url, new ResponseListener<ListCalorieDataResponse>() {
                    @Override
                    public void response(ListCalorieDataResponse responseObject) {
                        swipeRefresh.setRefreshing(false);
                        loading.setVisibility(View.GONE);

                        if (responseObject != null && responseObject.getCalorieData() != null) {
                            setUpHealthHistoryList(responseObject.getCalorieData(), currentPage);
                        } else {

                            rvHealthHistory.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }
                }, new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        swipeRefresh.setRefreshing(false);
                        loading.setVisibility(View.GONE);
                        rvHealthHistory.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                }, ListCalorieDataResponse.class);

        calorieDataResponseSimpleHttpAgent.get();

    }

    private void setUpHealthHistoryList(ArrayList<CalorieData> calorieData, int page) {

        if (page == 1) {
            calorieDataList.clear();
        }
        if (this.calorieDataList != null) {
            this.calorieDataList.addAll(calorieData);
        } else {
            this.calorieDataList = new ArrayList<>();
            this.calorieDataList.addAll(calorieData);
        }

        if (currentPage == page - 1) {
            currentPage = page;
        }

        pageOver = calorieData.size() < 10;

        if (rvHealthHistory.getAdapter() == null) {
            healthHistoryAdapter = new HealthHistoryAdapter(HealthDashboardActivity.this,
                    calorieDataList, this, isLinked);
            LinearLayoutManager linearlayoutmanager = new LinearLayoutManager(HealthDashboardActivity.this
                    , LinearLayoutManager.VERTICAL, false);
            rvHealthHistory.setLayoutManager(linearlayoutmanager);
            rvHealthHistory.setAdapter(healthHistoryAdapter);
        } else {
            if (rvHealthHistory.getAdapter() instanceof HealthHistoryAdapter) {
                healthHistoryAdapter.updateLinked(isLinked);
                healthHistoryAdapter.notifyDataSetChanged();
            }
        }

        if (calorieDataList.size() == 0) {
            rvHealthHistory.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            rvHealthHistory.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLastItemReached() {
        if (!pageOver)
            getHealthHistory(currentPage + 1);
        else {

            AppUtils.showToast("No more activities to show.", false, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            isLinked = data.getBooleanExtra("linked", false);
        }
    }

    @Override
    public void finish() {
        Intent intent = AppUtils.getHomeNavigationIntent(HealthDashboardActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        super.finish();
    }
}
