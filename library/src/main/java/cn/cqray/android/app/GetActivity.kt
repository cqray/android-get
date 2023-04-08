package cn.cqray.android.app

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import cn.cqray.android.Get
import cn.cqray.android.handle.RxDelegate
import cn.cqray.android.handle.RxProvider
import cn.cqray.android.tip.GetTipProvider
import cn.cqray.android.widget.GetToolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * [Get]基础Activity
 * @author Cqray
 */
@Suppress("unused")
open class GetActivity : AppCompatActivity(),
    GetViewProvider,
    GetNavProvider,
    GetTipProvider,
    RxProvider,
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

    override val rxDelegate by lazy { RxDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreating(savedInstanceState)
    }

    open fun onCreating(savedInstanceState: Bundle?) {}

    final override fun setContentView(@LayoutRes id: Int) = viewDelegate.setGetContentView(id)

    final override fun setContentView(view: View) = viewDelegate.setGetContentView(view)

    final override fun setContentView(view: View, params: ViewGroup.LayoutParams?) =
        viewDelegate.setGetContentView(view)

    /**
     * 避免重写，影响内部逻辑
     */
    final override fun onBackPressed() = super.onBackPressed()

    /**
     * 查找View
     */
    final override fun <T : View> findViewById(@IdRes id: Int): T = super<AppCompatActivity>.findViewById(id)
}