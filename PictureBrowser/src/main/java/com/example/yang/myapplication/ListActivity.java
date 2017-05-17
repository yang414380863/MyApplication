package com.example.yang.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.Rule;
import com.example.yang.myapplication.web.ItemRule;
import com.example.yang.myapplication.web.Website;

import static com.example.yang.myapplication.R.id.*;
import static com.example.yang.myapplication.web.Browser.sizeThisPage;
import static com.example.yang.myapplication.web.Browser.webContentList;
import static com.example.yang.myapplication.web.Browser.websiteNow;


//列表所在Activity
public class ListActivity extends AppCompatActivity {

    //侧滑菜单
    private DrawerLayout drawerLayout;
    //广播接收器
    private LoadFinishReceiver receiver;
    //下拉刷新 监听器
    static SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    //标题栏
    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    //侧滑菜单
    NavigationView navViewRight;
    NavigationView navViewLeft;
    //获取用户
    private SharedPreferences pref;

    final ListAdapter adapter=new ListAdapter(this);
    static ListAdapter adapter2;
    static int isRefreshing=0;
    static String refreshPlace;

    static ItemRule rulePOOCG=new ItemRule();
    final static Website POOCG=new Website("Poocg","https://www.poocg.com/works/index/type/new",rulePOOCG);
    static ItemRule ruleDEVIANTART=new ItemRule();
    final static Website DEVIANTART=new Website("Deviantart","http://www.deviantart.com/whats-hot/",ruleDEVIANTART);
    static ItemRule ruleUNSPLASH=new ItemRule();
    final static Website UNSPLASH=new Website("Unsplash","https://unsplash.com/",ruleUNSPLASH);
    static ItemRule ruleLEIFENG=new ItemRule();
    final static Website LEIFENG=new Website("雷锋网","http://www.leiphone.com/category/sponsor",ruleLEIFENG);
    static ItemRule ruleHAOQIXIN=new ItemRule();
    final static Website HAOQIXIN=new Website("雷锋网","http://www.leiphone.com/category/sponsor",ruleHAOQIXIN);

    final static Website[] websites=new Website[]{POOCG,DEVIANTART,UNSPLASH,LEIFENG};//先暂时这样写WebsiteList 以后再动态生成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        POOCG.setItemSelector("li:has(div.imgbox)");
        rulePOOCG.setLinkRule(new Rule("div.imgbox > a[href]","attr","href"));
        rulePOOCG.setThumbnailRule(new Rule("div.imgbox > a > img[src]","attr","src"));
        rulePOOCG.setTitleRule(new Rule("div.infobox > p.titles","text"));
        POOCG.setDetailItemSelector("img[style*=max-width]");
        rulePOOCG.setImgRule(new Rule("*","attr","src"
                ,"(https:\\/\\/imagescdn\\.poocg\\.me\\/uploadfile\\/photo\\/[0-9]{4}\\/[0-9]{1,2}\\/[a-z|0-9|_]+\\.[a-z]+)",new String[]{""}));
        POOCG.setNextPageRule(new Rule("a#pagenav","attr","href"));
        POOCG.setCategory(new String[]{"最新","https://www.poocg.com/works/index/type/new","新赞","https://www.poocg.com/works/index/type/love","热门","https://www.poocg.com/works/index/type/hot"
                ,"精华","https://www.poocg.com/works/index/type/best","推荐","https://www.poocg.com/works/index/type/rem"});

        DEVIANTART.setItemSelector("span[class*=thumb]:has(img[data-sigil=torpedo-img])");
        ruleDEVIANTART.setLinkRule(new Rule("a.torpedo-thumb-link","attr","href"));
        ruleDEVIANTART.setThumbnailRule(new Rule("a.torpedo-thumb-link > img[data-sigil=torpedo-img]","attr","src"));
        ruleDEVIANTART.setTitleRule(new Rule("span.info > span.title-wrap > span.title","text"));
        DEVIANTART.setDetailItemSelector("div[class=dev-view-deviation]");
        ruleDEVIANTART.setImgRule(new Rule("img[class=dev-content-full]","attr","src"));
        DEVIANTART.setNextPageRule(new Rule("a.selected","attr","href","(http:\\/\\/www\\.deviantart\\.com\\/[a-z|-]+\\/)()",new String[]{"?offset=","size"}));
        DEVIANTART.setCategory(new String[]{"Newest","http://www.deviantart.com/newest/","What's Hot","http://www.deviantart.com/whats-hot/"
                ,"Undiscovered","http://www.deviantart.com/undiscovered/","Popular 24 hours","http://www.deviantart.com/popular-24-hours/","Popular All Time","http://www.deviantart.com/popular-all-time/"});

