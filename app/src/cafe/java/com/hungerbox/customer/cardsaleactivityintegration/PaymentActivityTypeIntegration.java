package com.hungerbox.customer.cardsaleactivityintegration;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.mswipetech.wisepad.sdk.MSWisepadController;
import com.mswipetech.wisepad.sdk.data.EMIData;
import com.mswipetech.wisepad.sdk.data.EMITransactionDetailsResponseData;
import com.mswipetech.wisepad.sdk.data.MSDataStore;
import com.mswipetech.wisepad.sdk.listeners.MSWisepadControllerResponseListener;

import java.util.ArrayList;

public class PaymentActivityTypeIntegration extends FragmentActivity
{
	//fields for card sale amount details screen,
	EditText mTxtCreditAmount = null;
	EditText mTxtCreditTipAmount = null;
	EditText mTxtSaleCashAmount = null;

	EditText mTxtFirstSixDigits = null;

	EditText mTxtPhoneNum = null;
	EditText mTxtEmail = null;
	EditText mTxtReceipt = null;
	EditText mTxtNotes = null;

	EditText mTxtExtraOne = null;
	EditText mTxtExtraTwo = null;
	EditText mTxtExtraThree = null;
	EditText mTxtExtraFour = null;
	EditText mTxtExtraFive = null;
	EditText mTxtExtraSix = null;
	EditText mTxtExtraSeven = null;
	EditText mTxtExtraEight = null;
	EditText mTxtExtraNine = null;
	EditText mTxtExtraTen = null;

	boolean isCardSale ;
	boolean isSaleWithCash;
	boolean isEmiSale;
	boolean isPreAuth ;

	CustomProgressDialog mProgressActivity;
	ApplicationData applicationData = null;


	private String mEmiPeriod;
	private String mEmiBankCode;
	private String mEmiRate;
	private String mEmiAmount;

	int selectedPosition = -1;

	private String mTotalAmount = "";

