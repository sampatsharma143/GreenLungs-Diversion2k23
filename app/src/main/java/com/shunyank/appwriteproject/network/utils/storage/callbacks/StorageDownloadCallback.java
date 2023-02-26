package com.shunyank.appwriteproject.network.utils.storage.callbacks;

import io.appwrite.exceptions.AppwriteException;
import kotlin.Result;

public interface StorageDownloadCallback {

    void fileContent(byte[] fileBytes);
    void onFailed(Result.Failure failure);
    void onException(AppwriteException exception);
}
