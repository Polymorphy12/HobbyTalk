package com.example.sumin.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Sumin on 2017-09-01.
 */

public class FriendListViewAdapter extends BaseAdapter implements Filterable {


    private ArrayList<FriendListViewItem> listViewItemList = new ArrayList<FriendListViewItem>();
    private ArrayList<FriendListViewItem> filteredItemList = listViewItemList;


    //여기서부터 filter관련.

    Filter listFilter;

    @Override
    public Filter getFilter() {

        if(listFilter == null)
        {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter
    {
        @Override protected FilterResults performFiltering(CharSequence constraint)
        {
            Log.d("필터 perform", constraint.toString());
            FilterResults results = new FilterResults() ;
            if (constraint == null || constraint.length() == 0)
            {
                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;
            }
            else
            {
                ArrayList<FriendListViewItem> itemList = new ArrayList<FriendListViewItem>() ;
                for (FriendListViewItem item : listViewItemList)
                {
//      if (item.getYearMonth().toUpperCase().contains(constraint.toString().toUpperCase()) )
                    if(true)
                    { itemList.add(item) ; }
                }
                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            // update listview by filtered data list.
            filteredItemList = (ArrayList<FriendListViewItem>) results.values ;
            // notify
            if (results.count > 0)
            {
                notifyDataSetChanged();
            }
            else
            {
                notifyDataSetInvalidated() ;
            }
        }
    }



    // filter관련 끝

    @Override
    public int getCount()
    {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listViewItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //위치와 context가 필요해.
        final int pos = position;
        final Context context = parent.getContext();

        //"listview_item" Layout을 inflate하여 convertView 참조 획득.
        if(convertView == null)
        {
            //LayoutInflater 는 XML로 정의 해놓은 Resource들 (레이아웃 등)을 View 형태로 변환해주는 것이다.
            //보통 popup이나 Dialog를 구현할 때 배경화면이 될 레이아웃을 만들어 놓고 View 형태로 반환받아서 액티비티에서 실행한다.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득

        TextView nickname = (TextView) convertView.findViewById(R.id.itemTitle);
        TextView statusMessage = (TextView) convertView.findViewById(R.id.itemStatusMessage);

        LinearLayout textLayout1 = (LinearLayout) convertView.findViewById(R.id.textLayout);
        LinearLayout textLayout2 = (LinearLayout) convertView.findViewById(R.id.textLayout2);

        ImageView profileImage = (ImageView) convertView.findViewById(R.id.itemImage);



        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득

        FriendListViewItem listViewItem;
        listViewItem = filteredItemList.get(position);

        if(!listViewItem.getProfileImageUri().equals("none") && !listViewItem.getProfileImageUri().equals(""))
        {
            Log.e("RECV", listViewItem.IsThatMe()+"!!!!!!!!!!!!!!");
            if(listViewItem.isThatMe)
            {
                Log.e("RECV", "나 프로필 사진 있어!!!!!!!!!!!!!!!!! " + listViewItem.getId());
            }
            else
            {
                Log.e("RECV", "프로필 사진 "  + listViewItem.getId());
            }
            Glide.with(context).load("http://ty79450.vps.phps.kr/"+listViewItem.getId()
                                        +"/"+listViewItem.getProfileImageUri()).into(profileImage);
        }
        else
        {
            Log.e("RECV", listViewItem.IsThatMe()+"!!!!!!!!!!!!!!");

            if(listViewItem.isThatMe)
            {
                Log.e("RECV", "나 프로필 사진 없어!!!!!!!!!!!!!!! "  + listViewItem.getId());
            }
            else
            {
                Log.e("RECV", "프로필 사진 없당"  + listViewItem.getId());
            }


            profileImage.setImageResource(R.drawable.default_profile_pic);
//            profileImage.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.default_profile_pic, null) );
        }

        if(!listViewItem.IsThatMe())
        {
            textLayout1.setVisibility(View.GONE);
            textLayout2.setVisibility(View.GONE);
        }
        else
        {
            textLayout1.setVisibility(View.VISIBLE);
            textLayout2.setVisibility(View.VISIBLE);
        }


        //// 아이템 내 각 위젯에 데이터 반영

        nickname.setText(listViewItem.getName());
        statusMessage.setText((listViewItem.getStatus()));


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<FriendListViewItem> getListViewItemList() {return listViewItemList;}

    public ArrayList<FriendListViewItem> getFilteredItemList() { return filteredItemList;}

    public void addItem(FriendListViewItem item)
    {
        listViewItemList.add(item);
    }

    public void removeItem(int position)
    {
        listViewItemList.remove(position);
    }


}