package com.hungerbox.customer.model
import com.google.gson.annotations.SerializedName
import com.hungerbox.customer.model.SlotList

data class SlotListResponse(
        @SerializedName("data")
        val slotLists: List<SlotList>
)
