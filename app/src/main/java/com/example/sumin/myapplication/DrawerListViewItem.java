package com.example.sumin.myapplication;

/**
 * Created by Sumin on 2017-10-23.
 */

public class DrawerListViewItem {

    String userId;
    String userName;
    String userProfileURL;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserProfileURL(String userProfileURL) {
        this.userProfileURL = userProfileURL;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getUserProfileURL()
    {
        return userProfileURL;
    }
}
