package cn.cqray.android.log

import android.util.Log
import androidx.annotation.IntDef
import cn.cqray.android.Get

/**
 * [Get]日志
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
object GetLog {

    const val V = Log.VERBOSE
    const val D = Log.DEBUG
    const val I = Log.INFO
    const val W = Log.WARN
    const val E = Log.ERROR
    const val A = Log.ASSERT

    @IntDef(V, D, I, W, E, A)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Level

    /** 日志适配器缓存 **/
    @Suppress("ObjectPropertyName")
    private var _logAdapter: GetLogAdapter? = null

    /** 日志适配器 **/
    private val logAdapter: GetLogAdapter
        get() {
            val adapter = Get.init.logInit!!.logAdapter
            if (_logAdapter != adapter) _logAdapter = adapter
            return adapter
        }

    @JvmStatic
    fun v(vararg contents: Any?) = logAdapter.print(V, null, contents)

    @JvmStatic
    fun vTag(tag: String?, vararg contents: Any?) = logAdapter.print(V, tag, contents)

    @JvmStatic
    fun d(vararg contents: Any?) = logAdapter.print(D, null, contents)

    @JvmStatic
    fun dTag(tag: String?, vararg contents: Any?) = logAdapter.print(D, tag, contents)

    @JvmStatic
    fun i(vararg contents: Any?) = logAdapter.print(I, null, contents)
    
    @JvmStatic
    fun iTag(tag: String?, vararg contents: Any?) = logAdapter.print(I, tag, contents)

    @JvmStatic
    fun w(vararg contents: Any?) = logAdapter.print(W, null, contents)

    @JvmStatic
    fun wTag(tag: String?, vararg contents: Any?) = logAdapter.print(W, tag, contents)

    @JvmStatic
    fun e(vararg contents: Any?) = logAdapter.print(E, null, contents)

    @JvmStatic
    fun eTag(tag: String?, vararg contents: Any?) = logAdapter.print(E, tag, contents)

    @JvmStatic
    fun a(vararg contents: Any?) = logAdapter.print(A, null, contents)

    @JvmStatic
    fun aTag(tag: String?, vararg contents: Any?) = logAdapter.print(A, tag, contents)

    @JvmStatic
    fun print(@Level level: Int, tag: String?, vararg contents: Any?) = logAdapter.print(level, tag, *contents)

    @JvmStatic
    fun file(content: String?) = logAdapter.file(D, null, content)

    @JvmStatic
    fun file(tag: String?, content: String?) = logAdapter.file(D, tag, content)

    @JvmStatic
    fun file(@Level level: Int, content: String?) = logAdapter.file(level, null, content)

    @JvmStatic
    fun file(@Level level: Int, tag: String?, content: String?) = logAdapter.file(level, tag, content)

    @JvmStatic
    fun json(content: String?) = logAdapter.json(D, null, content)

    @JvmStatic
    fun json(tag: String?, content: String?) = logAdapter.json(D, tag, content)

    @JvmStatic
    fun json(@Level level: Int, content: String?) = logAdapter.json(level, null, content)

    @JvmStatic
    fun json(@Level level: Int, tag: String?, content: String?) = logAdapter.json(level, tag, content)

    @JvmStatic
    fun xml(content: String?) = logAdapter.xml(D, null, content)

    @JvmStatic
    fun xml(tag: String?, content: String?) = logAdapter.xml(D, tag, content)

    @JvmStatic
    fun xml(@Level level: Int, content: String?) = logAdapter.xml(level, null, content)

    @JvmStatic
    fun xml(@Level level: Int, tag: String?, content: String?) = logAdapter.xml(level, tag, content)

    @JvmStatic
    fun iFile(content: String?) = logAdapter.file(I, null, content)

    @JvmStatic
    fun iFile(tag: String?, content: String?) = logAdapter.file(I, tag, content)

    @JvmStatic
    fun iJson(content: String?) = logAdapter.json(I, null, content)

    @JvmStatic
    fun iJson(tag: String?, content: String?) = logAdapter.json(I, tag, content)

    @JvmStatic
    fun iXml(content: String?) = logAdapter.xml(I, null, content)

    @JvmStatic
    fun iXml(tag: String?, content: String?) = logAdapter.xml(I, tag, content)

    @JvmStatic
    fun eFile(content: String?) = logAdapter.file(I, null, content)

    @JvmStatic
    fun eFile(tag: String?, content: String?) = logAdapter.file(I, tag, content)

    @JvmStatic
    fun eJson(content: String?) = logAdapter.json(I, null, content)

    @JvmStatic
    fun eJson(tag: String?, content: String?) = logAdapter.json(I, tag, content)

    @JvmStatic
    fun eXml(content: String?) = logAdapter.xml(I, null, content)

    @JvmStatic
    fun eXml(tag: String?, content: String?) = logAdapter.xml(I, tag, content)
}