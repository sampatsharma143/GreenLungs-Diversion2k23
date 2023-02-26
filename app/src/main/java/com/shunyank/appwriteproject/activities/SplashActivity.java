package com.shunyank.appwriteproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.shunyank.appwriteproject.databinding.ActivitySplashBinding;
import com.shunyank.appwriteproject.network.AppWriteHelper;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: 25/02/23 check first time user
         pref = getSharedPreferences("pref",MODE_PRIVATE);
        // Todo remove this line
//        pref.edit().putBoolean("welcomed",false).apply();

        new CountDownTimer(1500,1000){

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                checkAccount();
            }
        }.start();


    }
    void checkAccount(){
        if (!pref.getBoolean("welcomed",false)){
            startActivity(new Intent(this,OnBoardingActivity.class));
            pref.edit().putBoolean("welcomed",true).apply();
            finish();
        }
        else {
            // TODO: 25/02/23 check login status
            Account account = new Account(AppWriteHelper.getClient());
            try {
                account.get(new Continuation<io.appwrite.models.Account>() {
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object o) {
                        if(o instanceof Result.Failure){
                            // open Login screen
                            ((Result.Failure) o).exception.printStackTrace();
                            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                            finish();
                        }else {
                            // set user details
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                            finish();
                            // open MainActivity
                        }
                    }
                });
            } catch (AppwriteException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }
}