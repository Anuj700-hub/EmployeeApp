package com.hungerbox.customer.spaceBooking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.R
import com.hungerbox.customer.model.SpaceBookingDate
import com.hungerbox.customer.spaceBooking.listeners.RecyclerViewClickListener

class DateAdapter : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {
    inner class DateViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val clBody : ConstraintLayout = itemView.findViewById(R.id.cl_date_body)
        val tvDay : TextView = itemView.findViewById(R.id.tv_day)
        val tvDate : TextView = itemView.findViewById(R.id.tv_date)
        fun bind(date: SpaceBookingDate, position: Int){
            tvDay.text = date.day
            tvDate.text = date.formattedDateString
            if (date.isSelected){
                clBody.background = itemView.context.resources.getDrawable(R.drawable.space_booking_date_bg)
            } else{
                clBody.background = null
            }
            clBody.setOnClickListener {
                listener?.let {
                    it.onItemClicked(position)
                }
            }
        }
    }
    inner class DateDiffUtil(val oldList : List<SpaceBookingDate>, val newList : List<SpaceBookingDate>) : DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].dateInMillis == newList[newItemPosition].dateInMillis
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }
    private var data : List<SpaceBookingDate> = ArrayList()
    private var listener : RecyclerViewClickListener? = null

    fun setData(newList: List<SpaceBookingDate>){
        //diffutil causing issues for item click
//        val diffResult = DiffUtil.calculateDiff(DateDiffUtil(data,newList))
//        diffResult.dispatchUpdatesTo(this)
        data = newList
        notifyDataSetChanged()

    }
    fun setClickListener(clickListener: RecyclerViewClickListener){
        listener = clickListener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_date_list_item,parent,false)
        return DateViewHolder(view)
    }
    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(data[position], position)
    }
}