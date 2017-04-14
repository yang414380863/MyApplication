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
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.Rule;
import com.example.yang.myapplication.web.RuleAll;
import com.example.yang.myapplication.web.Website;

import static com.example.yang.myapplication.R.id.collapsing_toolbar;
import static com.example.yang.myapplication.R.id.deviantart;
import static com.example.yang.myapplication.R.id.poocg;
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

    final ListAdapter adapter=new ListAdapter(this);
    static int isRefreshing=0;
    static String refreshPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        //final View systemBar = findViewById(R.id.content);

        RuleAll rulePOOCG=new RuleAll();
        rulePOOCG.setLinkRule(new Rule("div.imgbox > a[href]","attr","href"));
        rulePOOCG.setThumbnailRule(new Rule("div.imgbox > a > img[src]","attr","src"));
        rulePOOCG.setTitleRule(new Rule("div.imgbox > a > img[src]","attr","alt"));
        rulePOOCG.setImgRule(new Rule("div.wrapper > div > ul > li > a > img[src]","attr","src","(https:\\/\\/imagescdn\\.poocg\\.me\\/uploadfile\\/photo\\/[0-9]{4}\\/[0-9]{1,2}\\/\\d+\\.[a-z]+)\\!photo\\.middle\\.[a-z]+",new String[]{""}));
        rulePOOCG.setNextPageRule(new Rule("a#pagenav","attr","href"));
        rulePOOCG.setNextPageDetailRule(new Rule("a#pagenav","attr","href"));
        final Website POOCG=new Website("poocg","http://www.poocg.com/works/index",rulePOOCG);

        RuleAll ruleDEVIANTART=new RuleAll();
        ruleDEVIANTART.setLinkRule(new Rule("span[class*=thumb] > a","attr","href"));
        ruleDEVIANTART.setThumbnailRule(new Rule("span[class*=thumb] > a > img[data-sigil=torpedo-img]","attr","src"));
        ruleDEVIANTART.setTitleRule(new Rule("span[class*=thumb] > span.info > span.title-wrap > span.title","text"));
        ruleDEVIANTART.setImgRule(new Rule("div.dev-view-deviation > img[class=dev-content-full]","attr","src"));
        ruleDEVIANTART.setNextPageRule(new Rule("a.selected","attr","href","(http:\\/\\/www\\.deviantart\\.com\\/browse\\/all\\/\\?order=\\d+)()",new String[]{"&offset=","size"}));
        final Website DEVIANTART=new Website("deviantart","http://www.deviantart.com/browse/all/?order=67108864&offset=0",ruleDEVIANTART);



        //折叠标题栏
        imageView=(ImageView)findViewById(R.id.image_view);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(collapsing_toolbar);

        refreshPlace="top";

        //侧滑菜单
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView=(NavigationView)findViewById(R.id.nav_view_left) ;
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher_round);
        }
        navView.setCheckedItem(poocg);//默认选中
        Browser.sendRequest(POOCG,"new");
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

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
                    default:break;
                }
                swipeRefreshLayout.setRefreshing(true);
                isRefreshing=1;
                refreshPlace="top";
                Log.d("refresh","change website refresh!");
                collapsingToolbarLayout.setTitle(websiteNow.getWebSiteName());
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
                /*沉浸式
                    if(newState ==SCROLL_STATE_FLING){
                    systemBar.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//显示导航栏
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN//显示状态栏
                                    //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION//不显示导航栏
                                    //| View.SYSTEM_UI_FLAG_FULLSCREEN//不显示状态栏
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE);//沉浸模式
                }*/
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
                    /*systemBar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);*/
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
                swipeRefreshLayout.setRefreshing(false);
                isRefreshing=0;
            }
        }
    }
}
