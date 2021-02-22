package com.hungerbox.customer.util;

/**
 * Created by peeyush on 20/6/16.
 */
public interface ApplicationConstants {
    String SHARED_PREFERENCES = "sh_pref";
    String PREF_AUTH_TOKEN = "auth_token";
    String PREF_REFRESH_TOKEN = "refresh_token";
    String MANUAL_URL = "manual_url";
    String MANUAL_BUILD_TYPE = "manual_build";
    String PREF_AUTH_EXPIRY = "auth_token_expiry";
    String SERVER_PUB_KEY = "MEwwDQYJKoZIhvcNAQEBBQADOwAwOAIxANYu1oG2lB4Zvr2CbQWhgRzBZWBRBshZ\n" +
            "+++PJUhihJOphXyQneywRZhICcpQTedR5wIDAQAB";
    String SSO_LOGIN_LOCATION_ASKED = "sso_login_location_asked";
    String PREF_UNIFIED_COMPANY_ID = "unified_company_id";
    String PREF_UNIFIED_COMPANY_CODE = "unified_company_code";
    String PREF_USER_ID = "user_id";
    String PREF_USER_NAME = "user_name";
    String IGNORE_FEEDBACK_ON_SKIP= "ignore_feedback_on_skip";
    String IGNORE_FEEDBACK_ON_SKIP_LIST= "ignore_feedback_on_skip_list";
    String PREF_LAUNCH_TIME = "launch_time";
    String DIRECT_SOFT_UPDATE_TIME = "direct_soft_update_time";
    String PREF_USER_EMAIL = "user_email";
    String WAS_CONNECTED = "wasConnected";
    String VENDOR_ID = "vendorId";
    String VENDOR_NAME = "vendorName";
    String OCASSION_ID = "ocassionId";
    String LOCATION = "location";
    String ORDER = "order";
    String ORDER_ID = "orderId";
    String ALARM_TYPE = "alarmType";
    String OFFLINE_CERTIFICATE = "offline_certificate";
    String OFFLINE_PRIVATE_KEY = "offline_private_key";
    int ORDER_FEEDBACK = 1;
    String GUEST_TYPE_OFFICIAL = "official";
    String GUEST_TYPE_PERSONAL = "personal";
    String COMPANY_ID = "companyId";
    String LOCATION_NAME = "locationName";
    String LOCATION_CAPACITY = "location_capacity";
    String TIME_DIFFERENCE = "timeDifference";
    String LOCATION_ID = "locationId";
    String LOCATION_CITY_ID = "locationCityId";
    String INTERSTITIAL_BANNER_ID = "intestitial_banner_id";
    String BB_SDK_OPEN = "bb_sdk_open";
    String REGISTRATERED_USER = "registered_user";
    String PAYLOAD = "payload";
    String LOCATION_ID_PERMANENT = "locationIdPermanent";
    String LOCATION_NAME_PERMANENT = "locationNamePermanent";
    String AMOUNT_TO_PAY = "amountToPay";
    String PAYMENT_STATUS = "payment_status";
    String PAYEMT_SUCCESS = "received";
    String PAYMENT_PENDING = "payment_pending";
    String ORDER_AFTER_RECHARGE = "orderAfterRecharge";
    String ONBOARDING_COMPLETE = "onBoardingComplate";
    String LOGOUT_ALLOWED = "logoutAllowed";
    String PREF_FCM_TOKEN = "fcm_token";
    String PREF_FCM_TOKEN_SEND_TO_SERVER = "fcm_token_send_to_server";
    String VENDOR_TYPE_RESTAURANT = "restaurant";
    String CLOUDFLARE_KEY = "CB4aoMzTgrVzVLFj";
    String HOME = "Home";
    String MORE = "More";
    String CART = "Cart";
    String LOGOUT = "Logout";
    String FEEDBACK_TYPE_CHECK_BOX = "checkbox";
    String FEEDBACK_TYPE_INPUT_TEXT = "text";
    String FEEDBACK_TYPE_RATING_BAR = "star";
    String PREF_USER_EMP_TYPE_ID = "employee_type_id";
    String LAST_LOCATION_UPDATED_TIME = "last_location_time";
    long ONE_DAY_MILLIS = 1000l * 3600l * 24l;
    long THIRTY_MIN_MILLIS = 1000l * 60l * 30l;
    long ONE_MIN_MILLIS = 1000l * 60l;
    String SETTINGS = "Settings";
    String EMAIL_SETTING = "email_notification";
    String SMS_SETTING = "sms_notification";
    String APP_NOTIFICATION_SETTING = "app_notification";
    String CHAT_HEAD_SETTING = "chat_head";
    String PIP_SETTING = "pip";
    String GENERAL_NOTIFICATION = "general_notification";
    String SHOW_ORDER_NOTIFICATION = "show_order_notification";
    String ORDER_NOTIFICATION = "order_notification";
    String PERMISSION_ASKED = "permission_asked";
    String EMAIL_TEXT = "email";
    String MOBILE_NO_TEXT_1 = "mobile number";
    String MOBILE_NO_TEXT_2 = "mobile_no";
    String MOBILE_NO_TEXT_3 = "mobile_num";
    String BATTERY_EVENT = "v_b_e";
    String LAST_BATTERY_TIME = "last_battery_time";
    long FOUR_MINUTES = 4l * 60l * 1000l;
    String OTP_USER = "otp_user";
    String USER_REGISTRATION_CONT = "user_reg_cont";
    String USER_REGISTRATION_OBJ = "registration_user_obj";
    String USER_REGISTRATION_WALLET = "registration_wallet";
    String QR_CODE = "qr_code";
    String PREF_NAME = "name";
    String CARD_TYPE_COMPANY = "company_card";
    String CARD_TYPE_STICKER = "sticker";
    String HTTP_GET = "get";
    String HTTP_POST = "post";
    String REDIRECTION = "redirect";
    String HAS_USED_SIMPL = "HAS_USED_SIMPL";
    int PERMISSION_REQUEST_CODE = 1009;
    int DECLARATION_FORM_WEBVIEW = 3054;
    String ORDER_FOOD = "OrderFood";
    String HEALTH_TRACKER = "BeFit";
    String SHARED_ECONOMY = "SharedEconomy";
    String HISTORY = "History";
    String BOOK_EVENTS = "BookEvents";
    String PAYMENTS = "Payments";
    String ABOUT_US = "AboutUs";
    String CONTACT_US = "ContactUs";
    String FAQ = "Faq";
    String TAndC = "TermsAndCondition";
    String SPACE_MANAGEMENT = "space_management";
    String CURRENT_BOOKING = "Booking History";
    String SPACE_BOOKING = "space_booking";
    String MY_ACCOUNT = "MyAccount";
    String DEFAULT_LOCATION_ID = "default_location_id";
    String BOOKING_ID = "booking_id";
    String IS_ORDER_ARCHIVED = "is_order_archived";
    String IS_HISTORY = "is_history";
    String FROM_NAVBAR = "from_navbar";
    String FROM_BOOKMARK = "FROMBOOKMARK";
    String FROM_NOTIFICATION = "FROMNOTIFICATION";
    String FOR_FEEDBACK = "FORFEEDBACK";
    String FROM_REORDER = "fromReorder";
    String FROM_SLOT_BOOKING = "fromSlotBooking";
    String FROM_SPACE_BOOKING = "fromSpaceBooking";
    String POCKETS = "Pockets";
    String HEALTH_DETAILS = "healthDetails";
    String ACTION = "action";
    String ACTION_OPEN_LOCATION = "open_location";
    String PAYMENT_ORDER_TYPE = "events";
    String PAYMENT_ORDER_TYPE_EVENT = "event";
    String EVENT_URL = "event_url";
    String MEETING_URL = "meeting_url";
    String BANNER_URL = "banner_url";
    String ORDER_TYPE_FOOD = "order_type_food";
    String IS_NEW_ORDER = "new_order";
    String HISTORY_TITLE_FOOD = "Food";
    String HISTORY_TITLE_FOOD_OFFLINE = "Offline Order";
    String HISTORY_TITLE_SHARED_ECONOMY = "Shared Economy";
    String HISTORY_TITLE_EVENT = "Events";
    String PAYMENT_STATE_SUCCESS = "success";
    String PAYMENT_STATE_FAILURE = "failure";
    String PAYMENT_STATE_PENDING = "pending";
    String PAYMENT_STATE_REVERSE = "reversed";
    String PAYMENT_WALLET_SOURCE_EXTERNAL = "external";
    String PAYMENT_WALLET_SOURCE_INTERNAL = "internal";
    String SIMPL = "Simpl";
    String USER_PHONE = "user_phone";
    String IS_ORDER_PRE_ORDER = "order_pre_order";
    String PHONE_NUMBER = "phone_number";
    String EMAIL = "email";
    String PASSWORD = "password";
    String FIELD_TO_UPDATE = "field";
    String GUEST_ADD = "AddGuest";
    String GUEST_ORDER = "GuestOrder";
    String HISTORY_TYPE_ORDER = "order";
    String HISTORY_TYPE_GUEST_ORDER = "guest_order";
    String URL = "url";
    String HTTP_METHOD = "http_method";
    String POST_DATA = "post_data";
    String USER_CANCELLED = "user_cancelled";
    String REASON = "reason";
    String COMPANY_SUBDOMAIN = "subdomain";
    String PAYMENT_METHOD_TYPE_WALLET = "WALLET";
    String PAYMENT_DIRECT_PAYTM_POSTPAID ="PD_POSTPAID_WALLET";
    String PAYMENT_DIRECT_PAYTM_WALLET = "PD_WALLET";
    String FROM_REGISTRATION_WALLET= "fromRegistraionWallet";
    String PAYMENT_UPI_METHOD = "PaytmUpi";
    String PAYMENT_DYNAMIC_QR_PAYTM = "DynamicQRPaytm";
    String PAYMENT_DYNAMIC_QR_PAYTM_UPI = "DynamicQRPaytmUpi";
    String PINELBAS = "PINELABS";
    String PAYMENT_METHOD_TYPE_CARD = "CARD";
    String PAYMENT_METHOD_TYPE_NETBANKING = "NB";
    String PAYMENT_METHOD = "payment_method";
    String ERROR_MESSAGE = "err_msg";
    String PAYMENT_URL = "payment_url";
    String RETURN_URL = "return_url";
    String CONTEST_BANNER = "match";
    String REDEEM_POINTS = "Redeem";
    String HELP = "Help";
    String LINK = "link";
    String DEEPLINK = "deeplink";
    String BIG_BASKET = "bigbasket";
    String VENDING_MACHINE = "vending_machine";
    String SLOT_BOOKING = "slot_booking";
    String BIG_BASKET_MID = "6WRH7PYGjH4xvbKPg3T9NSklFZjEAi4";
    String BIG_BASKET_MERCHANT_TYPE = "HB";
    String DELIVERED_COUNT = "delivery_count";
    String EXPRESS_CHECKOUT_ACTION ="express_checkout_action" ;
    String BOOKMARK_VIEW_DIVIDED = "book_mark_view_divided";
    String BOOKMARK_VIEW_FULL = "book_mark_view_full";
    String BOOKMARK = "Bookmark";
    String FONT_MEDIUM = "medium";
    String IS_VENDORS_AVAILABLE = "is_vendor_available";
    String IS_PREORDER_AVAILABLE = "is_preorder_available";
    String PICK_UP_TYPE_DELIVERY = "delivery";
    String TERMS_AND_CONDITION = "terms_and_condition";
    String CURRENT_EXTRENAL_WALLET_BALANCE = "current_external_wallet_balance";
    String NOTIFICATION_SOURCE = "Notification";
    String OBJECT_ID_1 = "object_id_1";
    String OBJECT_ID_2 = "object_id_2";
    String OBJECT_ID_3 = "object_id_3";
    String HB_DEVICE_ID = "hb_device_id";
    String WATERMARK_ONLINE_CALLS = "watermark_online_calls";
    String WATERMARK_OFFLINE_CALLS = "watermark_offline_calls";
    String WATERMARK_FIREBASE_COUNT_SWITCH = "watermark_firebase_count_switch";
    String LOCATION_DESK_ORDERING_ENABLED = "location_desk_ordering_enabled";
    String ENFORCED_CAPACITY = "enforced_capacity";
    String LIST_WALLET = "list_wallet";
    String LAST_VISITED_WASHROOM = "last_visited_washroom";
    String LAST_VISITED_LIFT = "last_visited_lift";
    String OCCUPANCY = "Occupancy";
    String OCCUPANCY_WASHROOM_TYPE = "washroom";
    String OCCUPANCY_LOBBY_TYPE = "lift_lobby";
    String BACK_TO_SPACE_BOOKING = "back_to_space_booking";

