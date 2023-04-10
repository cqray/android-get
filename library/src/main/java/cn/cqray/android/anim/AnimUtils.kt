package cn.cqray.android.anim

import android.app.Activity
import android.text.TextUtils
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import cn.cqray.android.util.Contexts
import org.xmlpull.v1.XmlPullParser
import kotlin.math.max

/**
 * 动画工具类
 * @author Cqray
 */
internal object AnimUtils {

    /**
     * 获取Activity进入动画资源ID
     * @param act Activity实例
     * @return 资源ID
     */
    @JvmStatic
    fun getActivityOpenEnterAnimResId(act: Activity): Int {
        var attr = intArrayOf(android.R.attr.windowAnimationStyle)
        var array = act.theme.obtainStyledAttributes(attr)
        val animationStyleResId = array.getResourceId(0, -1)
        if (animationStyleResId != -1) {
            attr = intArrayOf(android.R.attr.activityOpenEnterAnimation)
            array = act.theme.obtainStyledAttributes(animationStyleResId, attr)
            return array.getResourceId(0, -1)
        }
        return -1
    }

    /**
     * 获取动画资源的动画时长
     * @param id 动画资源ID
     * @return 时长
     */
    @JvmStatic
    fun getAnimDurationFromResource(@AnimatorRes @AnimRes id: Int): Int {
        var duration = 0
        runCatching {
            val parser = Contexts.resources.getAnimation(id)
            // 循环直到文档结束
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                val value = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "duration")
                if (!TextUtils.isEmpty(value)) {
                    runCatching { duration = max(duration, value.toInt()) }
                }
            }
            parser.close()
        }
        return duration
    }
}