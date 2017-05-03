package com.example.yang.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;
import com.avos.avoscloud.AVOSCloud;
import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.basic.MyApplication;

import java.util.Date;
import java.util.Iterator;


public class PushReceiver extends BroadcastReceiver {

    private SharedPreferences pref;
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.d("Receive Broadcast");
        try {
            if (intent.getAction().equals("com.example.yang.myapplication.UPDATE_STATUS")) {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
/*
                Iterator itr = json.keys();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    LogUtil.d("..." + key + " => " + json.getString(key));
                }
*/
                LogUtil.d("index:"+json.getString("index"));
                LogUtil.d("date:"+json.getString("date"));

                //时间判断
                Date pushDate=new Date(json.getString("date"));
                pref= PreferenceManager.getDefaultSharedPreferences(context);
                String string=pref.getString("latestUpdate","");
                Date latestUpdate=new Date(string);
                if (pushDate.after(latestUpdate)){
                    Intent resultIntent = new Intent(AVOSCloud.applicationContext, ListActivity.class);
                    intent.putExtra("index",json.getString("index"));
                    intent.putExtra("date",json.getString("date"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
                    NotificationManager manager=(NotificationManager) MyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(context)
                            .setContentTitle("标题")//标题
                            .setContentText("正文")//正文
                            .setWhen(System.currentTimeMillis())//通知发生的时间为系统当前时间
                            .setContentIntent(pendingIntent)//点击跳转intent
                            .setAutoCancel(true)//点击之后自动消失
                            .setSmallIcon(R.mipmap.ic_launcher_round)   //若没有设置largeicon，此为左边的大icon，设置了largeicon，则为右下角的小icon，无论怎样，都影响Notifications area显示的图标
                            .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(),R.mipmap.ic_launcher))//largeicon，
                            .setVibrate(new long[]{300,100,300,100}) //设置震动，此震动数组为：long vT[]={300,100,300,100}; 还可以设置灯光.setLights(argb, onMs, offMs)
                            .build();
                    manager.notify(1,notification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
