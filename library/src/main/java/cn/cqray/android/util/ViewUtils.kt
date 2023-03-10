package cn.cqray.android.util

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils
import java.util.concurrent.atomic.AtomicReference

object ViewUtils {
    //    /** 设置Margin，默认单位DP **/
    //    public static void setMargin(View view, float margin) {
    //        setMargin(view, margin, TypedValue.COMPLEX_UNIT_DIP);
    //    }
    //
    //    public static void setMargin(View view, float margin, int unit) {
    //        setMargin(view, margin, margin, margin, margin, unit);
    //    }
    //    /** 设置Margin，默认单位DP **/
    //    public static void setMargin(View view, float left, float top, float right, float bottom) {
    //        setMargin(view, left, top, right, bottom, TypedValue.COMPLEX_UNIT_DIP);
    //    }
    //    public static void setMargin(View view, float left, float top, float right, float bottom, int unit) {
    //        if (view != null) {
    //            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
    //            params.leftMargin = (int) Sizes.applyDimension(left, unit);
    //            params.topMargin = (int) Sizes.applyDimension(top, unit);
    //            params.rightMargin = (int) Sizes.applyDimension(right, unit);
    //            params.bottomMargin = (int) Sizes.applyDimension(bottom, unit);
    //            view.requestLayout();
    //        }
    //    }


    fun setRippleBackground(view: View, rippleEnable: Boolean?) {
        val context = view.context
        val ripple = rippleEnable == null || rippleEnable
        if (ripple) {
            var drawable: Drawable? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val ta = context.obtainStyledAttributes(
                    intArrayOf(
                        R.attr.actionBarItemBackground
                    )
                )
                drawable = ta.getDrawable(0)
                ta.recycle()
            }
            ViewCompat.setBackground(view, drawable)
        }
        //        else {
//            ViewCompat.setBackground(view, null);
//        }
    }

    fun setElevation(view: View, elevation: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = elevation
        }
        val background = view.background
        if (background is ColorDrawable) {
            ViewCompat.setBackground(view, createMaterialShapeDrawableBackground(view.context, background))
        }
        MaterialShapeUtils.setParentAbsoluteElevation(view)
        MaterialShapeUtils.setElevation(view, elevation)
    }

    fun setOverScrollMode(view: View?, overScrollMode: Int) {
        runCatching {
            if (view !is ViewPager2) view?.overScrollMode = overScrollMode
            else view.getChildAt(0)?.overScrollMode = overScrollMode
        }
    }

    private fun createMaterialShapeDrawableBackground(context: Context, background: Drawable): MaterialShapeDrawable {
        val materialShapeDrawable = MaterialShapeDrawable()
        if (background is ColorDrawable) {
            materialShapeDrawable.fillColor = ColorStateList.valueOf(background.color)
        }
        materialShapeDrawable.initializeElevationOverlay(context)
        return materialShapeDrawable
    }

    fun closeRvAnimator(rv: RecyclerView?) {
        if (rv != null) {
            val animator = rv.itemAnimator
            if (animator != null) {
                animator.addDuration = 0
                animator.changeDuration = 0
                animator.moveDuration = 0
                animator.removeDuration = 0
            }
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
        }
    }

    /**
     * View转化为Activity
     * @param view 视图
     */
    @JvmStatic
    fun view2Activity(view: View?): Activity? {
        return view?.let {
            val context = AtomicReference(view.context)
            while (context.get() is ContextWrapper) {
                val tmp = context.get() as ContextWrapper
                if (tmp is Activity) return tmp
                else context.set(tmp.baseContext)
            }
            null
        }
    }

    /**
     * View转Bitmap
     * @param view 视图
     */
    @JvmStatic
    fun view2Bitmap(view: View?) = ImageUtils.view2Bitmap(view)

    /** 渲染界面  */
    fun inflate(@LayoutRes resId: Int): View {
        return ContextUtils.inflate(resId)
    }
}