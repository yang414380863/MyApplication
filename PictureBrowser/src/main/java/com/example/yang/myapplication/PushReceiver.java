package com.example.yang.myapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;
import com.avos.avoscloud.AVOSCloud;
import com.example.yang.myapplication.basic.LogUtil;

import java.util.Iterator;


public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("Receive Broadcast");
        try {
            if (intent.getAction().equals("com.example.yang.myapplication.UPDATE_STATUS")) {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));

                Iterator itr = json.keys();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    LogUtil.d("..." + key + " => " + json.getString(key));
                }
                //LogUtil.d("index:"+json.getString("index"));
                //LogUtil.d("date:"+json.getString("date"));
                Intent resultIntent = new Intent(AVOSCloud.applicationContext, ListActivity.class);
                intent.putExtra("index",json.getString("index"));
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(context, 0, resultIntent, 0);
                NotificationCompat.Builder mBuilder =           //Notification 的兼容类
                        new NotificationCompat.Builder(context)
                                .setContentTitle("标题")//标题
                                .setContentText("正文")//正文
                                .setWhen(System.currentTimeMillis())//通知发生的时间为系统当前时间
                                .setContentIntent(pendingIntent)//点击跳转intent
                                .setAutoCancel(true);//点击之后自动消失
                                // .setSmallIcon(R.drawable.ic_launcher)   //若没有设置largeicon，此为左边的大icon，设置了largeicon，则为右下角的小icon，无论怎样，都影响Notifications area显示的图标
                                //.setLargeIcon(smallicon)//largeicon，
                                //.setDefaults(Notification.DEFAULT_SOUND)//设置声音，此为默认声音
                                //.setVibrate(vT) //设置震动，此震动数组为：long vT[]={300,100,300,100}; 还可以设置灯光.setLights(argb, onMs, offMs)
                                //.setOngoing(true)//true使notification变为ongoing，用户不能手动清除，类似QQ,false或者不设置则为普通的通知
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
