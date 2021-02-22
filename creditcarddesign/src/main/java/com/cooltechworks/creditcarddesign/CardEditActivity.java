package com.cooltechworks.creditcarddesign;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashSet;

import static com.cooltechworks.creditcarddesign.CreditCardUtils.CARD_NUMBER_FORMAT;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.CARD_NUMBER_FORMAT_AMEX;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_BRAND;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_CVV;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_EXPIRY;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_HOLDER_NAME;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_CARD_NUMBER;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_ENTRY_START_PAGE;
import static com.cooltechworks.creditcarddesign.CreditCardUtils.EXTRA_TOKEN;


public class CardEditActivity extends AppCompatActivity {


    public static String mCardBrand = "";
    private CreditCardView mCreditCardView;
    private String mCardNumber;
    private String mCVV;
    private String mCardHolderName = "";
    private String mExpiry;
    private int mMaxCVV;
    private int mStartPage = 0;
    private String mToken = "";
    boolean isFront;
    private HashSet<String> methods;
    private boolean fromNavbar;
    EditText et_cardNo, et_cardName, et_card_expiry, et_card_cvv;
    Button bt_pay;
    private AppCompatImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_edit);
        et_cardNo = findViewById(R.id.card_number_field);
        et_cardName = findViewById(R.id.card_name);
        et_card_expiry = findViewById(R.id.card_expiry);
        et_card_cvv = findViewById(R.id.card_cvv);
        bt_pay = findViewById(R.id.pay_button);
        isFront = true;
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // pay
                onDoneTapped();
            }
        });
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setKeyboardVisibility(true);
        mCreditCardView = findViewById(R.id.credit_card_view);
        Bundle args = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        setOnfocusLnrs();
        setTextWatchers();
        checkParams(args);
    }

    private void setOnfocusLnrs(){
        et_cardName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if(!isFront){
                        mCreditCardView.showFront();
                        isFront = true;
                    }
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.theme_colorAccent));
                    ((TextView)findViewById(R.id.card_name_label)).setTextColor(colorStateList);
                }
                else{
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.default_text_color));
                    ((TextView)findViewById(R.id.card_name_label)).setTextColor(colorStateList);
                }
            }
        });
        et_card_cvv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if(isFront){
                        mCreditCardView.showBack();
                        isFront = false;
                    }
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.theme_colorAccent));
                    ((TextView)findViewById(R.id.card_cvv_label)).setTextColor(colorStateList);
                }
                else{
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.default_text_color));
                    ((TextView)findViewById(R.id.card_cvv_label)).setTextColor(colorStateList);
                }
            }
        });
        et_card_expiry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if(!isFront){
                        mCreditCardView.showFront();
                        isFront = true;
                    }
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.theme_colorAccent));
                    ((TextView)findViewById(R.id.card_expiry_label)).setTextColor(colorStateList);
                }
                else{
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.default_text_color));
                    ((TextView)findViewById(R.id.card_expiry_label)).setTextColor(colorStateList);
                }
            }
        });
        et_cardNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if(!isFront){
                        mCreditCardView.showFront();
                        isFront = true;
                    }
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.theme_colorAccent));
                    ((TextView)findViewById(R.id.card_number_label)).setTextColor(colorStateList);
                }
                else{
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.default_text_color));
                    ((TextView)findViewById(R.id.card_number_label)).setTextColor(colorStateList);
                }
            }
        });
    }
    private void setTextWatchers(){
        et_cardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int cursorPosition = et_cardNo.getSelectionEnd();
                int previousLength = et_cardNo.getText().length();

                String cardNumber = CreditCardUtils.handleCardNumber(s.toString());
                int modifiedLength = cardNumber.length();

                et_cardNo.removeTextChangedListener(this);
                et_cardNo.setText(cardNumber);
                String rawCardNumber = cardNumber.replace(CreditCardUtils.SPACE_SEPERATOR, "");
                CreditCardUtils.CardType cardType = CreditCardUtils.selectCardType(rawCardNumber);
                int maxLengthWithSpaces = ((cardType == CreditCardUtils.CardType.AMEX_CARD) ? CARD_NUMBER_FORMAT_AMEX : CARD_NUMBER_FORMAT).length();
                et_cardNo.setSelection(cardNumber.length() > maxLengthWithSpaces ? maxLengthWithSpaces : cardNumber.length());
                et_cardNo.addTextChangedListener(this);

                if (modifiedLength <= previousLength && cursorPosition < modifiedLength) {
                    et_cardNo.setSelection(cursorPosition);
                }

                mCardNumber = cardNumber.replace(CreditCardUtils.SPACE_SEPERATOR, "");
                if (!(CardEditActivity.mCardBrand != null && CardEditActivity.mCardBrand.length() > 0) && CreditCardUtils.validateCreditCardNumber(mCardNumber)) {

                    mCreditCardView.setCardNumber(mCardNumber);
                    setMaxCVV(CardSelector.selectCard(mCardNumber).getCvvLength());
                }

                if (rawCardNumber.length() == CreditCardUtils.selectCardLength(cardType)) {
                    if (CreditCardUtils.validateCreditCardNumber(rawCardNumber)){
                        et_cardName.requestFocus();
                    }
                    else
                        et_cardNo.setError("Invalid Card Number");
                }
            }
        });
        et_card_expiry.addTextChangedListener(new TextWatcher() {
            boolean mValidateCard = true;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 2) {
                    String text = s.toString();
                    et_card_expiry.removeTextChangedListener(this);
                    et_card_expiry.setText(text);
                    et_card_expiry.setSelection(text.length());
                    et_card_expiry.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


                String text = s.toString().replace(CreditCardUtils.SLASH_SEPERATOR, "");

                String month, year = "";
                if (text.length() >= 2) {
                    month = text.substring(0, 2);

                    if (text.length() > 2) {
                        year = text.substring(2);
                    }

                    if (mValidateCard) {
                        int mm = Integer.parseInt(month);

                        if (mm <= 0 || mm >= 13) {
                            et_card_expiry.setError(getString(R.string.error_invalid_month));
                            return;
                        }

                        if (text.length() >= 4) {

                            int yy = Integer.parseInt(year);

                            final Calendar calendar = Calendar.getInstance();
                            int currentYear = calendar.get(Calendar.YEAR);
                            int currentMonth = calendar.get(Calendar.MONTH) + 1;

                            int millenium = (currentYear / 1000) * 1000;


                            if (yy + millenium < currentYear) {
                                et_card_expiry.setError(getString(R.string.error_card_expired));
                                return;
                            } else if (yy + millenium == currentYear && mm < currentMonth) {
                                et_card_expiry.setError(getString(R.string.error_card_expired));
                                return;
                            }
                        }
                    }

                } else {
                    month = text;
                }

                int previousLength = et_card_expiry.getText().length();
                int cursorPosition = et_card_expiry.getSelectionEnd();

                text = CreditCardUtils.handleExpiration(month, year);

                et_card_expiry.removeTextChangedListener(this);
                et_card_expiry.setText(text);
                et_card_expiry.setSelection(text.length());
                et_card_expiry.addTextChangedListener(this);

                int modifiedLength = text.length();

                if (modifiedLength <= previousLength && cursorPosition < modifiedLength) {
                    et_card_expiry.setSelection(cursorPosition);
                }

                mExpiry = text;
                mCreditCardView.setCardExpiry(text);

                if (text.length() == 5) {
                    et_card_cvv.requestFocus();
                }
            }
        });
        et_card_cvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                mCVV = text;
                mCreditCardView.setCVV(text);
                if (s.length() == mMaxCVV) {
                    if(!isFront){
                        mCreditCardView.showFront();
                        isFront = true;
                    }
                }
            }
        });

        et_cardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                mCardHolderName = text;
                mCreditCardView.setCardHolderName(text);
                if (s.length() == getResources().getInteger(R.integer.card_name_len)) {
                    et_card_expiry.requestFocus();
                }
            }
        });
    }

    public void setMaxCVV(int maxCVVLength) {
        if (et_card_cvv != null && (et_card_cvv.getText().toString().length() > maxCVVLength)) {
            et_card_cvv.setText(et_card_cvv.getText().toString().substring(0, maxCVVLength));
        }

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(maxCVVLength);
        et_card_cvv.setFilters(FilterArray);
        mMaxCVV = maxCVVLength;
        String hintCVV = "";
        for (int i = 0; i < maxCVVLength; i++) {
            hintCVV += "X";
        }
        et_card_cvv.setHint(hintCVV);
    }

    private void checkParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        mCardHolderName = getString(R.string.card_holder_name_default);
        mCVV = bundle.getString(EXTRA_CARD_CVV);
        mExpiry = bundle.getString(EXTRA_CARD_EXPIRY);
        mCardNumber = bundle.getString(EXTRA_CARD_NUMBER);
        mStartPage = bundle.getInt(EXTRA_ENTRY_START_PAGE);
        mToken = bundle.getString(EXTRA_TOKEN);
        methods = (HashSet<String>) bundle.getSerializable("methods");
        fromNavbar = bundle.getBoolean("fromNavbar");

        if(mStartPage == 3){
            findViewById(R.id.view_card_expiry).setVisibility(View.GONE);
            findViewById(R.id.view_card_name).setVisibility(View.GONE);
            findViewById(R.id.view_card_number).setVisibility(View.GONE);
            mCardHolderName = bundle.getString(EXTRA_CARD_HOLDER_NAME);
        }
        else{
            bt_pay.setText("Add Card");
        }
        mCardBrand = bundle.getString(EXTRA_CARD_BRAND);

        final int maxCvvLength = CardSelector.selectCard(mCardNumber).getCvvLength();
        if (mCVV != null && mCVV.length() > maxCvvLength) {
            mCVV = mCVV.substring(0, maxCvvLength);
        }

        mCreditCardView.setCVV(mCVV);
        mCreditCardView.setCardHolderName(mCardHolderName);
        mCreditCardView.setCardExpiry(mExpiry);
        mCreditCardView.setCardNumber(mCardNumber);

        mCreditCardView.post(new Runnable() {
            @Override
            public void run() {
                setMaxCVV(maxCvvLength);
            }
        });

        int cardSide = bundle.getInt(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_FRONT);
        if (cardSide == CreditCardUtils.CARD_SIDE_BACK) {
            mCreditCardView.showBack();
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_CARD_CVV, mCVV);
        outState.putString(EXTRA_CARD_HOLDER_NAME, mCardHolderName);
        outState.putString(EXTRA_CARD_EXPIRY, mExpiry);
        outState.putString(EXTRA_CARD_NUMBER, mCardNumber);
        outState.putString(EXTRA_TOKEN, mToken);
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        checkParams(inState);
    }

    private void onDoneTapped() {
        if (validateAllInputs()) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CARD_CVV, mCVV);
            intent.putExtra(EXTRA_CARD_HOLDER_NAME, mCardHolderName);
            intent.putExtra(EXTRA_CARD_EXPIRY, mExpiry);
            intent.putExtra(EXTRA_CARD_NUMBER, mCardNumber);
            intent.putExtra(EXTRA_TOKEN, mToken);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(CardEditActivity.this, "Please verify all your details.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateAllInputs() {
        if (mCVV != null && mExpiry != null && mCardHolderName != null && mCardNumber != null) {

            if(mCVV.length() != CardSelector.selectCard(mCardNumber).getCvvLength() ){
                Toast.makeText(CardEditActivity.this, "Incorrect CVV length", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (mExpiry.length() == 0 || mCardNumber.replaceAll(" ", "").length() != CreditCardUtils.selectCardLength(CreditCardUtils.selectCardType(mCardNumber))) {
                return false;
            } else {
                mCardNumber.replaceAll(" ", "");
                if (!mCardNumber.contains("XXXX")) {
                    if (CreditCardUtils.validateCreditCardNumber(mCardNumber)) {
                        return true;
                    } else {
                        Toast.makeText(CardEditActivity.this, "Invalid Card Number", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    // from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

            RelativeLayout parent = findViewById(R.id.parent);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) parent.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            parent.setLayoutParams(layoutParams);

        }
    }

    private void setKeyboardVisibility(boolean visible) {
        final EditText editText = findViewById(R.id.card_number_field);
        if (!visible) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }


}
