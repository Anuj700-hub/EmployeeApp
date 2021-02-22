package com.hungerbox.customer.navmenu.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.DialogFragment
import com.hungerbox.customer.R
import com.hungerbox.customer.model.WalletBreakupItem
import com.hungerbox.customer.navmenu.activity.WalletHistoryActivity
import com.hungerbox.customer.navmenu.adapter.WalletBreakupDataAdapter
import kotlinx.android.synthetic.main.wallet_breakup_popup.*


class WalletBreakupFragment : DialogFragment() {
    companion object{

        private const val WALLETLIST = "wallet_list"
        private const val RECHARGE_ALLOWED = "rechargePopup"
        fun newInstance(walletList : ArrayList<WalletBreakupItem>, isRechargeAllowed : Boolean) : WalletBreakupFragment{
            val bundle = Bundle()
            bundle.putSerializable(WALLETLIST,walletList)
            bundle.putBoolean(RECHARGE_ALLOWED,isRechargeAllowed)
            var fragment = WalletBreakupFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME,0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.wallet_breakup_popup,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listOfWallets : ArrayList<WalletBreakupItem> = arguments?.get(WALLETLIST) as ArrayList<WalletBreakupItem>
        if (!listOfWallets.isNullOrEmpty()){
            val set = ConstraintSet()
            set.clone(cl_parent)
            rv_wallet_list.adapter = WalletBreakupDataAdapter(listOfWallets)
            when(listOfWallets.size){
                1 -> set.constrainPercentHeight(R.id.rv_wallet_list,0.45f)
                2 -> set.constrainPercentHeight(R.id.rv_wallet_list,0.6f)
                else -> set.constrainPercentHeight(R.id.rv_wallet_list,0.7f)
            }
            set.applyTo(cl_parent)
        }
        bt_wallet_history.setOnClickListener {
            val intent = Intent(activity,WalletHistoryActivity::class.java)
            intent.putExtra(RECHARGE_ALLOWED,arguments?.getBoolean(RECHARGE_ALLOWED))
            startActivity(intent)
            dismissAllowingStateLoss()
        }
        iv_close.setOnClickListener { dismissAllowingStateLoss() }
    }
}