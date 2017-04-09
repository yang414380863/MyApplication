package com.example.yang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yang.myapplication.basic.BaseActivity;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.Rule;
import com.example.yang.myapplication.web.RuleAll;
import com.example.yang.myapplication.web.Website;


//开始界面 以后换
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RuleAll rulePOOCG=new RuleAll();
        rulePOOCG.setLinkRule(new Rule("div.imgbox > a[href]","attr","href"));
        rulePOOCG.setThumbnailRule(new Rule("div.imgbox > a > img[src]","attr","src"));
        rulePOOCG.setTitleRule(new Rule("div.imgbox > a > img[src]","attr","alt"));
        rulePOOCG.setImgRule(new Rule("div.wrapper > div > ul > li > a > img[src]","attr","src"));
        rulePOOCG.setNextPageRule(new Rule("a#pagenav","attr","href"));
        rulePOOCG.setNextPageDetailRule(new Rule("a#pagenav","attr","href"));
        final Website POOCG=new Website("poocg","http://www.poocg.com/works/index",rulePOOCG);


        RuleAll ruleDEVIANTART=new RuleAll();
        ruleDEVIANTART.setLinkRule(new Rule("span[class*=thumb] > a","attr","href"));
        ruleDEVIANTART.setThumbnailRule(new Rule("span[class*=thumb] > a > img[data-sigil=torpedo-img]","attr","src"));
        ruleDEVIANTART.setTitleRule(new Rule("span[class*=thumb] > span.info > span.title-wrap > span.title","text"));
        ruleDEVIANTART.setImgRule(new Rule("div.dev-view-deviation > img[class=dev-content-full]","attr","src"));
        String[] replace={"size"};
        ruleDEVIANTART.setNextPageRule(new Rule("(http:\\/\\/www\\.deviantart\\.com\\/browse\\/all\\/\\?order=\\d+&offset=)\\d+",replace));
        final Website DEVIANTART=new Website("deviantart","http://www.deviantart.com/browse/all/?order=5&offset=0",ruleDEVIANTART);


/**
        RuleAll ruleUNSPLASH=new RuleAll();
        ruleUNSPLASH.setLinkRule(new Rule("span[class*=thumb]","href"));
        ruleUNSPLASH.setThumbnailRule(new Rule("div[id^=grid] > div > div > a","src"));
        ruleUNSPLASH.setImgRule(new Rule("div.dev-view-deviation > img[class=dev-content-full]","src"));
        ruleUNSPLASH.setNextPageRule(new Rule("a#pagenav","href"));
        final Website UNSPLASH=new Website("Unsplash","https://unsplash.com/",ruleUNSPLASH);
*/
        Button button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.sendRequest(DEVIANTART);
                Intent intent=new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }

        });
        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.sendRequest(POOCG);
                Intent intent=new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }

        });
    }

}
