package com.example.sumin.myapplication;

/**
 * Created by Sumin on 2017-09-11.
 */

public class ChatRoomListViewItem {

    String chatRoomTitle;
    String chatRoomMessage;
    String lastChatDate;
    String chatRoomImageUri;
    String chatRoomId;

    public void setChatRoomId(String id)
    {
        chatRoomId = id;
    }

    public void setChatRoomTitle(String title)
    {
        chatRoomTitle = title;
    }

    public void setChatRoomMessage(String message)
    {
        chatRoomMessage = message;
    }

    public void setLastChatDate(String date)
    {
        lastChatDate = date;
    }

    public void setChatRoomImageUri(String uri)
    {
        chatRoomImageUri = uri;
    }

    public String getChatRoomId() {return chatRoomId;}

    public String getChatRoomTitle(){return chatRoomTitle;}

    public String getChatRoomMessage(){return chatRoomMessage;}

    public String getLastChatDate() {return lastChatDate;}

    public String getChatRoomImageUri(){return getLastChatDate();}
}