        UNSPLASH.setItemSelector("div.y5w1y");
        ruleUNSPLASH.setLinkRule(new Rule("a[title]","attr","href","()(\\/\\?photo=[a-z|A-Z|0-9|-]+)",new String[]{"https://unsplash.com",""}));
        ruleUNSPLASH.setThumbnailRule(new Rule("a[href]","attr","style","(https:\\/\\/images\\.unsplash\\.com\\/[a-z|0-9|-|-|?|=|&|,|\\/]+)",new String[]{""}));
        ruleUNSPLASH.setTitleRule(new Rule("a[class=cV68d]","attr","title"));
        UNSPLASH.setDetailItemSelector("div.RN0KT");
        ruleUNSPLASH.setImgRule(new Rule("*","attr","style","(https:\\/\\/images\\.unsplash\\.com\\/[a-z|0-9|&||\\/|-]+)",new String[]{""}));
        UNSPLASH.setCategory(new String[]{"home","https://unsplash.com/","New","https://unsplash.com/new"});
        //ruleUNSPLASH.setNextPageRule(new Rule());没写下一页RULE

        LEIFENG.setItemSelector("li > div.box:has(div.img)");
        ruleLEIFENG.setLinkRule(new Rule("div.img > a[target]","attr","href"));
        ruleLEIFENG.setThumbnailRule(new Rule("div.img > a[target] > img.lazy","attr","data-original"));
        ruleLEIFENG.setTitleRule(new Rule("div.img > a[target] > img.lazy","attr","title"));
        LEIFENG.setDetailItemSelector("div[class=lph-article-comView] > p");
        ruleLEIFENG.setImgRule(new Rule("p img[alt]","attr","src"));
        ruleLEIFENG.setArticleRule(new Rule("p","text"));
        LEIFENG.setNextPageRule(new Rule("div.lph-page > a.next","attr","href"));
        LEIFENG.setCategory(new String[]{"人工智能","http://www.leiphone.com/category/ai","智能驾驶","http://www.leiphone.com/category/transportation","网络安全","http://www.leiphone.com/category/letshome"
                ,"AR/VR","http://www.leiphone.com/category/arvr","机器人","http://www.leiphone.com/category/robot","Fintect","http://www.leiphone.com/category/fintech","物联网","http://www.leiphone.com/category/iot"
                ,"未来医疗","http://www.leiphone.com/category/aihealth","只能硬件","http://www.leiphone.com/category/weiwu","AI+","http://www.leiphone.com/category/aijuejinzhi"});




        //Log.d("JSON", JsonUtils.ObjectToJson(POOCG));
        //Log.d("JSON", JsonUtils.ObjectToJson(DEVIANTART));
        //final Website newWebsite=JsonUtils.JsonToObject(JsonUtils.ObjectToJson(POOCG));
        //Browser.sendRequest(newWebsite,"new");//从JSON格式转换为Website对象

        adapter2=adapter;

        //点击推送通知后跳转的category
        if (getIntent().hasExtra("index")){
            Intent intent=getIntent();
            String index=intent.getExtras().getString("index");
            LogUtil.d("index:"+index);
            for (int i=0;i<websites.length;i++){
                if (websites[i].getCategory()==null){
                    continue;
                }
                for (int j=0;j<websites[i].getCategory().length;j++,j++){
                    if (websites[i].getCategory()[j+1].equals(index)){
                        websiteNow=websites[i];
                        websiteNow.setIndexUrl(websites[i].getCategory()[j+1]);
                        Browser.sendRequest(websiteNow,"new");
                    }
                }
            }
        }else {
            Browser.sendRequest(POOCG,"new");//默认首页 无推送时默认打开
        }

