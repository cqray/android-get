package cn.cqray.android.app

import android.R
import android.app.Activity
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import cn.cqray.android.util.ContextUtils.resources
import org.xmlpull.v1.XmlPullParser

internal object GetUtils {
    fun isGetFragmentClass(clazz: Class<*>?): Boolean {
        return (clazz != null && Fragment::class.java.isAssignableFrom(clazz)
                && GetNavProvider::class.java.isAssignableFrom(clazz))
    }

    fun isGetActivityClass(clazz: Class<*>?): Boolean {
        return (clazz != null && ComponentActivity::class.java.isAssignableFrom(clazz)
                && GetNavProvider::class.java.isAssignableFrom(clazz))
    }

    fun checkProvider(provider: Any) {
        if (provider is ComponentActivity || provider is Fragment) {
            return
        }
        throw RuntimeException(
            String.format(
                "%s must extends %s or %s.",
                provider.javaClass.name,
                ComponentActivity::class.java.name,
                Fragment::class.java.name
            )
        )
    }

    fun checkProvider(provider: GetViewProvider) {
        if (provider is ComponentActivity || provider is Fragment) {
            return
        }
        throw RuntimeException(
            String.format(
                "%s must extends %s or %s.",
                provider.javaClass.name,
                ComponentActivity::class.java.name,
                Fragment::class.java.name
            )
        )
    }

    /**
     * 获取Activity进入动画资源ID
     * @param act Activity实例
     * @return 资源ID
     */
    fun getActivityOpenEnterAnimationResId(act: Activity): Int {
        var attr = intArrayOf(R.attr.windowAnimationStyle)
        var array = act.theme.obtainStyledAttributes(attr)
        val animationStyleResId = array.getResourceId(0, -1)
        if (animationStyleResId != -1) {
            attr = intArrayOf(R.attr.activityOpenEnterAnimation)
            array = act.theme.obtainStyledAttributes(animationStyleResId, attr)
            return array.getResourceId(0, -1)
        }
        return -1
    }

    /**
     * 获取动画资源的动画时长
     * @param resId 动画资源ID
     * @return 时长
     */
    fun getAnimDurationFromResource(resId: Int): Int {
        if (resId == -1) {
            return 0
        }
        val parser = resources.getAnimation(resId)
        var duration = 0
        try {
            // 循环直到文档结束
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                val `val` = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "duration")
                if (!TextUtils.isEmpty(`val`)) {
                    try {
                        duration = Math.max(duration, `val`.toInt())
                    } catch (ignore: Exception) {
                    }
                }
            }
        } catch (ignored: Exception) {
        }
        parser.close()
        return duration
    }
}