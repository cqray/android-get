package cn.cqray.android.widget

import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.annotation.ColorInt
import cn.cqray.android.util.Colors
import cn.cqray.android.util.Sizes
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

/**
 * 带文字的Drawable
 * @author Cqray
 **/
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetTextDrawable(
    /** 宽度 **/
    val width: Number,
    /** 高度 **/
    val height: Number
) : ShapeDrawable() {
    /** 文本画笔 **/
    private val textPaint = Paint()

    /** 边框画笔 **/
    private val borderPaint = Paint()

    /** 是否初始化 **/
    private val paintInit = AtomicBoolean()

    /** 文本 **/
    var text: String? = null

    /** 文本颜色 **/
    @ColorInt
    var textColor = 0

    /** 文本大小 **/
    var textSize: Number = 0
        get() {
            // 获取画布宽高
            val width = intrinsicWidth
            val height = intrinsicHeight
            val size = Sizes.dp2px(field)
            // 文本大小
            return if (size <= 0) min(width, height) / 2F else size
        }

    /** 文本样式 **/
    var textStyle: Int = 0

    /** 文本边框大小 **/
    var textBorderSize: Number = 0

    /** 背景颜色 **/
    @ColorInt
    var color: Int = Colors.primary()

    /** 背景圆角 **/
    var radius: Number? = null
        set(value) {
            // 设置背景圆角
            value?.let {
                val radii = this.radii ?: Array<Number>(8) { 0 }
                for (i in 0 until 8) {
                    radii[i] = value
                }
            }
            field = null
        }

    /** 背景圆角 **/
    var radii: Array<Number>? = null

    /** 边框颜色 **/
    @ColorInt
    var borderColor: Int? = null

    /** 背景图形边框大小 **/
    var borderSize: Number = 0

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

        // 设置圆角矩形Path
        val radii = FloatArray(8) {
            val size = this.radii?.getOrNull(it) ?: 0
            Sizes.dp2px(size).toFloat()
        }
        val path = Path()
        path.addRoundRect(RectF(bounds), radii, Path.Direction.CW)

        // 绘制边框
        val borderSize = Sizes.dp2px(this.borderSize).toFloat()
        if (borderSize > 0) {
            val rectF = RectF(bounds)
            rectF.inset(borderSize, borderSize)
            canvas.drawPath(path, borderPaint)
        }

        // 绘制背景
        canvas.drawPath(path, paint)
        // 保存并移动画布
        val count = canvas.save()
        // 获取界限
        val rect = bounds
        // 平移画布
        canvas.translate(rect.left.toFloat(), rect.top.toFloat())
        // 绘制文本
        if (!text.isNullOrEmpty()) {
            // 设置文本大小
            textPaint.textSize = textSize.toFloat()
            // 绘制文本
            canvas.drawText(
                text!!,
                intrinsicWidth / 2F,
                intrinsicHeight / 2F - (textPaint.descent() + textPaint.ascent()) / 2F,
                textPaint
            )
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
        // 画布画笔
        val paint = paint
        paint.color = color
        paint.isAntiAlias = true
        // 边框画笔
        borderPaint.color = borderColor ?: getDarkerShade(color)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = Sizes.dp2px(borderSize).toFloat()
        // 文本画笔
        textPaint.isAntiAlias = true
        textPaint.color = textColor
        textPaint.typeface = Typeface.defaultFromStyle(textStyle)
        textPaint.strokeWidth = Sizes.dp2px(textBorderSize).toFloat()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
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
}