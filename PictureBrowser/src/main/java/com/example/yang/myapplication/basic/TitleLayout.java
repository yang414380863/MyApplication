package com.example.yang.myapplication.basic;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yang.myapplication.R;

/**
 * Created by YanGGGGG on 2017/3/20.
 */

//自定义控件
public class TitleLayout extends LinearLayout {

    public TitleLayout(Context context, AttributeSet attr){
        super(context,attr);
        LayoutInflater.from(context).inflate(R.layout.title,this);
        //from()构造出一个LayoutInflater对象   context是活动名
        //inflate动态加载一个布局文件(要加载的布局的ID,给加载好的布局添加一个父布局)
        Button titleBack=(Button)findViewById(R.id.title_back);
        Button titleQuit=(Button)findViewById(R.id.title_quit);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
        titleQuit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You clicked Quit button", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
            }
        });
    }
}