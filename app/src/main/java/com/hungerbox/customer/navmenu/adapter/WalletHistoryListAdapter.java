package com.hungerbox.customer.navmenu.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.WalletHistory;
import com.hungerbox.customer.navmenu.activity.WalletHistoryActivity;
import com.hungerbox.customer.navmenu.adapter.viewholder.LoaderViewHolder;
import com.hungerbox.customer.navmenu.adapter.viewholder.WalletViewHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.DateTimeUtil;

import java.util.ArrayList;

/**
 * Created by peeyush on 28/6/16.
 */
public class WalletHistoryListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOADING = 2;
    public static final int ITEM = 1;
    public final String TXN_STATUS_REFUND = "reversed";
    public final String TXN_STATUS_SUCCESS = "success";
    WalletHistoryActivity activity;
    LayoutInflater inflater;
    ArrayList<WalletHistory> walletHistories;
    RecyclerView.ViewHolder loaderViewHolder;
    private boolean isLoadingFooterRemoved = false;

    public WalletHistoryListAdapter(Activity activity, ArrayList<WalletHistory> walletHistories) {
        this.activity = (WalletHistoryActivity) activity;
        inflater = LayoutInflater.from(activity);
        this.walletHistories = walletHistories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                viewHolder = new WalletViewHolder(inflater.inflate(R.layout.wallet_history_item, parent, false));
                break;
            case LOADING:
                viewHolder = createLoadingViewHolder(parent);
                break;
            default:
                AppUtils.HbLog("Wallet Item", " type is not supported!!! type is " + viewType);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case ITEM:

                WalletViewHolder walletViewHolder = (WalletViewHolder) holder;
                WalletHistory walletHistory = walletHistories.get(position);
                String dateTime[] = DateTimeUtil.getDateString12Hour(walletHistory.getCreated_at()).split(",");
                walletViewHolder.tvComment.setText(walletHistory.getComment());
                walletViewHolder.tv_txn_amount.setText(Math.abs(Double.parseDouble(walletHistory.getChange())) + "");
                walletViewHolder.tv_txn_date.setText(dateTime[1].trim());
                walletViewHolder.tv_txn_time.setText(dateTime[0]);


                if (!walletHistory.getTransactionId().isEmpty()) {
                    walletViewHolder.tv_txn_id.setVisibility(View.VISIBLE);
                    walletViewHolder.tv_txn_id_label.setVisibility(View.VISIBLE);
                    walletViewHolder.tv_txn_id.setText(walletHistory.getTransactionId());
                } else {
                    walletViewHolder.tv_txn_id.setVisibility(View.GONE);
                    walletViewHolder.tv_txn_id_label.setVisibility(View.GONE);
                }
                if (!walletHistory.getReference_type().isEmpty() && walletHistory.getReference_type().equalsIgnoreCase("order")) {
                    walletViewHolder.iv_txn_status.setVisibility(View.VISIBLE);
                    walletViewHolder.tv_transaction_status.setVisibility(View.VISIBLE);
                    switch (walletHistory.getTransactionState()){
                        case ApplicationConstants.PAYMENT_STATE_SUCCESS :
                            walletViewHolder.iv_txn_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_success_icon_wallet_history_page));
                            walletViewHolder.tv_transaction_status.setText("Success");
                            walletViewHolder.tv_transaction_status.setTextColor(activity.getResources().getColor(R.color.colorAccent));
                            break;
                        case ApplicationConstants.PAYMENT_STATE_REVERSE :
                            walletViewHolder.iv_txn_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_refunded_icon_wallet_history_page));
                            walletViewHolder.tv_transaction_status.setText("Refunded");
                            walletViewHolder.tv_transaction_status.setTextColor(activity.getResources().getColor(R.color.green));
                            break;
                        case ApplicationConstants.PAYMENT_STATE_FAILURE :
                            walletViewHolder.iv_txn_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_cancelled_icon_wallet_history_page_));
                            walletViewHolder.tv_transaction_status.setText("Cancelled");
                            walletViewHolder.tv_transaction_status.setTextColor(activity.getResources().getColor(R.color.red));
                            break;
                        case ApplicationConstants.PAYMENT_STATE_PENDING :
                            walletViewHolder.iv_txn_status.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pending_icon_wallet_history_page));
                            walletViewHolder.tv_transaction_status.setText("Pending");
                            walletViewHolder.tv_transaction_status.setTextColor(activity.getResources().getColor(R.color.yellow));
                            break;
                    }
                } else {
                    walletViewHolder.iv_txn_status.setVisibility(View.GONE);
                    walletViewHolder.tv_transaction_status.setVisibility(View.GONE);
                }
                if (!walletHistory.getWalletName().isEmpty()){
                    walletViewHolder.tv_txn_type.setText(walletHistory.getWalletName());
                }

                if (walletHistory.getWalletSource().equals(ApplicationConstants.PAYMENT_WALLET_SOURCE_INTERNAL) &&
                        (walletHistory.getTransactionState().equals(ApplicationConstants.PAYMENT_STATE_SUCCESS) ||
                                walletHistory.getTransactionState().equals(ApplicationConstants.PAYMENT_STATE_REVERSE))){
                    walletViewHolder.tv_wallet_label.setVisibility(View.VISIBLE);
                    walletViewHolder.tv_wallet_amount.setVisibility(View.VISIBLE);
                    walletViewHolder.tv_wallet_amount.setText(walletHistory.getAfter_amount());
                }
                else{
                    walletViewHolder.tv_wallet_label.setVisibility(View.GONE);
                    walletViewHolder.tv_wallet_amount.setVisibility(View.GONE);

                }

                break;
            case LOADING:
                loaderViewHolder = holder;
                activity.loadMore();
            default:
                break;
        }

    }


    @Override
    public int getItemCount() {
        if (walletHistories.size() > 0)
            return walletHistories.size() + 1;
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == walletHistories.size()) {
            return LOADING;
        } else {
            return ITEM;
        }
    }

    public void changeWalletHistory(ArrayList<WalletHistory> walletHistories) {
        this.walletHistories = walletHistories;
    }

    public void addWalletHistories(ArrayList<WalletHistory> walletHistories) {
        int currentPosition = this.walletHistories.size();
        this.walletHistories.addAll(walletHistories);
        notifyItemRangeInserted(currentPosition, walletHistories.size());
    }

    private RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);
        return new LoaderViewHolder(v);
    }

    public void removeFooter() {
        if (loaderViewHolder != null && loaderViewHolder.itemView != null)
            loaderViewHolder.itemView.setVisibility(View.INVISIBLE);

    }

}
