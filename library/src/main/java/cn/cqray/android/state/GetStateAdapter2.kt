package cn.cqray.android.state

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import cn.cqray.android.R
import cn.cqray.android.lifecycle.GetLiveData
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.Views
import java.util.concurrent.atomic.AtomicReference

/**
 * 状态适配器
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
abstract class GetStateAdapter2<T : GetStateAdapter2<T>> : LifecycleOwner {

    /** 生命周期管理注册 **/
    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }

    /** 状态容器缓存 **/
    private val stateLayoutRef = AtomicReference<GetStateLayout>()

    /** 根布局缓存 **/
    private val rootViewRef = AtomicReference<FrameLayout>()

    /** 内容视图缓存 **/
    private val contentViewRef = AtomicReference<View>()

    /** 背景 **/
    private val background by lazy { GetLiveData<Any?>() }

    /** 文本样式 **/
    private val texts by lazy { GetLiveData(arrayOfNulls<String>(2)) }

    /** 文本颜色 **/
    private val textColor by lazy { GetLiveData(Colors.text()) }

    /** 文本大小 **/
    private val textSize by lazy { GetLiveData(Sizes.pxfBody()) }

    /** 文本样式 **/
    private val textStyle by lazy { GetLiveData(0) }

    /** 内容视图 **/
    val contentView: View
        get() = contentViewRef.get()
            ?: throw RuntimeException("Did you forget call setContentView?")

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    /** 状态文本 **/
    abstract val stateText: TextView?

    internal fun onAttach(layout: GetStateLayout) {
        // 已绑定则不继续
        if (stateLayoutRef.get() != null) return
        // 缓存相关容器
        synchronized(stateLayoutRef) { stateLayoutRef.set(layout) }
        // 初始化根视图
        synchronized(rootViewRef) {
            val view = Views.inflate(R.layout.get_state_layout_default)
            rootViewRef.set(view as FrameLayout)
        }
        // 改变生命周期状态
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        // 初始化背景LiveData
        initBackgroundLd()
        // 初始化文本相关LiveData
        initTextLd()
        // 创建
        onCreating()
    }

    /**
     * 初始化背景LiveData
     */
    private fun initBackgroundLd() {
        // 监听背景变化
        background.observe(this) {
            val view = rootViewRef.get()
            when (it) {
                is Int -> view.background = ContextCompat.getDrawable(view.context, it)
                is Drawable -> view.background = it
                else -> view.background = null
            }
        }
    }

    /**
     * 初始化文本相关LiveData
     */
    private fun initTextLd() {
        texts.observe(this) { stateText?.text = it[1]?.ifEmpty { it[0] } }
        textColor.observe(this) { stateText?.setTextColor(it) }
        textSize.observe(this) { stateText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
        textStyle.observe(this) { stateText?.typeface = Typeface.defaultFromStyle(it) }
    }

    internal fun onDetach() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    open fun onCreating() {}

    /**
     * 设置内容布局
     * @param id 视图资源ID
     */
    fun setContentView(@LayoutRes id: Int) = setContentView(Views.inflate(id))

    /**
     * 设置内容布局
     * @param view 视图
     */
    fun setContentView(view: View) {
        synchronized(contentViewRef) { contentViewRef.set(view) }
        rootViewRef.get().let {
            it.removeAllViews()
            it.addView(view)
        }
    }

    /**
     * 显示界面
     * @param text 文本内容
     */
    internal fun show(text: String?) = show(true).also { setText(text) }

    /**
     * 隐藏界面
     */
    internal fun hide() = show(false)

    /**
     * 显示、隐藏实现
     */
    private fun show(show: Boolean) {
        val view = rootViewRef.get()
        if (show) {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
            stateLayoutRef.get().addView(view)
            view.bringToFront()
        } else {
            stateLayoutRef.get().removeView(view)
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    /**
     * 设置默认的文本内容
     * @param text 文本内容
     */
    fun setDefaultText(text: String?): T {
        val array = texts.value!!
        array[0] = text
        texts.setValue(array)
        return this as T
    }

    /**
     * 设置文本内容
     * @param text 文本内容
     */
    fun setText(text: String?): T {
        val array = texts.value!!
        array[1] = text
        texts.setValue(array)
        return this as T
    }

    /**
     * 设置文本颜色
     * @param color 色值
     */
    fun setTextColor(@ColorInt color: Int) = also { textColor.setValue(color) } as T

    /**
     * 设置文本大小
     * @param size 大小
     */
    fun setTextSize(size: Number) = setTextSize(size, TypedValue.COMPLEX_UNIT_DIP)

    /**
     * 设置文本大小
     * @param size 大小
     * @param unit 尺寸
     */
    fun setTextSize(size: Number, unit: Int) = also { textSize.setValue(Sizes.any2sp(size, unit)) } as T

    /**
     * 设置文本样式
     * @param style 样式
     */
    fun setTextStyle(style: Int) = also { textStyle.setValue(style) } as T

    /**
     * 设置背景
     * @param background [Drawable]
     */
    fun setBackground(background: Drawable?) = also { this.background.setValue(background) } as T

    /**
     * 设置背景颜色
     * @param color [ColorInt]
     */
    fun setBackgroundColor(@ColorInt color: Int) = also { this.background.setValue(color) } as T

    /**
     * 设置背景资源ID
     * @param id 资源ID
     */
    fun setBackgroundResource(@DrawableRes id: Int) = also { this.background.setValue(id) } as T

}