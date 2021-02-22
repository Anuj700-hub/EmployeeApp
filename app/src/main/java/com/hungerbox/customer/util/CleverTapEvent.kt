package com.hungerbox.customer.util

import android.content.Context
import com.google.firebase.crashlytics.internal.common.CommonUtils
import com.hungerbox.customer.BuildConfig
import com.hungerbox.customer.hbanalytics.HBAnalytics
import com.hungerbox.customer.hbanalytics.HBAnalyticsConstants
import com.hungerbox.customer.model.User


class CleverTapEvent {

    class ProfileProperties{

        companion object {
            var name: String = "Name"
            var rooted: String = "Rooted"
            var email: String = "Email"
            var mobile: String = "Phone"
            var companyId: String = "Company_Id"
            var userId: String = "User_Id"
            var identity: String = "Identity"
            var is_unified: String = "Is_Unified"
            var api_level : String = "Api_Level"
            var version_code : String = "Version_Code"
            var version_name : String = "Version_Name"
            var os : String = "Os"
        }
    }

    class EventNames{

        companion object {
            @JvmStatic
            var cc_entered: String = "CC_Entered"
            @JvmStatic
            var scan_qr: String = "Scan_QR"
            @JvmStatic
            var location_click : String = "Location_Click"      //Removed
            @JvmStatic
            var ocassion_click : String = "Ocassion_Click"         //Removed
            @JvmStatic
            var search_click : String = "Search_Click"
            @JvmStatic
            var banner_click : String = "Banner_Click"
            @JvmStatic
            var nav_bar_click : String = "Nav_Bar_Click"
            @JvmStatic
            var bookmark_add : String = "BookMark_Click"
            @JvmStatic
            var bookmark_remove : String = "Bookmark_Remove"           //Removed
            @JvmStatic
            var veg_item_click : String = "Veg_Item_Click"
            @JvmStatic
            var vendor_click : String = "Vendor_Click"
            @JvmStatic
            var signin_click : String = "Signin_Click"
            @JvmStatic
            var signup_clicked : String = "Signup_Click"
            @JvmStatic
            var sign_in_with_otp_click : String  = "Sign_In_with_OTP_Click"
            @JvmStatic
            var sso_login_click : String = "SSO_Login_Click"
            @JvmStatic
            var add_item : String = "Add_Item_Click"
            @JvmStatic
            var view_cart_click : String = "View_Cart_Click"
            @JvmStatic
            var view_more_payment_options : String = "View_More_Payment_Options"
            @JvmStatic
            var forgot_password_click : String = "Forgot_Password_Click"
            @JvmStatic
            var my_account_click : String = "My_Account_Click"         //Removed
            @JvmStatic
            var wallet_recharge : String = "wallet_Recharge"           //Removed
            @JvmStatic
            var nav_item_click : String = "Nav_Item_Click"
            @JvmStatic
            var chat : String = "Chat"                                  //Removed
            @JvmStatic
            var location_change : String = "Location_Change"             //Removed
            @JvmStatic
            var ocassion_change : String = "Occasion_Change"  //Removed
            @JvmStatic
            var watermark_calls : String = "Watermark_Calls"
            @JvmStatic
            var link_delink : String = "Link_Delink" //Remove
            @JvmStatic
            var add_card : String = "Add_Card"                 //Removed
            @JvmStatic
            var exp_click : String = "Express_Checkout_Click"
            @JvmStatic
            var feedback_submit : String = "Feedback_Submit"           //Removed
            @JvmStatic
            var place_order : String = "Place_Order"
            @JvmStatic
            var pay_click : String = "Pay_Click"               //Removed
            @JvmStatic
            var setting_change : String = "Setting_Change"           //Removed
            @JvmStatic
            var app_start : String = "App_Start"                //Removed
            @JvmStatic
            var enter_in_pip : String = "Enter_In_Pip"              //Removed
            @JvmStatic
            var shortcut : String = "Shortcut"                 //Removed

            @JvmStatic
            var updatePopupShow : String = "Update_Popup_Show"               //Removed
            @JvmStatic
            var statusPopupShow : String = "Status_Popup_Show"             //Removed
            @JvmStatic
            var installUpdate : String = "InstallUpdate"              //Removed
            @JvmStatic
            var campaign_click : String = "Campaign_Click"              //Removed
            @JvmStatic
            var campaign_list_click : String = "Campaign_List_Click"     //Removed
            @JvmStatic
            var reward_click : String ="Reward_Click"                        //Removed
            @JvmStatic
            var question_click : String ="Question_Click"                  //Removed
            @JvmStatic
            var option_click : String = "Option_Click"                 //Removed
            @JvmStatic
            var answer_submit : String = "Answer_Submit"              //Removed
            @JvmStatic
            var terms_and_condition_click :String ="Terms_And_Condition_Click"        //Removed
            @JvmStatic
            var offline_mode : String ="Offline_Mode"                            //Removed
            @JvmStatic
            var upi_method : String = "Upi_Method"      //Removed
            @JvmStatic
            var qr_error : String = "Qr_error"             //Removed
            @JvmStatic
            var certificate_error : String = "Certificate_error"         //Removed
            @JvmStatic
            var reorder_click : String = "Reorder_Click"              //Removed
            @JvmStatic
            var items_not_available : String="Items_not_available"            //Removed
            @JvmStatic
            var reordering_price_change : String="Reordering_price_change"         //Removed
            @JvmStatic
            var category_click : String = "Category_Click"
            @JvmStatic
            var user_active : String = "User_Active"
            @JvmStatic
            var add_quantity_click : String = "Add_Quantity_Click"
            @JvmStatic
            var cancel_order_click : String = "Cancel_Order_Click"
            @JvmStatic
            var ble_service_stopped : String = "BLE_Service_Stopped"
        }
    }

