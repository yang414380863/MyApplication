package com.example.yang.myapplication.basic;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanGGGGG on 2017/3/20.
 */

//用一个List来暂存活动
public class ActivityCollector {
    public static List<Activity> activities=new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        //以后用ActivityCollector.finishAll()来退出应用
        for(Activity activity:activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}