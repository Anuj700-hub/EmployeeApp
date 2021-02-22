package com.hungerbox.customer.bluetooth.Model

import kotlin.collections.ArrayList

class ContactTracingData {

    var contacts: ArrayList<UserViolation>? = null
        get() {
            if(contacts == null)
                return ArrayList()
            return contacts
        }
}