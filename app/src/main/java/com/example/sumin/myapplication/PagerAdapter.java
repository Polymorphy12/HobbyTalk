package com.example.sumin.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Sumin on 2017-09-01.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    TabFragment1 tab1;
    TabFragment2 tab2;
    TabFragment3 tab3;
    TabFragment4 tab4;

    public PagerAdapter(FragmentManager fm, int NumOfTabs)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {

//        Log.d("불러왔니?", "응");
//        UpdateSecondFragment f = null;
//
//        if(object instanceof UpdateSecondFragment)
//            f = (UpdateSecondFragment) object;
//
//        if(f !=null)
//        {
//            Log.d("불러왔니?", "업데이트중");
//            f.update();
//        }

        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
            {
                //Log.d("불러왔니?", "요긴가?1");
                tab1 = new TabFragment1();
                return tab1;
            }
            case 1:
                //Log.d("불러왔니?", "요긴가?2");
                tab2 = new TabFragment2();
                return tab2;
            case 2:
                //Log.d("불러왔니?", "요긴가?3");
                tab3 = new TabFragment3();
                return tab3;
            case 3:
                //Log.d("불러왔니?", "요긴가?4");
                tab4 = new TabFragment4();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}