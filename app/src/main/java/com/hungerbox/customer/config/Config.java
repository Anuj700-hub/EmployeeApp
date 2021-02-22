package com.hungerbox.customer.config;


import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.MainApplication;
import com.hungerbox.customer.model.JusPayPaymentItemModel;
import com.hungerbox.customer.model.Location;
import com.hungerbox.customer.model.NavItemModel;
import com.hungerbox.customer.model.ShortCuts;
import com.hungerbox.customer.util.AppUtils;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@DatabaseTable(tableName = "c_config ")
public class  Config implements java.io.Serializable {


    public static String api_key = "config";

    private static final long serialVersionUID = -9101085459063432816L;
    @DatabaseField
    public boolean no_paid_orders = false;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    String[] allowed_for_guest_ordering;

    public String getProject_code_message() {
        return project_code_message;
    }

    public void setProject_code_message(String project_code_message) {
        this.project_code_message = project_code_message;
    }

    String project_code_message = "Please enter valid project code.";

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    @DatabaseField
    String subDomain = "";

    private String projectcode_place_holder = "Project Code";

    public String getProjectcode_place_holder() {
        return projectcode_place_holder;
    }

    public void setProjectcode_place_holder(String projectcode_place_holder) {
        this.projectcode_place_holder = projectcode_place_holder;
    }

    public String[] getTutorial_text() {
        return tutorial_text;
    }

