package com.hungerbox.customer.navmenu.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.PaymentStatus;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.ApplicationConstants;

public class SuccesFailActivity extends ParentActivity {

    TextView tvStatus, tvPaymentStatusResponse;
    Button btContinue;
    boolean orderAfterRecharge;
    LottieAnimationView ivStatus1, ivStatus2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_succes_fail);
        tvStatus = findViewById(R.id.tv_status);
        ivStatus1 = findViewById(R.id.iv_status1);
        ivStatus2 = findViewById(R.id.iv_status2);
        btContinue = findViewById(R.id.bt_continue);
        tvPaymentStatusResponse = findViewById(R.id.tv_payment_response_text);

        PaymentStatus paymentStatus = (PaymentStatus) getIntent().getSerializableExtra(ApplicationConstants.PAYMENT_STATUS);
        orderAfterRecharge = getIntent().getBooleanExtra(ApplicationConstants.ORDER_AFTER_RECHARGE, false);

        if (paymentStatus.getStatus().equalsIgnoreCase(ApplicationConstants.PAYEMT_SUCCESS)) {
            ivStatus1.setVisibility(View.VISIBLE);
            tvStatus.setText("Your recharge is successful");
            ivStatus1.playAnimation();
            tvPaymentStatusResponse.setVisibility(View.GONE);
        } else if (paymentStatus.getStatus().equalsIgnoreCase(ApplicationConstants.PAYMENT_PENDING)) {
            ivStatus2.setVisibility(View.VISIBLE);
            tvStatus.setText("Your recharge is Pending");
            ivStatus2.playAnimation();
            tvPaymentStatusResponse.setText("Your payment is still pending from payment gateway. We will update it as soon there is any status change");
        } else {
            ivStatus2.setVisibility(View.VISIBLE);
            tvPaymentStatusResponse.setVisibility(View.GONE);
            tvStatus.setText("Your recharge failed");
            ivStatus2.playAnimation();
            orderAfterRecharge = false;
        }

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
