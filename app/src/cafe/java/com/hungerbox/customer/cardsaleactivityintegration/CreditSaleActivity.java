package com.hungerbox.customer.cardsaleactivityintegration;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hungerbox.customer.R;
import com.hungerbox.customer.cardsaleactivityintegration.fragments.CreditSaleAmextCardPinEntryFragment;
import com.hungerbox.customer.cardsaleactivityintegration.fragments.CreditSaleDetailsFragment;
import com.hungerbox.customer.cardsaleactivityintegration.fragments.CreditSaleSwiperFragment;
import com.hungerbox.customer.cardsaleactivityintegration.fragments.EmiTermConditionFragment;
import com.mswipetech.sdk.network.MSGatewayConnectionListener;
import com.mswipetech.wisepad.sdk.MSWisepadController;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceController;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;
import com.mswipetech.wisepad.sdk.device.WisePadConnection;
import com.mswipetech.wisepad.sdk.device.WisePadTransactionState;

import java.util.ArrayList;

import static com.hungerbox.customer.cardsaleactivityintegration.Utils.removeChar;


public class CreditSaleActivity extends MSWisepadView
{

	/*enum constants using card sale fragments*/
	public CreditSaleEnumClass.CardSaleScreens mCardSaleScreens;
	public CreditSaleEnumClass.CardSaleScreens mCardSalePrevScreens;

	/*images to show the bluetooth and  websocket connection status i.e wheteher topbar_img_host_active or not*/
	public ImageView imgBlueToothStatus, imgHostConnectionStatus;
	public LinearLayout mLNRTopbarCancel;

	/*using these animation for connection status*/
	public Animation alphaAnim;

	/**
	 * the wise pad has to be disconnected if its not required and also when the application closes
	 * the wise pad has to be disconnected,
	 * this will see to that the dis-connecting to the wisepad does has execute multiple times
	 */

	public boolean onDoneWithCreditSaleCalled = false;
	public boolean mIsIgnoreBackDevicekeyOnCardProcess = false;

	/**
	 * in certain scenarios the wise pad where in a likely case becomes non responsive, at that precise
	 * moment a task is called and with in the elapsed time if their no communication back to the app this task will
	 * un-initiate the communication link from the wisepad
	 */

	public EMVProcessTask mEMVProcessTask = null;

	public enum EMVPPROCESSTASTTYPE{
		NO_TASK, STOP_BLUETOOTH, BACK_BUTTON,
		ONLINE_SUBMIT, SHOW_SIGNATURE}

	public EMVPPROCESSTASTTYPE mEMVPPROCESSTASTTYPE = EMVPPROCESSTASTTYPE.NO_TASK;


	/* To identify the tranaction message type general message or error */
	public CreditSaleMessageEnum.WisePosStateInfoType mWisePosStateInfoType = CreditSaleMessageEnum.WisePosStateInfoType.MessageType_None;



	/* we are storing the cardsale datea */
	public TransactionData mTransactionData;

	public String mWisePosStateInfo = "";

	protected ArrayList<CreditSaleEnumClass.CardSaleScreens> CardSaleScreensFlow = new ArrayList<CreditSaleEnumClass.CardSaleScreens>();

	private long mBackPressed=0;

	/*the preauth transaction if enabled to the user, since this is similar to the normal transaction they
	are handled here, and if this are configured for the user then a different set of api will be called */

	public boolean mIsPreAuth = false;
	public boolean mIsEmiSale = false;
	public boolean mIsSaleWithCash = false;

	public String mEmiPeriod = "";
	public String mEmiBankCode = "";
	public String mEmiRate = "";
	public String mEmiAmount = "";

	public String mReferenceId = "";
	public String mSessionToken = "";
	public boolean mIsTipEnable = false;
	public boolean mIsReceiptEnable = false;


	public float mConveniencePercentage;
	public float mServicePercentageOnConvenience;

