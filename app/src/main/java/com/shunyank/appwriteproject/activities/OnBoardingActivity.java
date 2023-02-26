package com.shunyank.appwriteproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.shunyank.appwriteproject.R;
import com.xcode.onboarding.OnBoarder;
import com.xcode.onboarding.OnBoardingPage;
import com.xcode.onboarding.OnFinishLastPage;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<OnBoardingPage> pages = new ArrayList<>();
        pages.add(new OnBoardingPage(R.drawable.onboarding1, getResources().getColor(R.color.main_color),Color.BLACK,"",""));
        pages.add(new OnBoardingPage(R.drawable.onboarfing2,getResources().getColor(R.color.main_color),Color.BLACK,"",""));
        pages.add(new OnBoardingPage(R.drawable.onboarding3,getResources().getColor(R.color.main_color),Color.BLACK,"",""));

        OnBoarder.startOnBoarding(this, pages, new OnFinishLastPage() {
            @Override
            public void onNext() {

                startActivity(new Intent(OnBoardingActivity.this,LoginActivity.class));
                finish();
                // this is called when user click finish button on last page.
            }
        });

    }
}