    class PropertiesNames{

        companion object {
            @JvmStatic
            var source: String = "Source"
            @JvmStatic
            var companyId: String = "Company_Id"
            @JvmStatic
            var userId: String = "User_Id"
            @JvmStatic
            var screen_name : String = "Screen_Name"
            @JvmStatic
            var banner_name : String = "Banner_Name"
            @JvmStatic
            var item_name : String = "Item_Name"
            @JvmStatic
            var item_price : String = "Item_Price"
            @JvmStatic
            var item_category : String = "Item_Category"
            @JvmStatic
            var vendor_name : String = "Vendor_Name"
            @JvmStatic
            var vendor_id : String = "Vendor_Id"
            @JvmStatic
            var action : String = "Action"
            @JvmStatic
            var mode : String = "Mode"
            @JvmStatic
            var ocassion_id : String = "Ocassion_ID"
            @JvmStatic
            var cafeteria_name : String = "Cafeteria_name"
            @JvmStatic
            var signin_type : String = "Signin_Type"
            @JvmStatic
            var is_bookmarked : String = "Is_Bookmarked"
            @JvmStatic
            var is_trending : String = "Is_Recommended"
            @JvmStatic
            var is_customised : String = "Is_Customised"
            @JvmStatic
            var amount : String = "Amount"
            @JvmStatic
            var location_id : String = "Location_ID"
            @JvmStatic
            var nav_item_name : String = "Nav_Item_Name"
            @JvmStatic
            var previous_location : String = "Previous_Location"
            @JvmStatic
            var new_location : String = "New_Location"
            @JvmStatic
            var is_default : String = "Is_Default"
            @JvmStatic
            var sub_items : String = "Customize_Items"
            @JvmStatic
            var is_change : String = "Is_Change"
            @JvmStatic
            var previous_ocassion : String = "Previous_Ocassion"
            @JvmStatic
            var new_occasion : String = "New_Occasion"
            @JvmStatic
            var user_action : String = "Action"
            @JvmStatic
            var payment_method_name : String = "Payment_Method_Name"
            @JvmStatic
            var rating : String = "Rating"
            @JvmStatic
            var order_status : String = "Order_Status"
            @JvmStatic
            var methods_selected : String = "Methods_Selected"
            @JvmStatic
            var default_method : String = "Default_Method"
            @JvmStatic
            var is_method_change : String = "Is_Default_Method_Change"
            @JvmStatic
            var cart_value : String = "Cart_Value"
            @JvmStatic
            var item_name_item_quantity : String = "Item_Name_Item_Quantity"
            @JvmStatic
            var is_company_paid : String = "Is_Company_Paid"
            @JvmStatic
            var special_instruction : String = "Special_Instruction"
            @JvmStatic
            var setting_name : String = "Setting_Name"
            @JvmStatic
            var campaign_id : String = "Campaign_Id"
            @JvmStatic
            var option_name : String = "Option_Name"
            @JvmStatic
            var answer : String = "Answer"
            @JvmStatic
            var question_id : String = "Question_Id"
            @JvmStatic
            var answer_id : String ="Answer_Id"
            @JvmStatic
            var status : String = "Status"
            @JvmStatic
            var upi_app_name : String ="Upi_App_Name"
            @JvmStatic
            var message : String ="message"
            @JvmStatic
            var reordering : String="reordering"
            @JvmStatic
            var orderId : String="Order_Id"
            @JvmStatic
            var menu_item_id:String = "Menu_Item_Id"
            @JvmStatic
            var os : String = "Os"
            @JvmStatic
            var version_name : String = "Version_Name"
            @JvmStatic
            var is_desk_order : String = "Is_Desk_Order"
            @JvmStatic
            var offline_calls : String="watermark_offline_calls"
            @JvmStatic
            var online_calls : String="watermark_online_calls"
            @JvmStatic
            var ble_service_stopped_reason : String ="Ble_Service_Stopped_Reason"
        }
    }

