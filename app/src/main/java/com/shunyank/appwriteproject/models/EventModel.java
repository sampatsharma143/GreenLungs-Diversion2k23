package com.shunyank.appwriteproject.models;

import com.google.gson.annotations.SerializedName;

public class EventModel {

    @SerializedName("$id")
    public String id;
    public String name;
    public String status;
    public String address;
    public String start_date;
    public String end_date;
    public String poster_url;
    public String volunteers_count;
    public String tree_planted;
    public String lat;
    @SerializedName("long")
    public String geoLong;
    public String description;
    public String organizer;
    public String creator_id;
    public String volunteers_list;




}
/**
 *
 *
 * name
 * string
 * -
 * status
 * string
 * -
 * volunteers_count
 * string
 * 0
 * address
 * string
 * -
 * tree_planted
 * string
 * 0
 * poster_url
 * string
 * -
 * lat
 * string
 * -
 * long
 * string
 * -
 * description
 * string
 * -
 * organizer
 * string
 * -
 * start_date
 * string
 * -
 * end_date
 * string
 * -
 * creator_id
 *
 *
 *
 * */