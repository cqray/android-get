package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import cn.cqray.android.R
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes

/**
 * 错误界面适配器
 * @author Cqray
 */
@Suppress("Unused")
class ErrorAdapter : StateAdapter<ErrorAdapter>(R.layout.get_layout_state_error) {

    /** 图片控件  */
    private var imageView: ImageView? = null

    /** 重试控件  */
    private var retryView: TextView? = null

    /** 重试按钮是否显示 **/
    private var retryVisible = true

    /** 重试文本 **/
    private var retryText = "点击重试"

    /** 图片资源  */
    private var image: Any? = null

    /** 重试监听 **/
    private var retryListener: View.OnClickListener? = null

    init {
        setDefaultText("数据异常")
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        imageView = view.findViewById(R.id.get_state_img)
        textView = view.findViewById(R.id.get_state_text)
        retryView = view.findViewById(R.id.get_state_retry)
        retryView!!.setOnClickListener(retryListener)
    }

    override fun onViewChanged(view: View) {
        super.onViewChanged(view)
        // 重试按钮属性
        retryView?.let {
            it.visibility = if (retryVisible) View.VISIBLE else View.GONE
            // 文本
            it.text = retryText
            // 设置文本颜色
            it.setTextColor(textView?.textColors)
            // 设置文本大小
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView?.textSize ?: Sizes.body())
        }
        // 设置图片
        when (image) {
            is Int -> imageView?.setImageResource(image as Int)
            is Drawable -> imageView?.setImageDrawable(image as Drawable)
            is Bitmap -> imageView?.setImageBitmap(image as Bitmap)
            else -> imageView?.setImageBitmap(null)
        }
    }
    fun setRetryText(text: String) = also { retryText = text }

    fun setRetryVisible(visible: Boolean) = also { retryVisible = visible }

    fun setRetryListener(listener: View.OnClickListener?) = also { retryListener = listener }

    /**
     * 设置图片
     * @param image [Drawable]
     */
    fun setImage(image: Drawable?) = also { this.image = image }

    /**
     * 设置背景资源
     * @param id 资源ID[DrawableRes]
     */
    fun setImage(@DrawableRes id: Int?) = also { this.image = id }

    /**
     * 设置背景资源
     * @param bitmap 图片[Bitmap]
     */
    fun setImage(bitmap: Bitmap?) = also { this.image = bitmap }
}