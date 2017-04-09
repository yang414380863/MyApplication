package com.example.yang.myapplication;

/**
 * Created by YanGGGGG on 2017/4/6.
 * 自定义爬虫的规则
 */

public class Rule {

    private String selector;//选择器
    private String method;
    private String attribute;//要提取的属性
    private String regex; //正则表达式
    private String[] replace;//替换式

    //如method=text时不需要attribute
    Rule(String selector,String method){
        this.selector=selector;
        this.method=method;
    }
    //默认
    Rule(String selector,String method,String attribute){
        this.selector=selector;
        this.attribute=attribute;
        this.method=method;
    }
    //只需要用正则的
    Rule(String regex,String[] replace){
        this.regex=regex;
        this.replace=replace;
    }
    //需要用选择器+正则的
    Rule(String selector,String method,String attribute,String regex,String[] replace){
        this(selector,method,attribute);
        this.regex=regex;
        this.replace=replace;
    }


    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String[] getReplace() {
        return replace;
    }

    public void setReplace(String[] replace) {
        this.replace = replace;
    }
}
