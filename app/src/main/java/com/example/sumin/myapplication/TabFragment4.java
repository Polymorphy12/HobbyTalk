package com.example.sumin.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sumin on 2017-09-01.
 */

public class TabFragment4 extends Fragment {


    //여기터 실질적인 프래그먼트의 뷰들 관리.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);

        return view;
    }

}
