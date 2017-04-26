package com.example.yang.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.JsonUtils;
import com.example.yang.myapplication.web.Rule;
import com.example.yang.myapplication.web.RuleAll;
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
    SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    //标题栏
    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    //侧滑菜单
    NavigationView navViewRight;

    final ListAdapter adapter=new ListAdapter(this);
    static int isRefreshing=0;
    static String refreshPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

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


        Log.d("JSON", JsonUtils.ObjectToJson(POOCG));
        final Website newWebsite=JsonUtils.JsonToObject(JsonUtils.ObjectToJson(POOCG));
        //Browser.sendRequest(newWebsite,"new");//从JSON格式转换为Website对象
        Browser.sendRequest(POOCG,"new");//首页 进去先加载这个

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
        NavigationView navViewLeft=(NavigationView)findViewById(R.id.nav_view_left);
        navViewRight=(NavigationView)findViewById(R.id.nav_view_right);
        //ToolBar 用于打开侧滑菜单的按钮
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }

        //navViewLeft.setCheckedItem(poocg);//默认选中
        navViewLeft.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //点击item之后的操作
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case poocg:
                        Browser.sendRequest(POOCG,"new");
                        break;
                    case deviantart:
                        Browser.sendRequest(DEVIANTART,"new");
                        break;
                    case unsplash:
                        Browser.sendRequest(UNSPLASH,"new");
                        break;
                    default:return true;
                }
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
                //点击item之后的操作
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
            if (intent.getExtras().getString("websiteName")!=null&&intent.getExtras().getString("websiteName").equals(websiteNow.getWebSiteName())){
                collapsingToolbarLayout.setTitle(websiteNow.getWebSiteName());
                Glide
                        .with(ListActivity.this)
                        .load(R.mipmap.ic_launcher)
                        .fitCenter()
                        .into(imageView);
                Log.d("refresh","finish refresh!");
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
}