	MSWisepadController.NETWORK_SOURCE mNetworkSource = null;
	MSWisepadController.GATEWAY_ENVIRONMENT mGatewayEnvironment = null;

	public  Boolean mIsConvenienceFeesEnabled = false;

	/**
	 * Called when the activity is first created.
	 * @param savedInstanceState
	 */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.creditsale_fragment);


		mEMVPPROCESSTASTTYPE = EMVPPROCESSTASTTYPE.NO_TASK;
		mTransactionData = new TransactionData();

		mReferenceId = getIntent().getStringExtra("referenceid");
		mSessionToken = getIntent().getStringExtra("sessiontoken");

		mIsTipEnable = getIntent().getBooleanExtra("tipenable", false);
		mIsReceiptEnable = getIntent().getBooleanExtra("receiptenable", false);

		mGatewayEnvironment = (MSWisepadController.GATEWAY_ENVIRONMENT) getIntent().getSerializableExtra("gatewayenvironment");
		mNetworkSource = (MSWisepadController.NETWORK_SOURCE) getIntent().getSerializableExtra("networksource");

		mConveniencePercentage = getIntent().getFloatExtra("conveniencepercentage", 0);
		mServicePercentageOnConvenience = getIntent().getFloatExtra("servicepercentageonconvenience", 0);

		mIsPreAuth = getIntent().getBooleanExtra("preauthsale", false);
		mIsEmiSale = getIntent().getBooleanExtra("emisale", false);
		mIsSaleWithCash = getIntent().getBooleanExtra("salewithcash", false);

		try {

			if(mIsEmiSale)
			{
				mEmiRate = getIntent().getStringExtra("emirate");
				mEmiAmount = getIntent().getStringExtra("emiamount");
				mEmiPeriod = getIntent().getStringExtra("emiperiod");
				mEmiBankCode = getIntent().getStringExtra("emibankcode");
				mTransactionData.mCardFirstSixDigit = getIntent().getStringExtra("cardfirstsixdigit");
				mTransactionData.mBaseAmount = getIntent().getStringExtra("baseamount");
			}
			else if(mIsSaleWithCash){
				mTransactionData.mSaleCashAmount = getIntent().getStringExtra("salecashamount");
			}else {
				mTransactionData.mBaseAmount = getIntent().getStringExtra("baseamount");
			}
			mTransactionData.mTotAmount = getIntent().getStringExtra("totalamount");
			mTransactionData.mTipAmount = getIntent().getStringExtra("tipamount");

			mTransactionData.mPhoneNo = getIntent().getStringExtra("mobileno");
			mTransactionData.mReceipt = getIntent().getStringExtra("receiptno");
			mTransactionData.mEmail = getIntent().getStringExtra("email");
			mTransactionData.mNotes = getIntent().getStringExtra("notes");
			mTransactionData.mExtraNote1 = getIntent().getStringExtra("extra1");
			mTransactionData.mExtraNote2 = getIntent().getStringExtra("extra2");
			mTransactionData.mExtraNote3 = getIntent().getStringExtra("extra3");
			mTransactionData.mExtraNote4 = getIntent().getStringExtra("extra4");
			mTransactionData.mExtraNote5 = getIntent().getStringExtra("extra5");
			mTransactionData.mExtraNote6 = getIntent().getStringExtra("extra6");
			mTransactionData.mExtraNote7 = getIntent().getStringExtra("extra7");
			mTransactionData.mExtraNote8 = getIntent().getStringExtra("extra8");
			mTransactionData.mExtraNote9 = getIntent().getStringExtra("extra9");
			mTransactionData.mExtraNote10 = getIntent().getStringExtra("extra9");
		}
		catch (Exception e){


		}


		double convenienceFeePercentage = 0.0f;
		double servicePercentage = 0.0f;

		if (!mIsEmiSale && !mIsPreAuth && !mIsSaleWithCash &&
				AppSharedPrefrences.getAppSharedPrefrencesInstace().getTipEnabled())
		{
			try {
				convenienceFeePercentage = Double.parseDouble(AppSharedPrefrences.getAppSharedPrefrencesInstace().getConveniencePercentage() + "");

				if(convenienceFeePercentage > 0)
				{
					mIsConvenienceFeesEnabled = true;
				}
				else{
					mIsConvenienceFeesEnabled = false;
				}

			}
			catch (Exception ex) {

				ex.printStackTrace();
				convenienceFeePercentage = 0.0f;
				mIsConvenienceFeesEnabled =false;
			}

			try
			{
				servicePercentage = Double.parseDouble(AppSharedPrefrences.getAppSharedPrefrencesInstace().getServicePercentageOnConvenience() + "");
			}
			catch (Exception ex) {
				mIsConvenienceFeesEnabled =false;
				ex.printStackTrace();
				servicePercentage = 0.0f;
			}
		}
		else {

			mIsConvenienceFeesEnabled =false;
			convenienceFeePercentage = 0.0f;
		}
		try{

			if (mIsConvenienceFeesEnabled)
			{
				try {

					String stAmount = removeChar(mTransactionData.mBaseAmount, ',');
					double baseAmount = Double.parseDouble(stAmount);

					double mConvenienceFeeAmount =  (double) Math.round((baseAmount * convenienceFeePercentage)) / 100;
					double mGSTAmount = (double) Math.round((mConvenienceFeeAmount * servicePercentage))/100;

					mTransactionData.mConvenienceAmount = (String.format("%.2f", mConvenienceFeeAmount));
					mTransactionData.mServiceTaxamount = (String.format("%.2f", mGSTAmount));

					mTransactionData.mTotAmount = String.format("%.2f",
							mTransactionData.mBaseAmount +
									Double.parseDouble(mTransactionData.mConvenienceAmount) +
									Double.parseDouble(mTransactionData.mServiceTaxamount));
				}
				catch (Exception ex) {

					ex.printStackTrace();
				}
			}
		}
		catch (Exception ex) {

			ex.printStackTrace();
		}

		mCardSaleDlgTitle = getResources().getString(R.string.card_sale);

		if (mIsPreAuth)
			mCardSaleDlgTitle = getResources().getString(R.string.preauth);
		else if (mIsSaleWithCash)
			mCardSaleDlgTitle = getResources().getString(R.string.sale_cash);
		else if (mIsEmiSale)
			mCardSaleDlgTitle = getResources().getString(R.string.emi);

		mCardSaleScreens = CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_NONE;
		mCardSalePrevScreens = CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_NONE;
		/*if(mIsSaleWithCash)
		{
			showScreen(CardSaleScreens.CardSaleScreens_SALECASH_AMOUNT);
		}
		else if(mIsEmiSale) {

			showScreen(CardSaleScreens.CardSaleScreens_EMI_AMOUNT);
		}else {

			showScreen(CardSaleScreens.CardSaleScreens_AMOUNT);
		}
*/


		//showScreen(CardSaleScreens.CardSaleScreens_AMOUNT);
		showScreen(CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT);


		initViews();
		startMSGatewayConnection();

	}

	/**
	 * @description:
	 * 	 Initializes the UI elements for the Creditsale activity
	 * 	 @param
	 * 	 @return
	 *
	 */

	private void initViews()
	{


		TextView txtHeading = ((TextView) findViewById(R.id.topbar_LBL_heading));
		txtHeading.setText(mCardSaleDlgTitle);

		imgBlueToothStatus = (ImageView) findViewById(R.id.topbar_IMG_position1);
		imgHostConnectionStatus = (ImageView) findViewById(R.id.topbar_IMG_position2);

		mLNRTopbarCancel = (LinearLayout)findViewById(R.id.topbar_LNR_topbar_cancel);
		mLNRTopbarCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);
			}
		});


		imgBlueToothStatus.setVisibility(View.VISIBLE);
		imgHostConnectionStatus.setVisibility(View.VISIBLE);

		alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);

	}


	@Override
	protected void onStop()
	{
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, " ");

		/*stops the gateway connection */
		stopMSGatewayConnection();

		if (!onDoneWithCreditSaleCalled)
		{

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName,  "onDoneWithCreditSaleCalled " + onDoneWithCreditSaleCalled);

			doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);
			super.onStop();

		} else {
			super.onStop();
		}
	}

	/**
	 * We are finishing all running processes.
	 * @param processTaskType
	 * @return
	 */

	public void doneWithCreditSale(EMVPPROCESSTASTTYPE processTaskType)
	{
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName,  "doneWithCreditSale  processTaskType " + processTaskType);

		if (!onDoneWithCreditSaleCalled)
		{

			if (mEMVProcessTask != null && mEMVProcessTask.getStatus() != AsyncTask.Status.FINISHED)
				mEMVProcessTask.cancel(true);

			mEMVPPROCESSTASTTYPE = processTaskType;
			mEMVProcessTask = new EMVProcessTask(); //every time create new object, as AsynTask will only be executed one time.
			mEMVProcessTask.execute();

			onDoneWithCreditSaleCalled = true;
		}
	}

	class EMVProcessTask extends AsyncTask<Void, Integer, Void>
	{
		@Override
		protected void onCancelled()
		{

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName,  "onCancelled mEMVPPROCESSTASTTYPE " + mEMVPPROCESSTASTTYPE);

			//when the back tab is pressed
			if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.BACK_BUTTON)
			{
				moveToPrevious();
				moveToPrevious();
			}
			else if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.ONLINE_SUBMIT)
			{

			}
			else if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.STOP_BLUETOOTH)
			{
				/*Intent intent = new Intent(CreditSaleActivity.this, MenuView.class);
				startActivity(intent);*/
				finish();
			}
			else if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.SHOW_SIGNATURE)
			{
				finish();
			}

			mEMVPPROCESSTASTTYPE = EMVPPROCESSTASTTYPE.NO_TASK;

		}

		@Override
		protected Void doInBackground(Void... unused) {

			//calling after this statement and canceling task will no meaning if you do some update database kind of operation
			//so be wise to choose correct place to put this condition
			//you can also put this condition in for loop, if you are doing iterative task
			//you should only check this condition in doInBackground() method, otherwise there is no logical meaning

			// if the task is not cancelled by calling LoginTask.cancel(true), then make the thread wait for 10 sec and then
			//quit it self


			int isec = 4;

			if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.STOP_BLUETOOTH)
				isec = 2;

			if (getWisePadConnectionState() == WisePadConnection.WisePadConnection_NO_PAIRED_DEVICES_FOUND)
				isec = 0;

			int ictr = 0;


			//it will wait for 15 sec or till the task is cancelled by the mSwiper routines.
			while (!isCancelled() & ictr < isec) {
				try {
					Thread.sleep(500);
				} catch (Exception ex) {
				}
				ictr++;
			}

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName,  "EmvOnlinePorcessTask  end doInBackground");
			return null;
		}

		@Override
		protected void onPostExecute(Void unused)
		{
			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName,  "onPostExecute mEMVPPROCESSTASTTYPE " + mEMVPPROCESSTASTTYPE);

			//when the back tab is pressed
			if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.BACK_BUTTON)
			{
				moveToPrevious();
				moveToPrevious();
			}
			else if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.ONLINE_SUBMIT)
			{

			}
			else if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.STOP_BLUETOOTH)
			{
				/*Intent intent = new Intent(CreditSaleActivity.this, MenuView.class);
				startActivity(intent);*/
				finish();
			}
			else if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.SHOW_SIGNATURE)
			{
				finish();
			}

			mEMVPPROCESSTASTTYPE = EMVPPROCESSTASTTYPE.NO_TASK;
		}
	}

	/**
	 * startMSGatewayConnection
	 *
	 * to start the server connections after we got, all the details from the user,
	 * amount, mobiel and receipt details
	 *
	 */
	public void startMSGatewayConnection() {

		new Handler().post(new Runnable()
		{
			@Override
			public void run() {

				MSWisepadController.getSharedMSWisepadController(CreditSaleActivity.this,
						AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment(),
						AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource(),
						new WisePadGatewayConncetionListener()).startMSGatewayConnection(CreditSaleActivity.this);
			}
		});

	}


	/**
	 * stopMSGatewayConnection
	 *
	 * to stop the server connections after transaction completed
	 *
	 */
	public void stopMSGatewayConnection() {

		/*
		MSWisepadController.getSharedMSWisepadController(CreditSaleActivity.this,
				AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment(),
				AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource(),
				new WisePadGatewayConncetionListener()).stopMSGatewayConnection();
				*/
	}

	public boolean isIgnoreBackDevicekeyOnCardProcess() {
		return mIsIgnoreBackDevicekeyOnCardProcess;
	}

	public void setIgnoreBackDevicekeyOnCardProcess(boolean ignoreBackDevicekeyOnCardProcess) {
		this.mIsIgnoreBackDevicekeyOnCardProcess = ignoreBackDevicekeyOnCardProcess;
	}

	/**setWisePadConnectionState
	 *	set the views to the connection state
	 * @param
	 * wisePadConnection
	 * 		The state of the wise pad connection
	 * @return
	 */

	@Override
	public void setWisePadConnectionStateResult(WisePadConnection wisePadConnection)
	{

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, " wisePadConnection " + wisePadConnection);

		if(mWisepadStateListner != null)
			mWisepadStateListner.onReturnWisepadConnectionState(wisePadConnection);


		if(wisePadConnection == WisePadConnection.WisePadConnection_CONNECTING)
		{

			imgBlueToothStatus.startAnimation(alphaAnim);
			imgBlueToothStatus.setImageResource(R.drawable.topbar_img_bluetooth_inactive);
			setIgnoreBackDevicekeyOnCardProcess(false);

		}
		else if(wisePadConnection == WisePadConnection.WisePadConnection_NOT_CONNECTED){
			if (mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.BACK_BUTTON
					|| mEMVPPROCESSTASTTYPE == EMVPPROCESSTASTTYPE.ONLINE_SUBMIT) {
				if (mEMVProcessTask != null && mEMVProcessTask.getStatus() != AsyncTask.Status.FINISHED)
					mEMVProcessTask.cancel(true);
			}

			setIgnoreBackDevicekeyOnCardProcess(false);

		}
		else if(wisePadConnection == WisePadConnection.WisePadConnection_CONNECTED){

			setIgnoreBackDevicekeyOnCardProcess( false);
			imgBlueToothStatus.setAnimation(null);
			imgBlueToothStatus.setImageResource(R.drawable.topbar_img_bluetooth_active);

		}
		else if(wisePadConnection == WisePadConnection.WisePadConnection_DEVICE_NOTFOUND){
			if (mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.BACK_BUTTON
					|| mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.ONLINE_SUBMIT) {
				if (mEMVProcessTask != null && mEMVProcessTask.getStatus() != AsyncTask.Status.FINISHED)
					mEMVProcessTask.cancel(true);
			}
			imgBlueToothStatus.setAnimation(null);
			imgBlueToothStatus.setImageResource(R.drawable.topbar_img_bluetooth_inactive);


			setIgnoreBackDevicekeyOnCardProcess( false);

		}
		else if(wisePadConnection == WisePadConnection.WisePadConnection_DIS_CONNECTED){

			if (mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.BACK_BUTTON
					|| mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.ONLINE_SUBMIT
					|| mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.STOP_BLUETOOTH) {
				if (mEMVProcessTask != null && mEMVProcessTask.getStatus() != AsyncTask.Status.FINISHED)
					mEMVProcessTask.cancel(true);
			}
			imgBlueToothStatus.setAnimation(null);
			imgBlueToothStatus.setImageResource(R.drawable.topbar_img_bluetooth_inactive);
			setIgnoreBackDevicekeyOnCardProcess( false);

		}
		else if(wisePadConnection == WisePadConnection.WisePadConnection_NO_PAIRED_DEVICES_FOUND){

			if (mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.BACK_BUTTON
					|| mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.ONLINE_SUBMIT
					|| mEMVPPROCESSTASTTYPE == mEMVPPROCESSTASTTYPE.STOP_BLUETOOTH) {
				if (mEMVProcessTask != null && mEMVProcessTask.getStatus() != AsyncTask.Status.FINISHED)
					mEMVProcessTask.cancel(true);
			}

			imgBlueToothStatus.setAnimation(null);
			imgBlueToothStatus.setImageResource(R.drawable.topbar_img_bluetooth_inactive);
			setIgnoreBackDevicekeyOnCardProcess( false);

		}
	}

	@Override
	public void setWisePadStateInfo(String msg){

		mWisePosStateInfo = msg;
		mWisePosStateInfoType = CreditSaleMessageEnum.WisePosStateInfoType.MessageType_Info;

		if(mWisepadStateListner != null)
			mWisepadStateListner.onReturnWisepadTransactionState(getWisePadTranscationState(), mWisePosStateInfo, mWisePosStateInfoType);
	}

	@Override
	public void setWisePadStateErrorInfo(String msg) {

		mWisePosStateInfo = msg;
		mWisePosStateInfoType = CreditSaleMessageEnum.WisePosStateInfoType.MessageType_Error;

		if (mWisepadStateListner != null)
			mWisepadStateListner.onReturnWisepadTransactionState(getWisePadTranscationState(), mWisePosStateInfo, mWisePosStateInfoType);
	}

	/*this will get called only in the total amount and when the total amount will bot be shown
	i.w in the amount screen
	 */
	@Override
	public void setWisepadAmount()
	{

		/* To start the gateway connection*/
		//startMSGatewayConnection();

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "");


		if (mMSWisepadDeviceController != null)
			mMSWisepadDeviceController.setAmount(mTransactionData.mTotAmount, MSWisepadDeviceControllerResponseListener.TransactionType.GOODS);


	}

	/**
	 *  where we are showing details screen to the merchant
	 *  @param
	 *  @return
	 */
	@Override
	public void showCardDetails() {
		// TODO Auto-generated method stub

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "getWisePadTranscationState " + getWisePadTranscationState());

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "mCardSaleScreens " + mCardSaleScreens);


		if (getWisePadTranscationState() == WisePadTransactionState.WisePadTransactionState_ICC_Online_Process
				|| getWisePadTranscationState() == WisePadTransactionState.WisePadTransactionState_MAG_Online_Process) {

			if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT){

				String dlgMsg = "";
				if(mCardData.getCardSchemeResults() == MSWisepadDeviceControllerResponseListener.CARDSCHEMERRESULTS.ICC_CARD){

					dlgMsg = getString(R.string.cardsaleactivity_cardinsertinsert_msg_CreditSaleActivity);
				}
				else {

					dlgMsg = getString(R.string.cardsaleactivity_cardswiperead_msg_CreditSaleActivity);
				}

				/*final Dialog dialog = Constants.showDialog(CreditSaleActivity.this, mCardSaleDlgTitle, dlgMsg, Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_INFO);
				Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);

				yes.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						showCardDetailsScreen();
					}
				});

				dialog.show();
*/

				showCardDetailsScreen();
			}

		}
	}

	/**
	 * @description
	 *   	After successful swipe/insert,we are showing details of the merchant to merchant on details frament.
	 * @param
	 * @return
	 *
	 */
	public void showCardDetailsScreen()
	{
		// TODO Auto-generated method stub

		SharedPreferences preferences;
		preferences = PreferenceManager.getDefaultSharedPreferences(CreditSaleActivity.this);
		preferences.edit().putString(MSWisepadDeviceController.LAST_CONNECTED_DEVCIECLASSNAME, mBlueToothMcId).commit();


		int ilen = mCardData.getCreditCardNo().length();

		String tempFirst2Digits;
		if (ilen >= 2)
			tempFirst2Digits = mCardData.getCreditCardNo().substring(0, 2);
		else
			tempFirst2Digits = mCardData.getCreditCardNo();

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName,  "mCardData.getCreditCardNo() " + mCardData.getCreditCardNo());

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName,  "mIsEmiSale " + mIsEmiSale);


		if(mIsEmiSale)
		{
			if (ilen >= 6) {

				String cardFirstSixDigit = mCardData.getCreditCardNo().substring(0, 6);

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName,  "mTransactionData.mCardFirstSixDigit " + mTransactionData.mCardFirstSixDigit);

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName,  "cardFirstSixDigit " + cardFirstSixDigit);


				if (!mTransactionData.mCardFirstSixDigit.equalsIgnoreCase(cardFirstSixDigit))
				{
					final Dialog dialog = Constants.showDialog(CreditSaleActivity.this, "", "invalid card, card first six digits not matched.", Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_INFO);
					Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);
					yes.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							dialog.dismiss();

							if(mCardData.getCardSchemeResults() == MSWisepadDeviceControllerResponseListener.CARDSCHEMERRESULTS.ICC_CARD)
								requestCancelOnlineProcess();
							else
								requestCheckCard();

							showScreen(CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT);

						}
					});

					dialog.show();

					return;
				}
			}
		}

		showScreen(CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARDDETAILS);

	}

	@Override
	public void setWisepadWaitingForCardResult() {

		setIgnoreBackDevicekeyOnCardProcess(true);
	}

	@Override
	public void showMultiplePairedDeviceDialog(){

		TaskShowMultiplePairedDevices pairedtask = new TaskShowMultiplePairedDevices();
		pairedtask.execute();
	}

	public void showNoPairedDeviceDialog(){

		final Dialog dialog = Constants.showDialog(this, mCardSaleDlgTitle,
				getResources().getString(R.string.no_paired_wisePad_found_please_pair_the_wisePad_from_your_phones_bluetooth_settings_and_try_again), Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_INFO, getString(R.string.ok),"");
		Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);
		yes.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v) {

				doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);
				//dialog.dismiss();

			}
		});
		dialog.show();
	}


	@Override
	protected void onStart() {
		super.onStart();




	}

	/**
	 * @description
	 *     We are handling backbutton manually based on screen position.
	 * @param keyCode
	 * @param event
	 * @return
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName,  "onKeyDown  keyCode " + keyCode);


			// if the state of the devcie connection is no
			// DEVCIE_NO_PAIRED_DEVICES then from the amount screen it will take
			// to steps screen,
			// from here the below will restrict moving back.

			if((!mIsEmiSale && mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_AMOUNT || mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_SALECASH_AMOUNT))
			{
				doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);
			}
			else if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT
					&& isIgnoreBackDevicekeyOnCardProcess()){
				if (mBackPressed + 2000 > System.currentTimeMillis()) {

					doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);

				}
				else {
					Toast.makeText(this, getResources().getString(R.string.processing_card_in_progress_press_back_key_twice_in_succession_to_terminate_the_transaction), Toast.LENGTH_SHORT).show();
					mBackPressed = System.currentTimeMillis();
				}
			}
			else if(mCardSaleScreens != CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARDDETAILS){

				moveToPrevious();

			}

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * @description
	 *     We are moving to previous screen exctlay one from present screen.
	 * @param
	 * @return
	 */
	public void moveToPrevious() {

		if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_EMI_AMOUNT){

			doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);

		}
		else if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT){

			doneWithCreditSale(EMVPPROCESSTASTTYPE.STOP_BLUETOOTH);

		}
		else {

			showScreen(CardSaleScreensFlow.get(CardSaleScreensFlow.size() - 1));
		}

	}



	/**
	 * description
	 *       it will so amex pin pin entry fragment.
	 */
	@Override
	public void showAmexPinEntry()
	{

		// TODO Auto-generated method stub
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		Fragment fragment = null;

		if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARDDETAILS)
		{
			fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
		}

		fragment = new CreditSaleAmextCardPinEntryFragment();
		fragmentTransaction.replace(R.id.creditsale_fragmentcontainer, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		CardSaleScreensFlow.add(CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT);

		mCardSalePrevScreens = CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT;
		mCardSaleScreens = CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_AMEX_PINENTRY;
	}

	/**
	 * description
	 *      here we are handling fragment replacement.
	 * @param cardSaleScreens
	 *      screen that have to replace
	 * @return
	 */

	public void showScreen(CreditSaleEnumClass.CardSaleScreens cardSaleScreens)
	{

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		// TODO Auto-generated method stub

		Fragment fragment = null;
		switch (cardSaleScreens)
		{
			case CardSaleScreens_AMOUNT:

				/*if(mCardSaleScreens == CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT)
				{
					fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
				}

				fragment = new CreditSaleAmountFragment();
				fragmentTransaction.replace(R.id.creditsale_fragmentcontainer,fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();*/


/*
				String dlgMsg = String.format(CARDSALE_ALERT_AMOUNTMSG, ApplicationData.Currency_Code);
				final Dialog dialog = Constants.showDialog(CreditSaleActivity.this ,mCardSaleDlgTitle,
						dlgMsg + mTransactionData.mTotAmount, Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_CONFIRMATION);
				Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);
				yes.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						dialog.dismiss();


						final Dialog dialog = Constants.showDialog(CreditSaleActivity.this ,mCardSaleDlgTitle, getString(R.string.cardsaleactivity_wisepadamountset_msg), Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_SHOW_DLG_INFO);
						Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);
						yes.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {

								dialog.dismiss();

								initiateWisepadConnection();

							}
						});

						dialog.show();

					}
				});

				dialog.show();*/


				//showScreen(CardSaleScreens.CardSaleScreens_CARD_SWIPE_OR_INSERT);



				break;



			case CardSaleScreens_CARD_SWIPE_OR_INSERT:

				if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARDDETAILS ||
						mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_AMEX_PINENTRY )
				{
					fragmentTransaction.setCustomAnimations( R.anim.enter_from_left, R.anim.exit_to_right);
				}
				else if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_AMOUNT){

					fragmentTransaction.setCustomAnimations(  R.anim.enter_from_right, R.anim.exit_to_left);
				}

				fragment = new CreditSaleSwiperFragment();
				fragmentTransaction.replace(R.id.creditsale_fragmentcontainer,fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
				mWisepadStateListner = (WisepadTransactionStateListener) fragment;
				break;

			case CardSaleScreens_CARDDETAILS:

				fragmentTransaction.setCustomAnimations(  R.anim.enter_from_right, R.anim.exit_to_left);

				fragment = new CreditSaleDetailsFragment();
				fragmentTransaction.replace(R.id.creditsale_fragmentcontainer,fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
				break;

			case CardSaleScreens_EMI_TERM_CONDITION:

				//mDrawerRequired = false;
				//mLINTopBarMenu.setVisibility(View.GONE);
				//mLINTopBarCancel.setVisibility(View.GONE);

				if(mCardSaleScreens == CreditSaleEnumClass.CardSaleScreens.CardSaleScreens_CARDDETAILS)
				{

					fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left );
				}

				fragment = new EmiTermConditionFragment();
				fragmentTransaction.replace(R.id.creditsale_fragmentcontainer,fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
				break;


			default:
				break;
		}

		if (CardSaleScreensFlow.contains(cardSaleScreens))
			CardSaleScreensFlow.remove(cardSaleScreens);
		else
			CardSaleScreensFlow.add(mCardSaleScreens);

		mCardSalePrevScreens = mCardSaleScreens;
		mCardSaleScreens = cardSaleScreens;
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



}