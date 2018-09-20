package com.cse.cou.mobarak.bookshope;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SplashScreen extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SplashScreen.this.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                SplashScreen.this.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        },4000);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();


    }
}
