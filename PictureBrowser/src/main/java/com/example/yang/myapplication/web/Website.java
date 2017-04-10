package com.example.yang.myapplication.web;

/**
 * Created by YanGGGGG on 2017/4/6.
 * 新建一个爬虫即新建一个Website对象
 */

public class Website {


    private String webSiteName;//网站名
    private String indexUrl;//网站首页
    private String nextPageUrl;
    private String nextDetailPageUrl;
    private String[] category;//分类
    private int loginRequired;

    private RuleAll ruleAll;



    public Website(String webSiteName,String indexUrl,RuleAll ruleAll){
        this.webSiteName=webSiteName;
        this.indexUrl=indexUrl;
        this.ruleAll=ruleAll;
    }


    public RuleAll getRuleAll() {
        return ruleAll;
    }

    public void setRuleAll(RuleAll ruleAll) {
        this.ruleAll = ruleAll;
    }

    public String getWebSiteName() {
        return webSiteName;
    }

    public void setWebSiteName(String webSiteName) {
        this.webSiteName = webSiteName;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }

    public String[] getCategory() {
        return category;
    }

    public void setCategory(String[] category) {
        this.category = category;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getNextDetailPageUrl() {
        return nextDetailPageUrl;
    }

    public void setNextDetailPageUrl(String nextDetailPageUrl) {
        this.nextDetailPageUrl = nextDetailPageUrl;
    }
}