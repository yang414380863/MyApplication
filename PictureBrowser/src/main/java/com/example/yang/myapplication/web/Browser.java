package com.example.yang.myapplication.web;


import android.content.Intent;
import android.util.Log;

import com.example.yang.myapplication.basic.MyApplication;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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
    private static String nextPageUrl;


    public static void sendRequest(final Website website,final String refreshPlace){
        websiteNow =website;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url=websiteNow.getIndexUrl();
                    OkHttpClient client = new OkHttpClient();
                    if (refreshPlace=="bottom"){
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
                            analysis(doc,refreshPlace);
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void analysis(Document doc,String refreshPlace){


        sizeThisPage=doc.select(websiteNow.getRuleAll().getThumbnailRule().getSelector()).size();
        int sizeNow=webContentList.size();//sizeNow=已经加载的item数量
        //如果是相同的一个页面 就覆盖原来的,且不增加长度  否则就加在后面
        switch (refreshPlace){
            case ("new"):
            {
                //为空(首次加载)
                webContentList.clear();
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebContent());
                }
                sizeNow=0;
                break;
            }
            case ("top"):
            {
                //是同一页面(顶部下拉刷新)刷新后可能数组过小
                for (int i=sizeNow;i<sizeThisPage;i++){
                    webContentList.add(new WebContent());
                }
                sizeNow=0;
                break;
            }
            case ("bottom"):{
                //不是同一页面(底部上拉刷新)
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebContent());
                }
                break;
            }
            default:break;
        }
/**
        Log.d("Link"," "+doc
                .select(websiteNow.getRuleAll().getLinkRule().getSelector()).size());
        Log.d("Thumbnail"," "+doc
                .select(websiteNow.getRuleAll().getThumbnailRule().getSelector()).size());
        Log.d("Title"," "+doc
                .select(websiteNow.getRuleAll().getTitleRule().getSelector()).size());
 */
        //解析主要信息
        for (int i=0;i<sizeThisPage;i++,sizeNow++){
            if (webContentList.size()==0){
                return;
            }
            Log.d("ListSize"," "+sizeNow);
            Log.d("ThisPage"," "+webContentList.size());
            Log.d("Match"," "+doc.select(websiteNow.getRuleAll().getLinkRule().getSelector()).size());
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
            //Log.d("No."+i,"Link:"+webContentList.get(sizeNow).getLink());
            //Log.d("No."+i,"Thumbnail:"+webContentList.get(sizeNow).getThumbnail());
            //Log.d("No."+i,"Title:"+webContentList.get(sizeNow).getTitle());
        }
        Log.d("Finish load "+sizeNow+" item","Next item is No "+sizeNow);
        //解析列表的下一页
        nextPageUrl=SelectorAndRegex.get(doc,websiteNow.getRuleAll().getNextPageRule(),0,sizeNow);
        Log.d("nextPageUrl"," "+nextPageUrl);
        //发送一个加载完成了的广播
        Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
        intent.putExtra("websiteName",websiteNow.getWebSiteName());
        MyApplication.getContext().sendBroadcast(intent);
    }

    public  static void sendRequestDetail(final int id,final String refreshPlace){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (refreshPlace=="new"){
                    webContentList.get(id).setImg(new ArrayList<String>());
                }else if (refreshPlace=="top"){
                    webContentList.get(id).getImg().clear();
                }else if (refreshPlace=="bottom"){

                }
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
        Elements list = doc.select(websiteNow.getRuleAll().getImgRule().getSelector());
        for (int i = 0; i < list.size(); i++) {
            webContentList.get(id).getImg().add("");
            webContentList.get(id).getImg().set(webContentList.get(id).getImg().size()-1,SelectorAndRegex.get(doc,websiteNow.getRuleAll().getImgRule(),i));
        }
        if (websiteNow.getRuleAll().getNextPageDetailRule()!=null) {
            nextDetailPage = SelectorAndRegex.get(doc,websiteNow.getRuleAll().getNextPageDetailRule());
            //Log.d("nextDetailPage"," "+nextDetailPage);
            if (nextDetailPage.equals("")) {
                //没有下一页
                //发送一个加载完成了的广播
                //Log.d("detail"," "+webContentList.get(id).getImg());
                Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
                intent.putExtra("position",id);
                MyApplication.getContext().sendBroadcast(intent);
            } else {//继续下一页
                websiteNow.setNextDetailPageUrl(nextDetailPage);
                sendRequestDetail(id,"bottom");
                //Log.d("nextPageDetail","not exist");
            }
        } else {//没有下一页的Rule
            //Log.d("nextPageDetailRule","not exist");
            //发送一个加载完成了的广播
            //Log.d("detail"," "+webContentList.get(id).getImg());
            Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
            intent.putExtra("position",id);
            MyApplication.getContext().sendBroadcast(intent);
        }
    }

    public  static void nextPage(){
        if (nextPageUrl!=null){
            websiteNow.setNextPageUrl(nextPageUrl);
            sendRequest(websiteNow,"bottom");
        }else {
            //Log.d("TAG","no more page");
        }
    }
}
