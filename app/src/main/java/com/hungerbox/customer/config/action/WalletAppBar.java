package com.hungerbox.customer.config.action;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.hungerbox.customer.model.User;
import com.hungerbox.customer.util.AppUtils;

/**
 * Created by peeyush on 9/8/16.
 */
public class WalletAppBar {

    View walletContainer;
    TextView tvWalletBalance;
    double walletbalance;
    User user;
    Context context;
    WalletClickListener walletClickListener;

    public WalletAppBar(View rlWalletContainer, TextView tvWalletBalance, @NonNull User user,
                        final WalletClickListener walletClickListener) {
        this.walletContainer = rlWalletContainer;
        this.tvWalletBalance = tvWalletBalance;
        this.user = user;
        this.walletbalance = user.getCurrentWalletBalance();
        this.walletClickListener = walletClickListener;
        this.context = walletContainer.getContext();
        if (AppUtils.getConfig(context) != null && AppUtils.getConfig(context).getWallet_present() && AppUtils.getConfig(context).is_wallet_displayed()) {
            rlWalletContainer.setVisibility(View.VISIBLE);
            tvWalletBalance.setText(getWalletBalanceString());
        } else {
            rlWalletContainer.setVisibility(View.INVISIBLE);
        }

        walletContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onWalletCliked();
            }
        });
    }


    public WalletAppBar(View rlWalletContainer, TextView tvWalletBalance, WalletClickListener walletClickListener) {
        this.walletContainer = rlWalletContainer;
        this.tvWalletBalance = tvWalletBalance;
        this.walletClickListener = walletClickListener;
        this.context = walletContainer.getContext();
    }

    private void onWalletCliked() {
        if (walletClickListener != null && AppUtils.getConfig(context) != null
                && AppUtils.getConfig(context).getWallet_present()
                && AppUtils.getConfig(context).is_wallet_displayed()
                && user.getCurrentWallets() != null) {
            walletClickListener.onWalletClicked(user);
        }
    }

    public void updateWallet(@NonNull User user) {
        this.user = user;
        this.walletbalance = user.getCurrentWalletBalance();
        updateWallet();
    }

    public void updateWallet() {
        if (AppUtils.getConfig(context).getWallet_present() && AppUtils.getConfig(context).is_wallet_displayed()) {
            walletContainer.setVisibility(View.VISIBLE);
            tvWalletBalance.setText(getWalletBalanceString());
        } else {
            walletContainer.setVisibility(View.INVISIBLE);
        }
    }

    public String getWalletBalanceString() {
        return String.format("₹ %.2f", this.walletbalance);
    }


}
