package com.hungerbox.customer.order.activity;

/**
 * Created by manas on 27/11/18.
 */

import android.widget.Toast;

import com.hungerbox.customer.cardsaleactivityintegration.AppSharedPrefrences;
import com.hungerbox.customer.cardsaleactivityintegration.Constants;
import com.mswipetech.wisepad.sdk.data.LoginResponseData;
import com.mswipetech.wisepad.sdk.data.MSDataStore;
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener;

/**
 * MSWisepadControllerResponseListenerObserver
 * The mswipe overridden class  observer which listens to the responses from the mswipe sdk function
 */

public class MSWisepadControllerResponseListenerObserver implements MSWisepadControllerResponseListener
{
    PaymentFragment paymentActivity;


    public MSWisepadControllerResponseListenerObserver(PaymentFragment paymentActivity){
        this.paymentActivity = paymentActivity;
    }

    /**
     * onReponseData
     * The response data notified back to the call back function
     * @param
     * aMSDataStore
     * 		the generic mswipe data store, this instance refers to LoginResponseData, so this
     * need be type cast back to LoginResponseData to access the login response data
     * @return
     */
    public void onReponseData(MSDataStore aMSDataStore)
    {

        LoginResponseData loginResponseData = (LoginResponseData) aMSDataStore;

        boolean responseStatus = loginResponseData.getResponseStatus();

        if (!responseStatus)
        {

            Toast.makeText(paymentActivity.getContext(),loginResponseData.getResponseFailureReason(),Toast.LENGTH_SHORT).show();

        }
        else if (responseStatus){

            String FirstName =  loginResponseData.getFirstName();

            //below details be used to call up the rest of the api, so by saving them to the device stores like,
            //shared preferences or sqllite, if this details are saved the login to the mswipe gateway is not
            //required again,


            //this Session_Tokeniser does not change until the password for the user id is not changed or if the same user has
            //logged in again.
            String Reference_Id =  loginResponseData.getReferenceId();
            String Session_Tokeniser =  loginResponseData.getSessionTokeniser();

            //save the referenceId and Session_Tokeniser for accessing the other api's, this are a kind of outh
            //authenticated permission for accessing the other services
            AppSharedPrefrences.getAppSharedPrefrencesInstace().setReferenceId(Reference_Id);
            AppSharedPrefrences.getAppSharedPrefrencesInstace().setSessionToken(Session_Tokeniser);

            String Currency_Code =  loginResponseData.getCurrency();
            boolean tipRequired =  loginResponseData.isTipsRequired();
            float convienencePercentage = loginResponseData.getConveniencePercentage();
            float serviceTax =  loginResponseData.getServiceTax();

            AppSharedPrefrences.getAppSharedPrefrencesInstace().setCurrencyCode(Currency_Code+".");
            AppSharedPrefrences.getAppSharedPrefrencesInstace().setTipEnabled(tipRequired);

            AppSharedPrefrences.getAppSharedPrefrencesInstace().setConveniencePercentage(convienencePercentage);
            AppSharedPrefrences.getAppSharedPrefrencesInstace().setServicePercentageOnConvenience(serviceTax);

            AppSharedPrefrences.getAppSharedPrefrencesInstace().setPinBypass(loginResponseData.isPinBypass());
            AppSharedPrefrences.getAppSharedPrefrencesInstace().setReceiptEnabled(loginResponseData.isReceiptRequired());

            boolean IS_Password_Changed = loginResponseData.isFirstTimePasswordChanged();

            //if this is false, then its suggest that the password has not changed since the user has
            //been created at-least once
            if (!IS_Password_Changed)
            {
//                    startActivity(new Intent(OrderReviewActivity.this, ChangePasswordView.class));
//                    finish();
                return;
            }
            else{

                if(paymentActivity.startTransaction)
                    ((PaymentActivity)(paymentActivity.getActivity())).startMswipeTransaction(paymentActivity);
                else{
                    paymentActivity.showMswipe =true;
                    paymentActivity.getUserPaymentDetails();
                }

//                    final Dialog dlg = Constants.showDialog(OrderReviewActivity.this, "LoginView", FirstName + " User authenticated.",
//                            Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_INFO);
//                    Button btnOk = (Button) dlg.findViewById(R.id.customdlg_BTN_yes);
//                    btnOk.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                        TODO Auto-generated method stub
//                            dlg.dismiss();
//
//                            finish();
//
//                        }
//                    });
//                    dlg.show();

            }

        }
        else{
            paymentActivity.showMswipe = false;
            paymentActivity.getUserPaymentDetails();
            Constants.showDialog(paymentActivity.getContext(), "LoginView", "Invalid response from Mswipe server, please contact support.");
        }
    }


}
