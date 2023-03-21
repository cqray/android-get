package cn.cqray.android.tip

import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import cn.cqray.android.Get
import cn.cqray.android.databinding.GetLayoutTipBinding
import cn.cqray.android.util.JsonUtils
import cn.cqray.android.util.ScreenUtils
import cn.cqray.android.util.Sizes
import java.util.*

/**
 * Tip适配器实现
 * @author Cqray
 */
open class GetTipDefAdapter : GetTipAdapter {

    /** 提示[Toast] **/
    private var tipToast: Toast? = null

    /** 提示任务集合 **/
    private val tipTasks: MutableList<TipTask> = Collections.synchronizedList(ArrayList())

    /** 提示处理[Handler] **/
    private val tipHandler: Handler = Handler(Looper.getMainLooper()) {
        cancelTip()
        showTip()
        true
    }

    /**
     * 显示提示
     */
    private fun showTip() {
        if (tipTasks.isEmpty()) return
        tipHandler.post {
            val task = tipTasks[0]
            val init = task.init
            initTip(task)
            tipHandler.sendEmptyMessageDelayed(0, init.duration.toLong())
        }
    }

    /**
     * 取消提示
     */
    private fun cancelTip() {
        tipToast?.let {
            it.cancel()
            tipToast = null
            tipHandler.removeMessages(0)
            if (tipTasks.isNotEmpty()) {
                // 提示隐藏回调
                tipTasks.removeAt(0).hideCallback?.invoke()
            }
        }
    }

    /**
     * 初始化Toast
     * @param tipTask [TipTask]
     */
    private fun initTip(tipTask: TipTask) {
        // 深拷贝配置信息
        val init = JsonUtils.deepClone(tipTask.init, GetTipInit::class.java)!!
        // 这一步用于自定义实现不同的效果
        onHandle(tipTask.text, init)
        // 初始化界面
        val binding = GetLayoutTipBinding.inflate(LayoutInflater.from(Get.context))
        // 设置容器满屏
        val params = binding.getTipLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.width = ScreenUtils.getAppScreenWidth()
        params.height = ScreenUtils.getAppScreenHeight()
        // 设置文本信息
        binding.getTipText.text = tipTask.text
        setTipTextInfo(binding.getTipText, init)
        // 设置提示背景
        setTipBackground(binding.getTipText, init)
        // 设置Tip位置
        setTipLocation(binding.getTipText, init)
        // 初始化Toast
        synchronized(GetTip::class.java) {
            tipToast = Toast.makeText(Get.context, "", Toast.LENGTH_LONG)
            tipToast?.let {
                @Suppress("deprecation")
                it.view = binding.root
                it.show()
                tipTask.showCallback?.invoke()
            }
        }
    }

    /**
     * 设置文本信息
     * @param view 文本控件 [TextView]
     * @param init 配置属性 [GetTipInit]
     */
    private fun setTipTextInfo(view: TextView, init: GetTipInit) {
        // 设置Tip样式
        val size = if (init.textSize == null) Sizes.h3Sp() else init.textSize
        val sizePx = Sizes.dp2px(size!!).toFloat()
        val lrPadding = (sizePx * 3 / 2).toInt()
        val tbPadding = (sizePx * 2 / 3).toInt()
        // 设置Tip宽度限制
        view.minimumWidth = Sizes.dp2px(init.minWidth)
        if (init.maxWidth != null) {
            view.maxWidth = Sizes.dp2px(init.maxWidth!!)
        }
        view.setTextColor(init.textColor)
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx)
        view.paint.isFakeBoldText = init.textBold
        view.setPadding(lrPadding, tbPadding, lrPadding, tbPadding)
    }

    /**
     * 设置Tip控件背景
     * @param view 文本控件 [TextView]
     * @param init 配置属性 [GetTipInit]
     */
    private fun setTipBackground(view: TextView, init: GetTipInit) {
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
     * @param init 配置属性 [GetTipInit]
     */
    private fun setTipLocation(view: TextView, init: GetTipInit) {
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

    override fun show(
        tag: Any?,
        text: CharSequence?,
        init: GetTipInit?,
        hideCallback: Function0<Unit>?,
        showCallback: Function0<Unit>?,
    ) {
        // 显示Get框架Tip
        val tipTask = TipTask()
        tipTask.text = text
        tipTask.init = init ?: Get.init.tipInit
        tipTask.hideCallback = hideCallback
        tipTask.showCallback = showCallback
        addTipTask(tipTask)
    }

    /**
     * 处理初始化数据，以达到显示不同布局目的
     * @param text 显示文本
     * @param init 配置信息
     */
    open fun onHandle(text: CharSequence?, init: GetTipInit) {}

    private class TipTask {
        /** 文本  */
        var text: CharSequence? = null

        /** 隐藏回调 **/
        var hideCallback: Function0<Unit>? = null

        /** 显示回调 **/
        var showCallback: Function0<Unit>? = null

        /** Tip属性  */
        lateinit var init: GetTipInit
    }
}