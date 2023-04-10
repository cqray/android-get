package cn.cqray.android.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import cn.cqray.android.Get
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils

@Suppress("Unchecked_cast")
object Views {

    @JvmStatic
    fun <VB : ViewBinding> binding(bindingClass: Class<VB>): VB {
        val activity = Get.topActivity
        val context = activity ?: Get.context
        val content = activity?.findViewById<ViewGroup>(android.R.id.content)
        val method = bindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return method.invoke(null, LayoutInflater.from(context), content, false) as VB
    }

    /**
     * 渲染界面
     * @param id 布局ID
     */
    @JvmStatic
    fun inflate(@LayoutRes id: Int): View {
        Get.topActivity?.let {
            val parent = it.findViewById<ViewGroup>(android.R.id.content)
            return LayoutInflater.from(it).inflate(id, parent, false)
        }
        val context = Get.context
        val parent = FrameLayout(context).also { it.layoutParams = ViewGroup.LayoutParams(-1, -1) }
        val view = LayoutInflater.from(context).inflate(id, parent, false)
        System.gc()
        return view
    }

    @JvmStatic
    fun setEditTextEnable(editText: EditText, enable: Boolean) {
        editText.isFocusableInTouchMode = enable
        editText.isFocusable = enable
        editText.isClickable = enable
        editText.isEnabled = enable
    }

    fun setRippleBackground(view: View, rippleEnable: Boolean?) {
        val context = view.context
        val ripple = rippleEnable == null || rippleEnable
        if (ripple) {
            var drawable: Drawable? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val ta = context.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarItemBackground))
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

    /**
     * 设置控件的越界属性
     * @param view 控件
     * @param overScrollMode 越界属性
     */
    fun setOverScrollMode(view: View?, overScrollMode: Int) {
        view?.let {
            when(it) {
                is ViewPager2 -> it.getChildAt(0).overScrollMode = overScrollMode
                else -> it.overScrollMode = overScrollMode
            }
        }
    }

    /**
     * 关闭[RecyclerView]自带动画
     * @param recyclerView [RecyclerView]控件
     */
    fun closeRvAnimator(recyclerView: RecyclerView?) {
        recyclerView?.let {
            val animator = it.itemAnimator
            animator?.let {
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

    private fun createMaterialShapeDrawableBackground(context: Context, background: Drawable): MaterialShapeDrawable {
        val materialShapeDrawable = MaterialShapeDrawable()
        if (background is ColorDrawable) {
            materialShapeDrawable.fillColor = ColorStateList.valueOf(background.color)
        }
        materialShapeDrawable.initializeElevationOverlay(context)
        return materialShapeDrawable
    }



    /**
     * View转化为Activity
     * @param view 视图
     */
    @JvmStatic
    fun view2Activity(view: View?): Activity? {
        return view?.let {
            var context = view.context
            while (context is ContextWrapper) {
                if (context is Activity) return context
                else context = context.baseContext
            }
            null
        }
    }
}