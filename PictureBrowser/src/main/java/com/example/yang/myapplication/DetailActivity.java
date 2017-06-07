package com.example.yang.myapplication;


import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.web.Browser;
import com.example.yang.myapplication.web.WebItem;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.example.yang.myapplication.R.id.collapsing_toolbar;
import static com.example.yang.myapplication.R.id.toolbar2;
import static com.example.yang.myapplication.web.Browser.webContentList;
import static com.example.yang.myapplication.web.Browser.websiteNow;


//详情所在Activity
public class DetailActivity extends AppCompatActivity {

    //广播接收器
    private Receiver receiver;
    //下拉刷新 监听器
    SwipeRefreshLayout swipeRefreshLayout;
    static int isRefreshing=0;
    //瀑布流
    final DetailAdapter adapter=new DetailAdapter(this);
    static int positionNow;
    //标题栏
    ImageView imageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    //toolbar
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        //标题栏
        imageView=(ImageView)findViewById(R.id.image_view);
        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Loading...");
        //ToolBar
        toolbar=(Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        //获取具体是哪一个item
        Intent intent=getIntent();
        positionNow=intent.getExtras().getInt("position");
        //瀑布流
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
        StaggeredGridLayoutManager layoutManager=new
                StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //广播接收器
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.yang.myapplication.LOAD_FINISH_DETAIL");
        intentFilter.addAction("com.example.yang.myapplication.CLICK_PUSH");
        receiver=new Receiver();
        registerReceiver(receiver,intentFilter);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        //首次进入先显示加载中
        swipeRefreshLayout.setRefreshing(true);
        //手动下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取最新数据并刷新
                if (isRefreshing==0){
                    isRefreshing=1;
                    Browser.sendRequestDetail(positionNow,"top");
                    Log.d("refresh","detail is going to refresh!");
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
    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.yang.myapplication.LOAD_FINISH_DETAIL")){
                if (intent.getExtras().getInt("position")==positionNow){
                    WebItem webContent= webContentList.get(positionNow);
                    collapsingToolbarLayout.setTitle(webContent.getTitle());
                    for (int i=0;i<webContent.getImg().size();i++){
                        if (!webContent.getImg().get(i).equals("")){
                            Glide
                                    .with(DetailActivity.this)
                                    .load(webContent.getImg().get(i))
                                    .fitCenter()
                                    .into(imageView);
                            imageView.setImageAlpha(150);
                            break;
                        }
                    }
                    Log.d("refresh","finish refresh!");
                    adapter.getUrls().clear();//要重新指向一次才能检测到刷新
                    adapter.getUrls().addAll(webContentList.get(positionNow).getImg());
                    adapter.getTexts().clear();//要重新指向一次才能检测到刷新
                    adapter.getTexts().addAll(webContentList.get(positionNow).getArticle());
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    isRefreshing=0;
                }
            }
            if (intent.getAction().equals("com.example.yang.myapplication.CLICK_PUSH")){
                LogUtil.d("DetailActivity finish");
                finish();
            }

        }
    }
    //ToolBar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:{
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Share Title: "+webContentList.get(positionNow).getTitle()+"\nURL: "+webContentList.get(positionNow).getLink());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            }
            case R.id.open_in_browser:{
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(webContentList.get(positionNow).getLink());
                intent.setData(content_url);
                startActivity(intent);
                break;
            }
            case R.id.copy_link:{
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(webContentList.get(positionNow).getLink());
                Toast.makeText(this,"Copy successful",Toast.LENGTH_SHORT).show();
                break;
            }

            default:break;
        }
        return true;
    }

}
