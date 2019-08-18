package com.example.sumin.myapplication;

/**
 * Created by Sumin on 2017-08-25.
 */

public class FriendListViewItem {

    String name;
    String statusMessage;
    String profileImageUri;
    String id;
    boolean isThatMe = false;

    public void setProfileImageUri(String pi) {profileImageUri = pi;}

    public void setIsThatMe(boolean b) {
        isThatMe = b;
    }

    public void setId(String i){id = i;}

    public void setName(String t)
    {
        name = t;
    }

    public void setStatusMessage(String s)
    {
        statusMessage = s;
    }

    public String getProfileImageUri(){return profileImageUri;}

    public boolean IsThatMe(){return isThatMe;}

    public String getId(){return id;}

    public String getName()
    {
        return name;
    }

    public String getStatus()
    {
        return statusMessage;
    }


}
