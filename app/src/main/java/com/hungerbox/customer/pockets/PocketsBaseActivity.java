package com.hungerbox.customer.pockets;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hungerbox.customer.R;
import com.hungerbox.customer.model.ErrorResponse;
import com.hungerbox.customer.model.User;
import com.hungerbox.customer.model.UserReposne;
import com.hungerbox.customer.network.ContextErrorListener;
import com.hungerbox.customer.network.ResponseListener;
import com.hungerbox.customer.network.SimpleHttpAgent;
import com.hungerbox.customer.network.UrlConstant;

public class PocketsBaseActivity extends AppCompatActivity {

    /**
     * This is permisssion request code for pockets
     **/
    private static String[] PERMISSIONS_PHONE = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //    String mobileNo = "9620377486";
    String mobileNo = "";
    User user;
    boolean isShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pockets_base);

        ImageView ivBack = findViewById(R.id.iv_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isShown)
            finish();
        isShown = true;
    }


//    public void initSDKInternal() {
////		System.out.println("this variable"+this.toString());
//        AppUtils.HbLog("pock_pee", "init");
//        PocketsSDK.initSDK(this, mobileNo, UrlConstant.POCKETS_SDK_CLIENT_ID, UrlConstant.POCKETS_SDK_MERCHANT_ID, UrlConstant.POCKETS_SDK_MERCHANT_NO,
//                new PocketsSDKInitializationListner() {
//                    @Override
//                    public void initFailed(int i) {
//                        showPocketInitializationFailed("Unable to initialize pockets");
//                    }
//
//                    @Override
//                    public void initSuccess() {
//                        goToDashBoard();
//                    }
//                },
//                new PocketsProgressUpdateListener() {
//                    @Override
//                    public void startProgress(Context context, String s) {
//
//                    }
//
//                    @Override
//                    public void stopProgress(Context context) {
//
//                    }
//                },
//                new AnalyticsListener() {
//                    @Override
//                    public void onEvent(Context context, String s, String s1, Map map) {
//
//                    }
//
//                    @Override
//                    public void onScreenView(Context context, String s) {
//
//                    }
//                });
////		Toast.makeText(this, "Clicked Init SDK",  Toast.LENGTH_LONG).show();
//
//    }


//    public void onBoardInternal(PocketsUserProfile pocketsUserProfile) {
//
////        PocketsSDK.onBoardUser(this, UrlConstant.POCKETS_SDK_CLIENT_ID, mobileNo, UrlConstant.POCKETS_SDK_MERCHANT_ID, UrlConstant.POCKETS_SDK_MERCHANT_NO, pocketsUserProfile, new PocketsUserOnBoardRequestListner() {
////            @Override
////            public void userOnBoardCanceled() {
////                showPocketInitializationFailed("Pockets Registration is cancelled");
////            }
////
////            @Override
////            public void userOnBoardFailed(int i) {
////                    showPocketInitializationFailed("Unable to register you on pockets");
////            }
////
////            @Override
////            public void userOnBoardSuccessful() {
////
////            }
////        });
//
////	PocketsUserProfile pocketsUserProfile = new PocketsUserProfile(((EditText)findViewById(R.id.et_fName)).getText().toString(),((EditText)findViewById(R.id.et_lName)).getText().toString(),((EditText)findViewById(R.id.et_email)).getText().toString(),((EditText)findViewById(R.id.et_gender)).getText().toString() , ((EditText)findViewById(R.id.et_dob)).getText().toString());
//        PocketsSDK.onBoardUser(this, PocketsSDK.sessionKey, mobileNo, UrlConstant.POCKETS_SDK_MERCHANT_ID, UrlConstant.POCKETS_SDK_MERCHANT_NO, pocketsUserProfile, new PocketsUserOnBoardRequestListner() {
//            @Override
//            public void userOnBoardSuccessful() {
////                goToDashBoard();
//
//            }
//
//            @Override
//            public void userOnBoardFailed(int i) {
//                showPocketInitializationFailed("");
//            }
//
//            @Override
//            public void userOnBoardCanceled() {
//
//
//            }
//        });
//
//    }

