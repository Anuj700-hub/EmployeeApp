package com.hungerbox.customer.spaceBooking

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.model.BookingGuest
import com.hungerbox.customer.order.activity.BookmarkPaymentActivity
import com.hungerbox.customer.spaceBooking.adapter.SpaceGuestListAdapter
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.SharedPrefUtil
import kotlinx.android.synthetic.main.activity_space_guest.*
import kotlinx.android.synthetic.main.space_booking_guest_list.view.*
import kotlinx.android.synthetic.main.toolbar_white_v2.*

class SpaceGuestActivity : AppCompatActivity() {

    var guestSize = 0
    var spaceLocationId = 0L
    var spaceLocationName :String? = null
    var guestDetails: Config.SpaceType.GuestDetails? = null
    var type:String?=null
    private lateinit var bookingGuests: ArrayList<BookingGuest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_guest)

        type = intent.getStringExtra("type")
        guestSize = intent.getIntExtra("guest_size",0)-1
        spaceLocationId = intent.getLongExtra(ApplicationConstants.SPACE_LOCATION_ID,0)
        spaceLocationName = intent.getStringExtra(ApplicationConstants.SPACE_LOCATION_NAME)

        guestDetails = AppUtils.getSpaceType(this, type?.toString())?.guest_details

        iv_back.setOnClickListener {
            onBackPressed()
        }
        tv_toolbar_title.text = AppUtils.getSpaceType(this, type?.toString())?.toolbar_header ?:"Book"

        if (guestDetails != null) {
            setUI();
        }

    }

    override fun onResume() {
        super.onResume()
        if(SharedPrefUtil.getBoolean(ApplicationConstants.BACK_TO_SPACE_BOOKING, false)){
            SharedPrefUtil.putBoolean(ApplicationConstants.BACK_TO_SPACE_BOOKING, false)
            finish()
        }
    }

    private fun setUI() {

        if (guestDetails!!.isNotification_box) {
            cb_notification.visibility = View.VISIBLE
        } else {
            cb_notification.visibility = View.GONE
        }

        bookingGuests = ArrayList<BookingGuest>()
        for (i in 1..guestSize) {
            bookingGuests.add(BookingGuest(null, null, null))
        }

        val spaceGuestListAdapter = SpaceGuestListAdapter(this, bookingGuests, guestDetails)
        rv_guest_list.adapter = spaceGuestListAdapter
        rv_guest_list.setItemViewCacheSize(guestSize)
        spaceGuestListAdapter.notifyDataSetChanged()


        bt_book.setOnClickListener {
            var allFieldsValid = true
            var errorString = "Enter all required details "
            for (i in 0 until guestSize) {

                bookingGuests[i].isNotification = cb_notification.isChecked

                val name = rv_guest_list.getChildAt(i).et_guest_name.text?.toString()?.trim()
                if (guestDetails?.name == 2) {
                    if (!name.isNullOrEmpty()) {
                        bookingGuests[i].name = name
                    } else {
                        allFieldsValid = false
                        break
                    }
                } else {
                    bookingGuests[i].name = name
                }

                val phoneNo = rv_guest_list.getChildAt(i).et_guest_phone.text?.toString()

                if (guestDetails?.phone_no == 2) {
                    if (phoneNo.isNullOrEmpty() || phoneNo.length != 10) {
                        errorString = "Enter valid phone number"
                        allFieldsValid = false
                        break
                    } else {
                        bookingGuests[i].phoneNo = phoneNo
                    }
                } else {
                    if (!phoneNo.isNullOrEmpty()) {
                        if (phoneNo.length == 10) {
                            bookingGuests[i].phoneNo = phoneNo
                        } else {
                            errorString = "Enter valid phone number"
                            allFieldsValid = false
                            break
                        }
                    }
                }

                val email = rv_guest_list.getChildAt(i).et_guest_email.text?.toString()?.trim()
                val pattern = Patterns.EMAIL_ADDRESS

                if (guestDetails?.email == 2) {
                    if (email.isNullOrEmpty() || !pattern.matcher(email).matches()) {
                        errorString = "Enter valid email"
                        allFieldsValid = false
                        break
                    } else {
                        bookingGuests[i].email = email
                    }
                } else {
                    if (!email.isNullOrEmpty()) {
                        if (pattern.matcher(email).matches()) {
                            bookingGuests[i].email = email
                        } else {
                            errorString = "Enter valid email"
                            allFieldsValid = false
                            break
                        }
                    }
                }
            }
            if (allFieldsValid) {
                val mainApplication = application as MainApplication
                if (MainApplication.isCartCreated()) {

                    val spaceGuestItem: ArrayList<BookingGuest> = ArrayList()
                    for(g in bookingGuests){
                        if(!g.name.isNullOrEmpty() || !g.email.isNullOrEmpty() || !g.phoneNo.isNullOrEmpty())
                            spaceGuestItem.add(g)
                    }
                    mainApplication.cart.spaceGuests = spaceGuestItem
                    val intent =  Intent(this, BookmarkPaymentActivity::class.java);
                    intent.putExtra(ApplicationConstants.SpaceBooking.SPACE_TYPE,type)
                    intent.putExtra(ApplicationConstants.FROM_SPACE_BOOKING,true)
                    intent.putExtra(ApplicationConstants.SPACE_LOCATION_ID,spaceLocationId)
                    intent.putExtra(ApplicationConstants.SPACE_LOCATION_NAME,spaceLocationName)
                    startActivity(intent)
                }

            } else {
                Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
            }
        }

    }

}