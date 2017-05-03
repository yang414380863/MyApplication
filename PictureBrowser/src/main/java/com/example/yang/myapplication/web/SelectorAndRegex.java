package com.example.yang.myapplication.web;


import com.example.yang.myapplication.basic.LogUtil;

import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by YanGGGGG on 2017/4/13.
 */

public class SelectorAndRegex {
    private static String string;

    public static String get(Document doc,Rule rule,int position,int sizeNow){
        //先用选择器
        if (doc.select(rule.getSelector()).size()==0){
            //匹配不到
            LogUtil.d("Selector can't find");
            return "";
        }
        if (rule.getMethod().equals("attr")){
            string=doc.select(rule.getSelector()).get(position).attr(rule.getAttribute());
        }else if (rule.getMethod().equals("text")){
            string=doc.select(rule.getSelector()).get(position).text();
        }
        //LogUtil.d("Selector"+position+" "+string);
        if (rule.getRegex()!=null){
            //用正则
            Pattern pattern=Pattern.compile(rule.getRegex());
            Matcher matcher=pattern.matcher(string);
            string="";
            if (matcher.find()){
                for (int i=0;i<matcher.groupCount();i++){
                    switch(rule.getReplace()[i]){
                        case "size":{
                            string+=matcher.group(i+1)+sizeNow;
                            break;
                        }
                        default:
                            string+=matcher.group(i+1)+rule.getReplace()[i];
                            break;
                    }
                }
                //LogUtil.d("Regex"+position+" "+string);
            }
        }
        //LogUtil.d("result "+string);
        return string;
    }

    public static String get(Document doc,Rule rule,int position){
        return get(doc,rule,position,0);
    }
    public static String get(Document doc,Rule rule){
        return get(doc,rule,0);
    }
}
