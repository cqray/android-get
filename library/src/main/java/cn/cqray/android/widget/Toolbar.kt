package cn.cqray.android.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue.*
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.MutableLiveData
import cn.cqray.android.R
import cn.cqray.android.util.SizeUnit
import cn.cqray.android.util.Sizes
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils
import kotlin.math.max

/**
 * 标题栏
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Toolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    /** 标题控件  */
    val titleView: TextView by lazy {
        AppCompatEditText(context).also {
            it.id = R.id.get_toolbar_title
            it.layoutParams = LayoutParams(-1, -1)
        }
    }

    /** 回退控件 **/
    val backView: IconTextView by lazy {
        IconTextView(context).also {
            it.id = R.id.get_toolbar_back_view
            it.layoutParams = LayoutParams(-2, -1).also { p ->
                p.addRule(ALIGN_PARENT_START)
            }
        }
    }

    /** 行为组件容器 **/
    val actionLayout: ActionLayout by lazy {
        ActionLayout(context).also {
            it.id = R.id.get_toolbar_action_layout
            it.layoutParams = LayoutParams(-2, -1).also { p ->
                p.addRule(ALIGN_PARENT_END)
            }
        }
    }

    /** 分割控件 **/
    val dividerView: View by lazy { View(getContext()) }

    /** 标题栏左右间隔 **/
    private val padding = MutableLiveData<Int>()

    /** 是否使用水波纹 **/
    private val useRipple = MutableLiveData<Boolean>()

    /** 标题居中 **/
    private val titleCenter = MutableLiveData<Boolean>()

    /** 标题可编辑 **/
    private val titleEditable = MutableLiveData<Boolean>()

    /** 标题左右间隔 **/
    private val titleSpace = MutableLiveData<Int>()

    /** 生命周期注册器 **/
    private val lifecycleRegistry: LifecycleRegistry by lazy {
        LifecycleRegistry(lifecycleOwner).also {
            it.currentState = Lifecycle.State.INITIALIZED
        }
    }

    /** 生命周期持有对象 **/
    val lifecycleOwner: LifecycleOwner by lazy { LifecycleOwner { lifecycleRegistry } }

    init {
        // 设置默认Id
        if (id == NO_ID) id = R.id.get_toolbar
        // 初始化一些基础属性
        initToolbarBasic(attrs)
        // 初始化回退按钮
        initToolbarBack(attrs)
        // 初始化Action
        initToolbarAction(attrs)
        // 初始化标题
        initToolbarTittle(attrs)
        // 初始化分割线
        initToolbarDivider(attrs)
        // 初始化LiveData
        initLiveData()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        MaterialShapeUtils.setParentAbsoluteElevation(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    /**
     * 初始化基础控价
     */
    private fun initToolbarBasic(attrs: AttributeSet?) {
        val size = Sizes.content()
        val elev = Sizes.px(R.dimen.elevation)
        // 获取默认属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val pd = ta.getDimension(R.styleable.Toolbar_sPadding, size)
        val ripple = ta.getBoolean(R.styleable.Toolbar_sUseRipple, true)
        val elevation = ta.getDimension(R.styleable.Toolbar_elevation, elev)
        val editable = ta.getBoolean(R.styleable.Toolbar_titleEditable, false)
        val center = ta.getBoolean(R.styleable.Toolbar_titleCenter, false)
        val space = ta.getDimension(R.styleable.Toolbar_titleSpace, size)
        ta.recycle()
        // 设置标题栏背景
        val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val background = background ?: ColorDrawable(primaryColor)
        ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(background))
        // 其他属性
        titleSpace.value = space.toInt()
        titleCenter.value = center
        titleEditable.value = editable
        padding.value = pd.toInt()
        useRipple.value = ripple
        setElevation(elevation)
    }

    /**
     * 初始化回退控件
     */
    private fun initToolbarBack(attrs: AttributeSet?) {
        val size = Sizes.h3()
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val drawable = ta.getDrawable(R.styleable.Toolbar_backIcon)
        val visible = ta.getBoolean(R.styleable.Toolbar_backIconVisible, true)
        val text = ta.getString(R.styleable.Toolbar_backText)
        val textColor = ta.getColor(R.styleable.Toolbar_backTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.Toolbar_backTextSize, size)
        val textStyle = ta.getInt(R.styleable.Toolbar_backTextStyle, 0)
        ta.recycle()
        // 设置Back布局
        backView.setText(text)
            .setTextColor(textColor)
            .setTextSize(textSize, SizeUnit.PX)
            .setTypeface(Typeface.defaultFromStyle(textStyle))
            .iconView.visibility = if (visible) VISIBLE else GONE
        // 设置图标
        if (drawable != null) backView.setIconDrawable(drawable)
        else backView.setIconResource(R.drawable.def_back_material_light)
        // 添加到容器
        addView(backView)
    }

    /**
     * 初始化行为控件
     */
    private fun initToolbarAction(attrs: AttributeSet?) = addView(actionLayout)

    /**
     * 初始化标题文本
     */
    private fun initToolbarTittle(attrs: AttributeSet?) {
        val size = Sizes.h2()
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val text = ta.getString(R.styleable.Toolbar_titleText)
        val textColor = ta.getColor(R.styleable.Toolbar_titleTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.Toolbar_titleTextSize, size)
        val textStyle = ta.getInt(R.styleable.Toolbar_titleTextStyle, 0)
        ta.recycle()
        // 设置标题
        titleView.text = text
        titleView.setTextColor(textColor)
        titleView.setTextSize(COMPLEX_UNIT_PX, textSize)
        titleView.typeface = Typeface.defaultFromStyle(textStyle)
        titleView.setBackgroundColor(Color.RED)
        // 添加到容器
        addView(titleView)
    }

    /**
     * 初始化分割线
     */
    private fun initToolbarDivider(attrs: AttributeSet?) {
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val def = ContextCompat.getColor(context, R.color.divider)
        val color = ta.getColor(R.styleable.Toolbar_dividerColor, def)
        val height = ta.getDimensionPixelSize(R.styleable.Toolbar_dividerHeight, 0)
        val margin = ta.getDimensionPixelSize(R.styleable.Toolbar_dividerMargin, 0)
        val visible = ta.getBoolean(R.styleable.Toolbar_dividerVisible, true)
        ta.recycle()
        // 设置分割线
        val params = LayoutParams(-1, height)
        params.addRule(ALIGN_PARENT_BOTTOM)
        params.setMargins(margin, 0, margin, 0)
        dividerView.setBackgroundColor(color)
        dividerView.layoutParams = params
        dividerView.visibility = if (visible) VISIBLE else INVISIBLE
        // 添加到容器
        addView(dividerView)
    }

    private fun initLiveData() {
        titleSpace.observe(lifecycleOwner) {
            val space = titleSpace.value ?: 0
            val center = titleCenter.value ?: false
            val padding = padding.value ?: 0
            val visible = backView.iconView.visibility == VISIBLE
            val startSpace = if (visible) space - padding else 0
            val endSpace = space// - actionLayout.getActionSpace()
            val params = titleView.layoutParams as LayoutParams
            if (center) {
                val m = max(
                    backView.width + startSpace,
                    actionLayout.width + endSpace
                )
                params.marginStart = m
                params.marginEnd = m
            } else {
                params.marginStart = startSpace
                params.marginEnd = endSpace
            }
            // 更新布局参数
            titleView.layoutParams = params
        }

        // 标题居中监听
        titleCenter.observe(lifecycleOwner) {
            val params = titleView.layoutParams as LayoutParams
            if (it) {
                params.addRule(START_OF, -1)
                params.addRule(END_OF, -1)
                params.addRule(CENTER_IN_PARENT)
                titleView.gravity = Gravity.CENTER
            } else {
                params.addRule(START_OF, R.id.get_toolbar_action_layout)
                params.addRule(END_OF, R.id.get_toolbar_back_view)
                params.addRule(CENTER_VERTICAL)
                titleView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
        }
        // 监听设置标题栏是否可编辑
        titleEditable.observe(lifecycleOwner) {
            titleView.isFocusableInTouchMode = it
            titleView.isClickable = it
            titleView.isFocusable = it
            titleView.isEnabled = it
            if (it) {
                val ta = context.obtainStyledAttributes(
                    intArrayOf(android.R.attr.editTextBackground)
                )
                val drawable = ta.getDrawable(0)
                ta.recycle()
                ViewCompat.setBackground(titleView, drawable)
            } else {
                ViewCompat.setBackground(titleView, null)
            }
            backView.bringToFront()
            actionLayout.bringToFront()
        }
        // 是否使用水波纹
        useRipple.observe(lifecycleOwner) {
            backView.setUseRipple(it)
            //actionLayout.setDefaultUseRipple(it)
        }
        // 间隔大小监听
        padding.observe(lifecycleOwner) {
            // 设置BackLayout内部间隔
            val iconVisible = backView.iconView.visibility == VISIBLE
            backView.setPadding(it, 0, (if (iconVisible) it else 0), 0)
            // 设置ActionLayout右部间隔
            val params = actionLayout.layoutParams as LayoutParams
            params.marginEnd = it// - actionLayout.getActionSpace()
        }
    }

    override fun setElevation(elevation: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(elevation)
        }
        MaterialShapeUtils.setElevation(this, elevation)
    }

    override fun setGravity(gravity: Int) {}

    fun setPadding(padding: Float) = also { setPadding(padding, SizeUnit.DIP) }

    fun setPadding(padding: Float, unit: SizeUnit) = also {
        val size = Sizes.applyDimension(padding, unit).toInt()
        this.padding.setValue(size)
    }

    fun setUseRipple(useRipple: Boolean?) = also { this.useRipple.setValue(useRipple ?: true) }

    fun setTitleCenter(center: Boolean?) = also { titleCenter.value = center ?: false }

    //============================================================//
    //======================BACK部分 END==========================//
    //=====================BACK部分 START=========================//
    //============================================================//

    fun setBackSpace(space: Float?) = also { backView.setViewSpace(space) }

    fun setBackIcon(@DrawableRes resId: Int?) = also {
        if (resId == null) backView.setIconDrawable(null)
        else backView.setIconResource(resId)
    }

    fun setBackIcon(drawable: Drawable?) = also { backView.setIconDrawable(drawable) }

    fun setBackIcon(bitmap: Bitmap?) = also { backView.setIconBitmap(bitmap) }

    fun setBackIconTintColor(@ColorInt color: Int?) = also { backView.setIconTintColor(color) }

    fun setBackIconTintList(tintList: ColorStateList?) = also { backView.setIconTintList(tintList) }

    fun setBackIconVisible(visible: Boolean?) = also {
        if (visible != (backView.iconView.visibility == VISIBLE)) {
            backView.iconView.visibility = if (visible) VISIBLE else GONE
            padding.value = padding.value!!
        }
    }

    fun setBackText(text: CharSequence?) = also { backView.setText(text) }

    fun setBackText(@StringRes id: Int?) = also { backView.setText(id) }

    fun setBackTextColor(@ColorInt color: Int?) = also { color?.let { backView.setTextColor(it) } }

    fun setBackTextSize(size: Float?) = also { size?.let { backView.setTextSize(it) } }

    fun setBackTextSize(size: Float?, unit: SizeUnit) = also { size?.let { backView.setTextSize(it, unit) } }

    fun setBackTextTypeface(typeface: Typeface?) = also { backView.setTypeface(typeface) }

    fun setBackListener(listener: OnClickListener?) = also { backView.setOnClickListener(listener) }

    //============================================================//
    //======================BACK部分 END==========================//
    //=====================TITLE部分 START=========================//
    //============================================================//

    fun setTitle(@StringRes id: Int?) = also {
        if (id != null) titleView.setText(id)
        else setTitle(null as String?)
    }

    fun setTitle(text: CharSequence?) = also { titleView.text = text }

    fun setTitleTextColor(@ColorInt color: Int?) = also {
        color?.let { titleView.setTextColor(it) }
    }

    fun setTitleTextSize(size: Float?) = also {
        size?.let { titleView.setTextSize(COMPLEX_UNIT_SP, it) }
    }

    fun setTitleTextSize(size: Float?, unit: SizeUnit) = also {
        size?.let { titleView.setTextSize(unit.type, it) }
    }

    fun setTitleTextStyle(typeface: Typeface?) = also { }

    fun setTitleTypeface(typeface: Typeface?) = also { titleView.typeface = typeface }

    fun setTitleEditable(editable: Boolean) = also { titleEditable.value = editable }

    fun setTitleSpace(space: Float) = also { setTitleSpace(space, SizeUnit.DIP) }

    fun setTitleSpace(space: Float, unit: SizeUnit) = also {
        val value = Sizes.applyDimension(space, unit).toInt()
        titleSpace.value = value
    }

    //============================================================//
    //======================TITLE部分 END=========================//
    //====================ACTION部分 START========================//
    //============================================================//

    fun setActionRipple(ripple: Boolean?) = also { actionLayout.setActionRipple(ripple) }

    fun setActionRipple(key: Int?, ripple: Boolean?) = also { actionLayout.setActionRipple(key, ripple) }

    fun setActionVisible(visible: Boolean?) = also { actionLayout.setActionVisible(visible) }

    fun setActionVisible(key: Int?, visible: Boolean?) = also { actionLayout.setActionVisible(key, visible) }

    fun setActionSpace(space: Float?) = also { setActionSpace(space, SizeUnit.DIP) }

    fun setActionSpace(space: Float?, unit: SizeUnit) = also {
        actionLayout.setActionSpace(space, unit)
        padding.value = padding.value!!
        titleCenter.value = titleCenter.value
    }

    fun setActionText(key: Int?, @StringRes resId: Int) = also { actionLayout.setActionText(key, resId) }

    fun setActionText(key: Int?, text: CharSequence?) = also { actionLayout.setActionText(key, text) }

    fun setActionTextColor(@ColorInt color: Int?) = also { actionLayout.setActionTextColor(color) }

    fun setActionTextColor(key: Int?, @ColorInt color: Int?) = also { actionLayout.setActionTextColor(key, color) }

    fun setActionTextSize(size: Float?) = also { actionLayout.setActionTextSize(size) }

    fun setActionTextSize(size: Float?, unit: SizeUnit) = also { actionLayout.setActionTextSize(size, unit) }

    fun setActionTextSize(key: Int?, size: Float?) = also { actionLayout.setActionTextSize(key, size) }

    fun setActionTextSize(key: Int?, size: Float?, unit: SizeUnit) = also {
        actionLayout.setActionTextSize(key, size, unit)
    }

    fun setActionTextTypeface(typeface: Typeface?) = also { actionLayout.setActionTextTypeface(typeface) }

    fun setActionTextTypeface(key: Int?, typeface: Typeface?) = also {
        actionLayout.setActionTextTypeface(key, typeface)
    }

    fun setActionIcon(key: Int?, @DrawableRes resId: Int?) = also { actionLayout.setActionIcon(key, resId) }

    fun setActionIcon(key: Int?, @DrawableRes resId: Int?, @ColorInt tintColor: Int?) = also {
        actionLayout.setActionIcon(key, resId, tintColor)
    }

    fun setActionIcon(key: Int?, drawable: Drawable?) = also { actionLayout.setActionIcon(key, drawable) }

    fun setActionIcon(key: Int?, drawable: Drawable?, @ColorInt tintColor: Int?) = also {
        actionLayout.setActionIcon(key, drawable, tintColor)
    }

    fun setActionIconTintColor(@ColorInt color: Int) = also { actionLayout.setActionIconTintColor(color) }

    fun setActionIconTintColor(key: Int?, @ColorInt tintColor: Int) = also {
        actionLayout.setActionIconTintColor(key, tintColor)
    }

    fun setActionListener(key: Int?, listener: OnClickListener?) = also {
        actionLayout.setActionListener(key, listener)
    }

    //============================================================//
    //=====================ACTION部分 END=========================//
    //===================DIVIDER部分 START=========================//
    //============================================================//

    fun setDividerColor(@ColorInt color: Int) = also {
        dividerView.setBackgroundColor(color)
    }

    fun setDividerHeight(height: Float) = also {
        return setDividerHeight(height, SizeUnit.DIP)
    }

    fun setDividerHeight(height: Float, unit: SizeUnit) = also {
        dividerView.layoutParams.height = Sizes.applyDimension(height, unit).toInt()
        dividerView.requestLayout()
    }

    fun setDividerMargin(margin: Float) = also {
        return setDividerMargin(margin, SizeUnit.DIP)
    }

    fun setDividerMargin(margin: Float, unit: SizeUnit) = also {
        val m = Sizes.applyDimension(margin, unit).toInt()
        val params = dividerView.layoutParams as LayoutParams
        params.setMargins(m, 0, m, 0)
        dividerView.requestLayout()
    }

    fun setDividerVisible(visible: Boolean) = also {
        dividerView.visibility = if (visible) VISIBLE else INVISIBLE
    }

    private fun createMaterialShapeDrawableBackground(background: Drawable): Drawable {
        return if (background is ColorDrawable) {
            val materialShapeDrawable = MaterialShapeDrawable()
            materialShapeDrawable.fillColor = ColorStateList.valueOf(background.color)
            materialShapeDrawable.initializeElevationOverlay(context)
            materialShapeDrawable
        } else {
            background
        }
    }
}