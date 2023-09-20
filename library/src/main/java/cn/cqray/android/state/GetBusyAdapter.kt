package cn.cqray.android.state

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.R
import cn.cqray.android.databinding.GetStateLayoutBusyBinding
import cn.cqray.android.lifecycle.GetLiveData
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.Views
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.UtilKt

/**
 * 忙碌布局适配器实现
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetBusyAdapter : GetStateAdapter<GetBusyAdapter>() {

    /** 忙碌控件  */
    private var spinKitView: SpinKitView? = null


    /** 忙碌框颜色 **/
    private val spinColor by lazy { GetLiveData(Colors.foreground()) }

    /** 忙碌框颜色 **/
    private val spinSize by lazy { GetLiveData(Sizes.dp2px(36)) }

    /** 忙碌样式  */
    private val spinStyle by lazy { GetLiveData(Style.CIRCLE) }

    /** 忙碌框颜色 **/
    private val frameColor by lazy { GetLiveData(Color.BLACK) }

    val binding by lazy { Views.getBinding(GetStateLayoutBusyBinding::class.java) }

    init {
        setBackground(null)
        setTextColor(Colors.foreground())
    }

    override val stateText: TextView
        get() = binding.getStateText

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

    private fun onFrameSizeChanged() {
        val textGone = stateText.text.isNullOrEmpty()
        // 上下间距
        val topBottom = Sizes.pxContent()
        // 左右间距
        val startEnd =
            if (textGone) Sizes.pxContent()
            else Sizes.pxLarge()

//        binding.root.background = GradientDrawable().also { drawable ->
//            drawable.setColor(frameColor.value)
//            drawable.cornerRadius = Sizes.pxfSmall()
//        }
        binding.root.setPadding(startEnd, topBottom, startEnd, topBottom)
    }

    private fun onStateTextChanged() {
        val textGone = stateText.text.isNullOrEmpty()
        with(stateText) {
            visibility = if (textGone) View.GONE else View.VISIBLE
            // 间隔大小
            val params = layoutParams as MarginLayoutParams
            val margin = (spinSize.value!! / 4 + textSize) / 2
            params.topMargin = margin.toInt()
            requestLayout()
        }
    }

//    override fun onViewChanged(view: View) {
//        super.onViewChanged(view)
//        // 文本不显示
//        val textGone = textView?.text.isNullOrEmpty()
//        textView?.let {
//            // 显示或隐藏
//            it.visibility = if (textGone) View.GONE else View.VISIBLE
//            // 间隔大小
//            val params = it.layoutParams as MarginLayoutParams
//            val margin = (spinSize / 4 + it.textSize) / 2
//            params.topMargin = margin.toInt()
//            it.requestLayout()
//        }
//        // 间隔信息
//        val cp = Sizes.pxContent()
//        val lp = Sizes.pxLarge()
//        val startEnd = if (textGone) cp else lp
//        // 外框样式
//        val frameView = spinKitView?.parent as? View
//        frameView?.let {
//            it.background = GradientDrawable().also { drawable ->
//                drawable.setColor(frameColor.value!!)
//                drawable.cornerRadius = Sizes.pxfSmall()
//            }
//            it.setPadding(startEnd, cp, startEnd, cp)
//        }
////        // 设置SpinKitView样式
////        spinKitView?.let {
////            it.setColor(spinColor)
////            it.setIndeterminateDrawable(SpriteFactory.create(spinStyle))
////            it.layoutParams.width = spinSize
////            it.layoutParams.height = spinSize
////            it.requestLayout()
////        }
//    }

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