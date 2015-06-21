package com.mnm.conquest;

import android.os.Message;
import android.util.Log;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class ServerConnection
{
    private static final String SERVER_IP = "ws://192.168.0.11:8181/";

    private static ServerConnection instance = new ServerConnection();
    private static WebSocketConnection socket;
    private static WebSocketHandler handler;
    private static final Object monitor = new Object();
    private static String response;

    private ServerConnection()
    {
        socket = new WebSocketConnection();
        handler = new WebSocketHandler()
        {
            @Override
            public void onOpen()
            {
                super.onOpen();
            }

            @Override
            public void onTextMessage(String payload)
            {
                super.onTextMessage(payload);

                Log.d("WebSocket", payload);

                if (payload.equals("confirmed"))
                {
                    synchronized (monitor)
                    {
                        response = payload;
                        monitor.notify();
                    }
                }
            }

            @Override
            public void onBinaryMessage(byte[] payload)
            {
                super.onBinaryMessage(payload);
            }

            @Override
            public void onClose(int code, String reason)
            {
                super.onClose(code, reason);
            }
        };
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
            socket.sendTextMessage("{ type: 0, data: { username: '" + username + "', password: '" + password + "' } }");
            synchronized (monitor)
            {
                try
                {
                    monitor.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
