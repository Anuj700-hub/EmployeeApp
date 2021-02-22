package com.hungerbox.customer.cardsaleactivityintegration;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.hungerbox.customer.R;
import com.mswipetech.sdk.network.MSGatewayConnectionListener;
import com.mswipetech.wisepad.sdk.MSWisepadController;
import com.mswipetech.wisepad.sdk.data.CardSaleResponseData;
import com.mswipetech.wisepad.sdk.data.MSDataStore;
import com.mswipetech.wisepad.sdk.data.ReceiptData;
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener;



public class CardSaleTransactionView extends CreditSaleActivity {


	/* the mswipe gateway server objects */

	protected WisePadGatewayConncetionListener mWisePadGatewayConncetionListener;

	/* the store the copy of the the date */

	public CardSaleResponseData mCardSaleResponseData = null;

	/**
	 * Called when the activity is first created.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mWisePadGatewayConncetionListener = new WisePadGatewayConncetionListener();

		MSWisepadController.getSharedMSWisepadController(CardSaleTransactionView.this,
				AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment(),
				AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource(),
				mWisePadGatewayConncetionListener);

		MSWisepadController.setNetworkSource(AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource());

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "");

	}

	/**
	 * processCardSaleOnline
	 * call up the API request which further processes the secured card data collected from the device by posting the data
	 * to online network and the responses are handled through the call back functions and are appropriately presented to the user
	 */

