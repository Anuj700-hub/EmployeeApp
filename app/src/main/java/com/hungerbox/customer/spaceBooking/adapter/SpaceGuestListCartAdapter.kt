package com.hungerbox.customer.spaceBooking.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.R
import com.hungerbox.customer.model.OrderGuestInfo
import com.hungerbox.customer.spaceBooking.viewholder.SpaceGuestListCartViewHolder
import kotlinx.android.synthetic.main.space_guest_list_cart_item.view.*
import java.util.ArrayList

class SpaceGuestListCartAdapter(private val activity: Activity, private val orderGuestInfos: ArrayList<OrderGuestInfo>) : RecyclerView.Adapter<SpaceGuestListCartViewHolder>() {
    private var inflater: LayoutInflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceGuestListCartViewHolder {
        return SpaceGuestListCartViewHolder(inflater.inflate(R.layout.space_guest_list_cart_item,parent,false))
    }

    override fun onBindViewHolder(holder: SpaceGuestListCartViewHolder, position: Int) {

        val guest = orderGuestInfos[position]
        val itemView = holder.itemView

        if(guest.email.isNullOrEmpty()){
            itemView.tv_guest_email.visibility = View.GONE
        }else{
            itemView.tv_guest_email.visibility = View.VISIBLE
            itemView.tv_guest_email.text = guest.email

        }

        if(guest.name.isNullOrEmpty()){
            itemView.tv_guest_name.visibility = View.GONE
        }else{
            itemView.tv_guest_name.visibility = View.VISIBLE
            itemView.tv_guest_name.text = guest.name

        }

        if(guest.phoneNo.isNullOrEmpty()){
            itemView.tv_guest_mobile.visibility = View.GONE
        }else{
            itemView.tv_guest_mobile.visibility = View.VISIBLE
            itemView.tv_guest_mobile.text = guest.phoneNo

        }

        itemView.tv_person_number.text = "${position+1} ."
    }

    override fun getItemCount(): Int {
        return orderGuestInfos.size
    }
}