//    public void onBoardUser(){
//        String first_name="", last_name = "";
//        String[] names = user.getName().split(" ");
//        first_name = names[0];
//        if(names.length>1){
//            last_name = names[0];
//        }
//        PocketsUserProfile pocketsUserProfile = new PocketsUserProfile(first_name, last_name, user.getEmail(),"m" , "");
//        onBoardInternal(pocketsUserProfile);
//    }

//    private void showPocketInitializationFailed(String message) {
//        GenericPopUpFragment errorFragment = GenericPopUpFragment.newInstance(message, "RETRY", new GenericPopUpFragment.OnFragmentInteractionListener() {
//            @Override
//            public void onPositiveInteraction() {
//                initSDKInternal();
//            }
//
//            @Override
//            public void onNegativeInteraction() {
//                finish();
//            }
//        });
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .add(errorFragment, "error")
//                .commitAllowingStateLoss();
//    }

//    private void goToDashBoard() {
//        PocketsSDK.showUserDashboard(getApplicationContext(),
//                UrlConstant.POCKETS_SDK_CLIENT_ID,
//                mobileNo,
//                UrlConstant.POCKETS_SDK_MERCHANT_ID,
//                UrlConstant.POCKETS_SDK_MERCHANT_NO,
//                new PocketsShowDashboardListner() {
//                    @Override
//                    public void ShowDashboardCanceled() {
//                        AppUtils.HbLog("PI_POCK", "dashboard canceled");
//                        finish();
//
//                    }
//
//                    @Override
//                    public void ShowDashboardFailed(int i) {
//                        //TODO add failure popup
//                        AppUtils.HbLog("PI_POCK", "dashboard failed reason="+i);
//                        if(i== PocketsSDKConstants.POCKETSSDK_DEVAUTHFAILED){
//                            onBoardUser();
//                        }else{
//                            showPocketInitializationFailed("Unable to load your pockets dashboard");
//                        }
//
//                    }
//
//                    @Override
//                    public void ShowDashboardSuccessful() {
//                        AppUtils.HbLog("PI_POCK", "dashboard success");
//                        finish();
//                    }
//                });
//
//    }

    public void getUserDetails() {
        String url = UrlConstant.USER_DETAIL;

        SimpleHttpAgent<UserReposne> userSimpleHttpAgent = new SimpleHttpAgent<UserReposne>(
                this,
                url,
                new ResponseListener<UserReposne>() {
                    @Override
                    public void response(UserReposne responseObject) {
                        mobileNo = responseObject.getUser().getPhoneNumber();
                        user = responseObject.getUser();
                        checkRequiredPermissions();
                    }
                },
                new ContextErrorListener() {
                    @Override
                    public void handleError(int errorCode, String error, ErrorResponse errorResponse) {
//                        showPocketInitializationFailed("initialization failed");
                    }
                },
                UserReposne.class
        );

        userSimpleHttpAgent.get();
    }

    private void checkRequiredPermissions() {
        int hasPhoneState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);

        int smsPerm = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);

        int smsRecvPerm = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int storagePerm = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (hasPhoneState != PackageManager.PERMISSION_GRANTED ||
                smsPerm != PackageManager.PERMISSION_GRANTED ||
                smsRecvPerm != PackageManager.PERMISSION_GRANTED ||
                storagePerm != PackageManager.PERMISSION_GRANTED) {
            requestPhoneStatePermissions(this);
        } else {
//            initSDKInternal();
        }

    }


    private void requestPhoneStatePermissions(final Context context) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.READ_PHONE_STATE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.

            // Display a SnackBar with an explanation and a button to trigger the request.
            ActivityCompat
                    .requestPermissions((Activity) context, PERMISSIONS_PHONE,
                            1002);


        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_PHONE, 1002);
        }
        // END_INCLUDE(contacts_permission_request)
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1002) {
//            initSDKInternal();
        }
    }


}