    int BOOKMARK_FROM_MAINACTIVITY = 1;
    int BOOKMARK_FROM_NAVBAR = 2;
    int MAX_QUANTITIY_OFFLINE = 4;
    long THIRTY_MIN_MILLIS_OFFLINE = 1000l * 60l * 30l;
    String REFERRAL_QR_CODE = "referral_qr_code";
    String IS_URL = "isUrl";
    String URL_STRING = "url";
    String HEADER_TITLE = "header_title";
    String PERIPHERAL_DEIVCE_ID = "unique_device_id";
    String QR_PAYTM  = "PAYTM";
    String QR_UPI = "UPI";
    String PROCEED_TO_PAY = "PROCEED TO PAY";
    String ADD_ITEM_SOURCE_NORMAL= "Normal";
    String ADD_ITEM_SOURCE_EXP = "Express Checkout";
    String LOG_SETTING = "log_setting";
    String CATEGORY_LIST = "category_list";
    String MIN_MAX_VALUES = "min_max_values";
    String BLE_PROXIMITY_ENABLED = "ble_proximity_enabled";
    String BLE_START_TIME = "ble_start_time";
    String DEEP_LINK_NOTIFICATION_ID = "10002";
    String OTHER_TYPE_LOCATION = "other_location_type";
    String SPACE_LOCATION_ID = "space_location_id";
    String SPACE_LOCATION_NAME = "space_location_name";
    String FOR_SPACE_BOOKING = "for_space_booking";
    String IS_APPROVAL_PENDING = "is_approval_pending";
    String FROM_SPACE_BOOKING_REORDER = "FROM_SPACE_BOOKING_REORDER";


