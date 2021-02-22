package com.hungerbox.customer.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.OrderProduct;
import com.hungerbox.customer.util.OrderUtil;

public class TrackerItemStatusView extends LinearLayout {
    View line1, line2;
    ImageView ivAccepted, ivReady, ivDelivered;

    public TrackerItemStatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(R.layout.order_tracker_item_status, this, true);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        ivAccepted = findViewById(R.id.iv_accepted);
        ivReady = findViewById(R.id.iv_ready);
        ivDelivered = findViewById(R.id.iv_delivered);
    }

    private void resetStatus(){

        ivAccepted.setImageResource(R.drawable.hollow_dark_grey);
        ivReady.setImageResource(R.drawable.hollow_dark_grey);
        ivDelivered.setImageResource(R.drawable.hollow_dark_grey);

        line1.setBackgroundColor(getResources().getColor(R.color.unselect_status_color));
        line2.setBackgroundColor(getResources().getColor(R.color.unselect_status_color));
    }

    private void reachAccepted(){
        ivAccepted.setImageResource(R.drawable.green_tick_round);
    }

    private void reachReady(){

        line1.setBackgroundColor(getResources().getColor(R.color.green));
        ivReady.setImageResource(R.drawable.green_tick_round);

    }

    private void reachDelivered(){
        line2.setBackgroundColor(getResources().getColor(R.color.green));
        ivDelivered.setImageResource(R.drawable.green_tick_round);
    }

    public void setItemStatus(OrderProduct item, boolean forSpaceBooking){

        resetStatus();

        String status = item.getStatus();

        if(forSpaceBooking){

            if(status.equalsIgnoreCase(OrderUtil.PRE_ORDER) ){
                reachAccepted();
            }
            if(status.equalsIgnoreCase(OrderUtil.NEW) || status.equalsIgnoreCase(OrderUtil.CONFIRMED) || status.equalsIgnoreCase(OrderUtil.PROCESSED)){
                reachAccepted();
                reachReady();
            }
            else if(status.equalsIgnoreCase(OrderUtil.DELIVERED) ){
                reachAccepted();
                reachReady();
                reachDelivered();
            }
        }
        else{
            if(status.equalsIgnoreCase(OrderUtil.CONFIRMED) ){
                reachAccepted();
            }
            else if(status.equalsIgnoreCase(OrderUtil.PROCESSED)){
                reachAccepted();
                reachReady();
            }
            else if(status.equalsIgnoreCase(OrderUtil.DELIVERED)){
                reachAccepted();
                reachReady();
                reachDelivered();
            }
            else if(status.equalsIgnoreCase(OrderUtil.REJECTED)){
                ivAccepted.setImageResource(R.drawable.red_cross);
            }
        }
    }
}
