package com.hungerbox.customer.order.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.hungerbox.customer.R;

public class BottomNavigationItemView extends RelativeLayout {
    private Context context;
    private boolean isNew = false;
    private String itemKey = "";
    private ImageView logo;
    private ImageView newBadge;
    private TextView name;
    private TypedArray typedArray;
    private TextView badge;
    public BottomNavigationItemView(Context context) {
        super(context);
    }

    public BottomNavigationItemView(Context context,String itemKey) {
        super(context);
        inflate(context,null);
        this.itemKey = itemKey;
    }

    public BottomNavigationItemView(Context context,String itemKey,boolean isNew) {
        super(context);
        inflate(context,null);
        this.itemKey = itemKey;
        this.isNew = isNew;
        if (isNew){
            newBadge.setVisibility(VISIBLE);
        } else{
            newBadge.setVisibility(INVISIBLE);
        }
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context,attrs);
    }

    private void inflate(Context context, AttributeSet attrs){
        this.context = context;
        if (attrs!=null) {
            typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.ColorOptionsView, 0, 0);
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bottom_navigation_item_view, this, true);
        logo = findViewById(R.id.iv_logo);
        newBadge = findViewById(R.id.iv_new_badge);
        name = findViewById(R.id.tv_name);
        badge = findViewById(R.id.tv_badge);

    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public void setLogo(@DrawableRes int resId){
        if (logo!=null){
            logo.setImageResource(resId);
        }

    }

    public void setName(String menuName){
        name.setText(menuName);
    }

    public ImageView getLogoImageView(){
        return logo;
    }

    public void setLogoParams(RelativeLayout.LayoutParams layoutParams){
        try {
          logo.setLayoutParams(layoutParams);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showBadge(String number){
        badge.setText(number);
        badge.setVisibility(VISIBLE);
    }

    public void hideBadge(){
        badge.setVisibility(GONE);
    }

}
