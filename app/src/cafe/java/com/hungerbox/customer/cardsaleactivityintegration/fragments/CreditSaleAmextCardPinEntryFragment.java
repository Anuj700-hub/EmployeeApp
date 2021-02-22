package com.hungerbox.customer.cardsaleactivityintegration.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.cardsaleactivityintegration.ApplicationData;
import com.hungerbox.customer.cardsaleactivityintegration.CreditSaleActivity;

/**
 * Created by venkatesh on 7/9/2015.
 */

 public class CreditSaleAmextCardPinEntryFragment extends Fragment implements View.OnClickListener {

    TextView lblAmtMsg = null;
    ImageView mpinEntryImageView1=null;
    ImageView mpinEntryImageView2=null;
    ImageView mpinEntryImageView3=null;
    ImageView mpinEntryImageView4=null;
    ImageView[] pinIndication = new ImageView[4];
    String mStrKeyCode = "";

    Context mContext;
    CreditSaleActivity mCardSaleAmexPinEntryView = null;
    ApplicationData applicationData = null;



    @Override
    public void onAttach(Activity activity) {

        mContext = activity;
        applicationData = ApplicationData.getApplicationDataSharedInstance();
        mCardSaleAmexPinEntryView = ((CreditSaleActivity)mContext);
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.creditsale_amexcardpinentryview, container, false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {

        lblAmtMsg = (TextView) view.findViewById(R.id.creditsale_LBL_swipe_amtmsg);

        mpinEntryImageView1=(ImageView)view.findViewById(R.id.creditsale_IMG_amex_pin1);
        mpinEntryImageView2=(ImageView)view.findViewById(R.id.creditsale_IMG_amex_pin2);
        mpinEntryImageView3=(ImageView)view.findViewById(R.id.creditsale_IMG_amex_pin3);
        mpinEntryImageView4=(ImageView)view.findViewById(R.id.creditsale_IMG_amex_pin4);

        pinIndication[0] = mpinEntryImageView1;
        pinIndication[1] = mpinEntryImageView2;
        pinIndication[2] = mpinEntryImageView3;
        pinIndication[3] = mpinEntryImageView4;

        lblAmtMsg.setText(ApplicationData.mCurrency + " " + (mCardSaleAmexPinEntryView.mTransactionData.mTotAmount));



        ((Button) view.findViewById(R.id.button1)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button2)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button3)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button4)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button5)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button6)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button7)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button8)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.button9)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.key_BTN_0)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.key_BTN_00)).setOnClickListener(this);
        ((ImageButton) view.findViewById(R.id.key_BTN_Del)).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        
        if (v.getId() == R.id.key_BTN_Del)
        {
            int strLength = mStrKeyCode.length();
            if(mStrKeyCode.length() > 0){
                mStrKeyCode = mStrKeyCode.substring(0, strLength - 1);
                pinIndication[mStrKeyCode.length()].setImageResource(R.drawable.imagecircle_inactive);
            }

        }
        else{

            if(mStrKeyCode.length() < 4){
                pinIndication[mStrKeyCode.length()].setImageResource(R.drawable.imagecircle_active);
                mStrKeyCode = mStrKeyCode + v.getTag();
            }
            if(mStrKeyCode.length() == 4){

                mCardSaleAmexPinEntryView.mTransactionData.mAmexSecurityCode = mStrKeyCode;
                mCardSaleAmexPinEntryView.showCardDetailsScreen();

            }
        }
    }
}
