package com.example.yang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.Rule;
import com.example.yang.myapplication.web.RuleAll;
import com.example.yang.myapplication.web.Website;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RuleAll rulePOOCG=new RuleAll();
        rulePOOCG.setLinkRule(new Rule("div.imgbox > a[href]","attr","href"));
        rulePOOCG.setThumbnailRule(new Rule("div.imgbox > a > img[src]","attr","src"));
        rulePOOCG.setTitleRule(new Rule("div.infobox > p.titles","text"));
        rulePOOCG.setImgRule(new Rule("div.wrapper > div > ul > div[class=workPage-images] > img[src]","attr","src"
                ,"(https:\\/\\/imagescdn\\.poocg\\.me\\/uploadfile\\/photo\\/[0-9]{4}\\/[0-9]{1,2}\\/[a-z|0-9|_]+\\.[a-z]+)",new String[]{""}));
        rulePOOCG.setNextPageRule(new Rule("a#pagenav","attr","href"));
        rulePOOCG.setNextPageDetailRule(new Rule("a[id=pagenav]","attr","href"));
        final Website POOCG=new Website("poocg","https://www.poocg.com/works/index/type/new",rulePOOCG);
        POOCG.setCategory(new String[]{"最新","https://www.poocg.com/works/index/type/new","新赞","https://www.poocg.com/works/index/type/love","热门","https://www.poocg.com/works/index/type/hot"
                ,"精华","https://www.poocg.com/works/index/type/best","推荐","https://www.poocg.com/works/index/type/rem"});

        RuleAll ruleDEVIANTART=new RuleAll();
        ruleDEVIANTART.setLinkRule(new Rule("span[class*=thumb] > a","attr","href"));
        ruleDEVIANTART.setThumbnailRule(new Rule("span[class*=thumb] > a > img[data-sigil=torpedo-img]","attr","src"));
        ruleDEVIANTART.setTitleRule(new Rule("span[class*=thumb] > span.info > span.title-wrap > span.title","text"));
        ruleDEVIANTART.setImgRule(new Rule("div.dev-view-deviation > img[class=dev-content-full]","attr","src"));
        ruleDEVIANTART.setNextPageRule(new Rule("a.selected","attr","href","(http:\\/\\/www\\.deviantart\\.com\\/browse\\/all\\/\\?order=\\d+)()",new String[]{"&offset=","size"}));
        final Website DEVIANTART=new Website("deviantart","http://www.deviantart.com/browse/all/?order=67108864",ruleDEVIANTART);
        DEVIANTART.setCategory(new String[]{"Newest","http://www.deviantart.com/browse/all/?order=5","What's Hot","http://www.deviantart.com/browse/all/?order=67108864"
                ,"Undiscovered","http://www.deviantart.com/browse/all/?order=134217728","Popular 24 hours","http://www.deviantart.com/browse/all/?order=11","Popular All Time","http://www.deviantart.com/browse/all/?order=9"});

        RuleAll ruleUNSPLASH=new RuleAll();
        ruleUNSPLASH.setLinkRule(new Rule("div.y5w1y > a","attr","href","()(\\/\\?photo=[a-z|A-Z|0-9|-]+)",new String[]{"https://unsplash.com",""}));
        ruleUNSPLASH.setThumbnailRule(new Rule("div.y5w1y > a","attr","style","(https:\\/\\/images.unsplash\\.com\\/photo\\-[a-z|0-9|-|-|?|=|&|,]+)",new String[]{""}));
        ruleUNSPLASH.setTitleRule(new Rule("a[class=_3XzpS _3myVE _2zITg]","text","()([a-z|A-Z|\\s]+)",new String[]{"Photo By: ",""}));
        ruleUNSPLASH.setImgRule(new Rule("div.RN0KT","attr","style","(https:\\/\\/images.unsplash\\.com\\/photo\\-[a-z|0-9|-|-|?|=|&]+)\\?",new String[]{""}));
        //ruleUNSPLASH.setNextPageRule(new Rule());没写下一页RULE
        final Website UNSPLASH=new Website("unsplash","https://unsplash.com/",ruleUNSPLASH);


        Button button1 = (Button) findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.sendRequest(DEVIANTART,"new");
                Intent intent=new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }

        });
        Button button2 = (Button) findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Browser.sendRequest(POOCG,"new");
                Intent intent=new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }

        });
    }
}
