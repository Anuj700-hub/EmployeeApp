package com.hungerbox.customer.util.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.hungerbox.customer.R
import com.hungerbox.customer.util.ApplicationConstants
import kotlinx.android.synthetic.main.fragment_error_pop.view.*


/**
 * A simple [Fragment] subclass.
 */
class ErrorPopFragment : DialogFragment() {

    var message: String = ""
    var positiveButton: String = ""
    var crossButton: Boolean = false
    var imageType: String = ""
    var listener: OnFragmentInteractionListener = object : OnFragmentInteractionListener {
        override fun onPositiveInteraction() {
        }
        override fun onNegativeInteraction() {
        }
    }

    companion object {
        fun newInstance(message: String, positiveButton: String,imageType: String , listener: OnFragmentInteractionListener): ErrorPopFragment {
            val fragment = ErrorPopFragment()
            fragment.message = message
            fragment.positiveButton = positiveButton
            fragment.listener = listener
            fragment.imageType  = imageType
            return fragment
        }

        fun newInstance(message: String, positiveButton: String, crossButton: Boolean, imageType: String,listener: OnFragmentInteractionListener): ErrorPopFragment {
            val fragment = ErrorPopFragment()
            fragment.message = message
            fragment.positiveButton = positiveButton
            fragment.crossButton = crossButton
            fragment.listener = listener
            fragment.imageType = imageType
            return fragment
        }

        fun newInstance(): ErrorPopFragment {
            return ErrorPopFragment();
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME,0)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

//        dialog?.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.tranparent)))
        val view = inflater.inflate(R.layout.fragment_error_pop, container)
        view.ll_header.visibility = if (crossButton) View.VISIBLE else View.INVISIBLE
        view.tv_error_text.text = message
        view.bt_positive.text = positiveButton
        view.iv_error.setImageResource(setImage(imageType))


        view.ll_header.setOnClickListener {
            dismissAllowingStateLoss()
            if (listener != null) {
                listener.onNegativeInteraction()
            }
        }

        view.bt_positive.setOnClickListener {
            dismissAllowingStateLoss()
            if (listener != null) {
                listener.onPositiveInteraction()
            }
        }


        return view
    }


    interface OnFragmentInteractionListener {
        fun onPositiveInteraction()

        fun onNegativeInteraction()
    }


    fun setImage(imageType: String): Int {
        when (imageType) {
            ApplicationConstants.NO_INTERNET_IMAGE -> return R.drawable.internet_error
            ApplicationConstants.GENERAL_ERROR -> return R.drawable.general_error
            ApplicationConstants.PAYMENT_FAILED_IMAGE -> return R.drawable.payment_error_new
            else -> {
                return R.drawable.general_error
            }
        }
    }

}
