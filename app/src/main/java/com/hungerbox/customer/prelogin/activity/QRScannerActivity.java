package com.hungerbox.customer.prelogin.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hungerbox.customer.R;
import com.hungerbox.customer.util.AppUtils;
import com.hungerbox.customer.util.ApplicationConstants;
import com.hungerbox.customer.util.SharedPrefUtil;

import java.util.HashMap;

public class QRScannerActivity extends ParentActivity {

    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        source = getIntent().getStringExtra("source");
        startQRScanner();
    }

    private void startQRScanner() {

        new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result == null || result.getContents() == null) {
            AppUtils.showToast("Some error occured.", true, 0);
            finish();
        } else {
            if (source != null && source.equals("common")) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("data", result.getContents());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                HashMap<String, Long> qrCodeMap = AppUtils.getConfig(this).getRegistration_qr_hashs();
                String scannedQrCode = result.getContents();
                if (qrCodeMap.containsKey(scannedQrCode)) {
                    long locationId = qrCodeMap.get(scannedQrCode);
                    navigateToSignUpActivity(scannedQrCode, locationId);
                } else {
                    AppUtils.showToast("QR code Unauthorised.", true, 0);
                    finish();
                }
            }
        }
    }

    private void navigateToSignUpActivity(String qrCode, long locationId) {
        Intent intent = new Intent(this, SignUpActivityBasic.class);
        SharedPrefUtil.putLong(ApplicationConstants.LOCATION_ID, locationId);
        intent.putExtra(ApplicationConstants.QR_CODE, qrCode);
        intent.putExtra(ApplicationConstants.LOCATION_ID, locationId);
        startActivity(intent);
        finish();
    }

}
