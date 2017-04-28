package com.example.yang.myapplication.basic;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by YanGGGGG on 2017/3/21.
 * 全局获取Context
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"4dwrmnAzfaW5WUmVI7yCu9P0-gzGzoHsz","lMQEwuP7o9ybGRApuGLwpGsR");
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        // 在应用发布之前，请关闭调试日志，以免暴露敏感数据。
        AVOSCloud.setDebugLogEnabled(true);
    }

    public static Context getContext(){
        return context;
    }
}
