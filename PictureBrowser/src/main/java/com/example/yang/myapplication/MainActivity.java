package com.example.yang.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.yang.myapplication.basic.BaseActivity;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.Rule;
import com.example.yang.myapplication.web.RuleAll;
import com.example.yang.myapplication.web.Website;


//开始界面 以后换
public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ToolBar
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //侧滑菜单
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView=(NavigationView)findViewById(R.id.nav_view_left) ;
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }
        //navView.setCheckedItem(R.id.poocg);//默认选中
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //点击item之后的操作
                drawerLayout.closeDrawers();
                return true;
            }
        });


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
        final Website DEVIANTART=new Website("deviantart","http://www.deviantart.com/browse/all/?order=67108864&offset=0",ruleDEVIANTART);


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
    //ToolBar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.setting:
                Toast.makeText(this,"Click Setting Button",Toast.LENGTH_SHORT).show();
                break;
            case R.id.download:
                Toast.makeText(this,"Click Download Button",Toast.LENGTH_SHORT).show();
                break;
            default:break;
        }
        return true;
    }
}
