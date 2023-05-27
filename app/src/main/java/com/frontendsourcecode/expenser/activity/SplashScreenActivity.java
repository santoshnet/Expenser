package com.frontendsourcecode.expenser.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.frontendsourcecode.expenser.R;
import com.frontendsourcecode.expenser.model.User;
import com.frontendsourcecode.expenser.util.localstorage.LocalStorage;

public class SplashScreenActivity extends AppCompatActivity {

    LocalStorage localStorage;
    String userString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_splash_screen);
       localStorage = new LocalStorage(getApplicationContext());
        userString = localStorage.getUserLogin();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userString!=null){
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }else {
                    Intent i = new Intent(getApplicationContext(), OnboardingScreenActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }


        }, 3000);
    }


}