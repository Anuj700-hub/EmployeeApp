package com.hungerbox.customer.util.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by ranjeet on 24,January,2019
 */
public class HbPaymentCardView extends CardView {

    String cardType = "";

    public HbPaymentCardView(@NonNull Context context) {
        super(context);
    }

    public HbPaymentCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //setCardType(context,attrs);
    }

    public HbPaymentCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setCardType(context,attrs);
    }

//    private void setCardType(Context context,AttributeSet attrs){
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HbPaymentCardView);
//        cardType = typedArray.getString(R.styleable.HbPaymentCardView_cardType);
//        typedArray.recycle();
//    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        WindowManager wm = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        heightSpec = MeasureSpec.makeMeasureSpec((height - 260) / 3, MeasureSpec.AT_MOST);

        super.onMeasure(widthSpec, heightSpec);
    }
}
