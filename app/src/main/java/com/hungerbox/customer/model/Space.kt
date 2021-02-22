package com.hungerbox.customer.model


import com.google.gson.annotations.SerializedName

data class Space(
    @SerializedName("active")
    val active: Int? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("category_id")
    val categoryId: Int? = null,
    @SerializedName("category_sort_order")
    val categorySortOrder: Int? = null,
    @SerializedName("container_charges")
    val containerCharges: Double? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("day_of_week")
    val dayOfWeek: Int? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("is_veg")
    val isVeg: Int? = null,
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("location_id")
    val locationId: Int? = null,
    @SerializedName("max_qty")
    val maxQty: Int? = null,
    @SerializedName("menu_id")
    val menuId: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("slot_end_time")
    val slotEndTime: String? = null,
    @SerializedName("slot_id")
    val slotId: Long? = null,
    @SerializedName("slot_start_time")
    val slotStartTime: String? = null,
    @SerializedName("sort_order")
    val sortOrder: Int? = null,
    @SerializedName("vendor_id")
    val vendorId: Long? = null
){
    var isSelected = false
}