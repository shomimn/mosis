package com.mnm.conquest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.google.android.gms.maps.model.Circle;


/**
 * Created by Tomasevic on 20.6.2015.
 */
public class CircularView extends View implements View.OnTouchListener {

    int count = 5;
    int currentRadius;
    Rect[] rect= new Rect[count];

    private Animation anim;

    private final static int TOTAL_DEGREE = 360;
    private final static int START_DEGREE = -90;

    private Paint mPaint;
    private RectF mOvalRect = null;

    private int mItemCount = count;
    private int mSweepAngle;

    private int mInnerRadius;
    private int mOuterRadius;
    private Bitmap mCenterIcon;
    private int[] mColors = {getResources().getColor(R.color.transparent_gray), getResources().getColor(R.color.transparent_gray), getResources().getColor(R.color.transparent_gray),
            getResources().getColor(R.color.transparent_gray),getResources().getColor(R.color.transparent_gray)};

    public CircularView(Context context) {
        this(context, null);

    }


    public CircularView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);

        mSweepAngle = TOTAL_DEGREE / mItemCount;

        Display display = ((WindowManager)ConquestApplication.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        int width = display.getWidth();
        int height = display.getHeight();

        mInnerRadius = width/6;
        mOuterRadius = (width/6)*3;


        currentRadius = mOuterRadius;

        mCenterIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        setOnTouchListener(this);
    }

    private void createAnimation(Canvas canvas, int x, int y)
    {
        anim = new RotateAnimation(0, 360, x, y);
        anim.setRepeatCount(0);
        anim.setDuration(1000L);
        startAnimation(anim);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int width = getWidth();
        int height = getHeight();

        if (mOvalRect == null)
            mOvalRect = new RectF(width / 2 - currentRadius, height / 2 - currentRadius, width / 2 + currentRadius, height / 2 + currentRadius);
        else
            mOvalRect.set(width / 2 - currentRadius, height / 2 - currentRadius, width / 2 + currentRadius, height / 2 + currentRadius);

        for (int i = 0; i < mItemCount; i++)
        {
            int startAngle = START_DEGREE + i * mSweepAngle;

            mPaint.setColor(mColors[i]);

            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawArc(mOvalRect, startAngle+2, mSweepAngle-2, true, mPaint);

            //mPaint.setColor(getResources().getColor(R.color.transparent));
            //mPaint.setStyle(Paint.Style.STROKE);
            //mPaint.setStrokeWidth(3);
            //canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            int centerX = (int) ((currentRadius + mInnerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2)));
            int centerY = (int) ((currentRadius + mInnerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2)));

            mPaint.setColor(getResources().getColor(R.color.transparent_gray));
            mPaint.setStyle(Paint.Style.FILL);

            if(anim == null)
                createAnimation(canvas, width/2, height/2);

            //canvas.drawCircle(width / 2 + centerX - mCenterIcon.getWidth() / 2 + mCenterIcon.getWidth() / 2, height / 2 + centerY - mCenterIcon.getHeight() / 2 + mCenterIcon.getHeight() / 2, mCenterIcon.getWidth(), mPaint);
            canvas.drawBitmap(mCenterIcon, width / 2 + centerX - mCenterIcon.getWidth() / 2, height / 2 + centerY - mCenterIcon.getHeight() / 2, null);

            if(rect[i] == null)
            rect[i] = new Rect(width / 2 + centerX - mCenterIcon.getWidth() / 2-10, height / 2 + centerY - mCenterIcon.getHeight() / 2-10,
                    width / 2 + centerX - mCenterIcon.getWidth() / 2+mCenterIcon.getWidth()+10, height / 2 + centerY - mCenterIcon.getHeight() / 2+mCenterIcon.getHeight()+10);
            else
                rect[i].set(width / 2 + centerX - mCenterIcon.getWidth() / 2-10, height / 2 + centerY - mCenterIcon.getHeight() / 2-10,
                        width / 2 + centerX - mCenterIcon.getWidth() / 2+mCenterIcon.getWidth()+10, height / 2 + centerY - mCenterIcon.getHeight() / 2+mCenterIcon.getHeight()+10);
        }
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mInnerRadius, mPaint);
        canvas.drawBitmap(mCenterIcon, width / 2 - mCenterIcon.getWidth() / 2, height / 2 - mCenterIcon.getHeight() / 2, null);

        super.onDraw(canvas);

//        currentRadius += 10;
//        if (currentRadius < mOuterRadius)
//            invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            for(int i=0; i<count; i++)
            {
                if(rect[i].contains((int)x, (int)y))
                {
                    for(int j=0; j<count; j++)
                        mColors[j] = getResources().getColor(R.color.transparent_gray);
                    mColors[i] = getResources().getColor(R.color.transparent_orange);
                    invalidate();
                }
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
                if(rect[0].contains((int)x, (int)y))
                {
                    this.setVisibility(View.GONE);
                    anim = null;
                    mColors[0] = getResources().getColor(R.color.transparent_gray);
                }
                else
                {
                    for(int i=0; i<count; i++)
                        mColors[i]=getResources().getColor(R.color.transparent_gray);
                    invalidate();
                }
        }
        return true;
    }
    public void setAnimNull()
    {
        anim = null;
    }
}
