package cn.cqray.android.state

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import cn.cqray.android.Get
import cn.cqray.android.util.JsonUtils

/**
 * 状态容器
 * @author Cqray
 */
class StateLayout : FrameLayout {

    /** 当前状态 **/
    var currentState: ViewState = ViewState.IDLE
        private set

    /** 适配器集合  */
    private val stateAdapters = SparseArray<StateAdapter<*>>()

    constructor(context: Context) : super(context) {
        isFocusable = true
        isClickable = true
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        isFocusable = true
        isClickable = true
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        isFocusable = true
        isClickable = true
    }

    /** 是否忙碌状态 **/
    fun isBusy(): Boolean = currentState == ViewState.BUSY

    /** 是否空闲状态 **/
    fun isIdle(): Boolean = currentState == ViewState.IDLE

    /** 移除内容控件 **/
    fun removeContents(): MutableList<View> {
        val list = ArrayList<View>()
        for (i in (0..childCount).reversed()) {
            val view = getChildAt(i)
            val tag = view.getTag(stateTag.hashCode())
            if (tag == stateTag) {
                removeView(view)
                list.add(view)
            }
        }
        return list
    }

    fun setBusyAdapter(adapter: StateAdapter<*>?) =
        stateAdapters.put(ViewState.BUSY.ordinal, adapter)

    fun setEmptyAdapter(adapter: StateAdapter<*>?) =
        stateAdapters.put(ViewState.EMPTY.ordinal, adapter)

    fun setErrorAdapter(adapter: StateAdapter<*>?) =
        stateAdapters.put(ViewState.ERROR.ordinal, adapter)

    fun setBusy(vararg texts: String?) = setState(ViewState.BUSY, *texts)

    fun setEmpty(vararg texts: String?) = setState(ViewState.EMPTY, *texts)

    fun setError(vararg texts: String?) = setState(ViewState.ERROR, *texts)

    fun setIdle() = setState(ViewState.IDLE, null)

    /**
     * 设置对应状态
     * @param state 状态[ViewState]
     * @param texts 文本内容
     */
    fun setState(state: ViewState?, vararg texts: String?) {
        // 获取状态
        val newState = state ?: ViewState.IDLE
        // 更新状态
        currentState = newState
        // 隐藏所有状态控件
        for (i in 0 until stateAdapters.size()) {
            val adapter = stateAdapters.valueAt(i)
            // 忙碌状态时，之前界面可能为空或异常，所以不隐藏
            if (newState != ViewState.BUSY) adapter?.hide()
        }
        // 显示对应的状态控件
        val adapter = getAdapter(newState)
        // 连接控件
        if (adapter?.contentView == null) {
            adapter?.onAttach(this)
            adapter?.contentView?.setTag(stateTag.hashCode(), stateTag)
        }
        // 显示界面
        adapter?.show(convertTexts(*texts))
    }

    /**
     * 获取对应状态的适配器
     * @param state 指定状态
     */
    @Suppress("TYPE_MISMATCH_WARNING")
    private fun getAdapter(state: ViewState): StateAdapter<*>? {
        if (state == ViewState.IDLE) return null
        var adapter = stateAdapters[state.ordinal]
        if (adapter == null) {
            // 获取全局配置
            val init = Get.init.stateInit!!
            when (state) {
                ViewState.BUSY -> adapter = init.busyAdapter!!
                ViewState.EMPTY -> adapter = init.emptyAdapter!!
                ViewState.ERROR -> adapter = init.errorAdapter!!
                else -> {}
            }
            if (adapter != null) {
                // 因为需要关联布局，所以需要克隆适配器
                adapter = JsonUtils.deepClone(adapter, adapter.javaClass)
                adapter?.hide()
            }
            stateAdapters.put(state.ordinal, adapter)
        }
        return adapter
    }

    /**
     * 转换文本内容
     * @param texts 文本列表
     */
    private fun convertTexts(vararg texts: String?): String? {
        if (texts.isEmpty()) return null
        if (texts.size == 1) return texts[0]
        // 多个数据
        val builder = StringBuilder()
        for (text in texts) {
            if (text == null) continue
            builder.append(text).append("\n")
        }
        if (builder.isNotEmpty()) builder.setLength(builder.length - 1)
        return builder.toString()
    }

    private companion object {
        const val stateTag = "State:tag"
    }
}