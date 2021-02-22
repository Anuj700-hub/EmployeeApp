package com.hungerbox.customer.health;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonSerializer;
import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.HealthDevice;
import com.hungerbox.customer.model.HealthDeviceAuth;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.NetworkUtil;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;
import com.hungerbox.customer.prelogin.activity.ParentActivity;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.EventUtil;
import com.hungerbox.customer.util.view.GenericPopUpFragment;

import java.util.HashMap;

public class ConnectDeviceActivity extends ParentActivity {

    private static final int RC_SIGN_IN = 7894;
    ImageView ivBack;
    Button btnSkip;
    CardView cvGoogle, cvApple, cvFitbit, cvMi;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean isLinked;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        ivBack = findViewById(R.id.iv_back);
        btnNext = findViewById(R.id.btn_next);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean afterDashboard = getIntent().getBooleanExtra("afterDashboard", false);
                if (!afterDashboard) {
                    Intent intent = new Intent(ConnectDeviceActivity.this, HealthDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
            }
        });

        cvApple = findViewById(R.id.cv_apple);
        cvFitbit = findViewById(R.id.cv_fitbit);
        cvGoogle = findViewById(R.id.cv_google);
        cvMi = findViewById(R.id.cv_mi);
        isLinked = getIntent().getBooleanExtra("isLinked", false);

        cvMi.setVisibility(View.GONE);
        cvApple.setVisibility(View.GONE);

        cvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLinked) {
                    GenericPopUpFragment linkPopUpFragment = GenericPopUpFragment.newInstance("Your current device will be delinked. \nAre you sure you want to continue?", "connect", new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            linkGoogleFit();
                        }

                        @Override
                        public void onNegativeInteraction() {

                        }
                    });
                    linkPopUpFragment.show(getSupportFragmentManager(), "link_popup");
                } else {
                    linkGoogleFit();
                }

            }
        });

        cvFitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLinked) {
                    GenericPopUpFragment linkPopUpFragment = GenericPopUpFragment.newInstance("Your current device will be delinked. \nAre you sure you want to continue?", "connect", new GenericPopUpFragment.OnFragmentInteractionListener() {
                        @Override
                        public void onPositiveInteraction() {
                            onFitBitConnectClicked();
                        }

                        @Override
                        public void onNegativeInteraction() {

                        }
                    });
                    linkPopUpFragment.show(getSupportFragmentManager(), "link_popup");
                } else {
                    onFitBitConnectClicked();
                }
            }
        });

        View.OnClickListener sampleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.showToast("Please Integrate with Google Fit", true, 2);
            }
        };
        cvMi.setOnClickListener(sampleClickListener);
        cvApple.setOnClickListener(sampleClickListener);
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();

        if (appLinkAction != null) {
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData.toString().contains("fitbit")) {
                String code = appLinkData.getQueryParameter("code");
                AppUtils.HbLog("callback_create", appLinkAction);
                AppUtils.HbLog("callback_create", code);
                onFitBitConnect(code);
            }
        }


    }

    private void linkGoogleFit() {
        String serverClientId = getString(R.string.server_client_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .requestServerAuthCode(serverClientId, true)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(ConnectDeviceActivity.this, gso);
        mGoogleSignInClient.signOut();
        getGoogleClientAuth();
    }

    private void onFitBitConnect(String code) {
        String url = UrlConstant.FITBIT_REGISTER_DEVICE;

        SimpleHttpAgent<Object> fitbitRegisterAgent = new SimpleHttpAgent<Object>(
                this,
                url,
                new ResponseListener<Object>() {
                    @Override
                    public void response(Object responseObject) {
                        AppUtils.showToast("Device Connected", true, 1);
                        Intent intent = new Intent(ConnectDeviceActivity.this, HealthDashboardActivity.class);
                        intent.putExtra("linked", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();

                        try {
                            Bundle bundle = new Bundle();
                            bundle.putString(EventUtil.HEALTH_DEVICE_NAME, "FitBit");
                            EventUtil.FbEventLog(ConnectDeviceActivity.this, EventUtil.HEALTH_DEVICE_CONNECT, bundle);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                Object.class
        );

        HashMap<String, String> body = new HashMap<>();
        body.put("code", code);
        body.put("device", "fitbit");
        fitbitRegisterAgent.post(body, new HashMap<String, JsonSerializer>());
    }

    private void onFitBitConnectClicked() {
        final String url = UrlConstant.FITBIT_REGISTER_DEVICE_AUTH;

        SimpleHttpAgent<HealthDeviceAuth> healthDeviceAuthSimpleHttpAgent = new SimpleHttpAgent<HealthDeviceAuth>(
                this,
                url,
                new ResponseListener<HealthDeviceAuth>() {
                    @Override
                    public void response(final HealthDeviceAuth responseObject) {
                        if (responseObject != null) {

                            if (responseObject.getLoginRequired()) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(responseObject.getAuthUrl()));
                                startActivity(i);
                            } else {
                                AppUtils.showToast("Device Already Connected", true, 2);
                            }
                        }
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {

                    }
                },
                HealthDeviceAuth.class
        );

        healthDeviceAuthSimpleHttpAgent.get();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        AppUtils.HbLog("callback", appLinkAction);
        AppUtils.HbLog("callback", appLinkData.toString());
    }

    private void getGoogleClientAuth() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    String authCode = account.getServerAuthCode();

                    sendAuthCode(authCode);
                } catch (ApiException e) {
                    if (!NetworkUtil.isInternetConnected(ConnectDeviceActivity.this)) {
                        AppUtils.showToast("Please check your network.", true, 0);
                    } else {
                        AppUtils.showToast(e.toString(), true, 0);
                    }
                }
            } else {
                if (!NetworkUtil.isInternetConnected(ConnectDeviceActivity.this)) {
                    AppUtils.showToast("Please check your network.", true, 0);
                } else {
                    AppUtils.showToast("Linking Cancelled", true, 0);
                }
            }
        }
    }

    private void sendAuthCode(String authCode) {
        String url = UrlConstant.HEALTH_DEVICE;

        SimpleHttpAgent<Object> authCodeAgent = new SimpleHttpAgent<Object>(ConnectDeviceActivity.this, url, new ResponseListener<Object>() {
            @Override
            public void response(Object responseObject) {

                AppUtils.showToast("Device Connected", true,    1);

                Intent intent = new Intent(ConnectDeviceActivity.this, HealthDashboardActivity.class);
                intent.putExtra("linked", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

                try {
                    Bundle bundle = new Bundle();
                    bundle.putString(EventUtil.HEALTH_DEVICE_NAME, "GoogleFit");
                    EventUtil.FbEventLog(ConnectDeviceActivity.this, EventUtil.HEALTH_DEVICE_CONNECT, bundle);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }

            }
        }, new ContextErrorListener() {
            @Override
            public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
                AppUtils.showToast(error, true,    0);

            }
        }, Object.class);

        HealthDevice healthDevice = new HealthDevice();
        healthDevice.setAuthCode(authCode);

        authCodeAgent.post(healthDevice, new HashMap<String, JsonSerializer>());

    }

}
