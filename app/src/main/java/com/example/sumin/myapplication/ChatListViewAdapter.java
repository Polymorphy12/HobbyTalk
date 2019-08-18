package com.example.sumin.myapplication;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Sumin on 2017-09-27.
 */

public class ChatListViewAdapter extends BaseAdapter {

    String textCheck;

    public class ListContents
    {
        String userId;
        String nickName;
        String msg;
        String profileImageURL;
        String time;
        int type;
        ListContents(String user_id, String nick_name, String message, String profileImageURL, String time, int type1)
        {
            this.userId = user_id;
            this.nickName = nick_name;
            this.msg = message;
            this.time = time;
            this.type = type1;
            this.profileImageURL = profileImageURL;
        }
    }

    private ArrayList<ListContents> myList;

    public ChatListViewAdapter()
    {
        myList = new ArrayList<ListContents>();
    }

    public void add(String user_id,String nick_name, String msg,String profileImageURL,String time, int type)
    {
        myList.add(new ListContents(user_id,nick_name,msg,profileImageURL, time, type));
    }

    public void remove(int position)
    {
        myList.remove(position);
    }

    public int getCount()
    {
        return myList.size();
    }

    public Object getItem(int position)
    {
        return myList.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public ArrayList<ListContents> getListViewItemList()
    {
        return myList;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //위치와 context가 필요해.
        final int pos = position;
        final Context context = parent.getContext();

        TextView nickName = null;
        TextView text = null;
        TextView text2 = null;
        viewHolder holder;
        RelativeLayout layout = null;
        LinearLayout horLayout = null;
        LinearLayout horLayout2 = null;
        View viewRight = null;
        View viewLeft = null;
        ImageView userImageView = null;
        TextView hourAndMinuteRight = null;
        TextView hourAndMinuteLeft = null;
        ImageView sendedImage = null;


        final ListContents listViewItem;
        listViewItem = myList.get(position);
        Log.d("챗 어댑터 겟뷰", listViewItem.msg );


        if(convertView == null) {
            //LayoutInflater 는 XML로 정의 해놓은 Resource들 (레이아웃 등)을 View 형태로 변환해주는 것이다.
            //보통 popup이나 Dialog를 구현할 때 배경화면이 될 레이아웃을 만들어 놓고 View 형태로 반환받아서 액티비티에서 실행한다.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_listview_item, parent, false);

            userImageView = (ImageView) convertView.findViewById(R.id.userImage);
            layout    = (RelativeLayout) convertView.findViewById(R.id.layout);
            horLayout = (LinearLayout) convertView.findViewById(R.id.hor_layout);
            horLayout2 = (LinearLayout) convertView.findViewById(R.id.hor_layout2);

            sendedImage = (ImageView) convertView.findViewById(R.id.sendedImage);

            text    = (TextView) convertView.findViewById(R.id.text);
            //알림
            text2   = (TextView) convertView.findViewById(R.id.text2);

            //채팅을 보낸 시간.
            //채팅 받았을 때.
            hourAndMinuteRight = (TextView) convertView.findViewById(R.id.hour_and_minute);
            //채팅을 보냈을 때.
            hourAndMinuteLeft = (TextView) convertView.findViewById(R.id.hour_and_minute2);

            nickName = (TextView) convertView.findViewById(R.id.nickName);
            viewRight    = (View) convertView.findViewById(R.id.imageViewright);
            viewLeft    = (View) convertView.findViewById(R.id.imageViewleft);

            // 홀더 생성 및 Tag로 등록
            holder = new viewHolder();
            holder.msg = listViewItem.msg;
            holder.userImageView = userImageView;
            holder.text = text;
            holder.text2 = text2;
            holder.nickName = nickName;
            holder.layout = layout;
            holder.hourAndMinuteRight = hourAndMinuteRight;
            holder.hourAndMinuteLeft = hourAndMinuteLeft;
            holder.horLayout = horLayout;
            holder.horLayout2 = horLayout2;
            holder.viewRight = viewRight;
            holder.viewLeft = viewLeft;
            holder.sendedImage = sendedImage;
            convertView.setTag(holder);
        }
        else
        {
            Log.d("보낼때", "뷰홀더" + position );
            holder = (viewHolder) convertView.getTag();
            userImageView = holder.userImageView;
            text = holder.text;
            text2 = holder.text2;
            layout = holder.layout;
            hourAndMinuteRight = holder.hourAndMinuteRight;
            hourAndMinuteLeft = holder.hourAndMinuteLeft;
            nickName = holder.nickName;
            horLayout = holder.horLayout;
            horLayout2 = holder.horLayout2;
            viewRight = holder.viewRight;
            viewLeft = holder.viewLeft;
            sendedImage = holder.sendedImage;
        }



        holder.userImageView.setImageResource(R.drawable.default_profile_pic);
        holder.text.setText("######");
        holder.text2.setText(listViewItem.msg);
        holder.nickName.setText(listViewItem.nickName);
        holder.hourAndMinuteRight.setText(listViewItem.time);
        holder.hourAndMinuteLeft.setText(listViewItem.time);
        holder.sendedImage.setImageResource(R.drawable.default_profile_pic);




        //Log.d("뷰 홀더가 담고 있는 메시지", holder.msg + " 이면서, " + listViewItem.msg);


        // 받을 때 Text 등록
        // ***********
        if( listViewItem.type == 0 )
        {
            holder.text.setText(listViewItem.msg);
            holder.nickName.setVisibility(View.VISIBLE);
            holder.nickName.setText(listViewItem.nickName);
            holder.userImageView.setVisibility(View.VISIBLE);

            //이미지 전송에 해당하는 이미지뷰는 표시되지 않는다.
            holder.sendedImage.setVisibility(View.GONE);

            //왼쪽 시간 뷰는 표시되지 않는다..
            holder.hourAndMinuteLeft.setVisibility(View.GONE);
            //오른쪽 시간 뷰는 표시된다.
            holder.hourAndMinuteRight.setVisibility(View.VISIBLE);
            holder.hourAndMinuteRight.setText(listViewItem.time);

            if(position >=1 && (myList.get(position-1).userId.equals(listViewItem.userId)))
            {
                Log.d("중복이냐", "ㅇㅇ 중복");
                holder.userImageView.setVisibility(View.GONE);
                holder.nickName.setVisibility(View.GONE);
                //사진이 없을 때, 말풍선  위치 조절. (사진 크기 만큼 왼쪽으로부터 공백)
                Log.d("보낼때", "사진 없게 나오나??");
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.text.getLayoutParams();

                int myMarginPx = 45 * parent.getResources().getDimensionPixelSize(R.dimen.my_margin); // 50 * 1dp (px)

                p.setMargins(myMarginPx,0,0,0);
                holder.text.requestLayout();
            }
            else
            {
                //사진이 있을 때, 말풍선 위치 조절. (말풍선 영어로: speech bubble)
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.text.getLayoutParams();
                p.setMargins(0,0,0,0);
                holder.text.requestLayout();
                if(!listViewItem.profileImageURL.equals("none") && !listViewItem.profileImageURL.equals(""))
                {
                    Glide.with(context).load("http://ty79450.vps.phps.kr/"+listViewItem.userId
                            +"/"+listViewItem.profileImageURL).into(holder.userImageView);
                }
                else
                {
                    holder.userImageView.setImageResource(R.drawable.default_profile_pic);
//            profileImage.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.default_profile_pic, null) );
                }
            }

            holder.text.setBackgroundResource(R.drawable.inbox2);
            holder.horLayout2.setGravity(Gravity.START);
            holder.horLayout2.setVisibility(View.VISIBLE);
            holder.horLayout.setVisibility(View.GONE);
        }
        //보낼 때
        //****************
        else if(listViewItem.type == 1){
            //채팅내용은 표시 된다.
            holder.text.setText(listViewItem.msg);
            //닉네임과 사진이 표시되지 않는다.
            holder.nickName.setVisibility(View.GONE);
            holder.nickName.setText(listViewItem.nickName);
            holder.userImageView.setVisibility(View.GONE);

            //이미지 전송에 해당하는 이미지뷰는 표시되지 않는다.
            holder.sendedImage.setVisibility(View.GONE);

            Log.d("챗 어댑터에 텍스트 추가", "@@@@@@@@@"
                    +listViewItem.msg + " " + position + " " + "겟텍스트 : " + holder.text.getText().toString()
            + "사진 : ");

            //오른 쪽 시간 뷰는 표시되지 않는다..
            Log.d("보낼때", "오른쪽 시간 뷰는 표시되지 않는다 " + position );
            holder.hourAndMinuteRight.setVisibility(View.GONE);
            //왼 쪽 시간 뷰는 표시된다.
            Log.d("보낼때", "왼쪽 시간 뷰는 표시된다. " + position );
            holder.hourAndMinuteLeft.setVisibility(View.VISIBLE);
            holder.hourAndMinuteLeft.setText(listViewItem.time);

            holder.text.setBackgroundResource(R.drawable.outbox2);
            holder.horLayout2.setGravity(Gravity.END);
            holder.horLayout2.setVisibility(View.VISIBLE);
            holder.horLayout.setVisibility(View.GONE);
        }
        else if(listViewItem.type == 2){
            holder.text2.setText(listViewItem.msg);
            holder.text2.setBackgroundResource(R.drawable.datebg);
            holder.horLayout.setGravity(Gravity.CENTER);
            holder.horLayout2.setVisibility(View.GONE);
            holder.viewRight.setVisibility(View.VISIBLE);
            holder.viewLeft.setVisibility(View.VISIBLE);
        }
        //이미지를 받을 때
        //****************
        else if(listViewItem.type == 3)
        {
            //채팅내용은 사라진다.
            holder.text.setVisibility(View.VISIBLE);

            //대신, 보내는 이미지가 표시된다.
            holder.sendedImage.setVisibility(View.VISIBLE);
            Glide.with(context).load("http://ty79450.vps.phps.kr/sended_images/"
                    +listViewItem.msg).into(holder.sendedImage);


            holder.nickName.setVisibility(View.VISIBLE);
            holder.nickName.setText(listViewItem.nickName);
            holder.userImageView.setVisibility(View.VISIBLE);

            //왼쪽 시간 뷰는 표시되지 않는다..
            holder.hourAndMinuteLeft.setVisibility(View.GONE);
            //오른쪽 시간 뷰는 표시된다.
            holder.hourAndMinuteRight.setVisibility(View.VISIBLE);
            holder.hourAndMinuteRight.setText(listViewItem.time);

            if(position >=1 && (myList.get(position-1).userId.equals(listViewItem.userId)))
            {
                Log.d("중복이냐", "ㅇㅇ 중복");
                holder.userImageView.setVisibility(View.GONE);
                holder.nickName.setVisibility(View.GONE);
                //사진이 없을 때, 말풍선 위치 조절. (사진 크기 만큼 왼쪽으로부터 공백)
                Log.d("보낼때", "사진 없게 나오나??");
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.text.getLayoutParams();

                int myMarginPx = 45 * parent.getResources().getDimensionPixelSize(R.dimen.my_margin); // 50 * 1dp (px)

                p.setMargins(myMarginPx,0,0,0);
                holder.text.requestLayout();
            }
            else
            {
                //사진이 있을 때, 말풍선 위치 조절. (말풍선 영어로: speech bubble)
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.text.getLayoutParams();
                p.setMargins(0,0,0,0);
                holder.text.requestLayout();
                if(!listViewItem.profileImageURL.equals("none") && !listViewItem.profileImageURL.equals(""))
                {
                    Glide.with(context).load("http://ty79450.vps.phps.kr/"+listViewItem.userId
                            +"/"+listViewItem.profileImageURL).into(holder.userImageView);
                }
                else
                {
                    holder.userImageView.setImageResource(R.drawable.default_profile_pic);
//            profileImage.setImageDrawable(ResourcesCompat.getDrawable(convertView.getResources(), R.drawable.default_profile_pic, null) );
                }
            }

            holder.horLayout2.setGravity(Gravity.START);
            holder.horLayout2.setVisibility(View.VISIBLE);
            holder.horLayout.setVisibility(View.GONE);
        }
        //이미지를 보낼 때
        //****************
        else if(listViewItem.type == 4)
        {
            //채팅내용은 사라진다.
            holder.text.setVisibility(View.GONE);

            //대신, 보내는 이미지가 표시된다.
            holder.sendedImage.setVisibility(View.VISIBLE);
            Glide.with(context).load("http://ty79450.vps.phps.kr/sended_images/"
                    +listViewItem.msg).into(holder.sendedImage);

            Log.d("챗 어댑터에 이미지 추가", "@@@@@@@@@" + "http://ty79450.vps.phps.kr/sended_images/"
                    +listViewItem.msg + position);

            //닉네임과 프로필 사진이 표시되지 않는다.
            holder.nickName.setVisibility(View.GONE);
            holder.userImageView.setVisibility(View.GONE);

            //오른 쪽 시간 뷰는 표시되지 않는다..
            Log.d("보낼때", "오른쪽 시간 뷰는 표시되지 않는다 " + position );
            holder.hourAndMinuteRight.setVisibility(View.GONE);
            //왼 쪽 시간 뷰는 표시된다.
            Log.d("보낼때", "왼쪽 시간 뷰는 표시된다. " + position );
            holder.hourAndMinuteLeft.setVisibility(View.VISIBLE);
            holder.hourAndMinuteLeft.setText(listViewItem.time);


            holder.horLayout2.setGravity(Gravity.END);
            holder.horLayout2.setVisibility(View.VISIBLE);
            holder.horLayout.setVisibility(View.GONE);
        }


        if(holder.text != null)
        {
            Log.d("보낼때2", "뷰홀더 " + position + " " + holder.text.getText() + " " +text.getText() +"\n"+holder.msg + " 이면서, " + listViewItem.msg );
            textCheck = holder.text.getText().toString();
        }

//        // 리스트 아이템을 터치 했을 때 이벤트 발생
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 터치 시 해당 아이템 이름 출력
//                //Toast.makeText(context, "리스트 클릭 : "+myList.get(pos), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // 리스트 아이템을 길게 터치 했을때 이벤트 발생
//        convertView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // 터치 시 해당 아이템 이름 출력
//                Toast.makeText(context, "리스트 롱 클릭 : "+listViewItem.msg, Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        return convertView;
    }


    private class viewHolder{

        String msg;

        ImageView sendedImage;
        ImageView userImageView;
        TextView nickName;
        TextView text;
        TextView text2;
        TextView hourAndMinuteRight;
        TextView hourAndMinuteLeft;
        RelativeLayout layout;
        LinearLayout horLayout;
        LinearLayout horLayout2;
        View viewRight;
        View viewLeft;
    }
}