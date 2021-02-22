package com.hungerbox.customer.offline.modelOffline;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.OrderUtil;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.UUID;

import io.realm.annotations.PrimaryKey;

@DatabaseTable(tableName = "Order_Offline")
public class OrderOffline implements Serializable {
    static final long serialVersionUID = 1L;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public OrderUserResponseOffline employee;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public OrderVendorReposneOffline vendor;
    @SerializedName("rating")
    public String rating;
    @SerializedName("vat")
    public double tax;
    @SerializedName("service_tax")
    public double serviceTax;
    @DatabaseField
    @SerializedName("cgst")
    public double cgst;
    @DatabaseField
    @SerializedName("sgst")
    public double sgst;

    public boolean isGroupOrder() {
        return isGroupOrder;
    }

    public void setGroupOrder(boolean groupOrder) {
        isGroupOrder = groupOrder;
    }

    public boolean isGroupOrder;
    public ArrayList<Long> users;
    public String comment;
    @DatabaseField
    @SerializedName("delivery_charge")
    public double deliveryCharge = 0;
    @DatabaseField
    @SerializedName("container_charge")
    public double containerCharge;
    @SerializedName("cafe")
    public String cafe;
    @SerializedName("reject_message")
    public String rejectMessage;
    @SerializedName("guest_type")
    public String guestType;
    @SerializedName("guest_list")
    public ArrayList<OrderGuestInfoOffline> orderGuestInfo;
    @SerializedName("group_users")
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public OrderUsersReponseOffline groupUser;
    @DatabaseField
    @SerializedName("used_id")
    public long primaryUserId;
    @DatabaseField
    @SerializedName("convenience_fee")
    public double convenienceFee = 0;
    @DatabaseField
    @SerializedName("order_hash")
    public String orderHash;
    @SerializedName("couch_order_id")
    String docId;
    @DatabaseField(generatedId = true,columnName = "CID")
    @SerializedName("order_id")
    long id;
    @DatabaseField
    @SerializedName("vendor_id")
    long vendorId;
    @DatabaseField
    @SerializedName("occasion_id")
    long occasionId;
    @DatabaseField
    @SerializedName("employee_id")
    long userId;
    @DatabaseField
    String name;
    @DatabaseField
    @SerializedName("vendor_order_ref")
    String orderId;
    @DatabaseField
    @SerializedName("order_code")
    String pin;
    @DatabaseField
    @SerializedName("status")
    String orderStatus;
    @DatabaseField
    @SerializedName("quantity")
    int quantity;
    @DatabaseField
    @SerializedName("value")
    double price;
    @DatabaseField
    @SerializedName("confirmed_at")
    long confirmedAt;
    @DatabaseField
    @SerializedName("estimated_delivery_time")
    long estimatedDeliveryTime;
    @DatabaseField
    @SerializedName("processed_at")
    long processedAt;
    @DatabaseField
    @SerializedName("delivered_at")
    long deliveredAt;
    @DatabaseField
    @SerializedName("created_at")
    long createdAt;
    @DatabaseField
    @SerializedName("updated_at")
    long updatedAt;
    @DatabaseField
    @SerializedName("vendor_name")
    public String vendorName;
    @DatabaseField
    @SerializedName("is_buffet")
    int isBuffet;
    @SerializedName("contact_number")
    String phoneNum;
    @SerializedName("gst_enabled")
    boolean gstEnabled = true;
    /*
    qr, nfc, pin, null
     */
    @SerializedName("delivery_method")
    String deliverymethod;
    @DatabaseField
    @SerializedName("wallet_used")
    double walletUsed;
    @DatabaseField
    @SerializedName("online_payment")
    double onlinePayment;
    @SerializedName("preorder_time")
    long orderTime;
    @SerializedName("location_id")
    long locationId;
    @DatabaseField
    @SerializedName("location_name")
    String locationName;
    @DatabaseField
    long waitTime;
    @DatabaseField
    @SerializedName("type")
    String type;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @SerializedName("products")
    OrderProductResponseOffline products;
    @SerializedName("kot")
    int kot;
    @SerializedName("order_queue")
    int orderQueue;
    @DatabaseField
    @SerializedName("pickup_type")
    String orderPickType;
    @SerializedName("external_payment_source")
    OrderPaymentOffline paymentModes;
    @SerializedName("payment_data")
    OrderPaymentResponseOffline orderPaymentResponse;
    @SerializedName("booking_id")
    long bookingId;
    @SerializedName("order_type")
    String orderType;
    @SerializedName("location")
    LocationSingleOffline location;
    @SerializedName("wallets")
    OrderWalletResposneOffline wallets;
    @SerializedName("internalWalletUsed")
    boolean internalWalletUsed = true;
    PaymentMethodOffline paymentMethod;
    @SerializedName("product_id")
    private long productId;
    private boolean isOrderFromServer = false;

