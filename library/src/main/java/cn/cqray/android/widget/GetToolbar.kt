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
class GetToolbar @JvmOverloads constructor(
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
    val backView: GetBackView by lazy {
        GetBackView(context).also { view ->
            view.id = R.id.get_toolbar_back_view
            view.layoutParams = LayoutParams(-2, -1).also { it.addRule(ALIGN_PARENT_START) }
        }
    }

    /** 行为组件容器 **/
    val actionLayout: GetActionLayout by lazy {
        GetActionLayout(context).also { view ->
            view.id = R.id.get_toolbar_action_layout
            view.layoutParams = LayoutParams(-2, -1).also { it.addRule(ALIGN_PARENT_END) }
        }
    }

    /** 分割控件 **/
    val dividerView: View by lazy {
        View(context).also {
            it.layoutParams = LayoutParams(-1, Sizes.px(R.dimen.divider).toInt())
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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetToolbar)
        val elevation = ta.getDimension(R.styleable.GetToolbar_elevation, elev)
        val editable = ta.getBoolean(R.styleable.GetToolbar_titleEditable, false)
        // 默认属性
        val paddingSE = ta.getDimension(R.styleable.GetToolbar_paddingSE, Sizes.px(R.dimen.content).toFloat())
        val titleSpace = ta.getDimension(R.styleable.GetToolbar_titleSpace, Sizes.px(R.dimen.content).toFloat())
        val titleCenter = ta.getBoolean(R.styleable.GetToolbar_titleCenter, false)
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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetToolbar)
        val ripple = ta.getBoolean(R.styleable.GetToolbar_backRipple, true)
        val drawable = ta.getDrawable(R.styleable.GetToolbar_backIcon)
        val visible = ta.getBoolean(R.styleable.GetToolbar_backIconVisible, true)
        val text = ta.getString(R.styleable.GetToolbar_backText)
        val textColor = ta.getColor(R.styleable.GetToolbar_backTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.GetToolbar_backTextSize, Sizes.px(R.dimen.h3).toFloat())
        val textStyle = ta.getInt(R.styleable.GetToolbar_backTextStyle, 0)
        ta.recycle()
        // 设置Back布局
        backView.setRipple(ripple)
        backView.setText(text)
        backView.setTextColor(textColor)
        backView.setTextSize(textSize, COMPLEX_UNIT_PX)
        backView.setTextStyle(textStyle)
        backView.iconView.visibility = if (visible) VISIBLE else GONE
        // 设置图标
        if (drawable == null) backView.setIconResource(R.drawable.def_back_material_light)
        else backView.setIconDrawable(drawable)
        // 添加到容器
        addView(backView)
    }

    /**
     * 初始化行为控件
     */
    private fun initToolbarAction(attrs: AttributeSet?) {
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetToolbar)
        val space = ta.getDimension(R.styleable.GetToolbar_actionSpace, Sizes.px(R.dimen.content).toFloat())
        val textColor = ta.getColor(R.styleable.GetToolbar_actionTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.GetToolbar_actionTextSize, Sizes.px(R.dimen.h3).toFloat())
        val textStyle = ta.getInt(R.styleable.GetToolbar_actionTextStyle, 0)
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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetToolbar)
        val text = ta.getString(R.styleable.GetToolbar_titleText)
        val textColor = ta.getColor(R.styleable.GetToolbar_titleTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.GetToolbar_titleTextSize, Sizes.px(R.dimen.h2).toFloat())
        val textStyle = ta.getInt(R.styleable.GetToolbar_titleTextStyle, 0)
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
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetToolbar)
        val def = ContextCompat.getColor(context, R.color.divider)
        val color = ta.getColor(R.styleable.GetToolbar_dividerColor, def)
        val height = ta.getDimensionPixelSize(R.styleable.GetToolbar_dividerHeight, 0)
        val margin = ta.getDimensionPixelSize(R.styleable.GetToolbar_dividerMargin, 0)
        val visible = ta.getBoolean(R.styleable.GetToolbar_dividerVisible, true)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(elevation)
        }
        runCatching { MaterialShapeUtils.setElevation(this, elevation) }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        paddingSeLD.value = intArrayOf(left, right)
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        paddingSeLD.value = intArrayOf(start, end)
    }

    fun setPaddingSE(padding: Float) = setPaddingSE(padding, COMPLEX_UNIT_DIP)

    fun setPaddingSE(padding: Float, unit: Int) = also {
        val newPadding = Sizes.applyDimension(padding, unit)
        paddingSeLD.setValue(IntArray(2) { newPadding.toInt() })
    }

    fun setPaddingSE(start: Float, end: Float) = setPaddingSE(start, end, COMPLEX_UNIT_DIP)

    fun setPaddingSE(start: Float, end: Float, unit: Int) = also {
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

    fun setBackIcon(@DrawableRes resId: Int) = also { backView.setIconResource(resId) }

    fun setBackIcon(drawable: Drawable?) = also { backView.setIconDrawable(drawable) }

    fun setBackIcon(bitmap: Bitmap?) = also { backView.setIconBitmap(bitmap) }

    fun setBackIconTintColor(@ColorInt color: Int?) = also { backView.setIconTintColor(color) }

    fun setBackIconSpace(space: Float) = also { backView.setIconSpace(space) }

    fun setBackIconSpace(space: Float, unit: Int) = also { backView.setIconSpace(space, unit) }

    fun setBackText(@StringRes id: Int) = also { backView.setText(id) }

    fun setBackText(text: CharSequence?) = also { backView.setText(text) }

    fun setBackTextColor(@ColorInt color: Int) = also { backView.setTextColor(color) }

    fun setBackTextSize(size: Float) = also { backView.setTextSize(size) }

    fun setBackTextSize(size: Float, unit: Int) = also { backView.setTextSize(size, unit) }

    fun setBackTextStyle(textStyle: Int) = also { backView.setTextStyle(textStyle) }

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

    fun setTitleTextSize(size: Float) = also { setTitleTextSize(size, COMPLEX_UNIT_SP) }

    fun setTitleTextSize(size: Float, unit: Int) = also {
        val newSize = Sizes.applyDimension(size, unit)
        titleView.setTextSize(COMPLEX_UNIT_PX, newSize)
    }

    fun setTitleTextStyle(textStyle: Int) = also { titleView.typeface = Typeface.defaultFromStyle(textStyle) }

    fun setTitleCenter(center: Boolean) = also { titleCenterLD.value = center }

    fun setTitleEditable(editable: Boolean) = also { titleEditableLD.value = editable }

    fun setTitleSpace(space: Float) = also { setTitleSpace(space, COMPLEX_UNIT_DIP) }

    fun setTitleSpace(space: Float, unit: Int) = also {
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

    fun setActionSpace(space: Float, unit: Int) = also {
        actionLayout.setDefaultSpace(space, unit)
        paddingSeLD.value = paddingSeLD.value!!
        actionLayout.post { titleSpaceLD.value = titleSpaceLD.value!! }
    }

    fun setActionText(key: Int?, @StringRes id: Int) = also { actionLayout.setText(key, id) }

    fun setActionText(key: Int?, text: CharSequence?) = also { actionLayout.setText(key, text) }

    fun setActionTextColor(@ColorInt color: Int) = also { actionLayout.setDefaultTextColor(color) }

    fun setActionTextColor(key: Int?, @ColorInt color: Int) = also { actionLayout.setTextColor(key, color) }

    fun setActionTextSize(size: Float) = also { actionLayout.setDefaultTextSize(size) }

    fun setActionTextSize(size: Float, unit: Int) = also { actionLayout.setDefaultTextSize(size, unit) }

    fun setActionTextSize(key: Int?, size: Float) = also { actionLayout.setTextSize(key, size) }

    fun setActionTextSize(key: Int?, size: Float, unit: Int) = also {
        actionLayout.setTextSize(key, size, unit)
    }

    fun setActionTextStyle(textStyle: Int) = also { actionLayout.setDefaultTextStyle(textStyle) }

    fun setActionTextStyle(key: Int?, textStyle: Int) = also { actionLayout.setTextStyle(key, textStyle) }

    fun setActionIcon(key: Int?, @DrawableRes id: Int) = also { actionLayout.setIcon(key, id) }

    fun setActionIcon(key: Int?, @DrawableRes id: Int, @ColorInt tintColor: Int?) = also {
        actionLayout.setIcon(key, id, tintColor)
    }

    fun setActionIcon(key: Int?, drawable: Drawable?) = also { actionLayout.setIcon(key, drawable) }

    fun setActionIcon(key: Int?, drawable: Drawable?, @ColorInt tintColor: Int?) = also {
        actionLayout.setIcon(key, drawable, tintColor)
    }

    fun setActionIconTintColor(@ColorInt color: Int?) = also { actionLayout.setDefaultIconTintColor(color) }

    fun setActionIconTintColor(key: Int?, @ColorInt color: Int?) = also { actionLayout.setIconTintColor(key, color) }

    fun setActionListener(key: Int?, listener: OnClickListener?) = also { actionLayout.setListener(key, listener) }

    //============================================================//
    //=====================ACTION部分 END=========================//
    //===================DIVIDER部分 START=========================//
    //============================================================//

    fun setDividerColor(@ColorInt color: Int) = also { dividerView.setBackgroundColor(color) }

    fun setDividerDrawable(drawable: Drawable?) = also { dividerView.background = drawable }

    fun setDividerHeight(height: Float) = also { return setDividerHeight(height, COMPLEX_UNIT_DIP) }

    fun setDividerHeight(height: Float, unit: Int) = also {
        dividerView.layoutParams.height = Sizes.applyDimension(height, unit).toInt()
        dividerView.requestLayout()
    }

    fun setDividerMargin(margin: Float) = also { setDividerMargin(margin, COMPLEX_UNIT_DIP) }

    fun setDividerMargin(margin: Float, unit: Int) = also {
        val newMargin = Sizes.applyDimension(margin, unit).toInt()
        val params = dividerView.layoutParams as LayoutParams
        params.setMargins(newMargin, 0, newMargin, 0)
        dividerView.requestLayout()
    }

    fun setDividerVisible(visible: Boolean) = also { dividerView.visibility = if (visible) VISIBLE else GONE }

    //============================================================//
    //=====================DIVIDER部分 END========================//
    //============================================================//

    fun setToolbarInit(init: GetToolbarInit) {
        // 基础属性
        init.paddingSE?.let { setPaddingSE(it) }
        init.height?.let { layoutParams.height = Sizes.applyDimension(it, COMPLEX_UNIT_DIP).toInt() }
        elevation = init.elevation ?: Sizes.dp(R.dimen.elevation)
        background = init.background?.invoke() ?: background
        // 回退按钮部分
        init.backRipple?.let { setBackRipple(it) }
        init.backIcon?.let { setBackIcon(it) }
        init.backIconSpace?.let { setBackIconSpace(it) }
        init.backTextColor?.let { setBackTextColor(it) }
        init.backTextSize?.let { setBackTextSize(it) }
        init.backTextStyle?.let { setActionTextStyle(it) }
        setBackText(init.backText)
        setBackIconTintColor(init.backIconTintColor)
        // 标题部分
        init.titleCenter?.let { setTitleCenter(it) }
        init.titleSpace?.let { setTitleSpace(it) }
        init.titleTextColor?.let { setTitleTextColor(it) }
        init.titleTextSize?.let { setTitleTextSize(it) }
        init.titleTextStyle?.let { setTitleTextStyle(it) }
        // Action部分
        init.actionRipple?.let { setActionRipple(it) }
        init.actionSpace?.let { setActionSpace(it) }
        init.actionTextColor?.let { setActionTextColor(it) }
        init.actionTextSize?.let { setActionTextSize(it) }
        init.actionTextStyle?.let { setActionTextStyle(it) }
        // 分割线部分
        init.dividerHeight?.let { setDividerHeight(it) }
        init.dividerMargin?.let { setDividerMargin(it) }
        init.dividerVisible?.let { setDividerVisible(it) }
        setDividerDrawable(init.dividerDrawable)
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