package com.hungerbox.customer.model

data class SlotList(val date: String,
                    val day_of_week: Int,
                    val location_id: Int,
                    val slot_end_time: String,
                    val slot_id: Long,
                    val slot_start_time: String)