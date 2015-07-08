package com.mnm.conquest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
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
        public static final int POSITION = 6;
        public static final int INIT = 7;
        public static final int UPDATE_FIELDS_INT = 8;
        public static final int UPDATE_FIELDS_STRING = 9;
        public static final int NEW_FORTRESS = 10;
        public static final int DELETEALLY = 11;
        public static final int NEWALLY = 12;
        public static final int FORTRESSES = 13;
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
    public static void logout(String username)
    {
        if (socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", Request.LOGOUT).put("data", new JSONObject().put("username", username));
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
                data.put("health", 100);
                data.put("attack", 10);
                data.put("defense", 10);
                data.put("interceptors", 2);
                data.put("scouts", 3);
                data.put("fighters", 1);
                data.put("gunships", 1);
                data.put("bombers", 1);
                data.put("coins", 100);
                data.put("fortresses", new JSONArray());
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

    public static void sendPosition(String username, double latitude, double longitude, int type)
    {
        if (socket.isConnected())
        {
            try
            {
                JSONObject json = new JSONObject().put("type", type)
                        .put("data", new JSONObject().put("username", username).put("latitude", latitude).put("longitude", longitude));
                socket.sendTextMessage(json.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void updateField(String username, String field, String value)
    {
        try
        {
            JSONObject json = new JSONObject().put("type", Request.UPDATE_FIELDS_STRING)
                    .put("data", new JSONObject().put("username", username).put("field", field).put("value", value));
            socket.sendTextMessage(json.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public static void updateField(String username, String field, int value)
    {
        try
        {
            JSONObject json = new JSONObject().put("type", Request.UPDATE_FIELDS_INT)
                    .put("data", new JSONObject().put("username", username).put("field", field).put("value", value));
            socket.sendTextMessage(json.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public static void sendFortress(String username, double latitude, double longitude, String markerName)
    {
        try
        {
            JSONObject json = new JSONObject().put("type", Request.NEW_FORTRESS);

            JSONObject fortress = new JSONObject();
            fortress.put("username", username);
            fortress.put("latitude", latitude);
            fortress.put("longitude", longitude);
            fortress.put("marker", markerName);
            fortress.put("health", 100);
            fortress.put("defense", 10);
            fortress.put("attack", 10);
            fortress.put("interceptors", 2);
            fortress.put("scouts", 3);
            fortress.put("fighters", 1);
            fortress.put("gunships", 1);
            fortress.put("bombers", 1);

            json.put("data", fortress);

            socket.sendTextMessage(json.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public static void getFortresses()
    {
        try
        {
            JSONObject json = new JSONObject().put("type", Request.FORTRESSES);
            socket.sendTextMessage(json.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
