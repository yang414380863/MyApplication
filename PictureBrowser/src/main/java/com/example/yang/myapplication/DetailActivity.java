package com.example.yang.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.yang.myapplication.basic.BaseActivity;

//详情所在Activity
public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        //获取具体是哪一张图片 以及图片总数
        Intent intent=getIntent();
        int position=intent.getExtras().getInt("position");
        WebContent webContent= Browser.webContentList.get(position);


        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
        StaggeredGridLayoutManager layoutManager=new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        final DetailAdapter adapter=new DetailAdapter(webContent);
        recyclerView.setAdapter(adapter);
    }
}
