package com.example.yang.myapplication.web;

import android.content.Intent;

import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.basic.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



/**
 * Created by YanGGGGG on 2017/5/30.
 */

public class GetToolBarImg {
    public static String imgSrc;
    static ItemRule ruleBING=new ItemRule();
    final static Website BING=new Website("Bing","http://cn.bing.com/",ruleBING);
    static Website websiteNow=BING;
    public static void sendRequest(){
        BING.setItemSelector("*");
        ruleBING.setThumbnailRule(new Rule("*","html","(\\/az\\/hprichbg\\/rb\\/[a-z|A-Z|0-9|_|-]+\\.jpg)","http://cn.bing.com$1"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url=websiteNow.getIndexUrl();
                    OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.d("onFailure");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try{
                                //解析HTML
                                Document doc= Jsoup.parse(response.body().string());

                                imgSrc=SelectorAndRegex.getItemData(doc,websiteNow,"Thumbnail",1);
                                LogUtil.d("Bing Today Img:"+imgSrc);
                                //发送一个加载完成了的广播
                                Intent intent=new Intent("com.example.yang.myapplication.TOOL_BAR_LOAD_FINISH");
                                MyApplication.getContext().sendBroadcast(intent);
                            }catch (Exception e){
                                //发送一个加载出错的广播
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
