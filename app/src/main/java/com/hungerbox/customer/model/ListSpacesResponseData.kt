package com.hungerbox.customer.model


import com.google.gson.annotations.SerializedName

data class ListSpacesResponseData(
    @SerializedName("active")
    val active: Int? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("beneficiary_bank_acc_num")
    val beneficiaryBankAccNum: Long? = null,
    @SerializedName("beneficiary_bank_branch")
    val beneficiaryBankBranch: String? = null,
    @SerializedName("beneficiary_bank_ifsc")
    val beneficiaryBankIfsc: String? = null,
    @SerializedName("beneficiary_bank_name")
    val beneficiaryBankName: String? = null,
    @SerializedName("beneficiary_name")
    val beneficiaryName: String? = null,
    @SerializedName("billing_cycle")
    val billingCycle: String? = null,
    @SerializedName("billing_start_date")
    val billingStartDate: String? = null,
    @SerializedName("cgst")
    val cgst: Double? = null,
    @SerializedName("checklist_approved")
    val checklistApproved: Int? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("commission_on_mrp_food_value")
    val commissionOnMrpFoodValue: Double? = null,
    @SerializedName("commission_on_mrp_sale")
    val commissionOnMrpSale: Double? = null,
    @SerializedName("commission_on_non_mrp_food_value")
    val commissionOnNonMrpFoodValue: Double? = null,
    @SerializedName("commission_on_non_mrp_sale")
    val commissionOnNonMrpSale: Double? = null,
    @SerializedName("commission_on_total_value")
    val commissionOnTotalValue: Double? = null,
    @SerializedName("commission_type")
    val commissionType: String? = null,
    @SerializedName("commission_value")
    val commissionValue: Double? = null,
    @SerializedName("company_id")
    val companyId: Int? = null,
    @SerializedName("container_charges")
    val containerCharges: Any? = null,
    @SerializedName("couch_locations")
    val couchLocations: Any? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("current_item_no")
    val currentItemNo: Int? = null,
    @SerializedName("customer_gst")
    val customerGst: Double? = null,
    @SerializedName("deliver_in_lunch")
    val deliverInLunch: Int? = null,
    @SerializedName("delivery_charges")
    val deliveryCharges: Any? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("desk_ordering_enabled")
    val deskOrderingEnabled: Int? = null,
    @SerializedName("display_name")
    val displayName: String? = null,
    @SerializedName("document_approved")
    val documentApproved: Int? = null,
    @SerializedName("ecafe")
    val ecafe: Int? = null,
    @SerializedName("ecatering")
    val ecatering: Int? = null,
    @SerializedName("efood_court")
    val efoodCourt: Int? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("eservices")
    val eservices: Int? = null,
    @SerializedName("exclude_gst_for_commission")
    val excludeGstForCommission: Int? = null,
    @SerializedName("exclude_gst_percentage")
    val excludeGstPercentage: Double? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("info")
    val info: Any? = null,
    @SerializedName("is_billable")
    val isBillable: Int? = null,
    @SerializedName("is_buffet")
    val isBuffet: Int? = null,
    @SerializedName("logo_download_id")
    val logoDownloadId: Any? = null,
    @SerializedName("lookup_type")
    val lookupType: String? = null,
    @SerializedName("master_vendor_id")
    val masterVendorId: Int? = null,
    @SerializedName("merchant_name")
    val merchantName: Any? = null,
    @SerializedName("merchant_vendor_ref")
    val merchantVendorRef: Any? = null,
    @SerializedName("mid")
    val mid: Any? = null,
    @SerializedName("min_order_amount")
    val minOrderAmount: Any? = null,
    @SerializedName("mobile_number")
    val mobileNumber: String? = null,
    @SerializedName("next_billing_date")
    val nextBillingDate: String? = null,
    @SerializedName("online_charges")
    val onlineCharges: Double? = null,
    @SerializedName("ordering_enabled")
    val orderingEnabled: Boolean? = null,
    @SerializedName("payment_from")
    val paymentFrom: String? = null,
    @SerializedName("restaurant_id")
    val restaurantId: Any? = null,
    @SerializedName("sdk_type")
    val sdkType: Any? = null,
    @SerializedName("search_keywords")
    val searchKeywords: String? = null,
    @SerializedName("service_tax")
    val serviceTax: Double? = null,
    @SerializedName("sgst")
    val sgst: Double? = null,
    @SerializedName("sodexo_commission_percentage")
    val sodexoCommissionPercentage: Double? = null,
    @SerializedName("sodexo_payable_percentage")
    val sodexoPayablePercentage: Double? = null,
    @SerializedName("sort_order")
    val sortOrder: Int? = null,
    @SerializedName("spaces")
    val spaces: List<Space>? = null,
    @SerializedName("state_gst_id")
    val stateGstId: Int? = null,
    @SerializedName("state_id")
    val stateId: Any? = null,
    @SerializedName("take_away_available")
    val takeAwayAvailable: Int? = null,
    @SerializedName("telephone")
    val telephone: Any? = null,
    @SerializedName("test")
    val test: Int? = null,
    @SerializedName("tid")
    val tid: Any? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("vat")
    val vat: Double? = null,
    @SerializedName("vendor_name")
    val vendorName: String? = null,
    @SerializedName("vendor_ordering_enabled")
    val vendorOrderingEnabled: Int? = null
)