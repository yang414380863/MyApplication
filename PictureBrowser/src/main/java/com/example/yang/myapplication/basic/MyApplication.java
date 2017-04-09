package com.example.yang.myapplication.basic;

import android.app.Application;
import android.content.Context;

/**
 * Created by YanGGGGG on 2017/3/21.
 * 全局获取Context
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
