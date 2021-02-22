package com.hungerbox.customer.spaceBooking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.R
import com.hungerbox.customer.model.Product
import com.hungerbox.customer.model.Space
import com.hungerbox.customer.model.SpaceBookingDate
import com.hungerbox.customer.spaceBooking.listeners.RecyclerViewClickListener

class SpaceAdapter : RecyclerView.Adapter<SpaceAdapter.SpaceViewHolder>() {
    inner class SpaceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val clBody : ConstraintLayout = itemView.findViewById(R.id.cl_space_body)
        val tvName : TextView = itemView.findViewById(R.id.tv_name)
        val tvDensity : TextView = itemView.findViewById(R.id.tv_density)
        val tvAdd : TextView = itemView.findViewById(R.id.tv_add)
        fun bind(space: Product, position: Int){
            tvName.text = space.name
            tvAdd.text = space.desc
            tvDensity.text = "${space.maxQty} slots left"
            if (space.isSelected){
                clBody.background = itemView.context.resources.getDrawable(R.drawable.space_booking_date_bg)
            } else{
                clBody.background = itemView.context.resources.getDrawable(R.drawable.spinner_border_grey)
            }
            clBody.setOnClickListener {
                listener?.let {
                    it.onItemClicked(position)
                }
            }
            when(space.density){
                "medium" -> tvDensity.setTextColor(itemView.context.resources.getColor(R.color.yellow))
                "high" -> tvDensity.setTextColor(itemView.context.resources.getColor(R.color.red))
                else -> tvDensity.setTextColor(itemView.context.resources.getColor(R.color.green))
            }

        }
    }
    inner class DateDiffUtil(val oldList : List<Product>, val newList : List<Product>) : DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
    private var data : List<Product> = ArrayList()
    private var listener : RecyclerViewClickListener? = null

    fun setData(newList: List<Product>){
//        val diffResult = DiffUtil.calculateDiff(DateDiffUtil(data,newList))
//        diffResult.dispatchUpdatesTo(this)
        data = newList
        notifyDataSetChanged()

    }
    fun setClickListener(clickListener: RecyclerViewClickListener){
        listener = clickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.space_item,parent,false)
        return SpaceViewHolder(view)
    }
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SpaceViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
}