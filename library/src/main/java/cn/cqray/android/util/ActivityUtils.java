package cn.cqray.android.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.Get;
import cn.cqray.android.manage.GetActivityManager;

public class ActivityUtils {

    /**
     * Return the activity by context.
     *
     * @param context The context.
     * @return the activity by context.
     */
    @Nullable
    public static Activity getActivityByContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        Activity activity = getActivityByContextInner(context);
        if (!isActivityAlive(activity)) {
            return null;
        }
        return activity;
    }

    @Nullable
    private static Activity getActivityByContextInner(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        List<Context> list = new ArrayList<>();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            Activity activity = getActivityFromDecorContext(context);
            if (activity != null) {
                return activity;
            }
            list.add(context);
            context = ((ContextWrapper) context).getBaseContext();
            if (context == null) {
                return null;
            }
            if (list.contains(context)) {
                // loop context
                return null;
            }
        }
        return null;
    }

    @Nullable
    private static Activity getActivityFromDecorContext(@Nullable Context context) {
        if (context == null) {
            return null;
        }
        if (context.getClass().getName().equals("com.android.internal.policy.DecorContext")) {
            try {
                Field mActivityContextField = context.getClass().getDeclaredField("mActivityContext");
                mActivityContextField.setAccessible(true);
                //noinspection ConstantConditions,unchecked
                return ((WeakReference<Activity>) mActivityContextField.get(context)).get();
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    /**
     * Return whether the activity is alive.
     *
     * @param context The context.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityAlive(final Context context) {
        return isActivityAlive(getActivityByContext(context));
    }

    /**
     * Return whether the activity is alive.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isActivityAlive(final Activity activity) {
        return activity != null && !activity.isFinishing()
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed());
    }

    /**
     * 检查屏幕横竖屏或者锁定就是固定
     */
    @SuppressWarnings("all")
    public static boolean isTranslucentOrFloating(Activity activity) {
        Boolean isTranslucentOrFloating = false;
        try {
            @SuppressLint("PrivateApi")
            Class<?> styleableClass = Class.forName("com.android.internal.R$styleable");
            Field windowField = styleableClass.getDeclaredField("Window");
            windowField.setAccessible(true);
            int[] styleableRes = (int[]) windowField.get(null);
            // 先获取到TypedArray
            assert styleableRes != null;
            final TypedArray typedArray = activity.obtainStyledAttributes(styleableRes);
            Class<?> activityInfoClass = ActivityInfo.class;
            // 调用检查是否屏幕旋转
            @SuppressLint("DiscouragedPrivateApi")
            Method isTranslucentOrFloatingMethod = activityInfoClass.getDeclaredMethod("isTranslucentOrFloating", TypedArray.class);
            isTranslucentOrFloatingMethod.setAccessible(true);
            isTranslucentOrFloating = (Boolean) isTranslucentOrFloatingMethod.invoke(null, typedArray);
        } catch (Exception ignored) {}
        return Boolean.valueOf(true).equals(isTranslucentOrFloating);
    }

    /**
     * java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
     * 修复android 8.0存在的问题
     * 在Activity中onCreate()中super之前调用
     */
    public static void hookOrientation(@NonNull Activity activity) {
        // 目标版本8.0及其以上
        if (activity.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O) {
            if (isTranslucentOrFloating(activity)) {
                fixOrientation(activity);
            }
        }
    }

    /**
     * 设置屏幕不固定，绕过检查
     */
    private static void fixOrientation(@NonNull Activity activity) {
        try {
            Class<Activity> activityClass = Activity.class;
            Field mActivityInfoField = activityClass.getDeclaredField("mActivityInfo");
            mActivityInfoField.setAccessible(true);
            ActivityInfo activityInfo = (ActivityInfo) mActivityInfoField.get(activity);
            // 设置屏幕不固定
            assert activityInfo != null;
            activityInfo.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        } catch (Exception ignored) {}
    }

//    static void autoHideKeyboard(Object target) {
//        if (target instanceof ViewProvider) {
//            boolean autoHide = ((ViewProvider) target).onKeyboardAutoHide();
//            if (autoHide) {
//                View view;
//                if (target instanceof Activity) {
//                    view = ((Activity) target).findViewById(android.R.id.content);
//                } else {
//                    view = ((Fragment) target).requireView();
//                }
//                KeyboardUtils.hideSoftInput(view);
//                View focusView = view.findFocus();
//                if (focusView != null) {
//                    focusView.clearFocus();
//                }
//            }
//        }
//    }

//    public static void onDestroyed(FragmentActivity activity, Function0<FragmentActivity> function) {
//        activity.getLifecycle().addObserver();
//    }

    /**
     * Start the activity.
     *
     * @param intent The description of the activity to start.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean startActivity(@NonNull final Intent intent) {
        return startActivity(intent, getTopActivityOrApp(), null);
    }

    /**
     * Start the activity.
     *
     * @param intent  The description of the activity to start.
     * @param options Additional options for how the Activity should be started.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean startActivity(@NonNull final Intent intent,
                                        @Nullable final Bundle options) {
        return startActivity(intent, getTopActivityOrApp(), options);
    }


    private static boolean startActivity(final Intent intent,
                                         final Context context,
                                         final Bundle options) {
        if (!isIntentAvailable(intent)) {
            Log.e("ActivityUtils", "intent is unavailable");
            return false;
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (options != null) {
            context.startActivity(intent, options);
        } else {
            context.startActivity(intent);
        }
        return true;
    }

    private static boolean isIntentAvailable(final Intent intent) {
        return Get.getContext()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    @NonNull
    private static Context getTopActivityOrApp() {
        if (GetActivityManager.isAppForeground()) {
            Activity topActivity = Get.getTopActivity();
            return topActivity == null ? Get.getContext() : topActivity;
        } else {
            return Get.getContext();
        }
    }
}
