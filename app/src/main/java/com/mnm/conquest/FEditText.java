package com.mnm.conquest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class FEditText extends EditText
{
    public FEditText(Context context)
    {
        super(context);
        init();
    }

    public FEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setTypeface(ConquestApplication.font);
    }
}
