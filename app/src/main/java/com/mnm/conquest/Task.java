package com.mnm.conquest;


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

        public int getResponseCode()
        {
            return responseCode;
        }

        public String getResponseMessage()
        {
            return responseMessage;
        }
    }
}
