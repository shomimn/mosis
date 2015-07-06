package com.mnm.conquest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class FButton extends Button
{
    public FButton(Context context)
    {
        super(context);
        init();
    }

    public FButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setTypeface(ConquestApplication.font);
    }
}
