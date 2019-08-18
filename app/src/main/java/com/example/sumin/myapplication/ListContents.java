package com.example.sumin.myapplication;

/**
 * Created by Sumin on 2017-11-17.
 */

public class ListContents {

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
