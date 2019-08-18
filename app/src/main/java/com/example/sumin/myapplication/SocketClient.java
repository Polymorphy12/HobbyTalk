package com.example.sumin.myapplication;

/**
 * Created by Sumin on 2017-09-11.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class SocketClient {

    HashMap<String, DataOutputStream> clients;
    HashMap<String, HashMap<String, DataOutputStream>> rooms;


    Connection conn;
    Statement stmt;

    private ServerSocket ServerSocket = null;

    public static void main(String[] args)
    {
        new SocketClient().start();
    }

    //Constructor, 생성자.
    public SocketClient()
    {
        //db로 연결.
        try
        {
            conn = DriverManager.getConnection("ty79450.vps.phps.kr", "root", "thstnals124!");
            stmt = conn.createStatement();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        //연결부 hashmap 생성자 (key, value) 선언.
        clients = new HashMap<String, DataOutputStream>(); // 이것 하나가 방이라 생각하면 좋다.
        // clients 동기화
        Collections.synchronizedMap(clients);
    }

    public void addRoom(String roomId)
    {
        HashMap<String, DataOutputStream> clientsInTheRoom = new HashMap<String, DataOutputStream>();
        rooms.put(roomId,clientsInTheRoom);
    }

    public void removeRoom(String roomId)
    {
        rooms.remove(roomId);
    }

    public void addRoomGuest(MultiThread guest)
    {
        HashMap<String, DataOutputStream> clientsInTheRoom = rooms.get(guest.roomId);
        clientsInTheRoom.put(guest.mac, guest.output);
    }

    public void removeRoomGuest(MultiThread guest)
    {
        HashMap<String, DataOutputStream> clientsInTheRoom = rooms.get(guest.roomId);
        clientsInTheRoom.remove(guest.mac);
        if(rooms.size() == 0)
        {
            removeRoom(guest.roomId);
            // broadcast??
        }
    }

    public void getChatRoomsFromDB()
    {
        String sql = "select * from chatRooms";

        try
        {
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                addRoom(rs.getString("room_id"));
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void insertChatRoomUserIdToDB(String roomId,String user_id)
    {
        //db에 넣어줘야 한다.
        String sql = "insert into chatRoomUserList values("+roomId+"," +user_id+")";
        try
        {
            stmt.executeUpdate(sql);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }


    public void insertChatRoomToDB(String roomId, String roomName)
    {
        String sql = "insert into chatRooms values("+roomId+"," +roomName+")";
        try
        {
            stmt.executeUpdate(sql);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public String getUserListFromSQL(String roomId)
    {
        String sql = "select * from chatRoomUserList where chatroom_id = '" +roomId + "';";
        String jsonObject = "[{\"nickname\":\"\",\"user_id\":\"\",\"profileImage\":\"\"}";
        try
        {
            //Connection conn = DriverManager.getConnection("ty79450.vps.phps.kr", "root", "thstnals124!");
            //Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next())
            {
                String tempId = rs.getString("user_id");

                String sql2 = "select * from users where user_id = '" + tempId +"';";
                ResultSet rs2 = stmt.executeQuery(sql2);

                while(rs2.next())
                {
                    jsonObject += ",{\"nickname\":\""+rs2.getString("nickname")+"\",\"user_id\":\""+rs2.getString("user_id")+
                            "\",\"profileImage\":\""+rs2.getString("profileImage")+"\"}";
                }
            }
            jsonObject += "]";
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void start()
    {
        int port = 5001; // 뭐 7777등으로 바꾸거나.. 해도 괜찮다. //체크, csport
        Socket socket = null;

        //Get chatrooms and put to HashMap named, "rooms"
        getChatRoomsFromDB();

        try {
            // 서버 소켓 생성한 후 while문으로 진입해서 accept 하고 접속시 ip 주소를 획득하고 출력한 뒤
            // MultiThread를 생성한다.
            ServerSocket = new ServerSocket(port); //서버 소켓을 만들고
            System.out.println("접속대기중");
            while(true) {	//while문에 진입한 후
                socket = ServerSocket.accept();
                InetAddress ip = socket.getInetAddress(); //ip주소를 획득하고.
                System.out.println(ip + " connected");
                new MultiThread(socket).start(); //MultiThread를 생성한다.
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    class MultiThread extends Thread
    {
        Socket socket = null;  // 체크. st_sock

        //mac이 닉네임.
        String mac = null;
        String msg = null;
        String roomId = null;
        String roomName = null;
        String userId = null;

        DataInputStream input;  // 체크. st_in
        DataOutputStream output; // st_out

        public MultiThread(Socket socket)
        {
            this.socket = socket;
            try
            {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            }
            catch(IOException e)
            {

            }
        }

        public void run()
        {
            try
            {
                //접속된 후 바로 MAC 주소를 받아와 출력하고 clients에 정보를 넘겨주고
                //클라이언트에게 mac 주소를 보낸다.
                mac = input.readUTF();

                String[] array;
                array = mac.split(":::");

                if(array.length == 4)
                {
                    mac = array[0];
                    roomId = array[1];
                    userId = array[2];
                    roomName = array[3];
                    insertChatRoomToDB(roomId,roomName);
                    //insertChatRoomUserIdToDB(roomId, userId);
                }
                else
                {
                    mac = array[0];
                    roomId = array[1];
                    userId = array[2];

                }


                addRoomGuest(this);

                HashMap<String, DataOutputStream> clientsInTheRoom = rooms.get(roomId);

                System.out.println("Mac address : " + mac);
                clientsInTheRoom.put(mac,  output);

                //참여자 목록 중에 자기가 있는지 확인.
                String sql = "select * from chatRoomUserList where chatroom_id = '" +roomId
                        + "' and user_id = '"+userId+"';";

                try
                {
                    ResultSet rs = stmt.executeQuery(sql);

                    if(!rs.next())
                    {
                        //없다면? DB에 user_id 추가하고, "nickname 님이 들어오셨습니다." 라고 sendMsg.
                        insertChatRoomUserIdToDB(roomId, userId);
                        sendMsgPractice(mac + "  님이 들어오셨습니다.",roomId);
                    }
                    //있다면? 그냥 진행.
                    rs.beforeFirst();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }


                while(input != null)
                {
                    sendMsgPractice(input.readUTF(),roomId);
                }


                // //그 후에 채팅 메세지 수신할 때.
                // while(input != null)
                // {
                // 	try
                // 	{
                // 		String temp = input.readUTF();
                // 		sendMsg(temp);
                // 		System.out.println(temp);
                // 	}
                // 	catch(IOException e)
                // 	{
                // 		sendMsg(mac + "  님께서 나가셨습니다.");
                // 		break;
                // 	}
                // }
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
            finally{
                sendMsgPractice("#" + mac +"님이 나가셨습니다.",roomId);
                clients.remove(mac);
            }
        }

        //메세지 수신 후 클라이언트에 Return 할 sendMsg메소드
        private void sendMsg(String msg) {

            // clients의 key값을 받아서 String 배열로 선언.
            Iterator<String> it = clients.keySet().iterator();

            //Return할 key값이 없을 때까지.
            while(it.hasNext())
            {
                try
                {
                    OutputStream dos = clients.get(it.next());
                    // System.out.println(msg);
                    DataOutputStream output = new DataOutputStream(dos);
                    output.writeUTF(msg);
                }
                catch(IOException e)
                {

                }
            }

        }

        private void sendMsgPractice(String msg, String room_id)
        {
            HashMap<String, DataOutputStream> clientsInTheRoom = rooms.get(room_id);

            // clients의 key값을 받아서 String 배열로 선언.
            Iterator<String> it = clientsInTheRoom.keySet().iterator();

            //Return할 key값이 없을 때까지.
            while(it.hasNext())
            {
                try
                {
                    OutputStream dos = clientsInTheRoom.get(it.next());
                    // System.out.println(msg);
                    DataOutputStream output = new DataOutputStream(dos);
                    output.writeUTF(msg);
                }
                catch(IOException e)
                {

                }
            }
        }
    }

}