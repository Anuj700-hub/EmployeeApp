package com.hungerbox.customer.cardsaleactivityintegration.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.cardsaleactivityintegration.ApplicationData;
import com.hungerbox.customer.cardsaleactivityintegration.CardSaleTransactionView;
import com.hungerbox.customer.cardsaleactivityintegration.Constants;
import com.hungerbox.customer.cardsaleactivityintegration.CreditSaleEnumClass;
import com.hungerbox.customer.cardsaleactivityintegration.CustomProgressAnimView;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener.CARDSCHEMERRESULTS;

public class CreditSaleDetailsFragment extends Fragment
{

	ApplicationData applicationData = null;
	CardSaleTransactionView mCardSaleDetailsView = null;
	Context mContext;

	TextView mCardHolderName = null;
	TextView mCardNo = null;
	TextView mExpiryDate = null;
	TextView mAmtMsg = null;

	private CustomProgressAnimView mProgressAnim = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		mContext = activity;
		applicationData = ApplicationData.getApplicationDataSharedInstance();
		mCardSaleDetailsView = ((CardSaleTransactionView) mContext);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		if (applicationData.IS_DEBUGGING_ON)
			Log.v(mContext.getPackageName(), "We are in card holder init views. ");

		View view = inflater.inflate(R.layout.creditsale_cardholderdetailsview, container, false);
		initViews(view);

		return view;
	}


	private void initViews(View view) {
		// TODO Auto-generated method stub

		mProgressAnim = (CustomProgressAnimView) view.findViewById(R.id.creditsale_details_LNL_progress);
		mAmtMsg = (TextView) view.findViewById(R.id.creditsale_details_LBL_swipe_amtmsg);

		// The screen is for the credit details
		mCardHolderName = (TextView) view.findViewById(R.id.creditsale_details_LBL_CardHolderName);
		mCardNo = (TextView) view.findViewById(R.id.creditsale_details_LBL_CardNo);
		mExpiryDate = (TextView) view.findViewById(R.id.creditsale_details_LBL_ExpiryDate);

		CreditSaleEnumClass.CARDTYPE cardtype = Constants.checkCardType(mCardSaleDetailsView.mCardData.getCreditCardNo(), mCardSaleDetailsView.mCardData.getFirst4Digits());

		/**
		 * by using the creditcard num we are setting image in the details fragment
		 */

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "cardtype " + cardtype);

		if (cardtype == CreditSaleEnumClass.CARDTYPE.EXPRESS)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.amex);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.VISA)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.visa);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.MASTER)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.mastercard);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.DINERS)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.diners_club);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.DISCOVER)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.discover);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.JCB)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.jcb);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.MAESTRO)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.maestro);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.RUPAY)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.rupay);
		else if (cardtype == CreditSaleEnumClass.CARDTYPE.UNKNOWN)
			((ImageView) view.findViewById(R.id.creditsale_details_IMG_card)).setImageResource(R.drawable.unknown_card);

		if(mCardSaleDetailsView.mTransactionData.mTotAmount.toString().length()>= 7)
			mAmtMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData.font_size_amount_small);
		else
			mAmtMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData.font_size_amount_normal);

		mAmtMsg.setText(mCardSaleDetailsView.mTransactionData.mTotAmount);

		if(mCardSaleDetailsView.mCardData.getCardSchemeResults() == CARDSCHEMERRESULTS.MAG_CARD)
		{
			String expDate = mCardSaleDetailsView.mCardData.getExpiryDate();
			mCardHolderName.setText((mCardSaleDetailsView.mCardData.getCardHolderName()).toLowerCase());
			mExpiryDate.setText("xx/xx");
			mCardNo.setText("**** " + mCardSaleDetailsView.mCardData.getLast4Digits());

		}
		else{

			mCardHolderName.setText((mCardSaleDetailsView.mCardData.getCardHolderName()).toLowerCase());
			mExpiryDate.setText("xx/xx");
			mCardNo.setText("**** " + mCardSaleDetailsView.mCardData.getLast4Digits());

		}

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				processCardSale();
			}
		}, 100);
	}

	private void processCardSale() {

		mProgressAnim.startAnimation();
		mCardSaleDetailsView.processCardSaleOnline();

    }
}