    public void setTutorial_text(String[] tutorial_text) {
        this.tutorial_text = tutorial_text;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    String[] tutorial_text = {"Please confirm your location before ordering","Select meal session to order","Click here for one step checkout","Bookmark your favourite items for faster checkouts","Bookmarked by you","Add items to the cart","See the trending items here","View your bookmarked items here","Pay using your favourite wallet"};
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    HashSet<Integer> review_allowed_employee_types = new HashSet<>();



    @DatabaseField
    private boolean group_location = false;
    @DatabaseField(id = true)
    private long company_id = -1;
    @DatabaseField
    private long banner_auto_move_time = 5000;

    @DatabaseField
    private long order_tracker_auto_move_time = 5000;

    @DatabaseField
    private long order_tracker_refresh_time = 30000;

    @DatabaseField
    private long pre_order_tracker_visible_time = 30 * 60 * 1000;

    public long getPre_order_tracker_visible_time() {
        return pre_order_tracker_visible_time;
    }

    public void setPre_order_tracker_visible_time(long pre_order_tracker_visible_time) {
        this.pre_order_tracker_visible_time = pre_order_tracker_visible_time;
    }

    public long getOrder_tracker_refresh_time() {
        return order_tracker_refresh_time;
    }

    public void setOrder_tracker_refresh_time(long order_tracker_refresh_time) {
        this.order_tracker_refresh_time = order_tracker_refresh_time;
    }

    public long getOrder_tracker_auto_swipe_time() {
        return order_tracker_auto_move_time;
    }

    public void setOrder_tracker_auto_swipe_time(long order_tracker_auto_swipe_time) {
        this.order_tracker_auto_move_time = order_tracker_auto_swipe_time;
    }

    public String getDirect_cancellation() {
        if (AppUtils.isCafeApp()){
            return "";
        } else {
            return direct_cancellation;
        }
    }

    public boolean isShow_my_account() {
        return show_my_account;
    }

    public void setShow_my_account(boolean show_my_account) {
        this.show_my_account = show_my_account;
    }

    @DatabaseField
    private boolean show_my_account = true;

    public void setDirect_cancellation(String direct_cancellation) {
        this.direct_cancellation = direct_cancellation;
    }

    @DatabaseField
    private String direct_cancellation = "";

    @DatabaseField
    private boolean show_wallet_breakup = false;
    public boolean showWallet_breakup(){
        return show_wallet_breakup;
    }
    public void setWallet_breakup(boolean wallet_breakup){
        show_wallet_breakup = wallet_breakup;
    }


    public boolean isSkip_reset() {
        return skip_reset;
    }

    public void setSkip_reset(boolean skip_reset) {
        this.skip_reset = skip_reset;
    }

    @DatabaseField
    private boolean skip_reset = true;

    public boolean isDirect_soft_update() {
        return direct_soft_update;
    }

    public void setDirect_soft_update(boolean direct_soft_update) {
        this.direct_soft_update = direct_soft_update;
    }

    @DatabaseField
    private boolean direct_soft_update = false;

    @DatabaseField
    private boolean auto_update = true;

    @DatabaseField
    private int direct_soft_update_gap = 172800000;

    public String getWorkstation_address() {
        return workstation_address;
    }

    public void setWorkstation_address(String workstation_address) {
        this.workstation_address = workstation_address;
    }

    @DatabaseField
    private String workstation_address = "Workstation Address";
    @DatabaseField
    private String app_name = "";

    public String getVendor_header() {
        if(vendor_header == null){
            return "";
        }
        return vendor_header;
    }

    public void setVendor_header(String vendor_header) {
        this.vendor_header = vendor_header;
    }

    @DatabaseField
    private String vendor_header = "Food Partners";

    public boolean isIgnore_feedback_on_skip() {
        return ignore_feedback_on_skip;
    }

    public void setIgnore_feedback_on_skip(boolean ignore_feedback_on_skip) {
        this.ignore_feedback_on_skip = ignore_feedback_on_skip;
    }

    @DatabaseField
    private String location_permission_info = "HungerBox needs your permission to access location in background in order to track your bluetooth and discover devices near you, the location data collected is not stored by HungerBox";

    public String getLocation_permission_info() {
        return location_permission_info;
    }

    public void setLocation_permission_info(String location_permission_info) {
        this.location_permission_info = location_permission_info;
    }

    @DatabaseField
    private boolean ignore_feedback_on_skip = false;
    @DatabaseField
    private String sso_logout_url;
    @DatabaseField
    private int cur_version;
    @DatabaseField
    private int order_detail_refresh_time = 30000;

    @DatabaseField
    private String company_paid_text = "Company Paid";

    @DatabaseField
    private String on_campus= "On Campus";

    @DatabaseField
    private String minimum_balance_error = "Insufficient balance. Please add one more payment option to continue";

    public boolean isBtn_on_banner() {
        return btn_on_banner;
    }

    public void setBtn_on_banner(boolean btn_on_banner) {
        this.btn_on_banner = btn_on_banner;
    }

    @DatabaseField
    private boolean btn_on_banner= false;

    public long getOrder_feedback_delay() {
        return order_feedback_delay;
    }

    public void setOrder_feedback_delay(long order_feedback_delay) {
        this.order_feedback_delay = order_feedback_delay;
    }

    @DatabaseField
    private long order_feedback_delay = 3600*1000;
    @DatabaseField
    private long order_feedback_time_limit = 0;

    public long getOrder_feedback_time_limit() {
        return order_feedback_time_limit;
    }

    public void setOrder_feedback_time_limit(long order_feedback_time_limit) {
        this.order_feedback_time_limit = order_feedback_time_limit;
    }

    @DatabaseField
    private String company_logo = "Hungerbox cafe";
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ConfigLogin_auth login_auth;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] auth_methods;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] login_methods = new String[]{"username", "email"};
    @DatabaseField
    private boolean wallet_present = true;
    @DatabaseField
    private boolean cash_recharge = false;


    @DatabaseField
    private String desk_refrence_ask_msg = "Please Enter Desk Number";
    @DatabaseField
    private String email_verification_ask_msg = "Please enter a valid email";

    public String getEmail_verification_ask_msg() {
        return email_verification_ask_msg;
    }

    public void setEmail_verification_ask_msg(String email_verification_ask_msg) {
        this.email_verification_ask_msg = email_verification_ask_msg;
    }

    public boolean isIs_analytics_enabled() {
        return is_analytics_enabled;
    }

    public void setIs_analytics_enabled(boolean is_analytics_enabled) {
        this.is_analytics_enabled = is_analytics_enabled;
    }

    @DatabaseField
    private boolean is_analytics_enabled = true;

    @DatabaseField
    private boolean show_location = true;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private AppUpdate app_update;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private AppUpdateCafe app_update_cafe;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Branding branding;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Terms terms;

    @DatabaseField
    private boolean hide_qrcode;

    public boolean isHide_qrcode(Location location) {
        if (MainApplication.appContext!=null && AppUtils.isSocialDistancingActive(location)){
            return false;
        } else {
            return hide_qrcode;
        }
    }

    public void setHide_qrcode(boolean hide_qrcode) {
        this.hide_qrcode = hide_qrcode;
    }

    public Terms getTerms() {
        return terms;
    }

    public void setTerms(Terms terms) {
        this.terms = terms;
    }

    public GroupOrdering getGroup_ordering() {
        return group_ordering;
    }

    public void setGroup_ordering(GroupOrdering group_ordering) {
        this.group_ordering = group_ordering;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private GroupOrdering group_ordering;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HealthBanner health_bannner;
    @DatabaseField
    private String sso_login_text = "Associate Login";
    @DatabaseField
    private boolean place_order = true;
    @DatabaseField
    private boolean auto_logout = false;
    @DatabaseField
    private String powered_by_merc = "";
    @DatabaseField
    private int vendor_feedback_comment_mandatory = -1;
    @DatabaseField
    private String vendor_feedback_comment_mandatory_msg = "What went wrong? Please provide us additional info to serve better.";
    @DatabaseField
    private long logout_idle_timeout = 1000l * 10l;

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    @DatabaseField
    private String country_code = "+91";
    @DatabaseField
    private boolean card_login = false;
    @DatabaseField
    private String meal_card_not_allowed_msg = "";
    @DatabaseField
    private long cart_timeout = 10l * 60l * 1000l;
    @DatabaseField
    private boolean is_wallet_displayed = false;
    @DatabaseField
    private boolean is_location_fixed = true;

    public boolean isNew_juspay_recharge() {
        return new_juspay_recharge;
    }

    public void setNew_juspay_recharge(boolean new_juspay_recharge) {
        this.new_juspay_recharge = new_juspay_recharge;
    }

    @DatabaseField
    private boolean new_juspay_recharge = true;

    public boolean isStop_simple_payload() {
        return stop_simple_payload;
    }

    public void setStop_simple_payload(boolean stop_simple_payload) {
        this.stop_simple_payload = stop_simple_payload;
    }

    @DatabaseField
    private boolean stop_simple_payload = false;
    @DatabaseField
    private boolean is_recharge_allowed = false;
    @DatabaseField
    private String update_redirect_url = "https://play.google.com/store/apps/details?id=com.hungerbox.customer.common";
    @DatabaseField
    private boolean review_comment = true;
    @DatabaseField
    private boolean is_guest_order = false;
    @DatabaseField
    private boolean chat_head = true;
    @DatabaseField
    private boolean otp_login_allowed = true;
    @DatabaseField
    private boolean picture_in_picture = false;
    @DatabaseField
    private int minimum_recharge_amount = 200;
    @DatabaseField
    private boolean guest_registration = false;
    @DatabaseField
    private boolean password_change_on_nfc = false;
    @DatabaseField
    private boolean is_sign_up_enabled = false;
    @DatabaseField
    private boolean menu_review_enabled = false;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, Long> registration_qr_hashs = new HashMap<>();
    @DatabaseField
    private boolean hide_price = false;
    @DatabaseField
    private boolean hide_timings = false;
    @DatabaseField
    private String launch_date = "3-8-2016";
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> email_verification;
    @DatabaseField
    private boolean otp_on_order = false;
    @DatabaseField
    private String zeta_sms_keyword = ".+(otp|pin).+(hungerbox|eatgood technologies).+";
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<NavItemModel> navbars = new ArrayList<>();
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<NavItemModel> bottombars = new ArrayList<>();

    public ArrayList<OnboardingText> getOnboarding_text() {
        return onboarding_text;
    }

    public void setOnboarding_text(ArrayList<OnboardingText> onboarding_text) {
        this.onboarding_text = onboarding_text;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<OnboardingText> onboarding_text = new ArrayList<>();

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<ShortCuts> shortcuts = new ArrayList<>();
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<JusPayPaymentItemModel> Juspay_payment_option = new ArrayList<>();
    @DatabaseField
    private boolean isHealthEnabled = false;
    @DatabaseField
    private boolean isDashBordEnabled = false;
    @DatabaseField
    private boolean is_sso_login = false;
    @DatabaseField
    private long onboarding_delay = 3000;
    @DatabaseField
    private boolean reccomendation_on_review = true;
    @DatabaseField
    private boolean new_zeta_enabled = true;
    @DatabaseField
    private boolean ext_wlt_rch_enabled = false;
    private boolean offer_banners_available = true;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Rating rating;
    @DatabaseField
    private boolean show_simpl = true;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private RegistrationWallet registration_wallet;
    @DatabaseField
    private boolean show_mswipe = false;
    @DatabaseField
    private boolean hb_wallet_compulsory = false;
    @DatabaseField
    private boolean is_sso_login_only = false;
    @DatabaseField
    private boolean should_take_review_on_start = true;

    @DatabaseField
    private String direct_soft_update_before_download = "Your update is being downloaded, we'll let you know when it is done.";

    @DatabaseField
    private String direct_soft_update_after_download = "Update Available";

    public String getDirect_soft_update_failed() {
        return direct_soft_update_failed;
    }

    public void setDirect_soft_update_failed(String direct_soft_update_failed) {
        this.direct_soft_update_failed = direct_soft_update_failed;
    }

    @DatabaseField
    private String direct_soft_update_failed = "Sorry, update failed";

    @DatabaseField
    private String email_pattern = "";
    @DatabaseField
    private boolean signup_email_activation = false;
    @DatabaseField
    private boolean is_feedback_skip_allowed = true;
    @DatabaseField
    private boolean add_card = false;
    @DatabaseField boolean hide_discount = false;
    @DatabaseField
    private boolean express_checkout = false;
    @DatabaseField
    private boolean coach_mark_visible = false;
    @DatabaseField
    private boolean sentiment_feedback = false;
    @DatabaseField
    private int sentiment_feedback_count = 3;
    @DatabaseField
    private boolean qrScreenShotAllowed = false;
    @DatabaseField
    private boolean contest_reward_visible = false;
    @DatabaseField
    private boolean allow_screen_brightness = false;
    @DatabaseField
    private boolean desc_arrow_visible = true;
    @DatabaseField
    private int max_upi_count = 3;
    @DatabaseField
    private boolean check_internal_wallet = true;
    @DatabaseField
    private double bb_minimum_balance = 100;
    @DatabaseField
    private boolean device_registration_allowed = true;
    @DatabaseField
    private boolean cafe_app_guest_ordering = true;
    @DatabaseField
    private long pinelab_retry_interval = 10L * 1000L;
    @DatabaseField
    private long pinelab_time_out = 3L * 60L * 1000L;
    @DatabaseField
    private boolean show_guest_order_pin = false;
    @DatabaseField
    private long  guest_logout_time = 1L * 60L * 1000L;

    @DatabaseField
    private String company_type;
    @DatabaseField
    private boolean otp_over_email = true;

    @DatabaseField
    private boolean ask_gender_info = false;

    public boolean getAsk_gender_info() {
        return ask_gender_info;
    }

    public void setAsk_gender_info(boolean ask_gender_info) {
        this.ask_gender_info = ask_gender_info;
    }

    public boolean isOtp_over_email() {
        return otp_over_email;
    }

    public void setOtp_over_email(boolean otp_over_email) {
        this.otp_over_email = otp_over_email;
    }

    public String getCompany_type() {
        return company_type;
    }

    public void setCompany_type(String company_type) {
        this.company_type = company_type;
    }

    public long getPinelab_retry_interval() {
        return pinelab_retry_interval;
    }

    public void setPinelab_retry_interval(long pinelab_retry_interval) {
        this.pinelab_retry_interval = pinelab_retry_interval;
    }

    public long getPinelab_time_out() {
        return pinelab_time_out;
    }

    public void setPinelab_time_out(long pinelab_time_out) {
        this.pinelab_time_out = pinelab_time_out;
    }

    public boolean isDevice_registration_allowed() {
        return device_registration_allowed;
    }

    public void setDevice_registration_allowed(boolean device_registration_allowed) {
        this.device_registration_allowed = device_registration_allowed;
    }

    public boolean isCafe_app_guest_ordering() {
        return cafe_app_guest_ordering;
    }

    public void setCafe_app_guest_ordering(boolean cafe_app_guest_ordering) {
        this.cafe_app_guest_ordering = cafe_app_guest_ordering;
    }

    public boolean isShow_guest_order_pin() {
        return show_guest_order_pin;
    }

    public void setShow_guest_order_pin(boolean show_guest_order_pin) {
        this.show_guest_order_pin = show_guest_order_pin;
    }

    public long getGuest_logout_time() {
        return guest_logout_time;
    }

    public void setGuest_logout_time(long guest_logout_time) {
        this.guest_logout_time = guest_logout_time;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<Long> project_code_array;

    public ArrayList<Long> getProject_code_array() {
        return project_code_array;
    }

    public void setProject_code_array(ArrayList<Long> project_code_array) {
        this.project_code_array = project_code_array;
    }

    @DatabaseField
    private boolean show_gst;

    @DatabaseField
    private boolean show_order_pending_label = false;

    @DatabaseField
    private boolean show_order_delivery_time = false;

    @DatabaseField
    private String terms_of_use = "";

    @DatabaseField
    private String privacy_policy = "";

    @DatabaseField
    private int left_threshold = 2;

    @DatabaseField
    private String vending_machine_support_version;

    @DatabaseField
    private String free_meal_exhaust_msg = "Free meal over for this item. Please select some other item from the menu.";

    @DatabaseField
    private String congestion_msg = "To maintain cafe congestion, please book another cafe entry slot in the next occasion";

    public boolean isVendingMachineEnabled(){
        return AppUtils.compareAppVersion(vending_machine_support_version);
    }

    public void setVending_machine_support_version(String vending_machine_support_version) {
        this.vending_machine_support_version = vending_machine_support_version;
    }

    public int getLeft_threshold() {
        return left_threshold;
    }

    public void setLeft_threshold(int left_threshold) {
        this.left_threshold = left_threshold;
    }

    public String getFree_meal_exhaust_msg() {
        if(free_meal_exhaust_msg == null || free_meal_exhaust_msg.trim().length()== 0)
            return "Free meal over for this item. Please select some other item from the menu.";
        return free_meal_exhaust_msg;
    }

    public void setFree_meal_exhaust_msg(String free_meal_exhaust_msg) {
        this.free_meal_exhaust_msg = free_meal_exhaust_msg;
    }

    public String getCongestion_msg() {
        if(congestion_msg == null || congestion_msg.trim().length() == 0)
            return "To maintain cafe congestion, please book another cafe entry slot in the next occasion";
        return congestion_msg;
    }

    public void setCongestion_msg(String congestion_msg) {
        this.congestion_msg = congestion_msg;
    }

    public String getTerms_of_use() {
        return terms_of_use;
    }

    public void setTerms_of_use(String terms_of_use) {
        this.terms_of_use = terms_of_use;
    }

    public String getPrivacy_policy() {
        return privacy_policy;
    }

    public void setPrivacy_policy(String privacy_policy) {
        this.privacy_policy = privacy_policy;
    }

    public boolean isShow_order_delivery_time() {
        return show_order_delivery_time;
    }

    public void setShow_order_delivery_time(boolean show_order_delivery_time) {
        this.show_order_delivery_time = show_order_delivery_time;
    }

    public boolean isShow_order_pending_label() {
        return show_order_pending_label;
    }

    public void setShow_order_pending_label(boolean show_order_pending_label) {
        this.show_order_pending_label = show_order_pending_label;
    }

    public boolean isShow_gst() {
        return show_gst;
    }

    public void setShow_gst(boolean show_gst) {
        this.show_gst = show_gst;
    }

    public double getBb_minimum_balance() {
        return bb_minimum_balance;
    }

    public void setBb_minimum_balance(double bb_minimum_balance) {
        this.bb_minimum_balance = bb_minimum_balance;
    }

    public int getMax_upi_count() {
        return max_upi_count;
    }
    public void setMax_upi_count(int max_upi_count) {
        this.max_upi_count = max_upi_count;
    }

    public boolean isDesc_arrow_visible() {
        return desc_arrow_visible;
    }

    public void setDesc_arrow_visible(boolean desc_arrow_visible) {
        this.desc_arrow_visible = desc_arrow_visible;
    }

    public boolean isAllow_screen_on() {
        return allow_screen_on;
    }

    public void setAllow_screen_on(boolean allow_screen_on) {
        this.allow_screen_on = allow_screen_on;
    }

    public boolean isCheck_internal_wallet() {
        return check_internal_wallet;
    }

    public void setCheck_internal_wallet(boolean check_internal_wallet) {
        this.check_internal_wallet = check_internal_wallet;
    }

    @DatabaseField
    private boolean allow_screen_on = true;
    @DatabaseField
    private boolean free_menu_mapping = false;

    public boolean isStop_animation_below_lollipop() {
        return stop_animation_below_lollipop;
    }

    public void setStop_animation_below_lollipop(boolean stop_animation_below_lollipop) {
        this.stop_animation_below_lollipop = stop_animation_below_lollipop;
    }

    @DatabaseField
    private boolean stop_animation_below_lollipop = true;

    //for email activation dialog messages
    @DatabaseField
    private String email_verification_message = "Please verify your ibm email in order to enjoy complementary items";

    @DatabaseField
    private String verify_email_message = "Verification link has been sent to your email. Please check your inbox";

    @DatabaseField
    private String resend_email_message = "Email verification is pending. Tap on Resend, if you haven't received the link yet";

    public Branding getBranding() {
        return branding;
    }

    public void setBranding(Branding branding) {
        this.branding = branding;
    }

    public String getEmail_verification_message() {
        return email_verification_message;
    }

    public void setEmail_verification_message(String email_verification_message) {
        this.email_verification_message = email_verification_message;
    }

    public String getVerify_email_message() {
        return verify_email_message;
    }

    public void setVerify_email_message(String verify_email_message) {
        this.verify_email_message = verify_email_message;
    }

    public String getResend_email_message() {
        return resend_email_message;
    }

    public void setResend_email_message(String resend_email_message) {
        this.resend_email_message = resend_email_message;
    }

    public String getOrder_status_window() {
        return order_status_window;
    }

    public void setOrder_status_window(String order_status_window) {
        this.order_status_window = order_status_window;
    }
    public String order_status_window = "Order Status Window";

    public boolean isFree_menu_mapping() {
        return free_menu_mapping;
    }

    public void setFree_menu_mapping(boolean free_menu_mapping) {
        this.free_menu_mapping = free_menu_mapping;
    }

    public boolean isExpress_checkout(){return express_checkout;}

    public void setExpress_checkout(boolean express_checkout ){this.express_checkout = express_checkout;}

    public boolean isHide_discount() {
        return hide_discount;
    }

    public void setHide_discount(boolean hide_discount) {
        this.hide_discount = hide_discount;
    }

    public boolean isAdd_card() {
        return add_card;
    }

    public void setAdd_card(boolean add_card) {
        this.add_card = add_card;
    }

    public boolean isShow_location() {
        return show_location;
    }

    public void setShow_location(boolean show_location) {
        this.show_location = show_location;
    }

    public boolean isShow_mswipe() {
        return show_mswipe;
    }

    public void setShow_mswipe(boolean show_mswipe) {
        this.show_mswipe = show_mswipe;
    }
    public int getMax_recharge() {
        return max_recharge;
    }

    public void setMax_recharge(int max_recharge) {
        this.max_recharge = max_recharge;
    }

    @DatabaseField
    private int max_recharge = 2000;

    @DatabaseField
    private String low_paytm_balance_msg = "Insufficient Balance.Please select another payment method";

    @DatabaseField
    private String postpaid_not_eligible ="";

    @DatabaseField
    private int default_delivery_option = 1;

    public int getDefault_delivery_option() {
        return default_delivery_option;
    }

    public void setDefault_delivery_option(int default_delivery_option) {
        this.default_delivery_option = default_delivery_option;
    }

    public String getLow_paytm_balance_msg() {
        return low_paytm_balance_msg;
    }

    public void setLow_paytm_balance_msg(String low_paytm_balance_msg) {
        this.low_paytm_balance_msg = low_paytm_balance_msg;
    }

    public String getPostpaid_not_eligible() {
        return postpaid_not_eligible;
    }

    public void setPostpaid_not_eligible(String postpaid_not_eligible) {
        this.postpaid_not_eligible = postpaid_not_eligible;
    }

    public boolean isSignup_email_activation() {
        return signup_email_activation;
    }

    public void setSignup_email_activation(boolean signup_email_activation) {
        this.signup_email_activation = signup_email_activation;
    }

    public AppUpdate getApp_update() {
        return app_update;
    }

    public void setApp_update(AppUpdate app_update) {
        this.app_update = app_update;
    }

    public HealthBanner getHealthBannner() {
        return health_bannner;
    }

    public void setHealthBannner(HealthBanner healthBannner) {
        this.health_bannner = healthBannner;
    }

    public boolean is_feedback_skip_allowed() {
        return is_feedback_skip_allowed;
    }

    public void setIs_feedback_skip_allowed(boolean is_feedback_skip_allowed) {
        this.is_feedback_skip_allowed = is_feedback_skip_allowed;
    }

    public boolean isShould_take_review_on_start() {
        return should_take_review_on_start;
    }

    public void setShould_take_review_on_start(boolean should_take_review_on_start) {
        this.should_take_review_on_start = should_take_review_on_start;
    }

    public HashSet<Integer> getReview_allowed_employee_types() {
        return review_allowed_employee_types;
    }

    public void setReview_allowed_employee_types(HashSet<Integer> review_allowed_employee_types) {
        this.review_allowed_employee_types = review_allowed_employee_types;
    }

    public boolean employee_allowed_for_review(int employeeId) {
        if (review_allowed_employee_types == null || review_allowed_employee_types.size() <= 0) {
            return true;
        } else {
            return review_allowed_employee_types.contains(employeeId);
        }
    }


    public boolean isHb_wallet_compulsory() {
        return hb_wallet_compulsory;
    }

    public void setHb_wallet_compulsory(boolean hb_wallet_compulsory) {
        this.hb_wallet_compulsory = hb_wallet_compulsory;
    }

    public boolean isIs_sso_login_only() {
        return is_sso_login_only;
    }

    public void setIs_sso_login_only(boolean is_sso_login_only) {
        this.is_sso_login_only = is_sso_login_only;
    }

    public boolean isShow_simpl() {
        return show_simpl;
    }

    public void setShow_simpl(boolean show_simpl) {
        this.show_simpl = show_simpl;
    }

    public boolean isOffer_banners_available() {
        return offer_banners_available;
    }

    public void setOffer_banners_available(boolean offer_banners_available) {
        this.offer_banners_available = offer_banners_available;
    }

    public long getOnboarding_delay() {
        return onboarding_delay;
    }

    public void setOnboarding_delay(long onboarding_delay) {
        this.onboarding_delay = onboarding_delay;
    }

    public String getLaunch_date() {
        return launch_date;
    }

    public boolean isHide_timings() {
        return hide_timings;
    }

    public void setHide_timings(boolean hide_timings) {
        this.hide_timings = hide_timings;
    }

    public boolean isHide_price() {
        return hide_price;
    }

    public void setHide_price(boolean hide_price) {
        this.hide_price = hide_price;
    }

    public HashMap<String, Long> getRegistration_qr_hashs() {
        return registration_qr_hashs;
    }

    public void setRegistration_qr_hashs(HashMap<String, Long> registration_qr_hash) {
        this.registration_qr_hashs = registration_qr_hash;
    }

    public boolean isMenu_review_enabled() {
        return menu_review_enabled;
    }

    public void setMenu_review_enabled(boolean menu_review_enabled) {
        this.menu_review_enabled = menu_review_enabled;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public boolean isGuest_registration() {
        return guest_registration;
    }

    public void setGuest_registration(boolean guest_registration) {
        this.guest_registration = guest_registration;
    }

    public boolean isNo_paid_orders() {
        return no_paid_orders;
    }

    public void setNo_paid_orders(boolean no_paid_orders) {
        this.no_paid_orders = no_paid_orders;
    }

    public boolean isChat_head() {
        return chat_head;
    }

    public void setChat_head(boolean chat_head) {
        this.chat_head = chat_head;
    }

    public ArrayList getAllowed_for_guest_ordering() {
        ArrayList<String> emp_id_list = new ArrayList<>();
        if (allowed_for_guest_ordering != null) {
            for (int i = 0; i < allowed_for_guest_ordering.length; i++) {
                emp_id_list.add(allowed_for_guest_ordering[i]);
            }
        }
        return emp_id_list;
    }

    public void setAllowed_for_guest_ordering(String[] allowed_for_guest_ordering) {
        this.allowed_for_guest_ordering = allowed_for_guest_ordering;
    }

    public boolean is_guest_order() {
        return is_guest_order;
    }

    public void setIs_guest_order(boolean is_guest_order) {
        this.is_guest_order = is_guest_order;
    }

    public int getCur_version() {
        return this.cur_version;
    }

    public void setCur_version(int cur_version) {
        this.cur_version = cur_version;
    }

    public String getCompany_logo() {
        return this.company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    public ConfigLogin_auth getLogin_auth() {
        return this.login_auth;
    }

    public void setLogin_auth(ConfigLogin_auth login_auth) {
        this.login_auth = login_auth;
    }

    public String[] getAuth_methods() {
        return this.auth_methods;
    }

    public void setAuth_methods(String[] auth_methods) {
        this.auth_methods = auth_methods;
    }

    public boolean getWallet_present() {
        return this.wallet_present;
    }

    public boolean isAuto_logout() {
        return auto_logout;
    }

    public void setAuto_logout(boolean auto_logout) {
        this.auto_logout = auto_logout;
    }

    public long getLogout_idle_timeout() {
        return logout_idle_timeout;
    }

    public void setLogout_idle_timeout(long logout_idle_timeout) {
        this.logout_idle_timeout = logout_idle_timeout;
    }

    public boolean isPlace_order() {
        return place_order;
    }

    public void setPlace_order(boolean place_order) {
        this.place_order = place_order;
    }

    public boolean isCard_login() {
        return card_login;
    }

    public void setCard_login(boolean card_login) {
        this.card_login = card_login;
    }

    public long getCart_timeout() {
        return cart_timeout;
    }

    public void setCart_timeout(long cart_timeout) {
        this.cart_timeout = cart_timeout;
    }

    public boolean is_wallet_displayed() {
        return is_wallet_displayed;
    }

    public void setIs_wallet_displayed(boolean is_wallet_displayed) {
        this.is_wallet_displayed = is_wallet_displayed;
    }

    public long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(long company_id) {
        this.company_id = company_id;
    }

    public boolean is_location_fixed() {
        return is_location_fixed;
    }

    public void setIs_location_fixed(boolean is_location_fixed) {
        this.is_location_fixed = is_location_fixed;
    }

    public boolean is_recharge_allowed() {
        return is_recharge_allowed;
    }

    public void setIs_recharge_allowed(boolean is_recharge_allowed) {
        this.is_recharge_allowed = is_recharge_allowed;
    }

    public String getUpdate_redirect_url() {
        return update_redirect_url;
    }

    public void setUpdate_redirect_url(String update_redirect_url) {
        this.update_redirect_url = update_redirect_url;
    }

    public boolean isReview_comment() {
        return review_comment;
    }

    public void setReview_comment(boolean review_comment) {
        this.review_comment = review_comment;
    }

    public String[] getLogin_methods() {
        return login_methods;
    }

    public void setLogin_methods(String[] login_methods) {
        this.login_methods = login_methods;
    }

    public boolean isOtp_login() {
        return otp_login_allowed;
    }

    public void setOtp_login(boolean otp_login) {
        this.otp_login_allowed = otp_login;
    }

    public int getMinimumRechargeAmount() {
        return minimum_recharge_amount;
    }

    public void setMinimumRechargeAmount(int minimumRechargeAmount) {
        this.minimum_recharge_amount = minimumRechargeAmount;
    }

    public boolean isPassword_change_on_nfc() {
        return password_change_on_nfc;
    }

    public void setPassword_change_on_nfc(boolean password_change_on_nfc) {
        this.password_change_on_nfc = password_change_on_nfc;
    }

    public boolean is_sign_up_enabled() {
        return is_sign_up_enabled;
    }

    public void setIs_sign_up_enabled(boolean is_sign_up_enabled) {
        this.is_sign_up_enabled = is_sign_up_enabled;
    }

    public ArrayList<String> getEmail_verification() {
        if (email_verification == null)
            email_verification = new ArrayList<>();
        return email_verification;
    }

    public void setEmail_verification(ArrayList<String> email_verification) {
        this.email_verification = email_verification;
    }

    public boolean isOtp_on_order() {
        return otp_on_order;
    }

    public void setOtp_on_order(boolean otp_on_order) {
        this.otp_on_order = otp_on_order;
    }

    public String getZeta_sms_keyword() {
        return zeta_sms_keyword;
    }

    public void setZeta_sms_keyword(String zeta_sms_keyword) {
        this.zeta_sms_keyword = zeta_sms_keyword;
    }

    public ArrayList<NavItemModel> getNavbars() {

        if (navbars == null || navbars.size() <= 0) {
            navbars = new ArrayList<>();
        }

        return navbars;
    }

    public ArrayList<NavItemModel> getBottombars() {

        if (bottombars == null || bottombars.size() <= 0) {
            bottombars = new ArrayList<>();
        }

        return bottombars;
    }

    public void setNavbars(ArrayList<NavItemModel> navbars) {
        this.navbars = navbars;
    }

    public void setBottombars(ArrayList<NavItemModel> bottombars) {
        this.bottombars = bottombars;
    }

    public boolean isHealthEnabled() {
        return isHealthEnabled;
    }

    public void setHealthEnabled(boolean healthEnabled) {
        isHealthEnabled = healthEnabled;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public boolean isDashBordEnabled() {
        return isDashBordEnabled;
    }

    public void setDashBordEnabled(boolean dashBordEnabled) {
        isDashBordEnabled = dashBordEnabled;
    }

    public boolean isReccomendation_on_review() {
        return reccomendation_on_review;
    }

    public void setReccomendation_on_review(boolean reccomendation_on_review) {
        this.reccomendation_on_review = reccomendation_on_review;
    }

    public boolean isNew_zeta_enabled() {
        return new_zeta_enabled;
    }

    public void setNew_zeta_enabled(boolean new_zeta_enabled) {
        this.new_zeta_enabled = new_zeta_enabled;
    }

    public boolean isExt_wlt_rch_enabled() {
        return ext_wlt_rch_enabled;
    }

    public void setExt_wlt_rch_enabled(boolean ext_wlt_rch_enabled) {
        this.ext_wlt_rch_enabled = ext_wlt_rch_enabled;
    }

    public boolean isGroup_location() {
        return group_location;
    }

    public void setGroup_location(boolean group_location) {
        this.group_location = group_location;
    }

    public boolean isIs_sso_login() {
        return is_sso_login;
    }

    public void setIs_sso_login(boolean is_sso_login) {
        this.is_sso_login = is_sso_login;
    }

    public String getSso_login_text() {
        return sso_login_text;
    }

    public void setSso_login_text(String sso_login_text) {
        this.sso_login_text = sso_login_text;
    }

    public int getOrder_detail_refresh_time() {
        return order_detail_refresh_time;
    }

    public void setOrder_detail_refresh_time(int order_detail_refresh_time) {
        this.order_detail_refresh_time = order_detail_refresh_time;
    }

    public long getBanner_auto_move_time() {
        return banner_auto_move_time;
    }

    public void setBanner_auto_move_time(long banner_auto_move_time) {
        this.banner_auto_move_time = banner_auto_move_time;
    }

    public int isVendor_feedback_comment_mandatory() {
        return vendor_feedback_comment_mandatory;
    }

    public void setVendor_feedback_comment_mandatory(int vendor_feedback_comment_mandatory) {
        this.vendor_feedback_comment_mandatory = vendor_feedback_comment_mandatory;
    }

    public String getVendor_feedback_comment_mandatory_msg() {
        return vendor_feedback_comment_mandatory_msg;
    }

    public void setVendor_feedback_comment_mandatory_msg(String vendor_feedback_comment_mandatory_msg) {
        this.vendor_feedback_comment_mandatory_msg = vendor_feedback_comment_mandatory_msg;
    }

    public boolean isCash_recharge() {
        return cash_recharge;
    }

    public void setCash_recharge(boolean cash_recharge) {
        this.cash_recharge = cash_recharge;
    }

    public String getPowered_by_merc() {
        return powered_by_merc;
    }

    public void setPowered_by_merc(String powered_by_merc) {
        this.powered_by_merc = powered_by_merc;
    }

    public String getSso_logout_url() {
        return sso_logout_url;
    }

    public void setSso_logout_url(String sso_logout_url) {
        this.sso_logout_url = sso_logout_url;
    }

    public ArrayList<JusPayPaymentItemModel> getJuspay_payment_option() {
        return Juspay_payment_option;
    }

    public void setJuspay_payment_option(ArrayList<JusPayPaymentItemModel> juspay_payment_option) {
        Juspay_payment_option = juspay_payment_option;
    }

    public String getEmail_pattern() {
        return email_pattern;
    }

    public void setEmail_pattern(String email_pattern) {
        this.email_pattern = email_pattern;
    }

    public String getMeal_card_not_allowed_msg() {
        return meal_card_not_allowed_msg;
    }

    public void setMeal_card_not_allowed_msg(String meal_card_not_allowed_msg) {
        this.meal_card_not_allowed_msg = meal_card_not_allowed_msg;
    }

    public boolean isCoach_mark_visible() {
        return coach_mark_visible&&!AppUtils.isCafeApp();
    }
    public void setCoach_mark_visible(boolean coach_mark_visible){
        this.coach_mark_visible = coach_mark_visible;
    }

    public String getDesk_refrence_ask_msg() {
        return desk_refrence_ask_msg;
    }

    public void setDesk_refrence_ask_msg(String desk_refrence_ask_msg) {
        this.desk_refrence_ask_msg = desk_refrence_ask_msg;
    }

    public ArrayList<ShortCuts> getShortcuts() {
        return shortcuts;
    }

    public void setShortcuts(ArrayList<ShortCuts> shortcuts) {
        this.shortcuts = shortcuts;
    }

    public boolean isPicture_in_picture() {
        return picture_in_picture;
    }

    public void setPicture_in_picture(boolean picture_in_picture) {
        this.picture_in_picture = picture_in_picture;
    }

    public boolean isSentiment_feedback() {
        return sentiment_feedback;
    }

    public void setSentiment_feedback(boolean sentiment_feedback) {
        this.sentiment_feedback = sentiment_feedback;
    }

    public int getSentiment_feedback_count() {
        return sentiment_feedback_count;
    }

    public void setSentiment_feedback_count(int sentiment_feedback_count) {
        this.sentiment_feedback_count = sentiment_feedback_count;
    }

    public String getCompany_paid_text() {
        return company_paid_text;
    }

    public void setCompany_paid_text(String company_paid_text) {
        this.company_paid_text = company_paid_text;
    }

    public String getOn_campus() {
        return on_campus;
    }

    public void setOn_campus(String on_campus) {
        this.on_campus = on_campus;
    }


    public String getDirect_soft_update_before_download() {
        return direct_soft_update_before_download;
    }

    public void setDirect_soft_update_before_download(String direct_soft_update_before_download) {
        this.direct_soft_update_before_download = direct_soft_update_before_download;
    }

    public String getDirect_soft_update_after_download() {
        return direct_soft_update_after_download;
    }

    public void setDirect_soft_update_after_download(String direct_soft_update_after_download) {
        this.direct_soft_update_after_download = direct_soft_update_after_download;
    }

    public boolean isQrScreenShotAllowed() {
        return qrScreenShotAllowed;
    }

    public void setQrScreenShotAllowed(boolean qrScreenShotAllowed) {
        this.qrScreenShotAllowed = qrScreenShotAllowed;
    }

    public boolean isContest_reward_visible() {
        return contest_reward_visible;
    }

    public void setContest_reward_visible(boolean contest_reward_visible) {
        this.contest_reward_visible = contest_reward_visible;
    }

    public int getDirect_soft_update_gap() {
        return direct_soft_update_gap;
    }

    public void setDirect_soft_update_gap(int direct_soft_update_gap) {
        this.direct_soft_update_gap = direct_soft_update_gap;
    }

    public boolean isAllow_screen_brightness() {
        return allow_screen_brightness;
    }

    public void setAllow_screen_brightness(boolean allow_screen_brightness) {
        this.allow_screen_brightness = allow_screen_brightness;
    }

    public boolean isAuto_update() {
        return auto_update;
    }

    public void setAuto_update(boolean auto_update) {
        this.auto_update = auto_update;
    }

    public String getMinimum_balance_error() {
        return minimum_balance_error;
    }

    public void setMinimum_balance_error(String minimum_balance_error) {
        this.minimum_balance_error = minimum_balance_error;
    }


    public class Terms implements Serializable{
        private static final long serialVersionUID = -1613720253677610610L;
        public String url;
        public String terms_text;
        public int start_index;
        public int end_index;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTerms_text() {
            return terms_text;
        }

        public void setTerms_text(String terms_text) {
            this.terms_text = terms_text;
        }

        public int getStart_index() {
            return start_index;
        }

        public void setStart_index(int start_index) {
            this.start_index = start_index;
        }

        public int getEnd_index() {
            return end_index;
        }

        public void setEnd_index(int end_index) {
            this.end_index = end_index;
        }
    }

    public RegistrationWallet getRegistration_wallet() {
        return registration_wallet;
    }

    public void setRegistration_wallet(RegistrationWallet registration_wallet) {
        this.registration_wallet = registration_wallet;
    }

    public class Branding implements Serializable{
        private static final long serialVersionUID = -1613720253677610610L;
        public String logo = "";
        public String welcome_text_title = "";
        public String welcome_text_desc= "";
        private boolean welcome_logo_enabled = true;
        private boolean navigation_logo_enabled = true;

        public boolean isWelcome_logo_enabled() {
            return welcome_logo_enabled;
        }


        public boolean isNavigation_logo_enabled() {
            return navigation_logo_enabled;
        }

        public void setWelcome_logo_enabled(boolean welcome_logo_enabled) {
            this.welcome_logo_enabled = welcome_logo_enabled;
        }

        public void setNavigation_logo_enabled(boolean navigation_logo_enabled) {
            this.navigation_logo_enabled = navigation_logo_enabled;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            if (logo==null){
                this.logo = "";
            }
            this.logo = logo;
        }

        public String getWelcome_text_title() {
            return welcome_text_title;
        }

        public void setWelcome_text_title(String welcome_text_title) {
            this.welcome_text_title = welcome_text_title;
        }

        public String getWelcome_text_desc() {
            return welcome_text_desc;
        }

        public void setWelcome_text_desc(String welcome_text_desc) {
            this.welcome_text_desc = welcome_text_desc;
        }
    }

    public class AppUpdate implements Serializable {
        private static final long serialVersionUID = -1613720253677610610L;
        public String title;
        public int soft_version;
        public int hard_version;
        public String soft_desc;
        public String hard_desc;
        public String update_redirect_url;

        public String getUpdate_redirect_url() {
            return update_redirect_url;
        }

        public void setUpdate_redirect_url(String update_redirect_url) {
            this.update_redirect_url = update_redirect_url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSoft_version() {
            return soft_version;
        }

        public void setSoft_version(int soft_version) {
            this.soft_version = soft_version;
        }

        public int getHard_version() {
            return hard_version;
        }

        public void setHard_version(int hard_version) {
            this.hard_version = hard_version;
        }

        public String getSoft_desc() {
            return soft_desc;
        }

        public void setSoft_desc(String soft_desc) {
            this.soft_desc = soft_desc;
        }

        public String getHard_desc() {
            return hard_desc;
        }

        public void setHard_desc(String hard_desc) {
            this.hard_desc = hard_desc;
        }
    }

    public AppUpdateCafe getApp_update_cafe() {
        return app_update_cafe;
    }

    public void setApp_update_cafe(AppUpdateCafe app_update_cafe) {
        this.app_update_cafe = app_update_cafe;
    }

    public class AppUpdateCafe implements Serializable {
        private static final long serialVersionUID = -1613720253677610610L;
        public String title;
        public int soft_version;
        public int hard_version;
        public String soft_desc;
        public String hard_desc;
        public String update_redirect_url;

        public String getUpdate_redirect_url() {
            return update_redirect_url;
        }

        public void setUpdate_redirect_url(String update_redirect_url) {
            this.update_redirect_url = update_redirect_url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getSoft_version() {
            return soft_version;
        }

        public void setSoft_version(int soft_version) {
            this.soft_version = soft_version;
        }

        public int getHard_version() {
            return hard_version;
        }

        public void setHard_version(int hard_version) {
            this.hard_version = hard_version;
        }

        public String getSoft_desc() {
            return soft_desc;
        }

        public void setSoft_desc(String soft_desc) {
            this.soft_desc = soft_desc;
        }

        public String getHard_desc() {
            return hard_desc;
        }

        public void setHard_desc(String hard_desc) {
            this.hard_desc = hard_desc;
        }
    }

    public class GroupOrdering implements Serializable {
        private static final long serialVersionUID = -1613720253677610610L;

        public String title = "Add friends";
        public String error = "Some Error Occured";
        private String count_text = "friend added";
        public boolean enable = true;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getCount_text() {
            return this.count_text;
        }

        public void setCount_text(String count_text) {
            this.count_text = count_text;
        }
    }

    public class OnboardingText implements Serializable {
        private static final long serialVersionUID = -1613720253677610610L;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String title;
        public String desc;
    }

    public class Rating implements Serializable {
        private static final long serialVersionUID = -1613720253677610610L;

        public String title;
        public String desc;
        public String feedback_title;
        public String feedback_desc;
        public String step;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getFeedback_title() {
            return feedback_title;
        }

        public void setFeedback_title(String feedback_title) {
            this.feedback_title = feedback_title;
        }

        public String getFeedback_desc() {
            return feedback_desc;
        }

        public void setFeedback_desc(String feedback_desc) {
            this.feedback_desc = feedback_desc;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }
    }

    public class RegistrationWallet implements Serializable{

        private static final long serialVersionUID = -1613720253677610610L;

        public String wallet_code;
        public String wallet_name;
        public String logo;
        public String logo1;
        public String logo2;
        public String logo3;
        public String desc;
        public String desc1;
        public String desc2;
        public String desc3;
        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getLogo1() {
            return logo1;
        }

        public void setLogo1(String logo1) {
            this.logo1 = logo1;
        }

        public String getLogo2() {
            return logo2;
        }

        public void setLogo2(String logo2) {
            this.logo2 = logo2;
        }

        public String getLogo3() {
            return logo3;
        }

        public void setLogo3(String logo3) {
            this.logo3 = logo3;
        }

        public String getWallet_code() {
            return wallet_code;
        }

        public void setWallet_code(String wallet_code) {
            this.wallet_code = wallet_code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getWallet_name() {
            return wallet_name;
        }

        public void setWallet_name(String wallet_name) {
            this.wallet_name = wallet_name;
        }

        public String getDesc1() {
            return desc1;
        }

        public void setDesc1(String desc1) {
            this.desc1 = desc1;
        }

        public String getDesc2() {
            return desc2;
        }

        public void setDesc2(String desc2) {
            this.desc2 = desc2;
        }

        public String getDesc3() {
            return desc3;
        }

        public void setDesc3(String desc3) {
            this.desc3 = desc3;
        }
    }
    public class HealthBanner implements Serializable {
        private static final long serialVersionUID = -1613720253677610610L;
        public String image;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Reordering reordering;

    public class Reordering implements Serializable{
        boolean reordering_enabled = true;
        ArrayList<String> order_status;
        private static final long serialVersionUID = -1613720253677610610L;

        public boolean isReordering_enabled() {
            return reordering_enabled;
        }

        public void setReordering_enabled(boolean reordering_enabled) {
            this.reordering_enabled = reordering_enabled;
        }

        public ArrayList<String> getStatus() {
            return order_status;
        }

        public void setStatus(ArrayList<String> status) {
            this.order_status = status;
        }
    }

    public Reordering getReordering() {
        return reordering;
    }

    public void setReordering(Reordering reordering) {
        this.reordering = reordering;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ContestConf contestConfiguration= new ContestConf();

    public ContestConf getContestConfiguration() {
        return contestConfiguration;
    }

    public void setContestConfiguration(ContestConf contestConfiguration) {
        this.contestConfiguration = contestConfiguration;
    }

    public class ContestConf implements Serializable {
         private static final long serialVersionUID = -1613720253677610610L;
        private String questionExpired ; //text below submit button
        private String questionExpiresAt ;//text below submit button
        private String futureOrderText ;//text shown when order start time is of future
        private String futureQuestionText ;//text shown when question start time is of future
        private String submissionPending ;//question status text if (task.canAnswer!=null && task.canAnswer && task.status == null)
        private String resultPending ;//if (task.status!=null && task.status == STATUSPARTICIPATED)
        private String correctAnswer ;//if (task.status!=null && task.status == STATUSPASS)
        private String incorrectAnswer ;//if (task.status!=null && task.status == STATUSFAIL)
        private String submissionExpired ;//if (task.canAnswer!=null && task.canAnswer==false && task.status == null)
        private String orderHeader ;
        private String submissionNotOpen;
        private String bannerUrl = "";
         private String emptyDescriptionText;
         private String matchTied ;
         private String matchRescheduled ;
         private String matchAbandoned ;
         private String activeCampaign;
         private String pastCampaign;
         private String offerExpired;
         private String answerSubmissionSuccess;
        public String getQuestionExpired() {
            if (questionExpired == null){
                return "Submission is now closed";
            }
            return questionExpired;
        }

        public void setQuestionExpired(String questionExpired) {
            this.questionExpired = questionExpired;
        }

        public String getQuestionExpiresAt() {
            if (questionExpiresAt == null){
                return "Submission closes at ";
            }
            return questionExpiresAt;
        }

        public void setQuestionExpiresAt(String questionExpiresAt) {
            this.questionExpiresAt = questionExpiresAt;
        }

        public String getFutureOrderText() {
            if (futureOrderText == null){
                return "Order";
            }
            return futureOrderText;
        }

        public void setFutureOrderText(String futureOrderText) {
            this.futureOrderText = futureOrderText;
        }

        public String getFutureQuestionText() {
            if (futureQuestionText == null){
                return "Question";
            }
            return futureQuestionText;
        }

        public void setFutureQuestionText(String futureQuestionText) {
            this.futureQuestionText = futureQuestionText;
        }

        public String getSubmissionPending() {
            if (submissionPending == null){
                return "Waiting for submission";
            }
            return submissionPending;
        }

        public void setSubmissionPending(String submissionPending) {
            this.submissionPending = submissionPending;
        }

        public String getResultPending() {
            if (resultPending == null){
                return "Answer submitted";
            }
            return resultPending;
        }

        public void setResultPending(String resultPending) {
            this.resultPending = resultPending;
        }

        public String getCorrectAnswer() {
            if (correctAnswer == null){
                return "Correct prediction";
            }
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getIncorrectAnswer() {
            if (incorrectAnswer == null){
                return "Incorrect prediction";
            }
            return incorrectAnswer;
        }

        public void setIncorrectAnswer(String incorrectAnswer) {
            this.incorrectAnswer = incorrectAnswer;
        }

        public String getSubmissionExpired() {
            if (submissionExpired == null){
                return "Answer not submitted";
            }
            return submissionExpired;
        }

        public void setSubmissionExpired(String submissionExpired) {
            this.submissionExpired = submissionExpired;
        }

        public String getOrderHeader() {
            if (orderHeader == null){
                return "Order";
            }
            return orderHeader;
        }

        public void setOrderHeader(String orderHeader) {
            this.orderHeader = orderHeader;
        }

        public String getBannerUrl() {
            if(bannerUrl==null)
                return "";
            return bannerUrl;
        }

        public void setBannerUrl(String bannerUrl) {
            this.bannerUrl = bannerUrl;
        }

        public String getEmptyDescriptionText() {
            if (futureOrderText == null){
                return "Order";
            }
            return emptyDescriptionText;
        }

        public void setEmptyDescriptionText(String emptyDescriptionText) {
            this.emptyDescriptionText = emptyDescriptionText;
        }

        public String getActiveCampaign() {
            if(activeCampaign==null)
                return "Ongoing Contests";
            return activeCampaign;
        }

        public void setActiveCampaign(String activeCampaign) {
            this.activeCampaign = activeCampaign;
        }

        public String getPastCampaign() {
            if(pastCampaign==null)
                return "Past Campaigns";
            return pastCampaign;
        }

        public void setPastCampaign(String pastCampaign) {
            this.pastCampaign = pastCampaign;
        }

        public String getOfferExpired() {
            if(offerExpired == null)
                return "Campaign Over";
            return offerExpired;
        }

        public void setOfferExpired(String offerExpired) {
            this.offerExpired = offerExpired;
        }

        public String getMatchTied() {
            if (matchTied==null){
                return "Match Tied";
            }
            return matchTied;
        }

        public void setMatchTied(String matchTied) {
            this.matchTied = matchTied;
        }

        public String getMatchRescheduled() {
            if (matchRescheduled==null){
                return "Match Rescheduled";
            }
            return matchRescheduled;
        }

        public void setMatchRescheduled(String matchRescheduled) {
            this.matchRescheduled = matchRescheduled;
        }

        public String getMatchAbandoned() {
            if (matchAbandoned==null){
                return "Match Abandoned";
            }
            return matchAbandoned;
        }

        public void setMatchAbandoned(String matchAbandoned) {
            this.matchAbandoned = matchAbandoned;
        }

        public String getAnswerSubmissionSuccess() {
            if (answerSubmissionSuccess==null){
                return "Answer submitted successfully";
            }
            return answerSubmissionSuccess;
        }

        public void setAnswerSubmissionSuccess(String answerSubmissionSuccess) {
            this.answerSubmissionSuccess = answerSubmissionSuccess;
        }

        public String getSubmissionNotOpen() {
            if (submissionNotOpen == null){
                return "Submissions are not open yet";
            }
            return submissionNotOpen;
        }

        public void setSubmissionNotOpen(String submissionNotOpen) {
            this.submissionNotOpen = submissionNotOpen;
        }
    }
    @DatabaseField
    private boolean show_user_settings = true;

    public boolean isShow_user_settings() {
        return show_user_settings;
    }

    public void setShow_user_settings(boolean show_user_settings) {
        this.show_user_settings = show_user_settings;
    }

    @DatabaseField
    private boolean wallet_categorisation = true;

    public boolean isWallet_categorisation() {
        return wallet_categorisation;
    }

    public void setWallet_categorisation(boolean wallet_categorisation) {
        this.wallet_categorisation = wallet_categorisation;
    }

    @DatabaseField
    private boolean convenience_charge_applicable = false;

    public boolean isConvenience_charge_applicable() {
        return convenience_charge_applicable;
    }

    public void setConvenience_charge_applicable(boolean convenience_charge_applicable) {
        this.convenience_charge_applicable = convenience_charge_applicable;
    }

    @DatabaseField
    private String payment_due_header = "";

    public String getPayment_due_header() {
        return payment_due_header;
    }

    public void setPayment_due_header(String payment_due_header) {
        this.payment_due_header = payment_due_header;
    }

    @DatabaseField
    private int auth_expiry_as_percent = 75;

    public int getAuth_expiry_as_percent() {
        return auth_expiry_as_percent;
    }

    public void setAuth_expiry_as_percent(int auth_expiry_as_percent) {
        this.auth_expiry_as_percent = auth_expiry_as_percent;
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private SocialDistancing social_distancing;

    public SocialDistancing getSocial_distancing() {
        return social_distancing;
    }

    public void setSocial_distancing(SocialDistancing social_distancing) {
        this.social_distancing = social_distancing;
    }

    public class SocialDistancing implements Serializable{
        private static final long serialVersionUID = -1613720253677610610L;

        private String info_url = "";
        private String vendor_image = "";
        private String eta_text = "";
        private long supported_company;
        private ArrayList<Long> supported_locations;

        public String getInfo_url() {
            return info_url;
        }

        public void setInfo_url(String info_url) {
            this.info_url = info_url;
        }

        public String getVendor_image() {
            return vendor_image;
        }

        public void setVendor_image(String vendor_image) {
            this.vendor_image = vendor_image;
        }

        public long getSupported_company() {
            return supported_company;
        }

        public void setSupported_company(long supported_company) {
            this.supported_company = supported_company;
        }

        public ArrayList<Long> getSupported_locations() {
            return supported_locations;
        }

        public void setSupported_locations(ArrayList<Long> supported_locations) {
            this.supported_locations = supported_locations;
        }

        public String getEta_text() {
            if (eta_text==null || eta_text.equalsIgnoreCase("")){
                return "Estimated time to enter the cafeteria at";
            } else {
                return eta_text;
            }
        }

        public void setEta_text(String eta_text) {
            this.eta_text = eta_text;
        }
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private BluetoothDistancing bluetooth_distancing;

    public BluetoothDistancing getBluetooth_distancing() {
        if(AppUtils.isEligibleForBluetooth())
            return bluetooth_distancing;
        else
            return null;
    }

    public void setBluetooth_distancing(BluetoothDistancing bluetooth_distancing) {
        this.bluetooth_distancing = bluetooth_distancing;
    }

    public class BluetoothDistancing implements Serializable{
        private static final long serialVersionUID = -1613720253677610610L;

        private int rssi = -60;
        private int threshold = 2;
        private long duration = 60000;
        private long max_duration = 30*60*1000;
        private String info_url = "";
        private long supported_company;
        private ArrayList<Long> supported_locations;
        private String bt_info_text = "";
        private long interaction_lost_duration = 10*60*1000;
        private int send_violation_count = 10;
        private int violation_api_max_days = 7;
        private int contact_tracing_api_max_days = 15;
        private int proximity_switch = 0;
        private int shift_duration = 0;
        private boolean share_option = false;
        private boolean delete_option = false;
        private boolean is_all_day_tracking_enabled = false;
        private String tracking_start_time_end_time = "";

        public int getShift_duration() {
            return shift_duration;
        }

        public void setShift_duration(int shift_duration) {
            this.shift_duration = shift_duration;
        }

        public int getContact_tracing_api_max_days() {
            return contact_tracing_api_max_days;
        }

        public void setContact_tracing_api_max_days(int contact_tracing_api_max_days) {
            this.contact_tracing_api_max_days = contact_tracing_api_max_days;
        }

        public int getProximity_switch() {
            return proximity_switch;
        }

        public void setProximity_switch(int proximity_switch) {
            this.proximity_switch = proximity_switch;
        }

        public boolean getShare_option() {
            return share_option;
        }

        public void setShare_option(boolean share_option) {
            this.share_option = share_option;
        }

        public boolean getDelete_option() {
            return delete_option;
        }

        public void setDelete_option(boolean delete_option) {
            this.delete_option = delete_option;
        }

        public boolean getIs_all_day_tracking_enabled() {
            return is_all_day_tracking_enabled;
        }

        public void setIs_all_day_tracking_enabled(boolean is_all_day_tracking_enabled) {
            this.is_all_day_tracking_enabled = is_all_day_tracking_enabled;
        }

        public String getTracking_start_time_end_time() {
            return tracking_start_time_end_time != null? tracking_start_time_end_time : "";
        }

        public void setTracking_start_time_end_time(String tracking_start_time_end_time) {
            this.tracking_start_time_end_time = tracking_start_time_end_time;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public String getInfo_url() {
            return info_url;
        }

        public void setInfo_url(String info_url) {
            this.info_url = info_url;
        }

        public long getSupported_company() {
            return supported_company;
        }

        public void setSupported_company(long supported_company) {
            this.supported_company = supported_company;
        }

        public ArrayList<Long> getSupported_locations() {
            return supported_locations;
        }

        public void setSupported_locations(ArrayList<Long> supported_locations) {
            this.supported_locations = supported_locations;
        }

        public long getMax_duration() {
            return max_duration;
        }

        public void setMax_duration(long max_duration) {
            this.max_duration = max_duration;
        }

        public String getBt_info_text() {
            return bt_info_text;
        }

        public void setBt_info_text(String bt_info_text) {
            this.bt_info_text = bt_info_text;
        }

        public long getInteraction_lost_duration() {
            return interaction_lost_duration;
        }

        public void setInteraction_lost_duration(long interaction_lost_duration) {
            this.interaction_lost_duration = interaction_lost_duration;
        }

        public int getSend_violation_count() {
            return send_violation_count;
        }

        public void setSend_violation_count(int send_violation_count) {
            this.send_violation_count = send_violation_count;
        }

        public int getViolation_api_max_days() {
            return violation_api_max_days;
        }

        public void setViolation_api_max_days(int violation_api_max_days) {
            this.violation_api_max_days = violation_api_max_days;
        }
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Occupancy occupancy;

    public Occupancy getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Occupancy occupancy) {
        this.occupancy = occupancy;
    }

    public class Occupancy implements Serializable{

        private static final long serialVersionUID = -1613720253677610610L;

        private OccupancySpace lift_lobby;
        private OccupancySpace washroom;

        public OccupancySpace getLift_lobby() {
            return lift_lobby;
        }

        public void setLift_lobby(OccupancySpace lift_lobby) {
            this.lift_lobby = lift_lobby;
        }

        public OccupancySpace getWashroom() {
            return washroom;
        }

        public void setWashroom(OccupancySpace washroom) {
            this.washroom = washroom;
        }

        public class OccupancySpace implements Serializable{

            private CongestionMessage message;

            public CongestionMessage getMessage() {
                return message;
            }

            public void setMessage(CongestionMessage message) {
                this.message = message;
            }

            public class CongestionMessage implements Serializable{
                private String low;
                private String medium;
                private String high;

                public String getLow() {
                    return low;
                }

                public void setLow(String low) {
                    this.low = low;
                }

                public String getMedium() {
                    return medium;
                }

                public void setMedium(String medium) {
                    this.medium = medium;
                }

                public String getHigh() {
                    return high;
                }

                public void setHigh(String high) {
                    this.high = high;
                }
            }
        }
    }

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public SpaceManagement space_management;

    public SpaceManagement getSpace_management() {
        return space_management;
    }

    public void setSpace_management(SpaceManagement space_management) {
        this.space_management = space_management;
    }

    public class SpaceManagement implements Serializable{
        private static final long serialVersionUID = -1613720253677610610L;
        private String title;
        private String space_booking_in_order_detail;
        @SerializedName("data")
        private ArrayList<SpaceType> spaceTypes;
        private String on_dashboard;
        private boolean show_rebook = true;
        private boolean show_extend_booking = true;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSpace_booking_in_order_detail() {
            return space_booking_in_order_detail;
        }

        public void setSpace_booking_in_order_detail(String space_booking_in_order_detail) {
            this.space_booking_in_order_detail = space_booking_in_order_detail;
        }

        public ArrayList<SpaceType> getSpaceTypes() {
            return spaceTypes;
        }

        public void setSpaceTypes(ArrayList<SpaceType> spaceTypes) {
            this.spaceTypes = spaceTypes;
        }

        public String getOn_dashboard() {
            return on_dashboard;
        }

        public void setOn_dashboard(String on_dashboard) {
            this.on_dashboard = on_dashboard;
        }

        public boolean isShow_rebook() {
            return show_rebook;
        }

        public void setShow_rebook(boolean show_rebook) {
            this.show_rebook = show_rebook;
        }

        public boolean isShow_extend_booking() {
            return show_extend_booking;
        }

        public void setShow_extend_booking(boolean show_extend_booking) {
            this.show_extend_booking = show_extend_booking;
        }
    }
    public class SpaceType implements Serializable{
        private String key;
        private String name;
        private String description;
        private String icon_url;
        private String available_spaces_text;
        private String facility_text;
        private GuestDetails guest_details;
        private boolean is_food_allowed;
        private String qr_code_entry_text;
        private String qr_code_exit_text;
        private String approval_pending_title;
        private String approval_pending_desc;
        private int guest_min;
        private int guest_max;
        private int working_days;
        private String max_qty_error;
        private String meal_section_header;
        private String space_section_header;
        private String meal_section_text;
        private String space_section_text;
        private boolean isSelected;
        private String toolbar_header;
        private String space_name;
        private String cart_error = "Something went wrong. Please try to reduce the number of guests selected";

        public String getCart_error() {
            if(cart_error == null){
                return "";
            }
            return cart_error;
        }

        public void setCart_error(String cart_error) {
            this.cart_error = cart_error;
        }

        public String getMeal_section_header() {
            return meal_section_header == null ? "" : meal_section_header;
        }

        public void setMeal_section_header(String meal_section_header) {
            this.meal_section_header = meal_section_header;
        }

        public String getSpace_section_header() {
            return space_section_header == null ? "" : space_section_header;
        }

        public void setSpace_section_header(String space_section_header) {
            this.space_section_header = space_section_header;
        }

        public String getMeal_section_text() {
            return meal_section_text == null ? "" : meal_section_text;
        }

        public void setMeal_section_text(String meal_section_text) {
            this.meal_section_text = meal_section_text;
        }

        public String getSpace_section_text() {
            return space_section_text == null ? "" : space_section_text;
        }

        public void setSpace_section_text(String space_section_text) {
            this.space_section_text = space_section_text;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            if(name==null)
                return "";
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            if(description==null)
                return "";
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon_url() {
            return icon_url;
        }

        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }

        public String getAvailable_spaces_text() {
            return available_spaces_text;
        }

        public void setAvailable_spaces_text(String available_spaces_text) {
            this.available_spaces_text = available_spaces_text;
        }

        public String getFacility_text() {
            return facility_text == null? "" : facility_text;
        }

        public void setFacility_text(String facility_text) {
            this.facility_text = facility_text;
        }

        public GuestDetails getGuest_details() {
            return guest_details;
        }

        public void setGuest_details(GuestDetails guest_details) {
            this.guest_details = guest_details;
        }

        public boolean isIs_food_allowed() {
            return is_food_allowed;
        }

        public void setIs_food_allowed(boolean is_food_allowed) {
            this.is_food_allowed = is_food_allowed;
        }

        public String getQr_code_entry_text() {
            return qr_code_entry_text;
        }

        public void setQr_code_entry_text(String qr_code_entry_text) {
            this.qr_code_entry_text = qr_code_entry_text;
        }

        public String getQr_code_exit_text() {
            return qr_code_exit_text;
        }

        public void setQr_code_exit_text(String qr_code_exit_text) {
            this.qr_code_exit_text = qr_code_exit_text;
        }

        public String getApproval_pending_title() {
            return approval_pending_title;
        }

        public void setApproval_pending_title(String approval_pending_title) {
            this.approval_pending_title = approval_pending_title;
        }

        public String getApproval_pending_desc() {
            return approval_pending_desc;
        }

        public void setApproval_pending_desc(String approval_pending_desc) {
            this.approval_pending_desc = approval_pending_desc;
        }

        public int getGuest_max() {
            return guest_max;
        }

        public void setGuest_max(int guest_max) {
            this.guest_max = guest_max;
        }

        public int getGuest_min() {
            return guest_min;
        }

        public void setGuest_min(int guest_min) {
            this.guest_min = guest_min;
        }

        public int getWorking_days() {
            return working_days;
        }

        public void setWorking_days(int working_days) {
            this.working_days = working_days;
        }

        public String getMax_qty_error() {
            return max_qty_error;
        }

        public void setMax_qty_error(String max_qty_error) {
            this.max_qty_error = max_qty_error;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public String getToolbar_header() {
            if(toolbar_header == null)
                return "";
            return toolbar_header;
        }

        public void setToolbar_header(String toolbar_header) {
            this.toolbar_header = toolbar_header;
        }

        public String getSpace_name() {
            if(space_name == null)
                return "";
            return space_name;
        }

        public void setSpace_name(String space_name) {
            this.space_name = space_name;
        }

        public class GuestDetails implements Serializable{
            private int name;
            private int phone_no;
            private int email;
            private boolean notification_box;

            public int getName() {
                return name;
            }

            public void setName(int name) {
                this.name = name;
            }

            public int getPhone_no() {
                return phone_no;
            }

            public void setPhone_no(int phone_no) {
                this.phone_no = phone_no;
            }

            public int getEmail() {
                return email;
            }

            public void setEmail(int email) {
                this.email = email;
            }

            public boolean isNotification_box() {
                return notification_box;
            }

            public void setNotification_box(boolean notification_box) {
                this.notification_box = notification_box;
            }
        }
    }





}
