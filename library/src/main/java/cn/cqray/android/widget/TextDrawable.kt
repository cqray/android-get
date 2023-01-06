package cn.cqray.android.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.cqray.android.util.Sizes;
import lombok.Builder;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

/**
 * 带文字的Drawable
 * @author Cqray
 */
@Builder
@Accessors(prefix = "m")
public class TextDrawable extends ShapeDrawable {

    /** 文本画笔 **/
    private final Paint mTextPaint = new Paint();
    /** 边框画笔 **/
    private final Paint mBorderPaint = new Paint();
    /** 是否初始化 **/
    private final AtomicBoolean mPaintInit = new AtomicBoolean();
    /** 高度 **/
    private float mHeight;
    /** 宽度 **/
    private float mWidth;
    /** 文本 **/
    private String mText;
    /** 字体颜色 **/
    private int mTextColor;
    /** 字体大小 **/
    private float mTextSize;
    /** 字体加粗 **/
    private boolean mTextBold;
    /** 字体边框厚度 **/
    private float mTextBorderThickness;
    /** 背景颜色 **/
    private int mColor;
    /** 背景圆角 **/
    private float mRadius;
    /** 背景圆角 **/
    private float[] mRadii;
    /** 边框颜色 **/
    private int mBorderColor;
    /** 背景图形边框厚度 **/
    private float mBorderThickness;

    @Tolerate
    private TextDrawable() {
        super();
    }

    private int getDarkerShade(int color) {
        return Color.rgb((int) (0.9 * Color.red(color)),
                (int) (0.9 * Color.green(color)),
                (int) (0.9 * Color.blue(color)));
    }

    @Override
    public void draw(Canvas canvas) {
        // 初始化画笔
        initPaints();
        // 获取界限
        Rect rect = getBounds();
        // 设置圆角矩形Path
        float[] radii = new float[8];
        for (int i = 0; i < radii.length ; i++) {
            radii[i] = Sizes.dp2px(mRadii == null ? mRadius : mRadii[i]);
        }
        Path path = new Path();
        path.addRoundRect(new RectF(getBounds()), radii, Path.Direction.CW);
        // 绘制边框
        if (mBorderThickness > 0) {
            int thickness = Sizes.dp2px(mBorderThickness / 2);
            RectF rectf = new RectF(getBounds());
            rectf.inset(thickness, thickness);
            canvas.drawPath(path, mBorderPaint);
        }
        // 绘制背景
        canvas.drawPath(path, getPaint());
        // 保存并移动画布
        int count = canvas.save();
        canvas.translate(rect.left, rect.top);
        // 获取画布宽高
        int width = getIntrinsicWidth() < 0 ? rect.width() : getIntrinsicWidth();
        int height = getIntrinsicHeight() < 0 ? rect.height() : getIntrinsicHeight();
        // 绘制文字
        if (!TextUtils.isEmpty(mText)) {
            float textSize = mTextSize <= 0 ? (Math.min(width, height) / 2f) : mTextSize;
            mTextPaint.setTextSize(Sizes.dp2px(textSize));
            canvas.drawText(mText, width / 2f, height / 2f - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
        }
        // 恢复画布
        canvas.restoreToCount(count);

    }
    private synchronized void initPaints() {
        if (mPaintInit.get()) {
            return;
        }
        setShape(new RectShape());

        mTextPaint.setColor(mTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(mTextBold);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStrokeWidth(mTextBorderThickness);

        mBorderPaint.setColor(mBorderColor == 0 ? getDarkerShade(mColor) : mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderThickness);

        Paint paint = getPaint();
        paint.setColor(mColor);
        paint.setAntiAlias(true);
    }

    @Override
    public void setAlpha(int alpha) {
        initPaints();
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        initPaints();
        mTextPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return Sizes.dp2px(mWidth);
    }

    @Override
    public int getIntrinsicHeight() {
        return Sizes.dp2px(mHeight);
    }
}