package cn.cqray.android.state

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import cn.cqray.android.Get
import java.util.concurrent.atomic.AtomicReference

/**
 * 状态容器控件
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /** 适配器集合  */
    private val adapters = SparseArray<GetStateAdapter<*>>()

    /** 当前状态 **/
    private val stateRef = AtomicReference(GetViewState.IDLE)

    /** 当前状态 **/
    val currentState: GetViewState get() = stateRef.get()

    /** 是否忙碌 **/
    val isBusy = stateRef.get() == GetViewState.BUSY

    /** 是否空 **/
    val isEmpty = stateRef.get() == GetViewState.EMPTY

    /** 是否异常 **/
    val isError = stateRef.get() == GetViewState.ERROR

    /** 是否空闲状态 **/
    val isIdle = stateRef.get() == GetViewState.IDLE

    /** 忙碌视图适配器 **/
    val busyAdapter: GetStateAdapter<*> get() = getAdapter(GetViewState.BUSY)!!

    /** 空白视图适配器 **/
    val emptyAdapter: GetStateAdapter<*> get() = getAdapter(GetViewState.EMPTY)!!

    /** 异常视图适配器 **/
    val errorAdapter: GetStateAdapter<*> get() = getAdapter(GetViewState.ERROR)!!

    /** 移除内容控件 **/
    fun removeContents(): MutableList<View> {
        val list = ArrayList<View>()
        for (i in (0 until childCount).reversed()) {
            val view = getChildAt(i)
            val tag = view?.getTag(View.NO_ID)
            tag?.let {
                removeView(view)
                list.add(view)
            }
        }
        return list
    }

    /**
     * 设置忙碌状态适配器
     * @param adapter 适配器
     */
    fun setBusyAdapter(adapter: GetStateAdapter<*>?) = setAdapter(GetViewState.BUSY, adapter)

    /**
     * 设置空状态适配器
     * @param adapter 适配器
     */
    fun setEmptyAdapter(adapter: GetStateAdapter<*>?) = setAdapter(GetViewState.EMPTY, adapter)

    /**
     * 设置错误状态适配器
     * @param adapter 适配器
     */
    fun setErrorAdapter(adapter: GetStateAdapter<*>?) = setAdapter(GetViewState.ERROR, adapter)

    /**
     * 设置对应状态的适配器
     * @param state 指定状态
     * @param adapter 适配器
     */
    private fun setAdapter(state: GetViewState, adapter: GetStateAdapter<*>?) {
        adapters[state.ordinal]?.hide()
        adapters.put(state.ordinal, adapter)
    }

    /**
     * 设置为忙碌状态
     * @param text 文本信息
     */
    @JvmOverloads
    fun setBusy(text: String? = null) = setState(GetViewState.BUSY, text)

    /**
     * 设置为空状态
     * @param text 文本信息
     */
    @JvmOverloads
    fun setEmpty(text: String? = null) = setState(GetViewState.EMPTY, text)

    /**
     * 设置为异常状态
     * @param text 文本信息
     */
    @JvmOverloads
    fun setError(text: String? = null) = setState(GetViewState.ERROR, text)

    /**
     * 设置为空闲状态
     */
    fun setIdle() = setState(GetViewState.IDLE, null)

    /**
     * 设置对应状态
     * @param state 状态[GetViewState]
     * @param text 文本内容
     */
    fun setState(state: GetViewState, text: String? = null) {
        // 更新状态
        synchronized(stateRef) { stateRef.set(state) }
        // 隐藏所有状态控件
        for (i in 0 until adapters.size()) {
            val adapter = adapters.valueAt(i)
            // 忙碌状态时，之前界面可能为空或异常，所以不隐藏
            if (!isBusy) adapter?.hide()
        }
        // 显示对应的状态控件
        getAdapter(state)?.let {
            // 未初始化则初始化
            if (it.view == null) {
                it.onAttach(this@GetStateLayout)
                it.view?.setTag(View.NO_ID, it.javaClass)
            }
            // 显示
            it.show(text)
        }
    }

    /**
     * 获取对应状态的适配器
     * @param state 指定状态
     */
    private fun getAdapter(state: GetViewState): GetStateAdapter<*>? {
        // 空闲界面没有适配器
        if (state == GetViewState.IDLE) return null
        // 获取对应的适配器
        return adapters[state.ordinal] ?: run {
            // 没有则初始化
            val adapter = when (state) {
                GetViewState.BUSY -> Get.init.stateInit.busyGet()
                GetViewState.EMPTY -> Get.init.stateInit.emptyGet()
                else -> Get.init.stateInit.errorGet()
            }
            // 默认隐藏
            adapter.hide()
            // 放入缓存
            adapters.put(state.ordinal, adapter)
            // 返回
            adapter
        }
    }
}