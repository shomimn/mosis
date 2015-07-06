package com.mnm.conquest;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

public class ConquestApplication extends Application
{
    public static String SHARED_PREF_KEY = "PREF";
    public static Typeface font;

    private static ConquestApplication instance;

    public ConquestApplication()
    {
        instance = this;
        ServerConnection.connect();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        font = Typeface.createFromAsset(getAssets(), "kenvector_future.ttf");
    }

    public static Context getContext()
    {
        return instance;
    }
}
