package com.hungerbox.customer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition


class ImageHandling {


    companion object {
        @JvmStatic
        fun loadRemoteImage(image : String?, view : ImageView, errorImage : Int, placeHolderImage : Int, context : Context?){

            val requestOptions = RequestOptions()

            if(placeHolderImage != -1){
                requestOptions.placeholder(placeHolderImage)
            }

            if(errorImage == -1){
                requestOptions.error(errorImage)
            }

            if(image!=null && AppUtils.getFileExtension(image).equals("gif")){
                loadRemoteGifImage(context,view,image)
            }else {
                try {
                    Glide.with(context!!).load(if (image == null) "" else image).apply(requestOptions).addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            Log.v("Glide", dataSource?.name)
                            return false
                        }

                    }).into(view)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        @JvmStatic
        fun loadRemoteImageAsBitmap(image : String?, view : ImageView, errorImage : Int, placeHolderImage : Int, context : Context?){

            val requestOptions = RequestOptions()

            if(placeHolderImage != -1){
                requestOptions.placeholder(placeHolderImage)
            }

            if(errorImage == -1){
                requestOptions.error(errorImage)
            }

            try {
                Glide.with(context!!).asBitmap().load(if (image == null) "" else image).apply(requestOptions).addListener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.v("Glide", dataSource?.name)
                        return false
                    }

                }).into(view)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }

        @JvmStatic
        fun loadLocalGifImage(context: Context, view : ImageView, resourseId : Int){

            Glide.with(context).load(resourseId).into(view)
        }

        @JvmStatic
        fun loadRemoteGifImage(context: Context?, view : ImageView, url : String){

            try {
                Glide.with(context!!).load(url).into(view)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun loadLocalImage(context: Context, view : ImageView, resourseId : Int){

            view.setImageResource(resourseId)
        }

        @JvmStatic
        fun loadBackgroundImageInView(image : String?, view : View, errorImage : Int, placeHolderImage : Int, context : Context){

            val requestOptions = RequestOptions()

            if(placeHolderImage != -1){
                requestOptions.placeholder(placeHolderImage)
                view.setBackground(ContextCompat.getDrawable(context, placeHolderImage))
            }

            Glide.with(context).load(if(image == null)  "" else image).apply(requestOptions).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    view.setBackground(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    view.setBackground(ContextCompat.getDrawable(context, errorImage))
                }
            })
        }

        @JvmStatic
        fun loadBackgroundImageInViewWithCallback(image : String?, context : Context, target : SimpleTarget<Drawable>){

            Glide.with(context).load(image).into(target)
        }
    }
}