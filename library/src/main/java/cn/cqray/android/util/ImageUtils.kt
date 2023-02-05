package cn.cqray.android.util

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import cn.cqray.android.Get
import kotlin.math.max


object ImageUtils {

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