    class ASPECT_RATIO {
        public static final double MARKET_BANNER = 500/1000.0;
    }
    String MSWIPE_SODEXO = "mswipe_sodexo";
    String MSWIPE_CREDIT_DEBIT = "mswipe_card";
    String SHOULD_REFRESH_FROM_CHAT = "should_refresh_from_chat";
    String IS_GUEST_USER = "is_guest_user";
    String GUEST_ORDERING_SCOPE = "guest-ordering";
    String PERIPHERAL_DEIVCE_REF = "peripheral_device_ref";


    //Contest Constants
    String CONTEST_TITLE_CAMPAIGN = "Contests";
    String CONTEST_TITLE_OFFERS = "Offers";
    String CONTEST_TITLE_INBOX = "Inbox";
    String CAMPAIGN = "Campaign";
    String CAMPAIGN_ID = "campaign_id";
    String TEMPLATE_NAME = "template_name";
    String TEMPLATE_TYPE_QNA = "QnA";
    String NO_NET_STRING = "Poor Network connection.\n" + "Please check your connectivity.";
    String SOME_WRONG = "Something went wrong.\nPlease try again";


    //Space
    String SPACE_TYPE_TABLE = "table";
    String SPACE_TYPE_DESK = "desk";
    String SPACE_TYPE_GYM = "gym";
    String SPACE_TYPE_PARKING = "parking";
    String REDIRECT_SPACE_BOOKING = "redirect_space_booking";
    String QR_ENTRY_TEXT = "qr_entry_text";
    String QR_EXIT_TEXT = "qr_exit_text";

