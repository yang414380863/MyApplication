package com.example.yang.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.BaseActivity;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.WebContent;

import static com.bumptech.glide.Glide.with;
import static com.example.yang.myapplication.R.id.collapsing_toolbar;


//详情所在Activity
public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        ImageView imageView=(ImageView)findViewById(R.id.image_view);
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(collapsing_toolbar);

        //获取具体是哪一张图片 以及图片总数
        Intent intent=getIntent();
        int position=intent.getExtras().getInt("position");
        WebContent webContent= Browser.webContentList.get(position);
        if (webContent.getImg()==null){
            Toast.makeText(MyApplication.getContext(),"DETAIL NOT READY YET",Toast.LENGTH_SHORT).show();
            finish();
        }else {
            String strings=webContent.getImg();
            String[] url=strings.split(",");//如果strings包含多个链接,将其拆分开成string[]
            if (url.length==1){
                //单张图片直接进ViewPic
                Intent intent1=new Intent(this,ViewPicture.class);
                intent1.putExtra("url",url[0]);
                //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent1);
                finish();
            }else {//多张图
                collapsingToolbarLayout.setTitle(webContent.getTitle());
                Glide
                        .with(this)
                        .load(url[0])
                        .fitCenter()
                        .into(imageView);
                RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
                StaggeredGridLayoutManager layoutManager=new
                        StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                final DetailAdapter adapter=new DetailAdapter(webContent,this);
                recyclerView.setAdapter(adapter);
            }
        }





    }
}
