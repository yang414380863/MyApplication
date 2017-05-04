package com.example.yang.myapplication.web;


/**
 * Created by YanGGGGG on 2017/4/6.
 */

public class ItemRule {

    private Rule thumbnailRule;
    private Rule titleRule;
    private Rule linkRule;
    private Rule imgRule;
    private Rule detailRule;


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


}