    @SerializedName("desk_reference")
    private String deskReference = null;

    @DatabaseField
    private String qrCoder;

    public String getDeskReference() {
        if (deskReference == null)
            deskReference = "";
        return deskReference;
    }

    public String getQrCoder() {
        return qrCoder;
    }

    public void setQrCoder(String qrCoder) {
        this.qrCoder = qrCoder;
    }

    public void setDeskReference(String deskReference) {
        this.deskReference = deskReference;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public OrderWalletResposneOffline getWallets() {
        if (wallets == null)
            wallets = new OrderWalletResposneOffline();
        return wallets;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId() {
        UUID uuid = UUID.randomUUID();
        this.docId = uuid.toString();
    }

    public LocationOffline getLocation() {
        return location.location;
    }

    public void setLocation(LocationOffline location) {
        this.location.location = location;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public OrderPaymentOffline getPaymentModes() {
        return paymentModes;
    }

    public void setPaymentModes(OrderPaymentOffline paymentModes) {
        this.paymentModes = paymentModes;
    }

    public String getRejectMessage() {
        if (rejectMessage == null)
            return "";
        return rejectMessage;
    }

    public void setRejectMessage(String rejectMessage) {
        this.rejectMessage = rejectMessage;
    }

    public double getConvenienceFee() {
        return convenienceFee;
    }

    public void setConvenienceFee(double convenienceFee) {
        this.convenienceFee = convenienceFee;
    }

    public double getWalletUsed() {
        return walletUsed;
    }

    public void setWalletUsed(int walletUsed) {
        this.walletUsed = walletUsed;
    }

    public void setWalletUsed(double walletUsed) {
        this.walletUsed = walletUsed;
    }

    public String getWalletUsedString() {
        return String.format("₹ %.2f", walletUsed);
    }

    public double getOnlinePayment() {
        return onlinePayment;
    }

    public void setOnlinePayment(int onlinePayment) {
        this.onlinePayment = onlinePayment;
    }

    public void setOnlinePayment(double onlinePayment) {
        this.onlinePayment = onlinePayment;
    }

    public String getOnlinePaymentString() {
        return String.format("₹ %.2f", onlinePayment);
    }

    public long getId() {
        return id;
    }

    public OrderOffline setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrderOffline setName(String name) {
        this.name = name;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderOffline setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getPin() {
        return pin;
    }

    public OrderOffline setPin(String pin) {
        this.pin = pin;
        return this;
    }

    public String getOrderStatus() {
        if (orderStatus == null)
            orderStatus = OrderUtil.NEW;
        return orderStatus;
    }

    public OrderOffline setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public OrderOffline setPrice(double price) {
        this.price = price;
        return this;
    }

    public double getTotalPrice() {
//        return price + deliveryCharge + containerCharge + tax + serviceTax
//                + convenienceFee + cgst + sgst;
        return price + deliveryCharge + containerCharge
                + convenienceFee + cgst + sgst;
    }

    public String getPriceString() {
        return String.format("₹ %.2f", price);
    }

    public OrderOffline addPrice(double priceToAdd) {
        this.price += priceToAdd;
        this.quantity++;
        return this;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public ArrayList<OrderProductOffline> getProducts() {
        if (products == null) {
            products = new OrderProductResponseOffline();
            products.products = new ArrayList<>();
        }
        return products.products;
    }

    public OrderOffline setProducts(ArrayList<OrderProductOffline> products) {
        if (this.products == null)
            this.products = new OrderProductResponseOffline();

        this.products.products = products;
        return this;
    }

    public void setProducts(OrderProductResponseOffline products) {
        this.products = products;
    }

    public long getVendorId() {
        return vendorId;
    }

    public OrderOffline setVendorId(long vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getConfirmedAt() {
        return confirmedAt;
//        return (confirmedAt==null)?0: Long.parseLong(confirmedAt);
    }

    public void setConfirmedAt(long confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public long getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
//        return (estimatedDeliveryTime==null)?0: Long.parseLong(estimatedDeliveryTime);
    }

    public void setEstimatedDeliveryTime(long estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public long getEstimatedDeliveryTimeInmillis() {
        return estimatedDeliveryTime * 1000l;
    }

    public long getProcessedAt() {
        return processedAt;
//        return (processedAt==null)?0: Long.parseLong(processedAt);
    }

    public void setProcessedAt(long processedAt) {
        this.processedAt = processedAt;
    }

    public long getDeliveredAt() {
        return deliveredAt;
//        return (deliveredAt==null)?0: Long.parseLong(deliveredAt);
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public long getCreatedAt() {
        return createdAt;
//        return (createdAt==null)?0: Long.parseLong(createdAt);
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
//        return (updatedAt==null)?0: Long.parseLong(updatedAt);
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVendorName() {
        if (vendor == null)
            vendorName = "";
        else
            vendorName = vendor.vendor.getUserName();
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public long getOccasionId() {
        return occasionId;
    }

    public void setOccasionId(long occasionId) {
        this.occasionId = occasionId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDeliverymethod() {
        return deliverymethod;
    }

    public void setDeliverymethod(String deliverymethod) {
        this.deliverymethod = deliverymethod;
    }

    public OrderUserResponseOffline getEmployee() {
        return employee;
    }

    public void setEmployee(OrderUserResponseOffline employee) {
        this.employee = employee;
    }

    public OrderVendorReposneOffline getVendor() {
        if (vendor == null)
            vendor = new OrderVendorReposneOffline();
        return vendor;
    }

    public void setVendor(OrderVendorReposneOffline vendor) {
        this.vendor = vendor;
    }

    public void setVendor(VendorOffline vendor) {
        if (this.vendor == null || this.vendor.vendor == null) {
            this.vendor = new OrderVendorReposneOffline();
            this.vendor.vendor = new OrderVendorOffline();
            this.vendor.vendor.type = vendor.type;
            this.vendor.vendor.id = vendor.id;
            this.vendor.vendor.buffet = vendor.isBuffetAvailable;
        } else {
            this.vendor.vendor.type = vendor.type;
            this.vendor.vendor.id = vendor.id;
            this.vendor.vendor.buffet = vendor.isBuffetAvailable;
        }
    }

    public int getIsBuffet() {
        return isBuffet;
    }

    public void setIsBuffet(int isBuffet) {
        this.isBuffet = isBuffet;
    }

    public OrderOffline removePrice(double priceToSubtract) {
        this.price -= priceToSubtract;
        this.quantity--;
        return this;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public long getOrderTimeInMillis() {
        return orderTime * 1000l;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getCustomerName() {
        if (employee == null)
            return "";
        else
            return employee.user.getUserName();
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getContainerCharge() {
        containerCharge = 0;
        if (products != null) {
            for (OrderProductOffline orderProduct : getProducts()) {
                containerCharge += orderProduct.getConatinerCharge() * orderProduct.getQuantity();
            }
        }
        return containerCharge;
    }

    public void setContainerCharge(double containerCharge) {
        this.containerCharge = containerCharge;
    }

    public double getAbsContainerCharge() {
        return containerCharge;
    }

    public double getVat(VendorOffline vendor) {
        tax = 0;
        if (products != null) {
            for (OrderProductOffline orderProduct : getProducts()) {
                tax += (orderProduct.getTotalPrice() * (vendor.getTax() / 100));
            }
        }
        tax = Math.round(tax);
        return tax;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getServiceTax() {
        return serviceTax;
    }

    public void setServiceTax(double serviceTax) {
        this.serviceTax = serviceTax;
    }

    public double getServiceTax(VendorOffline vendor) {
        serviceTax = 0;
        if (products != null) {
            for (OrderProductOffline orderProduct : getProducts()) {
                serviceTax += (orderProduct.getTotalPrice() * (vendor.getServiceTax() / 100));
            }
        }
        serviceTax = Math.round(serviceTax);
        return serviceTax;
    }

    public double getCgst(VendorOffline vendor) {
        cgst = 0;
        if (products != null) {
            for (OrderProductOffline orderProduct : getProducts()) {
                 cgst += (orderProduct.getTotalPrice() * (vendor.getCgst() / 100));
            }
        }
        cgst = Math.round(cgst * 100) / 100.00;
        return cgst;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getSgst(VendorOffline vendor) {
        sgst = 0;
        if (products != null) {
            for (OrderProductOffline orderProduct : getProducts()) {
                sgst += (orderProduct.getTotalPrice() * (vendor.getSgst() / 100));
            }
        }
        sgst = Math.round(sgst * 100) / 100.00;
        return sgst;
    }

    public String getOrderHash() {
        if (orderHash == null)
            orderHash = "";
        return orderHash;
    }

    public void setOrderHash(String orderHash) {
        this.orderHash = orderHash;
    }

    public String getCreatedAtString() {
        String DATE_FORMAT = "dd MMM h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date(getCreatedAt() * 1000l));
    }

    public void setOrderTimeMiliis(long timeMiliis) {
        this.orderTime = (timeMiliis / 1000l);
    }

    public String getProductItemList() {
        StringBuffer productDetails = new StringBuffer("");

        for (int i = 0; i < getProducts().size(); i++) {
            productDetails.append(getProducts().get(i).getName());
            productDetails.append('(');
            productDetails.append(getProducts().get(i).getQuantity());
            productDetails.append(')');

            if (i != getProducts().size() - 1) {
                productDetails.append(',');
            }
        }
        return productDetails.toString();
    }

    public double calculateOrderProductPrice() {
        price = getCalculateOrderProductPrice();
        return price;
    }

    public double getCalculateOrderProductPrice() {
        double totalPrice = 0;
        for (OrderProductOffline orderProduct : getProducts()) {
//            if (orderProduct.isFree()) {
//                totalPrice += 0;
//            } else {
            if (isOrderFromServer()) {
                totalPrice += orderProduct.getTotalServerPrice();
            } else {
                totalPrice += orderProduct.getTotalPrice();
            }
//            }
        }
        return totalPrice;
    }

    public String getCafe() {
        return cafe;
    }

    public void setCafe(String cafe) {
        this.cafe = cafe;
    }

    public double calculateGuestOrderPrice(String guestType) {
        double totalPrice = 0;
        int freeQuantity = 0;
        int guestItemCount = 0;

        for (OrderProductOffline product : getProducts()) {
            if (product.getProduct().isFree()) {
                freeQuantity = product.getProduct().getFreeQuantity();
                break;
            }
        }
        if (freeQuantity > 0) {
            for (OrderProductOffline product : getProducts()) {
                if (product.getProduct().isFree())
                    guestItemCount += product.quantity;
            }
            guestItemCount -= freeQuantity;
        }
        for (OrderProductOffline orderProduct : getProducts()) {

            if (orderProduct.getProduct().isFree()) {

                if (guestType.equals(ApplicationConstants.GUEST_TYPE_PERSONAL) && guestItemCount > 0) {
                    totalPrice += orderProduct.getTotalPrice();
                    guestItemCount--;
                } else {
                    totalPrice += 0;
                }
            } else {
                totalPrice += orderProduct.getTotalPrice();
            }
        }
        price = totalPrice;
        return totalPrice;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OrderUsersReponseOffline getGroupUser() {
        return groupUser;
    }

    public TreeSet<Long> getCouchLocations() {
        if (vendor == null || vendor.getVendor() == null) {
            vendor = new OrderVendorReposneOffline();
            vendor.vendor = new OrderVendorOffline();
        }
        return vendor.getVendor().getCouchLocations();
    }

    public void setCouchLocations(TreeSet<Long> couchLocations) {
        if (vendor == null || vendor.getVendor() == null) {
            vendor = new OrderVendorReposneOffline();
            vendor.vendor = new OrderVendorOffline();
        }
        this.vendor.getVendor().setCouchLocations(couchLocations);
    }

    public String getGroupUserNames() {
        String groupUsers = "";
        if (groupUser != null) {
            int i = 0;
            for (i = 0; i < groupUser.getUsers().size(); i++) {
                groupUsers += groupUser.getUsers().get(i).getName();
                if (i + 1 < groupUser.getUsers().size()) {
                    groupUsers += ",";
                }
            }
        }

        return groupUsers;
    }

    public long getPrimaryUserId() {
        return primaryUserId;
    }

    public void setPrimaryUserId(long primaryUserId) {
        this.primaryUserId = primaryUserId;
    }

    public void getOrderDetail(boolean withProducts, boolean withVendor, boolean withUser, boolean withRating
            , Context context,
                               final ResponseListener<OrderResponseOffline> responseListener,
                               final ContextErrorListener contextErrorListener) {
        String url = UrlConstant.ORDER_DETAIL + getId() +
                String.format("/%d/%d/%d/%d", withProducts ? 1 : 0, withVendor ? 1 : 0, withUser ? 1 : 0, withRating ? 1 : 0);


        SimpleHttpAgent<OrderResponseOffline> orderResponseSimpleHttpAgent = new SimpleHttpAgent<OrderResponseOffline>(
                context,
                url,
                new ResponseListener<OrderResponseOffline>() {
                    @Override
                    public void response(OrderResponseOffline responseObject) {
                        responseListener.response(responseObject);
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                        contextErrorListener.handleError(errorCode, error, errorResponse);
                    }
                },
                OrderResponseOffline.class
        );

        orderResponseSimpleHttpAgent.get();
    }

    public boolean hasCrossedEstimated30Mins() {
        if (estimatedDeliveryTime == 0)
            return false;
        else {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long estTime = (estimatedDeliveryTime * 1000l) + ApplicationConstants.THIRTY_MIN_MILLIS;
            if (currentTime >= estTime) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean isOfflineTimeCrossed() {
        return Calendar.getInstance().getTimeInMillis() - (getCreatedAt() * 1000l) > ApplicationConstants.ONE_MIN_MILLIS;
    }

    public OrderPaymentResponseOffline getOrderPaymentResponse() {
        return orderPaymentResponse;
    }

    public void setOrderPaymentResponse(OrderPaymentResponseOffline orderPaymentResponse) {
        this.orderPaymentResponse = orderPaymentResponse;
    }

    public ArrayList<Long> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Long> users) {
        this.users = users;
    }

    public String getGuestType() {
        return guestType;
    }

    public void setGuestType(String guestType) {
        this.guestType = guestType;
    }

    public ArrayList<OrderGuestInfoOffline> getOrderGuestInfo() {
        return orderGuestInfo;
    }

    public void setOrderGuestInfo(ArrayList<OrderGuestInfoOffline> orderGuestInfo) {
        this.orderGuestInfo = orderGuestInfo;
    }

    public boolean isGstEnabled() {
        return gstEnabled;
    }

    public void setGstEnabled(boolean gstEnabled) {
        this.gstEnabled = gstEnabled;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getKot() {
        return kot;
    }

    public void setKot(int kot) {
        this.kot = kot;
    }

    public int getOrderQueue() {
        return orderQueue;
    }

    public void setOrderQueue(int orderQueue) {
        this.orderQueue = orderQueue;
    }

    public String getOrderPickType() {
        if (orderPickType == null)
            orderPickType = "";
        return orderPickType;
    }

    public void setOrderPickType(String orderPickType) {
        this.orderPickType = orderPickType;
    }

    public boolean isInternalWalletUsed() {
        return internalWalletUsed;
    }

    public void setInternalWalletUsed(boolean internalWalletUsed) {
        this.internalWalletUsed = internalWalletUsed;
    }

    public void setPaymentMethods(PaymentMethodOffline externalPaymentMethod) {
        this.paymentMethod = externalPaymentMethod;
    }

    public PaymentMethodOffline getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodOffline paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isOrderFromServer() {
        return isOrderFromServer;
    }

    public void setOrderFromServer(boolean orderFromServer) {
        this.isOrderFromServer = orderFromServer;
    }
}
