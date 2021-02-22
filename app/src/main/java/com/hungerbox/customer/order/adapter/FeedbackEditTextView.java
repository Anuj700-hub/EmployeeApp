package com.hungerbox.customer.order.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.FeedbackReason;

public class FeedbackEditTextView extends RelativeLayout {
    private Context context;
    private EditText editText;
    private String itemKey;
    public FeedbackEditTextView(Context context) {
        super(context);
    }

    public FeedbackEditTextView(Context context, String itemKey) {
        super(context);
        inflate(context);
        this.itemKey = itemKey;
        editText.setGravity(Gravity.TOP);
    }

    private void inflate(Context context){
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.feedback_edittext, this, true);
        this.editText = view.findViewById(R.id.feedbackTextView);
    }

    public void setHint(String reason){
        editText.setHint(reason);
    }

    public EditText getEditText(){
        return this.editText;
    }

    public void setEnabled(boolean flag){
        editText.setEnabled(true);
    }

    public void setTag(FeedbackReason reason){
        editText.setTag(reason);
    }

    public void setText(String reason){
        editText.setText(reason);
    }

    public void setBackgroundResource(int drawable){
        editText.setBackgroundResource(drawable);
    }

    public void setPadding(int left, int top, int right, int bottom){
        editText.setPadding(left, top, right, bottom);
    }

    public void setMinLines(int lines){
        editText.setMinLines(lines);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);

    }
}
