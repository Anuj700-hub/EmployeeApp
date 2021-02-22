package com.hungerbox.customer.navmenu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.R
import com.hungerbox.customer.model.WalletBreakupItem
import java.util.*

class WalletBreakupDataAdapter(var myWallets: ArrayList<WalletBreakupItem>) : RecyclerView.Adapter<WalletBreakupDataAdapter.WalletViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): WalletViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item, parent, false)
        return WalletViewHolder(v)
    }

    override fun onBindViewHolder(walletViewHolder: WalletViewHolder, position: Int) {
        walletViewHolder.tvWalletName.text = myWallets[position].getWalletName()
        walletViewHolder.tvAmount.text = myWallets[position].getWalletAmmount()
        if (position == itemCount - 1) {
            walletViewHolder.separator.visibility = View.GONE
        } else {
            walletViewHolder.separator.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return myWallets.size
    }

    inner class WalletViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tvWalletName: TextView = v.findViewById(R.id.tv_wallet_name)
        var tvAmount: TextView = v.findViewById(R.id.tv_amount)
        var separator: View = v.findViewById(R.id.separator)

    }
}