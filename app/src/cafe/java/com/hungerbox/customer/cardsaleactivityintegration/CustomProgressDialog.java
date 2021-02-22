package com.hungerbox.customer.cardsaleactivityintegration;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.hungerbox.customer.R;


public class CustomProgressDialog extends Dialog {

	private String Message = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progressdlg);

		TextView msg = (TextView) findViewById(R.id.progressdlg_TXT_title);
		msg.setText(Message);

	}

	public CustomProgressDialog(Context context, String msg) {
		super(context, R.style.styleCustDlg);
		Message = msg;
		this.setTitle("");
		this.setCancelable(false);

	}

}
