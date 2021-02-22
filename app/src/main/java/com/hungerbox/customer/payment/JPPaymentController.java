package com.hungerbox.customer.payment;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.webkit.WebView;

import com.hungerbox.customer.BuildConfig;
import com.hungerbox.customer.exception.PaymentNotSetExpection;
import com.hungerbox.customer.model.PaymentMethod;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import in.juspay.ec.sdk.api.Environment;
import in.juspay.ec.sdk.api.ExpressCheckoutService;
import in.juspay.ec.sdk.api.core.AbstractPayment;
import in.juspay.godel.analytics.GodelTracker;
import in.juspay.juspaysafe.BrowserCallback;
import in.juspay.juspaysafe.BrowserParams;
import in.juspay.juspaysafe.JuspaySafeBrowser;

/**
 * Created by sandipanmitra on 4/17/18.
 */

public class JPPaymentController {


    private static final String[] END_URLS = new String[]{
            UrlConstant.PAYMENT_RESPONSE_REGEX};
    AbstractPayment JPPayment;
    OnPaymentStatusChangeListener listener;
    PaymentMethod paymentMethodToUse;

    public JPPaymentController(PaymentMethod paymentMethod) {
        this.paymentMethodToUse = paymentMethod;

        String targetEnv = SharedPrefUtil.getString(ApplicationConstants.MANUAL_BUILD_TYPE,"");

        if (BuildConfig.BUILD_TYPE.contains("qa") && targetEnv.equals("qa"))
            Environment.configure(Environment.SANDBOX, UrlConstant.JUSPAY_MERCHANT_ID);
        else
            Environment.configure(Environment.PRODUCTION, UrlConstant.JUSPAY_MERCHANT_ID);
    }


    public void setJPPayment(AbstractPayment payment) {
        this.JPPayment = payment;
        this.JPPayment = this.JPPayment.setEndUrlRegexes(END_URLS);
        this.JPPayment = this.JPPayment.setMerchantId(UrlConstant.JUSPAY_MERCHANT_ID);
    }

    public void startPayment(final Activity parentActivity, @NonNull final OnPaymentStatusChangeListener listener) throws PaymentNotSetExpection {
        this.listener = listener;

        if (paymentMethodToUse == null || JPPayment == null)
            throw new PaymentNotSetExpection();


        JPPayment.startPayment(parentActivity,
                new BrowserParams(),
                new BrowserCallback() {
                    @Override
                    public void endUrlReached(WebView webView, JSONObject jsonObject) {
                        GodelTracker.getInstance().trackPaymentStatus(JPPayment.getOrderId(), GodelTracker.SUCCESS);
                        try {
                            String url = (String) jsonObject.get("url");
                            JuspaySafeBrowser.exit();
                            if (url.contains("success")) {
                                listener.onPaymentComplete(true);
                            } else {
                                listener.onPaymentComplete(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            JuspaySafeBrowser.exit();
                            listener.onPaymentComplete(false);
                        }


                    }

                    @Override
                    public void onTransactionAborted(JSONObject jsonObject) {
                        GodelTracker.getInstance().trackPaymentStatus(JPPayment.getOrderId(), GodelTracker.FAILURE);
                        JuspaySafeBrowser.exit();
                        listener.onPaymentAborted();
                    }

                },
                new ExpressCheckoutService.TxnInitListener() {
                    @Override
                    public void beforeInit() {
                        listener.onPaymentStatusInit();
                    }

                    @Override
                    public void onTxnInitResponse(ExpressCheckoutService.ExpressCheckoutResponse expressCheckoutResponse) {

                    }

                    @Override
                    public void initError(Exception e, @Nullable ExpressCheckoutService.ExpressCheckoutResponse expressCheckoutResponse) {
                        try {
                            AppUtils.HbLog("peeyush", expressCheckoutResponse.response.toString());
                        } catch (Exception ex) {

                        }
                        listener.onPaymentComplete(false);
                    }

                }
        );

    }


}
