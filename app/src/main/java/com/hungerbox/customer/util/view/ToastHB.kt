package com.hungerbox.customer.util.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.hungerbox.customer.R

class ToastHB{
    @SuppressLint("InflateParams")
    fun make(context: Context, text: CharSequence?, duration: Int, BackgroundColor: Int?, TextColor: String?,ToastIcon:Int?): Toast {
        val toast = Toast(context)
        val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflate.inflate(R.layout.toast_layout, null)
        val toastText = v.findViewById(R.id.text) as TextView
        val linear = v.findViewById(R.id.ln) as LinearLayout
        val toasticon = v.findViewById(R.id.toasticon) as ImageView

        linear.setBackgroundResource(BackgroundColor!!)

        if (TextColor.isNullOrBlank()){
            toastText.setTextColor(Color.parseColor("#ffffff"))

        }else{
            toastText.setTextColor(Color.parseColor(TextColor))

        }
        if (ToastIcon == null){
            toasticon.visibility = View.GONE

        }else{
            toasticon.setBackgroundResource(ToastIcon)
        }


        toastText.text = text
        toast.duration = duration
        toast.view = v

        return toast
    }

    private fun toastBackground(color: Int?): LayerDrawable {
        val outerRadii = floatArrayOf(75f, 75f, 75f, 75f,75f, 75f, 75f, 75f)
        val shapeDrawable = ShapeDrawable()
        shapeDrawable.shape = RoundRectShape(outerRadii,null,null)
        shapeDrawable.paint.color = color!!
        shapeDrawable.paint.style = Paint.Style.FILL
        shapeDrawable.paint.isAntiAlias = true
        val drawables = arrayOf<Drawable>(shapeDrawable)
        return LayerDrawable(drawables)
    }

}