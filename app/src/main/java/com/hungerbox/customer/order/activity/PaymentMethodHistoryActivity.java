package com.hungerbox.customer.order.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.config.action.LogoutTask;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.WalletHistory;
import com.hungerbox.customer.model.WalletHistoryReponse;
import com.hungerbox.customer.navmenu.adapter.AdapterPaginationInterface;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.order.adapter.PmHistoryAdapter;
import com.hungerbox.customer.prelogin.activity.ParentActivity;

import java.util.ArrayList;

public class PaymentMethodHistoryActivity extends ParentActivity implements AdapterPaginationInterface {

    PmHistoryAdapter walletHistoryListAdapter;
    private ImageView ivBack;
    private TextView tvPaymentTitle, tvBalance;
    private RecyclerView rvPmHistory;
    private long selectedWalletId;
    private String selectedWalletName;
    private int walletPage;
    private double selectedWalletAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method_history);
        ivBack = findViewById(R.id.iv_back);
        tvPaymentTitle = findViewById(R.id.tv_payment_method_name);
        tvBalance = findViewById(R.id.tv_cur_balance);
        rvPmHistory = findViewById(R.id.rv_payment_history);
        rvPmHistory.setLayoutManager(new LinearLayoutManager(PaymentMethodHistoryActivity.this));
        selectedWalletId = getIntent().getLongExtra("selectedWalletId", 0);
        selectedWalletName = getIntent().getStringExtra("selectedWalletName");
        selectedWalletAmount = getIntent().getDoubleExtra("selectedWalletAmount", 0);
        tvPaymentTitle.setText(selectedWalletName);
        if (selectedWalletAmount != 0)
            tvBalance.setText("BALANCE : " + selectedWalletAmount);
        else
            tvBalance.setText("BALANCE : N/A");
        walletPage = 1;
        getWalletHistoryFromServer(walletPage);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        LogoutTask.updateTime();
    }

    @Override
    public void loadMore() {
        getWalletHistoryFromServer(++walletPage);
    }

    private void getWalletHistoryFromServer(final int page) {
        String url = UrlConstant.WALLET_HISTORY + page;
        if (selectedWalletId > 0)
            url = url + "?companyWalletId=" + selectedWalletId;

        SimpleHttpAgent<WalletHistoryReponse> walletHistoryReponseSimpleHttpAgent = new SimpleHttpAgent<WalletHistoryReponse>(
                this,
                url,
                new ResponseListener<WalletHistoryReponse>() {
                    @Override
                    public void response(WalletHistoryReponse responseObject) {
                        if (responseObject != null && responseObject.walletHistories != null) {
                            if (page == 0) {
                                updateWalletHistoryUi(responseObject.walletHistories, false);
                            } else {
                                updateWalletHistoryUi(responseObject.walletHistories, true);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                WalletHistoryReponse.class
        );
        walletHistoryReponseSimpleHttpAgent.get();
    }

    private void updateWalletHistoryUi(ArrayList<WalletHistory> walletHistories, boolean isPaginating) {
        if (walletHistoryListAdapter == null || rvPmHistory.getAdapter() == null) {
            walletHistoryListAdapter = new PmHistoryAdapter(this, walletHistories);
            rvPmHistory.setAdapter(walletHistoryListAdapter);
        } else if (isPaginating) {
            walletHistoryListAdapter.addWalletHistories(walletHistories);
            if (walletHistories.isEmpty())
                walletHistoryListAdapter.removeFooter();
        } else {
            walletHistoryListAdapter.changeWalletHistory(walletHistories);
            walletHistoryListAdapter.notifyDataSetChanged();
        }
    }

}
