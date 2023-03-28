package cn.cqray.android.state

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.R
import cn.cqray.android.util.GetCompat
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.ViewUtils
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style

/**
 * 忙碌布局适配器实现
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetBusyAdapter : GetStateAdapter<GetBusyAdapter>(R.layout.get_layout_state_busy) {

    /** 忙碌控件  */
    private var spinKitView: SpinKitView? = null

    /** 控件颜色  */
    private var spinColor: Int = R.color.foreground

    /** 忙碌样式  */
    private var spinStyle = Style.CIRCLE

    /** 忙碌框颜色 **/
    private var frameColor: Int = Color.BLACK

    init {
        setBackground(null)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        // 初始化组件
        textView = view.findViewById(R.id.get_state_text)
        spinKitView = view.findViewById(R.id.get_state_spin_kit)
        // 注册忙碌控件唤醒
        val activity = ViewUtils.view2Activity(view) as? ComponentActivity
        activity?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                spinKitView?.onWindowFocusChanged(true)
            }
        })
    }

    override fun onViewChanged(view: View) {
        super.onViewChanged(view)
        val cp = Sizes.pxContent()
        val lp  = Sizes.pxLarge()
        val frameView = spinKitView?.parent as View
        if (textView?.text.isNullOrEmpty()) {
            // 设置相应的间隔
            textView?.visibility = View.GONE
            frameView.setPadding(cp, cp, cp, cp)
        } else {
            // 设置相应的间隔
            textView?.visibility = View.VISIBLE
            frameView.setPadding(lp, cp, lp, cp)
        }
        // 设置SpinKitView样式
        spinKitView?.setColor(GetCompat.getColor(spinColor))
        spinKitView?.setIndeterminateDrawable(SpriteFactory.create(spinStyle))
        frameView.background = GradientDrawable().also {
            it.setColor(GetCompat.getColor(frameColor))
            it.cornerRadius = Sizes.pxfSmall()
        }
    }

    /**
     * 设置忙碌控件颜色
     * @param any 颜色资源ID或色值
     */
    fun setSpinColor(any: Int) = also { spinColor = any }

    /**
     * 设置忙碌组件样式
     * @param style 样式[Style]
     */
    fun setSpinStyle(style: Style) = also { spinStyle = style }

    /**
     * 设置加载框颜色
     * @param any 颜色资源ID或色值
     */
    fun setFrameColor(any: Int) = also { spinColor = any }
}