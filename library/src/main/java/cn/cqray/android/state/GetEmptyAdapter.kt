package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.DrawableRes
import cn.cqray.android.R
import cn.cqray.android.databinding.GetStateLayoutEmptyBinding
import cn.cqray.android.lifecycle.GetLiveData
import cn.cqray.android.util.Sizes
import cn.cqray.android.util.Views

/**
 * 空布局适配器实现
 * @author Cqray
 */
@Suppress("Unused")
class GetEmptyAdapter : GetStateAdapter<GetEmptyAdapter>() {

    /** 图片资源  */
    private val image by lazy { GetLiveData<Any?>(R.drawable.empty2) }

    /** 图片宽度 **/
    private val imageWidth by lazy { GetLiveData(Sizes.dp2px(100)) }

    /** 图片高度 **/
    private val imageHeight by lazy { GetLiveData(Sizes.dp2px(100)) }

    /** 绑定视图 **/
    private val binding by lazy { Views.getBinding(GetStateLayoutEmptyBinding::class.java) }

    /** 状态文本 **/
    override val stateText: TextView get() = binding.getStateText

    init {
        setDefaultText("暂无数据")
    }

    override fun onCreating() {
        super.onCreating()
        // 设置界面
        setContentView(binding.root)
        // 图片
        image.observe(this) {
            val iv = binding.getStateImg
            // 设置图片
            when (it) {
                is Int -> iv.setImageResource(it)
                is Drawable -> iv.setImageDrawable(it)
                is Bitmap -> iv.setImageBitmap(it)
                else -> iv.setImageBitmap(null)
            }
        }
        // 图片宽度
        imageWidth.observe(this) {
            with(binding.getStateImg) {
                layoutParams.width = it
                requestLayout()
            }
        }
        // 图片高度
        imageHeight.observe(this) {
            with(binding.getStateImg) {
                layoutParams.height = it
                requestLayout()
                // 间隔大小
                val margin = (imageHeight.value!! / 4 + stateText.textSize) / 2
                setTextTopMargin(margin, TypedValue.COMPLEX_UNIT_PX)
            }
        }
    }

    /**
     * 设置图片
     * @param image [Drawable]
     */
    fun setImage(image: Drawable?) = also { this.image.setValue(image) }

    /**
     * 设置背景资源
     * @param id 资源ID[DrawableRes]
     */
    fun setImage(@DrawableRes id: Int) = also { image.setValue(id) }

    /**
     * 设置背景资源
     * @param bitmap 图片[Bitmap]
     */
    fun setImage(bitmap: Bitmap?) = also { image.setValue(bitmap) }

    /**
     * 设置图片宽度
     * @param width 宽度
     */
    fun setImageWidth(width: Number) = also { imageWidth.setValue(Sizes.dp2px(width.toFloat())) }

    /**
     * 设置图片宽度
     * @param width 宽度
     */
    fun setImageWidth(width: Number, unit: Int) = also { imageWidth.setValue(Sizes.any2px(width, unit)) }

    /**
     * 设置图片高度
     * @param height 高度
     */
    fun setImageHeight(height: Number) = also { imageHeight.setValue(Sizes.dp2px(height.toFloat())) }

    /**
     * 设置图片宽度
     * @param height 高度
     */
    fun setImageHeight(height: Number, unit: Int) = also { imageHeight.setValue(Sizes.any2px(height, unit)) }
}