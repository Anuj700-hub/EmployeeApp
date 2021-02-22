package com.hungerbox.customer.cardsaleactivityintegration.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.cardsaleactivityintegration.AppSharedPrefrences;
import com.hungerbox.customer.cardsaleactivityintegration.ApplicationData;
import com.hungerbox.customer.cardsaleactivityintegration.Constants;
import com.hungerbox.customer.cardsaleactivityintegration.CreditSaleActivity;
import com.hungerbox.customer.cardsaleactivityintegration.CreditSaleMessageEnum.WisePosStateInfoType;
import com.hungerbox.customer.cardsaleactivityintegration.MSWisepadView;
import com.hungerbox.customer.cardsaleactivityintegration.WisepadTransactionStateListener;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;
import com.mswipetech.wisepad.sdk.device.WisePadConnection;
import com.mswipetech.wisepad.sdk.device.WisePadTransactionState;

import java.util.Hashtable;

public class CreditSaleSwiperFragment extends Fragment
		implements WisepadTransactionStateListener
{

	TextView lblAmtMsg = null;
	TextView txtProgMsg = null;
	Button mBtnSwipe = null;

	Context mContext;
	CreditSaleActivity mCreditSaleActivity = null;
	ApplicationData applicationData = null;
	boolean isPinBypassed = false;

	LinearLayout linearLayout_pinIndication = null;
	LinearLayout linearLayout_swipeorinsert = null;
	TextView textView_enterPin=null;
	TextView textView_progressMsg=null;

	ImageView mImgPinPad = null;

	ImageView mpinEntryImageView1;
	ImageView mpinEntryImageView2;
	ImageView mpinEntryImageView3;
	ImageView mpinEntryImageView4;

	@Override
	public void onAttach(Activity activity) {

		mContext = activity;
		applicationData = ApplicationData.getApplicationDataSharedInstance();
		mCreditSaleActivity = ((CreditSaleActivity)mContext);
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view =  inflater.inflate(R.layout.creditsale_swiperview, container, false);
		initViews(view);
		return view;
	}


	private void initViews(View view)
	{

		mCreditSaleActivity.mLNRTopbarCancel.setVisibility(View.VISIBLE);

		linearLayout_pinIndication = (LinearLayout)view.findViewById(R.id.creditsale_swiperview_layout_pin);
		linearLayout_swipeorinsert = (LinearLayout)view.findViewById(R.id.creditsale_swiperview_layout_swipeorinsert);

		textView_enterPin=(TextView)view.findViewById(R.id.creditsale_swiperview_TXT_enterpin);

		textView_progressMsg=(TextView)view.findViewById(R.id.creditsale_swiperview_EDT_swipe_progmsg);

		mImgPinPad = (ImageView)view.findViewById(R.id.creditsale_swiperview_IMG_enterpin);

		mpinEntryImageView1 = (ImageView)view.findViewById(R.id.creditsale_swiperview_IMG_amex_pin1);
		mpinEntryImageView2 = (ImageView)view.findViewById(R.id.creditsale_swiperview_IMG_amex_pin2);
		mpinEntryImageView3 = (ImageView)view.findViewById(R.id.creditsale_swiperview_IMG_amex_pin3);
		mpinEntryImageView4 = (ImageView)view.findViewById(R.id.creditsale_swiperview_IMG_amex_pin4);

		lblAmtMsg = (TextView) view.findViewById(R.id.creditsale_totalamountview_LBL_totalamount);
		txtProgMsg = (TextView) view.findViewById(R.id.creditsale_swiperview_EDT_swipe_progmsg);

		mBtnSwipe = (Button) view.findViewById(R.id.creditsale_swiperview_BTN_swipe);

		if(mCreditSaleActivity.mTransactionData.mTotAmount.toString().length()>= 7)
			lblAmtMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData.font_size_amount_small);
		else
			lblAmtMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData.font_size_amount_normal);

		lblAmtMsg.setText(mCreditSaleActivity.mTransactionData.mTotAmount);

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, " mTotAmount " + mCreditSaleActivity.mTransactionData.mTotAmount);
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, " WisePadConnection: " + mCreditSaleActivity.getWisePadConnectionState());
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, " WisePadTranscation: " + mCreditSaleActivity.getWisePadTranscationState());

		if(mCreditSaleActivity.getWisePadConnectionState() ==  WisePadConnection.WisePadConnection_CONNECTED
				&& mCreditSaleActivity.getWisePadTranscationState() ==  WisePadTransactionState.WisePadTransactionState_Ready)
		{
			mCreditSaleActivity.requestCheckCard();

		}


		if(mCreditSaleActivity.getWisePadConnectionState() != WisePadConnection.WisePadConnection_CONNECTED)
		{
			onReturnWisepadConnectionState(mCreditSaleActivity.getWisePadConnectionState());
		}
		else if (mCreditSaleActivity.getWisePadTranscationState() == WisePadTransactionState.WisePadTransactionState_ICC_Online_Process
				|| mCreditSaleActivity.getWisePadTranscationState() == WisePadTransactionState.WisePadTransactionState_MAG_Online_Process) {


			String dlgMsg = "";
			if(mCreditSaleActivity.mCardData.getCardSchemeResults() == MSWisepadDeviceControllerResponseListener.CARDSCHEMERRESULTS.ICC_CARD){

				dlgMsg = getString(R.string.cardsaleactivity_cardinsertinsert_msg_CreditSaleSwiperFragment);
			}
			else {

				dlgMsg = getString(R.string.cardsaleactivity_cardswiperead_msg_CreditSaleSwiperFragment);
			}

			final Dialog dialog = Constants.showDialog(mCreditSaleActivity, mCreditSaleActivity.mCardSaleDlgTitle, dlgMsg, Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_INFO);
			Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);

			yes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					mCreditSaleActivity.showCardDetailsScreen();
				}
			});

			dialog.show();


		}
		else {

			mBtnSwipe.setEnabled(false);
			mBtnSwipe.setBackgroundResource(R.drawable.button_next_inactive);

			onReturnWisepadTransactionState(mCreditSaleActivity.getWisePadTranscationState(), mCreditSaleActivity.mWisePosStateInfo, mCreditSaleActivity.mWisePosStateInfoType);
		}

		mBtnSwipe.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				setWisepadStatusMsg(getString(R.string.connecting));
				mCreditSaleActivity.processDeviceConnetionWithAutoInitiation(MSWisepadView.AutoInitiationDevice.CHECKCARD);

			}
		});

		if(mCreditSaleActivity.mIsConvenienceFeesEnabled){

			((LinearLayout)view.findViewById(R.id.payment_LNR_convenienceamount)).setVisibility(View.VISIBLE);
			((LinearLayout)view.findViewById(R.id.payment_LNR_servicetaxamount)).setVisibility(View.VISIBLE);

			double conveniencePercentage = Double.parseDouble(AppSharedPrefrences.getAppSharedPrefrencesInstace().getConveniencePercentage() + "");
			double servicePercentage = Double.parseDouble(AppSharedPrefrences.getAppSharedPrefrencesInstace().getServicePercentageOnConvenience() + "");

			String strconveniencePercentage = (String.format("%.2f", conveniencePercentage));
			String strservicePercentage = (String.format("%.2f", servicePercentage));

			((TextView)view.findViewById(R.id.payment_LBL_convenienceamount)).setText("conv fee ("+strconveniencePercentage+"%)");
			((TextView)view.findViewById(R.id.payment_LBL_servicetaxamount)).setText("gst ("+strservicePercentage+"%)");

			((TextView)view.findViewById(R.id.payment_TXT_convenienceamount)).setText(mCreditSaleActivity.mTransactionData.mConvenienceAmount);
			((TextView)view.findViewById(R.id.payment_TXT_servicetaxamount)).setText(mCreditSaleActivity.mTransactionData.mServiceTaxamount);
		}
	}


	/**
	 * setSwiperScreenForWisepadConnetion
	 * set the current status of the wisepad conneciton.
	 *
	 * @param aWisePadConnection
	 * state of the connection
	 *
	 */

	@Override
	public void onReturnWisepadConnectionState(WisePadConnection aWisePadConnection)
	{
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, " aWisePadConnection " + aWisePadConnection);

		if(aWisePadConnection == WisePadConnection.WisePadConnection_CONNECTED)	{

			enableStatusInfo();
			setWisepadStatusMsg(getString(R.string.device_connected));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_CONNECTING)	{

			setWisepadStatusMsg(getString(R.string.connecting_device));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_DEVICE_DETECTED){

			setWisepadStatusMsg(getString(R.string.device_detected));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_DEVICE_NOTFOUND){

			setWisepadStatusMsg(getString(R.string.device_not_found));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_DIS_CONNECTED){

			setWisepadStatusMsg(getString(R.string.device_disconnected));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_FAIL_TO_START_BT){

			setWisepadStatusMsg(getString(R.string.fail_to_start_bluetooth_v2));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_BLUETOOTH_DISABLED){

			setWisepadStatusMsg(getString(R.string.creditsaleswiperfragment_enable_bluetooth));
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_MULTIPLE_PAIRED_DEVCIES_FOUND)
		{
			mCreditSaleActivity.showMultiplePairedDeviceDialog();
		}
		else if(aWisePadConnection == WisePadConnection.WisePadConnection_NO_PAIRED_DEVICES_FOUND)
		{
			mCreditSaleActivity.showNoPairedDeviceDialog();

		}
	}



	/**
	 * setSwiperScreenForWisepadState
	 * set the current status of the transaction with the wisepad.
	 *
	 * @param aWisePadTransactionState
	 * state of the tranaction
	 *
	 * @param aMessage
	 * any message set by the wisepad
	 *
	 * @param aWisePosStateInfoType
	 * thye of the message error or information
	 *
	 */
	@Override
	public void onReturnWisepadTransactionState(WisePadTransactionState aWisePadTransactionState, String aMessage, WisePosStateInfoType aWisePosStateInfoType) {

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(mCreditSaleActivity.getPackageName(), " " +
					"aWisePadTransactionState " + aWisePadTransactionState +" " +
					"aMessage " + aMessage + " " +
					"aWisePosStateInfoType " + aWisePosStateInfoType);

		if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_WaitingForCard) {

			if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Info) {

				isPinBypassed = false;
				setWisepadStatusMsg(aMessage);

				txtProgMsg.setVisibility(View.GONE);
				linearLayout_swipeorinsert.setVisibility(View.VISIBLE);
				mBtnSwipe.setEnabled(false);
				mBtnSwipe.setBackgroundResource(R.drawable.button_next_inactive);

			}
			else if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Error) {

				isPinBypassed = false;

				enableStatusInfo();
				setWisepadStatusMsg(aMessage);

			}

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_Pin_Entry_Request) {

			isPinBypassed = false;

			linearLayout_swipeorinsert.setVisibility(View.GONE);
			txtProgMsg.setVisibility(View.GONE);
			linearLayout_pinIndication.setVisibility(View.VISIBLE);

			if(aMessage.equalsIgnoreCase(mCreditSaleActivity.getString(R.string.bypass)))
				showPinByPassed(aMessage);
			else
				showPinEntry();

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_Pin_Entry_Asterisk) {

			int astricIndex = 0;
			try {
				astricIndex = Integer.parseInt(aMessage);
			}catch (Exception e){

			}

			if(astricIndex >= 1 && astricIndex <=4)
				showPinAsterisk(astricIndex);

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_Pin_Entry_Results) {

			isPinBypassed = false;

			if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Info) {

				linearLayout_swipeorinsert.setVisibility(View.GONE);
				txtProgMsg.setVisibility(View.GONE);
				linearLayout_pinIndication.setVisibility(View.VISIBLE);

				if(aMessage.equalsIgnoreCase(mCreditSaleActivity.getString(R.string.bypass)))
					showPinByPassed(aMessage);
				else
					showPinEntryStatus(true, aMessage);

			}
			else if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Error) {

				enableStatusInfo();
				setWisepadStatusMsg(aMessage);
			}

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_ICC_SetAmount) {

			txtProgMsg.setVisibility(View.VISIBLE);
			linearLayout_swipeorinsert.setVisibility(View.GONE);
			linearLayout_pinIndication.setVisibility(View.GONE);

			setWisepadStatusMsg(aMessage);

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_CheckCard) {

			isPinBypassed = false;

			txtProgMsg.setVisibility(View.VISIBLE);
			linearLayout_swipeorinsert.setVisibility(View.GONE);
			linearLayout_pinIndication.setVisibility(View.GONE);

			setWisepadStatusMsg(aMessage);

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_CancelCheckCard) {

			enableStatusInfo();
			setWisepadStatusMsg(aMessage);

		}
		else if (aWisePadTransactionState == WisePadTransactionState.WisePadTransactionState_Ready) {

			enableStatusInfo();

			if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Info)
			{
				setWisepadStatusMsg(aMessage);
			}
			else if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Error){

				if(aMessage.equalsIgnoreCase(mCreditSaleActivity.getString(R.string.device_busy)))
					setWisepadStatusMsg(getString(R.string.creditsaleswiperfragment_status)+": "+ aMessage);
				else
					setWisepadStatusMsg(aMessage);

			}
		}
		else {

			if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Info) {

				txtProgMsg.setVisibility(View.VISIBLE);
				linearLayout_swipeorinsert.setVisibility(View.GONE);
				linearLayout_pinIndication.setVisibility(View.GONE);

				setWisepadStatusMsg(aMessage);
			}
			else if(aWisePosStateInfoType == WisePosStateInfoType.MessageType_Error){

				enableStatusInfo();
				setWisepadStatusMsg(aMessage);
			}
		}
	}

	@Override
	public void onReturnWisepadDeviceInfo(Hashtable<String, String> deviceInfoData) {

	}


	private void setWisepadStatusMsg(String msg)
	{

		if(isPinBypassed && !AppSharedPrefrences.getAppSharedPrefrencesInstace().isPinBypassEnabled()) {

			enableStatusInfo();
			msg = mCreditSaleActivity.getString(R.string.creditsale_swiperview_pin_bypass_disabled);

		}

		txtProgMsg.setText(msg);

	}

	private void disableStatusInfo(){

		mBtnSwipe.setEnabled(false);
		mBtnSwipe.setBackgroundResource(R.drawable.button_next_inactive);
		txtProgMsg.setVisibility(View.VISIBLE);
		linearLayout_swipeorinsert.setVisibility(View.GONE);
		linearLayout_pinIndication.setVisibility(View.GONE);
	}

	private void enableStatusInfo(){

		mBtnSwipe.setEnabled(true);
		mBtnSwipe.setBackgroundResource(R.drawable.button_next_active);
		txtProgMsg.setVisibility(View.VISIBLE);
		linearLayout_swipeorinsert.setVisibility(View.GONE);
		linearLayout_pinIndication.setVisibility(View.GONE);
	}

	private void showPinAsterisk(int key){

		if(key == 1) {
			mpinEntryImageView1.setImageResource(R.drawable.roundcircleblue);
		}
		else if(key == 2) {
			mpinEntryImageView2.setImageResource(R.drawable.roundcircleblue);
		}
		else if(key == 3) {
			mpinEntryImageView3.setImageResource(R.drawable.roundcircleblue);
		}
		else if(key == 4) {
			mpinEntryImageView4.setImageResource(R.drawable.roundcircleblue);
		}
	}

	private void showPinEntryStatus(boolean status, String msg)
	{
		if(status)
		{
			mImgPinPad.setPressed(true);
			textView_enterPin.setText(msg);

			mpinEntryImageView1.setImageResource(R.drawable.imagecircle_active);
			mpinEntryImageView2.setImageResource(R.drawable.imagecircle_active);
			mpinEntryImageView3.setImageResource(R.drawable.imagecircle_active);
			mpinEntryImageView4.setImageResource(R.drawable.imagecircle_active);

		}
		else{

			linearLayout_pinIndication.setVisibility(View.GONE);
			textView_progressMsg.setVisibility(View.VISIBLE);
		}
	}

	private void showPinByPassed(String msg)
	{

		mImgPinPad.setPressed(true);
		textView_enterPin.setText(msg);

		mpinEntryImageView1.setImageResource(R.drawable.imagecircle_active_red);
		mpinEntryImageView2.setImageResource(R.drawable.imagecircle_active_red);
		mpinEntryImageView3.setImageResource(R.drawable.imagecircle_active_red);
		mpinEntryImageView4.setImageResource(R.drawable.imagecircle_active_red);

		isPinBypassed = true;

	}

	private void showPinEntry()
	{
		mImgPinPad.setPressed(false);
		textView_enterPin.setText(getString(R.string.creditsale_swiperview_lable_enter_pin));

		mpinEntryImageView1.setImageResource(R.drawable.imagecircle_inactive);
		mpinEntryImageView2.setImageResource(R.drawable.imagecircle_inactive);
		mpinEntryImageView3.setImageResource(R.drawable.imagecircle_inactive);
		mpinEntryImageView4.setImageResource(R.drawable.imagecircle_inactive);

	}
}
