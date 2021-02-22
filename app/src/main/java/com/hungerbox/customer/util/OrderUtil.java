package com.hungerbox.customer.util;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.Location;

/**
 * Created by peeyush on 5/7/16.
 */
public class OrderUtil {

    public static final String PAYMENT_PENDING = "payment_pending";
    public static final String PRE_ORDER = "preorder";
    public static final String NEW = "new";
    public static final String CONFIRMED = "confirmed";
    public static final String PROCESSED = "processed";
    public static final String DELIVERED = "delivered";
    public static final String REJECTED = "rejected";
    public static final String NOT_COLLECTED = "not collected";
    public static final String PAYMENT_FAILED = "payment_failed";
    public static final String AGENT_PICKED = "agent_picked";
    public static final String ATTENTION_REQUIRED = "attention_required";
    public static final String HANDED_OVER = "handed_over";
    public static final String CANCELLED = "cancelled";
    public static final String FULFILLED = "fulfilled";
    public static final String PARTIALLY_CONFIRMED = "partially_confirmed";
    public static final String PARTIALLY_PROCESSED = "partially_processed";
    public static final String PARTIALLY_DELIVERED = "partially_delivered";
    public static final String APPROVAL_PENDING = "approval_pending";


    public static final String ORDER_TYPE_INDIVIDUAL = "individual";
    public static final String ORDER_TYPE_GROUP = "group_order";
    public static final String ORDER_TYPE_ADMIN_ORDER = "admin_order";
    public static final String ORDER_TYPE_CATERING = "catering";
    public static final String ORDER_TYPE_GUEST_ORDER = "guest_order";
    public static final String ORDER_TYPE_BOOKING = "event_booking";
    public static final String ORDER_WALLET_SOURCE_EXTERNAL = "external";
    public static final String ORDER_WALLET_SOURCE_INTERNAL = "internal";


    public static String getOrderStatusLabel(String status) {
        switch (status) {
            case PAYMENT_FAILED:
                return "PAYMENT FAILED";
            case ATTENTION_REQUIRED:
            case AGENT_PICKED:
            case NEW:
                return "NEW";
            case CONFIRMED:
                return "CONFIRMED";
            case PROCESSED:
                return "PROCESSED";
            case DELIVERED:
                return "DELIVERED";
            case FULFILLED:
                return "FULFILLED";
            case REJECTED:
                return "CANCELLED";
            case CANCELLED:
                return "CANCELLED";
            case NOT_COLLECTED:
                return "NOT COLLECTED";
            case PAYMENT_PENDING:
                return "PAYMENT PENDING";
            case HANDED_OVER:
                return "HANDED OVER TO SECURITY";
            case PRE_ORDER:
                return "YOUR PRE-ORDER IS PLACED!";
            default:
                return "DEFAULT";
        }
    }

    public static Boolean isFinalState(String status) {
        if(status == null){
            return false;
        }
        if(status.equalsIgnoreCase(FULFILLED) || status.equalsIgnoreCase(DELIVERED) || status.equalsIgnoreCase(REJECTED) || status.equalsIgnoreCase(NOT_COLLECTED) || status.equalsIgnoreCase(PAYMENT_FAILED) || status.equalsIgnoreCase(PAYMENT_PENDING) || status.equalsIgnoreCase(HANDED_OVER)){
            return true;
        }
        return false;
    }

    public static String getOrderTypeLabel(String status) {
        switch (status) {
            case ORDER_TYPE_INDIVIDUAL:
                return "Individual Order";
            case ORDER_TYPE_GROUP:
                return "Group Order";
            case ORDER_TYPE_ADMIN_ORDER:
                return "Admin Order";
            case ORDER_TYPE_CATERING:
                return "Catering Order";
            case ORDER_TYPE_GUEST_ORDER:
                return "Guest Order";
            default:
                return "Indiviudal Order";
        }
    }

    public static int getImageId(String orderStatus) {
        switch (orderStatus) {
            case PAYMENT_FAILED:
                return R.drawable.orderfood;
            case ATTENTION_REQUIRED:
            case AGENT_PICKED:
            case NEW:
                return R.drawable.icon_order_received;
            case CONFIRMED:
                return R.drawable.icon_orderconfirmed;
            case PROCESSED:
                return R.drawable.icon_orderalmostready;
            case DELIVERED:
                return R.drawable.icon_orderready;
            case REJECTED:
                return R.drawable.icon_orderready;
            case NOT_COLLECTED:
                return R.drawable.icon_orderready;
            case PAYMENT_PENDING:
                return R.drawable.orderfood;
            case HANDED_OVER:
                return R.drawable.orderfood;
            case PRE_ORDER:
                return R.drawable.orderfood;
            default:
                return R.drawable.orderfood;

        }
    }


    public static int getOrderStatusImageId(String orderStatus) {
        switch (orderStatus) {
            case PAYMENT_FAILED:
                return R.drawable.history_page_payment_failed;
            case ATTENTION_REQUIRED:
            case AGENT_PICKED:
            case NEW:
                return R.drawable.history_page_order_placed_icon;
            case CONFIRMED:
                return R.drawable.history_page_order_placed_icon;
            case PROCESSED:
                return R.drawable.history_page_ready_icon;
            case DELIVERED:
                return R.drawable.history_page_delivered_icon;
            case FULFILLED:
                return R.drawable.fulfilled_with_status;
            case REJECTED:
                return R.drawable.history_page_cancelled_icon;
            case NOT_COLLECTED:
                return R.drawable.history_page_not_collected_icon;
            case PAYMENT_PENDING:
                return R.drawable.history_page_payment_pending_icon;
            case HANDED_OVER:
                return R.drawable.history_page_delivered_icon;
            case PRE_ORDER:
                return R.drawable.history_page_preorder_icon;
            case PARTIALLY_CONFIRMED:
                return R.drawable.in_process_icon;
            case PARTIALLY_DELIVERED:
                return R.drawable.in_process_icon;
            case PARTIALLY_PROCESSED:
                return R.drawable.in_process_icon;
            default:
                return 0;

        }
    }

    public static int getOrderStatusImageIdForOrderDetail(String orderStatus) {
        switch (orderStatus) {
            case PAYMENT_FAILED:
                return R.drawable.ic_payment_failed;
            case ATTENTION_REQUIRED:
            case AGENT_PICKED:
            case NEW:
                return 0;
            case CONFIRMED:
                return 0;
            case PROCESSED:
                return 0;
            case DELIVERED:
                return R.drawable.ic_delivered;
            case FULFILLED:
                return R.drawable.ic_fulfilled;
            case REJECTED:
                return R.drawable.ic_cancelled;
            case NOT_COLLECTED:
                return R.drawable.ic_not_collected;
            case PAYMENT_PENDING:
                return R.drawable.ic_payment_pending;
            case HANDED_OVER:
                return  R.drawable.ic_delivered;
            case PRE_ORDER:
                return R.drawable.ic_pre_order;
            default:
                return 0;

        }
    }
    public static int getSentimentReviewColor(int rating){
        switch (rating){
            case 1:
                return R.color.rating_sad;
            case 3:
                return R.color.rating_neutral;
            case 5:
                return R.color.rating_happy;
            default:
                return R.color.text_dark;
        }
    }

    public static int getSentimentReviewImage(int rating){
        switch (rating){
            case 1:
                return R.drawable.sad;
            case 3:
                return R.drawable.netural;
            case 5:
                return R.drawable.smiling;
            default:
                return R.drawable.smiling;
        }
    }

}
