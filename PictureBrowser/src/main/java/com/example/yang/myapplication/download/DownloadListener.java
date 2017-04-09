package com.example.yang.myapplication.download;

/**
 * Created by YanGGGGG on 2017/3/26.
 */

public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