    companion object {

        @JvmStatic
        fun pushUserProfile(user: User, context: Context) {

            try {
                if(!AppUtils.getConfig(context).isIs_analytics_enabled)
                    return

                SharedPrefUtil.putString("name", user.name)
                SharedPrefUtil.putString("email", user.empEmail)
                SharedPrefUtil.putString("phone", user.phoneNumber)
                SharedPrefUtil.putString("c_id", user.companyId.toString())
                SharedPrefUtil.putLong("user_id", user.id)
                SharedPrefUtil.putLong("identity", user.id)
                if (BuildConfig.FLAVOR.equals("common"))
                    SharedPrefUtil.putBoolean("is_unified", true)
                else
                    SharedPrefUtil.putBoolean("is_unified", false)



                val profileUpdate = HashMap<String, Any>()
                profileUpdate.put(ProfileProperties.name, user.name)
                profileUpdate.put(ProfileProperties.email, user.empEmail)
                profileUpdate.put(ProfileProperties.mobile, AppUtils.getConfig(context).country_code +""+user.phoneNumber)
                profileUpdate.put(ProfileProperties.companyId, user.companyId.toString())
                profileUpdate.put(ProfileProperties.userId, user.id.toString())
                profileUpdate.put(ProfileProperties.identity, user.id)
                profileUpdate.put(ProfileProperties.version_code, BuildConfig.VERSION_CODE)
                profileUpdate.put(ProfileProperties.version_name,AppUtils.getVersionName())

                if (BuildConfig.FLAVOR.equals("common")) {
                    profileUpdate.put(ProfileProperties.is_unified, true)
                } else {
                    profileUpdate.put(ProfileProperties.is_unified, false)
                }

                profileUpdate.put(ProfileProperties.os, "Android")
                profileUpdate.put(ProfileProperties.api_level, android.os.Build.VERSION.SDK_INT)
                try{
                    if(CommonUtils.isRooted(context)){
                        profileUpdate.put(ProfileProperties.rooted, true)
                    }else{
                        profileUpdate.put(ProfileProperties.rooted, false)
                    }
                }catch (exp : Exception){
                    exp.printStackTrace()
                }
                HBAnalytics.pushUserProfile(profileUpdate,context,HBAnalyticsConstants.FIREBASE)
            }catch (exp: Exception){
                exp.printStackTrace()
            }
        }

        @JvmStatic
        fun pushUserEvents(name: String ,map: HashMap<String, Any>?, context:Context) {
            try{
                if(!AppUtils.getConfig(context).isIs_analytics_enabled)
                    return

                var newMap = HashMap<String, Any>()
                if(map != null){
                    newMap.putAll(map)
                }


//                newMap.put(ProfileProperties.name, SharedPrefUtil.getString("name",""))
//                newMap.put(ProfileProperties.email, SharedPrefUtil.getString("email",""))
//                newMap.put(ProfileProperties.mobile, AppUtils.getConfig(context).country_code +""+SharedPrefUtil.getString("phone",""))
//                newMap.put(ProfileProperties.companyId, SharedPrefUtil.getString("c_id",""))
//                newMap.put(ProfileProperties.userId, SharedPrefUtil.getLong("user_id",0).toString())
//                newMap.put(ProfileProperties.identity, SharedPrefUtil.getLong("identity",0))
//                newMap.put(ProfileProperties.is_unified, SharedPrefUtil.getBoolean("is_unified",false))
//                newMap.put(PropertiesNames.location_id, SharedPrefUtil.getLong("location_id",0).toString())
//                newMap.put(ProfileProperties.os, "Android")

//                try{
//                    if(CommonUtils.isRooted(context)){
//                        newMap.put(ProfileProperties.rooted, true)
//                    }else{
//                        newMap.put(ProfileProperties.rooted, false)
//                    }
//                }catch (exp : Exception){
//                    exp.printStackTrace()
//                }
//
//                try{
//                    newMap.put(ProfileProperties.api_level, android.os.Build.VERSION.SDK_INT)
//                }catch (exp : Exception){
//                    exp.printStackTrace()
//                }
//                try{
//                    newMap.put(ProfileProperties.version_code, BuildConfig.VERSION_CODE)
//                    newMap.put(ProfileProperties.version_name, BuildConfig.VERSION_NAME)
//                }catch (exp : Exception){
//                    exp.printStackTrace()
//                }

                HBAnalytics.pushUserEvent(newMap,name,context,HBAnalyticsConstants.FIREBASE,null)
            }catch (exp:Exception){
                exp.printStackTrace()
            }
        }

        @JvmStatic
        fun pushFCMEvents(fcmId: String? , context:Context) {
            try{
                if(!AppUtils.getConfig(context).isIs_analytics_enabled)
                    return

                HBAnalytics.pushFcmEvents(fcmId, context, HBAnalyticsConstants.FIREBASE)
            }catch (exp:Exception){
                exp.printStackTrace()
            }
        }
    }
}