package com.hungerbox.customer.order.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hungerbox.customer.R

class ApprovalPendingDialog(val title : String, val desc : String, var listener: OnFragmentInteractionListener?) : DialogFragment() {

    lateinit var btPositive: Button
    lateinit var tvTitle: TextView
    lateinit var tvDesc: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.approval_pending_popup, container, false)
        btPositive = view.findViewById(R.id.bt_ok_apr)
        tvTitle = view.findViewById(R.id.popup_title)
        tvDesc = view.findViewById(R.id.popup_desc)

        btPositive.setOnClickListener {
            dismissAllowingStateLoss()
            listener?.onPositiveInteraction()
        }
        tvTitle.text = title
        tvDesc.text = desc

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onPositiveInteraction()
    }
}