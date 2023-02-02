package cn.cqray.android.multi

import android.util.SparseArray
import android.util.SparseIntArray
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.lifecycle.GetViewModel

class GetMultiViewModel2(
    lifecycleOwner: LifecycleOwner
) : GetViewModel(lifecycleOwner) {

    /** **/
    private val containerIds = SparseIntArray()

    /** Fragment位置索引（对应不同容器） **/
    private val indexArrays = SparseIntArray()

    /** Fragment列表（对应不同容器） **/
    private val fragmentArrays = SparseArray<MutableList<Fragment>>()

}