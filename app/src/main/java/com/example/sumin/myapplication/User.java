package com.example.sumin.myapplication;

import java.net.URI;

/**
 * Created by Sumin on 2017-08-22.
 */

public class User {
    //각 유저는 아이디, 대화명, 상태메시지, 사진을 가지고 있다.
    private String userId;
    private String userName;
    private String statusMessage = "";
    private URI profilePhoto;


    public void setUserId(String id)
    {
        userId = id;
    }

    public void setUserName(String name)
    {
        userName = name;
    }

    public void setStatusMessage(String message)
    {
        statusMessage = message;
    }
    public void setProfilePhoto(URI photo)
    {
        profilePhoto = photo;
    }

    private String getUserId()
    {
        return userId;
    }

    private String getUserName()
    {
        return userName;
    }

    private String getStatusMessage()
    {
        return statusMessage;
    }

    private URI getProfilePhoto()
    {
        return profilePhoto;
    }
}
