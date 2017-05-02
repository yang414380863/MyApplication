package com.example.yang.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.SignUpCallback;

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
        setContentView(R.layout.activity_login);
        accountEdit=(EditText)findViewById(username);
        passwordEdit=(EditText)findViewById(R.id.password);

        pref= PreferenceManager.getDefaultSharedPreferences(this);
        rememberPass=(CheckBox)findViewById(R.id.remember_pass);
        boolean isRemember=pref.getBoolean("remember_password",false);
        boolean isLogout;
        //检测是否是因注销而打开的Login Activity
        try{
            Intent intent=getIntent();
            isLogout=intent.getExtras().getBoolean("isLogout");
        }catch (Exception e){
            isLogout=false;
        }
        if (isLogout){
            //是注销
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
            String loginUsername=pref.getString("loginUsername","");
            if (!loginUsername.equals(""))
            {
                Intent intent2=new Intent(Login.this,ListActivity.class);
                intent2.putExtra("loginUsername",loginUsername);
                startActivity(intent2);
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
                                //清除SharedPreferences文件中的数据
                                editor.clear();
                            }
                            editor.putString("loginUsername",username);
                            editor.apply();

                            //登录跳转
                            Intent intent=new Intent(Login.this,ListActivity.class);
                            intent.putExtra("username",username);
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
                                            //清除SharedPreferences文件中的数据
                                            editor.clear();
                                        }
                                        editor.putString("loginUsername",username);
                                        editor.apply();

                                        Toast.makeText(Login.this,"register success",Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(Login.this,ListActivity.class);
                                        intent.putExtra("username",username);
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
                Intent intent=new Intent(Login.this,ListActivity.class);
                intent.putExtra("loginUsername","visitor");
                startActivity(intent);
                finish();
            }
        });
    }
}