    // BLE Constants
    String ACTION_START = "ACTION_START";
    String ACTION_STOP = "ACTION_STOP";
    String URI_BLUETOOTH = "hb-uri";

    //Error String Constant
    String NO_INTERNET_IMAGE = "NO_INTERNET_IMAGE";
    String PAYMENT_FAILED_IMAGE = "PAYMENT_FAILED";
    String CART_ERROR_IMAGE = "CART_ERROR";
    String GENERAL_ERROR = "GENERAL_OTHER";

    enum CRUD{
        GET,POST,PUT,DELETE,GET_WITHOUT_HEADER
    }

    //Company type
    String COMP_TYPE_CASH_N_CARRY = "cash_n_carry";
    String COMP_TYPE_COMPANY_PAID = "company_paid";
    String COMP_TYPE_SUBSIDY = "subsidy";

    class Tutorial{
        public static final String TUTORIAL_LOCATION = "tutorial_location";
        public static final String TUTORIAL_OCCASSION = "tutorial_occassion";
        public static final String TUTORIAL_SWIPE = "tutorial_swipe";
        public static final String TUTORIAL_MENU_BOOKMARK_ICON = "tutorial_bookmark_icon";
        public static final String BOOKMARK_IN_NAV = "bookmark_in_nav";
        public static final String TUTORIAL_PAYMENT_BUTTON_IN_BOOKMARK = "payment_button_in_bookmark";
        public static final String TUTORIAL_BOOKMARK_BOOKMARK_HEADER = "tutorial_bookmark_bookmark_header";
        public static final String TUTORIAL_BOOKMARK_TRENDING_HEADER = "tutorial_bookmark_trending_header";
        public static final String TUTORIAL_BOOKMARK_ADD = "tutorial_bookmark_add";
    }

