package com.shunyank.appwriteproject.network;

import static com.shunyank.appwriteproject.network.Constants.getAppUrl;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.shunyank.appwriteproject.App;


import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.SessionList;
import io.appwrite.services.Account;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class AppWriteHelper {
    private static Client client = null;

    public static Client getClient()
    {
        if (client == null) {

            client = new Client(App.getInstance().getBaseContext())
                    .setEndpoint(getAppUrl()) // Your API Endpoint
                    .setProject("63f74ab4bdf39b9fda3f")
                    .setSelfSigned(true);// Your project ID
        }
        return client;
    }
//
//    public static void getAccounts(Activity activity,AccountCallback callback){
//
//    }
//

    public static void createSessionIfRevoked(Activity activity, Account account, CompleteCallback callback){

        try {
            account.listSessions(new Continuation<SessionList>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                    if(o instanceof Result.Failure){

                        Log.e("session error",o.toString());
//                        createSession(activity,account,callback);
                    }else {

                        callback.onComplete();
                    }
                }
            });
        } catch (AppwriteException e) {
            e.printStackTrace();
        }


    }

//    public static void createSession(Activity activity, Account account,CompleteCallback callback){
//        try {
//            account.createAnonymousSession(new Continuation<Session>() {
//                @NonNull
//                @Override
//                public CoroutineContext getContext() {
//                    return EmptyCoroutineContext.INSTANCE;
//                }
//
//                @Override
//                public void resumeWith(@NonNull Object o) {
//
//
//                    if (o instanceof Result.Failure) {
//
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Result.Failure failure = (Result.Failure) o;
//                                Log.e("Session failure", failure.exception.getLocalizedMessage());
//                                failure.exception.printStackTrace();
//                                Toast.makeText(activity, activity.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                    else {
//                        if(callback!=null) {
//                            callback.onComplete();
//                            Log.e("Session", new Gson().toJson(o));
//
//                        }
//                    }
//
//
//
//
//
//
//                }
//            });
//        } catch (AppwriteException e) {
//            e.printStackTrace();
//        }
//    }



}
