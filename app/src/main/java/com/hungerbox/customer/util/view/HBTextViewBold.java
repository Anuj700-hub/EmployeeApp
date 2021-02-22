package com.hungerbox.customer.util.view;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.hungerbox.customer.util.FontCache;

public class HBTextViewBold extends AppCompatTextView {

    public HBTextViewBold(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public HBTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public HBTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("avenir-next-bold.ttf", context);
        setTypeface(customFont);
    }
}