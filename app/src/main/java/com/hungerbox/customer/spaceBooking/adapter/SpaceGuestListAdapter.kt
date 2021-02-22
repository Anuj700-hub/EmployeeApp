package com.hungerbox.customer.spaceBooking.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hungerbox.customer.R
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.model.BookingGuest
import com.hungerbox.customer.spaceBooking.viewholder.SpaceGuestListViewHolder
import kotlinx.android.synthetic.main.space_booking_guest_list.view.*
import java.util.ArrayList

class SpaceGuestListAdapter(private val activity: Activity, private var bookingGuests: ArrayList<BookingGuest>,
                            private val guestDetails: Config.SpaceType.GuestDetails?) : RecyclerView.Adapter<SpaceGuestListViewHolder>() {
    private var inflater: LayoutInflater = LayoutInflater.from(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpaceGuestListViewHolder {
        return SpaceGuestListViewHolder(inflater.inflate(R.layout.space_booking_guest_list, parent, false))
    }

    override fun onBindViewHolder(holder: SpaceGuestListViewHolder, position: Int) {
        val bookingGuest = bookingGuests[position]

        holder.itemView.tv_guest_no.text = "Guest ${position + 1}"

        if (guestDetails?.name == 0) {
            holder.itemView.til_guest_name.visibility = View.GONE
        } else if (guestDetails?.name == 1) {
            holder.itemView.til_guest_name.visibility = View.VISIBLE
            holder.itemView.et_guest_name.setText(bookingGuest.name ?: "")
        } else {
            holder.itemView.til_guest_name.visibility = View.VISIBLE
            holder.itemView.til_guest_name.hint = "Name *"
            holder.itemView.et_guest_name.setText(bookingGuest.name ?: "")
        }

        if (guestDetails?.phone_no == 0) {
            holder.itemView.til_guest_phone.visibility = View.GONE
        } else if (guestDetails?.phone_no == 1) {
            holder.itemView.til_guest_phone.visibility = View.VISIBLE
            holder.itemView.et_guest_phone.setText(bookingGuest.phoneNo ?: "")
        } else {
            holder.itemView.til_guest_phone.visibility = View.VISIBLE
            holder.itemView.til_guest_phone.hint = "Phone No. *"
            holder.itemView.et_guest_phone.setText(bookingGuest.phoneNo ?: "")
        }

        if (guestDetails?.email == 0) {
            holder.itemView.til_guest_email.visibility = View.GONE
        } else if (guestDetails?.email == 1) {
            holder.itemView.til_guest_email.visibility = View.VISIBLE
            holder.itemView.et_guest_email.setText(bookingGuest.email ?: "")
        } else {
            holder.itemView.til_guest_email.visibility = View.VISIBLE
            holder.itemView.til_guest_email.hint = "Email ID *"
            holder.itemView.et_guest_email.setText(bookingGuest.email ?: "")
        }
    }

    override fun getItemCount() = bookingGuests.size

    fun setData(bookingGuests: ArrayList<BookingGuest>) {
        this.bookingGuests = bookingGuests
        notifyDataSetChanged()
    }
}