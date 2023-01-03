package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import cn.cqray.android.R

/**
 * 空布局适配器实现
 * @author Cqray
 */
class EmptyAdapter : StateAdapter<EmptyAdapter>(R.layout.get_layout_state_empty) {

    /** 图片控件  */
    @Transient
    private var imageView: ImageView? = null

    /** 文本控件  */
    @Transient
    private var textView: TextView? = null

    /** 重试控件  */
    @Transient
    private var retryView: TextView? = null

    /** 图片资源  */
    private var imageResource: Any? = null

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        imageView = view.findViewById(R.id.get_state_img)
        textView = view.findViewById(R.id.get_state_text)
        retryView = view.findViewById(R.id.get_state_retry)
//        mRetryView.setVisibility(View.GONE)
//        if (mImageResource != null) {
//            mImageView.setImageDrawable(mImageResource)
//        }
        setDefaultText("暂无数据")
    }

    override fun onTextChanged(text: String?) {
        textView?.text = text
    }

    override fun onPostViewChanged(view: View) {
        super.onPostViewChanged(view)
        // 设置图片
        when (imageResource) {
            is Int -> imageView?.setImageResource(imageResource as Int)
            is Drawable -> imageView?.setImageDrawable(imageResource as Drawable)
            is Bitmap -> imageView?.setImageBitmap(imageResource as Bitmap)
            else -> imageView?.setImageBitmap(null)
        }
    }

    /**
     * 设置图片
     * @param image [Drawable]
     */
    fun setImage(image: Drawable?): EmptyAdapter {
        this.imageResource = image
        return this
    }

    /**
     * 设置背景资源
     * @param resId 资源ID[DrawableRes]
     */
    fun setImageResource(@DrawableRes resId: Int?): EmptyAdapter {
        this.imageResource = resId
        return this
    }

    /**
     * 设置背景资源
     * @param bitmap 图片[Bitmap]
     */
    fun setImageBitmap(bitmap: Bitmap?): EmptyAdapter {
        this.imageResource = bitmap
        return this
    }
}