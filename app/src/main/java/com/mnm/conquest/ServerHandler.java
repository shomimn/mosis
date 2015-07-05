package com.mnm.conquest;

import android.util.Log;

import com.mnm.conquest.ecs.Game;

import de.tavendo.autobahn.WebSocketHandler;

public class ServerHandler extends WebSocketHandler
{
    private Task.Waitable waitingTask;

    public interface ResponseCallback
    {
        void setResponse(String payload);
    }

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
        else
            Game.asyncUpdate(payload);
    }

    @Override
    public void onRawTextMessage(byte[] payload)
    {
        super.onRawTextMessage(payload);
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
