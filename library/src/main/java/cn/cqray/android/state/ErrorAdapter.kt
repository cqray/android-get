package cn.cqray.android.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import cn.cqray.android.R

/**
 * 错误界面适配器
 * @author Cqray
 */
class ErrorAdapter : StateAdapter<ErrorAdapter>(R.layout.get_layout_state_error) {

    /** 图片控件  */
    private var imageView: ImageView? = null

    /** 文本控件  */
    private var textView: TextView? = null

    /** 重试控件  */
    private var retryView: TextView? = null

    /** 图片资源  */
    private var imageResource: Any? = null

    private var mRetryListener: View.OnClickListener? = null

//    init {
//        if (javaClass.superclass == StateAdapter::class.java) {
//            makeSureOverridden()
//        }
//        setDefaultText("暂无数据")
//    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        val parent = view as ViewGroup
        val btn = parent.getChildAt(2) as TextView
        btn.setOnClickListener { v: View? ->
            if (mRetryListener != null) {
                mRetryListener!!.onClick(v)
            }
        }
        imageView = view.findViewById(R.id.get_state_img)
        textView = view.findViewById(R.id.get_state_text)
        retryView = view.findViewById(R.id.get_state_retry)
//        retryView.setVisibility(View.GONE)
//        if (mImageResource != null) {
//            mImageView.setImageDrawable(mImageResource)
//        }
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

    fun setButtonVisible(visible: Boolean): ErrorAdapter {
        val parent = view as ViewGroup
        val btn = parent.getChildAt(2) as TextView
        btn.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setRetryListener(listener: View.OnClickListener?) {
        //mRetryListener = listener
    }

    /**
     * 设置图片
     * @param image [Drawable]
     */
    fun setImage(image: Drawable?): ErrorAdapter {
        this.imageResource = image
        return this
    }

    /**
     * 设置背景资源
     * @param resId 资源ID[DrawableRes]
     */
    fun setImageResource(@DrawableRes resId: Int?): ErrorAdapter {
        this.imageResource = resId
        return this
    }

    /**
     * 设置背景资源
     * @param bitmap 图片[Bitmap]
     */
    fun setImageBitmap(bitmap: Bitmap?): ErrorAdapter {
        this.imageResource = bitmap
        return this
    }
}