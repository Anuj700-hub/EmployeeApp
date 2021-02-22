package com.hungerbox.customer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by manas on 8/10/17.
 */
public class WalletResponse implements Serializable {
    @SerializedName("data")
    ArrayList<Wallet> wallets;

    public ArrayList<Wallet> getWallets() {
        if (wallets == null)
            return new ArrayList<>();
        return wallets;
    }

    public double getConvenienceFee(double price) {
        double convenienceFee = 0;
        double remainingPrice = price;
//        Collections.sort(wallets, new Comparator<Wallet>() {
//            @Override
//            public int compare(Wallet lhs, Wallet rhs) {
//                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
//                return lhs.priority > rhs.priority ? -1 : (lhs.priority < rhs.priority) ? 1 : 0;
//            }
//        });
        for (Wallet wallet : wallets) {
            if (remainingPrice > wallet.getAmount()) {
                convenienceFee += (wallet.getConvenienceFee() / 100) * remainingPrice;
            } else {
                convenienceFee += (wallet.getConvenienceFee() / 100) * remainingPrice;
                break;
            }

        }
        return convenienceFee;
    }

    public boolean canEmployeeRecharge() {
        for (Wallet wallet : wallets) {
            if (wallet.isEmployeeCanRecharge())
                return true;
        }
        return false;

    }

    public double getConvenienceFees() {
        for (Wallet wallet : wallets) {
            if (wallet.getConvenienceFee() > 0) {
                return wallet.getConvenienceFee();
            }
        }
        return 0;
    }
}
