package cn.cqray.android.state

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import cn.cqray.android.Get
import cn.cqray.android.R
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style

/**
 * 忙碌布局适配器实现
 * @author Cqray
 */
class BusyAdapter : StateAdapter<BusyAdapter>(R.layout.get_layout_state_busy) {
    /** 控件颜色  */
    private var color: Int? = null

    /** 忙碌样式  */
    private var spinStyle: Style? = null

    /** 忙碌样式控件  */
    @Transient
    private var spinKitView: SpinKitView? = null

    /** 文本控件  */
    @Transient
    private var textView: TextView? = null

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        // 初始化组件
        spinKitView = view.findViewById(R.id.get_state_spin_kit)
        textView = view.findViewById(R.id.get_state_text)
        // 设置SpinKitView样式
        val color = this.color ?: ContextCompat.getColor(Get.context, R.color.colorPrimary)
        val style = spinStyle ?: Style.CIRCLE
        spinKitView?.setColor(color)
        spinKitView?.setIndeterminateDrawable(SpriteFactory.create(style))
    }

    override fun onTextChanged(text: String?) : Unit = text.let { textView?.text = it }

    /**
     * 设置加载框颜色
     * @param color 颜色
     */
    fun setSpinColor(color: Int) = also {
        this.color = color
        spinKitView?.setColor(color)
    }

    /**
     * 设置加载框样式
     * @param style 样式[Style]
     */
    fun setSpinStyle(style: Style) = also {
        this.spinStyle = style
        spinKitView?.setIndeterminateDrawable(SpriteFactory.create(style))
    }
}