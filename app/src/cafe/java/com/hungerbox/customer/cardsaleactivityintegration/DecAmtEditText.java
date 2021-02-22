package com.hungerbox.customer.cardsaleactivityintegration;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class DecAmtEditText extends androidx.appcompat.widget.AppCompatEditText {
    Context context = null;
    ApplicationData applicationData;

    public DecAmtEditText(Context context) {

        super(context);
        this.context = context;

        applicationData = ApplicationData.getApplicationDataSharedInstance();
        this.setTypeface(applicationData.font);

        init();
    }

    public DecAmtEditText(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        this.context = context;

        applicationData = ApplicationData.getApplicationDataSharedInstance();
        this.setTypeface(applicationData.font);

        init();
    }

    public DecAmtEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;

        applicationData = ApplicationData.getApplicationDataSharedInstance();
        this.setTypeface(applicationData.font);

        init();
    }

    void init() {

        // Set bounds of the Clear button so it will look ok
        // if the Close image is displayed and the user remove his finger from the button, clear it. Otherwise do nothing

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //this would enable the keyborad popup on ontouching the widget when the keyboard is not visible
                if (DecAmtEditText.this.isFocused() && DecAmtEditText.this.getText().length() != 0) {
                    DecAmtEditText.this.setSelection(DecAmtEditText.this.getText().length());
                    InputMethodManager imm = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
                    }
                    return true;
                }

                return false;
            }
        });

        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                DecAmtEditText.this.setSelection(DecAmtEditText.this.getText().length());

            }
        });

        // if text changes, take care of the button
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                DecAmtEditText.this.removeTextChangedListener(this);
                String stAmt =DecAmtEditText.this.getText().toString();
                String stOrg = removeChar(stAmt, '.');
                stOrg = removeChar(stOrg, ',');

                if(stOrg.indexOf("0") == 0)
                {
                    if(stOrg.equals("0"))
                        stOrg = stOrg.substring(0);
                    else if(stOrg.equals("00000"))
                        stOrg = stOrg.substring(2);
                    else
                        stOrg = stOrg.substring(1);
                }

                int ilen = stOrg.length();

                if (ilen == 2 || ilen ==1)
                {
                    stOrg = "0." + stOrg;
                }
                else if (ilen > 2)
                {
                    stOrg = stOrg.substring(0, ilen - 2) + "." + stOrg.substring(ilen - 2, ilen);
                }

                /*ilen = stOrg.length();
                if (ilen > 6)
                {
                    stOrg = stOrg.substring(0, ilen - 6)+","+ stOrg.substring(ilen - 6, ilen);
                }

                ilen = stOrg.length();
                if (ilen > 9)
                {
                    stOrg = stOrg.substring(0, ilen - 9)+","+ stOrg.substring(ilen - 9, ilen);
                }*/

                if (!stOrg.equals(stAmt))
                {
                    DecAmtEditText.this.setText(stOrg);
                }

                DecAmtEditText.this.setSelection(DecAmtEditText.this.getText().length());
                DecAmtEditText.this.addTextChangedListener(this);

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    public String removeChar(String s, char c) {

        String r = "";

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) r += s.charAt(i);
        }

        return r;
    }
}
