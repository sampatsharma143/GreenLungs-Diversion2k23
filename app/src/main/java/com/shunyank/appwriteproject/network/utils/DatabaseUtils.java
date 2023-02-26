package com.shunyank.appwriteproject.network.utils;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.shunyank.appwriteproject.network.AppWriteHelper;
import com.shunyank.appwriteproject.network.callbacks.DocumentCreateListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentDeleteListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentFetchListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentFindAndCreateListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentListFetchListener;
import com.shunyank.appwriteproject.network.callbacks.DocumentUpdateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.appwrite.Client;
import io.appwrite.exceptions.AppwriteException;
import io.appwrite.models.Document;
import io.appwrite.models.DocumentList;
import io.appwrite.services.Databases;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class DatabaseUtils {

    private static Databases databases;
    public static String DATABASE_ID ="default";
    public static Databases getDatabase(Client client){
        
        if(databases==null){
            databases =  new Databases(client);
        }
        return databases;
    }

    public static void deleteDocument( String collectionId, String documentId, DocumentDeleteListener documentDeleteListener){

        try {
            getDatabase(AppWriteHelper.getClient()).deleteDocument(DATABASE_ID,collectionId, documentId, new Continuation<Object>() {
                @Override
                public void resumeWith(@NonNull Object o) {

                            if(o instanceof Result.Failure){

                                    Log.e("failure collection",collectionId);

                                    ((Result.Failure) o).exception.printStackTrace();


                                documentDeleteListener.onFailed((Result.Failure) o);
                            }else {
                                documentDeleteListener.onDeletedSuccessfully();
                            }


                }

                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }
            });
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }

    }
    public static void updateDocument(String collectionId,
                                      String documentId,
                                      HashMap<Object,Object> data,
                                      DocumentUpdateListener documentUpdateListener){

        try {
            getDatabase(AppWriteHelper.getClient()).updateDocument(DATABASE_ID,collectionId, documentId,data ,new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }
                @Override
                public void resumeWith(@NonNull Object o) {

                            if(o instanceof Result.Failure){
                                    Log.e("failure collection",collectionId);

                                    ((Result.Failure) o).exception.printStackTrace();

                                documentUpdateListener.onFailed((Result.Failure) o);
                            }else {

                                documentUpdateListener.onUpdatedSuccessfully((Document) o);

                            }
                        }
            });

        }catch (AppwriteException exception){
            documentUpdateListener.onException(exception);
        }

    }



    public static void fetchDocument(String collectionId, String documentId, DocumentFetchListener documentFetchListener){

        try {
            getDatabase(AppWriteHelper.getClient()).getDocument(DATABASE_ID,collectionId, documentId, new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {


                            if(o instanceof Result.Failure){

                                    Log.e("failure collection",collectionId);

                                    ((Result.Failure) o).exception.printStackTrace();

                                documentFetchListener.onFailed((Result.Failure) o);
                            }else {

                                documentFetchListener.onFetchSuccessfully((Document) o);

                            }




                }
            });

        }catch (AppwriteException exception){
            documentFetchListener.onException(exception);
        }

    }

    public static void fetchDocument(Activity activity, String collectionId, String documentId, DocumentFetchListener documentFetchListener){

        try {
            getDatabase(AppWriteHelper.getClient()).getDocument(DATABASE_ID,collectionId, documentId, new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(o instanceof Result.Failure){
                                Log.e("Appwrite Failure","true");

                                    Log.e("failure collection",collectionId);

                                    ((Result.Failure) o).exception.printStackTrace();


                                documentFetchListener.onFailed((Result.Failure) o);
                            }else {

                                documentFetchListener.onFetchSuccessfully((Document) o);

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
                Log.e("Appwrite Exception","true");

            documentFetchListener.onException(exception);
        }

    }


    public static void updateDocument(Activity activity,String collectionId,
                                      String documentId,
                                      HashMap<Object,Object> data,
                                      DocumentUpdateListener documentUpdateListener){

        try {
            getDatabase(AppWriteHelper.getClient()).updateDocument(DATABASE_ID,collectionId, documentId,data ,new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }
                @Override
                public void resumeWith(@NonNull Object o) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(o instanceof Result.Failure){
                                    Log.e("failure collection",collectionId);

                                    ((Result.Failure) o).exception.printStackTrace();


                                documentUpdateListener.onFailed((Result.Failure) o);
                            }else {

                                documentUpdateListener.onUpdatedSuccessfully((Document) o);

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
            documentUpdateListener.onException(exception);
        }

    }



    public static void fetchDocuments(String collectionId, List<String> searchQueries,
                                      DocumentListFetchListener documentListFetchListener){
        try {
            List<String> search ;
            if(searchQueries==null){
                search = new ArrayList<>();
                searchQueries = search;
            }
            getDatabase(AppWriteHelper.getClient()).listDocuments(DATABASE_ID,collectionId, searchQueries, new Continuation<DocumentList>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                            if (o instanceof Result.Failure) {
                                Log.e("Appwrite Failure", "true");
                                    Log.e("failure collection",collectionId);
                                    ((Result.Failure) o).exception.printStackTrace();

                                documentListFetchListener.onFailed((Result.Failure) o);
                            } else {


                                DocumentList documentList = (DocumentList) o;


                                documentListFetchListener.onFetchSuccessfully(documentList.getDocuments());

                            }
                        }

            });

        }catch (AppwriteException exception){
                Log.e("AppWrite Exception","true");

            documentListFetchListener.onException(exception);
        }
    }



    public static void fetchDocuments(Activity activity,String collectionId, List<String> searchQueries,
                                      DocumentListFetchListener documentListFetchListener){
        try {
            if(searchQueries==null){
                searchQueries = new ArrayList<>();
            }
            getDatabase(AppWriteHelper.getClient()).listDocuments(DATABASE_ID,collectionId, searchQueries, new Continuation<DocumentList>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (o instanceof Result.Failure) {
                                Log.e("Appwrite Failure", "true");
                                    Log.e("failure collection",collectionId);

                                    ((Result.Failure) o).exception.printStackTrace();

                                documentListFetchListener.onFailed((Result.Failure) o);
                            } else {


                                DocumentList documentList = (DocumentList) o;


                                documentListFetchListener.onFetchSuccessfully(documentList.getDocuments());

                            }
                        }
                    });


                }
            });

        }catch (AppwriteException exception){
                Log.e("AppWrite Exception","true");

            documentListFetchListener.onException(exception);
        }
    }

    public static void createDocument(Activity activity,String collectionId,String docId, HashMap<Object,Object> data, DocumentCreateListener documentCreateListener) {

        try {
            getDatabase(AppWriteHelper.getClient()).createDocument(DATABASE_ID,collectionId, docId, data, new Continuation<Document>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (o instanceof Result.Failure) {
                                    Log.e("failure collection",collectionId);
                                    ((Result.Failure) o).exception.printStackTrace();

                                documentCreateListener.onFailed((Result.Failure) o);

                            } else {

                                documentCreateListener.onCreatedSuccessfully((Document) o);

                            }
                        }
                    });


                }
            });


        } catch (AppwriteException e) {
            e.printStackTrace();
            documentCreateListener.onException(e);
        }
    }
    public static void createDocumentIfNotFound(Activity activity, Databases database, List<String> searchQuery, String collectionId,
                                                HashMap<Object,Object> data,
                                                DocumentFindAndCreateListener documentFindAndCreateListener) {



        fetchDocuments(activity, collectionId, searchQuery, new DocumentListFetchListener() {
            @Override
            public void onFetchSuccessfully(List<Document> document) {
                Log.e("fetchedData",new Gson().toJson(document));
                if(document.size()>0){
                    // 0 for only first item
                    documentFindAndCreateListener.onFindSuccessfully(document);
                }else {

                    try {
                        database.createDocument(DATABASE_ID,collectionId, "unique()", data, new Continuation<Document>() {
                            @NonNull
                            @Override
                            public CoroutineContext getContext() {
                                return EmptyCoroutineContext.INSTANCE;
                            }

                            @Override
                            public void resumeWith(@NonNull Object o) {

                                if(o instanceof Result.Failure){

                                        Log.e("failure collection",collectionId);

                                        ((Result.Failure) o).exception.printStackTrace();

                                    documentFindAndCreateListener.onFailed((Result.Failure) o);

                                }else {

                                    documentFindAndCreateListener.onCreatedSuccessfully((Document) o);

                                }

                            }
                        });
                    } catch (AppwriteException e) {
                        e.printStackTrace();
                        documentFindAndCreateListener.onException(e);
                    }

                }
            }
            @Override
            public void onFailed(Result.Failure failure) {
                documentFindAndCreateListener.onFailed(failure);

            }

            @Override
            public void onException(AppwriteException exception) {
                documentFindAndCreateListener.onException(exception);

            }
        });

    }



    public static <T> T convertToModel(Document document, Class<T> classOfT){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(document.getData());
        return new Gson().fromJson(jsonElement,classOfT);
    }

    public static <T> ArrayList<T> convertToModelList(List<Document> documents, Class<T> classOfT){
        Gson gson = new Gson();
        ArrayList<T> data = new ArrayList<>();
        for(Document document:documents){
            JsonElement jsonElement = gson.toJsonTree(document.getData());
            data.add( new Gson().fromJson(jsonElement,classOfT));
        }
        return data;
    }
    public static HashMap<Object,Object> convertToHashmap(Object object){
        HashMap<Object, Object> data = new Gson().fromJson(
                new Gson().toJson(object), new TypeToken<HashMap<String, Object>>() {}.getType()
        );
        return data;
    }

}
