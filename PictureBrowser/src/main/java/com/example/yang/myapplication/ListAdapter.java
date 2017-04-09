package com.example.yang.myapplication;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.WebContent;

import java.util.ArrayList;


/**
 * Created by YanGGGGG on 2017/3/21.
 * 显示列表
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private ArrayList<WebContent> webContents=new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public ViewHolder(View view){
            super(view);
            image=(ImageView)view.findViewById(R.id.image);
            name=(TextView)view.findViewById(R.id.name);
        }
    }

    public ListAdapter(){//构造方法
        webContents.addAll(Browser.webContentList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //生成一个webimg_item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.web_content_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //点击->获取链接->显示图片/目录
                int position=holder.getAdapterPosition();
                Intent intent=new Intent(MyApplication.getContext(),DetailActivity.class);
                intent.putExtra("position",position);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        WebContent webContent= Browser.webContentList.get(position);
        holder.name.setText(webContent.getTitle());
        Glide
                .with(MyApplication.getContext())
                .load(webContent.getThumbnail())
                .thumbnail(Glide.with(MyApplication.getContext()).load(R.drawable.loading1))
                .placeholder(R.drawable.white)
                .fitCenter()
                .dontAnimate()//无载入动画
                //.crossFade() //设置淡入淡出效果，默认300ms，可以传参 会导致图片变形 先不用
                .into(holder.image);
    }

    @Override
    public int getItemCount(){
        return webContents.size();
    }

    public ArrayList<WebContent> getWebContents(){
        return webContents;
    }

}
