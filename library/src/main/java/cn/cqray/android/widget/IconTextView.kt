package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.cqray.android.R;
import cn.cqray.android.util.SizeUnit;
import cn.cqray.android.util.Sizes;
import cn.cqray.android.util.ViewUtils;

/**
 * 图标文本控件
 * @author Cqray
 */
public class IconTextView extends LinearLayout {

    public static final int LOCATION_LEFT = 0;
    public static final int LOCATION_TOP = 1;
    public static final int LOCATION_RIGHT = 2;
    public static final int LOCATION_BOTTOM = 3;

    @IntDef({LOCATION_LEFT, LOCATION_TOP, LOCATION_RIGHT, LOCATION_BOTTOM})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Location{}

    /** 间隔控件 **/
    private final Space mSpaceView;
    /** 图标控件 **/
    private final AppCompatImageView mIconView;
    /** 文本控件 **/
    private final AppCompatTextView mTextView;
    /** 图标的位置 **/
    private int mItvLocation;
    /** 图标与文字的间隔 **/
    private int mItvSpace;

    public IconTextView(Context context) {
        this(context, null);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTextView);
        mItvLocation = ta.getInt(R.styleable.IconTextView_sIconLocation, LOCATION_LEFT);
        mItvSpace = ta.getDimensionPixelSize(R.styleable.IconTextView_sViewSpace, getResources().getDimensionPixelSize(R.dimen.small));
        String text = ta.getString(R.styleable.IconTextView_android_text);
        int textColor = ta.getColor(R.styleable.IconTextView_android_textColor, ContextCompat.getColor(context, R.color.text));
        int textSize = ta.getDimensionPixelSize(R.styleable.IconTextView_android_textSize, getResources().getDimensionPixelSize(R.dimen.body));
        int textStyle = ta.getInt(R.styleable.IconTextView_android_textStyle, 0);
        boolean useRipple = ta.getBoolean(R.styleable.IconTextView_useRipple, true);
        Drawable drawable = ta.getDrawable(R.styleable.IconTextView_android_src);
        ta.recycle();
        // 设置方向
        boolean horizontal = mItvLocation == LOCATION_LEFT || mItvLocation == LOCATION_RIGHT;
        boolean iconBefore = mItvLocation == LOCATION_LEFT || mItvLocation == LOCATION_TOP;
        setOrientation(horizontal ? HORIZONTAL : VERTICAL);
        // 初始化图标
        mIconView = new AppCompatImageView(context);
        mIconView.setLayoutParams(new ViewGroup.LayoutParams(horizontal ? -2 : -1, horizontal ? -1 : -2));
        mIconView.setImageDrawable(drawable);
        mIconView.setFocusable(true);
        mIconView.setClickable(true);
        // 初始化文本
        mTextView = new AppCompatTextView(context);
        mTextView.setLayoutParams(new ViewGroup.LayoutParams(horizontal ? -2 : -1, horizontal ? -1 : -2));
        mTextView.setGravity(android.view.Gravity.CENTER);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        mTextView.setText(text);
        mTextView.setTextColor(textColor);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mTextView.setTypeface(Typeface.defaultFromStyle(textStyle));
        // 初始化间隔
        mSpaceView = new Space(context);
        mSpaceView.setLayoutParams(new ViewGroup.LayoutParams(horizontal ? mItvSpace : -1, !horizontal ? mItvSpace : -1));
        mSpaceView.setVisibility(TextUtils.isEmpty(text) ? GONE : VISIBLE);
        // 添加控件
        addView(iconBefore ? mIconView : mTextView);
        addView(mSpaceView);
        addView(!iconBefore ? mIconView : mTextView);
        // 设置背景
        ViewUtils.setRippleBackground(mIconView, useRipple);
        ViewUtils.setRippleBackground(mTextView, useRipple);
        // 设置点击事件
        setOnClickListener(null);
    }

    public IconTextView setIconLocation(@Location int location) {
        removeAllViews();
        mItvLocation = location;
        boolean horizontal = mItvLocation == LOCATION_LEFT || mItvLocation == LOCATION_RIGHT;
        boolean iconBefore = mItvLocation == LOCATION_LEFT || mItvLocation == LOCATION_TOP;
        // 重新添加控件，以改变位置
        mSpaceView.setLayoutParams(new ViewGroup.LayoutParams(horizontal ? mItvSpace : -1, !horizontal ? mItvSpace : -1));
        addView(iconBefore ? mIconView : mTextView);
        addView(mSpaceView);
        addView(!iconBefore ? mIconView : mTextView);
        return this;
    }

    public IconTextView setViewSpace(float space) {
        return setViewSpace(space, SizeUnit.DIP);
    }

    public IconTextView setViewSpace(float space, SizeUnit unit) {
        boolean horizontal = mItvLocation == LOCATION_LEFT || mItvLocation == LOCATION_RIGHT;
        mItvSpace = (int) Sizes.applyDimension(space, unit);
        mSpaceView.setLayoutParams(new ViewGroup.LayoutParams(horizontal ? mItvSpace : -1, !horizontal ? mItvSpace : -1));
        return this;
    }

    public IconTextView setUseRipple(boolean useRipple) {
        ViewUtils.setRippleBackground(mIconView, useRipple);
        ViewUtils.setRippleBackground(mTextView, useRipple);
        return this;
    }

    public IconTextView setIconDrawable(Drawable drawable) {
        mIconView.setImageDrawable(drawable);
        return this;
    }

    public IconTextView setIconBitmap(Bitmap bitmap) {
        mIconView.setImageBitmap(bitmap);
        return this;
    }

    public IconTextView setIconResource(@DrawableRes int resId) {
        mIconView.setImageResource(resId);
        return this;
    }

    public IconTextView setIconTintColor(int color) {
        ImageViewCompat.setImageTintList(mIconView, ColorStateList.valueOf(color));
        return this;
    }

    public IconTextView setIconTintList(ColorStateList tintList) {
        ImageViewCompat.setImageTintList(mIconView, tintList);
        return this;
    }

    public IconTextView setText(@StringRes int resId) {
        mTextView.setText(resId);
        mSpaceView.setVisibility(TextUtils.isEmpty(mTextView.getText()) ? GONE : VISIBLE);
        return this;
    }

    public IconTextView setText(CharSequence text) {
        mTextView.setText(text);
        mSpaceView.setVisibility(TextUtils.isEmpty(mTextView.getText()) ? GONE : VISIBLE);
        return this;
    }

    public IconTextView setTextColor(int color) {
        mTextView.setTextColor(color);
        return this;
    }

    public IconTextView setTextSize(float textSize) {
        mTextView.setTextSize(textSize);
        return this;
    }

    public IconTextView setTextSize(float textSize, int unit) {
        mTextView.setTextSize(unit, textSize);
        return this;
    }

    public IconTextView setTypeface(Typeface typeface) {
        mTextView.setTypeface(typeface);
        return this;
    }

    public ImageView getIconView() {
        return mIconView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public int getIconLocation() {
        return mItvLocation;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mIconView.setOnClickListener(l);
        mTextView.setOnClickListener(l);
        super.setOnClickListener(v -> {
            mIconView.setPressed(true);
            mIconView.performClick();
        });
    }
}
