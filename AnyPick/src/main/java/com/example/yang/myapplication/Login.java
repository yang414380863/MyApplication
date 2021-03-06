package com.example.yang.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.bumptech.glide.Glide;
import com.example.yang.myapplication.basic.MyApplication;

import java.util.List;

import static com.bumptech.glide.Glide.with;
import static com.example.yang.myapplication.ListActivity.forPush;
import static com.example.yang.myapplication.R.id.username;


public class Login extends AppCompatActivity {

    private EditText accountEdit;
    private EditText passwordEdit;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        accountEdit=(EditText)findViewById(username);
        passwordEdit=(EditText)findViewById(R.id.password);

        //push相关
        final Intent intent=new Intent(Login.this,ListActivity.class);
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(this, ListActivity.class);
        // 保存 installation 到服务器
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
       });

        ImageView loginBackground=(ImageView)findViewById(R.id.login_background);
        Glide
                .with(this)
                .load(R.drawable.login)
                .centerCrop()
                .into(loginBackground);

        if (getIntent().hasExtra("index")){
            //发送一个点击了推送的广播,使不需要的Activity关闭
            Intent intent3=new Intent("com.example.yang.myapplication.CLICK_PUSH");
            MyApplication.getContext().sendBroadcast(intent3);
            Intent intent2=getIntent();
            String index=intent2.getExtras().getString("index");
            intent.putExtra("index",index);//如果List之前未启动则通过intent获取推送index
            forPush(index);//否则通过forPush()获取index
        }

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass=(CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember=pref.getBoolean("remember_password",false);
        boolean isLogout;
        //检测是否是因注销而打开的Login Activity
        try{
            Intent intent2=getIntent();
            isLogout=intent2.getExtras().getBoolean("isLogout");
        }catch (Exception e){
            isLogout=false;
        }
        if (isLogout){
            //是注销
            //清空订阅

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

            if (isRemember){
                //自动填写帐号密码
                String username=pref.getString("username","");
                String password=pref.getString("password","");
                accountEdit.setText(username);
                passwordEdit.setText(password);
                rememberPass.setChecked(true);
            }
        }else {
            //是正常打开APP 如果之前已经登录则直接跳转List Activity
            /*默认要保存username么???
            String username=pref.getString("username","");
            accountEdit.setText(username);
            */
            String loginUsername=pref.getString("loginUsername","");
            if (!loginUsername.equals(""))//loginUsername=""即未登录
            {
                startActivity(intent);
                finish();
            }
        }

        Button login=(Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String username=accountEdit.getText().toString();
                final String password=passwordEdit.getText().toString();
                if (username.equals("")||password.equals("")){
                    Toast.makeText(Login.this,"username/password is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                //如果帐号密码都对,就成功跳转.否则尝试注册新用户
                AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if (e == null){
                            //帐号密码正确
                            editor=pref.edit();
                            if (rememberPass.isChecked()){
                                //保存帐号密码
                                editor.putBoolean("remember_password",true);
                                editor.putString("username",username);
                                editor.putString("password",password);
                            }else{
                                //清除帐号密码
                                editor.remove("username");
                                editor.remove("password");
                            }
                            editor.putString("loginUsername",username);
                            editor.apply();

                            //登录跳转
                            startActivity(intent);
                            finish();
                        }else {
                            //登录失败,注册新账号
                            AVUser user = new AVUser();// 新建 AVUser 对象实例
                            user.setUsername(username);// 设置用户名
                            user.setPassword(password);// 设置密码
                            user.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        // 注册成功
                                        editor=pref.edit();
                                        if (rememberPass.isChecked()){
                                            //保存帐号密码
                                            editor.putBoolean("remember_password",true);
                                            editor.putString("username",username);
                                            editor.putString("password",password);
                                        }else{
                                            //清除帐号密码
                                            editor.remove("username");
                                            editor.remove("password");
                                        }
                                        editor.putString("loginUsername",username);
                                        editor.apply();

                                        Toast.makeText(Login.this,"register success",Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // 失败的原因可能有多种，常见的是用户名已经存在。
                                        Toast.makeText(Login.this,"username has exist/password is incorrect",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        Button visitor=(Button)findViewById(R.id.visitor);
        visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
            }
        });
    }
}