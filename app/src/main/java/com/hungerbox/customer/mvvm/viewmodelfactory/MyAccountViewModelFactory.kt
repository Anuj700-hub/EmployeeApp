package com.hungerbox.customer.mvvm.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hungerbox.customer.mvvm.repository.MyAccountRepository
import com.hungerbox.customer.mvvm.util.MyAccountUtil
import com.hungerbox.customer.mvvm.viewmodel.MYAccountViewModel

class MyAccountViewModelFactory(private val repository: MyAccountRepository,private val util:MyAccountUtil) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MYAccountViewModel::class.java)) {
            return MYAccountViewModel(repository,util) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}