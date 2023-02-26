package com.shunyank.appwriteproject.activities;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.gson.Gson;
import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.databinding.ActivityMainBinding;
import com.shunyank.appwriteproject.models.BasicUser;
import com.shunyank.appwriteproject.network.Constants;
import com.shunyank.appwriteproject.network.callbacks.DocumentFetchListener;
import com.shunyank.appwriteproject.network.utils.DatabaseUtils;
import com.shunyank.appwriteproject.utils.ActivityUtils;
import com.shunyank.appwriteproject.utils.Pref;

import java.util.ArrayList;
import java.util.List;

import io.appwrite.Query;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import kotlin.Result;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActivityUtils.setDarkStatus(this);

//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // check if user is new and not completed his profile




    }



    //complete profile dialog box



}