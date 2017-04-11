package com.example.yang.myapplication;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.web.WebContent;


/**
 * Created by YanGGGGG on 2017/3/22.
 * 显示详细情况
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>{

    private String[] url;
    private int numOfPic;
    private WebContent webContent;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;

        public ViewHolder(View view){
            super(view);
            image=(ImageView)view.findViewById(R.id.image);
            name=(TextView)view.findViewById(R.id.name);
        }
    }

    public DetailAdapter(WebContent webContent, Context context){
        this.context=context;
        this.webContent=webContent;
        if (webContent.getImg()!=null){
            String strings=webContent.getImg();
            url=strings.split(",");//如果strings包含多个链接,将其拆分开成string[]
            numOfPic=url.length;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.web_content_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                //显示下载按钮 以及其他
                int position=holder.getAdapterPosition();
                Intent intent=new Intent(context,ViewPicture.class);
                intent.putExtra("url",url[position]);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Glide
                .with(context)
                .load(url[position])
                .thumbnail(Glide.with(context).load(R.drawable.loading1))
                .placeholder(R.drawable.white)
                .error(R.drawable.error)
                .fitCenter()
                .dontAnimate()//无载入动画
                //.crossFade() //设置淡入淡出效果，默认300ms，可以传参 会导致图片变形 先不用
                .into(holder.image);

    }

    @Override
    public int getItemCount(){
        return numOfPic;
    }
}