        // 沉浸式
        final View systemBar = findViewById(collapsing_toolbar);
        //折叠标题栏
        imageView=(ImageView)findViewById(R.id.image_view);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Loading...");
        refreshPlace="top";
        //ToolBar
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //侧滑菜单
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navViewLeft=(NavigationView)findViewById(R.id.nav_view_left);
        navViewRight=(NavigationView)findViewById(R.id.nav_view_right);
        //用户信息
        View navViewLeftHeader=navViewLeft.getHeaderView(0);
        TextView usernameShow=(TextView)navViewLeftHeader.findViewById(R.id.username_show);
        ImageView userIcon=(ImageView)navViewLeftHeader.findViewById(R.id.user_icon);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        final String loginUsername=pref.getString("loginUsername","");
        if (!loginUsername.equals("")){
            //已登录
            usernameShow.setText("Welcome:"+loginUsername);
            userIcon.setImageResource(R.drawable.ic_account_circle_black_48dp);
        }else {
            //未登录
            usernameShow.setText("Welcome: visitor");
            userIcon.setImageResource(R.drawable.ic_account_circle_grey600_48dp);
        }

        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListActivity.this,Login.class);
                if (!loginUsername.equals("")){
                    //点击ICON注销
                    SharedPreferences.Editor editor;
                    editor=pref.edit();
                    editor.putString("loginUsername","");
                    editor.apply();
                    intent.putExtra("isLogout",true);
                    startActivity(intent);
                    finish();
                }else {
                    //点击ICON登录
                    startActivity(intent);
                    finish();
                }
            }
        });
        //ToolBar 用于打开侧滑菜单的按钮
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }
        //动态生成侧滑菜单(Left)
        Menu menuLeft=navViewLeft.getMenu();
        menuLeft.clear();
        if (websites.length!=0){
            for (int i=0;i<websites.length;i++){
                menuLeft.add(group_left,i,i,websites[i].getWebSiteName());
            }
        }
        navViewLeft.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //点击左侧item之后的操作
                adapter.getWebContents().clear();//要重新指向一次才能检测到刷新
                adapter.notifyDataSetChanged();
                drawerLayout.closeDrawers();
                int position=0;
                for (int i=0;i<websites.length;i++){
                    if (item.getTitle().equals(websites[i].getWebSiteName())){
                        position=i;
                    }
                }
                Browser.sendRequest(websites[position],"new");
                swipeRefreshLayout.setRefreshing(true);
                isRefreshing=1;
                refreshPlace="top";
                Log.d("refresh","change website refresh!");
                collapsingToolbarLayout.setTitle(websiteNow.getWebSiteName());
                return true;
            }
        });
        navViewRight.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //点击右侧item之后的操作
                adapter.getWebContents().clear();//要重新指向一次才能检测到刷新
                adapter.notifyDataSetChanged();
                drawerLayout.closeDrawers();
                int positionOfCategory=0;
                if (websiteNow.getCategory()!=null){
                    for (int i=0;i<websiteNow.getCategory().length/2;i++){
                        if (item.getTitle().equals(websiteNow.getCategory()[2*i])){
                            positionOfCategory=2*i+1;
                        }
                    }
                    websiteNow.setIndexUrl(websiteNow.getCategory()[positionOfCategory]);
                    Browser.sendRequest(websiteNow,"new");
                    swipeRefreshLayout.setRefreshing(true);
                    isRefreshing=1;
                    refreshPlace="top";
                    Log.d("refresh","change category refresh!");
                    collapsingToolbarLayout.setTitle(websiteNow.getWebSiteName());
                }
                return true;
            }
        });
        //广播接收器
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.yang.myapplication.LOAD_FINISH");
        receiver=new LoadFinishReceiver();
        registerReceiver(receiver,intentFilter);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        //瀑布流
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
        StaggeredGridLayoutManager layoutManager=new
                StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);//列数2
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //设置loading颜色 最多4个
        //swipeRefreshLayout.setColorSchemeColors();
        //首次进入先显示加载中
        swipeRefreshLayout.setRefreshing(true);
        //手动下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取最新数据并刷新
                if (isRefreshing==0){
                    isRefreshing=1;
                    refreshPlace="top";
                    Browser.sendRequest(websiteNow,"top");
                    Log.d("refresh","top is going to refresh!");
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int SCROLL_STATE_IDLE=0;//表示屏幕已停止。屏幕停止滚动时为0
                int SCROLL_STATE_TOUCH_SCROLL=1;//表示正在滚动。当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1
                int SCROLL_STATE_FLING=2;//手指做了抛的动作（手指离开屏幕前，用力滑了一下，屏幕产生惯性滑动）
                //沉浸式
                if(newState == SCROLL_STATE_FLING){
                    /*//隐藏?需要
                    systemBar.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//显示导航栏
                                    //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//显示状态栏
                                    //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//不显示导航栏
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN//不显示状态栏
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);//沉浸模式
                    */
                }
                if(newState == SCROLL_STATE_IDLE){
                    systemBar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
                //划到底部刷新
                if(!recyclerView.canScrollVertically(1)){//检测划到了底部
                    if (isRefreshing==0){
                        isRefreshing=1;
                        refreshPlace="bottom";
                        Log.d("refresh","bottom is going to refresh!");
                        Browser.nextPage();//发送加载下一页的请求
                        snackbar = Snackbar.make(collapsingToolbarLayout, "Loading", Snackbar.LENGTH_INDEFINITE);
                        snackbar.getView().getBackground().setAlpha(100);
                        snackbar.show();
                    }
                }else if(!recyclerView.canScrollVertically(-1)) {//检测划到了顶部
                    systemBar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }

            }
        });

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }
    class LoadFinishReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getString("websiteIndex")!=null&&intent.getExtras().getString("websiteIndex").equals(websiteNow.getIndexUrl())){
                collapsingToolbarLayout.setTitle(websiteNow.getWebSiteName());
                Glide
                        .with(ListActivity.this)
                        .load(R.mipmap.ic_launcher)
                        .fitCenter()
                        .into(imageView);
                LogUtil.d("finish refresh!");
                adapter.getWebContents().clear();//要重新指向一次才能检测到刷新
                adapter.getWebContents().addAll(webContentList);
                if (refreshPlace=="top"){
                    adapter.notifyDataSetChanged();
                }else if (refreshPlace=="bottom"){
                    snackbar.setText("Finish Loading");
                    snackbar.setDuration(Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    adapter.notifyItemRangeInserted(webContentList.size(),webContentList.size()+sizeThisPage);
                }
                //动态生成侧滑菜单(Right)
                Menu menuRight=navViewRight.getMenu();
                menuRight.clear();
                if (websiteNow.getCategory()!=null){
                    for (int i=0;i<websiteNow.getCategory().length/2;i++){
                        menuRight.add(group_right,i,i,websiteNow.getCategory()[2*i]);
                        menuRight.findItem(i).setCheckable(true);
                        if (websiteNow.getCategory()[2*i+1].equals(websiteNow.getIndexUrl())){
                            navViewRight.setCheckedItem(menuRight.findItem(i).getItemId());
                        }
                    }
                }
                //设置左侧滑菜单的被选中item
                Menu menuLeft=navViewLeft.getMenu();
                menuLeft.clear();
                if (websites.length!=0){
                    for (int i=0;i<websites.length;i++){
                        menuLeft.add(group_left,i,i,websites[i].getWebSiteName());
                        menuLeft.findItem(i).setCheckable(true);
                        if (websites[i].getWebSiteName().equals(websiteNow.getWebSiteName())){
                            navViewLeft.setCheckedItem(menuLeft.findItem(i).getItemId());
                        }
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                isRefreshing=0;
            }
        }
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

    public static void forPush(String index){
        adapter2.getWebContents().clear();//要重新指向一次才能检测到刷新
        adapter2.notifyDataSetChanged();

        for (int i=0;i<websites.length;i++){
            if (websites[i].getCategory()==null){
                continue;
            }
            for (int j=0;j<websites[i].getCategory().length;j+=2){
                if (websites[i].getCategory()[j+1].equals(index)){
                    websiteNow=websites[i];
                    websiteNow.setIndexUrl(websites[i].getCategory()[j+1]);
                    swipeRefreshLayout.setRefreshing(true);
                    Browser.sendRequest(websiteNow,"new");
                }
            }
        }
    }
}
