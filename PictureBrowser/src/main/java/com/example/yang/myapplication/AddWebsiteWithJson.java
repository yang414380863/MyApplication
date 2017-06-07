package com.example.yang.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.basic.MyApplication;
import com.example.yang.myapplication.web.JsonUtils;
import com.example.yang.myapplication.web.Website;

import static com.example.yang.myapplication.ListActivity.websitesString;
import static com.example.yang.myapplication.ListActivity.websites;

public class AddWebsiteWithJson extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_website_with_json);


        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar4);
        toolbar.setTitle("Add Website With Json");
        setSupportActionBar(toolbar);

        final EditText editText=(EditText)findViewById(R.id.json_string);
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.finish_add_website);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String JsonString=editText.getText().toString();
                Website websiteNew=JsonUtils.JsonToObject(JsonString);

                if (websiteNew==null){
                    finish();
                    return;
                }

                String[] websitesStringNew=new String[websitesString.length+1];
                for (int i=0;i<websitesString.length;i++){
                    websitesStringNew[i]=websitesString[i];
                }
                websitesStringNew[websitesString.length]=websiteNew.getWebSiteName();
                //websitesStringNew个数=旧的+1->替换websitesString->存到"websitesString"
                pref= PreferenceManager.getDefaultSharedPreferences(AddWebsiteWithJson.this);
                editor=pref.edit();
                String s=websitesStringNew[0];
                for (int i=1;i<websitesStringNew.length;i++){
                    s=s+","+websitesStringNew[i];
                }
                editor.putString("websitesString",s);
                editor.putString(websiteNew.getWebSiteName(),JsonUtils.ObjectToJson(websiteNew));
                editor.apply();
                for (int i=0;i<websitesStringNew.length;i++){
                    LogUtil.d(pref.getString(websitesStringNew[i],""));
                }


                String[] websitesStringNew2=pref.getString("websitesString","").split(",");
                Website[] websitesNew2=new Website[websitesStringNew2.length];
                for (int i=0;i<websitesStringNew2.length;i++){
                    String websiteInJson=pref.getString(websitesStringNew2[i],"");
                    websitesNew2[i]=JsonUtils.JsonToObject(websiteInJson);
                }
                websites=websitesNew2;
                websitesString=websitesStringNew2;
                for (int i=0;i<websitesStringNew2.length;i++){
                    LogUtil.d(pref.getString(websitesStringNew2[i],""));
                }
                //发送一个List更新了的广播
                Intent intent=new Intent("com.example.yang.myapplication.ADD_FINISH");
                MyApplication.getContext().sendBroadcast(intent);
                finish();
            }
        });
    }
    //ToolBar
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar4,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:{
                Intent intent=new Intent(AddWebsiteWithJson.this,AddWebsite.class);
                startActivity(intent);
                finish();
                break;
            }
            default:break;
        }
        return true;
    }
}
