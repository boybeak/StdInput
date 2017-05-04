package com.example.boybe.stdinput;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Queue;
import java.util.Random;

/**
 * Created by boybe on 2017/5/2.
 */

public class WaveformView extends View {

    private static final String TAG = WaveformView.class.getSimpleName();

    private static final int AMP_MAX = 32767 / 128;

    private static final int DEFAULT_BAR_WIDTH_DP = 8, DEFAULT_GAP_WIDTH_DP = 2, DEFAULT_PERIOD = 1000;

    private byte[] mByteArray;

    private int mCursor = 0, mCount = 50;
    private float mBarWidth, mGapWidth;
    private long mPeriod = DEFAULT_PERIOD, mMovePeriod = 20, mLastNewBarTime = 0;

    private int mBarColor;

    private boolean isStarted = false;

    private Paint mPaint, mTextPaint;

    public WaveformView(Context context) {
        this(context, null);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThis(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initThis(context, attrs);
    }

    private void initThis (Context context, @Nullable AttributeSet attrs) {

        mPaint = new Paint();
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);

        final float density = context.getResources().getDisplayMetrics().density;

        final float barWidthDef = density * DEFAULT_BAR_WIDTH_DP;
        final float gapWidthDef = density * DEFAULT_GAP_WIDTH_DP;

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaveformView);
            mBarColor = array.getColor(R.styleable.WaveformView_barColor, Color.GREEN);
            mPaint.setColor(mBarColor);
            mBarWidth = array.getDimensionPixelSize(R.styleable.WaveformView_barWidth, 0);
            mPeriod = array.getInt(R.styleable.WaveformView_period, DEFAULT_PERIOD);
            if (mBarWidth == 0) {
                mBarWidth = barWidthDef;
            }
            mGapWidth = array.getDimensionPixelSize(R.styleable.WaveformView_gapWidth, 0);
            if (mGapWidth == 0) {
                mGapWidth = gapWidthDef;
            }
            array.recycle();
        } else {


            mPaint.setColor(Color.GREEN);
        }

        mByteArray = new byte[1024];
        for (int i = 0; i < mByteArray.length; i++) {
            if (mByteArray[i] <= 0) {
                mByteArray[i] = 2;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCount = (int) Math.ceil(getMeasuredWidth() / (mBarWidth + mGapWidth));
        mMovePeriod = (int)(mPeriod / (mBarWidth + mGapWidth));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStarted) {
            drawBars(canvas);
            postInvalidate();
        }
    }

    private void drawBars (Canvas canvas) {
        long now = SystemClock.elapsedRealtime();

        long delta = now - mLastNewBarTime;

        float move = delta / mMovePeriod;

        if (mCursor >= mByteArray.length) {
            return;
        }

        int offset = 0;
        for (int i = mCursor; i > mCursor - mCount && i > 0; i--) {
            float left = getLeft() + (mGapWidth + mBarWidth) * offset + mGapWidth + move;
            float right = left + mBarWidth;
            float bottom = getBottom() - getPaddingBottom();
            if (offset == 0) {

                float remain = mByteArray[i] * ((float)delta / mPeriod) * 4;
                if (remain > mByteArray[i]) {
                    remain = mByteArray[i];
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - mByteArray[i], right, bottom, mPaint);
            }
            canvas.drawText(i + "", left, getBottom() - 30, mTextPaint);
            canvas.drawText(mByteArray[i] + "", left, getBottom() - 15, mTextPaint);
            offset++;
        }
        if (delta > mPeriod) {
            mCursor++;
            mLastNewBarTime = now;
        }
    }

    /*public void addWave (byte wave) {
        mByteArray.add(wave);
    }*/

    public void start () {
        isStarted = true;
        //mLastNewBarTime = SystemClock.elapsedRealtime();
        postInvalidate();
    }

    public void putInt (int amp) {
        putByte((byte)(amp / AMP_MAX));
    }

    private void putByte (byte b) {
        if (mCursor < mByteArray.length - 1) {
            if (b <= 0) {
                b = 1;
            }
            mByteArray[mCursor + 1] = b;
        }
    }

}
