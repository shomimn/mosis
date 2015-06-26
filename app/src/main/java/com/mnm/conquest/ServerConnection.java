package com.mnm.conquest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ServerConnection
{
    private static final String SERVER_IP = "ws://192.168.1.6:8181/";

    private static ServerConnection instance = new ServerConnection();
    private static WebSocketConnection socket;
    private static ServerHandler handler;
    private static final Object monitor = new Object();

    public static class Request
    {
        public static final int LOGIN = 0;
        public static final int LOGOUT = 1;

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
    }

    public static void connect()
    {
        try
        {
            socket.connect(SERVER_IP, handler);
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

}
