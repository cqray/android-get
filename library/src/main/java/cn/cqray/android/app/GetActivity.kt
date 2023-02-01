package cn.cqray.android.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import cn.cqray.android.Get
import cn.cqray.android.state.StateProvider
import cn.cqray.android.tip.GetTipProvider
import cn.cqray.android.widget.Toolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * [Get]基础Activity
 * @author Cqray
 */
@Suppress("unused")
open class GetActivity : AppCompatActivity(),
    GetViewProvider,
    GetNavProvider,
    StateProvider,
    GetTipProvider {

    /** 标题栏 **/
    @Keep
    @NonNull
    @JvmField
    @Suppress("KotlinNullnessAnnotation")
    val toolbar: Toolbar? = null

    /** 刷新容器 **/
    @Keep
    @NonNull
    @JvmField
    @Suppress("KotlinNullnessAnnotation")
    val refreshLayout: SmartRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreating(savedInstanceState)
    }

    open fun onCreating(savedInstanceState: Bundle?) {}

    final override fun setContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    final override fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    final override fun setContentView(view: View, params: ViewGroup.LayoutParams?) =
        viewDelegate.setGetContentView(view)

    final override fun onBackPressed() {
        // 避免重写，影响内部逻辑
        super.onBackPressed()
    }
}