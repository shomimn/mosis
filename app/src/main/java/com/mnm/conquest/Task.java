package com.mnm.conquest;


import android.app.ProgressDialog;

import com.nineoldandroids.animation.AnimatorSet;

import org.json.JSONException;
import org.json.JSONObject;

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

        public void templatedExecute()
        {
            execute();
        }
    }

    public static abstract class Waitable extends Ui
    {
        private int responseCode;
        private String responseMessage;

        public Waitable()
        {
            type = SERVER;
        }

        @Override
        public void templatedExecute()
        {
            ServerConnection.getHandler().setWaitingTask(this);
            super.templatedExecute();
        }

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

    public static class Login extends Waitable
    {
        private ProgressDialog progressDialog;
        private String username;
        private String password;
        private AnimatorSet animSetLogIn;

        public Login(ProgressDialog dialog, String user, String pw, AnimatorSet animSet)
        {
            super();

            progressDialog = dialog;
            username = user;
            password = pw;
            animSetLogIn = animSet;
        }

        @Override
        public void execute()
        {
            if (ServerConnection.isConnected())
            {
                ServerConnection.getHandler().setWaitingTask(this);
                ServerConnection.login(username, password);
                synchronize();
            }
            else
            {
                setResponseCode(ServerConnection.Response.FAILURE);
                setResponseMessage("Server not available, try again");
            }
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
                        animSetLogIn.start();
                }
            }, 500);
        }
    }
}
