package com.example.yang.myapplication.web.json;

/**
 * Created by JesusY on 2017/6/4.
 */

public class JsonItemRule {
    private JsonRule thumbnailRule;
    private JsonRule titleRule;
    private JsonRule linkRule;
    private JsonRule categoryRule;
    private JsonRule authorRule;
    private JsonRule publishTimeRule;
    private JsonRule nextPageRule;

    public JsonRule getNextPageRule() {
        return nextPageRule;
    }

    public void setNextPageRule(JsonRule nextPageRule) {
        this.nextPageRule = nextPageRule;
    }

    public JsonRule getAuthorRule() {
        return authorRule;
    }

    public void setAuthorRule(JsonRule authorRule) {
        this.authorRule = authorRule;
    }

    public JsonRule getPublishTimeRule() {
        return publishTimeRule;
    }

    public void setPublishTimeRule(JsonRule publishTimeRule) {
        this.publishTimeRule = publishTimeRule;
    }

    public JsonRule getThumbnailRule() {
        return thumbnailRule;
    }

    public void setThumbnailRule(JsonRule thumbnailRule) {
        this.thumbnailRule = thumbnailRule;
    }

    public JsonRule getTitleRule() {
        return titleRule;
    }

    public void setTitleRule(JsonRule titleRule) {
        this.titleRule = titleRule;
    }

    public JsonRule getLinkRule() {
        return linkRule;
    }

    public void setLinkRule(JsonRule linkRule) {
        this.linkRule = linkRule;
    }

    public JsonRule getCategoryRule() {
        return categoryRule;
    }

    public void setCategoryRule(JsonRule categoryRule) {
        this.categoryRule = categoryRule;
    }




}
