package com.hungerbox.customer.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.UserHealth;
import com.hungerbox.customer.model.UserHealthResponse;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.network.VolleyRequestFactory;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

public class HealthHomeActivity extends ParentActivity {


    String CANCELLABLE = "cancellable";
    private ImageView ivBack;
    private Button btnContinue;
    private View sflHealth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_home);
        ivBack = findViewById(R.id.iv_back);
        sflHealth = findViewById(R.id.sfl_health);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VolleyRequestFactory.INSTANCE.getMRequestQueue().cancelAll(CANCELLABLE);
                finish();
            }
        });
        getEmployeeHealth();
    }

    private void getEmployeeHealth() {
        String url = UrlConstant.USER_HEALTH;
        SimpleHttpAgent<UserHealthResponse> userHealthAgent = new SimpleHttpAgent<UserHealthResponse>(HealthHomeActivity.this, url, new ResponseListener<UserHealthResponse>() {
            @Override
            public void response(UserHealthResponse responseObject) {

                if (responseObject != null)
                    navigateToUserHealthDashbord(responseObject.getUserHealth());
                else
                    navigateToUserHealthDetails();
            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                String msg;
                if (errorResponse == null || errorResponse.message == null) {
                    msg = "Some error occured.";
                } else {
                    msg = errorResponse.message;
                }
                GenericPopUpFragment healthErrorDialog = GenericPopUpFragment.newInstance(msg, "Retry", new GenericPopUpFragment.OnFragmentInteractionListener() {
                    @Override
                    public void onPositiveInteraction() {
                        getEmployeeHealth();
                    }

                    @Override
                    public void onNegativeInteraction() {
                        finish();
                    }
                });
                healthErrorDialog.setCancelable(false);
                healthErrorDialog.show(getSupportFragmentManager(), "health_error");
            }

        }, UserHealthResponse.class);

        userHealthAgent.get(CANCELLABLE);

    }

    private void navigateToUserHealthDashbord(UserHealth userHealth) {
        Intent intent = new Intent(HealthHomeActivity.this, HealthDashboardActivity.class);
        if (userHealth != null && userHealth.deviceLinked) {
            intent.putExtra("linked", true);
        } else {
            intent = new Intent(HealthHomeActivity.this, ConnectDeviceActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    private void navigateToUserHealthDetails() {

        Intent intent = new Intent(HealthHomeActivity.this, HealthOnBoarding.class);
        intent.putExtra("isRegister", true);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
