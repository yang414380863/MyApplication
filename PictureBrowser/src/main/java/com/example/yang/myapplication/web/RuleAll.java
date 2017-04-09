package com.example.yang.myapplication.web;


/**
 * Created by YanGGGGG on 2017/4/6.
 */

public class RuleAll {

    private Rule thumbnailRule;
    private Rule titleRule;
    private Rule linkRule;
    private Rule imgRule;
    private Rule detailRule;
    private Rule nextPageRule;//List页面下一页
    private String nextPageRegex;//正则
    private Rule nextPageDetailRule;//Detail页面下一页


    public Rule getThumbnailRule() {
        return thumbnailRule;
    }

    public void setThumbnailRule(Rule thumbnailRule) {
        this.thumbnailRule = thumbnailRule;
    }

    public Rule getTitleRule() {
        return titleRule;
    }

    public void setTitleRule(Rule titleRule) {
        this.titleRule = titleRule;
    }

    public Rule getLinkRule() {
        return linkRule;
    }

    public void setLinkRule(Rule linkRule) {
        this.linkRule = linkRule;
    }

    public Rule getImgRule() {
        return imgRule;
    }

    public void setImgRule(Rule imgRule) {
        this.imgRule = imgRule;
    }

    public Rule getDetailRule() {
        return detailRule;
    }

    public void setDetailRule(Rule detailRule) {
        this.detailRule = detailRule;
    }

    public Rule getNextPageRule() {
        return nextPageRule;
    }

    public void setNextPageRule(Rule nextPageRule) {
        this.nextPageRule = nextPageRule;
    }

    public String getNextPageRegex() {
        return nextPageRegex;
    }

    public void setNextPageRegex(String nextPageRegex) {
        this.nextPageRegex = nextPageRegex;
    }

    public Rule getNextPageDetailRule() {
        return nextPageDetailRule;
    }

    public void setNextPageDetailRule(Rule nextPageDetailRule) {
        this.nextPageDetailRule = nextPageDetailRule;
    }
}
