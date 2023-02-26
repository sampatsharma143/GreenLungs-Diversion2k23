package com.shunyank.appwriteproject.network;

import static com.shunyank.appwriteproject.network.Constants.NodeApiEndPoint;
import static com.shunyank.appwriteproject.network.Constants.NodeApiVersion;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    public static   Retrofit retrofit=null;
    public static RetrofitAPI retrofitAPI=null;

    public static RetrofitAPI getRetrofitApi( )
    {
        if (retrofit == null||retrofitAPI==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(NodeApiEndPoint+NodeApiVersion)
                    // as we are sending data in json format so
                    // we have to add Gson converter factory
                    .addConverterFactory(GsonConverterFactory.create())
                    // at last we are building our retrofit builder.
                    .build();
            retrofitAPI = retrofit.create(RetrofitAPI.class);
        }
        return retrofitAPI;
    }



    public Retrofit Retrofit() {

        return retrofit;
    }
}
