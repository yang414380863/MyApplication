package com.example.yang.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.GetToolBarImg;
import com.example.yang.myapplication.web.JsonUtils;
import com.example.yang.myapplication.web.Website;
import com.example.yang.myapplication.web.WebsiteInit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.yang.myapplication.R.id.*;
import static com.example.yang.myapplication.ViewPicture.systemBar;
import static com.example.yang.myapplication.web.Browser.*;
import static com.example.yang.myapplication.web.JsonUtils.JsonToObject;
import static com.example.yang.myapplication.web.WebsiteInit.*;


//列表所在Activity
public class ListActivity extends AppCompatActivity {

    //瀑布流
    RecyclerView recyclerView;
    //侧滑菜单
    private DrawerLayout drawerLayout;
    NavigationView navViewRight;
    NavigationView navViewLeft;
    //广播接收器
    private LoadFinishReceiver receiver;
    //下拉刷新 监听器
    static SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    //标题栏
    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    //toolbar
    Toolbar toolbar;
    //获取用户
    private SharedPreferences pref;
    //写入订阅
    private SharedPreferences.Editor editor;

    final ListAdapter adapter=new ListAdapter(this);
    static ListAdapter adapter2;
    static int isRefreshing=0;
    static String refreshPlace;

    static Website[] websites;//=new Website[]{POOCG,DEVIANTART,LEIFENG, Qdaily,SSPAI};//先暂时这样写WebsiteList 以后再动态生成
    static String[] websitesString;//=new String[]{"POOCG","DEVIANTART","LEIFENG","Qdaily","SSPAI"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        editor=pref.edit();
        boolean haveInit=pref.getBoolean("haveInit",false);
        editor.putBoolean("haveInit",true);
        if (haveInit){
            //读"websitesString"个数->创建数组->String->Object
            String[] websitesStringNew=pref.getString("websitesString","").split(",");
            Website[] websitesNew=new Website[websitesStringNew.length];
            for (int i=0;i<websitesStringNew.length;i++){
                String websiteInJson=pref.getString(websitesStringNew[i],"");
                websitesNew[i]=JsonUtils.JsonToObject(websiteInJson);
            }
            websites=websitesNew;
            websitesString=websitesStringNew;
        }else {
            //第一次登录 赋初始值
            WebsiteInit.init();
            websites=new Website[]{POOCG,DEVIANTART,LEIFENG, Qdaily,SSPAI};
            websitesString=new String[]{"Poocg","Deviantart","雷锋网","好奇心日报","少数派"};
            String s=websitesString[0];
            for (int i=1;i<websitesString.length;i++){
                s=s+","+websitesString[i];
            }
            editor.putString("websitesString", s);
            for (int i=0;i<websites.length;i++){
                editor.putString(websites[i].getWebSiteName(),JsonUtils.ObjectToJson(websites[i]));
                editor.apply();
                LogUtil.d(pref.getString(websites[i].getWebSiteName(),""));
            }
            editor.apply();
        }


        //Log.d("JSON_________________", JsonUtils.ObjectToJson(Qdaily));
        //Log.d("JSON", JsonUtils.ObjectToJson(DEVIANTART));
        //final Website newWebsite=JsonUtils.JsonToObject(JsonUtils.ObjectToJson(POOCG));
        //Browser.sendRequest(newWebsite,"new");//从JSON格式转换为Website对象


        GetToolBarImg.sendRequest();
        adapter2=adapter;

