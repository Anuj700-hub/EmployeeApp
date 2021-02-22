package com.hungerbox.customer.order.adapter;

import android.content.Context;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hungerbox.customer.R;

public class Edittext_guest_ordering extends RelativeLayout {
    private Context context;
    private EditText editText;
    private String itemKey;

    public Edittext_guest_ordering(Context context) {
        super(context);
        inflate(context);
    }

    private void inflate(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.edittext_affilation_custom, this, true);
        this.editText = view.findViewById(R.id.editText_guest);
    }

    public void setHint(String reason) {
        editText.setHint(reason);
    }

    public void setEnabled(boolean flag) {
        editText.setEnabled(true);
    }


    public void setText(String reason) {
        editText.setText(reason);
    }

    public void setBackgroundResource(int drawable) {
        editText.setBackgroundResource(drawable);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        editText.setPadding(left, top, right, bottom);
    }

    public void setMinLines(int lines) {
        editText.setMinLines(lines);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }

    public EditText getEditText() {
        return editText;
    }

    public void setParams(){
        LayoutParams lparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); // Width , height

        editText.setLayoutParams(lparams);
    }
}