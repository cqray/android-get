package cn.cqray.android.adapter

import android.util.SparseArray
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 选择类型的Adapter委托
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class SelectAdapterDelegate<T>(val adapter: BaseQuickAdapter<T, out BaseViewHolder>) {

    /** 选中数据监听 **/
    private val selectedDataLD = MutableLiveData(mutableListOf<T>())

    /** 超出选中数量限制 **/
    private val countLimitLD = MutableLiveData<Int>()

    /** 选中数据项 **/
    private val selectedItems = SparseArray<T>()

    /** 最大选中数 **/
    var maxCount = -1

    /** 是否启用总是能够选择，即超出数量后，出栈第一个 **/
    var alwaysSelectEnable = false

    /** 选中数据 **/
    val selectedData: List<T> get() = selectedDataLD.value!!

    fun observeSelectedData(owner: LifecycleOwner, observer: Observer<MutableList<T>>) {
        selectedDataLD.observe(owner, observer)
    }

    fun observeCountLimit(owner: LifecycleOwner, observer: Observer<Int>) {
        countLimitLD.observe(owner, observer)
    }

    /**
     * 全部选中或全不选
     * @param selectAll true全选 false全不选
     */
    fun selectAll(selectAll: Boolean) {
        selectedItems.clear()
        if (selectAll) {
            adapter.data.forEachIndexed { i, item ->
                selectedItems.put(i, item)
            }
        }
        adapter.notifyDataSetChanged()
        notifySelectedChanged()
    }

    /**
     * 选择数据
     * @param position 位置
     * @param select 是否选择 true 选择 false 取消选择
     */
    fun select(position: Int, select: Boolean) {
        // 没有找到指定项，则不继续
        if (adapter.getItemOrNull(position) == null) return
        // 最大选中项数为 0，则不继续
        if (maxCount == 0) return
        // 清除无效的选中项
        clearInvalidSelectedItem()
        // 当前选择数量
        val size = selectedItems.size()
        // 超出数量且不允许继续选择，则不继续
        if ((maxCount > 0) and (size > maxCount) and !alwaysSelectEnable) {
            countLimitLD.value = maxCount
            return
        }
        if (select) {
            // 最大选中数有效，且已选中数量大于最大选中数
            // 则需要移除历史选中数据
            if ((maxCount > 0) and (size >= maxCount)) {
                val index = selectedItems.keyAt(0)
                selectedItems.removeAt(0)
                adapter.notifyItemChanged(index)
            }
            // 添加新的选中项
            selectedItems.put(position, adapter.getItem(position))
        } else selectedItems.remove(position)
        // 更新单项
        adapter.notifyItemChanged(position)
        // 通知数据更新
        notifySelectedChanged()
    }

    /**
     * 切换选中项
     */
    fun toggleSelect(position: Int) {
        val select = selectedItems.get(position) != null
        select(position, !select)
    }

    /**
     * 清空选中数据标识
     */
    fun clearSelectedDataFlag() {
        selectedItems.clear()
        notifySelectedChanged()
    }

    /**
     * 移除选中数据
     */
    fun removeSelectedData() {
        val list = selectedData
        for (t in list) adapter.remove(t)
        clearSelectedDataFlag()
    }

    /**
     * 是否选中
     */
    fun isSelected(position: Int) = selectedItems[position] == adapter.data.getOrNull(position)

    /**
     * 通知选中的数据发生变化
     */
    private fun notifySelectedChanged() {
        // 清空无效选中项
        clearInvalidSelectedItem()
        // 获取数据
        val list = selectedDataLD.value!!.also { it.clear() }
        for (i in 0 until selectedItems.size()) {
            val item = selectedItems.valueAt(i)
            list.add(item)
        }
        selectedDataLD.value = list
    }

    /**
     * 清除无效的选中数据
     */
    private fun clearInvalidSelectedItem() {
        // 倒序，排除无效的选择项
        for (i in (0 until selectedItems.size()).reversed()) {
            // 存储的索引
            val index = selectedItems.keyAt(i)
            // 存储的值
            val item = selectedItems.valueAt(i)
            // 存储的值和位置不匹配，则移除缓存
            if (item != adapter.getItemOrNull(index)) selectedItems.removeAt(i)
        }
    }
}