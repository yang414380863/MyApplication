package com.example.yang.myapplication;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.yang.myapplication.basic.LogUtil;
import com.example.yang.myapplication.web.Website;

import java.util.ArrayList;
import java.util.Arrays;

import static android.R.attr.category;
import static android.R.attr.name;

public class AddWebsite extends AppCompatActivity {

    Website website;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_website);

        final LinearLayout categoryLayout=(LinearLayout)findViewById(R.id.category_parent);
        final LinearLayout itemRuleLayout=(LinearLayout)findViewById(R.id.item_rule_parent);
        final LayoutInflater inflater = LayoutInflater.from(AddWebsite.this);

        Button addCategory=(Button)findViewById(R.id.add_category);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflater.inflate(R.layout.add_category,categoryLayout);
            }
        });
        Button addItemRule=(Button)findViewById(R.id.add_item_rule);
        addItemRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflater.inflate(R.layout.add_item_rule,itemRuleLayout);
            }
        });

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.finish_add_website);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //category
                String[] category= new String[categoryLayout.getChildCount()*2];
                for (int i=0;i<categoryLayout.getChildCount();i++){
                    EditText index=(EditText)categoryLayout.getChildAt(i).findViewById(R.id.category_index);
                    EditText name=(EditText)categoryLayout.getChildAt(i).findViewById(R.id.category_name);
                    if (index.getText().equals("")||name.getText().equals("")){
                    }else{
                        category[2*i]=index.getText().toString();
                        category[2*i+1]=name.getText().toString();
                    }
                }
                LogUtil.d("category: "+Arrays.toString(category));

                //itemRule
                for (int i=0;i<itemRuleLayout.getChildCount();i++){
                    EditText selector=(EditText)itemRuleLayout.getChildAt(i).findViewById(R.id.selector);
                    EditText method=(EditText)itemRuleLayout.getChildAt(i).findViewById(R.id.method);
                    if (selector.getText().equals("")||method.getText().equals("")){
                    }else{

                    }
                }

            }
        });
    }
}
