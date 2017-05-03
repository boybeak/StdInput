package com.example.boybe.stdinput;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;

/**
 * Created by boybe on 2017/5/2.
 */

public class StdInputLayout extends ViewGroup {

    private static final String TAG = StdInputLayout.class.getSimpleName();

    private static final int MIN_HEIGHT_DP = 40, BTN_PADDING_DP = 8;

    private int mMinHeightPx = 0, mBtnPadding = 0;

    private AppCompatEditText mInputEt;
    private AppCompatImageView mSendIv, mVoiceIv;
    private GLAudioVisualizationView mAvView;

    private AudioVisualization mAv;

    private boolean isInputResized = false;

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            animShowOrHideSendBtn(s.length() > 0);
        }
    };

    public StdInputLayout(Context context) {
        this(context, null);
    }

    public StdInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StdInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThis(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StdInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initThis(context);
    }

    private void initThis (Context context) {

        float density = context.getResources().getDisplayMetrics().density;
        mMinHeightPx = (int)(MIN_HEIGHT_DP * density);
        mBtnPadding = (int)(BTN_PADDING_DP * density);

        mInputEt = new AppCompatEditText(context);
        mInputEt.setMaxLines(3);
        mInputEt.setTag("mInputEt");
        mInputEt.setHint("mInputEt");
        mInputEt.setPadding(mBtnPadding, mBtnPadding / 2, mBtnPadding, mBtnPadding / 2);

        mAvView = new GLAudioVisualizationView.Builder(getContext())
                .setBubblesSize(20f)
                .setBubblesRandomizeSize(true)
                .setWavesHeight(30f)
                .setWavesFooterHeight(5f)
                .setWavesCount(7)
                .setLayersCount(3)
                .setBackgroundColor(Color.CYAN)
                .setLayerColors(new int[]{Color.GREEN, Color.BLUE, Color.YELLOW})
                .setBubblesPerLayer(16)
                .build();
        mAv = (AudioVisualization)mAvView;

        mSendIv = new AppCompatImageView(context);
        mSendIv.setTag("mSendIv");
        mSendIv.setImageResource(R.drawable.ic_send);
        mSendIv.setVisibility(GONE);
        mSendIv.setAlpha(0f);
        mSendIv.setPadding(mBtnPadding, mBtnPadding, mBtnPadding, mBtnPadding);

        mVoiceIv = new AppCompatImageView(context);
        mVoiceIv.setTag("mVoiceIv");
        mVoiceIv.setImageResource(R.drawable.ic_microphone);
        mVoiceIv.setPadding(mBtnPadding, mBtnPadding, mBtnPadding, mBtnPadding);

        LayoutParams btnParams = new LayoutParams(mMinHeightPx, mMinHeightPx);

        addView(mVoiceIv, btnParams);
        addView(mSendIv, btnParams);
        addView(mInputEt);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mInputEt.addTextChangedListener(mWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mInputEt.removeTextChangedListener(mWatcher);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int minHeight = mMinHeightPx + getPaddingTop() + getPaddingBottom();

        int height = Math.min(heightSize, minHeight);

        final int childCount = getChildCount();

        Log.v(TAG, "onMeasure childCount=" + childCount);
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //Log.v(TAG, "child=" + child.getTag());
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            if (child == mSendIv || child == mVoiceIv) {
                if (!isInputResized) {
                    LayoutParams params = mInputEt.getLayoutParams();
                    params.width = widthSize - child.getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
                    params.height = LayoutParams.WRAP_CONTENT;
                    mInputEt.setLayoutParams(params);
                    isInputResized = true;
                }
            } else if (child == mInputEt) {
                height = Math.max(height, child.getMeasuredHeight() + getPaddingTop() + getPaddingBottom());
            }
            Log.v(TAG, "child=" + child.getTag() + " child.mWidth=" + child.getMeasuredWidth() + " child.mHeight=" + child.getMeasuredHeight());
        }
        Log.v(TAG, "onMeasure widthMode=" + widthMode + " heightMode=" + heightMode + " widthSize=" + widthSize + " heightSize=" + heightSize);
        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v(TAG, "onLayout changed=" + changed + " l=" + l + " t=" + t + " r=" + r + " b=" + b);
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child == mVoiceIv || child == mSendIv) {
                child.layout(r - child.getMeasuredWidth() - getPaddingRight(), b - child.getMeasuredHeight() - getPaddingBottom(), r - getPaddingRight(), b - getPaddingBottom());
            } else if (child == mInputEt) {
                Log.v(TAG, "onLayout mInput.mHeight=" + mInputEt.getMeasuredHeight());
                int top = (t + b - mInputEt.getMeasuredHeight()) / 2;
                int bottom = (t + b + mInputEt.getMeasuredHeight()) / 2;
                child.layout(l + getPaddingLeft(), top, r - mMinHeightPx - getPaddingRight(), bottom);
            }
        }
    }

    private void animShowOrHideSendBtn (boolean show) {
        if (mSendIv.getVisibility() == VISIBLE && show) {
            return;
        }
        final AnimatorSet set = new AnimatorSet();
        ObjectAnimator sendAnim = null;
        ObjectAnimator voiceAnim = null;
        if (show) {
            sendAnim = ObjectAnimator.ofFloat(mSendIv, "alpha", mSendIv.getAlpha(), 1f);
            voiceAnim = ObjectAnimator.ofFloat(mVoiceIv, "alpha", mVoiceIv.getAlpha(), 0f);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mSendIv.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mVoiceIv.setVisibility(GONE);
                    set.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            sendAnim = ObjectAnimator.ofFloat(mSendIv, "alpha", mSendIv.getAlpha(), 0f);
            voiceAnim = ObjectAnimator.ofFloat(mVoiceIv, "alpha", mVoiceIv.getAlpha(), 1f);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mVoiceIv.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mSendIv.setVisibility(GONE);
                    set.removeAllListeners();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        set.play(sendAnim).with(voiceAnim);
        set.setDuration(getContext().getResources().getInteger(android.R.integer.config_shortAnimTime));
        set.start();
    }

}
