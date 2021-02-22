package com.hungerbox.customer.mvvm.listeners

import com.hungerbox.customer.model.UserReposne

interface UserDetailResponseListener {
    fun onSuccess(userResponse : UserReposne?)
    fun onError()
}