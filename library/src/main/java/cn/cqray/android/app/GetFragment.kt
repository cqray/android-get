package cn.cqray.android.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import cn.cqray.android.Get
import cn.cqray.android.handle.GetHandleDelegate
import cn.cqray.android.handle.GetHandleProvider
import cn.cqray.android.tip.GetTipProvider
import cn.cqray.android.widget.GetToolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * [Get]基础Fragment
 * @author Cqray
 */
@Suppress("unused")
open class GetFragment : Fragment(),
    GetViewProvider,
    GetNavProvider,
    GetTipProvider,
    GetHandleProvider,
    KeyboardProvider {

    /** 标题栏 **/
    @Keep
    @NonNull
    @JvmField
    @Suppress("KotlinNullnessAnnotation")
    val toolbar: GetToolbar? = null

    /** 刷新容器 **/
    @Keep
    @NonNull
    @JvmField
    @Suppress("KotlinNullnessAnnotation")
    val refreshLayout: SmartRefreshLayout? = null

    override val handleDelegate by lazy { GetHandleDelegate(this) }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onCreating(savedInstanceState)
        return viewDelegate.root
    }

    open fun onCreating(savedInstanceState: Bundle?) {}
}