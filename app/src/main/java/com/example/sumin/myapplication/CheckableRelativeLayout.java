package com.example.sumin.myapplication;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by Sumin on 2017-11-04.
 */

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // mIsChecked = false ;
    }

    @Override
    public boolean isChecked() {
        AppCompatCheckBox cb = (AppCompatCheckBox) findViewById(R.id.invite_check) ;

        return cb.isChecked() ;
        // return mIsChecked ;
    }

    @Override
    public void toggle() {
        AppCompatCheckBox cb = (AppCompatCheckBox) findViewById(R.id.invite_check) ;

        setChecked(cb.isChecked() ? false : true) ;
        // setChecked(mIsChecked ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        AppCompatCheckBox cb = (AppCompatCheckBox) findViewById(R.id.invite_check) ;

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }

        // CheckBox 가 아닌 View의 상태 변경.
    }





}
