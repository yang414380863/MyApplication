package com.example.yang.myapplication.web;


import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.html.SelectorAndRegex;
import com.example.yang.myapplication.web.json.JsonRuleConnector;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static ArrayList<WebItem> webContentList=new ArrayList<>();
    public static Website websiteNow;
    public static int sizeThisPage;
    private static String nextPageUrl;
    private static int pageNext;
    static Date latestUpdate;
    private static String categoryNow;

    public static void sendRequest(final Website website,final String refreshPlace){
        websiteNow =website;
        Date date = new Date(System.currentTimeMillis());
        latestUpdate=date;
        SharedPreferences pref;
        pref= PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        SharedPreferences.Editor editor;
        editor=pref.edit();
        editor.putString("latestUpdate",date.toString());
        editor.apply();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url=websiteNow.getIndexUrl();
                    OkHttpClient client = new OkHttpClient();
                    if (refreshPlace.equals("bottom")){
                        url=websiteNow.getNextPageUrl();
                        pageNext++;
                    }else if (refreshPlace.equals("new")){
                        pageNext=2;
                    }
                    //LogUtil.d("url "+url);
                    final Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.d("onFailure");
                            //Toast.makeText(MyApplication.getContext(),"Network connection failure",Toast.LENGTH_SHORT).show();
                            //发送一个加载完成了的广播
                            Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
                            intent.putExtra("websiteIndex",websiteNow.getIndexUrl());
                            MyApplication.getContext().sendBroadcast(intent);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try{
                                if (pageNext>2){
                                    if (!website.isJsonNext()){
                                        //解析HTML
                                        Document doc=Jsoup.parse(response.body().string());
                                        analysis(doc,refreshPlace);
                                    }else {
                                        //解析JSON
                                        String s=response.body().string();
                                        //JSONObject jsonObject=JSON.parseObject(response.body().string());
                                        analysisJSON(s,refreshPlace);
                                    }
                                }else {
                                    if (!website.isJsonIndex()){
                                        //解析HTML
                                        Document doc=Jsoup.parse(response.body().string());
                                        analysis(doc,refreshPlace);
                                    }else {
                                        //解析JSON
                                        String s=response.body().string();
                                        //JSONObject jsonObject=JSON.parseObject(response.body().string());
                                        analysisJSON(s,refreshPlace);
                                    }
                                }
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

    private static void analysis(Document doc,String refreshPlace){

        Elements list = doc.select(websiteNow.getItemSelector());
        sizeThisPage=list.size();
        int sizeNow=webContentList.size();//sizeNow=已经加载的item数量
        //如果是相同的一个页面 就覆盖原来的,且不增加长度  否则就加在后面
        switch (refreshPlace){
            case ("new"):
            {
                //为空(首次加载)
                webContentList.clear();
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebItem());
                }
                sizeNow=0;
                nextPageUrl="";
                break;
            }
            case ("top"):
            {
                //是同一页面(顶部下拉刷新)刷新后可能数组过小
                for (int i=sizeNow;i<sizeThisPage;i++){
                    webContentList.add(new WebItem());
                }
                sizeNow=0;
                break;
            }
            case ("bottom"):{
                //不是同一页面(底部上拉刷新)
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebItem());
                }
                break;
            }
            default:break;
        }

        //解析主要信息
        for (int i=0;i<sizeThisPage;i++,sizeNow++){
            if (webContentList.size()==0){
                return;
            }
            webContentList.get(sizeNow).setLink(SelectorAndRegex.getItemData(doc,websiteNow,"Link",i));
            webContentList.get(sizeNow).setThumbnail(SelectorAndRegex.getItemData(doc,websiteNow,"Thumbnail",i));
            webContentList.get(sizeNow).setTitle(SelectorAndRegex.getItemData(doc,websiteNow,"Title",i));
/*
            LogUtil.d("No.+i Link:"+webContentList.get(sizeNow).getLink());
            LogUtil.d("No.+i Thumbnail:"+webContentList.get(sizeNow).getThumbnail());
            LogUtil.d("No.+i Title:"+webContentList.get(sizeNow).getTitle());
*/
        }
        LogUtil.d("Finish load "+sizeNow+" item  Next item is No "+sizeNow);

        if (!websiteNow.getCategoryRule().getSelector().equals("")){
            categoryNow=SelectorAndRegex.getOtherData(doc,websiteNow,"Category");
        }
        //解析列表的下一页
        if (!websiteNow.getNextPageRule().getSelector().equals("")){
            nextPageUrl=SelectorAndRegex.getOtherData(doc,websiteNow,"NextPage",sizeNow+1,pageNext);
            nextPageUrl=nextPageUrl.replaceAll("categorys","categories");
        }
        LogUtil.d("nextPageUrl "+nextPageUrl);
        //发送一个加载完成了的广播
        Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
        intent.putExtra("websiteIndex",websiteNow.getIndexUrl());
        MyApplication.getContext().sendBroadcast(intent);
    }

    private static void analysisJSON(String jsonData,String refreshPlace){

        List<Object> links = JsonRuleConnector.getCompleteLinks(jsonData);
        List<Object> thumbnails = JsonRuleConnector.getCompleteThumbnails(jsonData);
        List<Object> titles = JsonRuleConnector.getCompleteTitles(jsonData);
        String nextPage = JsonRuleConnector.getCompleteNextPage(jsonData);

        categoryNow=categoryNow.replaceAll("categorys","categories");
        nextPageUrl=nextPage.replaceAll("category",categoryNow);

        sizeThisPage=links.size();
        int sizeNow=webContentList.size();//sizeNow=已经加载的item数量
        //如果是相同的一个页面 就覆盖原来的,且不增加长度  否则就加在后面
        switch (refreshPlace){
            case ("new"):
            {
                //为空(首次加载)
                webContentList.clear();
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebItem());
                }
                sizeNow=0;
                nextPageUrl="";
                break;
            }
            case ("top"):
            {
                //是同一页面(顶部下拉刷新)刷新后可能数组过小
                for (int i=sizeNow;i<sizeThisPage;i++){
                    webContentList.add(new WebItem());
                }
                sizeNow=0;
                break;
            }
            case ("bottom"):{
                //不是同一页面(底部上拉刷新)
                for (int i=0;i<sizeThisPage;i++){
                    webContentList.add(new WebItem());
                }
                break;
            }
            default:break;
        }

        //解析主要信息
        for (int i=0;i<sizeThisPage;i++,sizeNow++){
            if (webContentList.size()==0){
                return;
            }
            webContentList.get(sizeNow).setLink(links.get(i).toString());
            webContentList.get(sizeNow).setThumbnail(thumbnails.get(i).toString());
            webContentList.get(sizeNow).setTitle(titles.get(i).toString());
/*
            LogUtil.d("No.+i Link:"+webContentList.get(sizeNow).getLink());
            LogUtil.d("No.+i Thumbnail:"+webContentList.get(sizeNow).getThumbnail());
            LogUtil.d("No.+i Title:"+webContentList.get(sizeNow).getTitle());
*/
        }
        LogUtil.d("Finish load "+sizeNow+" item  Next item is No "+sizeNow);
        //解析列表的下一页
        LogUtil.d("nextPageUrl "+nextPageUrl);
        //发送一个加载完成了的广播
        Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
        intent.putExtra("websiteIndex",websiteNow.getIndexUrl());
        MyApplication.getContext().sendBroadcast(intent);

    }

    public  static void sendRequestDetail(final int id,final String refreshPlace){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (refreshPlace.equals("new")){
                    webContentList.get(id).setImg(new ArrayList<String>());
                    webContentList.get(id).setArticle(new ArrayList<String>());
                }else if (refreshPlace.equals("top")){
                    webContentList.get(id).getImg().clear();
                }else if (refreshPlace.equals("bottom")){

                }
                try{
                    OkHttpClient client = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(websiteNow.getNextPageDetailUrl())
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.d("onFailure");
                            Toast.makeText(MyApplication.getContext(),"Network connection failure",Toast.LENGTH_SHORT).show();
                            //发送一个加载完成了的广播
                            Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH");
                            intent.putExtra("websiteIndex",websiteNow.getIndexUrl());
                            MyApplication.getContext().sendBroadcast(intent);
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try{
                                Document doc=Jsoup.parse(response.body().string());
                                analysisDetail(id,doc);
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

    private static void analysisDetail(final int id,Document doc) {
        String nextPageDetail;
        Elements list = doc.select(websiteNow.getDetailItemSelector());
        for (int i = 0; i < list.size(); i++) {
            webContentList.get(id).getImg().add("");
            webContentList.get(id).getArticle().add("");
            webContentList.get(id).getImg().set(webContentList.get(id).getImg().size()-1,SelectorAndRegex.getDetailData(doc,websiteNow,"Img",i));
            webContentList.get(id).getArticle().set(webContentList.get(id).getArticle().size()-1,SelectorAndRegex.getDetailData(doc,websiteNow,"Article",i));
        }
        if (!websiteNow.getNextPageDetailRule().getSelector().equals("")) {
            nextPageDetail = SelectorAndRegex.getOtherData(doc,websiteNow,"NextPageDetail");
            //LogUtil.d("nextPageDetail "+nextPageDetail);
            if (nextPageDetail.equals("")) {
                //没有下一页
//LogUtil.d("detail "+webContentList.get(id).getImg());
                //发送一个加载完成了的广播
                Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH_DETAIL");
                intent.putExtra("position",id);
                MyApplication.getContext().sendBroadcast(intent);
            } else {//继续下一页
                websiteNow.setNextPageDetailUrl(nextPageDetail);
                sendRequestDetail(id,"bottom");
                LogUtil.d("nextPageDetail not exist");
            }
        } else {//没有下一页的Rule
//LogUtil.d("detail "+webContentList.get(id).getImg());
            //发送一个加载完成了的广播
            Intent intent=new Intent("com.example.yang.myapplication.LOAD_FINISH_DETAIL");
            intent.putExtra("position",id);
            MyApplication.getContext().sendBroadcast(intent);
        }
    }

    public  static void nextPage(){
        if (nextPageUrl!=null){
            websiteNow.setNextPageUrl(nextPageUrl);
            sendRequest(websiteNow,"bottom");
        }else {
            //LogUtil.d(no more page");
        }
    }
}
