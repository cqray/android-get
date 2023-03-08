package cn.cqray.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;

/**
 * 旋转控件
 * @author Cqray
 */
public class SpinView extends View {

    /** 消息 **/
    private static final int MESSAGE_WHAT = 1;
    /** 刷新间隔 **/
    private static final int INVALIDATE_DELAY = 15;

    private Paint paint;
    private RectF mArcRectF;
    private int mViewWidth;
    private int mViewHeight;
    /** 单页最小角度 **/
    private float mMinAngle;
    /** 单页最小角度 **/
    private float mAddAngle;
    /** 旋转速度 **/
    private float mRotateRate;
    /** 抖动比例 **/
    private float mSnakeRatio;
    /** 圆弧宽度 **/
    private float mStrokeWidth;
    /** 叶型圆弧角度间隔 **/
    private float mIntervalAngle;
    /** 开始画弧线的角度 **/
    private float mStartAngle;
    /** 需要画弧线的角度 **/
    private float    mSweepAngle;
    /** 是否正在增加角度 **/
    private boolean mAngleAdding;
    /** 叶数 **/
    private int mArcCount;
    /** 圆弧颜色 **/
    private int[] mArcColors;
    /** Handler **/
    private Handler mHandler;

    public SpinView(Context context) {
        super(context);
        init(context);
    }

    public SpinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        mArcRectF = new RectF();
        mArcCount = 1;
        mArcColors = new int[] {ContextCompat.getColor(context, R.color.colorAccent)};
        mMinAngle = 30;
        mAddAngle = 270;
        mRotateRate = 4;
        mAngleAdding = true;
        mIntervalAngle = 45;
        mStrokeWidth = (float) Math.ceil(3 * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_WHAT);
            if (visibility == VISIBLE) {
                mHandler.sendEmptyMessage(MESSAGE_WHAT);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler = new Handler(Looper.getMainLooper(), message -> {
            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, INVALIDATE_DELAY);
            invalidate();
            return true;
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rotate();

        int size = Math.min(mViewWidth, mViewHeight);
        float strokeWidth = getRealStrokeWidth(size, mArcCount);
        mArcRectF.set(
                (Math.abs(size - mViewWidth) >> 1) + strokeWidth,
                (Math.abs(size - mViewHeight) >> 1) + strokeWidth,
                (Math.abs(size - mViewWidth) >> 1) + size - strokeWidth,
                (Math.abs(size - mViewHeight) >> 1) + size - strokeWidth);
        for (int i = 0; i < mArcCount; i++) {
            paint.setColor(mArcColors[i % mArcColors.length]);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            float startAngle = mStartAngle + i * (360f / mArcCount);
            // 画圆弧
            canvas.drawArc(mArcRectF, startAngle, mSweepAngle, false, paint);
            // 画两个圆圈,是弧线变得圆润
            paint.setStyle(Paint.Style.FILL);

            float radius = mArcRectF.width() / 2;
            float endAngle = startAngle + mSweepAngle;

            float x = (float) (mArcRectF.left + radius + radius * Math.cos(startAngle * Math.PI / 180));
            float y = (float) (mArcRectF.top + radius + radius * Math.sin(startAngle * Math.PI / 180));

            float x2 = (float) (mArcRectF.left + radius + radius * Math.cos(endAngle * Math.PI / 180));
            float y2 = (float) (mArcRectF.top + radius + radius * Math.sin(endAngle * Math.PI / 180));

            canvas.drawCircle(x, y, strokeWidth / 2, paint);
            canvas.drawCircle(x2, y2, strokeWidth / 2, paint);
        }
    }

    /**
     * 设置弧线的颜色
     * @param colors 颜色列表
     */
    public SpinView setArcColors(@NonNull @ColorInt int... colors) {
        if (colors.length == 0) {
            this.mArcColors = new int[] {ContextCompat.getColor(getContext(), R.color.colorAccent)};
        } else {
            this.mArcColors = colors;
//            if (colors.length > mArcCount) {
//                mArcCount = colors.length;
//            }
        }
        return this;
    }

    /**
     * 设置弧线的数量
     * @param count 数量
     */
    public SpinView setArcCount(@IntRange(from = 1) int count) {
        mArcCount = count > 1 ? count : 1;
        return this;
    }

    /**
     * 设置增量角度（仅对叶数为1有效）
     * @param add 增量角度
     */
    public SpinView setArcAddAngle(float add) {
        mAddAngle = add;
        return this;
    }

    /**
     * 设置最小角度（仅对叶数为1有效）
     * @param min 最小角度
     */
    public SpinView setArcMinAngle(float min) {
        mMinAngle = min;
        return this;
    }

    /**
     * 设置弧线间隔角度
     * @param angle 角度
     */
    public SpinView setArcIntervalAngle(float angle) {
        mIntervalAngle = angle;
        return this;
    }

    /**
     * 设置抖动比例(即改变弧的宽度)
     * @param ratio 抖动比例
     */
    public SpinView setArcShakeRatio(float ratio) {
        mSnakeRatio = ratio;
        return this;
    }

    /**
     * 设置弧线宽度
     * @param width 宽度（单位dp）
     */
    public SpinView setArcStrokeWidth(float width) {
        mStrokeWidth = getResources().getDisplayMetrics().density * width;
        return this;
    }

    /**
     * 设置转一圈需要时间
     * @param time 转一圈需要时间
     */
    public SpinView setRoundUseTime(int time) {
        mRotateRate = 3600f / time;
        return this;
    }

    /**
     * 旋转圆弧
     */
    private void rotate() {
        // 开始的幅度角度
        mStartAngle += mAngleAdding ? mRotateRate : mRotateRate * 2;
        if (mArcCount > 1) {
            mSweepAngle = (360f / mArcCount - mIntervalAngle);
            mSweepAngle = (int) (mSweepAngle - mSweepAngle / 2 * getRouteNumber());
        } else {
            // 需要画的弧度
            mSweepAngle += mAngleAdding ? mRotateRate : -mRotateRate;
            mAngleAdding = mAngleAdding ? mSweepAngle < mMinAngle + mAddAngle : mSweepAngle <= mMinAngle;
        }
    }

    /**
     * 获取一个变化的宽度
     * @param size 控件大小
     * @param arcCount 弦的数量
     */
    private float getRealStrokeWidth(int size, int arcCount) {
        return getRouteNumber() * size / 2 * (arcCount > 1 ? mSnakeRatio : 0) + mStrokeWidth;
    }

    /**
     * 获取一个随mStartAngle正弦曲线变化的值（也会随时间变化）
     * @return 变化值
     */
    private float getRouteNumber() {
        return (float) (1.0 + Math.sin(Math.PI * mStartAngle / 180)) / 2;
    }
    
}
