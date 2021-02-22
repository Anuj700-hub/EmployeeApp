package com.hungerbox.customer.util.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hungerbox.customer.R;


/**
 * Created by peeyush on 2/01/17.
 */
public class BarView extends View {

    int mComplete;
    int mColor;

    public BarView(Context context) {
        super(context);
        mComplete = 0;
        mColor = Color.DKGRAY;
    }

    public BarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.BarView,
                0, 0);
        mComplete = a.getInteger(R.styleable.BarView_complete, 0);
        mColor = a.getColor(R.styleable.BarView_bar_color, getResources().getColor(R.color.colorPrimary));
    }

    public void setComplete(int complete) {
        this.mComplete = complete;
        invalidate();
        requestLayout();
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PaintArc(canvas);
    }

    private void PaintArc(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        if (mComplete > 0)
            paint.setColor(mColor);
        else
            paint.setColor(getResources().getColor(R.color.colorPrimary));

        RectF oval3 = new RectF(10, 10, getWidth() - 10, getHeight() - 10);

        float val = (float) mComplete / 100;
//        canvas.drawArc(oval3, -90, -(360*val), false, paint);
        canvas.drawRect(10, 10, getWidth() - 10, getHeight() - 10, paint);
    }
}
