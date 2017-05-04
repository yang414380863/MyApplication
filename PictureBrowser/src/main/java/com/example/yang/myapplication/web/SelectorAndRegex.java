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

    public static String getItemData(Document doc,Website website,String ruleString,int position){
        //判断是哪个Rule
        Rule rule;
        switch (ruleString){
            case "Link":{
                rule=website.getItemRule().getLinkRule();
                break;
            }
            case "Title":{
                rule=website.getItemRule().getTitleRule();
                break;
            }
            case "Thumbnail":{
                rule=website.getItemRule().getThumbnailRule();
                break;
            }
            case "Img":{
                rule=website.getItemRule().getImgRule();
                break;
            }
            case "Article":{
                rule=website.getItemRule().getArticleRule();
                break;
            }
            default:
                rule=website.getItemRule().getLinkRule();//肯定不触发
                break;
        }
        if (rule==null){
            return "";
        }
        //先用选择器
        if (doc.select(website.getItemSelector()).size()==0){
            //匹配不到
LogUtil.d(ruleString+" Selector can't find");
            return "";
        }
        if (rule.getMethod().equals("attr")){
            string=doc.select(website.getItemSelector()).get(position).select(rule.getSelector()).attr(rule.getAttribute());
        }else if (rule.getMethod().equals("text")){
            string=doc.select(website.getItemSelector()).get(position).select(rule.getSelector()).text();
        }
LogUtil.d(ruleString+" Selector"+position+" "+string);
        if (rule.getRegex()!=null){
            //用正则
            Pattern pattern=Pattern.compile(rule.getRegex());
            Matcher matcher=pattern.matcher(string);
            string="";
            if (matcher.find()){
                for (int i=0;i<matcher.groupCount();i++){
                    string+=matcher.group(i+1)+rule.getReplace()[i];
LogUtil.d(ruleString+" Regex"+i+" "+string);
                }
            }
        }
LogUtil.d(ruleString+" result "+position+": "+string);
        return string;
    }

    public static String getOtherData(Document doc,Website website,String ruleString){
        return getOtherData(doc,website,ruleString,0,0);
    }
    public static String getOtherData(Document doc,Website website,String ruleString,int sizeNow,int pageNow){
        //判断是哪个Rule
        Rule rule;
        switch (ruleString){
            case "NextPage":{
                rule=website.getNextPageRule();
                break;
            }
            case "NextPageDetail":{
                rule=website.getNextPageDetailRule();
                break;
            }
            default:
                rule=website.getNextPageRule();//肯定不触发
                break;
        }
        //先用选择器
        if (doc.select(rule.getSelector()).size()==0){
            //匹配不到
LogUtil.d(ruleString+" Selector can't find");
            return "";
        }
        string=doc.select(rule.getSelector()).attr(rule.getAttribute());
LogUtil.d(ruleString+" Selector "+string);
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
                        case "page":{
                            string+=matcher.group(i+1)+pageNow;
                            break;
                        }
                        default:
                            string+=matcher.group(i+1)+rule.getReplace()[i];
                            break;
                    }
LogUtil.d(ruleString+" Regex"+i+" "+string);
                }
            }
        }
LogUtil.d(ruleString+" result : "+string);
        return string;
    }

    public static String getDetailData(Document doc,Website website,String ruleString,int position){
        //判断是哪个Rule
        Rule rule;
        switch (ruleString){
            case "Img":{
                rule=website.getItemRule().getImgRule();
                break;
            }
            case "Article":{
                rule=website.getItemRule().getArticleRule();
                break;
            }
            default:
                rule=website.getItemRule().getImgRule();//肯定不触发
                break;
        }
        if (rule==null){
            return "";
        }
        //先用选择器
        if (doc.select(website.getDetailItemSelector()).size()==0){
            //匹配不到
LogUtil.d(ruleString+" Selector can't find");
            return "";
        }
        if (rule.getMethod().equals("attr")){
            string=doc.select(website.getDetailItemSelector()).get(position).select(rule.getSelector()).attr(rule.getAttribute());
        }else if (rule.getMethod().equals("text")){
            string=doc.select(website.getDetailItemSelector()).get(position).select(rule.getSelector()).text();
        }
LogUtil.d(ruleString+" Selector"+position+" "+string);
        if (rule.getRegex()!=null){
            //用正则
            Pattern pattern=Pattern.compile(rule.getRegex());
            Matcher matcher=pattern.matcher(string);
            string="";
            if (matcher.find()){
                for (int i=0;i<matcher.groupCount();i++){
                    string+=matcher.group(i+1)+rule.getReplace()[i];
LogUtil.d(ruleString+" Regex"+i+" "+string);
                }
            }
        }
LogUtil.d(ruleString+" result "+position+" "+string);
        return string;
    }

    public static int getItemcount(Document doc,Website website){
        int itemcount=doc.select(website.getItemSelector()).size();
LogUtil.d("itemcount="+itemcount);
        return itemcount;
    }
}
