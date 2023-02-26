package com.shunyank.appwriteproject.models;

import com.google.gson.annotations.SerializedName;

public class MyEventModel {

    @SerializedName("$id")
    public String id;
    public String name;
    public String status;
    public String address;
    public String start_date;
    public String end_date;
    public String poster_url;

    public String lat;
    @SerializedName("long")
    public String geoLong;

}
