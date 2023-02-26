package com.shunyank.appwriteproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.shunyank.appwriteproject.App;
import com.shunyank.appwriteproject.models.BasicUser;

public class Pref {

    public static SharedPreferences sharedPreferences;

    public static SharedPreferences getPref() {
        if(sharedPreferences==null){
            sharedPreferences = App.getInstance().getSharedPreferences("pref", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static void saveBasicUser(BasicUser basicUser){
        getPref().edit().putString("BASIC_USER_PREF",new Gson().toJson(basicUser)).apply();
    }
    public static BasicUser getBasicUser(){
       String basicPref =  getPref().getString("BASIC_USER_PREF","");

       if(basicPref.isEmpty()){
           return null;
       }else {
           return new Gson().fromJson(basicPref,BasicUser.class);
       }
    }
}
