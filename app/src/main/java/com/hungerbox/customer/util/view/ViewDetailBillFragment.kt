package com.hungerbox.customer.util.view


import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.hungerbox.customer.R
import com.hungerbox.customer.model.Order
import com.hungerbox.customer.model.User
import com.hungerbox.customer.model.Vendor
import com.hungerbox.customer.util.AppUtils
import kotlinx.android.synthetic.main.fragment_view_detail_bill.view.*


/**
 * A simple [Fragment] subclass.
 */
class ViewDetailBillFragment : DialogFragment() {

    lateinit var selectedOrderVendor : Vendor
    lateinit var order : Order
    lateinit var user : User
    lateinit var listener: OnFragmentInteractionListener

    companion object {
        fun newInstance(user : User,order: Order, vendor: Vendor,listener : OnFragmentInteractionListener): ViewDetailBillFragment {
            val fragment = ViewDetailBillFragment()
            fragment.selectedOrderVendor = vendor
            fragment.order = order
            fragment.user = user
            fragment.listener = listener
            return fragment
        }


        fun newInstance(): ViewDetailBillFragment {
            return ViewDetailBillFragment();
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.tranparent)))
        val view = inflater.inflate(R.layout.fragment_view_detail_bill, container)

        val cgst = order.getCgst(selectedOrderVendor)
        if (cgst <= 0) {
            view.ll_cgst_container.setVisibility(View.GONE)
        } else {
            view.ll_cgst_container.setVisibility(View.VISIBLE)
            view.tv_cgst.setText("₹ $cgst")
        }

        val sgst = order.getSgst(selectedOrderVendor)
        if (sgst <= 0) {
            view.ll_sgst_container.setVisibility(View.GONE)
        } else {
            view.ll_sgst_container.setVisibility(View.VISIBLE)
            view.tv_sgst.setText(String.format("₹ $sgst"))
        }

        if (!selectedOrderVendor.isRestaurant() && AppUtils.getConfig(activity).isShow_gst && selectedOrderVendor.getCustomerGst() > 0) {
            val customerGst = selectedOrderVendor.getCustomerGst()
            val roundedPrice = java.lang.Double.parseDouble(String.format("%.2f", order.getPrice() * customerGst / (2.0 * (100 + customerGst))))
            view.tv_total_order.setText(String.format("₹ %.2f", order.getPrice() - roundedPrice - roundedPrice))
            view.tv_total_price.setText(String.format("₹ %.2f", order.getTotalPrice()))

            view.ll_cgst_container.setVisibility(View.VISIBLE)
            view.ll_sgst_container.setVisibility(View.VISIBLE)
            view.tv_sgst.setText(String.format("\u20B9 %.2f", order.price * customerGst / (2.0 * (100 + customerGst))))
            view.tv_cgst.setText(String.format("\u20B9 %.2f", order.price * customerGst / (2.0 * (100 + customerGst))))

        } else {
            view.tv_total_order.setText(String.format("₹ %.2f", order.getPrice()))
            view.tv_total_price.setText(String.format("₹ %.2f", order.getTotalPrice()))
        }

        if (order.getDeliveryCharge() <= 0) {
            view.ll_delivery_container.setVisibility(View.GONE)
        } else {
            view.ll_delivery_container.setVisibility(View.VISIBLE)
            view.tv_delivery.setText(String.format("₹ %.2f", order.getDeliveryCharge()))
        }
        val containerCharge = order.getContainerCharge()

        if (containerCharge <= 0) {
            view.ll_container_charge_container.setVisibility(View.GONE)
        } else {
            view.ll_container_charge_container.setVisibility(View.VISIBLE)
            view.tv_container.setText(String.format("₹ %.2f", containerCharge))
        }

        val convenienceFee = user.getCurrentWallets().getConvenienceFee(order.price)

        if (convenienceFee > 0) {
            view.ll_convenience_container.setVisibility(View.VISIBLE)
            view.tv_convenience.setText("₹ " + Math.round(convenienceFee * 100) / 100.00)
            order.setConvenienceFee(convenienceFee)
        } else
            view.ll_convenience_container.setVisibility(View.GONE)

        view.bt_positive.setOnClickListener {
            dismissAllowingStateLoss()
            if (listener != null) {
                listener.positiveClick()
            }
        }

        return view
    }

    interface OnFragmentInteractionListener {
        fun positiveClick()
    }


}
