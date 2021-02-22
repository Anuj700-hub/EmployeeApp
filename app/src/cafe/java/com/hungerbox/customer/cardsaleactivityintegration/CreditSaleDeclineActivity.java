package com.hungerbox.customer.cardsaleactivityintegration;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.mswipetech.wisepad.sdk.data.CardSaleResponseData;
import com.mswipetech.wisepad.sdk.device.MSWisepadDeviceControllerResponseListener;

public class CreditSaleDeclineActivity extends Activity
{
	private final static String log_tab = "CreditSaleDeclineView=>";
	private String title = "";

	private boolean isEmvSwiper = false;

	String mStrCardNum = "";
	String mStrExpDate = "";
	String mStrAmt = "";
	String mDeclineErrorMsg = "";

	private ImageButton btnSubmit = null;

	ApplicationData applicationData = ApplicationData.getApplicationDataSharedInstance();

	/**
	 * Stores the card sale response data, which received from the sdk
	 */
	CardSaleResponseData cardSaleResponseData = null;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			return false;
		}else{
			return super.onKeyDown(keyCode, event);
		}

	}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.creditsale_declineview);

		//mDrawerRequired = false;

		if(title == null)
			title = "Card sale";

		try {

			title = (String)getIntent().getStringExtra("Title");
			cardSaleResponseData = (CardSaleResponseData) getIntent().getSerializableExtra("cardSaleResponseData");

			String errorno = "";
			try{
				errorno = ((cardSaleResponseData.getFO39Tag().length() > 0)? cardSaleResponseData.getFO39Tag(): cardSaleResponseData.getErrorNo() + "");
			}catch(Exception ex){
				errorno = cardSaleResponseData.getErrorNo() + "";
			}

			mDeclineErrorMsg = cardSaleResponseData.getResponseFailureReason() + " (" + cardSaleResponseData.getErrorCode() + "-" + errorno + ")";

			if(cardSaleResponseData.getCardSchemeResults() == MSWisepadDeviceControllerResponseListener.CARDSCHEMERRESULTS.ICC_CARD)
				isEmvSwiper = true;

		} catch (Exception e)
		{
			// TODO: handle exception
		}

		Resources resources = this.getResources();

		String ExpiryDate = cardSaleResponseData.getExpiryDate();
		String mLast4Digits = cardSaleResponseData.getLast4Digits();
		String mAmt = cardSaleResponseData.getTrxAmount();

     	if(isEmvSwiper) {

			mStrCardNum = "card num: XXXX XXXX XXXX " + mLast4Digits;
			mStrExpDate = "exp date: " + "xx/xx";
			mStrAmt = "amt: " + applicationData.mCurrency + " " + mAmt;

		}else{

			mStrCardNum = "card num: XXXX XXXX XXXX " + mLast4Digits;
			mStrExpDate = "exp date: " +"xx/xx";
			mStrAmt = "amt: " + applicationData.mCurrency + " " + mAmt  ;
		}

        initViews();

     }

	public void initViews()
	{

		((RelativeLayout)findViewById(R.id.top_bar_REL_content)).setBackgroundColor(getResources().getColor(R.color.red));

		((LinearLayout) findViewById(R.id.topbar_LNR_topbar_menu)).setVisibility(View.GONE);
		((LinearLayout) findViewById(R.id.topbar_LNR_topbar_cancel)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.topbar_LBL_heading)).setText(title);

		TextView errorMessage = (TextView)findViewById(R.id.creditsale_decline_LBL_declinederror);
		TextView cardData = (TextView)findViewById(R.id.creditsale_decline_TXT_redceiptdetails);

		//setting tht receipt details to textview
		cardData.setText(""+mStrCardNum+"\n"+mStrExpDate+" "+mStrAmt);

		errorMessage.setText(mDeclineErrorMsg);

		ImageButton btnSubmit = (ImageButton) findViewById(R.id.creditsale_decline_BTN_submitsignature);
		btnSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doneWithCreditSale();
			}
		});

	}


	public void doneWithCreditSale()
	{
		finish();
	}
}
