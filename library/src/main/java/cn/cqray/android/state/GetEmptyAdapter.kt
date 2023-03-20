package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import cn.cqray.android.R

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
    fun setImage(@DrawableRes id: Int?) = also { this.image = id }

    /**
     * 设置背景资源
     * @param bitmap 图片[Bitmap]
     */
    fun setImage(bitmap: Bitmap?) = also { this.image = bitmap }
}