package com.hungerbox.customer.util

import android.app.Activity
import com.google.gson.JsonSerializer
import com.hungerbox.customer.model.*
import com.hungerbox.customer.network.SimpleHttpAgent
import com.hungerbox.customer.network.UrlConstant
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class BackgroundOrder(private val apiTag:String, private val activity:Activity, private val vendorId : Long, private val occasionId : Long, private val locationId : Long, private val orderPlaceListener: BackgroundOrderPlaceListener) {
    var order : Order = Order()
    init {
        order.vendorId = vendorId
        order.occasionId = occasionId
        order.locationId = locationId
        order.setDeliveryCharge(0.0)
        order.setContainerCharge(0.0)
        order.price = 0.0

    }
    fun getEtaFromServer(etaListener: GetEtaListener){
        //UrlConstant.ORDER_ETA+"?occasion_id="+occasionId+"&location_id="+locationId
        val url = "${UrlConstant.ORDER_ETA}?occasion_id=$occasionId&location_id=$locationId"


        val getEta = SimpleHttpAgent<Eta>(
                activity,
                url,
                { vendorResponse ->
                    vendorResponse.data?.let {
                        val etaString = SimpleDateFormat("hh:mm a").format(Date(it.eta*1000)).toUpperCase()
                        etaListener.onEtaReceived(etaString)
                    }

                },
                { _, error, _ ->
                    etaListener.onError(error)
                },
                Eta::class.java
        )
        getEta.get(apiTag)
    }
    fun getProductFromVendor(withPlaceOrder:Boolean){
        val url = "${UrlConstant.VENDOR_MENU_GET}?vendorId=$vendorId&occasionId=$occasionId&locationId=$locationId"


        val getCategory = SimpleHttpAgent<VendorResponse>(
                activity,
                url,
                { vendorResponse ->
                    if (!vendorResponse?.vendor?.menu?.products.isNullOrEmpty()){
                        val product = vendorResponse?.vendor!!.menu.products[0]
                        if (product!!.freeQuantity>0) {
                            var orderProduct = OrderProduct()
                            orderProduct.copy(product)
                            order.products.add(orderProduct)
                            if (withPlaceOrder) placeOrder()
                        } else{
                            orderPlaceListener.onFreeQuantityExhausted()
                        }
                    }
                },
                { errorCode, error, errorResponse ->
                    orderPlaceListener.onError("error while getting product from vendor")
                },
                VendorResponse::class.java
        )
        getCategory.get(apiTag)
    }
    private fun placeOrder(){
        val url = UrlConstant.POST_ORDER_URL
        val orderSimpleHttpAgent: SimpleHttpAgent<OrderResponse> = SimpleHttpAgent<OrderResponse>(activity, url,
                { responseObject ->
                    orderPlaceListener.onSuccess(responseObject)
                },
                { _, error, _ ->
                    EventUtil.FbEventLog(activity, EventUtil.CART_ORDER_ERROR, EventUtil.SCREEN_CART)
                    orderPlaceListener.onError(error)
                },
            OrderResponse::class.java)
        orderSimpleHttpAgent.post(order, HashMap<String, JsonSerializer<Any>>(), apiTag)
    }
    interface BackgroundOrderPlaceListener{
        fun onSuccess(orderResponse: OrderResponse?)
        fun onError(error: String)
        fun onFreeQuantityExhausted()
    }
    interface GetEtaListener{
        fun onEtaReceived(eta: String)
        fun onError(error: String)
    }
}