package com.hungerbox.customer.network

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.hungerbox.customer.MainApplication

object VolleyRequestFactory {

    var mRequestQueue: RequestQueue = Volley.newRequestQueue(MainApplication.appContext, 50 * 1024 * 1024)
}