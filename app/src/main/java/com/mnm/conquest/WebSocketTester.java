package com.mnm.conquest;

import android.util.Log;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class WebSocketTester
{
    private WebSocketConnection connection = new WebSocketConnection();
    private static final String SERVER_IP = "ws://192.168.0.105:8181/";

    public WebSocketTester()
    {

    }

    public void connect()
    {
        try
        {
            connection.connect(SERVER_IP, new WebSocketHandler()
            {
                @Override
                public void onOpen()
                {
                    super.onOpen();

                    Log.d("Tester", "Connection open");
                }

                @Override
                public void onTextMessage(String payload)
                {
                    super.onTextMessage(payload);
                }

                @Override
                public void onClose(int code, String reason)
                {
                    super.onClose(code, reason);
                }
            });
        }
        catch (WebSocketException e)
        {
            Log.d("Tester", e.getMessage());
        }
    }

    public void send(String text)
    {
        connection.sendTextMessage(text);
    }

    public boolean isConnected()
    {
        return connection.isConnected();
    }
}
