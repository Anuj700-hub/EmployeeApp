package com.hungerbox.customer.spaceBooking

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.config.Config
import com.hungerbox.customer.order.fragment.GeneralDialogFragment
import com.hungerbox.customer.spaceBooking.adapter.SpaceTypeListAdapter
import com.hungerbox.customer.util.AppUtils
import kotlinx.android.synthetic.main.activity_space_booking_dashboard.*
import kotlinx.android.synthetic.main.toolbar_white_v2.*

class SpaceBookingDashboard : AppCompatActivity() {

    var spaceManagement: Config.SpaceManagement? = null
    var selectedSpaceType: String? = null
    lateinit var mainApplication: MainApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_booking_dashboard)


        mainApplication = application as MainApplication
        spaceManagement = AppUtils.getSpaceConfig(this)
        if (spaceManagement != null && spaceManagement?.spaceTypes != null && spaceManagement?.spaceTypes?.size!! > 0) {
            setUI();
        }
    }

    private fun setUI() {
        tv_toolbar_title.text = spaceManagement?.title ?: "Space Management"
        iv_back.setOnClickListener {
            onBackPressed()
        }

        clearSelection()

        val spaceTypeAdapter = SpaceTypeListAdapter(this, spaceManagement!!.spaceTypes, object : SpaceTypeClick {
            override fun onSpaceTypeSelected(spaceType: Config.SpaceType) {
                for (space in spaceManagement?.spaceTypes!!) {
                    space.isSelected = spaceType.key.equals(space.key, ignoreCase = true)
                }
                selectedSpaceType = spaceType.key
            }
        })
        rl_space_type.adapter = spaceTypeAdapter
        rl_space_type.layoutManager = GridLayoutManager(this, 2)
        spaceTypeAdapter.notifyDataSetChanged()


        bt_proceed.setOnClickListener {
            if (selectedSpaceType == null) {
                Toast.makeText(applicationContext, "Please select a space first", Toast.LENGTH_SHORT).show()
            } else if (mainApplication.getCart().getOrderProducts().size > 0) {
                showRemoveCartItemDialog()
            } else {
                navigateToNextActivity()
            }
        }

    }

    private fun showRemoveCartItemDialog() {
        val generalDialogFragment = GeneralDialogFragment.newInstance("Replace cart item?",
                "All the previous items in the cart will be discarded.", object : GeneralDialogFragment.OnDialogFragmentClickListener {
            override fun onPositiveInteraction(dialog: GeneralDialogFragment) {
                mainApplication.clearOrder()
                navigateToNextActivity()
            }

            override fun onNegativeInteraction(dialog: GeneralDialogFragment) {
            }
        })
        generalDialogFragment.show(supportFragmentManager, "dialog")
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this, SpaceBookingActivity::class.java)
        intent.putExtra("type", selectedSpaceType)
        startActivity(intent)
    }

    private fun clearSelection() {
        for (space in spaceManagement!!.spaceTypes) {
            space.isSelected = false
        }
    }

    interface SpaceTypeClick {
        fun onSpaceTypeSelected(spaceType: Config.SpaceType)
    }


}