package com.hungerbox.customer.mvvm.util

import android.os.Build
import android.text.SpannableString
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.hungerbox.customer.MainApplication

class HBDesignPatternConst {

    companion object{

        @JvmStatic
        @BindingAdapter("isVisible")
        fun setIsVisible(view: View, isVisible: Int) {
            view.visibility = isVisible
        }

        @JvmStatic
        @BindingAdapter("switchChecked")
        fun setSwitchChecked(view: View, isChecked: Boolean) {
            (view as? SwitchCompat)?.isChecked = isChecked
        }

        @JvmStatic
        @BindingAdapter("isFocus")
        fun setIsFocus(view: View, isFocus: Int) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    view.focusable = isFocus
                else
                    view.requestFocus()
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }

        @JvmStatic
        @BindingAdapter("rightDrawable")
        fun setRightDrawable(view: View, rightDrawable: Int) {
            if(view is TextInputEditText){
                view.setCompoundDrawablesWithIntrinsicBounds(null, null, MainApplication.appContext.resources.getDrawable(rightDrawable), null)
            }
        }

        @JvmStatic
        @BindingAdapter("spannableText")
        fun setSpannableText(view: View, spannableText: SpannableString) {
            if(view is TextView){
                view.text = spannableText
            }
        }
    }
}