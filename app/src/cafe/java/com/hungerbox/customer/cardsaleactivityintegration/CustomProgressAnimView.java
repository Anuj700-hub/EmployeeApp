package com.hungerbox.customer.cardsaleactivityintegration;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.hungerbox.customer.R;


/**
 * Created by sivakumarboddu on 7/7/2015.
 */
public class CustomProgressAnimView extends LinearLayout
{
    boolean playAnimation = false;
    public CustomProgressAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
       /* LayoutInflater inflater = (LayoutInflater)   context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View child = inflater.inflate(R.layout.creditsale_progress_animatin, this);*/

        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.creditsale_progress_animatin, null, false);

        initView(inflatedLayout);
        this.addView(inflatedLayout);
    }

    View view_step1, view_step2, view_step3;

    private void initView(View v)
    {
            view_step1 = v.findViewById(R.id.prog_step1);
            view_step2 = v.findViewById(R.id.prog_step2);
            view_step3 = v.findViewById(R.id.prog_step3);
    }

    public void startAnimation()
    {
        playAnimation = true;
        ProgressAnimThread progressAnimThread = new ProgressAnimThread();
        progressAnimThread.start();
    }

    public void stopAnimation()
    {
        playAnimation = false;

    }

    private class ProgressAnimThread extends Thread
    {
        @Override
        public void run() {
            try{
                while (playAnimation)
                {
                    Thread.sleep(500);
                    progressAnimThreadHandler.sendEmptyMessage(0);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    int step = 0;
    private Handler progressAnimThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (step){
                case 0:
                    view_step1.setBackgroundResource(R.drawable.roundcircleblue);
                    view_step2.setBackgroundResource(R.drawable.roundcirclelightgrey);
                    view_step3.setBackgroundResource(R.drawable.roundcirclelightgrey);
                    break;
                case 1:
                    view_step1.setBackgroundResource(R.drawable.roundcirclelightgrey);
                    view_step2.setBackgroundResource(R.drawable.roundcircleblue);
                    view_step3.setBackgroundResource(R.drawable.roundcirclelightgrey);
                    break;
                case 2:
                    view_step1.setBackgroundResource(R.drawable.roundcirclelightgrey);
                    view_step2.setBackgroundResource(R.drawable.roundcirclelightgrey);
                    view_step3.setBackgroundResource(R.drawable.roundcircleblue);
                    break;
            }

            if(step != 2)
                step = step + 1;
            else
                step = 0;
        }
    };
}
