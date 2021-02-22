package com.hungerbox.customer.navmenu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.contracts.AdapterPaginationInterface;
import com.hungerbox.customer.model.WalletFilterList;
import com.hungerbox.customer.model.WalletFilterListResponse;
import com.hungerbox.customer.model.WalletHistory;
import com.hungerbox.customer.model.WalletHistoryReponse;
import com.hungerbox.customer.navmenu.adapter.WalletHistoryListAdapter;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.ArrayList;

public class WalletHistoryActivity extends ParentActivity implements AdapterPaginationInterface {

    int walletPage = 1;
    boolean hasMore = true;
    boolean hasMoreArchived = true;
    boolean usingArchivedWallets = false;
    long companyId, selectedWalletId = 0;
    ImageView ivBack;
    Spinner spWallets;
    RecyclerView rvWalletDetail;
    RelativeLayout moreHistory;
    TextView olderHistoryText;
    ArrayList<WalletHistory> walletHistories = new ArrayList<>();

    WalletHistoryListAdapter walletHistoryListAdapter;
    private TextView ivNoTransactions;
    public RelativeLayout rlRecharge;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_history);

        rvWalletDetail = findViewById(R.id.rv_wallet_history);
        spWallets = findViewById(R.id.sp_wallets);
        ivBack = findViewById(R.id.iv_back);
        moreHistory = findViewById(R.id.view_more_wallet_history);
        olderHistoryText = findViewById(R.id.older_history_text);

        rvWalletDetail.setLayoutManager(new LinearLayoutManager(this));
        ivNoTransactions = findViewById(R.id.tv_no_transactions);
        rlRecharge = findViewById(R.id.rl_recharge);
        companyId = SharedPrefUtil.getLong(ApplicationConstants.COMPANY_ID, 0);


        SpannableString content = new SpannableString(olderHistoryText.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        olderHistoryText.setText(content);

        getWalletTypeList();

        olderHistoryText.setOnClickListener(v -> {
            moreHistory.setVisibility(View.GONE);
            usingArchivedWallets = true;
            walletPage = 1;
            getWalletHistoryFromServer(walletPage);
        });

        rlRecharge.setOnClickListener(view -> navigateToRechargeScreen());


        ivBack.setOnClickListener(view -> finish());

        if(getIntent().getBooleanExtra("rechargePopup", false)){
            rlRecharge.setVisibility(View.VISIBLE);
        }else{
            rlRecharge.setVisibility(View.INVISIBLE);
        }

    }

    private void navigateToRechargeScreen() {
        Intent intent = new Intent(this, RechargeActivity.class);
        intent.putExtra(EventUtil.MixpanelEvent.SubProperties.SOURCE, "Wallet History");
        startActivity(intent);
    }


    private void getWalletTypeList() {
        String url = UrlConstant.WALLET_LIST_FILTER;

        SimpleHttpAgent<WalletFilterListResponse> walletTypeResponseSimpleHttpAgent = new SimpleHttpAgent<>(
                this,
                url,
                responseObject -> {
                    if (responseObject != null) {
                        WalletFilterList walletType = new WalletFilterList();
                        walletType.setId(0);
                        walletType.setWalletDisplayName("All");
                        walletType.setWalletName("All");
                        responseObject.getWalletFilterLists().add(0, walletType);
                        updateWalletSpinner(responseObject.getWalletFilterLists());
                    }
                },
                (errorCode, error, errorResponse) -> {

                },
                WalletFilterListResponse.class
        );

        walletTypeResponseSimpleHttpAgent.get();
    }


    private void updateWalletSpinner(final ArrayList<WalletFilterList> walletTypes) {
        ArrayAdapter<WalletFilterList> walletsArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_custom, walletTypes);
        walletsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWallets.setAdapter(walletsArrayAdapter);
        spWallets.setSelection(0);

        spWallets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedWalletId = walletTypes.get(i).getId();
                walletPage = 1;
                hasMore = true;
                hasMoreArchived = true;
                usingArchivedWallets = false;
                walletHistories.clear();
                moreHistory.setVisibility(View.GONE);
                getWalletHistoryFromServer(walletPage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getWalletHistoryFromServer(final int page) {
        if (!hasMoreArchived) {
            removeLoader();
            return;
        }

        String url;

        if(usingArchivedWallets)
            url = UrlConstant.ARCHIVED_WALLET_HISTORY + page;
        else
            url = UrlConstant.WALLET_HISTORY + page;

        if (selectedWalletId > 0)
            url = url + "?companyWalletId=" + selectedWalletId;

        SimpleHttpAgent<WalletHistoryReponse> walletHistoryReponseSimpleHttpAgent = new SimpleHttpAgent<WalletHistoryReponse>(
                this,
                url,
                responseObject -> {
                    if (responseObject != null && responseObject.walletHistories != null) {
                        walletHistories.addAll(responseObject.getWalletHistories());

                        if(usingArchivedWallets && responseObject.getWalletHistories().size() < 20)
                            hasMoreArchived = false;
                        else if(!usingArchivedWallets && responseObject.getWalletHistories().size() < 20) {
                            hasMore = false;
                        }

                        updateWalletHistoryUi(walletHistories);
                    }
                },
                (errorCode, error, errorResponse) -> removeLoader(),
                WalletHistoryReponse.class
        );
        walletHistoryReponseSimpleHttpAgent.get();
    }

    private void removeLoader() {
        if (walletHistoryListAdapter != null) {
            walletHistoryListAdapter.removeFooter();
        }
    }


    private void manageNoTransactionUI() {
        if (walletHistories.size() <= 0 && usingArchivedWallets) {
            ivNoTransactions.setVisibility(View.VISIBLE);
        } else {
            ivNoTransactions.setVisibility(View.GONE);
        }
    }

    private void updateWalletHistoryUi(ArrayList<WalletHistory> walletHistories) {

        manageNoTransactionUI();

        if (walletHistoryListAdapter == null || rvWalletDetail.getAdapter() == null) {
            walletHistoryListAdapter = new WalletHistoryListAdapter(this, walletHistories);
            rvWalletDetail.setAdapter(walletHistoryListAdapter);

        } else {
            walletHistoryListAdapter.changeWalletHistory(walletHistories);
            walletHistoryListAdapter.notifyDataSetChanged();
            walletHistoryListAdapter.removeFooter();
        }

        if (!hasMoreArchived) {
            walletHistoryListAdapter.removeFooter();
        }

        if(!usingArchivedWallets && walletHistories.size() == 0){
            moreHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadMore() {


        if(usingArchivedWallets){

            if(hasMoreArchived){
                getWalletHistoryFromServer(++walletPage);
            }else{
                noMoreMessage();
            }

        }
        else{
            if(hasMore) {
                getWalletHistoryFromServer(++walletPage);
            }else{
                moreHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    private void noMoreMessage(){

        AppUtils.showToast("No more entries to show", true, 2);
    }
}
