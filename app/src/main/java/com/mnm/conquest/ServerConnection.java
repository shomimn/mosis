package com.mnm.conquest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.mnm.conquest.ecs.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import de.tavendo.autobahn.WebSocketOptions;

public class ServerConnection
{

    private static final String SERVER_IP = "ws://192.168.0.103:8181/";

    private static ServerConnection instance = new ServerConnection();
    private static WebSocketConnection socket;
    private static WebSocketOptions options;
    private static ServerHandler handler;

    public static class Request
    {
        public static final int LOGIN = 0;
        public static final int LOGOUT = 1;
        public static final int REGISTER = 2;
        public static final int DATA = 3;
        public static final int UPDATE = 4;
        public static final int ALLIES = 5;
        public static final int NEWALLY = 10;
        public static final int DELETEALLY = 11;
        private Request() {}
    }

    public static class Response
    {
        public static final int SUCCESS = 0;
        public static final int FAILURE = 1;

        private Response() {}
    }

    private ServerConnection()
    {
        socket = new WebSocketConnection();
        handler = new ServerHandler();
        options = new WebSocketOptions();
        options.setMaxFramePayloadSize(options.getMaxFramePayloadSize() * 10);
        options.setMaxMessagePayloadSize(options.getMaxMessagePayloadSize() * 10);
    }

    public static void connect()
    {
        try
        {
            socket.connect(SERVER_IP, handler, options);
        }
        catch (WebSocketException e)
        {
            e.printStackTrace();
        }
    }

    public static void send(String text)
    {
        if (socket.isConnected())
            socket.sendTextMessage(text);
    }

    public static void send(byte[] bytes)
    {
        if (socket.isConnected())
            socket.sendBinaryMessage(bytes);
    }

    public static void sendRaw(byte[] bytes)
    {
        if (socket.isConnected())
            socket.sendRawTextMessage(bytes);
    }

    public static void login(String username, String password)
    {
        if (socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", Request.LOGIN).put("data", new JSONObject().put("username", username).put("password", password));
                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static ServerHandler getHandler()
    {
        return handler;
    }

    public static boolean isValid()
    {
        return socket.isConnected();
    }

    public static void register(Bundle userInfo, Bitmap photo, int type)
    {
        if (socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", type);

                JSONObject data = new JSONObject();
                data.put("_id", userInfo.getString("username"));
                data.put("name", userInfo.getString("name"));
                data.put("lastname", userInfo.getString("lastname"));
                data.put("email", userInfo.getString("email"));
                data.put("username", userInfo.getString("username"));
                data.put("password", userInfo.getString("password"));
                data.put("marker", userInfo.getString("marker"));
                data.put("allies",new JSONArray() );

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();

                data.put("photo", Base64.encodeToString(byteArray, Base64.DEFAULT));
                json.put("data", data);

                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void getData(String username)
    {
        if (socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", Request.DATA).put("data", new JSONObject().put("username", username));
                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void getAllies(String username)
    {
        if(socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", Request.ALLIES)
                        .put("data", new JSONObject().put("username", username));
                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void addAlly(String username, String ally)
    {
        if(socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", Request.NEWALLY)
                        .put("data", new JSONObject().put("username", username).put("ally", ally));
                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void deleteAlly(String username, String ally)
    {
        if(socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", Request.DELETEALLY)
                        .put("data", new JSONObject().put("username", username).put("ally", ally));
                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

}