    class ShortCutsConstants{
        public static final String SHORTCUTS_HISTORY= "history";
        public static final String SHORTCUTS_LATEST_ORDER = "latest_order";
        public static final String SHORTCUTS_BOOKMARK = "bookmark";
    }

    class Navigation{
        public static final String HISTORY_SCREEN = "history";
        public static final String PAYMENT_SCREEN = "payment";
        public static final String BOOKMARK_SCREEN = "bookmark";
        public static final String CONTEST_SCREEN = "contest";
        public static final String ACCOUNT_SCREEN = "account";
        public static final String HELP_SCREEN = "help";
        public static final String CART_SCREEN = "cart";
        public static final String CONTEST_DETAIL_SCREEN = "contest_detail";
        public static final String MENU_SCREEN = "menu";
    }

    class NotificationsConstants{
        public static final String DEEPLINK_SCREEN = "deeplink_screen";
        public static final String LOCATION_ID = "location_id";
        public static final String OCCASION_ID = "occasion_id";
        public static final String VENDOR_ID = "vendor_id";
        public static final String ITEM_ID = "item_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String ACTION = "action";//values may be add,scroll or bookmark
        public static final String ACTION_ADD_ITEM = "add_item";
        public static final String ACTION_SCROLL_ITEM = "scroll_item";
        public static final String ACTION_BOOKMARK_ITEM = "bookmark_item";
        public static final String ACTION_SCROLL_CATEGORY = "scroll_category";
    }

    class RecommendationType{
        public static final String REGULAR_MENU = "regular_menu";
        public static final String RECOMMENDED_MENU = "recommended_menu";
        public static final String BOOKMARK = "bookmark";
        public static final String BOOKMARK_TRENDING = "bookmark_trending";
        public static final String CART_TRENDING = "cart_trending";
        public static final String REORDER = "reorder";
    }

    class GlobalSearch{
        public static final String TAB_VENDOR = "Vendors";
        public static final String TAB_DISH = "Dishes";
        public static final String TAB_CATEGORY = "Category";
    }

    class Actions{
        public static final int ACTIVITY_BACK = 1;
        public static final int FRAGMENT_DIALOG = 2;
        public static final int LOG_STATE_CHANGED = 3;
        public static final int ENABLED_BUTTON_UPDATE = 4;
        public static final int LOGOUT = 5;
    }

    class SpaceBooking{
        public static final String SPACE_TYPE = "type";
        public static final String AUTO_SELECT_LOCATION_ID = "auto_select_location_id";
    }
}
