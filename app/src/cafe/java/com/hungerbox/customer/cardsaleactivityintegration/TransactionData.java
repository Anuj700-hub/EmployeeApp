package com.hungerbox.customer.cardsaleactivityintegration;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TransactionData implements Parcelable, Serializable{

	// Card sale trx

	public String mBaseAmount = "0.00";
	public String mTipAmount = "0.00";
	public String mSaleCashAmount = "0.00";
	public String mTotAmount = "0.00";
	public String mReceipt="";
	public String mPhoneNo="";
	public String mEmail="";
	public String mNotes="";
	public String mAmexSecurityCode = "";

	public String mEmiPeriod = "";
	public String mEmiBankCode = "";

	public  String mConvenienceAmount="0.00";
	public  String mServiceTaxamount = "0.00";

	public String mCardFirstSixDigit="";

	// Refund trx

	public String mDate = "";
	public String mSelectedDate = "";
	public String mLast4Digits = "";
	public String mStandId = "";
	public String mVoucherNo = "";
	public String mAuthCode = "";
	public String mRRNo = "";
	public String mInvoiceNo = "";
	public String mPrismInvoiceNo = "";

	//bank trx
	public String mChequeNo = "";

	public String mExtraNote1 = "";
	public String mExtraNote2 = "";
	public String mExtraNote3 = "";
	public String mExtraNote4 = "";
	public String mExtraNote5 = "";
	public String mExtraNote6 = "";
	public String mExtraNote7 = "";
	public String mExtraNote8 = "";
	public String mExtraNote9 = "";
	public String mExtraNote10 = "";



	public static final Creator<TransactionData> CREATOR = new Creator<TransactionData>() {
		public TransactionData createFromParcel(Parcel source) {

			TransactionData transactionData = new TransactionData();

			transactionData.mBaseAmount = source.readString();
			transactionData.mTipAmount = source.readString();
			transactionData.mSaleCashAmount = source.readString();
			transactionData.mTotAmount = source.readString();

			transactionData.mReceipt = source.readString();
			transactionData.mPhoneNo = source.readString();
			transactionData.mEmail = source.readString();
			transactionData.mNotes = source.readString();

			transactionData.mAmexSecurityCode = source.readString();

			transactionData.mConvenienceAmount = source.readString();
			transactionData.mServiceTaxamount = source.readString();

			transactionData.mDate = source.readString();
			transactionData.mSelectedDate = source.readString();
			transactionData.mLast4Digits = source.readString();
			transactionData.mStandId = source.readString();
			transactionData.mVoucherNo = source.readString();
			transactionData.mAuthCode = source.readString();
			transactionData.mRRNo = source.readString();
			transactionData.mInvoiceNo = source.readString();
			transactionData.mPrismInvoiceNo = source.readString();
			transactionData.mChequeNo = source.readString();

			transactionData.mExtraNote1 = source.readString();
			transactionData.mExtraNote2 = source.readString();
			transactionData.mExtraNote3 = source.readString();
			transactionData.mExtraNote4 = source.readString();
			transactionData.mExtraNote5 = source.readString();
			transactionData.mExtraNote6 = source.readString();
			transactionData.mExtraNote7 = source.readString();
			transactionData.mExtraNote8 = source.readString();
			transactionData.mExtraNote9 = source.readString();
			transactionData.mExtraNote10 = source.readString();

			return transactionData;

		}
		public TransactionData[] newArray(int size) {

			return new TransactionData[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel parcel, int flags) {

		parcel.writeString(mBaseAmount);
		parcel.writeString(mTipAmount);
		parcel.writeString(mSaleCashAmount);
		parcel.writeString(mTotAmount);
		parcel.writeString(mReceipt);
		parcel.writeString(mPhoneNo);
		parcel.writeString(mEmail);
		parcel.writeString(mNotes);

		parcel.writeString(mAmexSecurityCode);
		parcel.writeString(mConvenienceAmount);
		parcel.writeString(mServiceTaxamount);

		parcel.writeString(mDate);
		parcel.writeString(mSelectedDate);
		parcel.writeString(mLast4Digits);
		parcel.writeString(mStandId);
		parcel.writeString(mVoucherNo);
		parcel.writeString(mAuthCode);
		parcel.writeString(mRRNo);
		parcel.writeString(mInvoiceNo);
		parcel.writeString(mPrismInvoiceNo);
		parcel.writeString(mChequeNo);

		parcel.writeString(mExtraNote1);
		parcel.writeString(mExtraNote2);
		parcel.writeString(mExtraNote3);
		parcel.writeString(mExtraNote4);
		parcel.writeString(mExtraNote5);
		parcel.writeString(mExtraNote6);
		parcel.writeString(mExtraNote7);
		parcel.writeString(mExtraNote8);
		parcel.writeString(mExtraNote9);
		parcel.writeString(mExtraNote10);


	}
}
