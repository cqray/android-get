package cn.cqray.android.state

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment

import cn.cqray.android.R
import cn.cqray.android.app.GetDelegate
import cn.cqray.android.app.GetViewProvider

import cn.cqray.android.util.ContextUtils

import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import java.lang.reflect.Field

/**
 * 状态管理委托
 * @author Cqray
 */
@Suppress("unused")
class StateDelegate {

    /** 状态缓存  */
    private val refreshStates: Array<Boolean?> = arrayOfNulls(3)

    /** 已关联的布局 **/
    private var attachedLayout: ViewGroup? = null

    /** 状态控件 **/
    private val stateLayout by lazy {
        StateLayout(ContextUtils.get()).also {
            it.isClickable = true
            it.isFocusable = true
            it.layoutParams = ViewGroup.MarginLayoutParams(-1, -1)
        }
    }

    /**
     * 关联Activity
     * @param activity
     */
    internal fun attachActivity(activity: Activity) {
        if (activity is GetViewProvider) {
            val delegate = activity.viewDelegate
            val layout = delegate.refreshLayout
                ?: delegate.contentLayout.findViewById(R.id.get_refresh)
                ?: delegate.contentLayout
            attachLayout(layout)
        }
    }

    /**
     * 关联Fragment
     * @param fragment
     */
    internal fun attachFragment(fragment: Fragment) {
        if (fragment is GetViewProvider) {
            val delegate = fragment.viewDelegate
            val layout = delegate.refreshLayout
                ?: delegate.contentLayout.findViewById(R.id.get_refresh)
                ?: delegate.contentLayout
            attachLayout(layout)
        }
    }

    /**
     * 关联[FrameLayout]容器
     * @param layout [FrameLayout]容器
     */
    fun attachLayout(layout: FrameLayout?) = attachLayout(layout as ViewGroup?)

    /**
     * 关联[SmartRefreshLayout]容器
     * @param layout [SmartRefreshLayout]容器
     */
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
    }

    /**
     * 断开连接
     * @param oldLayout 已连接的控件
     */
    private fun detachLayout(oldLayout: ViewGroup?) {
        oldLayout?.let {
            // 设置为空闲
            setIdle()
            // 移除并获取内容控件
            val contents = stateLayout.removeContents()
            // 移除与AttachedLayout的绑定
            it.removeView(stateLayout)
            // 没有内容控件则不需要回置
            if (contents.isEmpty()) return
            // 控件回归原始状态
            if (oldLayout is SmartRefreshLayout) oldLayout.setRefreshContent(contents[0])
            else contents.forEach { view -> oldLayout.addView(view) }
        }
    }

    /**
     * 设置忙碌状态适配器
     * @param adapter 适配器
     */
    fun setBusyAdapter(adapter: StateAdapter<*>?) = stateLayout.setBusyAdapter(adapter)

    /**
     * 设置空状态适配器
     * @param adapter 适配器
     */
    fun setEmptyAdapter(adapter: StateAdapter<*>?) = stateLayout.setEmptyAdapter(adapter)

    /**
     * 设置异常状态适配器
     * @param adapter 适配器
     */
    fun setErrorAdapter(adapter: StateAdapter<*>?) = stateLayout.setErrorAdapter(adapter)

    /**
     * 设置为忙碌状态
     * @param text 文本信息
     */
    fun setBusy(text: String?) = setState(ViewState.BUSY, text)

    /**
     * 设置为空状态
     * @param text 文本信息
     */
    fun setEmpty(text: String?) = setState(ViewState.EMPTY, text)

    /**
     * 设置为异常状态
     * @param text 文本信息
     */
    fun setError(text: String?) = setState(ViewState.ERROR, text)

    /**
     * 设置为空闲状态
     */
    fun setIdle() = setState(ViewState.IDLE, null)

    /**
     * 设置为指定的状态
     * @param state 指定状态
     * @param text 文本信息
     */
    fun setState(state: ViewState?, text: String?) {
        // 新状态
        val newState = state ?: ViewState.IDLE
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
        runCatching {
            val layout = attachedLayout as SmartRefreshLayout
            if (state == null || state == ViewState.IDLE) {
                (0..2).forEach { i -> refreshStates[i] = refreshFields[i]!!.getBoolean(layout) }
            }
        }
    }

    /**
     * 恢复刷新控件启用状态
     */
    private fun restoreRefreshStates(state: ViewState?) {
        if (attachedLayout !is SmartRefreshLayout) return
        runCatching {
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
        }
    }

    companion object {

        /** SmartLayout一些Enable属性  */
        private val refreshFields = arrayOfNulls<Field>(4)

        init {
            // 静态反射初始化一些属性
            val cls: Class<*> = SmartRefreshLayout::class.java
            runCatching {
                refreshFields[0] = cls.getDeclaredField("mEnableRefresh")
                refreshFields[1] = cls.getDeclaredField("mEnableLoadMore")
                refreshFields[2] = cls.getDeclaredField("mEnableOverScrollDrag")
                refreshFields[3] = cls.getDeclaredField("mManualLoadMore")
                refreshFields.forEach { field -> field?.isAccessible = true }
            }
        }
    }
}