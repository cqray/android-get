package cn.cqray.android.util;

import android.content.Context;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;

import cn.cqray.android.R;

/**
 * 尺寸工具类
 * @author Cqray
 */
@SuppressWarnings("unused")
public class Sizes {

    private Sizes() {}

//        <dimen name="toolbar">52dp</dimen>
//    <dimen name="tabbed">48dp</dimen>
//    <dimen name="loading">30dp</dimen>
//    <dimen name="divider">0.5dp</dimen>
//    <dimen name="line">44dp</dimen>
//
//    <dimen name="smaller">4dp</dimen>
//    <dimen name="small">10dp</dimen>
//    <dimen name="content">16dp</dimen>
//    <dimen name="large">24dp</dimen>
//    <dimen name="larger">40dp</dimen>
//
//    <dimen name="h1">20sp</dimen>
//    <dimen name="h2">18sp</dimen>
//    <dimen name="h3">16sp</dimen>
//    <dimen name="body">14sp</dimen>
//    <dimen name="caption">12sp</dimen>
//    <dimen name="min">10sp</dimen>
//    <dimen name="marker">8sp</dimen>
//    <dimen name="elevation">6dp</dimen>

    public static int line() {
        return px(R.dimen.line);
    }

    public static int lineDp() {
        return dp(R.dimen.line);
    }

    public static int larger() {
        return px(R.dimen.larger);
    }

    public static int largerDp() {
        return dp(R.dimen.larger);
    }

    public static int large() {
        return px(R.dimen.large);
    }

    public static int largeDp() {
        return dp(R.dimen.large);
    }

    public static int content() {
        return px(R.dimen.content);
    }

    public static int contentDp() {
        return dp(R.dimen.content);
    }

    public static int small() {
        return px(R.dimen.small);
    }

    public static int smallDp() {
        return dp(R.dimen.small);
    }

    public static int smaller() {
        return px(R.dimen.smaller);
    }

    public static int smallerDp() {
        return dp(R.dimen.smaller);
    }

    public static int divider() {
        return px(R.dimen.divider);
    }

    public static int dividerDp() {
        return dp(R.dimen.divider);
    }

    public static int h1() {
        return px(R.dimen.h1);
    }

    public static int h1Sp() {
        return sp(R.dimen.h1);
    }

    public static int h2() {
        return px(R.dimen.h2);
    }

    public static int h2Sp() {
        return sp(R.dimen.h2);
    }

    public static int h3() {
        return px(R.dimen.h3);
    }

    public static int h3Sp() {
        return sp(R.dimen.h3);
    }

    public static int body() {
        return px(R.dimen.body);
    }

    public static int bodySp() {
        return sp(R.dimen.body);
    }

    public static int caption() {
        return px(R.dimen.caption);
    }

    public static int captionSp() {
        return sp(R.dimen.caption);
    }

    public static int min() {
        return px(R.dimen.min);
    }

    public static int minSp() {
        return sp(R.dimen.min);
    }

    public static int px(final @DimenRes int resId) {
        return getContext().getResources().getDimensionPixelSize(resId);
    }

    public static int sp(final @DimenRes int resId) {
        float size = px(resId);
        return px2sp(size);
    }

    public static int dp(final @DimenRes int resId) {
        float size = px(resId);
        return px2dp(size);
    }

    public static int dp2px(final float dpVal) {
        return SizeUtils.dp2px(dpVal);
    }

    public static int px2dp(final float pxVal) {
        return SizeUtils.px2dp(pxVal);
    }

    public static int sp2px(final float spVal) {
        return SizeUtils.sp2px(spVal);
    }

    public static int px2sp(final float pxVal) {
        return SizeUtils.px2sp(pxVal);
    }

    public static float applyDimension(final float value, final int unit) {
        return SizeUtils.applyDimension(value, unit);
    }

    /**
     * 测量View控件的大小
     * @param view 要测量的控件
     * @param observer 测量结果
     */
    public static void view(@NonNull View view, Observer<int[]> observer) {
        view.post(() -> {
            if (observer != null) {
                observer.onChanged(SizeUtils.measureView(view));
            }
        });
    }

    private static Context getContext() {
        Context context = ActivityUtils.getTopActivity();
        return context == null ? Utils.getApp().getApplicationContext() : context;
    }
}
