package cn.cqray.android.exc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import cn.cqray.android.Get;
import cn.cqray.android._Get;

/**
 * 异常管理界面
 * @author Cqray
 */
public class ExceptionDispatcher {

    private ExceptionDispatcher() {
    }

    public static void dispatchThrowable(Object source, String intro, Throwable throwable) {
        Intent intent = new Intent();
        Context context = Get.getTopActivity();
        // 如果没有获取到Activity
        if (context == null) {
            // 则取全局Context
            context = Get.getContext();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra("intro", intro);
        intent.putExtra("throwable", throwable);
        intent.putExtra("source", source == null ? null : source.getClass().getName());
        intent.setComponent(new ComponentName(context, GetExcActivity.class));
        context.startActivity(intent);
    }

    public static void dispatchStarterThrowable(Object source, String intro, String desc) {
        dispatchThrowable(source, intro, new GetException(desc));
    }

    public static void dispatchThrowable(Object source, @NonNull ExceptionType type) {
        dispatchThrowable(source, type.mIntro, new GetException(type.mDesc));
    }
}
