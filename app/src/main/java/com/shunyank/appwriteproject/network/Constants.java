package com.shunyank.appwriteproject.network;

public class Constants {
    public static String AppUrl = "backend.live";
    public static String API_VERSION = "/v1";
    public static String AppWrite_AppName = "https://app.";
    public static String EventCollection="event_collection";


    public static String getAppUrl() {
        return AppWrite_AppName+AppUrl+API_VERSION;
    }

    public static String NodeApiEndPoint = "https://megamind-nodejs-backend-test.herokuapp.com";
    public static String NodeApiVersion = "/api/v1/";

    public static String UserCollectionId = "users-collection";
    public static String databaseId = "default";
}