        //点击推送通知后跳转的category
        //无推送时默认进入上次最后打开的
        //之前没打开过则打开默认的第一个
        if (getIntent().hasExtra("index")){
            Intent intent=getIntent();
            String index=intent.getExtras().getString("index");
            LogUtil.d("index:"+index);
            for (int i=0;i<websites.length;i++){
                if (websites[i].getCategory()==null){
                    if (websites[i].getIndexUrl().equals(index)){
                        Browser.sendRequest(websiteNow,"new");
                    }else {
                        //无Category/Index的跳过
                        continue;
                    }
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
            String indexNow=pref.getString("IndexNow","");
            if (indexNow.equals("")){
                Browser.sendRequest(websites[0],"new");//默认首页第一个
            }else {
                for (int i=0;i<websites.length;i++){
                    if (websites[i].getCategory()==null){
                        if (websites[i].getIndexUrl().equals(indexNow)){
                            Browser.sendRequest(websiteNow,"new");
                        }else {
                            //无Category/Index的跳过
                            continue;
                        }
                    }
                    for (int j=0;j<websites[i].getCategory().length;j++,j++){
                        if (websites[i].getCategory()[j+1].equals(indexNow)){
                            websiteNow=websites[i];
                            websiteNow.setIndexUrl(websites[i].getCategory()[j+1]);
                            Browser.sendRequest(websiteNow,"new");
                        }
                    }
                }
            }
        }

        // 沉浸式
        //final View systemBar = findViewById(collapsing_toolbar);
        //折叠标题栏
        imageView=(ImageView)findViewById(R.id.image_view);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Loading...");
        refreshPlace="top";
        //ToolBar
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //侧滑菜单
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navViewLeft=(NavigationView)findViewById(R.id.nav_view_left);
        navViewRight=(NavigationView)findViewById(R.id.nav_view_right);

        //用户信息
        final View navViewLeftHeader=navViewLeft.getHeaderView(0);
        ImageView leftBackground=(ImageView)navViewLeftHeader.findViewById(R.id.left_background);
        Glide
                .with(this)
                .load(R.drawable.leftheader)
                .centerCrop()
                .into(leftBackground);
        ImageView rightBackground=(ImageView)navViewRight.getHeaderView(0).findViewById(R.id.right_background);
        Glide
                .with(this)
                .load(R.drawable.rightheader)
                .centerCrop()
                .into(rightBackground);
        TextView usernameShow=(TextView)navViewLeftHeader.findViewById(R.id.username_show);
        ImageView userIcon=(ImageView)navViewLeftHeader.findViewById(R.id.user_icon);

        final String loginUsername=pref.getString("loginUsername","");
        if (!loginUsername.equals("")){
            //已登录
            usernameShow.setText("Welcome:"+loginUsername);
            userIcon.setImageResource(R.drawable.ic_power_settings_new_black_48dp);
            //同步云端订阅 _User->pref->_Installation
            final AVQuery<AVObject> query1 = new AVQuery<>("_User");
            query1.whereEqualTo("username", loginUsername);
            query1.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    for (AVObject item : list) {
                        if (item.get("mark")!=null){
                            editor=pref.edit();
                            editor.putString("mark",item.get("mark").toString());
                            editor.apply();
                        }
                    }
                }
            });
            final AVQuery<AVObject> query2 = new AVQuery<>("_Installation");
            query2.whereEqualTo("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
            query2.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    for (AVObject item : list) {
                        item.put("mark", pref.getString("mark",""));
                        item.saveInBackground();
                    }
                }
            });
        }else {
            //未登录
            usernameShow.setText("Welcome: visitor");
            userIcon.setImageResource(R.drawable.ic_account_circle_black_48dp);
            final AVQuery<AVObject> query2 = new AVQuery<>("_Installation");
            query2.whereEqualTo("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
            query2.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    for (AVObject item : list) {
                        item.put("mark", "");
                        item.saveInBackground();
                    }
                }
            });
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
                    editor.putString("mark","");
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
        final ImageButton addWebsite=(ImageButton)navViewLeftHeader.findViewById(R.id.add_website);
        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListActivity.this,AddWebsite.class);
                startActivityForResult(intent,1);
            }
        });
        final TextView addWebsiteText=(TextView)navViewLeftHeader.findViewById(R.id.add_website_text);
        addWebsiteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListActivity.this,AddWebsite.class);
                startActivity(intent);
            }
        });
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
        intentFilter.addAction("com.example.yang.myapplication.TOOL_BAR_LOAD_FINISH");
        intentFilter.addAction("com.example.yang.myapplication.ADD_FINISH");
        receiver=new LoadFinishReceiver();
        registerReceiver(receiver,intentFilter);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        //瀑布流
        recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
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
                    //systemBar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
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
                    //systemBar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
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
            if (intent.getAction().equals("com.example.yang.myapplication.TOOL_BAR_LOAD_FINISH")){
                Glide
                        .with(ListActivity.this)
                        .load(GetToolBarImg.imgSrc)
                        .error(R.drawable.toolbar)
                        .fitCenter()
                        .into(imageView);
            }else if (intent.getAction().equals("com.example.yang.myapplication.LOAD_FINISH")){
                if (intent.getExtras().getString("websiteIndex")!=null&&intent.getExtras().getString("websiteIndex").equals(websiteNow.getIndexUrl())){
                    collapsingToolbarLayout.setTitle(websiteNow.getWebSiteName());
                    LogUtil.d("finish refresh!");
                    adapter.getWebContents().clear();//要重新指向一次才能检测到刷新
                    adapter.getWebContents().addAll(webContentList);
                    if (refreshPlace.equals("top")){
                        recyclerView.smoothScrollToPosition(0);//回到顶端
                        adapter.notifyDataSetChanged();
                        snackbar = Snackbar.make(collapsingToolbarLayout, "Loading", Snackbar.LENGTH_INDEFINITE);
                        snackbar.getView().getBackground().setAlpha(100);
                        snackbar.setText("Finish Loading");
                        snackbar.setDuration(Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }else if (refreshPlace.equals("bottom")){
                        snackbar.setText("Finish Loading");
                        snackbar.setDuration(Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        adapter.notifyItemRangeInserted(webContentList.size(),webContentList.size()+sizeThisPage);
                    }
                    //动态生成侧滑菜单(Right)设置被选中item
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
                    //动态生成侧滑菜单(left)设置被选中item
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
                    //设置toolbar
                    Menu toolbarMenu= toolbar.getMenu();
                    if (isSubscribe(websiteNow.getIndexUrl())){
                        toolbarMenu.getItem(0).setIcon(R.drawable.ic_star_white_48dp);//换成已订阅的图标
                    }else{
                        toolbarMenu.getItem(0).setIcon(R.drawable.ic_star_border_white_48dp);//换成未订阅的图标
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    isRefreshing=0;
                }
            }else if(intent.getAction().equals("com.example.yang.myapplication.ADD_FINISH")){
                //动态生成侧滑菜单(left)设置被选中item
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
        Menu toolbarMenu= toolbar.getMenu();
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
            case R.id.subscribe:
                subscribe(pref.getString("loginUsername",""),websiteNow.getIndexUrl());//执行订阅/取消订阅操作
                if (isSubscribe(websiteNow.getIndexUrl())){
                    toolbarMenu.getItem(0).setIcon(R.drawable.ic_star_white_48dp);//换成已订阅的图标
                    Toast.makeText(this,"Subscribe Successful",Toast.LENGTH_SHORT).show();
                }else{
                    toolbarMenu.getItem(0).setIcon(R.drawable.ic_star_border_white_48dp);//换成未订阅的图标
                    Toast.makeText(this,"Unsubscribe Successful",Toast.LENGTH_SHORT).show();
                }
                break;
            default:break;
        }
        return true;
    }

    public void subscribe(String username,String websiteIndex){
        int hasMark=0;
        String[] strings=pref.getString("mark","").split(",");
        ArrayList<String> mark=new ArrayList<>();
        for (int i=0;i<strings.length;i++){
            if(strings[0].equals("")){
                break;
            }
            if (strings[i].equals(websiteIndex)){
                //如果已经mark订阅过了 则将其从订阅中删除
                hasMark=1;
                continue;
            }
            mark.add(strings[i]);
        }
        if (hasMark==0){
            //没有mark过,添加
            mark.add(websiteIndex);
        }
        String[] newStrings=mark.toArray(new String[mark.size()]);
        String newString="";
        for (int i=0;i<newStrings.length;i++){
            if (newString.equals("")){
                newString=newStrings[i];
            }else {
                newString=newString+","+newStrings[i];
            }
        }
        LogUtil.d(mark.size());
        editor=pref.edit();
        editor.putString("mark",newString);
        editor.apply();
        //上传服务器
        final String s=newString;
        final AVQuery<AVObject> query1 = new AVQuery<>("_User");
        query1.whereEqualTo("username", username);
        query1.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject item : list) {
                    item.put("mark", s);
                    item.saveInBackground();
                }
            }
        });
        final AVQuery<AVObject> query2 = new AVQuery<>("_Installation");
        query2.whereEqualTo("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
        query2.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject item : list) {
                    item.put("mark", s);
                    item.saveInBackground();
                }
            }
        });

        LogUtil.d("mark "+newString);
    }

    public boolean isSubscribe(String websiteIndex){
        boolean hasMark=false;
        String[] strings=pref.getString("mark","").split(",");
        for (int i=0;i<strings.length;i++){
            if (strings[i].equals(websiteIndex)){
                //如果已经mark订阅过了 则将其从订阅中删除
                hasMark=true;
            }
        }
        return hasMark;
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

    //双击返回退出
    //定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

}
