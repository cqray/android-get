package cn.cqray.android.state

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.databinding.GetStateLayoutBusyBinding
import cn.cqray.android.lifecycle.GetLiveData
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.Views
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
class GetBusyAdapter : GetStateAdapter<GetBusyAdapter>() {

    /** 忙碌框颜色 **/
    private val spinColor by lazy { GetLiveData(Colors.foreground()) }

    /** 忙碌框颜色 **/
    private val spinSize by lazy { GetLiveData(Sizes.dp2px(36)) }

    /** 忙碌样式  */
    private val spinStyle by lazy { GetLiveData(Style.CIRCLE) }

    /** 忙碌框颜色 **/
    private val frameColor by lazy { GetLiveData(Color.BLACK) }

    /** 绑定视图 **/
    val binding by lazy { Views.getBinding(GetStateLayoutBusyBinding::class.java) }

    /** 状态文本 **/
    override val stateText: TextView get() = binding.getStateText

    init {
        setBackground(null)
        setTextColor(Colors.foreground())
    }

    override fun onCreating() {
        super.onCreating()
        setContentView(binding.root)
        spinColor.observe(this) { binding.getStateSpinKit.setColor(it) }
        spinStyle.observe(this) { binding.getStateSpinKit.setIndeterminateDrawable(SpriteFactory.create(it)) }
        // 忙碌组件大小
        spinSize.observe(this) { size ->
            binding.getStateSpinKit.let {
                it.layoutParams.width = size
                it.layoutParams.height = size
                it.requestLayout()
            }
            // 设置文本间隔
            val margin = (spinSize.value!! / 4 + stateText.textSize) / 2
            setTextTopMargin(margin, TypedValue.COMPLEX_UNIT_PX)
            // 更改框大小
            onFrameSizeChanged()
        }
        // 边框颜色
        frameColor.observe(this) { color ->
            binding.root.background = GradientDrawable().also {
                it.setColor(color)
                it.cornerRadius = Sizes.pxfSmall()
            }
        }
        // 注册忙碌控件唤醒
        this.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                binding.getStateSpinKit.onWindowFocusChanged(true)
            }
        })
    }

    /**
     * 方框尺寸变化
     */
    private fun onFrameSizeChanged() {
        val textGone = stateText.text.isNullOrEmpty()
        // 上下间距
        val topBottom = Sizes.pxContent()
        // 左右间距
        val startEnd =
            if (textGone) Sizes.pxContent()
            else Sizes.pxLarge()
        // 设置内部间隔
        binding.root.setPadding(startEnd, topBottom, startEnd, topBottom)
    }

    /**
     * 设置忙碌控件颜色
     * @param color 色值
     */
    fun setSpinColor(@ColorInt color: Int) = also { spinColor.setValue(color) }

    /**
     * 设置忙碌组件样式
     * @param style 样式[Style]
     */
    fun setSpinStyle(style: Style) = also { spinStyle.setValue(style) }

    /**
     * 设置忙碌组件大小，单位DP
     * @param size 大小
     */
    fun setSpinSize(size: Number) = also { spinSize.setValue(Sizes.dp2px(size)) }

    /**
     * 设置忙碌组件大小
     * @param size 大小
     * @param unit 尺寸
     */
    fun setSpinSize(size: Number, unit: Int) = also { spinSize.setValue(Sizes.any2px(size, unit)) }

    /**
     * 设置加载框颜色
     * @param color 色值
     */
    fun setFrameColor(@ColorInt color: Int) = also { frameColor.setValue(color) }
}