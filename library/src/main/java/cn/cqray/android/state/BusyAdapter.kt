package cn.cqray.android.state

import android.annotation.SuppressLint
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import cn.cqray.android.R
import cn.cqray.android.util.GetCompat
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.SpriteFactory
import com.github.ybq.android.spinkit.Style

/**
 * 忙碌布局适配器实现
 * @author Cqray
 */
@Suppress("Unused")
class BusyAdapter : StateAdapter<BusyAdapter>(R.layout.get_layout_state_busy) {

    /** 控件颜色  */
    private var spinColor: Int = R.color.colorPrimary

    /** 忙碌样式  */
    private var spinStyle = Style.CIRCLE

    /** 忙碌样式控件  */
    private var spinKitView: SpinKitView? = null

    init {
        setBackground(null)
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        // 初始化组件
        spinKitView = view.findViewById(R.id.get_state_spin_kit)
        textView = view.findViewById(R.id.get_state_text)
        // 设置SpinKitView样式
        spinKitView?.setColor(GetCompat.getColor(spinColor))
        spinKitView?.setIndeterminateDrawable(SpriteFactory.create(spinStyle))
    }

    /**
     * 设置加载框颜色
     * @param any 颜色资源ID或色值
     */
    @SuppressLint("ResourceAsColor")
    fun setSpinColor(@ColorRes @ColorInt any: Int) = also { spinColor = any }

    /**
     * 设置加载框样式
     * @param style 样式[Style]
     */
    fun setSpinStyle(style: Style) = also { spinStyle = style }
}