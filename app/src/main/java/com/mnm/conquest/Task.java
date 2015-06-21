package com.mnm.conquest;


public abstract class Task
{
    public static final int SERVER = 0;
    public static final int GENERAL = 1;

    public int type;

    public Task(int t)
    {
        type = t;
    }

    public abstract void execute();

    public static abstract class Ui extends Task
    {
        public Ui(int t)
        {
            super(t);
        }

        public abstract void uiExecute();
    }
}
