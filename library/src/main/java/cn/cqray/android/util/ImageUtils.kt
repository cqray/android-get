package cn.cqray.android.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import cn.cqray.android.Get
import kotlin.math.max


object ImageUtils {

    /**
     * 将[View]转换成[Bitmap]
     * @param view 视图
     */
    @Suppress("Deprecation")
    fun view2Bitmap(view: View?): Bitmap? {
        if (view == null) return null
        val drawingCacheEnabled = view.isDrawingCacheEnabled
        val willNotCacheDrawing = view.willNotCacheDrawing()
        view.isDrawingCacheEnabled = true
        view.setWillNotCacheDrawing(false)
        var drawingCache = view.drawingCache
        val bitmap: Bitmap
        if (null == drawingCache || drawingCache.isRecycled) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.buildDrawingCache()
            drawingCache = view.drawingCache
            if (null == drawingCache || drawingCache.isRecycled) {
                bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Config.RGB_565)
                val canvas = Canvas(bitmap)
                view.draw(canvas)
            } else {
                bitmap = Bitmap.createBitmap(drawingCache)
            }
        } else {
            bitmap = Bitmap.createBitmap(drawingCache)
        }
        view.setWillNotCacheDrawing(willNotCacheDrawing)
        view.isDrawingCacheEnabled = drawingCacheEnabled
        return bitmap
    }

    /**
     * 将[Activity]转化为[Bitmap]
     * @param activity [Activity]
     */
    fun activity2Bitmap(activity: Activity?) = view2Bitmap(activity?.findViewById(android.R.id.content))

    fun drawable2Bitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) return drawable.bitmap
        val opaque = drawable.opacity == PixelFormat.OPAQUE
        val width = max(drawable.intrinsicWidth, 1)
        val height = max(drawable.intrinsicHeight, 1)
        val bitmap = Bitmap.createBitmap(width, height, if (opaque) Config.RGB_565 else Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun bitmap2Drawable(bitmap: Bitmap) = BitmapDrawable(Get.context.resources, bitmap)

}