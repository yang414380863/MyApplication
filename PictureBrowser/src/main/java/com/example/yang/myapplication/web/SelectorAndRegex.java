package com.example.yang.myapplication.web;

import android.util.Log;

import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.yang.myapplication.web.Browser.websiteNow;

/**
 * Created by YanGGGGG on 2017/4/13.
 */

public class SelectorAndRegex {
    private static String string;

    public static String get(Document doc,Rule rule,int position,int sizeNow){
        //先用选择器
        string=doc.select(rule.getSelector()).get(position).attr(rule.getAttribute());
        Log.d("Selector"+position," "+string);
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
                Log.d("Regex"+position," "+string);
            }
        }
        return string;
    }

    public static String get(Document doc,Rule rule,int position){
        return get(doc,rule,position,0);
    }
    public static String get(Document doc,Rule rule){
        //先用选择器
        string=doc.select(rule.getSelector()).attr(rule.getAttribute());
        if (rule.getRegex()!=null){
            //用正则
            Pattern pattern=Pattern.compile(rule.getRegex());
            Matcher matcher=pattern.matcher(string);
            string="";
            if (matcher.find()){
                for (int i=0;i<matcher.groupCount();i++){
                    switch(rule.getReplace()[i]){
                        case "size":{
                            break;
                        }
                        default:
                            string+=matcher.group(i+1)+rule.getReplace()[i];
                            break;
                    }
                }
                Log.d("Regex"," "+string);
            }
        }else {
            Log.d("Selector"," "+string);
        }
        return string;
    }
}
