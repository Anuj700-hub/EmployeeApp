package com.hungerbox.customer.util

import com.hungerbox.customer.model.Product

class ListToMapUtil {
    fun convertToMap(list : List<Product>): Map<Long, Product> {
        val myMap = list.map { it.id to it }.toMap()

        return myMap
    }
}