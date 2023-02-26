package com.shunyank.appwriteproject.network.utils.storage;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.shunyank.appwriteproject.network.AppWriteHelper;
import com.shunyank.appwriteproject.network.Constants;
import com.shunyank.appwriteproject.network.utils.storage.callbacks.StorageDownloadCallback;

import io.appwrite.exceptions.AppwriteException;
import io.appwrite.services.Storage;
import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class StorageUtils {
    public static Storage storage = null;


    public static void downloadFile(Activity activity, String bucketId, String fileId, StorageDownloadCallback downloadCallback) {
        try {
            getStorage(activity).getFileDownload(bucketId, fileId, new Continuation<byte[]>() {
                @NonNull
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object o) {

                    // TODO: 9/14/2022 change it to background thread we should not download file on main ui thread
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(o instanceof Result.Failure){
                                downloadCallback.onFailed((Result.Failure) o);
                            }
                            byte[] data = (byte[]) o;
                            downloadCallback.fileContent(data);
                        }
                    });


                }
            });
        } catch (AppwriteException e) {
            throw new RuntimeException(e);
        }

    }

    private static Storage getStorage(Context context) {
        if (storage == null) {
            storage = new Storage(AppWriteHelper.getClient());
            return storage;
        }
        return storage;
    }

    public static String fileLink(String buckedId,String fileId){
        String url = Constants.getAppUrl()+"/storage/buckets/"+buckedId+"/files/"+fileId+"/preview?project="+"63f74ab4bdf39b9fda3f";
        //[YOUR_ENDPOINT]/storage/buckets/{bucketId}/files/{fileId}/preview?project=[PROJECT_ID]
        return url;
    }
}