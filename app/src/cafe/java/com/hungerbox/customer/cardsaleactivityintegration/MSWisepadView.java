package com.hungerbox.customer.cardsaleactivityintegration;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.hungerbox.customer.R;
import com.mswipetech.wisepad.sdk.data.CardData;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceController;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceController.LocalBinder;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceController.WisepadCheckCardMode;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;
import com.mswipetech.wisepad.sdk.device.WisePadConnection;
import com.mswipetech.wisepad.sdk.device.WisePadTransactionState;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * MSWisepadView 
 * 		class can be overridden to integrate the wisepad device,  necessarily here all the required actions are in place
 * which enable communications to the wise pad device
 */
public class MSWisepadView extends FragmentActivity
{

	/**
	 * the instance to communicate with the device service, this get initializes when the service successfully binds to
	 * the activity.
	 */

	public MSWisepadDeviceController mMSWisepadDeviceController = null;
	
	/*the callback listener with observes all the communications from the wise pad controller*/

	public MSWisepadDeviceObserver mMSWisepadDeviceObserver = null;
	public boolean mIsMSWisepasConnectionServiceBound;


	/**
	 * the details of the card used for the transaction returned my the mswipe wisepad device controller
	 * through the listener object
	 */

	public CardData mCardData = new CardData();

	/**
	 * progress for all the transaction which involve chip card, since for the EMV card's the wise pad need to
	 * communicate back the information from the user to the card, this will display the progress for this activities.
	 *
	 */

	protected CustomProgressDialog mProgressActivity = null;

	/**
	 * The amex card security code located at the back for the card has to be punched in when the mswipe wisepad
	 * detects the card been used is Amex card through the callback function onReturnWisePadOfflineCardTransactionResultsCARDSCHEMERRESULTS.MCR_AMEXCARD
	 */

	public String mCardSaleDlgTitle = "";


	/* by using this we are sending the status of the wisepad to the fragments to  progress the merchant */
	public WisepadTransactionStateListener mWisepadStateListner = null;

	public boolean mAutoConnect = false;
	public boolean mCallCheckCardAfterConnection = true;

	/*this will initialize the firm ware upate sdk*/
	public boolean mAllowFirmwareUpdatge = false;
	/*using this will be able to know whether  merchant is pressed amount next button for swipe or not
	 based on this we are setting the amount*/
	protected boolean isConnetCalled = false;

	/*storing the laste topbar_img_host_active wisepad id so that for next time it will connect automatically to this devise*/
	protected String mBlueToothMcId = "";

	/* when the flag from the login response for pin bypass is set to true then the trx should
	* be allowed to online if not then a bypassed should be displayed to the user
	* this should be re-initialzed in insert or checkcard or start emv since for the chcekc crad and
	* mag card straint online mag card would be called and not delegate to set this back wouldn't
	* be called
	 *  */
	public boolean mIsPinBypassed = false;

	public enum AutoInitiationDevice{CHECKCARD,START,DEVICE_INFO}


	/**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAutoConnect = getIntent().getBooleanExtra("autoconnect", true);
		mCallCheckCardAfterConnection = getIntent().getBooleanExtra("checkcardAfterConnection", true);


		try{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}catch(Exception e){}

        mMSWisepadDeviceObserver = new MSWisepadDeviceObserver();

	}

	@Override
	protected void onStart()
	{

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName,  " *********** onStart ");

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {

				/**
				 * binding the service will start the service, and notifies back to the bound objects and this will
				 * unable the interactions with the service
				 *
				 */

