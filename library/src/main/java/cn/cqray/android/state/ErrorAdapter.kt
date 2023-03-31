package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import cn.cqray.android.R
import cn.cqray.android.util.Sizes

/**
 * 错误界面适配器
 * @author Cqray
 */
@Suppress("Unused")
class ErrorAdapter : StateAdapter<ErrorAdapter>(R.layout.get_layout_state_error) {

    /** 图片控件  */
    private var imageView: ImageView? = null

    /** 图片资源  */
    private var image: Any? = R.drawable.empty2

    /** 图片尺寸 **/
    private val imageSize = arrayOf(Sizes.dp2px(80), Sizes.dp2px(80))

    /** 重试控件  */
    private var retryView: TextView? = null

    /** 重试按钮是否显示 **/
    private var retryVisible = true

    /** 重试文本 **/
    private var retryText = "点击重试"

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
            // 设置文本大小
            it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView?.textSize ?: Sizes.pxfBody())
            // 设置文本颜色
            textView?.textColors?.let { color -> it.setTextColor(color) }
        }

        // 文本不显示
        val textGone = textView?.text.isNullOrEmpty()
        textView?.let {
            // 显示或隐藏
            it.visibility = if (textGone) View.GONE else View.VISIBLE
            // 间隔大小
            val params = it.layoutParams as ViewGroup.MarginLayoutParams
            val margin = (imageSize[1] / 4 + it.textSize) / 2
            params.topMargin = margin.toInt()
            it.requestLayout()
        }

        val width = imageSize[0]
        val height = imageSize[1]
        val params = imageView?.layoutParams
        // 改变图片大小
        if (width != params?.width) params?.width = width
        if (height != params?.height) params?.height = width

        // 设置图片
        when (image) {
            is Int -> imageView?.setImageResource(image as Int)
            is Drawable -> imageView?.setImageDrawable(image as Drawable)
            is Bitmap -> imageView?.setImageBitmap(image as Bitmap)
            else -> imageView?.setImageBitmap(null)
        }
    }

    /**
     * 设置重试文本
     * @param text 重试文本
     */
    fun setRetryText(text: String) = also { retryText = text }

    /**
     * 显示重试按钮
     * @param visible 是否显示
     */
    fun setRetryVisible(visible: Boolean) = also { retryVisible = visible }

    /**
     * 重试监听
     * @param listener 监听
     */
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
    fun setImage(@DrawableRes id: Int) = also { this.image = id }

    /**
     * 设置背景资源
     * @param bitmap 图片[Bitmap]
     */
    fun setImage(bitmap: Bitmap?) = also { this.image = bitmap }

    /**
     * 设置图片宽度
     * @param width 宽度
     */
    fun setImageWidth(width: Number) = also { this.imageSize[0] = Sizes.dp2px(width.toFloat()) }

    /**
     * 设置图片宽度
     * @param width 宽度
     */
    fun setImageWidth(width: Number, unit: Int) = also { this.imageSize[0] = Sizes.any2px(width, unit) }

    /**
     * 设置图片高度
     * @param height 高度
     */
    fun setImageHeight(height: Number) = also { this.imageSize[1] = Sizes.dp2px(height.toFloat()) }

    /**
     * 设置图片宽度
     * @param height 高度
     */
    fun setImageHeight(height: Number, unit: Int) = also { this.imageSize[1] = Sizes.any2px(height, unit) }
}