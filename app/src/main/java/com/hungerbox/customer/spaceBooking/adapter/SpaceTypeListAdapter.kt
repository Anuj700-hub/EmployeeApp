package com.hungerbox.customer.spaceBooking.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.R
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.spaceBooking.SpaceBookingDashboard
import com.hungerbox.customer.spaceBooking.viewholder.SpaceTypeListViewHolder
import com.hungerbox.customer.util.ImageHandling
import kotlinx.android.synthetic.main.space_type_item_list.view.*

class SpaceTypeListAdapter(private val activity: Activity, private val spaceTypes: ArrayList<Config.SpaceType>, private val spaceTypeSelected: SpaceBookingDashboard.SpaceTypeClick) : RecyclerView.Adapter<SpaceTypeListViewHolder>() {

    private var inflater: LayoutInflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceTypeListViewHolder {
        return SpaceTypeListViewHolder(inflater.inflate(R.layout.space_type_item_list, parent, false))
    }

    override fun onBindViewHolder(holder: SpaceTypeListViewHolder, position: Int) {
        val spaceType = spaceTypes.get(position)

        holder.itemView.tv_space_type.text = spaceType.name
        holder.itemView.cl_space_type.isSelected = spaceType.isSelected
        holder.itemView.tv_space_type.isSelected = spaceType.isSelected
        ImageHandling.loadRemoteImage(spaceType.icon_url, holder.itemView.iv_space_image, R.drawable.ic_table_booking_icon_big, R.drawable.ic_table_booking_icon_big, activity)

        holder.itemView.setOnClickListener {
            spaceTypeSelected.onSpaceTypeSelected(spaceType)
            notifyDataSetChanged()
        }


    }

    override fun getItemCount(): Int = spaceTypes.size

}