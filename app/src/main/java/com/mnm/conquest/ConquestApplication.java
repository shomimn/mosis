package com.mnm.conquest;

import android.app.Application;
import android.content.Context;

public class ConquestApplication extends Application
{
    private static ConquestApplication instance;

    public ConquestApplication()
    {
        instance = this;
        ServerConnection.connect();
    }

    public static Context getContext()
    {
        return instance;
    }
}
