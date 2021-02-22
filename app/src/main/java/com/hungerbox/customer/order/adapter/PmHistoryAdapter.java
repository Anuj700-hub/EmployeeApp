package com.hungerbox.customer.order.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.WalletHistory;
import com.hungerbox.customer.navmenu.adapter.viewholder.LoaderViewHolder;
import com.hungerbox.customer.order.activity.PaymentMethodHistoryActivity;
import com.hungerbox.customer.order.adapter.viewholder.PmHistoryViewHolder;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.DateTimeUtil;

import java.util.ArrayList;

/**
 * Created by manas on 18/12/17.
 */
public class PmHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int LOADING = 2;
    public static final int ITEM = 1;
    PaymentMethodHistoryActivity activity;
    LayoutInflater inflater;
    ArrayList<WalletHistory> walletHistories;
    RecyclerView.ViewHolder loaderViewHolder;
    private boolean isLoadingFooterRemoved = false;

    public PmHistoryAdapter(Activity activity, ArrayList<WalletHistory> walletHistories) {
        this.activity = (PaymentMethodHistoryActivity) activity;
        inflater = LayoutInflater.from(activity);
        this.walletHistories = walletHistories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM:
                viewHolder = new PmHistoryViewHolder(inflater.inflate(R.layout.payment_method_history_item, parent, false));
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
                PmHistoryViewHolder walletViewHolder = (PmHistoryViewHolder) holder;
                WalletHistory walletHistory = walletHistories.get(position);
                walletViewHolder.tvDate.setText(DateTimeUtil.getDateString(walletHistory.getCreated_at()));
                walletViewHolder.tvCreditDebit.setText(walletHistory.getChange());
                walletViewHolder.tvRemarks.setText(walletHistory.getComment());
//                walletViewHolder.tvWalletAmount.setText(walletHistory.getBefore_amount());
//                walletViewHolder.tvDate.setText(DateTimeUtil.getDateString(walletHistory.getCreated_at()));
//                walletViewHolder.tvWalletName.setText(walletHistory.getWalletName());

//                if(!walletHistory.getTransactionId().isEmpty()){
//                    walletViewHolder.llTransactionContainer.setVisibility(View.VISIBLE);
//                    walletViewHolder.tvTransactionId.setText(walletHistory.getTransactionId());
//
//                    walletViewHolder.llStatusContainer.setVisibility(View.VISIBLE);
//                    walletViewHolder.tvStatus.setText(walletHistory.getStatus());
//                }else{
//                    walletViewHolder.llTransactionContainer.setVisibility(View.GONE);
//                    walletViewHolder.llStatusContainer.setVisibility(View.GONE);
//                }

                if (!walletHistory.getStatus().isEmpty()) {

                }

                break;
            case LOADING:
                activity.loadMore();
                loaderViewHolder = holder;
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
        loaderViewHolder.itemView.setVisibility(View.INVISIBLE);

    }

}
