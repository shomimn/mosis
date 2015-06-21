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


/**
 * Created by Tomasevic on 20.6.2015.
 */
public class CircularView extends View implements View.OnTouchListener {

    int count = 3;
    int currentRadius;
    Rect[] rect= new Rect[count];
    Context con = null;

    private final static int TOTAL_DEGREE = 360;
    private final static int START_DEGREE = -90;

    private Paint mPaint;
    private RectF mOvalRect = null;

    private int mItemCount = count;
    private int mSweepAngle;

    private int mInnerRadius;
    private int mOuterRadius;
    private Bitmap mCenterIcon;
    private int[] mColors = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN};

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

        Log.d("width", String.valueOf(width));
        Log.d("width", String.valueOf(height));

        mInnerRadius = width/6;
        mOuterRadius = (width/6)*3;


        currentRadius = mInnerRadius;

        mCenterIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {


        int width = getWidth();
        int height = getHeight();


        if (mOvalRect == null)
            mOvalRect = new RectF(width / 2 - currentRadius, height / 2 - currentRadius, width / 2 + currentRadius, height / 2 + currentRadius);
        else
            mOvalRect.set(width / 2 - currentRadius, height / 2 - currentRadius, width / 2 + currentRadius, height / 2 + currentRadius);

        for (int i = 0; i < mItemCount; i++) {
            int startAngle = START_DEGREE + i * mSweepAngle;

            mPaint.setColor(getResources().getColor(R.color.transparent_gray));

            mPaint.setStyle(Paint.Style.FILL);
            //canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            //canvas.drawArc(mOvalRect, startAngle, mSweepAngle, true, mPaint);

            int centerX = (int) ((currentRadius + mInnerRadius) / 2 * Math.cos(Math.toRadians(startAngle + mSweepAngle / 2)));
            int centerY = (int) ((currentRadius + mInnerRadius) / 2 * Math.sin(Math.toRadians(startAngle + mSweepAngle / 2)));

            mPaint.setColor(getResources().getColor(R.color.transparent_gray));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2 + centerX - mCenterIcon.getWidth() / 2+mCenterIcon.getWidth()/2,height / 2 + centerY - mCenterIcon.getHeight() / 2+ mCenterIcon.getHeight()/2, mCenterIcon.getWidth(), mPaint);
            canvas.drawBitmap(mCenterIcon, width / 2 + centerX - mCenterIcon.getWidth() / 2, height / 2 + centerY - mCenterIcon.getHeight() / 2, null);

            if(rect[i] == null)
            rect[i] = new Rect(width / 2 + centerX - mCenterIcon.getWidth() / 2, height / 2 + centerY - mCenterIcon.getHeight() / 2,
                    width / 2 + centerX - mCenterIcon.getWidth() / 2+mCenterIcon.getWidth(), height / 2 + centerY - mCenterIcon.getHeight() / 2+mCenterIcon.getHeight());
            else
                rect[i].set(width / 2 + centerX - mCenterIcon.getWidth() / 2, height / 2 + centerY - mCenterIcon.getHeight() / 2,
                        width / 2 + centerX - mCenterIcon.getWidth() / 2+mCenterIcon.getWidth(), height / 2 + centerY - mCenterIcon.getHeight() / 2 + mCenterIcon.getHeight());
        }

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, height / 2, mInnerRadius, mPaint);
        canvas.drawBitmap(mCenterIcon, width / 2 - mCenterIcon.getWidth() / 2, height / 2 - mCenterIcon.getHeight() / 2, null);

        super.onDraw(canvas);

        currentRadius += 10;
        if (currentRadius < mOuterRadius)
            invalidate();


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for(int i=0; i<count; i++)
        {
            if(rect[i].contains((int)x, (int)y))
            {
                Log.d("touch", String.valueOf(i));
            }
        }

        if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
            Log.d("TouchTest", "Touch down");
        }
        return true;
    }
}
