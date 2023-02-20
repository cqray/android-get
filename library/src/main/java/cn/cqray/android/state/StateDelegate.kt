package cn.cqray.android.state

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.R
import cn.cqray.android.app.GetUtils
import cn.cqray.android.app.GetViewProvider
import cn.cqray.android.log.GetLog

import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.Sizes
import cn.cqray.android.widget.GetToolbar
import cn.cqray.java.tool.SizeUnit
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import java.lang.reflect.Field

/**
 * 状态管理委托
 * @author Cqray
 */
@Suppress("unused")
class StateDelegate(val provider: StateProvider) {

    init {
        // 检查Provider是否合法
        GetUtils.checkProvider(provider)
        // 加入缓存
        cacheDelegates[provider] = this
        // 资源回收事件订阅
        (provider as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 从缓存中移除
                cacheDelegates.remove(provider)
            }
        })
    }

    /** 偏移量 **/
    private val offsets: Array<Int?> = arrayOfNulls(4)

    /** 状态缓存  */
    private val refreshStates: Array<Boolean?> = arrayOfNulls(3)

    /** 已关联的布局 **/
    private var attachedLayout: ViewGroup? = null

    /** 状态控件 **/
    private var stateLayout: StateLayout? = null
        get() = field ?: StateLayout(ContextUtils.get()).also {
            it.isClickable = true
            it.isFocusable = true
            it.layoutParams = ViewGroup.MarginLayoutParams(-1, -1)
            field = it
        }

    internal fun attachActivity(activity: Activity) {
        if (activity is GetViewProvider) {
            val delegate = activity.viewDelegate
            val refresh: SmartRefreshLayout? = delegate.refreshLayout ?: activity.findViewById(R.id.get_refresh_layout)
            refresh?.let { attachLayout(refresh) }
        } else {
            // 获取Activity的根布局，进行连接
            val root = activity.findViewById<FrameLayout>(android.R.id.content)
            root.addView(stateLayout)
            stateLayout?.let {
                it.visibility = View.GONE
                it.post { refreshOffsets() }
            }
        }
    }

    internal fun attachFragment(fragment: Fragment) {
        if (fragment is GetViewProvider) {
            val refresh: SmartRefreshLayout? = fragment.viewDelegate.refreshLayout
                ?: fragment.viewDelegate.findViewById(R.id.get_refresh_layout)
            if (refresh != null) {
                attachLayout(refresh)
                return
            }
            val root = fragment.viewDelegate.rootView as FrameLayout?
            root?.let { attachLayout(it) }
        }
    }

    fun attachLayout(layout: FrameLayout?) = attachLayout(layout as ViewGroup?)

    fun attachLayout(layout: SmartRefreshLayout?) = attachLayout(layout as ViewGroup?)

    /**
     * 初始化状态控件
     */
    private fun attachLayout(layout: ViewGroup?) {
        // 没有接入容器，则不继续
        if (layout == null) return
        else {
            // 断开连接
            detachLayout(attachedLayout)
            attachedLayout = layout
        }
        // 连接控件
        val stateLayout = this.stateLayout!!
        val childCount = layout.childCount
        // 遍历AttachedLayout，将内容控件置换到StateLayout中
        for (i in (0 until childCount).reversed()) {
            val view = layout.getChildAt(i)
            if (layout is SmartRefreshLayout) {
                if (view !is RefreshHeader || view !is RefreshFooter) {
                    layout.removeView(view)
                    stateLayout.addView(view)
                }
            } else {
                layout.removeView(view)
                stateLayout.addView(view)
            }
        }
        // 将StateLayout添加到AttachedLayout中
        if (layout is SmartRefreshLayout) {
            layout.setRefreshContent(stateLayout)
        } else {
            layout.addView(stateLayout)
        }
        // 设置容器偏移
        stateLayout.visibility = View.GONE
        stateLayout.post { refreshOffsets() }
    }

    /**
     * 断开连接
     * @param oldLayout 已连接的控件
     */
    private fun detachLayout(oldLayout: ViewGroup?) {
        // 判断是否需要回收
        if (oldLayout == null) return
        // 获取状态控件
        val stateLayout = this.stateLayout!!
        // 设置为空闲
        setIdle()
        // 移除并获取内容控件
        val contents = stateLayout.removeContents()
        // 移除与AttachedLayout的绑定
        oldLayout.removeView(stateLayout)
        // 没有内容控件则不需要回置
        if (contents.isEmpty()) return
        // 控件回归原始状态
        if (oldLayout is SmartRefreshLayout) oldLayout.setRefreshContent(contents[0])
        else contents.forEach { view -> oldLayout.addView(view) }
    }

    /**
     * 设置偏移,单位DIP
     * @param start start方向偏移
     * @param top top方向偏移
     * @param end end方向偏移
     * @param bottom bottom方向偏移
     */
    fun setOffsets(start: Float?, top: Float?, end: Float?, bottom: Float?) {
        setOffsets(start, top, end, bottom, SizeUnit.DIP)
    }

    /**
     * 设置偏移
     * @param start start方向偏移
     * @param top top方向偏移
     * @param end end方向偏移
     * @param bottom bottom方向偏移
     * @param unit 单位，默认DIP
     */
    @Synchronized
    fun setOffsets(start: Float?, top: Float?, end: Float?, bottom: Float?, unit: SizeUnit?) {
        val newUnit = unit ?: SizeUnit.DIP
        if (start != null) offsets[0] = Sizes.applyDimension(start, newUnit).toInt()
        if (top != null) offsets[1] = Sizes.applyDimension(top, newUnit).toInt()
        if (end != null) offsets[2] = Sizes.applyDimension(end, newUnit).toInt()
        if (bottom != null) offsets[3] = Sizes.applyDimension(bottom, newUnit).toInt()
        refreshOffsets()
    }

    /**
     * 刷新偏移量
     */
    private fun refreshOffsets() {
        // 获取标题栏
        val toolbar: GetToolbar? = when (provider) {
            is Activity -> provider.findViewById(R.id.get_toolbar)
            is Fragment -> provider.view?.findViewById(R.id.get_toolbar)
            else -> null
        }
        // 获取标题栏底部位置为顶部偏移量
        toolbar?.let { offsets[1] = offsets[1] ?: it.bottom }
        // 更新属性
        with(stateLayout!!) {
            // 显示或隐藏
            visibility = View.VISIBLE;//if (isIdle()) View.GONE. else View.VISIBLE
            // 设置四周的间距
            val params = layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = offsets[0] ?: params.marginStart
            params.topMargin = offsets[1] ?: params.topMargin
            params.marginEnd = offsets[2] ?: params.marginEnd
            params.bottomMargin = offsets[3] ?: params.bottomMargin
        }
    }


    fun setBusyAdapter(adapter: StateAdapter<*>?) = stateLayout?.setBusyAdapter(adapter)

    fun setEmptyAdapter(adapter: StateAdapter<*>?) = stateLayout?.setEmptyAdapter(adapter)

    fun setErrorAdapter(adapter: StateAdapter<*>?) = stateLayout?.setErrorAdapter(adapter)

    fun setBusy(text: String?) = setState(ViewState.BUSY, text)

    fun setEmpty(text: String?) = setState(ViewState.EMPTY, text)

    fun setError(text: String?) = setState(ViewState.ERROR, text)

    fun setIdle() = setState(ViewState.IDLE, null)

    @Synchronized
    fun setState(state: ViewState?, text: String?) {
        // 新状态
        val newState = state ?: ViewState.IDLE
        // 更新偏移量
        refreshOffsets()
        // 获取状态控件
        val stateLayout = this.stateLayout!!
        // 保存刷新控件空闲状态时的相关属性
        saveRefreshStates(stateLayout.currentState)
        // 设置状态
        stateLayout.setState(newState, text)
        // 恢复刷新控件状态
        restoreRefreshStates(newState)
    }

    /**
     * 保存刷新控件空闲状态时的相关属性
     */
    private fun saveRefreshStates(state: ViewState?) {
        if (attachedLayout !is SmartRefreshLayout) return
        try {
            val layout = attachedLayout as SmartRefreshLayout
            if (state == null || state == ViewState.IDLE) {
                (0..2).forEach { i -> refreshStates[i] = refreshFields[i]!!.getBoolean(layout) }
            }
        } catch (ignore: Exception) {
        }
    }

    /**
     * 恢复刷新控件启用状态
     */
    private fun restoreRefreshStates(state: ViewState?) {
        if (attachedLayout !is SmartRefreshLayout) return
        try {
            val layout = attachedLayout as SmartRefreshLayout
            if (state == null || state == ViewState.IDLE) {
                (0..2).forEach { int ->
                    refreshFields[int]?.setBoolean(layout, refreshStates[int]!!)
                }
            } else {
                val busy = state == ViewState.BUSY
                refreshFields[0]?.setBoolean(layout, !busy && refreshStates[0]!!)
                refreshFields[1]?.setBoolean(layout, false)
                refreshFields[2]?.setBoolean(layout, !busy)
                refreshFields[3]?.setBoolean(layout, true)
            }
        } catch (ignore: Exception) {
        }
    }

    companion object {

        /** SmartLayout一些Enable属性  */
        private val refreshFields = arrayOfNulls<Field>(4)

        /** 委托缓存 [StateDelegate] **/
        private val cacheDelegates = HashMap<StateProvider, StateDelegate>()

        init {
            // 静态反射初始化一些属性
            val cls: Class<*> = SmartRefreshLayout::class.java
            try {
                refreshFields[0] = cls.getDeclaredField("mEnableRefresh")
                refreshFields[1] = cls.getDeclaredField("mEnableLoadMore")
                refreshFields[2] = cls.getDeclaredField("mEnableOverScrollDrag")
                refreshFields[3] = cls.getDeclaredField("mManualLoadMore")
                refreshFields.forEach { field -> field?.isAccessible = true }
            } catch (ignore: NoSuchFieldException) {
                ignore.fillInStackTrace()
            }
        }

        /**
         * 获取并初始化[StateDelegate]
         * @param provider [StateProvider]实现对象
         */
        @JvmStatic
        @Synchronized
        fun get(provider: StateProvider): StateDelegate = cacheDelegates[provider] ?: StateDelegate(provider)
    }
}