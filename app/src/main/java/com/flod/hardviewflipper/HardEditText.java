package com.flod.hardviewflipper;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-01-09
 * UseDes:
 * TODO setErr 图标的处理
 */
public class HardEditText extends AppCompatEditText {


    private static final int DEFAULT_CLEAR_ICON = R.drawable.ic_clear_black_24dp;
    private static final int DEFAULT_VISIBLE_ICON = R.drawable.ic_visibility_black_24dp;
    private static final int DEFAULT_INVISIBLE_ICON = R.drawable.ic_visibility_off_black_24dp;

    private final int DEFAULT_BTN_SIZE = getResources().getDimensionPixelSize(R.dimen.HardEditText_icon_size);
    private final int DEFAULT_BTN_PADDING = getResources().getDimensionPixelSize(R.dimen.HardEditText_icon_padding);

    private int mClearBtnResId;
    private int mVisibleBtnResId;
    private int mInvisibleBtnResId;

    private Bitmap mClearBtnBitmap;
    private Bitmap mVisibleBtnBitmap;
    private Bitmap mInvisibleBtnBitmap;

    private int mBtnSize = DEFAULT_BTN_SIZE; //图标的宽度和高度
    private int mBtnPadding = DEFAULT_BTN_PADDING;

    private Paint mPaint;
    private int mPaddingRight;//右边的内边距  //TODO


    private boolean enableClearBtn = true;
    private boolean enableSeePassword = true;  //密码是否是

    private boolean drawClearBtn;
    private boolean drawSeePwBtn;  //密码是否是

    private boolean isClearBtnVisible;
    private boolean isPwVisible;

    private boolean visibleBtnAnimRunning = false;
    private boolean clearBtnAnimRunning = false;

    private ValueAnimator mHideAnimator;        //清除按钮消失动画
    private ValueAnimator mShowAnimator;  //清除按钮出现动画
    private static final int CLEAR_ICON_ANIMATOR_TIME = 200;

    private boolean isVisible = true;

    public HardEditText(Context context) {
        super(context);
    }

