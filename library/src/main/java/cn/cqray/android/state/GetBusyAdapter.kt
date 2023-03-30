package cn.cqray.android.state

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.R
import cn.cqray.android.util.Colors
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

    /** 控件大小  */
    private var spinSize = Sizes.dp2px(36)

    /** 控件颜色  */
    private var spinColor = Colors.foreground()

    /** 忙碌样式  */
    private var spinStyle = Style.CIRCLE

    /** 忙碌框颜色 **/
    private var frameColor = Color.BLACK

    init {
        setBackground(null)
        setTextColor(Colors.foreground())
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
        // 文本不显示
        val textGone = textView?.text.isNullOrEmpty()
        textView?.let {
            // 显示或隐藏
            it.visibility = if (textGone) View.GONE else View.VISIBLE
            // 间隔大小
            val params = it.layoutParams as MarginLayoutParams
            val margin = (spinSize / 4 + it.textSize) / 2
            params.topMargin= margin.toInt()
            it.requestLayout()
        }
        // 间隔信息
        val cp = Sizes.pxContent()
        val lp = Sizes.pxLarge()
        val startEnd = if (textGone) cp else lp
        // 外框样式
        val frameView = spinKitView?.parent as? View
        frameView?.let {
            it.background = GradientDrawable().also { drawable ->
                drawable.setColor(frameColor)
                drawable.cornerRadius = Sizes.pxfSmall()
            }
            it.setPadding(startEnd, cp, startEnd, cp)
        }
        // 设置SpinKitView样式
        spinKitView?.let {
            it.setColor(spinColor)
            it.setIndeterminateDrawable(SpriteFactory.create(spinStyle))
            it.layoutParams.width = spinSize
            it.layoutParams.height = spinSize
            it.requestLayout()
        }
    }

    /**
     * 设置忙碌控件颜色
     * @param color 色值
     */
    fun setSpinColor(@ColorInt color: Int) = also { spinColor = color }

    /**
     * 设置忙碌组件样式
     * @param style 样式[Style]
     */
    fun setSpinStyle(style: Style) = also { spinStyle = style }

    /**
     * 设置忙碌组件大小，单位DP
     * @param size 大小
     */
    fun setSpinSize(size: Number) = also { spinSize = Sizes.dp2px(size) }

    /**
     * 设置忙碌组件大小
     * @param size 大小
     * @param unit 尺寸
     */
    fun setSpinSize(size: Number, unit: Int) = also { spinSize = Sizes.any2px(size, unit) }

    /**
     * 设置加载框颜色
     * @param color 色值
     */
    fun setFrameColor(@ColorInt color: Int) = also { frameColor = color }
}