				try
				{
					doBindMswipeWisepadDeviceService();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}, 1000);

		super.onStart();
	}

	@Override
	protected void onStop()
	{
		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName, "onStop");
		//unbinding the service , when the app no longer requires the connection to the wisepad,
		//this will disconnect the connection to the wise pad
		try
		{
			doUnbindMswipeWisepadDeviceService();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		super.onStop();
	}
    
    /**
	 * @description
	 *        Initiates the wisepad device service which run in the back ground and controls the connections 
	 *        and disconnection and the application interactions independently of the application.
	 */

	void doBindMswipeWisepadDeviceService() 
	{
		bindService(new Intent(this, MSWisepadDeviceController.class), mMSWisepadDeviceControllerService, Context.BIND_AUTO_CREATE);
	}

	/**
	 * @description
	 *        Stops the wisepad service this need to be called when the wisepad is no more required, and 
	 *        this function should be called in onStop, this instance will be called when the app moves to 
	 *        back ground 
	 */

    public void doUnbindMswipeWisepadDeviceService()
    {

		if (ApplicationData.IS_DEBUGGING_ON)
			Log.v(ApplicationData.packName,  "wisePadConnection ");

		if (mIsMSWisepasConnectionServiceBound)
		{
			unbindService(mMSWisepadDeviceControllerService);

			mIsMSWisepasConnectionServiceBound = false;
		}
	}

    
	/**
	 * @description
	 *       The wisepad service callback listener, when the service is stopped or started 
	 *       the service connection object will be notified 
	 */

    private ServiceConnection mMSWisepadDeviceControllerService = new ServiceConnection()
    {
		public void onServiceConnected(ComponentName className, IBinder service) 
		{
			try
			{
				LocalBinder localBinder = (LocalBinder) service;
				mMSWisepadDeviceController = localBinder.getService();			
				mIsMSWisepasConnectionServiceBound = true;

				/**
				 * start the connection to the wise pad asynchronously, and call backs the listeners object
				 * with the status of the connection
				 */

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "Wisepad servcie binded staring the connection... " + mAllowFirmwareUpdatge);

				/**
				 * if any delay while initializing the service and user already requested for connect for wisepad,
				 * we are enabling the auto connect while initilizing the mswipe device controller.
				 */
				if(isConnetCalled)
				{
					mAutoConnect = getIntent().getBooleanExtra("autoconnect", true);
					mCallCheckCardAfterConnection = getIntent().getBooleanExtra("checkcardAfterConnection", true);
				}

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "mAutoConnect " + mAutoConnect +" mCallCheckCardAfterConnection "+mCallCheckCardAfterConnection);


				if(mMSWisepadDeviceController != null) {
					mMSWisepadDeviceController.initMswipeWisepadDeviceController(mMSWisepadDeviceObserver,
							mAutoConnect, false, mCallCheckCardAfterConnection,
							mAllowFirmwareUpdatge, WisepadCheckCardMode.SWIPE_OR_INSERT);
				}
			} 
			catch (Exception e) {

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "exception."+e.toString());
			}
		}

		public void onServiceDisconnected(ComponentName className) 
		{
			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "Wisepad servcie un-binded and wisepad is disconnected...");
			/**
			 * This is called when the connection with the service has been
			 * unexpectedly disconnected - process crashed.
			 *
			 */

			mIsMSWisepasConnectionServiceBound = false;
			mMSWisepadDeviceController = null;

		}
	};


	public void initiateWisepadConnection()
	{
		if(!isConnetCalled)
		{
			if (mMSWisepadDeviceController != null)
			{
				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "connecttion request");

				mMSWisepadDeviceController.connect();

			}
			else{
				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "device service not initialized" );
			}
			isConnetCalled = true;
		}
	}


	public void disConnect()
	{
		mMSWisepadDeviceController.disconnect();;
	}

	public void startNfcDetection(Hashtable<String, Object> data)
	{
		mMSWisepadDeviceController.startNfcDetection(data);
	}

	public void stopNfcDetection(Hashtable<String, Object> data)
	{
		mMSWisepadDeviceController.stopNfcDetection(data);
	}

	public void nfcDataExchange(Hashtable<String, Object> data)
	{
		mMSWisepadDeviceController.nfcDataExchange(data);


	}

	/*
	 * getWisePadTranscationState to get the transaction sate with the wisepad
	 *
	 */

	public WisePadConnection getWisePadConnectionState()
	{
		if(mMSWisepadDeviceController != null)
			return mMSWisepadDeviceController.getWisepadConnectionState();

		return  WisePadConnection.WisePadConnection_NOT_CONNECTED;
	}

	/*
	 * getWisePadTranscationState to get the transaction sate with the wisepad
	 *
	 */
	public WisePadTransactionState getWisePadTranscationState()
	{
		if(mMSWisepadDeviceController != null)
			return mMSWisepadDeviceController.getWisePadTransactionState();

		return  WisePadTransactionState.WisePadTransactionState_Ready;
	}

	public void setWisepadWaitingForCardResult() {}

	/*
	 * setWisePadConnectionState overridden function to display the connection state of the device
	 *
	 */
	public void setWisepadAmount(){}

	public void setWisePadConnectionStateResult(WisePadConnection wisePadConnection){}

	public void setWisePadStateInfo(String msg){}

	public void setWisePadStateErrorInfo(String msg){}

	public void setWisepadDeviceInfo(Hashtable<String, String> paramHashtable){}

	public void setNFCDeviceStatusInfo(String aTxtStatusMsg) {}

	public void setNFCReadResult(String aTxtStatusMsg) {}


	/* when the details of the card are notified back to the user,  override this function to display the details to the user
	 *
	 */
	public void showCardDetails(){}

	public void requestCheckCard(){


		if (mMSWisepadDeviceController != null)
		{
			mIsPinBypassed = false;
			mMSWisepadDeviceController.checkCard(WisepadCheckCardMode.SWIPE_OR_INSERT);
		}

	}

	public void requestCancelOnlineProcess(){

		if (mMSWisepadDeviceController != null)
			mMSWisepadDeviceController.cancelOnlineProcess();

	}

	public void requestDeviceInfo(){

		if (mMSWisepadDeviceController != null)
			mMSWisepadDeviceController.getDeviceInfo();
	}

	public void requestDisconnect(){

		if (mMSWisepadDeviceController != null)
			mMSWisepadDeviceController.disconnect();
	}

		/*Card Sale transaction */

	public void processDeviceConnetionWithAutoInitiation(AutoInitiationDevice aAutoInitiationDevice)
	{

		if (mMSWisepadDeviceController != null &&
				mMSWisepadDeviceController.getWisepadConnectionState() != WisePadConnection.WisePadConnection_CONNECTING)
		{
			if (mMSWisepadDeviceController != null &&
					!mMSWisepadDeviceController.isDevicePresent())
			{
				String msg = getString(R.string.mswisepadview_wisepad_not_connected_please_make_sure_that_the_wisepad_is_switched_on);


				final Dialog dialog = Constants.showDialog(MSWisepadView.this, mCardSaleDlgTitle,
						msg, Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_CONFIRMATION, getString(R.string.connect), getString(R.string.close));

				Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);
				yes.setOnClickListener(new OnClickListener()
				{

					public void onClick(View v) {
						dialog.dismiss();

						if(mMSWisepadDeviceController != null)
						{
							mMSWisepadDeviceController.connect();
						}
					}
				});

				Button no = (Button) dialog.findViewById(R.id.customdlg_BTN_no);
				no.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				return;
			}
			else {

				if (mMSWisepadDeviceController != null) {

					if(aAutoInitiationDevice == AutoInitiationDevice.CHECKCARD)
						mMSWisepadDeviceController.checkCard(WisepadCheckCardMode.SWIPE_OR_INSERT);
					else
						mMSWisepadDeviceController.getDeviceInfo();
				}
			}
		}
		else { // if the device is connecting in the back ground

			final Dialog dialog = Constants.showDialog(MSWisepadView.this, mCardSaleDlgTitle,
					getResources().getString(R.string.mswisepadview_connecting_to_wisepad_if_its_taking_longer_than_usual_please_restart_the_wisepad_and_try_reconnecting), Constants.CUSTOM_DLG_TYPE.CUSTOM_DLG_TYPE_RETURN_DLG_INFO);

			Button yes = (Button) dialog.findViewById(R.id.customdlg_BTN_yes);
			yes.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					dialog.dismiss();

				}
			});

			dialog.show();
		}
	}

	public void showAmexPinEntry(){}

	public void showMultiplePairedDeviceDialog(){}

	/**
     * MSWisepadDeviceObserver
     * 		The mswipe device controller class  observer which listens to the responses of the wisepad delegated function 
     * based on the device notification, appropriate steps  need to be considered or request should be sent back to the 
     * wisepad this will ensure a smooth communications back and forth between the wisepad device and the application
     */

	class MSWisepadDeviceObserver implements MSWisepadDeviceControllerResponseListener
	{
		/*the bluetooth connection channel states callback function between the device and application,
		*/
		public void onReturnWisepadConnection(WisePadConnection wisePadConntection, BluetoothDevice bluetoothDevice)
		{
			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "" + wisePadConntection);
			
			if(wisePadConntection == WisePadConnection.WisePadConnection_CONNECTED)
			{
				/*
				Store the last connected device to mswipelastconnecteddevice, so that the sdk will
				pickup this device to connected
				*/

				try 
				{
					mBlueToothMcId = bluetoothDevice.getName();
					
				} catch (Exception e) {
					// TODO: handle exception
				}


				setWisepadAmount();
				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_CONNECTED);

			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_CONNECTING){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_CONNECTING);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_DEVICE_DETECTED){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_DEVICE_DETECTED);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_DEVICE_NOTFOUND){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_DEVICE_NOTFOUND);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_NOT_CONNECTED){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_NOT_CONNECTED);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_DIS_CONNECTED){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_DIS_CONNECTED);

			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_FAIL_TO_START_BT){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_FAIL_TO_START_BT);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_BLUETOOTH_DISABLED){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_BLUETOOTH_DISABLED);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_BLUETOOTH_SWITCHEDOFF){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_BLUETOOTH_SWITCHEDOFF);

				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				final int REQUEST_ENABLE_BT = 0;
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_MULTIPLE_PAIRED_DEVCIES_FOUND){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_MULTIPLE_PAIRED_DEVCIES_FOUND);

			}
			else if(wisePadConntection == WisePadConnection.WisePadConnection_NO_PAIRED_DEVICES_FOUND){

				setWisePadConnectionStateResult(WisePadConnection.WisePadConnection_NO_PAIRED_DEVICES_FOUND);
			}
		}
		
		/*
		when the a request is sent to the the wise pad to check for the card used and hence when it detects the card, the device,
		callback with the information related to the card used or callbacks with information requesting information to be sent back to the wisepad,
		*/
		@Override
		public void onRequestWisePadCheckCardProcess(CheckCardProcess checkCardProcess, ArrayList<String> dataList)
		{
			// TODO Auto-generated method stub
			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "" + checkCardProcess);

			String msg = "";

			if(checkCardProcess == CheckCardProcess.CheckCardProcess_WAITING_FOR_CARD)
			{
				mIsPinBypassed = false;
				setWisepadWaitingForCardResult();
				setWisePadStateInfo(MSWisepadView.this.getString(R.string.waiting_for_card));
			}
			else if(checkCardProcess == CheckCardProcess.CheckCardProcess_SET_AMOUNT){

				setWisepadAmount();
				setWisePadStateInfo(MSWisepadView.this.getString(R.string.processing));
			}
			else if(checkCardProcess == CheckCardProcess.CheckCardProcess_PIN_ENTRY_MAG_CARD){

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.please_enter_pin_on_wisepad_or_press_enter_green_key_to_bypass_pin));
			}
			else if(checkCardProcess == CheckCardProcess.CheckCardProcess_PIN_ENTRY_ICC_CARD){

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.please_enter_pin_on_wisepad_or_press_enter_green_key_to_bypass_pin));

			}
			else if(checkCardProcess == CheckCardProcess.CheckCardProcess_SELECT_APPLICATION){

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.please_select_app));

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "onRequestSelectApplication " + dataList.size());

				if (dataList.size() == 1) {

					if(mMSWisepadDeviceController != null)
						mMSWisepadDeviceController.selectApplication(0);

				}
				else {


					final Dialog dialogApp = Constants.showAppCustomDialog(MSWisepadView.this, getResources().getString(R.string.please_select_app));

					String[] appNameList = new String[dataList.size()];
					for (int i = 0; i < appNameList.length; ++i) {
						appNameList[i] = dataList.get(i);

						if (ApplicationData.IS_DEBUGGING_ON)
							Log.v(getPackageName(),  "App name " + appNameList[i]);
					}

					ListView appListView = (ListView) dialogApp.findViewById(R.id.customapplicationdlg_LST_applications);


					appListView.setAdapter(new ListAdapter(MSWisepadView.this, dataList));
					appListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							dialogApp.dismiss();

							if(mMSWisepadDeviceController != null)
								mMSWisepadDeviceController.selectApplication(position);

						}

					});

					((Button)dialogApp.findViewById(R.id.customapplicationdlg_BTN_cancel)).setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {


							dialogApp.dismiss();
						}
					});

					dialogApp.show();

				}
			}
		}

		/*once the card is detected and then when the proper request had been collected from the user the devcie
		callback the details of the card, and this off-line data can be used to post to the gateway for further validation
		*/
		@Override
		public void onReturnWisePadOfflineCardTransactionResults(CheckCardProcessResults checkCardResults,
																 Hashtable<String, Object> paramHashtable)
		{
			// TODO Auto-generated method stub
			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "" + checkCardResults);


			if(checkCardResults == CheckCardProcessResults.ON_REQUEST_ONLINEPROCESS)
			{


				mCardData = (CardData)paramHashtable.get("cardData");

				if (ApplicationData.IS_DEBUGGING_ON)
					Log.v(ApplicationData.packName, "mCardData.getCardSchemeResults() " + mCardData.getCardSchemeResults());

				if (mCardData.getCardSchemeResults() == CARDSCHEMERRESULTS.ICC_CARD)
				{

					if(mIsPinBypassed)
					{
						if(AppSharedPrefrences.getAppSharedPrefrencesInstace().isPinBypassEnabled())
						{
							showCardDetails();
						}
						else{
							if(mMSWisepadDeviceController != null)
							{
								mMSWisepadDeviceController.sendOnlineProcessResult(null);

							}
						}
					}
					else
					{
						showCardDetails();
					}
				}
				else if (mCardData.getCardSchemeResults() == CARDSCHEMERRESULTS.MAG_CARD) {

					if(mIsPinBypassed )
					{
						if(AppSharedPrefrences.getAppSharedPrefrencesInstace().isPinBypassEnabled())
						{
							showCardDetails();
						}
						else {

							setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.creditsale_swiperview_pin_bypass_disabled));

						}
					}
					else {
						showCardDetails();
					}
				}
				else if (mCardData.getCardSchemeResults() == CARDSCHEMERRESULTS.MAG_AMEXCARD) {

					if(mIsPinBypassed)
					{
						if(AppSharedPrefrences.getAppSharedPrefrencesInstace().isPinBypassEnabled())
						{
							showAmexPinEntry();
						}
					}
					else {
						showAmexPinEntry();
					}

				}
			}
			else if(checkCardResults == CheckCardProcessResults.NO_CARD){

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.no_card_detected));
			}
			else if(checkCardResults == CheckCardProcessResults.NOT_ICC){

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.card_inserted));
			}
			else if(checkCardResults == CheckCardProcessResults.BAD_SWIPE){

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.bad_swipe));
			}
			else if(checkCardResults == CheckCardProcessResults.MAG_HEAD_FAIL){

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.mag_head_fail));
			}
			else if(checkCardResults == CheckCardProcessResults.USE_ICC_CARD){

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.please_use_chip_card));
			}
			else if(checkCardResults == CheckCardProcessResults.PIN_ENTERED) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.pin_entered));
			}
			else if(checkCardResults == CheckCardProcessResults.PIN_BYPASS) {

				mIsPinBypassed = true;
				setWisePadStateInfo(MSWisepadView.this.getString(R.string.bypass));

			}
			else if(checkCardResults == CheckCardProcessResults.PIN_CANCEL) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.pin_canceled));
			}
			else if (checkCardResults == CheckCardProcessResults.PIN_TIMEOUT) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.pin_timeout));
			}
			else if (checkCardResults == CheckCardProcessResults.PIN_UNKNOWN_ERROR) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.key_error));
			}
			else if (checkCardResults == CheckCardProcessResults.PIN_Asterisk) {

				String astreik = (String)paramHashtable.get("key");
				setWisePadStateInfo(astreik);
			}
			else if (checkCardResults == CheckCardProcessResults.PIN_WRONG_PIN_LENGTH) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.pin_wrong_pin_length));
			}
			else if (checkCardResults == CheckCardProcessResults.PIN_INCORRECT_PIN) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.no_pin));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_TERMINATED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_terminated));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_DECLINED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_declined));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_CANCELED_OR_TIMEOUT) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_cancel));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_CAPK_FAIL) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_capk_fail));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_NOT_ICC) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_not_icc));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_SELECT_APP_FAIL) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_app_fail));
			}
			else if (checkCardResults == CheckCardProcessResults.TRANSACTION_DEVICE_ERROR) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_device_error));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_APPLICATION_BLOCKED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_application_blocked));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_ICC_CARD_REMOVED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_icc_card_removed));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_CARD_BLOCKED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_card_blocked));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_CARD_NOT_SUPPORTED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_card_not_supported));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_CONDITION_NOT_SATISFIED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_condition_not_satisfied));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_INVALID_ICC_DATA) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_invalid_icc_data));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_MISSING_MANDATORY_DATA) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_missing_mandatory_data));
			}
			else if(checkCardResults == CheckCardProcessResults.TRANSACTION_NO_EMV_APPS) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_no_emv_apps));
			}
			else if(checkCardResults == CheckCardProcessResults.CANCEL_CHECK_CARD) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.canceled_please_try_again));
			}
			else{
				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.unknown_error));
			}
		}

		/*during the process of detecting the card and collecting the data from the user when the device encounters any issues it callback 
		through this delegate to notify the application with the specifics of the error. 
		*/
		@Override
		public void onError(Error errorState, String errorMsg)
		{

			String msg = "";

			if (errorState == Error.UNKNOWN)
			{
				msg = (MSWisepadView.this.getString(R.string.unknown_error));
			}
			else if (errorState ==  Error.CMD_NOT_AVAILABLE)
			{
				msg = (MSWisepadView.this.getString(R.string.command_not_available));
			} 
			else if (errorState ==  Error.TIMEOUT) {
				msg = (MSWisepadView.this.getString(R.string.device_no_response));
			}
			else if (errorState ==  Error.DEVICE_BUSY) {
				msg = (MSWisepadView.this.getString(R.string.device_busy));
			} 
			else if (errorState ==  Error.INPUT_OUT_OF_RANGE) {
				msg = (MSWisepadView.this.getString(R.string.out_of_range));
			} 
			else if (errorState ==  Error.INPUT_INVALID_FORMAT) {
				msg = (MSWisepadView.this.getString(R.string.invalid_format));
			}
			else if (errorState ==  Error.INPUT_INVALID) {
				msg = (MSWisepadView.this.getString(R.string.input_invalid));
			} 
			else if (errorState ==  Error.CASHBACK_NOT_SUPPORTED) {
				msg = (MSWisepadView.this.getString(R.string.cashback_not_supported));
			} 
			else if (errorState ==  Error.CRC_ERROR) {
				msg = (MSWisepadView.this.getString(R.string.crc_error));
			} 
			else if (errorState ==  Error.COMM_ERROR) {
				msg = (MSWisepadView.this.getString(R.string.comm_error));
			}
			else if(errorState ==  Error.FAIL_TO_START_BT) {
				msg = (MSWisepadView.this.getString(R.string.fail_to_start_bluetooth_v2));
			}
			else if(errorState ==  Error.FAIL_TO_START_AUDIO) {
				msg = (MSWisepadView.this.getString(R.string.fail_to_start_audio));
			}
			else if (errorState ==  Error.COMM_LINK_UNINITIALIZED) {
				msg = (MSWisepadView.this.getString(R.string.comm_link_uninitialized));
			}
			else if (errorState == Error.BTV4_NOT_SUPPORTED) {
				msg = (MSWisepadView.this.getString(R.string.bluetooth_4_not_supported));
			}
			else if (errorState == Error.FAIL_TO_START_SERIAL) {
				msg = ("Fail to start serial");
			}
			else if (errorState == Error.USB_DEVICE_NOT_FOUND) {
				msg = ("Usb device not found");
			}
			else if (errorState == Error.USB_DEVICE_PERMISSION_DENIED) {
				msg = ("Usb device permission denied");
			}
			else if (errorState == Error.USB_NOT_SUPPORTED) {
				msg = ("Usb device not supported");
			}
			else if (errorState == Error.CHANNEL_BUFFER_FULL) {
				msg = (MSWisepadView.this.getString(R.string.channel_buffer_full));
			}
			else if (errorState == Error.BLUETOOTH_PERMISSION_DENIED) {
				msg = (MSWisepadView.this.getString(R.string.bluetooth_permission_denied));
			}
			else if (errorState == Error.HARDWARE_NOT_SUPPORTED) {
				msg = ("Harware not supported");
			}
			else if (errorState == Error.TAMPER) {
				msg = ("Device tampered");
			}
			else if (errorState == Error.PCI_ERROR) {
				msg = ("Pci error");
			}
			else  {
				msg = (MSWisepadView.this.getString(R.string.unknown_error));
			}

			setWisePadStateErrorInfo(msg);

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "onError  the error state is " + errorState);
		
		}
		
		/*during the process of detecting the card and collecting the data from the user the device callback's 
		*through this delegate to notify the application about the information about the wise pad current processing state, this
		*information can be displayed back to the user and this information presented is just a text and the 
		* wise pad does not expect any action further actions from the user, this as to be used only for presenting the state of the wisepad to the user.
		*/
		@Override
		public void onRequestDisplayWispadStatusInfo(DisplayText displayText)
		{
			// TODO Auto-generated method stub

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, "the displayText is " + displayText);

			if (displayText == DisplayText.NOT_ICC_CARD) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.card_inserted));
			}
			else if (displayText == DisplayText.NO_EMV_APPS) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.card_inserted));
			}
			else if (displayText == DisplayText.APPROVED) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.approved));
			} 
			else if (displayText == DisplayText.CALL_YOUR_BANK) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.call_your_bank));
			}
			else if (displayText == DisplayText.DECLINED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.decline));
			} 
			else if (displayText == DisplayText.ENTER_PIN_BYPASS) {


				setWisePadStateInfo(MSWisepadView.this.getString(R.string.please_enter_pin_on_wisepad_or_press_enter_green_key_to_bypass_pin));
			}        
			else if (displayText == DisplayText.ENTER_PIN) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.enter_pin));
			} 
			else if (displayText == DisplayText.INCORRECT_PIN) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.incorrect_pin));
			} 
			else if (displayText == DisplayText.INSERT_CARD) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.insert_card));
			}
			else if (displayText == DisplayText.NOT_ACCEPTED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.not_accepted));
			}
			else if (displayText == DisplayText.PIN_OK) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.pin_ok));
			} 
			else if (displayText == DisplayText.PLEASE_WAIT) {

				/*
				As the state is pin result no need show other messages
				as we are waiting for the online process, so we are skipping this message
				for old firmware after pinentry result we are getting this extra message.
				 */

				if(getWisePadTranscationState() !=  WisePadTransactionState.WisePadTransactionState_Pin_Entry_Results)
					setWisePadStateInfo(MSWisepadView.this.getString(R.string.wait));

			}
			else if (displayText == DisplayText.REMOVE_CARD) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.remove_card));
			}
			else if (displayText == DisplayText.USE_MAG_STRIPE) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.use_mag_stripe));
			} 
			else if (displayText == DisplayText.TRY_AGAIN) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.try_again));
			} 
			else if (displayText == DisplayText.REFER_TO_YOUR_PAYMENT_DEVICE) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.refer_payment_device));
			} 
			else if (displayText == DisplayText.TRANSACTION_TERMINATED) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.transaction_terminated));
			}
			else if (displayText == DisplayText.PROCESSING) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.processing));
			}
			else if (displayText == DisplayText.LAST_PIN_TRY) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.last_pin_try));
			}
			else if(displayText == DisplayText.SELECT_ACCOUNT) {

				setWisePadStateInfo(MSWisepadView.this.getString(R.string.select_account));
			}
			else if(displayText == DisplayText.LOW) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.battery_low));
			}
			else if(displayText == DisplayText.CRITICALLY_LOW) {

				setWisePadStateErrorInfo(MSWisepadView.this.getString(R.string.battery_critically_low));
			}
		}

		/*
		 * Returns the information about the device, though this callback function initiated through getDeviceInfo
		 * */
		@Override
		public void onReturnDeviceInfo(Hashtable<String, String> paramHashtable) {
			// TODO Auto-generated method stub
			setWisepadDeviceInfo(paramHashtable);
		}

		@Override
		public void onReturnWispadNetwrokSettingInfo(WispadNetwrokSetting wispadNetwrokSetting, boolean status, Hashtable<String, Object> netwrokSettingInfo) {

		}


		@Override
		public void onReturnNfcDataExchangeResult(boolean isSuccess, Hashtable<String, String> data) {

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, isSuccess + " " + data );

			if (isSuccess)
			{
				/*String text = "nfc_data_exchange_success ";
				text += "\n" + "ndef_record "  + data.get("ndefRecord");
				setNFCDeviceStatusInfo(text);*/

				String text = data.get("ndefRecord");
				setNFCReadResult(text);

			}
			else {
				String text = "nfc_data_exchange_fail ";
				text += "\n" + "Error " + data.get("errorMessage");
				setNFCDeviceStatusInfo(text);
			}
		}

		@Override
		public void onReturnNfcDetectCardResult(NfcDetectCardResult nfcDetectCardResult, Hashtable<String, Object> data) {

			if (ApplicationData.IS_DEBUGGING_ON)
				Log.v(ApplicationData.packName, nfcDetectCardResult + " " + data);

			String text = "";
			text += "nfcDetectCardResult " ;

			if(nfcDetectCardResult == NfcDetectCardResult.NFC_WAITING_FOR_CARD)
			{
				text += MSWisepadView.this.getString(R.string.nfc_waiting_for_card);
			}
			else if(nfcDetectCardResult == NfcDetectCardResult.NFC_WAITING_CARD_REMOVAL)
			{
				text += MSWisepadView.this.getString(R.string.nfc_waiting_for_card_removal);
			}
			else if(nfcDetectCardResult == NfcDetectCardResult.NFC_CARD_REMOVED)
			{
				text += MSWisepadView.this.getString(R.string.nfc_card_removed);
			}
			else if(nfcDetectCardResult == NfcDetectCardResult.NFC_TIMEOUT)
			{
				text += MSWisepadView.this.getString(R.string.nfc_timeout);
			}
			else if(nfcDetectCardResult == NfcDetectCardResult.NFC_CARD_NOT_SUPPORTED)
			{
				text += MSWisepadView.this.getString(R.string.nfc_card_not_supported);
			}
			else if(nfcDetectCardResult == NfcDetectCardResult.NFC_MULTIPLE_CARD_DETECTED)
			{
				text += MSWisepadView.this.getString(R.string.nfc_multiple_card_detected);
			}
			else if(nfcDetectCardResult == NfcDetectCardResult.NFC_CARD_DETECTED)
			{
				text += MSWisepadView.this.getString(R.string.nfc_card_detected);

				text += "\n" + "nfc_tag_information " + data.get("nfcTagInfo");
				text += "\n" + "nfc_card_uid " + data.get("nfcCardUID");
			}

			if (data.containsKey("errorMessage")) {
				text += "\n" + "Error " + data.get("errorMessage");
			}

			setNFCDeviceStatusInfo(text);
		}
	}

	/*
	 * When the SDK detects multiple paired wisepad devices to connect to, it will display to the user to select
	 * any device from the list 
	 *
	 */
	public class TaskShowMultiplePairedDevices extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls)
		{
			return "";
		}

		@Override
		protected void onPostExecute(String result)
		{

			final Dialog dlgPairedDevices = Constants.showAppCustomDialog(MSWisepadView.this, getResources().getString(R.string.mswisepadview_select_wisepad));
			dlgPairedDevices.setCancelable(true);
			final ArrayList<BluetoothDevice> mswipeWisepadPairedDevices = mMSWisepadDeviceController.getMswipeWisepadPairedDevices();

			final ArrayList<String> deviceNameList = new ArrayList<String>();

			for (int i = 0; i < mswipeWisepadPairedDevices.size(); ++i)
			{
				deviceNameList.add(mswipeWisepadPairedDevices.get(i).getName());
			}

			final ListView appListView = (ListView) dlgPairedDevices.findViewById(R.id.customapplicationdlg_LST_applications);

			appListView.setAdapter(new ListAdapter(MSWisepadView.this, deviceNameList));
			appListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					if (mMSWisepadDeviceController != null)
						mMSWisepadDeviceController.connect(mswipeWisepadPairedDevices.get(position));

					dlgPairedDevices.dismiss();
				}

			});

			dlgPairedDevices.findViewById(R.id.customapplicationdlg_BTN_cancel).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					dlgPairedDevices.dismiss();
				}
			});

			dlgPairedDevices.show();
		}
	}

}