    public HardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public HardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        //抗锯齿，很牛逼
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HardEditText);
            mClearBtnResId = array.getResourceId(R.styleable.HardEditText_clearDrawable, DEFAULT_CLEAR_ICON);
            mVisibleBtnResId = array.getResourceId(R.styleable.HardEditText_visibleDrawable, DEFAULT_VISIBLE_ICON);
            mInvisibleBtnResId = array.getResourceId(R.styleable.HardEditText_invisibleDrawable, DEFAULT_INVISIBLE_ICON);
            array.recycle();
        }

        //拿到三个Icon的bitmap
        mClearBtnBitmap = getBitmap(context, mClearBtnResId);
        mVisibleBtnBitmap = getBitmap(context, mVisibleBtnResId);
        mInvisibleBtnBitmap = getBitmap(context, mInvisibleBtnResId);


        //按钮出现和消失的动画
        mHideAnimator = ValueAnimator.ofFloat(1f, 0f).setDuration(CLEAR_ICON_ANIMATOR_TIME);
        mShowAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(CLEAR_ICON_ANIMATOR_TIME);

        //enableSeePassword = getInputType() != (InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置右边距（为右边图标提供位置）
        setPadding(getPaddingLeft(), getPaddingTop(), mPaddingRight, getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //需要注意editView 会不断地draw,每次draw后原先的draw的icon都会消失
        mBtnSize = getHeight() - 2 * mBtnPadding;
        drawBtn(canvas);


    }


    private void drawBtn(Canvas canvas) {
        float showValue = (float) mShowAnimator.getAnimatedValue();
        float hideValue = (float) mHideAnimator.getAnimatedValue();

        //clearBtn相关的动画
        if (enableClearBtn) {
            if (clearBtnAnimRunning) {
                if (isClearBtnVisible) {
                    //出现动画
                    if (mShowAnimator.isStarted()) {
                        drawClearBtn(showValue, canvas);
                        invalidate();  //keep draw
                    } else {
                        drawClearBtn(1, canvas);
                        clearBtnAnimRunning = false;
                    }
                } else {
                    //消失动画
                    if (mHideAnimator.isStarted()) {
                        drawClearBtn(hideValue, canvas);
                        invalidate();  //keep draw
                    } else {
                        drawClearBtn(0, canvas);
                        clearBtnAnimRunning = false;
                    }
                }
            } else {
                if (isClearBtnVisible) drawClearBtn(1, canvas);
            }

        }


        //VisibleBtn相关的动画
        //状态转换：可见消失->不可见出现 或者 不可见消失->可见出现
        if (enableSeePassword) {
            if (visibleBtnAnimRunning) {
                if (mHideAnimator.isStarted()) {
                    drawVisibleBtn(hideValue, canvas, isPwVisible);
                    invalidate();
                } else {
                    drawVisibleBtn(0, canvas, isPwVisible);
                    mShowAnimator.start(); //消失动画结束隐藏动画开始
                    isPwVisible = !isPwVisible; //置换状态
                    invalidate();
                    return;
                }

                if (!mHideAnimator.isStarted()) {
                    if (mShowAnimator.isStarted()) {
                        drawVisibleBtn(showValue, canvas, isPwVisible);
                        invalidate();
                    } else {
                        drawVisibleBtn(1, canvas, isPwVisible);
                        visibleBtnAnimRunning = false;
                    }
                }

            } else {
                drawVisibleBtn(1, canvas, isPwVisible);
            }
        }


    }

    private void drawClearBtn(float animValue, Canvas canvas) {
        //TODO 简化
        int right = (int) (getWidth() - mBtnSize - 2 * mBtnPadding - (mBtnSize - mBtnSize * animValue) / 2);
        int left = (int) (right - mBtnSize * animValue);
        int top = (int) ((getHeight() - mBtnSize * animValue) / 2);
        int bottom = (int) (top + mBtnSize * animValue);
        Rect rect = new Rect(left, top, right, bottom);
        //src是对原图片裁剪，第二个是裁剪后的位置
        canvas.drawBitmap(mClearBtnBitmap, null, rect, mPaint);
    }

    private void drawVisibleBtn(float animValue, Canvas canvas, boolean isVisible) {
        int right = (int) (getWidth() - mBtnPadding - (mBtnSize - mBtnSize * animValue) / 2);
        int left = (int) (right - mBtnSize * animValue);
        int top = (int) ((getHeight() - mBtnSize * animValue) / 2);
        int bottom = (int) (top + mBtnSize * animValue);
        Rect rect = new Rect(left, top, right, bottom);

        if (isVisible) canvas.drawBitmap(mVisibleBtnBitmap, null, rect, mPaint);
        else canvas.drawBitmap(mInvisibleBtnBitmap, null, rect, mPaint);
    }


    private Bitmap getBitmap(Context context, int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable != null) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (enableClearBtn) {
            if (focused && getText() != null && getText().length() > 0) {
                if (!isClearBtnVisible) setClearBtnVisible(true);
            } else {
                if (isClearBtnVisible) setClearBtnVisible(false);
            }
        }

    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (enableClearBtn) {
            if (text.length() > 0) {
                if (!isClearBtnVisible) setClearBtnVisible(true);
            } else {
                if (isClearBtnVisible) setClearBtnVisible(false);
            }

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (enableClearBtn || enableSeePassword) {
                //只判断水平上是否有在范围内
                boolean isClearBtnTouch = getWidth() - (mBtnSize * 2 + 2 * mBtnPadding) < event.getX()
                        && getWidth() - (mBtnSize + 2 * mBtnPadding) > event.getX();
                boolean isSeePwBtnTouch = getWidth() - mBtnSize - mBtnPadding < event.getX()
                        && getWidth() - mBtnPadding > event.getX();
                if (isClearBtnTouch && enableClearBtn) {
                    setText("");
                } else if (isSeePwBtnTouch && enableSeePassword) {
                    if (isPwVisible) {
                        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        setSelection(getText().length());
                    } else {
                        setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        setSelection(getText().length());
                    }
                    setPwVisible(isPwVisible);
                    return true;
                }

            }


        }
        return super.onTouchEvent(event);
    }

    private void setClearBtnVisible(boolean visible) {
        mShowAnimator.end();
        mHideAnimator.end();
        isClearBtnVisible = visible;
        clearBtnAnimRunning = true;
        if (visible) {
            mShowAnimator.start();

        } else {
            mHideAnimator.start();
        }
        invalidate();
    }


    private void setPwVisible(boolean visible) {
        mShowAnimator.end();
        mHideAnimator.end();
        visibleBtnAnimRunning = true;
        //isPwVisible = visible;
        mHideAnimator.start();
        invalidate();
    }


}
