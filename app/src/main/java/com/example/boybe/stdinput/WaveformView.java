package com.example.boybe.stdinput;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by boybe on 2017/5/2.
 */

public class WaveformView extends View {

    private static final String TAG = WaveformView.class.getSimpleName();

    private byte[] mByteArray;

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
        initThis(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initThis(context);
    }

    private void initThis (Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);

        mByteArray = new byte[1024];
        Random random = new Random();
        for (int i = 0; i < mByteArray.length; i++) {
            random.nextBytes(mByteArray);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            Log.v(TAG, "AT_MOST");
        } else if (heightMode == MeasureSpec.EXACTLY) {
            Log.v(TAG, "EXACTLY");
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            Log.v(TAG, "UNSPECIFIED");
        }

        setMeasuredDimension(widthSize, 128);
        Log.v(TAG, "onMeasure widthMode=" + widthMode + " heightMode=" + heightMode + " widthSize=" + widthSize + " heightSize=" + heightSize);*/
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < 20; i++) {
            int left = getLeft() + 40 * i;
            int right = left + 10;
            canvas.drawRect(left, getBottom() / 2 - mByteArray[i], right, getBottom() / 2, mPaint);
            canvas.drawText(i + "", left, getBottom() - 30, mTextPaint);
            canvas.drawText(mByteArray[i] + "", left, getBottom() - 15, mTextPaint);
        }
        if (isStarted) {
            postInvalidate();
        }
    }

    /*public void addWave (byte wave) {
        mByteArray.add(wave);
    }*/

    public void start () {
        isStarted = true;
        postInvalidate();
    }
}
