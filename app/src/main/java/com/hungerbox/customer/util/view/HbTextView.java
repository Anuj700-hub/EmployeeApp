package com.hungerbox.customer.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.FontCache;

/**
 * Created by manas on 9/11/17.
 */

public class HbTextView extends TextView {

    String fontStyle = "";

    public HbTextView(Context context) {
        super(context);

       // applyCustomFont(context);
    }

    public HbTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //initFontType(context,attrs);
        //applyCustomFont(context);
    }

    public HbTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //initFontType(context,attrs);
        //applyCustomFont(context);
    }
    private void initFontType(Context context ,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HbTextView);
        fontStyle = typedArray.getString(R.styleable.HbTextView_textFontType);
        typedArray.recycle();
    }

    private void applyCustomFont(Context context) {
        Typeface customFont;
        if(fontStyle!=null && fontStyle.equals(ApplicationConstants.FONT_MEDIUM)){
            customFont = FontCache.getTypeface("AvenirNext-Medium.ttf",context);
        }else{
            customFont = FontCache.getTypeface("avenir-next-regular.ttf", context);
        }
        setTypeface(customFont);
    }
}
