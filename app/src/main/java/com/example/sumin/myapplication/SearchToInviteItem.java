package com.example.sumin.myapplication;

/**
 * Created by Sumin on 2017-11-04.
 */

public class SearchToInviteItem {

    String userId;
    String userName;
    String userProfileURL;
    boolean isChecked;

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
