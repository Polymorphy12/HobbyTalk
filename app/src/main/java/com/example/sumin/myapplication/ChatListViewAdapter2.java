package com.example.sumin.myapplication;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Sumin on 2017-11-17.
 */

public class ChatListViewAdapter2 extends BaseAdapter implements Filterable {


    private static final int RECEIVE_MESSAGE = 0;
    private static final int SEND_MESSAGE = 1;
    private static final int ENTER_OR_EXIT = 2;
    private static final int RECEIVE_PHOTO = 3;
    private static final int SEND_PHOTO = 4;



    private ArrayList<ListContents> listViewItemList = new ArrayList<ListContents>();
    private ArrayList<ListContents> filteredItemList = listViewItemList;

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position)
    {
        return filteredItemList.get(position).type;
    }

    //여기서부터 filter관련.

    Filter listFilter;

    @Override
    public Filter getFilter() {
        if(listFilter == null)
        {
            listFilter = new ChatListViewAdapter2.ListFilter();
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
                ArrayList<ListContents> itemList = new ArrayList<ListContents>() ;
                for (ListContents item : listViewItemList)
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
            filteredItemList = (ArrayList<ListContents>) results.values;
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
        return filteredItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //위치와 context가 필요해.
        final int pos = position;
        final Context context = parent.getContext();

        int viewType = getItemViewType(position);

        TextView nickName;
        TextView text;
        ImageView userImageView;
        TextView hourAndMinute;
        ImageView sendedImage;
        TextView text2;


        final ViewHolder viewHolder;

        ListContents listViewItem = filteredItemList.get(position);

        //"listview_item" Layout을 inflate하여 convertView 참조 획득.
        if(convertView == null)
        {
            //LayoutInflater 는 XML로 정의 해놓은 Resource들 (레이아웃 등)을 View 형태로 변환해주는 것이다.
            //보통 popup이나 Dialog를 구현할 때 배경화면이 될 레이아웃을 만들어 놓고 View 형태로 반환받아서 액티비티에서 실행한다.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.d("세팅까지 왔니", "겟뷰 시작 " + listViewItem.type + " "+ listViewItem.msg + " " + listViewItem.profileImageURL + " ");

            viewHolder = new ViewHolder();

            switch(viewType)
            {
                case RECEIVE_MESSAGE:
                    Log.d("세팅까지 왔니", "메시지 받기");
                    convertView = inflater.inflate(R.layout.chat_listview_item2, parent, false);

                    viewHolder.nickName = (TextView) convertView.findViewById(R.id.nickName);
                    viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                    viewHolder.userImageView = (ImageView) convertView.findViewById(R.id.userImage);
                    viewHolder.hourAndMinute = (TextView) convertView.findViewById(R.id.hour_and_minute);
                    viewHolder.sendedImage = (ImageView) convertView.findViewById(R.id.sendedImage);


                    break;

                case SEND_MESSAGE:
                    Log.d("챗 어댑터", "메시지 보내기 " + listViewItem.msg );
                    convertView = inflater.inflate(R.layout.chat_listview_item1, parent, false);

                    viewHolder.text = (TextView) convertView.findViewById(R.id.text);

                    viewHolder.hourAndMinute = (TextView) convertView.findViewById(R.id.hour_and_minute2);
                    viewHolder.sendedImage = (ImageView) convertView.findViewById(R.id.sendedImage);

                    break;

                case ENTER_OR_EXIT:
                    convertView = inflater.inflate(R.layout.chat_listview_item3, parent, false);

                    viewHolder.text2 = (TextView) convertView.findViewById(R.id.text2);
                    break;

                case RECEIVE_PHOTO:

                    Log.d("세팅까지 왔니", "사진 받기");
                    convertView = inflater.inflate(R.layout.chat_listview_item2, parent, false);


                    //닉네임, 할 말, 프로필 사진, 시간, 보낸 사진이 있다.
                    viewHolder.nickName = (TextView) convertView.findViewById(R.id.nickName);
                    viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                    viewHolder.userImageView = (ImageView) convertView.findViewById(R.id.userImage);
                    viewHolder.hourAndMinute = (TextView) convertView.findViewById(R.id.hour_and_minute);
                    viewHolder.sendedImage = (ImageView) convertView.findViewById(R.id.sendedImage);

                    break;

                case SEND_PHOTO:
                    Log.d("세팅까지 왔니", "사진 보내기");
                    convertView = inflater.inflate(R.layout.chat_listview_item1, parent, false);

                    viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                    viewHolder.hourAndMinute = (TextView) convertView.findViewById(R.id.hour_and_minute2);
                    viewHolder.sendedImage = (ImageView) convertView.findViewById(R.id.sendedImage);

                    break;
            }

            convertView.setTag(viewHolder);

        }

        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        switch(viewType)
        {
            case RECEIVE_MESSAGE:
                Log.d("세팅까지 왔니", "메시지 받기");

                viewHolder.nickName.setText(listViewItem.nickName);
                //텍스트는 보임.
                viewHolder.text.setText(listViewItem.msg);
                viewHolder.text.setVisibility(View.VISIBLE);

                viewHolder.hourAndMinute.setText((listViewItem.time));
                //이미지는 보여진다.
                viewHolder.sendedImage.setVisibility(View.GONE);
                viewHolder.userImageView.setVisibility(View.VISIBLE);


                //프로필 이미지는 보여진다.
                if(position >=1 && (filteredItemList.get(position-1).userId.equals(listViewItem.userId)))
                {
                    Log.d("중복이냐", "ㅇㅇ 중복");
                    viewHolder.userImageView.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.GONE);
                    //사진이 없을 때, 말풍선  위치 조절. (사진 크기 만큼 왼쪽으로부터 공백)
                    Log.d("보낼때", "사진 없게 나오나??");
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.text.getLayoutParams();

                    int myMarginPx = 45 * parent.getResources().getDimensionPixelSize(R.dimen.my_margin); // 50 * 1dp (px)

                    p.setMargins(myMarginPx,0,0,0);
                    viewHolder.text.requestLayout();
                }
                else
                {
                    //사진이 있을 때, 말풍선 위치 조절. (말풍선 영어로: speech bubble)
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.text.getLayoutParams();
                    p.setMargins(0,0,0,0);
                    viewHolder.text.requestLayout();
                    if(!listViewItem.profileImageURL.equals("none") && !listViewItem.profileImageURL.equals(""))
                    {
                        Glide.with(context).load("http://ty79450.vps.phps.kr/"+listViewItem.userId
                                +"/"+listViewItem.profileImageURL).into(viewHolder.userImageView);
                    }
                    else
                    {
                        viewHolder.userImageView.setImageResource(R.drawable.default_profile_pic);
//            profileImage.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.default_profile_pic, null) );
                    }
                }


                break;

            case SEND_MESSAGE:
                Log.d("챗 어댑터", "메시지 보내기 " + listViewItem.msg );

                //텍스트는 보임.
                viewHolder.text.setText(listViewItem.msg);
                viewHolder.text.setVisibility(View.VISIBLE);

                viewHolder.hourAndMinute.setText((listViewItem.time));
                //이미지는 보여지지 않는다.
                viewHolder.sendedImage.setImageResource(R.drawable.default_profile_pic);
                viewHolder.sendedImage.setVisibility(View.GONE);
                break;

            case ENTER_OR_EXIT:

                viewHolder.text2.setText(listViewItem.msg);
                break;

            case RECEIVE_PHOTO:

                viewHolder.nickName.setText(listViewItem.nickName);
                //텍스트는 보여지지 않는다.
                viewHolder.text.setText(listViewItem.msg);
                viewHolder.text.setVisibility(View.GONE);
                viewHolder.hourAndMinute.setText((listViewItem.time));
                //이미지는 보여진다.
                Glide.with(context).load("http://ty79450.vps.phps.kr/sended_images/"
                        +listViewItem.msg).into(viewHolder.sendedImage);
                viewHolder.sendedImage.setVisibility(View.VISIBLE);

                viewHolder.userImageView.setVisibility(View.VISIBLE);

                if(position >=1 && (filteredItemList.get(position-1).userId.equals(listViewItem.userId)))
                {
                    Log.d("중복이냐", "ㅇㅇ 중복");
                    viewHolder.userImageView.setVisibility(View.GONE);
                    viewHolder.nickName.setVisibility(View.GONE);
                    //사진이 없을 때, 말풍선  위치 조절. (사진 크기 만큼 왼쪽으로부터 공백)
                    Log.d("보낼때", "사진 없게 나오나??");
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.sendedImage.getLayoutParams();

                    int myMarginPx = 45 * parent.getResources().getDimensionPixelSize(R.dimen.my_margin); // 50 * 1dp (px)

                    p.setMargins(myMarginPx,0,0,0);
                    viewHolder.sendedImage.requestLayout();
                }
                else
                {
                    //사진이 있을 때, 말풍선 위치 조절. (말풍선 영어로: speech bubble)
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewHolder.text.getLayoutParams();
                    p.setMargins(0,0,0,0);
                    viewHolder.text.requestLayout();
                    if(!listViewItem.profileImageURL.equals("none") && !listViewItem.profileImageURL.equals(""))
                    {
                        Glide.with(context).load("http://ty79450.vps.phps.kr/"+listViewItem.userId
                                +"/"+listViewItem.profileImageURL).into(viewHolder.userImageView);
                    }
                    else
                    {
                        viewHolder.userImageView.setImageResource(R.drawable.default_profile_pic);
//            profileImage.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.default_profile_pic, null) );
                    }
                }

                break;

            case SEND_PHOTO:
                Log.d("세팅까지 왔니", "사진 보내기");

                //텍스트는 안보임.
                viewHolder.text.setText(listViewItem.msg);
                viewHolder.text.setVisibility(View.GONE);

                viewHolder.hourAndMinute.setText((listViewItem.time));
                //이미지는 보여진다.
                Glide.with(context).load("http://ty79450.vps.phps.kr/sended_images/"
                        +listViewItem.msg).into(viewHolder.sendedImage);
                viewHolder.sendedImage.setVisibility(View.VISIBLE);
                break;
        }

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

        //Log.d("세팅까지 왔니", "겟뷰 끝");


        return convertView;
    }

    public class ViewHolder
    {
        TextView nickName;
        TextView text;
        ImageView userImageView;
        TextView hourAndMinute;
        ImageView sendedImage;
        TextView text2;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public ArrayList<ListContents> getListViewItemList()
    {
        return listViewItemList;
    }

    public ArrayList<ListContents> getFilteredItemList()
    {
        return filteredItemList;
    }

    public void add(String user_id,String nick_name, String msg,String profileImageURL,String time, int type)
    {
        listViewItemList.add(new ListContents(user_id,nick_name,msg,profileImageURL, time, type));
    }

    public void addItem(ListContents item)
    {
        listViewItemList.add(item);
    }

    public void removeItem(int position)
    {
        listViewItemList.remove(position);
    }
}