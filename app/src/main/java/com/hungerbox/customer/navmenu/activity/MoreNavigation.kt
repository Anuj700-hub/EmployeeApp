package com.hungerbox.customer.navmenu.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.hungerbox.customer.R
import com.hungerbox.customer.model.NavigationItem
import com.hungerbox.customer.mvvm.view.MyAccountActivity
import com.hungerbox.customer.navmenu.adapter.BottomNavigationBarAdapter
import com.hungerbox.customer.navmenu.adapter.MoreNavigationAdapter
import com.hungerbox.customer.prelogin.activity.HBWelcomeActivity
import com.hungerbox.customer.util.AppUtils
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.SharedPrefUtil
import com.hungerbox.customer.util.view.GenericPopUpFragment
import kotlinx.android.synthetic.main.activity_more_navigation.*

class MoreNavigation : AppCompatActivity() {

    lateinit var bottomNavBar : LinearLayout
    lateinit var bottomNavigationBarAdapter : BottomNavigationBarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_navigation)
        bottomNavBar = findViewById(R.id.ll_bottom_nav)
        bottomNavigationBarAdapter = BottomNavigationBarAdapter(this, bottomNavBar)
        rv_activity_list.adapter = MoreNavigationAdapter(this, bottomNavigationBarAdapter.navItemsMore as java.util.ArrayList<NavigationItem>?)
        rv_activity_list.layoutManager = LinearLayoutManager(this)
        setAccountListeners()
        cl_parent.setOnClickListener{
            finish()
        }

    }
    fun setAccountListeners(){
        tv_account.setOnClickListener{
            var intent = Intent(this, MyAccountActivity::class.java)
            startActivity(intent)
            finish()
        }
        var userName = SharedPrefUtil.getString(ApplicationConstants.PREF_NAME, "")
        tv_username.text = userName
    }

    fun showLogoutPopUp() {
        val message = "Are you sure you want to Logout?"
        val fragment = GenericPopUpFragment.newInstance(message, "Yes",
                object : GenericPopUpFragment.OnFragmentInteractionListener {
                    override fun onPositiveInteraction() {
                        AppUtils.doLogoutServer(getApplicationContext())
                        SharedPrefUtil.remove(ApplicationConstants.PREF_AUTH_TOKEN)
                        SharedPrefUtil.remove(ApplicationConstants.PREF_USER_ID)
                        val intent = Intent(this@MoreNavigation, HBWelcomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    override fun onNegativeInteraction() {

                    }
                })
        fragment.isCancelable = true
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
                .add(fragment, "logout")
                .commit()
    }

}
