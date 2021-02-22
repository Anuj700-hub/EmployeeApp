package com.hungerbox.customer.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hungerbox.customer.R;
import com.hungerbox.customer.util.ApplicationConstants;

import static android.content.Context.WINDOW_SERVICE;


/**
 * Created by ranjeet on 02,January,2019
 */
public class HbCardView extends CardView {

    String viewType = ApplicationConstants.BOOKMARK_VIEW_DIVIDED;

    public HbCardView(Context context) {
        super(context);
    }

    public HbCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getViewType(context, attrs);
    }

    public HbCardView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getViewType(context, attrs);
    }

    private void getViewType(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HbCardView);
        viewType = typedArray.getString(R.styleable.HbCardView_viewType);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        WindowManager wm = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        if (viewType.equals(ApplicationConstants.BOOKMARK_VIEW_DIVIDED))
            heightSpec = MeasureSpec.makeMeasureSpec((height - 260) / 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthSpec, heightSpec);
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

}
