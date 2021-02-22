package com.hungerbox.customer.model

class ToastData {

    var message: String
    var type: Int = 0

    constructor(mes: String) {
        this.message = mes
    }

    constructor(mes: String, type: Int){
        this.message = mes
        this.type = type
    }
}