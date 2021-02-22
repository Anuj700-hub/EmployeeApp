package com.hungerbox.customer.util.view;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by peeyush on 27/6/16.
 */
public class CustomNonScrollingLayoutManager extends LinearLayoutManager {


    private boolean isScrollEnabled = false;

    public CustomNonScrollingLayoutManager(Context context) {
        super(context);
    }

    public CustomNonScrollingLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomNonScrollingLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
