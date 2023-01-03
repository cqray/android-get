package cn.cqray.android.tip

import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import cn.cqray.android.Get
import cn.cqray.android.Get.context
import cn.cqray.android.R
import cn.cqray.android.util.Sizes
import java.util.*

/**
 * Get框架Tip统一处理
 * @author Cqray
 */
object GetTip {
    /** Tip Toast  */
    private var tipToast: Toast? = null

    /** Tip Handler  */
    private val tipHandler: Handler = Handler(Looper.getMainLooper()) {
        cancelTip()
        showTip()
        true
    }

    /** Tip任务集合  */
    private val tipTasks: MutableList<TipTask> = Collections.synchronizedList(ArrayList())

    private fun showTip() {
        if (tipTasks.isEmpty()) {
            return
        }
        tipHandler.post {
            val task = tipTasks[0]
            val init = task.init
            initTip(task)
            tipHandler.sendEmptyMessageDelayed(0, init.duration.toLong())
        }
    }

    private fun cancelTip() {
        if (tipToast != null) {
            tipToast!!.cancel()
            tipToast = null
            tipHandler.removeMessages(0)
            if (tipTasks.isNotEmpty()) tipTasks.removeAt(0).callback?.onHide()
        }
    }

    /**
     * 初始化Toast
     * @param tipTask [TipTask]
     */

    private fun initTip(tipTask: TipTask) {
        val context = context
        val dm = context.resources.displayMetrics
        val init = tipTask.init
        // 初始化界面
        val view = View.inflate(context, R.layout.get_layout_tip, null)
        val text = view.findViewById<TextView>(R.id.get_tip_text)
        val root = text.parent as FrameLayout
        val halfWidth = (dm.widthPixels - 10) / 2
        val halfHeight = (dm.heightPixels - 10) / 2
        // 充满屏幕
        val params = root.layoutParams as MarginLayoutParams
        view.setPadding(halfWidth, halfHeight, halfWidth, halfHeight)
        params.width = halfWidth * 2
        params.height = halfHeight * 2
        params.setMargins(-halfWidth, -halfHeight, -halfWidth, -halfHeight)
        // 设置Tip样式
        val size = if (init.textSize == null) Sizes.h3Sp().toFloat() else init.textSize
        val sizePx = Sizes.dp2px(size!!).toFloat()
        val lrPadding = (sizePx * 3 / 2).toInt()
        val tbPadding = (sizePx * 2 / 3).toInt()
        // 设置Tip宽度限制
        text.minimumWidth = Sizes.dp2px(init.minWidth)
        if (init.maxWidth != null) {
            text.maxWidth = Sizes.dp2px(init.maxWidth!!)
        }
        text.setTextColor(init.textColor)
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx)
        text.paint.isFakeBoldText = init.textBold
        text.text = tipTask.text
        text.setPadding(lrPadding, tbPadding, lrPadding, tbPadding)
        // 设置提示背景
        setTipBackground(text, init)
        // 设置Tip位置
        setTipLocation(text, init)
        // 初始化Toast
        synchronized(GetTip::class.java) {
            tipToast = Toast.makeText(context, "", Toast.LENGTH_LONG)
            tipToast?.let {
                it.view = view
                it.show()
            }
            tipTask.callback?.onShow()
        }
    }

    /**
     * 设置Tip控件背景
     * @param view     文本控件 [TextView]
     * @param init 配置属性 [TipInit]
     */
    private fun setTipBackground(view: TextView, init: TipInit) {
        val radiiCount = 8
        var radii = init.backgroundRadii
        // 设置默认的圆角
        if (radii == null || radii.size < radiiCount) {
            val radius = Sizes.dp2px(999f).toFloat()
            radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        }
        // 构建背景Drawable
        val background = GradientDrawable()
        background.setColor(init.backgroundColor)
        background.cornerRadii = radii
        // 设置背景
        ViewCompat.setBackground(view, background)
    }

    /**
     * 设置Tip控件位置
     * @param init 配置属性 [TipInit]
     */
    private fun setTipLocation(view: TextView, init: TipInit) {
        val params = view.layoutParams as FrameLayout.LayoutParams
        val vGravity = if (init.atBottom) Gravity.BOTTOM else Gravity.TOP
        params.gravity = Gravity.CENTER_HORIZONTAL or vGravity
        params.leftMargin = (view.textSize * 2).toInt()
        params.rightMargin = (view.textSize * 2).toInt()
        if (init.atBottom) {
            params.bottomMargin = Sizes.dp2px(init.offset)
        } else {
            params.topMargin = Sizes.dp2px(init.offset)
        }
    }

    /**
     * 添加Tip任务
     * @param task Tip任务 [TipTask]
     */
    private fun addTipTask(task: TipTask) {
        val init = task.init
        if (tipTasks.isEmpty() || tipTasks.size >= init.queueCount) {
            tipTasks.add(task)
            tipHandler.sendEmptyMessage(0)
        } else {
            tipTasks.add(task)
        }
    }

    /**
     * 显示Tip
     * @param text Tip内容 [CharSequence]
     */
    @JvmStatic
    fun show(text: CharSequence?) {
        show(null, text, null, null)
    }

    /**
     * 显示Tip
     * @param text     Tip内容 [CharSequence]
     * @param callback TIP回调 [GetTipCallback]
     */
    @JvmStatic
    fun show(text: CharSequence?, callback: GetTipCallback?) {
        show(TipLevel.INFO, text, null, callback)
    }

    /**
     * 显示Tip
     * @param level    Tip级别 [TipLevel]
     * @param text     Tip内容 [CharSequence]
     * @param init Tip属性 [TipInit]
     * @param callback TIP回调 [GetTipCallback]
     */
    @JvmStatic
    fun show(
        level: TipLevel?,
        text: CharSequence?,
        init: TipInit?,
        callback: GetTipCallback?
    ) {
        val tipTask = TipTask()
        tipTask.level = level ?: TipLevel.INFO
        tipTask.text = text
        tipTask.init = init ?: Get.init.tipInit!!
        tipTask.callback = callback
        addTipTask(tipTask)
    }
}

private class TipTask {
    /** Tip等级  */
    var level: TipLevel? = null

    /** 文本  */
    var text: CharSequence? = null

    /** 结束回调  */
    var callback: GetTipCallback? = null

    /** Tip属性  */
    lateinit var init: TipInit
}