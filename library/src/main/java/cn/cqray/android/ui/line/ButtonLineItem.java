package cn.cqray.android.ui.line;

import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import cn.cqray.android.R;
import cn.cqray.android.util.Sizes;

/**
 * 按钮行
 * @author Cqray
 */
public class ButtonLineItem extends LineItem<ButtonLineItem> {

    private CharSequence mText;
    private float mTextSize;
    private int mTextColor;
    private int mTextColorRes;
    private transient ColorStateList mTextColorStateList;

    public ButtonLineItem(CharSequence text) {
        mText = text;
        mTextColor = ContextCompat.getColor(getContext(), R.color.text);
        mTextSize = Sizes.h3Sp();
        dividerHeight(0);
    }

    public ButtonLineItem text(@StringRes int resId) {
        mText = getContext().getString(resId);
        return this;
    }

    public ButtonLineItem text(CharSequence text) {
        mText = text;
        return this;
    }

    public ButtonLineItem textColor(int color) {
        mTextColor = color;
        mTextColorStateList = null;
        return this;
    }

    public ButtonLineItem textColor(String color) {
        mTextColor = Color.parseColor(color);
        return this;
    }

    public ButtonLineItem textColorRes(@ColorRes int resId) {
        mTextColorRes = resId;
        mTextColorStateList = null;
        return this;
    }

    public ButtonLineItem textSize(float size) {
        mTextSize = size;
        return this;
    }

    public ButtonLineItem textSizeRes(@DimenRes int resId) {
        mTextSize = Sizes.sp(resId);
        return this;
    }

    public CharSequence getText() {
        return mText;
    }

    public ColorStateList getTextColor() {
        if (mTextColorStateList == null) {
            if (mTextColorRes != 0) {
                mTextColorStateList = ContextCompat.getColorStateList(getContext(), mTextColorRes);
            }
            mTextColorStateList = ColorStateList.valueOf(mTextColor);
        }
        return mTextColorStateList;
    }

    public float getTextSize() {
        return mTextSize;
    }

    @Override
    public int getItemType() {
        return LineItem.BUTTON;
    }
}
