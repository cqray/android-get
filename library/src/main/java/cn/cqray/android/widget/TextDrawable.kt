package cn.cqray.android.widget

import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.text.TextUtils
import cn.cqray.android.util.Sizes
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

/**
 * 带文字的Drawable
 * @author Cqray
 */
class TextDrawable private constructor() : ShapeDrawable() {
    /** 文本画笔  */
    private val textPaint = Paint()

    /** 边框画笔  */
    private val borderPaint = Paint()

    /** 是否初始化  */
    private val paintInit = AtomicBoolean()

    /** 高度  */
    private val height = 0f

    /** 宽度  */
    private val width = 0f

    /** 文本  */
    private val text: String? = null

    /** 字体颜色  */
    private val textColor = 0

    /** 字体大小  */
    private val textSize = 0f

    /** 字体加粗  */
    private val textBold = false

    /** 字体边框厚度  */
    private val textBorderThickness = 0f

    /** 背景颜色  */
    private var color = 0

    /** 背景圆角  */
    private val radius = 0f

    /** 背景圆角  */
    private val radii: FloatArray? = null

    /** 边框颜色  */
    private val borderColor = 0

    /** 背景图形边框厚度  */
    private val borderThickness = 0f
    private fun getDarkerShade(color: Int): Int {
        return Color.rgb(
            (0.9 * Color.red(color)).toInt(),
            (0.9 * Color.green(color)).toInt(),
            (0.9 * Color.blue(color)).toInt()
        )
    }

    override fun draw(canvas: Canvas) {
        // 初始化画笔
        initPaints()
        // 获取界限
        val rect = bounds
        // 设置圆角矩形Path
        val radii = FloatArray(8)
        for (i in radii.indices) {
            radii[i] = Sizes.dp2px(this.radii?.get(i) ?: radius).toFloat()
        }
        val path = Path()
        path.addRoundRect(RectF(bounds), radii, Path.Direction.CW)
        // 绘制边框
        if (borderThickness > 0) {
            val thickness = Sizes.dp2px(borderThickness / 2)
            val rectf = RectF(bounds)
            rectf.inset(thickness.toFloat(), thickness.toFloat())
            canvas.drawPath(path, borderPaint)
        }
        // 绘制背景
        canvas.drawPath(path, paint)
        // 保存并移动画布
        val count = canvas.save()
        canvas.translate(rect.left.toFloat(), rect.top.toFloat())
        // 获取画布宽高
        val width = if (intrinsicWidth < 0) rect.width() else intrinsicWidth
        val height = if (intrinsicHeight < 0) rect.height() else intrinsicHeight
        // 绘制文字
        if (!TextUtils.isEmpty(text)) {
            val textSize = if (textSize <= 0) min(width, height) / 2f else textSize
            textPaint.textSize = Sizes.dp2px(textSize).toFloat()
            canvas.drawText(text!!, width / 2f, height / 2f - (textPaint.descent() + textPaint.ascent()) / 2, textPaint)
        }
        // 恢复画布
        canvas.restoreToCount(count)
    }

    @Synchronized
    private fun initPaints() {
        if (paintInit.get()) {
            return
        }
        shape = RectShape()
        textPaint.color = textColor
        textPaint.isAntiAlias = true
        textPaint.isFakeBoldText = textBold
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.strokeWidth = textBorderThickness
        borderPaint.color = if (borderColor == 0) getDarkerShade(color) else borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderThickness
        val paint = paint
        paint.color = color
        paint.isAntiAlias = true
    }

    override fun setAlpha(alpha: Int) {
        initPaints()
        textPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        initPaints()
        textPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return Sizes.dp2px(width)
    }

    override fun getIntrinsicHeight(): Int {
        return Sizes.dp2px(height)
    }

    fun setColor(color: Int?) = also {
        this.color = color?: this.color
    }
}