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
    private static WaitingHandler handler;
    private static final Object monitor = new Object();

    public static class Response
    {
        public static final int SUCCESS = 0;
        public static final int FAILURE = 1;

        private Response() {}
    }

    public static class WaitingHandler extends WebSocketHandler
    {
        private Task.Waitable waitingTask;

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

            if (waitingTask != null)
            {
                synchronized (waitingTask)
                {
                    waitingTask.setResponse(payload);
                    waitingTask.notify();
                }
                waitingTask = null;
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

        public void setWaitingTask(Task.Waitable task)
        {
            waitingTask = task;
        }
    }

    private ServerConnection()
    {
        socket = new WebSocketConnection();
        handler = new WaitingHandler();
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
            socket.sendTextMessage("{ type: 0, data: { username: '" + username + "', password: '" + password + "' } }");
    }

    public static WaitingHandler getHandler()
    {
        return handler;
    }

    public static boolean isConnected()
    {
        return socket.isConnected();
    }

}
