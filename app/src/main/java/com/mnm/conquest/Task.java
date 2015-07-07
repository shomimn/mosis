package com.mnm.conquest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.EditText;

import com.nineoldandroids.animation.AnimatorSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class Task
{
    public static final int TOTAL = 2;

    public static final int SERVER = 0;
    public static final int GENERAL = 1;

    public int type;

    public Task()
    {
    }

    public Task(int t)
    {
        type = t;
    }

    public abstract void execute();

    public static abstract class Ui extends Task
    {
        public Ui()
        {
        }

        public Ui(int t)
        {
            super(t);
        }

        public abstract void uiExecute();
    }

    public static abstract class Waitable extends Ui
    {
        protected int responseCode;
        protected String responseMessage;

        public Waitable()
        {
            type = SERVER;
        }

        @Override
        public void execute()
        {
            if (ServerConnection.isValid())
            {
                ServerConnection.getHandler().setWaitingTask(this);
                executeImpl();
                synchronize();
            }
            else
            {
                setResponseCode(ServerConnection.Response.FAILURE);
                setResponseMessage("Server not available, try again");
            }
        }

        public abstract void executeImpl();

        public void setResponse(String r)
        {
            try
            {
                JSONObject json = new JSONObject(r);
                JSONObject data = json.getJSONObject("data");

                responseCode = data.getInt("code");
                responseMessage = data.getString("message");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        public void setResponseCode(int code)
        {
            responseCode = code;
        }

        public int getResponseCode()
        {
            return responseCode;
        }

        public void setResponseMessage(String msg)
        {
            responseMessage = msg;
        }

        public String getResponseMessage()
        {
            return responseMessage;
        }

        protected void synchronize()
        {
            synchronized (this)
            {
                try
                {
                    this.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Login extends Data
    {
        private ProgressDialog progressDialog;
        private String username;
        private String password;
        private AnimatorSet animSetLogIn;

        public Login(ProgressDialog dialog, String user, String pw, AnimatorSet animSet, DataReadyCallback callback)
        {
            super(user, callback);

            progressDialog = dialog;
            username = user;
            password = pw;
            animSetLogIn = animSet;
        }

        @Override
        public void executeImpl()
        {
            ServerConnection.login(username, password);
        }

        @Override
        public void uiExecute()
        {
            progressDialog.setMessage(getResponseMessage());

            TaskManager.getMainHandler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog.dismiss();
                    if (getResponseCode() == ServerConnection.Response.SUCCESS)
                    {
                        SharedPreferences sharedPrefs = ConquestApplication.getContext()
                                .getSharedPreferences(ConquestApplication.SHARED_PREF_KEY, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("username", username);
                        editor.commit();

                        animSetLogIn.setDuration(500);
                        animSetLogIn.start();
                    }
                }
            }, 1000);
        }
    }

    public static class Logout extends Task.Ui
    {
        private EditText username;
        private EditText password;
        private AnimatorSet animSetLogOut;

        public Logout(EditText user, EditText pw, AnimatorSet animSet)
        {
            username = user;
            password = pw;
            animSetLogOut = animSet;
        }

        @Override
        public void execute()
        {
            username.setText("");
            password.setText("");
            SharedPreferences.Editor editor = ConquestApplication.getContext()
                    .getSharedPreferences(ConquestApplication.SHARED_PREF_KEY, Context.MODE_PRIVATE).edit();
            editor.remove("username");
            editor.commit();
        }

        @Override
        public void uiExecute()
        {
            animSetLogOut.setDuration(500);
            animSetLogOut.start();
        }
    }

    public static class Register extends Waitable
    {
        private ProgressDialog progressDialog;
        private Bundle userInfo;
        private Bitmap photo;
        private Activity activity;
        private boolean register;

        public Register(ProgressDialog dialog, Activity a, Bundle info, Bitmap p, boolean reg)
        {
            progressDialog = dialog;
            activity = a;
            userInfo = info;
            photo = p;
            register = reg;
        }

        @Override
        public void executeImpl()
        {
            ServerConnection.register(userInfo, photo, register ? ServerConnection.Request.REGISTER : ServerConnection.Request.UPDATE);
        }

        @Override
        public void uiExecute()
        {
            progressDialog.setMessage(getResponseMessage());

            TaskManager.getMainHandler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog.dismiss();
                    if (getResponseCode() == ServerConnection.Response.SUCCESS)
                        activity.finish();
                }
            }, 1000);
        }
    }

    public static class Data extends Waitable
    {
        public static abstract class DataReadyCallback
        {
            private JSONArray data;

            public abstract void dataReady();

            public void setData(JSONArray d)
            {
                data = d;
            }

            public JSONArray getData()
            {
                return data;
            }
        }

        protected JSONArray data;
        protected String username;
        protected DataReadyCallback callback;

        public Data(String u, DataReadyCallback c)
        {
            username = u;
            callback = c;
        }

        @Override
        public void executeImpl()
        {
            ServerConnection.getData(username);
        }

        @Override
        public void uiExecute()
        {
        }

        @Override
        public void setResponse(String r)
        {
            try
            {
                JSONObject json = new JSONObject(r);

                setResponseCode(json.getInt("code"));
                setResponseMessage(json.getString("message"));

                data = json.getJSONArray("data");
                callback.setData(data);
                callback.dataReady();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        public JSONArray getData()
        {
            return data;
        }

    }

    public static class Ally extends Data
    {
        private ProgressDialog progressDialog;
        private String ally;
        private boolean add;
        public Ally(String u, ProgressDialog d,String a,boolean add, DataReadyCallback callback)
        {
            super(u, callback);
            progressDialog = d;
            username = u;
            ally = a;
            this.add = add;
        }
        @Override
        public void executeImpl()
        {
            if(add)
                ServerConnection.addAlly(username,ally);
            else
                ServerConnection.deleteAlly(username, ally);
        }

        @Override
        public void uiExecute()
        {
            progressDialog.setMessage(getResponseMessage());
            TaskManager.getMainHandler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    progressDialog.dismiss();
                }
            }, 1000);
        }
        @Override
        public void setResponse(String r)
        {
            try
            {
                JSONObject json = new JSONObject(r);
                JSONObject obj = json.getJSONObject("data");
                responseCode = obj.getInt("code");
                responseMessage = obj.getString("message");
                data = obj.getJSONArray("data");
                callback.setData(data);
                callback.dataReady();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
