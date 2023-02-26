package com.shunyank.appwriteproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shunyank.appwriteproject.databinding.ActivityLoginAcitivityBinding;
import com.shunyank.appwriteproject.models.BasicUser;
import com.shunyank.appwriteproject.network.AppWriteHelper;
import com.shunyank.appwriteproject.utils.Pref;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Account;
import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginAcitivityBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start login process

                Account account = new Account(AppWriteHelper.getClient());

                try {
                    account.createOAuth2Session(
                            LoginActivity.this,
                            "google",
                            new Continuation<Unit>() {
                                @NonNull
                                @Override
                                public CoroutineContext getContext() {
                                    return EmptyCoroutineContext.INSTANCE;
                                }

                                @Override
                                public void resumeWith(@NonNull Object o) {
                                    if(o instanceof Result.Failure){
                                        Log.e("error","login error");
                                    }else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                saveAccount();
                                            }
                                        });
                                    }
                                }
                            }
                    );
                } catch (AppwriteException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private void saveAccount() {

        try {
            getAccount();
        } catch (AppwriteException e) {

            Toast.makeText(this, "Something Went Wrong!\nCould not get account details", Toast.LENGTH_SHORT).show();

//            throw new RuntimeException(e);

        }

    }

    private void getAccount() throws AppwriteException {
        Account account = new Account(AppWriteHelper.getClient());
        account.get(new Continuation<io.appwrite.models.Account>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object o) {
                if(o instanceof Result.Failure){
                    Log.e("failed","Login");
                    Toast.makeText(LoginActivity.this, "Something Went Wrong!\nCould not get account details", Toast.LENGTH_SHORT).show();

                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            io.appwrite.models.Account account1 = (io.appwrite.models.Account) o;
                            String id = account1.getId();
                            String email = account1.getEmail();
                            String name = account1.getName();

                            Pref.saveBasicUser(new BasicUser(id,name,email,null));
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    Log.e("account result",new Gson().toJson(o));
                }
            }
        });
    }
}