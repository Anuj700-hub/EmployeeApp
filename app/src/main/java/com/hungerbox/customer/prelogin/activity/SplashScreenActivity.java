package com.hungerbox.customer.prelogin.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.hungerbox.customer.R;

public class SplashScreenActivity extends ParentActivity {

    private static int SPLASH_TIME_OUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }
}
