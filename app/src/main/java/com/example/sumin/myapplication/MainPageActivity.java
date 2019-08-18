package com.example.sumin.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Sumin on 2017-08-23.
 */

public class MainPageActivity extends AppCompatActivity {

//    Button profileIntentButton;
//    @Override
//    protected void onStart() {
//        super.onStart();
//        SharedPreferences pref = this.getSharedPreferences("loginPref", MODE_PRIVATE);
//        if(pref.getString("loginUserName","").equals("") && AccessToken.getCurrentAccessToken() == null )
//        {
//            Log.d("뭐니", pref.getString("loginUserName","") + "aa");
//            finish();
//        }
//    }

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("친구목록"));
        tabLayout.addTab(tabLayout.newTab().setText("오픈채팅"));
        tabLayout.addTab(tabLayout.newTab().setText("채팅"));
        tabLayout.addTab(tabLayout.newTab().setText("설정"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        // new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
        });

//        profileIntentButton = (Button) findViewById(R.id.profile_intent);
//
//        profileIntentButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent  = new Intent(MainPageActivity.this , ProfileSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
