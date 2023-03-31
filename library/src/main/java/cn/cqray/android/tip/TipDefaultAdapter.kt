//package cn.cqray.android.tip
//
//import android.graphics.drawable.GradientDrawable
//import android.util.TypedValue
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.view.ViewCompat
//import cn.cqray.android.Get
//import cn.cqray.android.util.Sizes
//import com.blankj.utilcode.util.Utils
//import com.hjq.toast.ToastParams
//import com.hjq.toast.Toaster
//
//class TipDefaultAdapter : GetTipAdapter {
//
//    private var isInit = false
//
//    override fun show(
//        tag: Any?,
//        text: CharSequence?,
//        init: TipInit?,
//        hideCallback: (() -> Unit)?,
//        showCallback: (() -> Unit)?
//    ) {
//        // 保证初始化完成
//        synchronized(TipDefaultAdapter::class.java) {
//            // 初始化Toaster
//            if (!Toaster.isInit()) {
//                Toaster.init(Utils.getApp(), null) {
//                    val view = TextView(it)
//                    setTipTextInfo(view, Get.init.tipInit)
//                    setTipBackground(view, Get.init.tipInit)
//                    view
//                }
//            }
//        }
//        Toaster.show(ToastParams().apply {
//            this.text = text
//            this.style = Toaster.getStyle()
//            this.strategy = Toaster.getStrategy()
//            this.duration = Toast.LENGTH_LONG
//            this.interceptor = TipLogInterceptor()
//        })
//    }
//
//    /**
//     * 设置文本信息
//     * @param view 文本控件 [TextView]
//     * @param init 配置属性 [TipInit]
//     */
//    private fun setTipTextInfo(view: TextView, init: TipInit) {
//        // 设置Tip样式
//        val size = if (init.textSize == null) Sizes.spH3() else init.textSize
//        val sizePx = Sizes.dp2px(size!!).toFloat()
//        val lrPadding = (sizePx * 3 / 2).toInt()
//        val tbPadding = (sizePx * 2 / 3).toInt()
//        // 设置Tip宽度限制
//        view.minimumWidth = Sizes.dp2px(init.minWidth)
//        if (init.maxWidth != null) {
//            view.maxWidth = Sizes.dp2px(init.maxWidth!!)
//        }
//        view.setTextColor(init.textColor)
//        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx)
//        view.paint.isFakeBoldText = init.textBold
//        view.setPadding(lrPadding, tbPadding, lrPadding, tbPadding)
//    }
//
//    /**
//     * 设置Tip控件背景
//     * @param view 文本控件 [TextView]
//     * @param init 配置属性 [TipInit]
//     */
//    private fun setTipBackground(view: TextView, init: TipInit) {
//        val radiiCount = 8
//        var radii = init.backgroundRadii
//        // 设置默认的圆角
//        if (radii == null || radii.size < radiiCount) {
//            val radius = Sizes.dp2px(999f).toFloat()
//            radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
//        }
//        // 构建背景Drawable
//        val background = GradientDrawable()
//        background.setColor(init.backgroundColor)
//        background.cornerRadii = radii
//        // 设置背景
//        ViewCompat.setBackground(view, background)
//    }
//}