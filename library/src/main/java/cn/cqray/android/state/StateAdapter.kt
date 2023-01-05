package cn.cqray.android.state

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.JsonUtils

import java.io.Serializable

/**
 * 状态适配器
 * @author Cqray
 */
@Suppress("UNCHECKED_CAST")
open class StateAdapter<T : StateAdapter<T>>(
    @param:LayoutRes private val layoutResId: Int
) : Serializable {

    /** 文本内容 **/
    private var text: String? = null

    /** 默认文本内容  */
    private var defaultText: String? = null

    /** 背景 **/
    @Transient
    private var background: Any? = R.color.background

    /** 关联的控件 **/
    @Transient
    private var attachedView: ViewGroup? = null

    /** 内容控件 **/
    @Transient
    var contentView: View? = null
        private set

    @MainThread
    internal fun onAttach(layout: FrameLayout) {
        attachedView = layout
        // 初始化界面
        contentView = ContextUtils.inflate(layoutResId)
        // 控件被创建
        contentView?.let {
            it.isClickable = true
            it.isFocusable = true
            onViewCreated(it)
        }
    }

    /**
     * 控件被创建，仅调用一次
     * @param view 被创建的控件
     */
    protected open fun onViewCreated(view: View) {}

    /**
     * 文本内容发生了变化
     * @param text 文本内容
     */
    protected open fun onTextChanged(text: String?) {}

    /**
     * 背景发生了变化
     * @param background 背景
     */
    protected open fun onBackgroundChanged(background: Drawable?) {
        contentView?.background = background
    }

    /**
     * 控件变化后的回调，在[onTextChanged]、[onBackgroundChanged]之后调用,
     * 多次调用，每次显示都会调用
     * @param view 内容控件
     */
    protected open fun onPostViewChanged(view: View) {}

    /**
     * 显示界面
     * @param text 文本内容
     */
    internal fun show(text: String?) {
        this.text = text
        show(true)
    }

    /**
     * 隐藏界面
     */
    internal fun hide() = show(false)

    /**
     * 显示、隐藏实现
     */
    private fun show(show: Boolean) {
        // 没有关联界面，则不继续操作
        if (attachedView == null) return
        // 没有初始化界面，则不继续操作
        if (contentView == null) return
        // 关联了界面才进行显示或隐藏操作
        val parent = attachedView!!
        val content = contentView!!
        if (show) {
            // 控件变化
            onViewChanged()
            // 显示组件
            (content.parent as ViewGroup?)?.removeView(content)
            parent.addView(content)
            parent.visibility = View.VISIBLE
            content.bringToFront()
        } else {
            parent.removeView(content)
            parent.visibility = View.GONE
        }
    }

    /**
     * 控件变化
     */
    private fun onViewChanged() {
        // 文本变化
        onTextChanged(text ?: defaultText)
        // 背景变化
        when (background) {
            is Int -> onBackgroundChanged(ContextUtils.getDrawable(background as Int))
            is Drawable -> onBackgroundChanged(background as Drawable)
            else -> onBackgroundChanged(null)
        }
        if (contentView != null) onPostViewChanged(contentView!!)
    }

    /**
     * 设置文本内容
     * @param text 文本内容
     */
    fun setText(text: String?): T {
        this.text = text
        contentView?.let { onTextChanged(text) }
        return this as T
    }

    /**
     * 设置默认的文本内容
     * @param text 文本内容
     */
    fun setDefaultText(text: String?): T {
        this.defaultText = text
        return this as T
    }

    /**
     * 设置背景
     * @param background [Drawable]
     */
    fun setBackground(background: Drawable?): T {
        this.background = background
        contentView?.let { onBackgroundChanged(background) }
        return this as T
    }

    /**
     * 设置背景资源
     * @param resId 资源ID[DrawableRes]
     */
    fun setBackgroundResource(@DrawableRes resId: Int?): T {
        this.background = resId
        contentView?.let {
            val background = if (resId == null) null else ContextCompat.getDrawable(Get.context, resId)
            setBackground(background)
        }
        return this as T
    }

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    fun setBackgroundColor(color: Int?): T {
        if (color == null) setBackground(null as Drawable?)
        else setBackground(ColorDrawable(color))
        return this as T
    }

    /**
     * 深度拷贝状态适配器
     * @param <S> 泛型
     * @return 实例</S>
     */
    fun <T : StateAdapter<T>> deepClone(): T? {
        val adapter = JsonUtils.deepClone(this as T, javaClass)
        adapter?.background = background
        return adapter
    }
}