package com.mnm.conquest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FTextView extends TextView
{
    public FTextView(Context context)
    {
        super(context);
        init();
    }

    public FTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setTypeface(ConquestApplication.font);
    }
}
