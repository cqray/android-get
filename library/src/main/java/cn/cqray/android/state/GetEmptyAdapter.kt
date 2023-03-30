package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import cn.cqray.android.R
import cn.cqray.android.util.Sizes

/**
 * 空布局适配器实现
 * @author Cqray
 */
@Suppress("Unused")
class GetEmptyAdapter : GetStateAdapter<GetEmptyAdapter>(R.layout.get_layout_state_empty) {

    /** 图片控件  */
    private var imageView: ImageView? = null

    /** 图片资源  */
    private var image: Any? = R.drawable.empty2

    /** 图片尺寸 **/
    private val imageSize = arrayOf(Sizes.dp2px(80), Sizes.dp2px(80))

    init {
        setDefaultText("暂无数据")
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        imageView = view.findViewById(R.id.get_state_img)
        textView = view.findViewById(R.id.get_state_text)
    }

    override fun onViewChanged(view: View) {
        super.onViewChanged(view)
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