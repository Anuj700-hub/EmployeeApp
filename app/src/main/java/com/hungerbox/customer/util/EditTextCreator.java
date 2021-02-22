package com.hungerbox.customer.util;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.textfield.TextInputLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderGuestInfoView;
import com.hungerbox.customer.order.adapter.Edittext_guest_ordering;

import java.util.ArrayList;

/**
 * Created by manas on 24/1/17.
 */

public class EditTextCreator {
    public ArrayList<EditText> createEdittext(int count, LinearLayout parentView, Context context) {
        ArrayList<EditText> editTextArrayList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            EditText editText = new EditText(context);
            editText.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            editText.setHint("Guest-" + (i + 1));
            editText.setTextColor(Color.BLACK);
            editText.setMaxLines(1);
            parentView.addView(editText);
            editTextArrayList.add(editText);
        }
        return editTextArrayList;
    }

    public ArrayList<OrderGuestInfoView> createGuestOrderViews
            (int count, LinearLayout parentView, Context context) {
        ArrayList<OrderGuestInfoView> editTextArrayList = new ArrayList<>();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < count; i++) {

            Edittext_guest_ordering AffiliateEditText = new Edittext_guest_ordering(context);
            AffiliateEditText.setMinLines(2);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            AffiliateEditText.setHint((i + 1) + ". Guest Affiliation / Company");
            AffiliateEditText.setParams();
            TextInputLayout guestAffiliationInputLayout = new TextInputLayout(context);

            guestAffiliationInputLayout.setLayoutParams(layoutParams);
            guestAffiliationInputLayout.setMinimumHeight(30);
            guestAffiliationInputLayout.setGravity(Gravity.CENTER_VERTICAL);
            guestAffiliationInputLayout.addView(AffiliateEditText);
            LinearLayout linearLayoutWrapper = new LinearLayout(context);
            LinearLayout.LayoutParams linearlayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            linearlayoutParams.setMargins(0, 10, 0, 10);
            linearLayoutWrapper.setLayoutParams(linearlayoutParams);
            linearLayoutWrapper.setOrientation(LinearLayout.VERTICAL);
            linearLayoutWrapper.setPadding(10, 10, 10, 20);


            linearLayoutWrapper.addView(guestAffiliationInputLayout);
            parentView.addView(linearLayoutWrapper);

            OrderGuestInfoView orderGuestInfoView = new OrderGuestInfoView();
            orderGuestInfoView.affiliation = AffiliateEditText.getEditText();
            editTextArrayList.add(orderGuestInfoView);

        }
        return editTextArrayList;
    }
}
