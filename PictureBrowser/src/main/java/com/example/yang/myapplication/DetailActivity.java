package com.example.yang.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.BaseActivity;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.WebContent;

import java.util.ArrayList;

import static android.R.attr.id;
import static com.bumptech.glide.Glide.with;
import static com.example.yang.myapplication.ListActivity.refreshPlace;
import static com.example.yang.myapplication.R.id.collapsing_toolbar;
import static com.example.yang.myapplication.web.Browser.sizeThisPage;
import static com.example.yang.myapplication.web.Browser.webContentList;
import static com.example.yang.myapplication.web.Browser.websiteNow;


//详情所在Activity
public class DetailActivity extends BaseActivity {

    //广播接收器
    private LoadFinishReceiver receiver;
    //下拉刷新 监听器
    SwipeRefreshLayout swipeRefreshLayout;
    static int isRefreshing=0;

    final DetailAdapter adapter=new DetailAdapter(this);
    static int positionNow;

    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        imageView=(ImageView)findViewById(R.id.image_view);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(collapsing_toolbar);

        //获取具体是哪一张图片 以及图片总数
        Intent intent=getIntent();
        positionNow=intent.getExtras().getInt("position");

        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
        StaggeredGridLayoutManager layoutManager=new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //广播接收器
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.yang.myapplication.LOAD_FINISH");
        receiver=new LoadFinishReceiver();
        registerReceiver(receiver,intentFilter);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        //首次进入先显示加载中
        swipeRefreshLayout.setRefreshing(true);

        //手动下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取最新数据并刷新
                if (isRefreshing==0){
                    isRefreshing=1;
                    Browser.sendRequestDetail(positionNow);
                    Log.d("refresh","detail is going to refresh!");
                }
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }

    class LoadFinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            WebContent webContent= webContentList.get(positionNow);
            ArrayList<String> urls=webContent.getImg();
            if (urls.size()==1){
                //单张图片直接进ViewPic
                Intent intent1=new Intent(DetailActivity.this,ViewPicture.class);
                intent1.putExtra("url",urls.get(0));
                //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                finish();
            }else {//多张图
                collapsingToolbarLayout.setTitle(webContent.getTitle());
                Glide
                        .with(context)
                        .load(urls.get(0))
                        .fitCenter()
                        .into(imageView);
                imageView.setImageAlpha(150);
            }

            Log.d("refresh","finish refresh!");
            adapter.getUrls().clear();//要重新指向一次才能检测到刷新
            adapter.getUrls().addAll(webContentList.get(positionNow).getImg());
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            isRefreshing=0;
        }
    }


}
