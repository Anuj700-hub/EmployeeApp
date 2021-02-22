package com.hungerbox.customer.prelogin.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.hungerbox.customer.MainApplication
import com.hungerbox.customer.R
import com.hungerbox.customer.util.ApplicationConstants
import com.hungerbox.customer.util.SharedPrefUtil

class ManualURLActivity : ParentActivity(){

    private var ed: EditText?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manualurl_activity)
        ed = findViewById(R.id.editText)
    }

    fun qa(view: View) {
        setValues(ed!!.text.toString(),"qa")
    }

    fun rc(view: View) {
        setValues(ed!!.text.toString(),"rc")
    }

    fun rp(view: View) {
        setValues(ed!!.text.toString(),"releaseprod")
    }

    fun setValues(url : String, bt : String){
        SharedPrefUtil.putString(ApplicationConstants.MANUAL_BUILD_TYPE, bt)
        SharedPrefUtil.putString(ApplicationConstants.MANUAL_URL, url)
        val intent = Intent(MainApplication.appContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        MainApplication.appContext.startActivity(intent)
    }
}