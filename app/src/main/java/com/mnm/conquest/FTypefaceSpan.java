package com.mnm.conquest;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class FTypefaceSpan extends MetricAffectingSpan
{
    private Typeface mTypeface;

    public FTypefaceSpan(Context context, String typefaceName)
    {
        mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                .getAssets(), String.format("%s", typefaceName));
    }

    @Override
    public void updateMeasureState(TextPaint p)
    {
        p.setTypeface(mTypeface);

        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp)
    {
        tp.setTypeface(mTypeface);

        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
}