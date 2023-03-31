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
import cn.cqray.android.util.ContextUtils
import cn.cqray.android.util.Sizes
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.MaterialShapeUtils
import kotlin.math.max

/**
 * 标题栏
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused",
)
class Toolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    /** 标题控件  */
    val titleView: TextView by lazy {
        AppCompatEditText(context).also {
            it.id = R.id.get_toolbar_title
            it.layoutParams = LayoutParams(-1, -1)
            it.setPadding(0, 0, 0, 0)
        }
    }

    /** 回退控件 **/
    val backView: BackView by lazy {
        BackView(context).also { view ->
            view.id = R.id.get_toolbar_back_view
            view.layoutParams = LayoutParams(-2, -1).also { it.addRule(ALIGN_PARENT_START) }
        }
    }

    /** 行为组件容器 **/
    val actionLayout: ActionLayout by lazy {
        ActionLayout(context).also { view ->
            view.id = R.id.get_toolbar_action_layout
            view.layoutParams = LayoutParams(-2, -1).also { it.addRule(ALIGN_PARENT_END) }
        }
    }

    /** 分割控件 **/
    val dividerView: View by lazy {
        View(context).also {
            it.layoutParams = LayoutParams(-1, Sizes.px(R.dimen.divider))
        }
    }

    /** 内容间隔 **/
    private val paddingSeLD = MutableLiveData<IntArray>()

    /** 标题居中 **/
    private val titleCenterLD = MutableLiveData<Boolean>()

    /** 标题可编辑 **/
    private val titleEditableLD = MutableLiveData<Boolean>()

    /** 标题左右间隔 **/
    private val titleSpaceLD = MutableLiveData<Int>()

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
        initTitleSpaceLD()
        initTitleCenterLD()
        initTitleEditableLD()
        initPaddingSeLD()
        setTitleCenter(false)
        // 设置内部间隔为 0
        super.setPadding(0, 0, 0, 0)
        super.setPaddingRelative(0, 0, 0, 0)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        runCatching { MaterialShapeUtils.setParentAbsoluteElevation(this) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    /**
     * 初始化基础控价
     */
    private fun initToolbarBasic(attrs: AttributeSet?) {
        val elev = Sizes.px(R.dimen.elevation).toFloat()
        // 获取默认属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val elevation = ta.getDimension(R.styleable.Toolbar_elevation, elev)
        val editable = ta.getBoolean(R.styleable.Toolbar_titleEditable, false)
        // 默认属性
        val paddingSE = ta.getDimension(R.styleable.Toolbar_paddingSE, Sizes.px(R.dimen.content).toFloat())
        val titleSpace = ta.getDimension(R.styleable.Toolbar_titleSpace, Sizes.px(R.dimen.content).toFloat())
        val titleCenter = ta.getBoolean(R.styleable.Toolbar_titleCenter, false)
        ta.recycle()
        // 设置标题栏背景
        val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val background = background ?: ColorDrawable(primaryColor)
        ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(background))
        // 其他属性
        // 最先更新是否使用水波纹、标题位置及是否可编辑，因为它们不涉及数值计算
        titleCenterLD.value = titleCenter
        titleEditableLD.value = editable
        // 再更新标题左右间隔，因为它涉及的计算单一
        paddingSeLD.value = IntArray(2) { paddingSE.toInt() }
        // 最后更新标题内容左右空间，因为它涉及的计算最复杂
        titleSpaceLD.postValue(titleSpace.toInt())
        // 设置阴影大小
        setElevation(elevation)
    }

    /**
     * 初始化回退控件
     */
    private fun initToolbarBack(attrs: AttributeSet?) {
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val ripple = ta.getBoolean(R.styleable.Toolbar_backRipple, true)
        val drawable = ta.getDrawable(R.styleable.Toolbar_backIcon)
        val visible = ta.getBoolean(R.styleable.Toolbar_backIconVisible, true)
        val text = ta.getString(R.styleable.Toolbar_backText)
        val textColor = ta.getColor(R.styleable.Toolbar_backTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.Toolbar_backTextSize, Sizes.px(R.dimen.h3).toFloat())
        val textStyle = ta.getInt(R.styleable.Toolbar_backTextStyle, 0)
        ta.recycle()
        // 设置Back布局
        backView.setRipple(ripple)
        backView.setText(text)
        backView.setTextColor(textColor)
        backView.setTextSize(textSize, COMPLEX_UNIT_PX)
        backView.setTextStyle(textStyle)
        backView.iconView.visibility = if (visible) VISIBLE else GONE
        // 设置图标
        if (drawable == null) backView.setIcon(R.drawable.def_back_material_light)
        else backView.setIcon(drawable)
        // 添加到容器
        addView(backView)
    }

    /**
     * 初始化行为控件
     */
    private fun initToolbarAction(attrs: AttributeSet?) {
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val space = ta.getDimension(R.styleable.Toolbar_actionSpace, Sizes.px(R.dimen.content).toFloat())
        val textColor = ta.getColor(R.styleable.Toolbar_actionTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.Toolbar_actionTextSize, Sizes.px(R.dimen.h3).toFloat())
        val textStyle = ta.getInt(R.styleable.Toolbar_actionTextStyle, 0)
        ta.recycle()
        // 设置ActionLayout属性
        actionLayout.setDefaultSpace(space, COMPLEX_UNIT_PX)
        actionLayout.setDefaultTextColor(textColor)
        actionLayout.setDefaultTextSize(textSize, COMPLEX_UNIT_PX)
        actionLayout.setDefaultTextStyle(textStyle)
        // 添加到容器
        addView(actionLayout)
    }

    /**
     * 初始化标题文本
     */
    private fun initToolbarTittle(attrs: AttributeSet?) {
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val text = ta.getString(R.styleable.Toolbar_titleText)
        val textColor = ta.getColor(R.styleable.Toolbar_titleTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.Toolbar_titleTextSize, Sizes.px(R.dimen.h2).toFloat())
        val textStyle = ta.getInt(R.styleable.Toolbar_titleTextStyle, 0)
        ta.recycle()
        // 设置标题
        titleView.text = text
        titleView.setTextColor(textColor)
        titleView.setTextSize(COMPLEX_UNIT_PX, textSize)
        titleView.typeface = Typeface.defaultFromStyle(textStyle)
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

    //============================================================//
    //====================控件初始化部分 END========================//
    //==================LiveData初始化部分 START===================//
    //============================================================//

    private fun initTitleSpaceLD() {
        titleSpaceLD.observe(lifecycleOwner) { space ->
            // 标题位置
            val center = titleCenterLD.value ?: false
            // 更新标题间隔参数
            val tParams = titleView.layoutParams as LayoutParams
            // Action偏移量
            val offset = space - actionLayout.defaultSpace.toInt()
            // 新的间隔
            val newSpace = when (center) {
                false -> space
                else -> {
                    // 判断布局方向
                    val direction = resources.configuration.layoutDirection
                    val ltr = direction == View.LAYOUT_DIRECTION_LTR
                    // ActionLayout使用的宽度
                    val actionUsedWidth =
                        // 从左往右布局
                        if (ltr) width - actionLayout.left + offset
                        // 从右往左布局
                        else actionLayout.right + offset
                    // BackView使用的宽度
                    val backUsedWidth =
                        // 从左往右布局
                        if (ltr) backView.right
                        // 从右往左布局
                        else backView.left
                    // 返回较大的值
                    max(actionUsedWidth, backUsedWidth)
                }
            }
            // 设置Title间隔信息
            titleView.layoutParams = tParams.also {
                it.marginEnd = newSpace
                it.marginStart = newSpace
            }
            // 更新Action间隔参数
            val aParams = actionLayout.layoutParams as LayoutParams
            actionLayout.layoutParams = aParams.also { it.marginStart = offset - newSpace }
            // Title移动至最前方
            titleView.bringToFront()
        }
    }

    private fun initTitleCenterLD() {
        // 标题居中监听
        titleCenterLD.observe(lifecycleOwner) {
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
    }

    private fun initTitleEditableLD() {
        // 监听设置标题栏是否可编辑
        titleEditableLD.observe(lifecycleOwner) {
            titleView.isFocusableInTouchMode = it
            titleView.isClickable = it
            titleView.isFocusable = it
            titleView.isEnabled = it
            if (it) {
                val ta = context.obtainStyledAttributes(intArrayOf(android.R.attr.editTextBackground))
                ViewCompat.setBackground(titleView, ta.getDrawable(0))
                ta.recycle()
            } else {
                ViewCompat.setBackground(titleView, null)
            }
            backView.bringToFront()
            actionLayout.bringToFront()
        }
    }

    private fun initPaddingSeLD() {
        // 间隔大小监听
        paddingSeLD.observe(lifecycleOwner) {
            val start = it[0]
            val end = it[1]
            // 设置BackLayout内部间隔
            val backVisible = backView.visibility == VISIBLE
            if (backVisible) {
                backView.setPadding(start / 2, 0, start / 2, 0)
                val bParams = backView.layoutParams as LayoutParams
                bParams.marginEnd = -start / 2
                bParams.marginStart = start / 2
            }
            // 设置ActionLayout右部间隔
            val params = actionLayout.layoutParams as LayoutParams
            params.marginEnd = (end - actionLayout.defaultSpace).toInt()
        }
    }

    //============================================================//
    //=================LiveData初始化部分 END=======================//
    //===================其他设置部分 START=========================//
    //============================================================//

    override fun setElevation(elevation: Float) {
        val newElevation = Sizes.dp2px(elevation).toFloat()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(newElevation)
        }
        runCatching { MaterialShapeUtils.setElevation(this, newElevation) }
    }

    @JvmOverloads
    fun setElevation(
        elevation: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { setElevation(Sizes.any2dp(elevation, unit)) }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        paddingSeLD.value = intArrayOf(left, right)
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        paddingSeLD.value = intArrayOf(start, end)
    }

    @JvmOverloads
    fun setPaddingSE(padding: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        val newPadding = Sizes.applyDimension(padding, unit)
        paddingSeLD.setValue(IntArray(2) { newPadding.toInt() })
    }

    @JvmOverloads
    fun setPaddingSE(start: Number, end: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        val newStart = Sizes.applyDimension(start, unit)
        val newEnd = Sizes.applyDimension(end, unit)
        paddingSeLD.setValue(intArrayOf(newStart.toInt(), newEnd.toInt()))
    }

    //============================================================//
    //==================其他设置部分部分 END========================//
    //=====================BACK部分 START=========================//
    //============================================================//

    fun setBackVisible(visible: Boolean) = also {
        if (visible != (backView.visibility == VISIBLE)) {
            backView.visibility = if (visible) VISIBLE else GONE
            paddingSeLD.value = paddingSeLD.value!!
        }
    }

    fun setBackRipple(ripple: Boolean) = also { backView.setRipple(ripple) }

    fun setBackIcon(@DrawableRes id: Int) = also { backView.setIcon(id) }

    fun setBackIcon(drawable: Drawable?) = also { backView.setIcon(drawable) }

    fun setBackIcon(bitmap: Bitmap?) = also { backView.setIcon(bitmap) }

    fun setBackIconTintColor(@ColorInt color: Int?) = also { backView.setIconTintColor(color) }

    @JvmOverloads
    fun setBackIconSpace(space: Number, unit: Int = COMPLEX_UNIT_DIP) = also { backView.setIconSpace(space, unit) }

    fun setBackText(@StringRes id: Int) = also { backView.setText(id) }

    fun setBackText(text: CharSequence?) = also { backView.setText(text) }

    fun setBackTextColor(@ColorInt color: Int) = also { backView.setTextColor(color) }

    @JvmOverloads
    fun setBackTextSize(size: Number, unit: Int = COMPLEX_UNIT_DIP) = also { backView.setTextSize(size, unit) }

    fun setBackTextStyle(style: Int) = also { backView.setTextStyle(style) }

    fun setBackListener(listener: OnClickListener?) = also { backView.setOnClickListener(listener) }

    //============================================================//
    //======================BACK部分 END==========================//
    //=====================TITLE部分 START=========================//
    //============================================================//

    fun setTitle(@StringRes id: Int?) = also {
        // 无ID资源
        if (id == null) titleView.text = null
        // 有ID资源
        else titleView.setText(id)
    }

    fun setTitle(text: CharSequence?) = also { titleView.text = text }

    fun setTitleTextColor(@ColorInt color: Int) = also { titleView.setTextColor(color) }

    @JvmOverloads
    fun setTitleTextSize(size: Number, unit: Int = COMPLEX_UNIT_SP) = also {
        val newSize = Sizes.applyDimension(size, unit)
        titleView.setTextSize(COMPLEX_UNIT_PX, newSize)
    }

    fun setTitleTextStyle(style: Int) = also { titleView.typeface = Typeface.defaultFromStyle(style) }

    fun setTitleCenter(center: Boolean) = also { titleCenterLD.value = center }

    fun setTitleEditable(editable: Boolean) = also { titleEditableLD.value = editable }

    @JvmOverloads
    fun setTitleSpace(space: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        val newSpace = Sizes.applyDimension(space, unit)
        titleSpaceLD.postValue(newSpace.toInt())
    }

    //============================================================//
    //======================TITLE部分 END=========================//
    //====================ACTION部分 START========================//
    //============================================================//

    fun setActionRipple(ripple: Boolean) = also { actionLayout.setDefaultRipple(ripple) }

    fun setActionRipple(key: Int?, ripple: Boolean) = also { actionLayout.setRipple(key, ripple) }

    fun setActionVisible(visible: Boolean) = also { actionLayout.setDefaultVisible(visible) }

    fun setActionVisible(key: Int?, visible: Boolean) = also { actionLayout.setVisible(key, visible) }

    fun setActionSpace(space: Float) = also { setActionSpace(space, COMPLEX_UNIT_DIP) }

    @JvmOverloads
    fun setActionSpace(space: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        actionLayout.setDefaultSpace(space, unit)
        paddingSeLD.value = paddingSeLD.value!!
        actionLayout.post { titleSpaceLD.value = titleSpaceLD.value!! }
    }

    fun setActionText(key: Int?, @StringRes id: Int) = also { actionLayout.setText(key, id) }

    fun setActionText(key: Int?, text: CharSequence?) = also { actionLayout.setText(key, text) }

    fun setActionTextColor(@ColorInt color: Int) = also { actionLayout.setDefaultTextColor(color) }

    fun setActionTextColor(key: Int?, @ColorInt color: Int) = also { actionLayout.setTextColor(key, color) }

    @JvmOverloads
    fun setActionTextSize(
        size: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { actionLayout.setDefaultTextSize(size, unit) }

    @JvmOverloads
    fun setActionTextSize(
        key: Int?,
        size: Number,
        unit: Int = COMPLEX_UNIT_DIP
    ) = also { actionLayout.setTextSize(key, size, unit) }

    fun setActionTextStyle(style: Int) = also { actionLayout.setDefaultTextStyle(style) }

    fun setActionTextStyle(key: Int?, style: Int) = also { actionLayout.setTextStyle(key, style) }

    @JvmOverloads
    fun setActionIcon(
        key: Int?,
        @DrawableRes id: Int,
        @ColorInt tintColor: Int? = null
    ) = also { actionLayout.setIcon(key, id, tintColor) }

    @JvmOverloads
    fun setActionIcon(
        key: Int?,
        drawable: Drawable?,
        @ColorInt tintColor: Int? = null
    ) = also { actionLayout.setIcon(key, drawable, tintColor) }

    fun setActionIconTintColor(@ColorInt color: Int?) = also { actionLayout.setDefaultIconTintColor(color) }

    fun setActionIconTintColor(key: Int?, @ColorInt color: Int?) = also { actionLayout.setIconTintColor(key, color) }

    fun setActionListener(key: Int?, listener: OnClickListener?) = also { actionLayout.setListener(key, listener) }

    //============================================================//
    //=====================ACTION部分 END=========================//
    //===================DIVIDER部分 START=========================//
    //============================================================//

    fun setDividerColor(@ColorInt color: Int) = also { dividerView.setBackgroundColor(color) }

    fun setDividerDrawable(drawable: Drawable?) = also { dividerView.background = drawable }

    @JvmOverloads
    fun setDividerHeight(height: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        dividerView.layoutParams.height = Sizes.applyDimension(height, unit).toInt()
        dividerView.requestLayout()
    }

    @JvmOverloads
    fun setDividerMargin(margin: Number, unit: Int = COMPLEX_UNIT_DIP) = also {
        val newMargin = Sizes.applyDimension(margin, unit).toInt()
        val params = dividerView.layoutParams as LayoutParams
        params.setMargins(newMargin, 0, newMargin, 0)
        dividerView.requestLayout()
    }

    fun setDividerVisible(visible: Boolean) = also { dividerView.visibility = if (visible) VISIBLE else GONE }

    //============================================================//
    //=====================DIVIDER部分 END========================//
    //============================================================//

    fun setToolbarInit(init: ToolbarInit) {
        // 加载本地缓存数据
        init.loadFromLocal()
        // 基础属性
        setPaddingSE(init.paddingSE)
        layoutParams.height = Sizes.any2px(init.height, COMPLEX_UNIT_DIP)
        elevation = init.elevation.toFloat()
        // 背景
        if (init.backgroundResource != null) setBackgroundResource(init.backgroundResource!!)
        else if (init.backgroundColor != null) setBackgroundColor(init.backgroundColor!!)
        // 回退按钮部分
        init.backIcon?.let { setBackIcon(it) }
        setBackRipple(init.backRipple)
        setBackIconSpace(init.backIconSpace)
        setBackTextColor(init.backTextColor)
        setBackTextSize(init.backTextSize)
        setActionTextStyle(init.backTextStyle)
        setBackText(init.backText)
        setBackIconTintColor(init.backIconTintColor)
        // 标题部分
        setTitleCenter(init.titleCenter)
        setTitleSpace(init.titleSpace)
        setTitleTextColor(init.titleTextColor)
        setTitleTextSize(init.titleTextSize)
        setTitleTextStyle(init.titleTextStyle)
        // Action部分
        setActionRipple(init.actionRipple)
        setActionSpace(init.actionSpace)
        setActionTextColor(init.actionTextColor)
        setActionTextSize(init.actionTextSize)
        setActionTextStyle(init.actionTextStyle)
        // 分割线部分
        setDividerHeight(init.dividerHeight)
        setDividerMargin(init.dividerMargin)
        setDividerVisible(init.dividerVisible)
        // 分割线背景
        if (init.dividerResource != null) setDividerDrawable(ContextUtils.getDrawable(init.dividerResource!!))
        else if (init.dividerColor != null) setDividerColor(init.dividerColor!!)
        else setDividerDrawable(null)
    }

    private fun createMaterialShapeDrawableBackground(background: Drawable): Drawable {
        var newBackground: Drawable = background
        if (background is ColorDrawable) {
            runCatching {
                val materialShapeDrawable = MaterialShapeDrawable()
                materialShapeDrawable.fillColor = ColorStateList.valueOf(background.color)
                materialShapeDrawable.initializeElevationOverlay(context)
                newBackground = materialShapeDrawable
            }
        }
        return newBackground
    }
}