	public void processCardSaleOnline()
	{

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "ReferenceId " + mReferenceId);

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "SessionToken " + mSessionToken);

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "mGatewayEnvironment " + mGatewayEnvironment);

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "mNetworkSource " + mNetworkSource);

		if(mIsEmiSale)
		{

			MSWisepadController.getSharedMSWisepadController(CardSaleTransactionView.this,
					mGatewayEnvironment,
					mNetworkSource,
					mWisePadGatewayConncetionListener).processEMISaleOnline(
					mReferenceId,
					mSessionToken,
					removeChar(mTransactionData.mBaseAmount,','),
					removeChar(mTransactionData.mTipAmount,','),
					ApplicationData.smsCode + mTransactionData.mPhoneNo,
					mTransactionData.mReceipt,
					mTransactionData.mEmail,
					mTransactionData.mNotes,
					mIsTipEnable,
					mIsReceiptEnable,
					mTransactionData.mAmexSecurityCode,
					mEmiPeriod,
					mEmiBankCode,
					"",
					mTransactionData.mExtraNote1,
					mTransactionData.mExtraNote2,
					mTransactionData.mExtraNote3,
					mTransactionData.mExtraNote4,
					mTransactionData.mExtraNote5,
					mTransactionData.mExtraNote6,
					mTransactionData.mExtraNote7,
					mTransactionData.mExtraNote8,
					mTransactionData.mExtraNote9,
					mTransactionData.mExtraNote10,
					new MSWisepadControllerResponseListenerObserver());

		}
		else if(mIsSaleWithCash)
		{

			MSWisepadController.getSharedMSWisepadController(CardSaleTransactionView.this,
					mGatewayEnvironment,
					mNetworkSource,
					mWisePadGatewayConncetionListener).processSaleWithCashOnline(
					mReferenceId,
					mSessionToken,
					removeChar(mTransactionData.mBaseAmount,','),
					removeChar(mTransactionData.mTipAmount,','),
					ApplicationData.smsCode + mTransactionData.mPhoneNo,
					mTransactionData.mReceipt,
					mTransactionData.mEmail,
					mTransactionData.mNotes,
					mIsTipEnable,
					mIsReceiptEnable,
					mTransactionData.mAmexSecurityCode,
					removeChar(mTransactionData.mSaleCashAmount,','),
					"",
					mTransactionData.mExtraNote1,
					mTransactionData.mExtraNote2,
					mTransactionData.mExtraNote3,
					mTransactionData.mExtraNote4,
					mTransactionData.mExtraNote5,
					mTransactionData.mExtraNote6,
					mTransactionData.mExtraNote7,
					mTransactionData.mExtraNote8,
					mTransactionData.mExtraNote9,
					mTransactionData.mExtraNote10,
					new MSWisepadControllerResponseListenerObserver());

		}
		else if(mIsPreAuth)
		{

			MSWisepadController.getSharedMSWisepadController(CardSaleTransactionView.this,
					mGatewayEnvironment,
					mNetworkSource,
					mWisePadGatewayConncetionListener).processPreauthSaleOnline(
					mReferenceId,
					mSessionToken,
					removeChar(mTransactionData.mBaseAmount,','),
					removeChar(mTransactionData.mTipAmount,','),
					ApplicationData.smsCode + mTransactionData.mPhoneNo,
					mTransactionData.mReceipt,
					mTransactionData.mEmail,
					mTransactionData.mNotes,
					mIsTipEnable,
					mIsReceiptEnable,
					mTransactionData.mAmexSecurityCode,
					"",
					mTransactionData.mExtraNote1,
					mTransactionData.mExtraNote2,
					mTransactionData.mExtraNote3,
					mTransactionData.mExtraNote4,
					mTransactionData.mExtraNote5,
					mTransactionData.mExtraNote6,
					mTransactionData.mExtraNote7,
					mTransactionData.mExtraNote8,
					mTransactionData.mExtraNote9,
					mTransactionData.mExtraNote10,
					new MSWisepadControllerResponseListenerObserver());

		}
		else {

		}


		MSWisepadController.getSharedMSWisepadController(this,
				mGatewayEnvironment,
				mNetworkSource,
				mWisePadGatewayConncetionListener).processCardSaleOnline(
				mReferenceId,
				mSessionToken,
				removeChar(mTransactionData.mBaseAmount,','),
				removeChar(mTransactionData.mTipAmount,','),
				ApplicationData.smsCode + mTransactionData.mPhoneNo,
				mTransactionData.mReceipt,
				mTransactionData.mEmail,
				"",
				mIsTipEnable,
				mIsReceiptEnable,
				mTransactionData.mAmexSecurityCode,
				mConveniencePercentage,
				mServicePercentageOnConvenience,
				"",
				mTransactionData.mExtraNote1,
				mTransactionData.mExtraNote2,
				mTransactionData.mExtraNote3,
				"",
				"",
				"",
				"",
				"",
				"",
				"",
				new MSWisepadControllerResponseListenerObserver());
	}


	/**
	 * MSWisepadControllerResponseListenerObserver
	 * The mswipe overridden class  observer which listens to the response to the mswipe sdk function requests
	 */
	class MSWisepadControllerResponseListenerObserver implements MSWisepadControllerResponseListener {

		/**
		 * onReponseData
		 * The response data notified back to the call back function
		 *
		 * @param aMSDataStore the generic mswipe data store, this instance is refers to InvoiceTrxData or CardSaleResponseData, so this
		 *                     need be converted back to access the relavant data
		 * @return
		 */
		public void onReponseData(MSDataStore aMSDataStore)
		{

			if (aMSDataStore instanceof CardSaleResponseData) {

				mCardSaleResponseData = (CardSaleResponseData) aMSDataStore;

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "Status: " + mCardSaleResponseData.getResponseStatus() + " FailureReason: " + mCardSaleResponseData.getResponseFailureReason() );

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "ExceptoinStackTrace: " + mCardSaleResponseData.getExceptoinStackTrace());

				if(!mCardSaleResponseData.getResponseStatus())
				{

					showSignature();
				}
				else if(mIsEmiSale) {

					showScreen(CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_EMI_TERM_CONDITION);
				}
				else {

					playSound(100, R.raw.approved);
					showSignature();
				}

			}
		}
	}

	/**
	 * @param
	 * @return
	 * @description After successful transaction, we are showing signature screen.For that we sending details to signature screen.
	 */
	public void showSignature() {

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "not mIsEmiSale");

		/*

		if(mCardSaleResponseData.getResponseStatus())
		{

			//playSound(100, R.raw.approved);
			//intent = new Intent(CardSaleTransactionView.this, CreditSaleSignatureActivity.class);
		}
		else {
			//playSound(100, R.raw.declined);
			//intent = new Intent(CardSaleTransactionView.this, CreditSaleDeclineActivity.class);
		}
*/

		Intent intent = new Intent();

		if(mIsEmiSale) {

			mCardSaleResponseData.setPin(false);
			mCardSaleResponseData.setSignatureRequired(true);

			ReceiptData reciptDataModel = mCardSaleResponseData.getReceiptData();

			if(reciptDataModel != null)
				reciptDataModel.isPinVarifed = "false";
		}

		intent.putExtra("Title", mCardSaleDlgTitle);
		intent.putExtra("cardSaleResponseData", mCardSaleResponseData);


		if(mCardSaleResponseData.getResponseStatus())
		{

			intent.putExtra("status", true);
		}
		else {

			intent.putExtra("status", false);

		}

		setResult(Activity.RESULT_OK, intent);

		//startActivity(intent);

		doneWithCreditSale(EMVPPROCESSTASTTYPE.SHOW_SIGNATURE);

	}

	class WisePadGatewayConncetionListener implements MSGatewayConnectionListener {

		@Override
		public void Connected(String msg) {

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, " msg " + msg);

			imgHostConnectionStatus.setAnimation(null);
			imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_active);
		}

		@Override
		public void Connecting(String msg) {


			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, " msg " + msg);

			imgHostConnectionStatus.startAnimation(alphaAnim);
			imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_inactive);
		}

		@Override
		public void disConnect(String msg) {

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, " msg " + msg);

			imgHostConnectionStatus.setAnimation(null);
			imgHostConnectionStatus.setImageResource(R.drawable.topbar_img_host_inactive);
		}
	}


	private void playSound(long delay, int soundfile) {

		new playBeepTask(soundfile).execute(delay);
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "");
	}

	private class playBeepTask extends AsyncTask<Long, Void, Void> {

		int soundfile;

		playBeepTask(int soundfile) {
			this.soundfile = soundfile;
		}

		@Override
		protected Void doInBackground(Long... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(params[0]);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			MediaPlayer mp;
			mp = MediaPlayer.create(CardSaleTransactionView.this, soundfile);
			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.reset();
					mp.release();
					mp = null;
				}
			});
			mp.start();
			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "");
		}

	}

	/**
	 * @description
	 *        We are removing specific character form original string.
	 * @param s original string
	 * @param c specific character.
	 * @return
	 */
	public String removeChar(String s, char c) {

		String r = "";

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c)
				r += s.charAt(i);
		}

		return r;
	}

}