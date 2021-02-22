package com.hungerbox.customer.cardsaleactivityintegration.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.cardsaleactivityintegration.ApplicationData;
import com.hungerbox.customer.cardsaleactivityintegration.CardSaleTransactionView;
import com.mswipetech.wisepad.sdk.data.ReceiptData;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;

/**
 * Created by mSwipe on 11/19/2015.
 */
public class EmiTermConditionFragment extends Fragment {

    CardSaleTransactionView mEmiTermConditionView = null;
    Context mContext;
    ApplicationData applicationData = null;


    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub

        mContext = activity;
        mEmiTermConditionView = ((CardSaleTransactionView) mContext);
        applicationData = ApplicationData.getApplicationDataSharedInstance();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.emi_termconditionview, container, false);

       // mEmiTermConditionView.setChangeTopbarImageIcon(false);
       // mEmiTermConditionView.mLINTopBarMenu.setVisibility(View.GONE);

        initView(view);
        return view;

    }


    private void initView(View view){

        boolean isEmvSwiper = false;
        if(mEmiTermConditionView.mCardSaleResponseData.getCardSchemeResults() == MSWisepadDeviceControllerResponseListener.CARDSCHEMERRESULTS.ICC_CARD)
         isEmvSwiper = true;

        String title = mEmiTermConditionView.mCardSaleDlgTitle;

        if(title == null)
            title = getResources().getString(R.string.emi_termconditionview_emi);

        ReceiptData mReceiptData = new ReceiptData();

        if(mEmiTermConditionView.mCardSaleResponseData.getReceiptData() != null){

            mReceiptData = mEmiTermConditionView.mCardSaleResponseData.getReceiptData();
        }

        String mAmt= mEmiTermConditionView.mCardSaleResponseData.getTrxAmount();
        String authCode = mEmiTermConditionView.mCardSaleResponseData.getAuthCode();
        String rrno = mEmiTermConditionView.mCardSaleResponseData.getRRNO();
        String date = mEmiTermConditionView.mCardSaleResponseData.getDate();
        String ExpiryDate = mEmiTermConditionView.mCardSaleResponseData.getExpiryDate();
        String EmvCardExpdate = mEmiTermConditionView.mCardSaleResponseData.getEmvCardExpdate();
        String AppIdentifier = mEmiTermConditionView.mCardSaleResponseData.getAppIdentifier();
        String ApplicationName = mEmiTermConditionView.mCardSaleResponseData.getApplicationName();
        String SwitchCardType = mReceiptData.cardType;

        if(ApplicationData.IS_DEBUGGING_ON)
            Log.v(ApplicationData.packName, " The text is " +mAmt+ " "+authCode + " "+rrno );

        String TVR = mEmiTermConditionView.mCardSaleResponseData.getTVR();
        if(TVR == null) TVR = "";

        String TSI = mEmiTermConditionView.mCardSaleResponseData.getTSI();
        if(TSI == null) TSI = "";

        if(EmvCardExpdate==null)
            EmvCardExpdate = "";

        String txtID= "TXN ID: "+mEmiTermConditionView.mCardSaleResponseData.getStandId();

        String merchantDetails= "";
        String cardIssuer= "";
        String sponsorBankName= "";
        String emiTxnID= "";
        String merchantOtherDetails= "";


        String emiTenure= "";
        String emiRate= "";
        String emiTotalPayableAmount= "";
        String emiPerMonthEmi= "";

        StringBuilder cardNumMask = new StringBuilder();

        if(mReceiptData != null){

            merchantDetails = mReceiptData.merchantName+"\n"+ Html.fromHtml(mReceiptData.merchantAdd);
            cardIssuer = "CARD ISSUER: "+mReceiptData.cardIssuer;
            sponsorBankName = mReceiptData.bankName;
            emiTxnID = "EMI TXN ID: "+mReceiptData.billNo;

            String[] dateTime = date.split(",");
            merchantOtherDetails = "Date: "+dateTime[0]+"\n"+"Time: "+dateTime[1].trim()+"\n"+"MID: "+mReceiptData.mId+"\n"+"TID: "+mReceiptData.tId+"\n"+"Batch No.: "+mReceiptData.batchNo+"\nInvoice No.: "+mReceiptData.refNo+"\n"+"Bill No.:"+mReceiptData.billNo;


            if (mReceiptData.firstDigitsOfCard.length() >= 6) {

                cardNumMask.append(mReceiptData.firstDigitsOfCard.substring(0,4)+" "+mReceiptData.firstDigitsOfCard.substring(4,6));
            }

            if (mReceiptData.cardNo.length() >= 8) {

                cardNumMask.append(mReceiptData.cardNo.substring(8,mReceiptData.cardNo.length()));
            }
        }

        emiTenure = "TENURE: "+mEmiTermConditionView.mEmiPeriod+" Months";
        emiRate = "INTEREST RATE(P.A): "+mEmiTermConditionView.mEmiRate+"%";
        emiTotalPayableAmount = "TOTAL PAYABLE AMT(Incl. Interest): "+ApplicationData.mCurrency+" "+mEmiTermConditionView.mEmiAmount;
        emiPerMonthEmi = "EMI AMT: "+ApplicationData.mCurrency+" "+String.format("%.2f", (Double.parseDouble(mEmiTermConditionView.mEmiAmount)/Double.parseDouble(mEmiTermConditionView.mEmiPeriod)));


        String mStrAuthCodeReceipt= "";
        String mStrDate= "";
        String mStrCardNum= "";
        String mStrExpDate= "";
        String mStrAmt= "";
        String mStrCardType= "";
        String mStrApplication= "";
        String mStrTVR= "";

        if(isEmvSwiper)
        {
            String tempString=EmvCardExpdate.trim();
            if(tempString.length()==5)
            {
                EmvCardExpdate=tempString.substring(3,5);
                EmvCardExpdate=EmvCardExpdate+"/" +tempString.substring(0,2);

            }else if(tempString.length()==4){
                EmvCardExpdate=tempString.substring(2,4);
                EmvCardExpdate=EmvCardExpdate+  "/" + tempString.substring(0,2);
            }else{
                EmvCardExpdate=tempString;
            }

            mStrAuthCodeReceipt = "APPR CD: " + authCode;
            mStrDate = "DATE/TIME: " + date;
            //mStrCardNum = "CARD NUM: " + mReceiptData.cardNo;
            mStrCardNum = "CARD NUM: " + cardNumMask.toString().replaceAll("\\s","");
            mStrExpDate = "EXP DT: " + EmvCardExpdate;
            mStrAmt = "BASE AMT: " + applicationData.mCurrency + " "  + mAmt ;
            mStrCardType =  SwitchCardType +"-Chip";
            mStrApplication = "APP ID: " + AppIdentifier + " APP NAME: " + ApplicationName;
            mStrTVR = "TVR: " + TVR + " TSI: " + TSI;

        }
        else{
            mStrAuthCodeReceipt = "APPR CD: " + authCode;
            mStrDate = "DATE: " + date ;
            mStrCardNum = "CARD NUM: " + cardNumMask.toString().replaceAll("\\s","");
            mStrExpDate = "EXP DT: " +( ExpiryDate.length() >= 3 ? ExpiryDate.substring(0, 2)+"/"+ExpiryDate.substring(2) : ExpiryDate);
            mStrAmt = "BASE AMT: " + applicationData.mCurrency + " "  + mAmt ;
            mStrCardType = SwitchCardType +"-Swipe";

        }


        ((TextView)view. findViewById(R.id.topbar_LBL_heading)).setText(title);
        ((TextView)view. findViewById(R.id.sponsorbankname)).setText(sponsorBankName);
        ((TextView)view. findViewById(R.id.mearchant_details)).setText(merchantDetails.trim());
        ((TextView) view.findViewById(R.id.mearchant_other_details)).setText(merchantOtherDetails);

        TextView txtTransactionDetails = (TextView) view.findViewById(R.id.transaction_details);
        String swipeChip = "";
        if(isEmvSwiper)
            swipeChip = "Chip";
        else
            swipeChip ="Swipe";
        txtTransactionDetails.setText(mStrCardNum+" "+swipeChip+"\n"+mStrExpDate+"\n"+"CARD TYPE: "+mStrCardType+"\n"+txtID+"\n"+mStrAuthCodeReceipt
                +"\nRRNO: "+rrno+"\n"+mStrTVR);

        TextView txtEmiDetials = (TextView)view. findViewById(R.id.emi_details);
        txtEmiDetials.setText(emiTxnID+"\n"+emiTenure+"\n"+cardIssuer+"\n"+mStrAmt+"\n"+emiRate+"\n"+emiPerMonthEmi+"\n"+emiTotalPayableAmount);
        ((TextView)view. findViewById(R.id.final_transction_details)).setText("BASE AMT. :"+ApplicationData.mCurrency+" "+mAmt+"\n"+emiTotalPayableAmount);
        final ImageButton btn_next = (ImageButton) view.findViewById(R.id.emi_BTN_next);
        btn_next.setEnabled(false);
        btn_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                mEmiTermConditionView.showSignature();
            }
        });

        CheckBox chkIagree = (CheckBox) view.findViewById(R.id.emi_CHK_i_agree);
        chkIagree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                btn_next.setEnabled(isChecked);
            }
        });

    }


}