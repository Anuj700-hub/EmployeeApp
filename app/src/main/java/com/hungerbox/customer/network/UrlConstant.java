package com.hungerbox.customer.network;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

/**
 * Created by peeyushpathak on 13/06/16.
 */
public class UrlConstant {

    public static final String buildType = BuildConfig.BUILD_TYPE;
    public static String PAYMENT_RESPONSE_REGEX = "\\w+:\\/\\/\\w+\\.hungerbox\\.com\\/cafe.*";
    public static String PAYMENT_RESPONSE_REGEX_SUCCESS = "\\w+:\\/\\/\\w+\\.hungerbox\\.com\\/cafe\\/success.*";
    public static String PAYMENT_RESPONSE_REGEX_FAILURE = "\\w+:\\/\\/\\w+\\.hungerbox\\.com\\/cafe\\/failure.*";

    private static String targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"");

    static {

        if (targetEnv.equalsIgnoreCase("releaseprod") || buildType.equalsIgnoreCase("releaseprod")) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("fs"))
                BASE_URL = "https://firstsource.hungerbox.com/";
            else
                BASE_URL = "https://rest.hungerbox.com/";
            ABOUT_US = "https://firstsource.hungerbox.com/views/about-us.html";
            FAQ = "https://firstsource.hungerbox.com/views/faqs.html";
            TERMS = "https://firstsource.hungerbox.com/views/term.html";
            CONTACT_US = "https://firstsource.hungerbox.com/views/contact-us.html";
            CLIENT_ID = "7da036ae8f2ff72ca6bf2a6e839db937";
            CLIENT_SECRET = "cadf4ac9c6d882ea919c89dbefdfc3d8";
            JUSPAY_MERCHANT_ID = "hungerbox";
            POCKETS_SDK_CLIENT_ID = "d7as3672kfh9872397492jdsak2c2e21d51c5140";
            POCKETS_SDK_MERCHANT_ID = "MID002";
            POCKETS_SDK_MERCHANT_NO = "1002";

            SERVER_PUB_KEY = "MEwwDQYJKoZIhvcNAQEBBQADOwAwOAIxANYu1oG2lB4Zvr2CbQWhgRzBZWBRBshZ\n" +
                    "+++PJUhihJOphXyQneywRZhICcpQTedR5wIDAQAB";

            CHECK_SERVER_STATUS = "https://s3.ap-south-1.amazonaws.com/hb-offline-api-data-aps1/prod/server-status";

        }else if (targetEnv.equalsIgnoreCase("rc") || buildType.equalsIgnoreCase("rc")) {
            BASE_URL = "https://rest.rc.hungerbox.com/";
            ABOUT_US = "https://rest.rc.hungerbox.com/cafe/views/about-us.html";
            FAQ = "https://rest.rc.hungerbox.com/cafe/views/faqs.html";
            TERMS = "https://rest.rc.hungerbox.com/cafe/views/term.html";
            CONTACT_US = "https://rest.rc.hungerbox.com/cafe/views/contact-us.html";
            CLIENT_ID = "7da036ae8f2ff72ca6bf2a6e839db937";
            CLIENT_SECRET = "cadf4ac9c6d882ea919c89dbefdfc3d8";
            JUSPAY_MERCHANT_ID = "hungerbox";
            POCKETS_SDK_CLIENT_ID = "d7as3672kfh9872397492jdsak2c2e21d51c5140";
            POCKETS_SDK_MERCHANT_ID = "MID002";
            POCKETS_SDK_MERCHANT_NO = "1002";


            PAYMENT_RESPONSE_REGEX = "\\w+:\\/\\/\\w+\\.rc.hungerbox\\.com\\/cafe.*";
            PAYMENT_RESPONSE_REGEX_SUCCESS = "\\w+:\\/\\/\\w+\\.rc.hungerbox\\.com\\/cafe\\/success.*";
            PAYMENT_RESPONSE_REGEX_FAILURE = "\\w+:\\/\\/\\w+\\.rc.hungerbox\\.com\\/cafe\\/failure.*";

            SERVER_PUB_KEY = "MEwwDQYJKoZIhvcNAQEBBQADOwAwOAIxAOYaIpWYEbAji8H9cJ+Ff6G6vgopxttv\n" +
                    "ZhgO1ayrpmxlI1xwloBMlYGjTAOnLIWzaQIDAQAB";

            CHECK_SERVER_STATUS = "https://s3.ap-south-1.amazonaws.com/hb-offline-api-data-1/rc/server-status";

        } else if (targetEnv.equalsIgnoreCase("qa") || buildType.equalsIgnoreCase("qa")) {
            BASE_URL = "https://rest.qa.hungerbox.com/";
            ABOUT_US = "https://rest.qa.hungerbox.com/cafe/views/about-us.html";
            FAQ = "https://rest.qa.hungerbox.com/cafe/views/faqs.html";
            TERMS = "https://rest.qa.hungerbox.com/cafe/views/term.html";
            CONTACT_US = "https://rest.qa.hungerbox.com/cafe/views/contact-us.html";
            CLIENT_ID = "7da036ae8f2ff72ca6bf2a6e839db937";
            CLIENT_SECRET = "cadf4ac9c6d882ea919c89dbefdfc3d8";
            JUSPAY_MERCHANT_ID = "hb";
            POCKETS_SDK_CLIENT_ID = "d7as3672kfh9872397492jdsak2c2e21d51c5140";
            POCKETS_SDK_MERCHANT_ID = "MID002";
            POCKETS_SDK_MERCHANT_NO = "1002";

            PAYMENT_RESPONSE_REGEX = "\\w+:\\/\\/\\w+\\.qa.hungerbox\\.com\\/cafe.*";
            PAYMENT_RESPONSE_REGEX_SUCCESS = "\\w+:\\/\\/\\w+\\.qa.hungerbox\\.com\\/cafe\\/success.*";
            PAYMENT_RESPONSE_REGEX_FAILURE = "\\w+:\\/\\/\\w+\\.qa.hungerbox\\.com\\/cafe\\/failure.*";
            SERVER_PUB_KEY = "MEwwDQYJKoZIhvcNAQEBBQADOwAwOAIxAOYaIpWYEbAji8H9cJ+Ff6G6vgopxttv\n" +
                    "ZhgO1ayrpmxlI1xwloBMlYGjTAOnLIWzaQIDAQAB";

            CHECK_SERVER_STATUS = "https://s3.ap-south-1.amazonaws.com/hb-offline-api-data-1/qa/server-status";

        }else {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("fs"))
                BASE_URL = "https://firstsource.hungerbox.com/";
            else
                BASE_URL = "https://rest.hungerbox.com/";
            ABOUT_US = "https://firstsource.hungerbox.com/views/about-us.html";
            FAQ = "https://firstsource.hungerbox.com/views/faqs.html";
            TERMS = "https://firstsource.hungerbox.com/views/term.html";
            CONTACT_US = "https://firstsource.hungerbox.com/views/contact-us.html";
            CLIENT_ID = "7da036ae8f2ff72ca6bf2a6e839db937";
            CLIENT_SECRET = "cadf4ac9c6d882ea919c89dbefdfc3d8";
            JUSPAY_MERCHANT_ID = "hungerbox";
            POCKETS_SDK_CLIENT_ID = "d7as3672kfh9872397492jdsak2c2e21d51c5140";
            POCKETS_SDK_MERCHANT_ID = "MID002";
            POCKETS_SDK_MERCHANT_NO = "1002";

            SERVER_PUB_KEY = "MEwwDQYJKoZIhvcNAQEBBQADOwAwOAIxANYu1oG2lB4Zvr2CbQWhgRzBZWBRBshZ\n" +
                    "+++PJUhihJOphXyQneywRZhICcpQTedR5wIDAQAB";

            CHECK_SERVER_STATUS = "https://s3.ap-south-1.amazonaws.com/hb-offline-api-data-aps1/prod/server-status";
        }


        if(!SharedPrefUtil.getString(ApplicationConstants.MANUAL_URL, "").equalsIgnoreCase("")){
            BASE_URL = SharedPrefUtil.getString(ApplicationConstants.MANUAL_URL, "");
            String url = SharedPrefUtil.getString(ApplicationConstants.MANUAL_URL,"");
            if(targetEnv.equalsIgnoreCase("rc")){
                ABOUT_US = url+"cafe/views/about-us.html";
                FAQ = url+"cafe/views/faqs.html";
                TERMS = url+"cafe/views/term.html";
                CONTACT_US = url+"cafe/views/contact-us.html";
            }else if (targetEnv.equalsIgnoreCase("qa")){
                ABOUT_US = url+"cafe/views/about-us.html";
                FAQ = url+"cafe/views/faqs.html";
                TERMS = url+"cafe/views/term.html";
                CONTACT_US = url+"cafe/views/contact-us.html";
            }else if (targetEnv.equalsIgnoreCase("releaseprod")){
                ABOUT_US = "https://firstsource.hungerbox.com/views/about-us.html";
                FAQ = "https://firstsource.hungerbox.com/views/faqs.html";
                TERMS = "https://firstsource.hungerbox.com/views/term.html";
            }
        }
    }

    public static  String BASE_URL;
    public static  String ABOUT_US;
    public static  String FAQ;
    public static  String CONTACT_US;
    public static  String TERMS;
    public static final String CLIENT_ID;
    public static final String CLIENT_SECRET;
    public static final String JUSPAY_MERCHANT_ID;
    public static final String POCKETS_SDK_CLIENT_ID;
    public static final String POCKETS_SDK_MERCHANT_ID;
    public static final String POCKETS_SDK_MERCHANT_NO;
    public static final String SERVER_PUB_KEY;
    public static final String LOGIN_URL = BASE_URL + "api/oauth/access_token";
    public static final String GET_VENDOR_LIST = BASE_URL + "api/listVendors";
    public static final String VENDOR_MENU_GET = BASE_URL + "api/listMenu";
    public static final String USER_DETAIL = BASE_URL + "api/current_user";
    public static final String LAST_ORDER = BASE_URL + "api/order/list/";
    public static final String LAST_ARCHIVED_ORDER = BASE_URL + "api/archived_order/list/";
    public static final String ORDER_DETAIL = BASE_URL + "api/order/detail/";
    public static final String ARCHIVED_ORDER_DETAIL = BASE_URL + "api/archived_order/detail/";
    public static final String CONFIG_URL_GET = BASE_URL + "api/config-client-file/";
    public static final String COMMON_COMPANY_ID_GET = BASE_URL + "api/validate-company-code/";
    public static final String COMPANY_LOCATION_GET = BASE_URL + "api/company/";
    public static final String LOCATION_UPDATE = BASE_URL + "api/user/set_default_location";
    public static final String RECHARGE_URL = BASE_URL + "api/user/recharge";
    public static final String PAYMENT_STATUS = BASE_URL + "api/payment/details/";
    public static final String CHANGE_PASSWORD = BASE_URL + "api/password/change";
    public static final String CHANGE_EMPLOYEE_DETAILS = BASE_URL + "api/employees/";
    public static final String FEEDBACK_API = BASE_URL + "api/order/review/";
    public static final String FEEDBACK_REASON_API = BASE_URL + "api/order/review/options/";
    public static final String WALLET_HISTORY = BASE_URL + "api/wallet-history/";
    public static final String ARCHIVED_WALLET_HISTORY = BASE_URL + "api/archived-wallet-history/";
    public static final String RESET_PASSWORD = BASE_URL + "api/reset-password";
    public static final String DEVICE_REGISTER = BASE_URL + "api/firebase-device";
    public static final String LOGOUT = BASE_URL + "api/logout";
    public static final String OCCASION_LIST = BASE_URL + "api/listOccasions";
    public static final String USER_SETTINGS = BASE_URL + "api/users/getEmployeeDetails";
    public static final String SET_USER_SETTINGS = BASE_URL + "api/users/setEmployeeDetails";
    public static final String SEND_OTP = BASE_URL + "api/reset-request-otp";
    public static final String OTP_RESET_PASSWORD = BASE_URL + "api/reset-password/using-otp";
    public static final String SIGN_UP_BASIC = BASE_URL + "api/customer/request-registration";
    public static final String SIGN_UP_OTP_VALIDATE = BASE_URL + "api/customer/validate-otp-create-user";
    public static final String REGISTRATION_RESEND_OTP = BASE_URL + "api/customer/registration-resend-otp";
    public static final String USER_HEALTH = BASE_URL + "api/user-health";
    public static final String POST_ORDER_URL = BASE_URL + "api/order_place/";
    public static final String EVENT_API = BASE_URL + "api/cafe-tab/update";
    public static final String VALIDATE_OTP = BASE_URL + "api/validate-reset-otp";
    public static final String ADD_GUEST = BASE_URL + "api/guest/create-by-employee";
    public static final String GUEST_LIST = BASE_URL + "api/getGuestList";
    public static final String GUEST_MENU = BASE_URL + "api/guestMenu";
    public static final String GUEST_ORDER = BASE_URL + "api/guest_order_place";
    public static final String EVENTS_WEB_VIEW = "https://{1}.hungerbox.com/#/events";
    public static final String MEETINGS_WEB_VIEW = "https://{1}.hungerbox.com/#/meeting-rooms";
    public static final String BOOKING_DETAIL = BASE_URL + "api/booking-details/";
    public static final String BOOKING_DETAIL_SLOT_CANCEL = BASE_URL + "api/cancel-meeting-slot/";
    public static final String MEETINGS_HISTORY_WEB_VIEW = "https://{1}.hungerbox.com/#/booked-meeting-rooms";
    public static final String BOOKED_MEETINGS_HISTORY = BASE_URL + "api/booking-rooms-history";
    public static final String BOOKED_EVENTS_HISTORY = BASE_URL + "api/event-booking-history";
    public static final String SIGN_UP_SIMPLE_OTP_VALIDATION = BASE_URL + "api/validate-otp/simpl/register";
    public static final String SIGN_UP_SIMPL = BASE_URL + "api/initiate/simpl/register";
    public static final String SIMPL_INITIATE = BASE_URL + "api/simpl/access-token/initiate";
    public static final String SIMPL_VALIDATE = BASE_URL + "api/simpl/access-token/validate-otp";
    public static final String WALLET_OTP_SIGN_UP = BASE_URL + "api/registration/wallet/initiate";
    public static final String WALLET_VALIDATE_NEW = BASE_URL + "api/registration/wallet/validate-otp";
    public static final String WALLET_LIST = BASE_URL + "api/wallet-list";
    public static final String WALLET_LIST_V2 = BASE_URL + "api/v2/wallet-list";
    public static final String SIMPL_TRANSACTION_VALIDATION = BASE_URL + "api/balance-external-wallet";
    public static final String ZETA_TRANSACTION_VALIDATION = BASE_URL + "api/pay/zeta";
    public static final String WEB_PAYMENT_REJECTED_BACK = BASE_URL + "api/cancel-order-payment?orderId=";
    public static final String USER_APP_REVIEW = BASE_URL + "api/user-review";
    public static final String WALLETS = BASE_URL + "api/companies/{company_id}/wallets";
    public static final String ITEM_INTAKE = BASE_URL + "api/items-intake?date=";
    public static final String CALORIE_EATEN = BASE_URL + "api/calorie-eaten";
    public static final String CALORIE_UPDATE = BASE_URL + "api/items-update";
    public static final String SEARCH_ITEM = BASE_URL + "api/nutrition/search?text=";
    public static final String HEALTH_DEVICE = BASE_URL + "api/health-device";
    public static final String HEALTH_HISTORY = BASE_URL + "api/list-calorie-data";
    public static final String WEIGHT_TRACKER = BASE_URL + "api/weight-tracker";
    public static final String FITBIT_REGISTER_DEVICE_AUTH = BASE_URL + "api/register_device/fitbit";
    public static final String FITBIT_REGISTER_DEVICE = BASE_URL + "api/health-device";
    public static final String EVENTS_BOOKING_CANCEL = BASE_URL + "api/cancel-booking/";
    public static final String GET_BANNER_API = BASE_URL + "api/getHomeBanners";
    public static final String GET_HOME_BANNER_API = BASE_URL + "api/getBannersForHome";
    public static final String GET_HOME_BANNER_API_V2 = BASE_URL + "api/v2/getBannersForHome";
    public static final String GET_RECENT_ORDERS = BASE_URL + "api/recent_orders";
    public static final String GET_OFFER_BANNER = BASE_URL + "api/getContestBanners";
    public static final String GET_MATCH_SCORE = BASE_URL + "api/getMatchScore?match_id=";
    public static final String GET_USER_POINTS = BASE_URL + "api/getContestPoints";
    public static final String GET_CONTEST_MENU = BASE_URL + "api/getPromotionMenuList";
    public static final String ZETA_APP_TO_APP_TRANSACTION = BASE_URL + "api/zeta-app/charge-txn";
    public static final String JUSPAY_OTP_INIT = BASE_URL + "api/juspay-wallet/initiate-linking";
    public static final String JUSPAY_OTP_VALIDATE = BASE_URL + "api/juspay-wallet/validate-linking";
    public static final String JUSPAY_FETCH_NETBANKING_OPTIONS = BASE_URL + "api/listNetbanking";
    public static final String JUSPAY_WALLET_RECHARGE = BASE_URL + "api/juspay-wallet/recharge";
    public static final String JUSPAY_ADD_CARD = BASE_URL + "api/juspay-card/add";
    public static final String JUSPAY_DELINK_WALLET = BASE_URL + "api/delink-wallet/";
    public static final String JUSPAY_DELETE_SAVED_CARD = BASE_URL + "api/delete-saved-card/";
    public static final String DELINK_PAYMENT_SOURCE = BASE_URL + "api/delink-internal-wallet/";
    public static final String DELINK_HEALTH_TRACKER = BASE_URL + "api/delink-health-device";
    public static final String GET_HELP_API = BASE_URL + "api/support/getQuestions/help/";
    public static final String SSO_LOGIN = BASE_URL + "api/sso/redirect/{cid}";

    public static final String ORDER_CANCELLATION_ELIGIBILITY = BASE_URL + "api/order/cancellation-eligibility";
    public static final String ORDER_LIST_CANCELLATION_ELIGIBILITY = BASE_URL + "api/order-list/cancellation-eligibility";
    public static final String ORDER_CANCELLATION = BASE_URL + "api/external/order-status-update";

    public static final String BOOKMARK_FETCH = BASE_URL + "api/menu/user/bookmarks/fetch";
    public static final String BOOKMARK_ADD = BASE_URL + "api/menu/user/bookmarks/add";
    public static final String BOOKMARK_DELETE = BASE_URL + "api/menu/user/bookmarks/delete";

    public static final String UPDATE_EXTERNAL_PAYMENT = BASE_URL + "api/update-external-payment/";
    public static final String IS_EMAIL_VALID = BASE_URL + "api/isEmailVerified?request_id=";
    public static final String RESEND_EMAIL_VERIFICATION_TOKEN = BASE_URL+ "api/resendEmailVerificationToken/";

    public static final String EXPRESS_CHECKOUT_WALLET = BASE_URL + "api/v2/express-checkout-wallet-list";
    public static final String ADD_USER_IN_GROUP = BASE_URL+"api/users/allowed-in-group-order";
    public static final String WALLET_LIST_FILTER = BASE_URL + "api/wallet-filter-list";

    public static final String CAMPAIGN_LIST = BASE_URL + "api/list-campaigns-new/";
    public static final String CAMPAIGN_DETAIL = BASE_URL + "api/campaign-details-new/";
    public static final String CAMPAIGN_SUBMIT_ANSWER = BASE_URL + "api/submit-answer-new/";
    public static final String CAMPAIGN_REWARD = BASE_URL + "api/campaign-rewards-new";
    public static final String UNLOCK_REWARD = BASE_URL + "api/unlock-reward";
    public static final String KEY_REFRESH = BASE_URL + "api/sign-public-key";

    //KIOSK
    public static final String SEND_EMAIL_FOR_VERIFICATION = BASE_URL + "api/users/setEmployeeDetails";
    public static final String GENERATE_CHALLENGE = BASE_URL + "api/v3/guest_ordering/generateChallenge?hb_device_id=";
    public static final String GUEST_ACCESS_TOKEN = BASE_URL + "api/v3/oauth/access_token";
    public static final String EXTERNAL_PAYMENT_STATUS = BASE_URL + "api/check-external-payment-status/";
    public static final String CANCEL_EXTERNAL_PAYMENT = BASE_URL + "api/cancel-external-payment/";
    public static final String SEND_ORDER_DETAIL = BASE_URL +"api/order/update-contact-info";

    public static final String PAYTM_DIRECT_RECHARGE = BASE_URL + "api/paytm-direct/recharge?";

    //Offline
    public static final String CHECK_SERVER_STATUS;
    public static final String CHECK_POSTPAID_DUES = BASE_URL + "api/bill/dues";
    public static final String GET_ORDER_ID = BASE_URL +"api/bill/initiate-payment";

    //BB-Instant
    public static final String BIG_BASKET = BASE_URL + "api/bb-instant/generate-session";
    public static final String BB_MINIMUM_BALANCE = BASE_URL + "api/bb-instant/minimum-balance";

    public static final String SERVER_TIME = BASE_URL+"now";


    //Watermark
    public static final String WATERMARK_ALL = BASE_URL + "api/v3/watermark/all";
    public static final String CURRENT_USER_V3 = BASE_URL + "api/v3/consumer/current-user";
    public static final String OCCASION_LIST_V3 = BASE_URL + "api/v3/consumer/list-occasions";
    public static final String COMPANY_LOCATION_GET_V3 = BASE_URL + "api/v3/consumer/company/";
    public static final String LIST_VENDORS_V3 = BASE_URL + "api/v3/consumer/list-vendors";
    public static final String CURRENT_VENDORS_V3 = BASE_URL + "api/v3/consumer/current-vendors";
    public static final String ENTER_EXIT_URL = BASE_URL + "api/cafe/";


    //Social Distancing
    public static final String ORDER_ETA = BASE_URL+"api/v3/consumer/order/eta";
    public static final String FEEDBACK_ETA = BASE_URL+"api/v3/consumer/order/eta/feedback";
    public static final String VIOLATION_LOG = BASE_URL +"api/v3/consumer/violations";

    public static final String FEATURE_SEARCH = BASE_URL+"api/v3/consumer/search?";
    public static final String FEATURE_MENU_DETAIL = BASE_URL+"api/list-menu-search-results";

    public static final String PRIVACY_POLICY = "https://hungerbox.com/privacy-policy.html";

    //Contact Tracing
    public static final String CONTACT_TRACING = BASE_URL + "api/v3/consumer/violations/contact-tracing";

    //Gender
    public static final String OCCUPANCY_URL = BASE_URL + "api/v3/consumer/location/occupancy";
    public static final String UPDATE_USER_INFO = BASE_URL + "api/v3/user_management/new_user_register";

    //Space Booking
    public static final String LIST_LOCATION_WITH_ADDRESS = BASE_URL+"api/v3/consumer/list-locations-with-addresses";
    public static final String LIST_LOCATION_SLOT =BASE_URL+ "api/v3/consumer/list-location-slots";
    public static final String LIST_SPACES = BASE_URL+"api/v3/consumer/list-spaces";
    public static final String LIST_LINKED_LOCATIONS = BASE_URL+"api/v3/consumer/list-linked-locations";
}
