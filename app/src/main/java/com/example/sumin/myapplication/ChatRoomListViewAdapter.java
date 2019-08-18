package com.example.sumin.myapplication;

import android.content.Context;
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
 * Created by Sumin on 2017-09-11.
 */

public class ChatRoomListViewAdapter extends BaseAdapter implements Filterable {

    private ArrayList<ChatRoomListViewItem> listViewItemList = new ArrayList<ChatRoomListViewItem>();
    private ArrayList<ChatRoomListViewItem> filteredItemList = listViewItemList;


    //여기서부터 filter관련.

    Filter listFilter;

    @Override
    public Filter getFilter() {
        if(listFilter == null)
        {
            listFilter = new ChatRoomListViewAdapter.ListFilter();
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
                ArrayList<ChatRoomListViewItem> itemList = new ArrayList<ChatRoomListViewItem>() ;
                for (ChatRoomListViewItem item : listViewItemList)
                {
//                    if (item.getYearMonth().toUpperCase().contains(constraint.toString().toUpperCase()) )
                    if(true)
                    { itemList.add(item); }
                }
                results.values = itemList ;
                results.count = itemList.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            // update listview by filtered data list.
            filteredItemList = (ArrayList<ChatRoomListViewItem>) results.values ;
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
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = inflater.inflate(R.layout.chatroom_listview_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득

        TextView chatRoomItemTitle = (TextView) convertView.findViewById(R.id.chatRoomItemTitle);
        TextView chatRoomItemMessage = (TextView) convertView.findViewById(R.id.chatRoomItemMessage);

        ImageView profileImage = (ImageView) convertView.findViewById(R.id.chatRoomItemImage);



        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득

        ChatRoomListViewItem listViewItem;
        listViewItem = filteredItemList.get(position);

//        if(!listViewItem.getChatRoomImageUri().equals("none") && !listViewItem.getChatRoomImageUri().equals(""))
//        {
////            Glide.with(context).load("http://ty79450.vps.phps.kr/"+listViewItem.getId()
////                    +"/"+listViewItem.getProfileImageUri()).into(profileImage);
//        }
//        else
//        {
//            profileImage.setImageResource(R.drawable.default_profile_pic);
////            profileImage.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.default_profile_pic, null) );
//        }


        //// 아이템 내 각 위젯에 데이터 반영

        chatRoomItemTitle.setText(listViewItem.getChatRoomTitle());
        chatRoomItemMessage.setText((listViewItem.getChatRoomMessage()));


        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ArrayList<ChatRoomListViewItem> getListViewItemList() {return listViewItemList;}

    public ArrayList<ChatRoomListViewItem> getFilteredItemList() { return filteredItemList;}

    public void addItem(ChatRoomListViewItem item)
    {
        listViewItemList.add(item);
    }

    public void removeItem(int position)
    {
        listViewItemList.remove(position);
    }
}