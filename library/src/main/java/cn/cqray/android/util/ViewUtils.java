package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.SizeUtils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;

public class ViewUtils {

    /** 设置Margin，默认单位DP **/
    public static void setMargin(View view, float margin) {
        setMargin(view, margin, TypedValue.COMPLEX_UNIT_DIP);
    }

    public static void setMargin(View view, float margin, int unit) {
        setMargin(view, margin, margin, margin, margin, unit);
    }

    /** 设置Margin，默认单位DP **/
    public static void setMargin(View view, float left, float top, float right, float bottom) {
        setMargin(view, left, top, right, bottom, TypedValue.COMPLEX_UNIT_DIP);
    }

    public static void setMargin(View view, float left, float top, float right, float bottom, int unit) {
        if (view != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.leftMargin = (int) SizeUtils.applyDimension(left, unit);
            params.topMargin = (int) SizeUtils.applyDimension(top, unit);
            params.rightMargin = (int) SizeUtils.applyDimension(right, unit);
            params.bottomMargin = (int) SizeUtils.applyDimension(bottom, unit);
            view.requestLayout();
        }
    }

    public static void setRippleBackground(@NonNull View view, @Nullable Boolean rippleEnable) {
        Context context = view.getContext();
        boolean ripple = rippleEnable == null || rippleEnable;
        if (ripple) {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TypedArray ta = context.obtainStyledAttributes(new int[]{
                        android.R.attr.actionBarItemBackground});
                drawable = ta.getDrawable(0);
                ta.recycle();
            }
            ViewCompat.setBackground(view, drawable);
        }
//        else {
//            ViewCompat.setBackground(view, null);
//        }
    }

    public static void setElevation(@NonNull View view, float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevation);
        }
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable) {
            ViewCompat.setBackground(view, createMaterialShapeDrawableBackground(view.getContext(), background));
        }

        MaterialShapeUtils.setParentAbsoluteElevation(view);
        MaterialShapeUtils.setElevation(view, elevation);
    }

    public static void setOverScrollMode(View view, int overScrollMode) {
        if (view instanceof ViewPager2) {
            View child = ((ViewPager2) view).getChildAt(0);
            if (child instanceof RecyclerView) {
                child.setOverScrollMode(overScrollMode);
            }
        } else if (view != null) {
            view.setOverScrollMode(overScrollMode);
        }
    }

    @NonNull
    private static MaterialShapeDrawable createMaterialShapeDrawableBackground(Context context, Drawable background) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        if (background instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(
                    ColorStateList.valueOf(((ColorDrawable) background).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(context);
        return materialShapeDrawable;
    }

    public static void closeRvAnimator(RecyclerView rv) {
        if (rv != null) {
            RecyclerView.ItemAnimator animator = rv.getItemAnimator();
            if (animator != null) {
                animator.setAddDuration(0);
                animator.setChangeDuration(0);
                animator.setMoveDuration(0);
                animator.setRemoveDuration(0);
            }
            if (animator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            }
        }
    }

    /** 通过View获取Activity **/
    @Nullable
    public static Activity getActivity(@NonNull View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /** 渲染界面 **/
    public static View inflate(@LayoutRes int resId) {
        return ContextUtils.inflate(resId);
    }
}