	/**
	 * Called when the activity is first created.
	 * @param savedInstanceState
	 */

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.payment_amount_view);

		applicationData = ApplicationData.getApplicationDataSharedInstance();

		initViews();
	}


	/**
	 *@description
	 *      All fields intilising here.
	 *
	 */
	private void initViews()
	{

		isCardSale = getIntent().getBooleanExtra("cardsale", false);
		isSaleWithCash = getIntent().getBooleanExtra("salewithcash", false);
		isEmiSale = getIntent().getBooleanExtra("emisale", false);
		isPreAuth = getIntent().getBooleanExtra("preauthsale", false);

		TextView txtHeading = ((TextView) findViewById(R.id.topbar_LBL_heading));

		if(isSaleWithCash)
			txtHeading.setText("Cash");
		else if(isEmiSale)
			txtHeading.setText("EMI");
		else if(isPreAuth)
			txtHeading.setText("Pre Auth");
		else
			txtHeading.setText("CARD");


		//The screen are for the amount
		mTxtCreditAmount = (EditText) findViewById(R.id.payment_TXT_amount);
		mTxtCreditTipAmount = (EditText) findViewById(R.id.payment_TXT_tip_amount);
		mTxtSaleCashAmount = (EditText) findViewById(R.id.payment_TXT_salecashamount);

		mTxtCreditAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {


				calculateTotalAmt();

			}
		});


		mTxtCreditTipAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {


				calculateTotalAmt();

			}
		});

		mTxtSaleCashAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {

				calculateTotalAmt();

			}
		});


		mTxtFirstSixDigits = (EditText) findViewById(R.id.payment_TXT_first6digits);

		mTxtPhoneNum = (EditText) findViewById(R.id.payment_TXT_mobileno);
		mTxtEmail = (EditText) findViewById(R.id.payment_TXT_email);
		mTxtReceipt = (EditText) findViewById(R.id.payment_TXT_receipt);
		mTxtNotes = (EditText) findViewById(R.id.payment_TXT_notes);

		mTxtExtraOne = (EditText) findViewById(R.id.payment_TXT_extra_one);
		mTxtExtraTwo = (EditText) findViewById(R.id.payment_TXT_extra_two);
		mTxtExtraThree = (EditText) findViewById(R.id.payment_TXT_extra_three);
		mTxtExtraFour = (EditText) findViewById(R.id.payment_TXT_extra_four);
		mTxtExtraFive = (EditText) findViewById(R.id.payment_TXT_extra_five);
		mTxtExtraSix = (EditText) findViewById(R.id.payment_TXT_extra_six);
		mTxtExtraSeven = (EditText) findViewById(R.id.payment_TXT_extra_seven);
		mTxtExtraEight = (EditText) findViewById(R.id.payment_TXT_extra_eight);
		mTxtExtraNine = (EditText) findViewById(R.id.payment_TXT_extra_nine);
		mTxtExtraTen = (EditText) findViewById(R.id.payment_TXT_extra_ten);

		if (isEmiSale)
			((LinearLayout)findViewById(R.id.payment_LNR_first6digits)).setVisibility(View.VISIBLE);

		if(isSaleWithCash){

			((LinearLayout)findViewById(R.id.payment_LNR_salecashamount)).setVisibility(View.VISIBLE);
			((LinearLayout)findViewById(R.id.payment_LNR_amount)).setVisibility(View.GONE);
		}


		if(AppSharedPrefrences.getAppSharedPrefrencesInstace().getTipEnabled())
		{
			mTxtCreditTipAmount.setVisibility(View.VISIBLE);
		}

		Button btnAmtNext = (Button) findViewById(R.id.payment_BTN_amt_next);
		btnAmtNext.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if(imm != null)
					imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				double amount = 0;
				try
				{

					if(isSaleWithCash){

						if (mTxtSaleCashAmount.length() > 0)
							amount = Double.parseDouble(removeChar(mTxtSaleCashAmount.getText().toString(),','));

					}
					else {

						if (mTxtCreditAmount.length() > 0)
							amount = Double.parseDouble(removeChar(mTxtCreditAmount.getText().toString(),','));

					}

				}
				catch (Exception ex) {
					amount = 0;
				}

				if (amount < 1)
				{
					Constants.showDialog(PaymentActivityTypeIntegration.this, "pay", Constants.CARDSALE_ERROR_INVALIDAMT);
					return;
				}

				if (mTxtCreditTipAmount.length() > 0)
				{
					double tipamount  = Double.parseDouble(removeChar(mTxtCreditTipAmount.getText().toString(),','));

					if (tipamount < 1)
					{
						Constants.showDialog(PaymentActivityTypeIntegration.this, "pay", "invalid tip amount");
						return;
					}
				}

				if (mTxtPhoneNum.getText().toString().trim().length() != applicationData.PhoneNoLength) {

					String phoneLength = String.format(Constants.CARDSALE_ERROR_mobilenolen, applicationData.PhoneNoLength);
					Constants.showDialog(PaymentActivityTypeIntegration.this ,Constants.CASHSALE_DIALOG_MSG, phoneLength);
					mTxtPhoneNum.requestFocus();
					return;

				} else if (mTxtPhoneNum.getText().toString().trim().startsWith("0")) {

					Constants.showDialog(PaymentActivityTypeIntegration.this ,Constants.CASHSALE_DIALOG_MSG, Constants.CARDSALE_ERROR_mobileformat);
					mTxtPhoneNum.requestFocus();
					return;

				}
				else if ( mTxtEmail.getText().toString().trim().length() != 0) {

					if (emailCheck( mTxtEmail.getText().toString())) {

					} else {
						mTxtEmail.requestFocus();
						return;
					}

				}

				if(isEmiSale){

					if (mTxtFirstSixDigits.getText().toString().length() != 6)
					{
						Constants.showDialog(PaymentActivityTypeIntegration.this, "pay", "please enter valid first 6 digits");
						return;
					}
					else {

						getEmiDetails();

					}

				}
				else {

					boolean autoConnect = getIntent().getExtras().getBoolean("autoconnect");
					boolean checkCard = getIntent().getExtras().getBoolean("checkcardAfterConnection");

					Intent intent = new Intent(PaymentActivityTypeIntegration.this, CardSaleTransactionView.class);

					if (isPreAuth) {

						intent.putExtra("preauthsale", true);
						intent.putExtra("baseamount", mTxtCreditAmount.getText().toString().trim());

					}
					else if (isSaleWithCash) {

						intent.putExtra("salewithcash", true);
						intent.putExtra("salecashamount", mTxtSaleCashAmount.getText().toString().trim());

					}
					else if (isEmiSale) {

						intent.putExtra("emisale", true);
						intent.putExtra("baseamount", mTxtCreditAmount.getText().toString().trim());
					}
					else {
						intent.putExtra("cardsale", true);
						intent.putExtra("baseamount", mTxtCreditAmount.getText().toString().trim());

					}

					intent.putExtra("referenceid", AppSharedPrefrences.getAppSharedPrefrencesInstace().getReferenceId());
					intent.putExtra("sessiontoken", AppSharedPrefrences.getAppSharedPrefrencesInstace().getSessionToken());
					intent.putExtra("tipenable", AppSharedPrefrences.getAppSharedPrefrencesInstace().getTipEnabled());
					intent.putExtra("receiptenable", AppSharedPrefrences.getAppSharedPrefrencesInstace().isReceiptEnabled());

					intent.putExtra("gatewayenvironment", AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment());
					intent.putExtra("networksource", AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource());

					intent.putExtra("conveniencepercentage", AppSharedPrefrences.getAppSharedPrefrencesInstace().getConveniencePercentage());
					intent.putExtra("servicepercentageonconvenience", AppSharedPrefrences.getAppSharedPrefrencesInstace().getServicePercentageOnConvenience());

					intent.putExtra("totalamount", mTotalAmount);
					intent.putExtra("tipamount", mTxtCreditTipAmount.getText().toString().trim());

					intent.putExtra("mobileno", mTxtPhoneNum.getText().toString().trim());
					intent.putExtra("receiptno", mTxtReceipt.getText().toString().trim());
					intent.putExtra("email", mTxtEmail.getText().toString().trim());
					intent.putExtra("notes", mTxtNotes.getText().toString().trim());
					intent.putExtra("extra1", mTxtExtraOne.getText().toString().trim());
					intent.putExtra("extra2", mTxtExtraTwo.getText().toString().trim());
					intent.putExtra("extra3", mTxtExtraThree.getText().toString().trim());
					intent.putExtra("extra4", mTxtExtraFour.getText().toString().trim());
					intent.putExtra("extra5", mTxtExtraFive.getText().toString().trim());
					intent.putExtra("extra6", mTxtExtraSix.getText().toString().trim());
					intent.putExtra("extra7", mTxtExtraSeven.getText().toString().trim());
					intent.putExtra("extra8", mTxtExtraEight.getText().toString().trim());
					intent.putExtra("extra9", mTxtExtraNine.getText().toString().trim());
					intent.putExtra("extra10", mTxtExtraTen.getText().toString().trim());

					intent.putExtra("autoconnect", autoConnect);
					intent.putExtra("checkcardAfterConnection", checkCard);

					startActivityForResult(intent, 2002);
				}
			}
		});
	}


	public void getEmiDetails() {

		try {

			mProgressActivity = new CustomProgressDialog(PaymentActivityTypeIntegration.this, "EMI");
			mProgressActivity.show();

			MSWisepadController.getSharedMSWisepadController(PaymentActivityTypeIntegration.this,
					AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment(),
					AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource(),
					null).getEMISaleTrxDetails(
					AppSharedPrefrences.getAppSharedPrefrencesInstace().getReferenceId(),
					AppSharedPrefrences.getAppSharedPrefrencesInstace().getSessionToken(),
					mTxtFirstSixDigits.getText().toString().trim(),
					mTxtCreditAmount.getText().toString().trim(),
					new MSWisepadControllerResponseListenerObserver()
			);

		} catch (Exception e) {

			Constants.showDialog(PaymentActivityTypeIntegration.this, "EMI", "data decryption error");
		}
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

			if (aMSDataStore instanceof EMITransactionDetailsResponseData) {

				EMITransactionDetailsResponseData emiTransactionDetailsResponseData = (EMITransactionDetailsResponseData) aMSDataStore;

				if (emiTransactionDetailsResponseData.getResponseStatus()){

					final ArrayList<EMIData> arrayList = emiTransactionDetailsResponseData.getEmiTxtArrayListData();

					final Dialog dialog = Constants.showEmiOptionsDialog(PaymentActivityTypeIntegration.this);

					ListView listView = (ListView)dialog.findViewById(R.id.emisale_list_emi_selection);

					final EmiAdapter emiAdapter = new EmiAdapter(PaymentActivityTypeIntegration.this, arrayList);
					listView.setAdapter(emiAdapter);
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

							selectedPosition = position;

							mEmiPeriod = arrayList.get(position).emiTenure;
							mEmiBankCode = arrayList.get(position).emiBankCode;
							mEmiRate = arrayList.get(position).emiRate;
							mEmiAmount = arrayList.get(position).emiAmt;

							emiAdapter.notifyDataSetChanged();
						}
					});

					((Button)dialog.findViewById(R.id.customdlg_BTN_yes)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {


							if(selectedPosition == -1){

								Constants.showDialog(PaymentActivityTypeIntegration.this, "pay", "please select an option");

								return;
							}

							boolean autoConnect = getIntent().getExtras().getBoolean("autoconnect");
							boolean checkCard = getIntent().getExtras().getBoolean("checkcardAfterConnection");

							Intent intent = new Intent(PaymentActivityTypeIntegration.this, CardSaleTransactionView.class);

							if (isPreAuth) {

								intent.putExtra("preauthsale", true);
								intent.putExtra("baseamount", mTxtCreditAmount.getText().toString().trim());

							}
							else if (isSaleWithCash) {

								intent.putExtra("salewithcash", true);
								intent.putExtra("salecashamount", mTxtSaleCashAmount.getText().toString().trim());

							}
							else if (isEmiSale) {

								intent.putExtra("emisale", true);
								intent.putExtra("baseamount", mTxtCreditAmount.getText().toString().trim());
							}
							else {
								intent.putExtra("cardsale", true);
								intent.putExtra("baseamount", mTxtCreditAmount.getText().toString().trim());

							}

							intent.putExtra("referenceid", AppSharedPrefrences.getAppSharedPrefrencesInstace().getReferenceId());
							intent.putExtra("sessiontoken", AppSharedPrefrences.getAppSharedPrefrencesInstace().getSessionToken());
							intent.putExtra("tipenable", AppSharedPrefrences.getAppSharedPrefrencesInstace().getTipEnabled());
							intent.putExtra("receiptenable", AppSharedPrefrences.getAppSharedPrefrencesInstace().isReceiptEnabled());

							intent.putExtra("gatewayenvironment", AppSharedPrefrences.getAppSharedPrefrencesInstace().getGatewayEnvironment());
							intent.putExtra("networksource", AppSharedPrefrences.getAppSharedPrefrencesInstace().getNetworkSource());

							intent.putExtra("conveniencepercentage", AppSharedPrefrences.getAppSharedPrefrencesInstace().getConveniencePercentage());
							intent.putExtra("servicepercentageonconvenience", AppSharedPrefrences.getAppSharedPrefrencesInstace().getServicePercentageOnConvenience());

							intent.putExtra("totalamount", mTotalAmount);
							intent.putExtra("tipamount", mTxtCreditTipAmount.getText().toString().trim());

							intent.putExtra("mobileno", mTxtPhoneNum.getText().toString().trim());
							intent.putExtra("receiptno", mTxtReceipt.getText().toString().trim());
							intent.putExtra("email", mTxtEmail.getText().toString().trim());
							intent.putExtra("notes", mTxtNotes.getText().toString().trim());
							intent.putExtra("extra1", mTxtExtraOne.getText().toString().trim());
							intent.putExtra("extra2", mTxtExtraTwo.getText().toString().trim());
							intent.putExtra("extra3", mTxtExtraThree.getText().toString().trim());
							intent.putExtra("extra4", mTxtExtraFour.getText().toString().trim());
							intent.putExtra("extra5", mTxtExtraFive.getText().toString().trim());
							intent.putExtra("extra6", mTxtExtraSix.getText().toString().trim());
							intent.putExtra("extra7", mTxtExtraSeven.getText().toString().trim());
							intent.putExtra("extra8", mTxtExtraEight.getText().toString().trim());
							intent.putExtra("extra9", mTxtExtraNine.getText().toString().trim());
							intent.putExtra("extra10", mTxtExtraTen.getText().toString().trim());

							intent.putExtra("autoconnect", autoConnect);
							intent.putExtra("checkcardAfterConnection", checkCard);

							intent.putExtra("cardfirstsixdigit", mTxtFirstSixDigits.getText().toString().trim());
							intent.putExtra("emirate", mEmiRate);
							intent.putExtra("emiamount", mEmiAmount);
							intent.putExtra("emiperiod", mEmiPeriod);
							intent.putExtra("emibankcode", mEmiBankCode);

							startActivityForResult(intent, 2002);

							dialog.dismiss();

						}
					});

					dialog.show();

				}
				else {


				}

			}
		}
	}


	public class EmiAdapter extends BaseAdapter {
		ArrayList<EMIData> listData = null;
		Context context;

		public EmiAdapter(Context context, ArrayList<EMIData> listData) {
			this.listData = listData;
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}


		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.emi_list_row, null);
			}

			EMIData emiData = listData.get(position);

			TextView txt_month = (TextView) convertView.findViewById(R.id.emi_list_txt_month);
			TextView txt_month_intallment = (TextView) convertView.findViewById(R.id.emi_list_txt_monthly_installment);
			TextView txt_rate = (TextView) convertView.findViewById(R.id.emi_list_txt_rate);
			TextView txt_cashbank_amt = (TextView) convertView.findViewById(R.id.emi_list_txt_cashbackamt);
			TextView txt_cashback_rate = (TextView) convertView.findViewById(R.id.emi_list_txt_cashbackrate);
			final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.emi_list_checkbox);

			if(selectedPosition == position)
			{
				checkBox.setChecked(true);
			}
			else{
				checkBox.setChecked(false);
			}

			if (listData.get(position) != null){
				txt_month.setText(emiData.emiTenure+" months");
				txt_rate.setText(emiData.emiRate+" %");
				txt_month_intallment.setText(ApplicationData.mCurrency +" "+emiData.emiAmt);
				txt_cashback_rate.setText(emiData.cashBackRate+" %");
				txt_cashbank_amt.setText(ApplicationData.mCurrency +" "+emiData.cashBackAmt);
			}

			return convertView;
		}
	}


	public boolean emailCheck(String str) {

		if (!Constants.isValidEmail(str)) {
			Constants.showDialog(PaymentActivityTypeIntegration.this, "pay", "invalid email address");

			return false;
		} else {
			return true;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 2002)
		{
			if(resultCode == RESULT_OK)
			{

				boolean status = data.getBooleanExtra("status", false);

				Intent intent = data;

				if(status) {

//					intent.setClass(PaymentActivityTypeIntegration.this, CreditSaleSignatureActivity.class);
				}
				else{

					intent.setClass(PaymentActivityTypeIntegration.this, CreditSaleDeclineActivity.class);
				}

				startActivity(data);
				finish();
			}
		}
	}


	/**
	 * calculates the total amount for the transaction
	 * fields
	 * @param
	 * @return
	 */
	public void calculateTotalAmt()
	{
		//remove the decimal since in j2me it does not exists

		String stAmount = mTxtCreditAmount.getText().toString();
		String stTipAmount = mTxtCreditTipAmount.getText().toString();
		String stSaleCash = mTxtSaleCashAmount.getText().toString();

		stAmount = removeChar(stAmount, ',');
		stTipAmount = removeChar(stTipAmount, ',');
		stSaleCash = removeChar(stSaleCash, ',');


		if(stAmount.length() == 0 ){
			stAmount = "0.0";
		}

		if(stTipAmount.length() == 0){
			stTipAmount = "0.0";
		}

		if(stSaleCash.length() == 0 ){
			stSaleCash = "0.0";
		}

		double baseAmount = Double.parseDouble(stAmount);
		double tipAmount;
		double saleCashAmount;

		String totalAmount;

		tipAmount = Double.parseDouble(stTipAmount);
		saleCashAmount = Double.parseDouble(stSaleCash);
		totalAmount = String.format("%.2f", baseAmount + tipAmount + saleCashAmount);

		String text = totalAmount.toString();
		int ilen = text.length();

		if (ilen > 6) {
			text = text.substring(0, ilen - 6) + ","+ text.substring(ilen - 6, ilen);
		}

		ilen = text.length();

		if (ilen > 9) {
			text = text.substring(0, ilen - 9) + ","+ text.substring(ilen - 9, ilen);
		}

		mTotalAmount = text;
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