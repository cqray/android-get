package cn.cqray.android.state

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import cn.cqray.android.Get

/**
 * 状态容器控件
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

//    /** 状态控件标签 **/
//    private val stateTag = "State:tag"

    /** 当前状态 **/
    var currentState: ViewState = ViewState.IDLE
        private set

    /** 适配器集合  */
    private val stateAdapters = SparseArray<StateAdapter<*>>()

    /** 是否忙碌 **/
    val isBusy = currentState == ViewState.BUSY

    /** 是否空 **/
    val isEmpty = currentState == ViewState.EMPTY

    /** 是否异常 **/
    val isError = currentState == ViewState.ERROR

    /** 是否空闲状态 **/
    val isIdle = currentState == ViewState.IDLE

    /** 移除内容控件 **/
    fun removeContents(): MutableList<View> {
        val list = ArrayList<View>()
        for (i in (0 until childCount).reversed()) {
            val view = getChildAt(i)
            val tag = view?.getTag(STATE_TAG.hashCode())
            if (tag == STATE_TAG) {
                removeView(view)
                list.add(view)
            }
        }
        return list
    }

    fun setBusyAdapter(adapter: StateAdapter<*>?) = stateAdapters.put(ViewState.BUSY.ordinal, adapter)

    fun setEmptyAdapter(adapter: StateAdapter<*>?) = stateAdapters.put(ViewState.EMPTY.ordinal, adapter)

    fun setErrorAdapter(adapter: StateAdapter<*>?) = stateAdapters.put(ViewState.ERROR.ordinal, adapter)

    fun setBusy(text: String? = null) = setState(ViewState.BUSY, text)

    fun setEmpty(text: String? = null) = setState(ViewState.EMPTY, text)

    fun setError(text: String? = null) = setState(ViewState.ERROR, text)

    fun setIdle() = setState(ViewState.IDLE, null)

    /**
     * 设置对应状态
     * @param state 状态[ViewState]
     * @param text 文本内容
     */
    fun setState(state: ViewState, text: String? = null) {
//        // 获取状态
//        val newState = state
        // 更新状态
        currentState = state
        // 隐藏所有状态控件
        for (i in 0 until stateAdapters.size()) {
            val adapter = stateAdapters.valueAt(i)
            // 忙碌状态时，之前界面可能为空或异常，所以不隐藏
            if (!isBusy) adapter?.hide()
        }
        // 显示对应的状态控件
        val adapter = getAdapter(state)
        adapter?.let {
            if (it.isNotAttached) {
                it.onAttach(this@StateLayout)
                it.view?.setTag(STATE_TAG.hashCode(), STATE_TAG)
            }
        }
        // 显示界面
        adapter?.show(text)
    }

    /**
     * 获取对应状态的适配器
     * @param state 指定状态
     */
//    @Suppress("TYPE_MISMATCH_WARNING")
    private fun getAdapter(state: ViewState): StateAdapter<*>? {
//        if (state == ViewState.IDLE) return null
        var adapter = stateAdapters[state.ordinal]
        if (adapter == null) {
            // 获取全局配置
            val init = Get.init.stateInit!!
            adapter = when (state) {
                ViewState.BUSY -> init.busyAdapterGet()
                ViewState.EMPTY -> init.emptyAdapterGet()
                ViewState.ERROR -> init.errorAdapterGet()
                else -> null
            }
            adapter?.hide()
//            // 因为需要关联布局，所以需要克隆适配器
//            adapter?.let {
//                adapter = JsonUtils.deepClone(it, it.javaClass)
//                adapter.hide()
//            }
            // 放入缓存
            stateAdapters.put(state.ordinal, adapter)
        }
        return adapter
    }

    private companion object {
        /** 状态控件标签 **/
        private const val STATE_TAG = "State:tag"
    }
}