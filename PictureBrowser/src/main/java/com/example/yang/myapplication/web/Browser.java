package com.example.yang.myapplication.web;


import android.content.Intent;
import android.util.Log;

import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.WebContent;
import com.example.yang.myapplication.web.Website;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by YanGGGGG on 2017/3/21.
 * 抓取website的信息
 */

public class Browser {

    public static ArrayList<WebContent> webContentList=new ArrayList<>();
    public static Website websiteNow;
    public static int sizeThisPage;
    static String nextPageUrl;


    public static void sendRequest(final Website website){
        websiteNow =website;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url=websiteNow.getIndexUrl();
                    OkHttpClient client = new OkHttpClient();
                    if (websiteNow.getNextPageUrl()!=null){
                        url=websiteNow.getNextPageUrl();
                    }
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("OkHttpClient","onFailure");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Document doc=Jsoup.parse(response.body().string());
                            analysis(doc);
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void analysis(Document doc){


        sizeThisPage=doc.select(websiteNow.getRuleAll().getThumbnailRule().getSelector()).size();
        int sizeNow=webContentList.size();//sizeNow=已经加载的item数量
        //如果是相同的一个页面 就覆盖原来的,且不增加长度  否则就加在后面
        if (sizeNow<sizeThisPage){//为空(首次加载)/数组过小
            for (int i=sizeNow;i<sizeThisPage;i++){
                webContentList.add(new WebContent());
            }
        }else {
            //不为空
            if (webContentList.get(1).getThumbnail().equals(doc
                    .select(websiteNow.getRuleAll().getLinkRule().getSelector()).get(1)
                    .attr(websiteNow.getRuleAll().getLinkRule().getAttribute()))){
                //是同一页面(顶部下拉刷新)
                sizeNow=0;
            }else {
                //不是同一页面(底部上拉刷新)
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebContent());
                }
            }
        }


        Log.d("Link"," "+doc
                .select(websiteNow.getRuleAll().getLinkRule().getSelector()).size());
        Log.d("Thumbnail"," "+doc
                .select(websiteNow.getRuleAll().getThumbnailRule().getSelector()).size());
        Log.d("Title"," "+doc
                .select(websiteNow.getRuleAll().getTitleRule().getSelector()).size());
        for (int i=0;i<sizeThisPage;i++,sizeNow++){

            webContentList.get(sizeNow).setLink(doc
                    .select(websiteNow.getRuleAll().getLinkRule().getSelector()).get(i)
                    .attr(websiteNow.getRuleAll().getLinkRule().getAttribute()));

            webContentList.get(sizeNow).setThumbnail(doc
                    .select(websiteNow.getRuleAll().getThumbnailRule().getSelector()).get(i)
                    .attr(websiteNow.getRuleAll().getThumbnailRule().getAttribute()));

            if (websiteNow.getRuleAll().getTitleRule().getMethod()=="attr"){
                webContentList.get(sizeNow).setTitle(doc
                        .select(websiteNow.getRuleAll().getTitleRule().getSelector()).get(i)
                        .attr(websiteNow.getRuleAll().getTitleRule().getAttribute()));
            }else if (websiteNow.getRuleAll().getTitleRule().getMethod()=="text"){
                webContentList.get(sizeNow).setTitle(doc
                        .select(websiteNow.getRuleAll().getTitleRule().getSelector()).get(i)
                        .text());
            }
            websiteNow.setNextDetailPageUrl(webContentList.get(sizeNow).getLink());
            //Log.d("No."+i,"Link:"+webContentList.get(sizeNow).getLink());
            //Log.d("No."+i,"Thumbnail:"+webContentList.get(sizeNow).getThumbnail());
            //Log.d("No."+i,"Title:"+webContentList.get(sizeNow).getTitle());
            sendRequestDetail(sizeNow);

        }
        Log.d("Finish load "+sizeNow+" item","Next item is No "+sizeNow);

        if (websiteNow.getRuleAll().getNextPageRule().getSelector()!=null){//如果选择器有值 则用选择器匹配
            nextPageUrl= doc
                    .select(websiteNow.getRuleAll().getNextPageRule().getSelector())
                    .attr(websiteNow.getRuleAll().getNextPageRule().getAttribute());
        }else {//用正则匹配
            Pattern pattern=Pattern.compile(websiteNow.getRuleAll().getNextPageRule().getRegex());
            Matcher matcher=pattern.matcher(websiteNow.getIndexUrl());
            if (matcher.find()){
                for (int i=0;i<matcher.groupCount();i++){
                    switch(websiteNow.getRuleAll().getNextPageRule().getReplace()[i]){
                        case "size":{
                            nextPageUrl=matcher.group(i+1)+sizeNow;
                            //Log.d("nextPageUrl"," "+nextPageUrl);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
        }
        //发送一个加载完成了的广播
        Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
        MyApplication.getContext().sendBroadcast(intent);

    }

    public  static void sendRequestDetail(final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(websiteNow.getNextDetailPageUrl())
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("OkHttpClient","onFailure");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Document doc=Jsoup.parse(response.body().string());
                            analysisDetail(id,doc);
                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void analysisDetail(final int id,Document doc) {

        String nextDetailPage;
        StringBuilder string = new StringBuilder();
        Elements list = doc.select(websiteNow.getRuleAll().getImgRule().getSelector());
        for (int i = 0; i < list.size(); i++) {
            string.append(list.get(i)
                    .attr(websiteNow.getRuleAll().getImgRule().getAttribute()));
            string.append(",");
        }
        if (websiteNow.getRuleAll().getNextPageDetailRule()!=null) {
            nextDetailPage = doc
                    .select(websiteNow.getRuleAll().getNextPageDetailRule().getSelector())
                    .attr(websiteNow.getRuleAll().getNextPageDetailRule().getAttribute());
            if (nextDetailPage.equals("")) {//没有下一页
                webContentList.get(id).setImg(string.toString());
                //Log.d("string " + id, " " + string);
            } else {
                websiteNow.setNextDetailPageUrl(nextDetailPage);
                sendRequestDetail(id);
                //Log.d("nextPageDetail","not exist");
            }
        } else {//没有下一页的Rule
            webContentList.get(id).setImg(string.toString());
            //Log.d("nextPageDetailRule","not exist");
        }

    }

    public  static void nextPage(){
        if (nextPageUrl!=null){
            websiteNow.setNextPageUrl(nextPageUrl);
            sendRequest(websiteNow);
        }else {
            //Log.d("TAG","NO MORE");
        }
    }


}
