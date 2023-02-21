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
import cn.cqray.java.tool.SizeUnit
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
    val dividerView: View by lazy { View(getContext()) }

    /** 内容间隔 **/
    private val contentPadding = MutableLiveData<Int>()

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


    /** 默认参数，主要是对应值为空时，赋值 **/
    private val defaults: HashMap<Int, Any?> by lazy {
        val map = HashMap<Int, Any?>()
        map[RIPPLE] = true
        map[BACK_VISIBLE] = true
        map[CONTENT_PADDING] = Sizes.content()
        map[TITLE_CENTER] = false
        map[TITLE_SPACE] = Sizes.content()
        map[TITLE_TEXT_COLOR] = ContextCompat.getColor(context, R.color.foreground)
        map[TITLE_TEXT_SIZE] = Sizes.h2()
        map[TITLE_TEXT_STYLE] = Typeface.BOLD
        map
    }

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
        initTitleSpaceData()
        initTitleCenterLiveData()
        initTitleEditableLiveData()
        initContentPaddingLiveData()
        setTitleCenter(false)
    }

    /** 默认是否显示水波纹 **/
    private val defaultRipple get() = defaults[RIPPLE] as Boolean

    /** 默认是否显示回退组件 **/
    private val defaultBackVisible get() = defaults[BACK_VISIBLE] as Boolean

    /** 默认标题内容间隔 **/
    private val defaultContentPadding get() = defaults[CONTENT_PADDING] as Float

    /** 默认Title间隔 **/
    private val defaultTitleSpace get() = defaults[TITLE_SPACE] as Float

    /** 默认标题间隔 **/
    private val defaultTitleCenter get() = defaults[TITLE_CENTER] as Boolean

    /** 默认标题文本颜色 **/
    private val defaultTitleTextColor get() = defaults[TITLE_TEXT_COLOR] as Int

    /** 默认标题文本大小 **/
    private val defaultTitleTextSize get() = defaults[TITLE_TEXT_SIZE] as Float

    /** 默认标题文本样式 **/
    private val defaultTitleTextStyle get() = defaults[TITLE_TEXT_STYLE] as Int

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
        val elev = Sizes.px(R.dimen.elevation)
        // 获取默认属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.GetToolbar)
        val elevation = ta.getDimension(R.styleable.GetToolbar_elevation, elev)
        val editable = ta.getBoolean(R.styleable.GetToolbar_titleEditable, false)
        // 默认属性
        defaults[CONTENT_PADDING] = ta.getDimension(R.styleable.GetToolbar_contentPadding, defaultContentPadding)
        defaults[TITLE_SPACE] = ta.getDimension(R.styleable.GetToolbar_titleSpace, defaultTitleSpace)
        defaults[TITLE_CENTER] = ta.getBoolean(R.styleable.GetToolbar_titleCenter, defaultTitleCenter)
        ta.recycle()
        // 设置标题栏背景
        val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val background = background ?: ColorDrawable(primaryColor)
        ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(background))
        // 其他属性
        // 最先更新是否使用水波纹、标题位置及是否可编辑，因为它们不涉及数值计算
        titleCenter.value = defaultTitleCenter
        titleEditable.value = editable
        // 再更新标题左右间隔，因为它涉及的计算单一
        contentPadding.value = defaultContentPadding.toInt()
        // 最后更新标题内容左右空间，因为它涉及的计算最复杂
        titleSpace.postValue(defaultTitleSpace.toInt())
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
        val visible = ta.getBoolean(R.styleable.GetToolbar_backIconVisible, defaultBackVisible)
        val text = ta.getString(R.styleable.GetToolbar_backText)
        val textColor = ta.getColor(R.styleable.GetToolbar_backTextColor, defaultTitleTextColor)
        val textSize = ta.getDimension(R.styleable.GetToolbar_backTextSize, Sizes.h3())
        val textStyle = ta.getInt(R.styleable.GetToolbar_backTextStyle, 0)
        ta.recycle()
        // 设置Back布局
        backView.setRipple(ripple)
        backView.setText(text)
        backView.setTextColor(textColor)
        backView.setTextSize(textSize, SizeUnit.PX)
        backView.setTypeface(Typeface.defaultFromStyle(textStyle))
        backView.iconView.visibility = if (visible) VISIBLE else GONE
        defaults[BACK_VISIBLE] = visible
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
        val space = ta.getDimension(R.styleable.GetToolbar_actionSpace, Sizes.content())
        val textColor = ta.getColor(R.styleable.GetToolbar_actionTextColor, Color.WHITE)
        val textSize = ta.getDimension(R.styleable.GetToolbar_actionTextSize, Sizes.h3())
        val textStyle = ta.getInt(R.styleable.GetToolbar_actionTextStyle, 0)
        ta.recycle()
        // 设置ActionLayout属性
        actionLayout.setSpace(space, SizeUnit.PX)
        actionLayout.setTextColor(textColor)
        actionLayout.setTextSize(textSize, SizeUnit.PX)
        actionLayout.setTextTypeface(Typeface.defaultFromStyle(textStyle))
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
        defaults[TITLE_TEXT_COLOR] = ta.getColor(R.styleable.GetToolbar_titleTextColor, defaultTitleTextColor)
        defaults[TITLE_TEXT_SIZE] = ta.getDimension(R.styleable.GetToolbar_titleTextSize, defaultTitleTextSize)
        defaults[TITLE_TEXT_STYLE] = ta.getInt(R.styleable.GetToolbar_titleTextStyle, defaultTitleTextStyle)
        ta.recycle()
        // 设置标题
        titleView.text = text
        titleView.setTextColor(defaultTitleTextColor)
        titleView.setTextSize(COMPLEX_UNIT_PX, defaultTitleTextSize)
        titleView.typeface = Typeface.defaultFromStyle(defaultTitleTextStyle)
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
    //==================LiveData初始化部分 START====================//
    //============================================================//

    private fun initTitleSpaceData() {
        titleSpace.observe(lifecycleOwner) { space ->
            // 标题位置
            val center = titleCenter.value ?: false
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

    private fun initTitleCenterLiveData() {
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
    }

    private fun initTitleEditableLiveData() {
        // 监听设置标题栏是否可编辑
        titleEditable.observe(lifecycleOwner) {
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

    private fun initContentPaddingLiveData() {
        // 间隔大小监听
        contentPadding.observe(lifecycleOwner) {
            // 设置BackLayout内部间隔
            val backVisible = backView.visibility == VISIBLE
            if (backVisible) {
                backView.setPadding(it / 2, 0, it / 2, 0);
                val bParams = backView.layoutParams as LayoutParams
                bParams.marginEnd = -it / 2
                bParams.marginStart = it / 2
            }
            // 设置ActionLayout右部间隔
            val params = actionLayout.layoutParams as LayoutParams
            params.marginEnd = (it - actionLayout.defaultSpace).toInt()
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
        MaterialShapeUtils.setElevation(this, elevation)
    }

    fun setContentPadding(padding: Float?) = also { setContentPadding(padding, SizeUnit.DIP) }

    fun setContentPadding(padding: Float?, unit: SizeUnit) = also {
        val newPadding =
            if (padding == null) defaultContentPadding
            else Sizes.applyDimension(padding, unit)
        contentPadding.setValue(newPadding.toInt())
    }

    //============================================================//
    //==================其他设置部分部分 END========================//
    //=====================BACK部分 START=========================//
    //============================================================//

    fun setBackVisible(visible: Boolean?) = also {
        val newVisible = visible ?: defaultBackVisible
        if (newVisible != (backView.iconView.visibility == VISIBLE)) {
            backView.visibility = if (newVisible) VISIBLE else GONE
            contentPadding.value = contentPadding.value!!
        }
    }

    fun setBackRipple(ripple: Boolean?) = also { backView.setRipple(ripple) }

    fun setBackIcon(@DrawableRes resId: Int?) = also {
        // 无资源ID
        if (resId == null) backView.setIconDrawable(null)
        // 有资源ID
        else backView.setIconResource(resId)
    }

    fun setBackIcon(drawable: Drawable?) = also { backView.setIconDrawable(drawable) }

    fun setBackIcon(bitmap: Bitmap?) = also { backView.setIconBitmap(bitmap) }

    fun setBackIconTintColor(@ColorInt color: Int?) = also { backView.setIconTintColor(color) }

    fun setBackIconSpace(space: Float?) = also { backView.setIconSpace(space) }

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
        // 无ID资源
        if (id == null) titleView.text = null
        // 有ID资源
        else titleView.setText(id)
    }

    fun setTitle(text: CharSequence?) = also { titleView.text = text }

    fun setTitleTextColor(@ColorInt color: Int?) = also {
        val newColor = color ?: defaultTitleTextColor
        titleView.setTextColor(newColor)
    }

    fun setTitleTextSize(size: Float?) = also { setTitleTextSize(size, SizeUnit.SP) }

    fun setTitleTextSize(size: Float?, unit: SizeUnit) = also {
        val newSize =
            if (size == null) defaultTitleTextSize
            else Sizes.applyDimension(size, unit)
        titleView.setTextSize(SizeUnit.PX.type, newSize)
    }

    fun setTitleTextTypeface(typeface: Typeface?) = also {
        val newTypeface = typeface ?: Typeface.defaultFromStyle(defaultTitleTextStyle)
        titleView.typeface = newTypeface
    }

    fun setTitleCenter(center: Boolean?) = also { titleCenter.value = center ?: false }

    fun setTitleEditable(editable: Boolean?) = also { titleEditable.value = editable ?: false }

    fun setTitleSpace(space: Float?) = also { setTitleSpace(space, SizeUnit.DIP) }

    fun setTitleSpace(space: Float?, unit: SizeUnit) = also {
        val newSpace =
            if (space == null) defaultTitleSpace
            else Sizes.applyDimension(space, unit)
        titleSpace.postValue(newSpace.toInt())
    }

    //============================================================//
    //======================TITLE部分 END=========================//
    //====================ACTION部分 START========================//
    //============================================================//

    fun setActionRipple(ripple: Boolean?) = also { actionLayout.setRipple(ripple) }

    fun setActionRipple(key: Int?, ripple: Boolean?) = also { actionLayout.setRipple(key, ripple) }

    fun setActionVisible(visible: Boolean?) = also { actionLayout.setVisible(visible) }

    fun setActionVisible(key: Int?, visible: Boolean?) = also { actionLayout.setVisible(key, visible) }

    fun setActionSpace(space: Float?) = also { setActionSpace(space, SizeUnit.DIP) }

    fun setActionSpace(space: Float?, unit: SizeUnit) = also {
        actionLayout.setSpace(space, unit)
        contentPadding.value = contentPadding.value!!
        actionLayout.post { titleSpace.value = titleSpace.value!! }
    }

    fun setActionText(key: Int?, @StringRes resId: Int) = also { actionLayout.setText(key, resId) }

    fun setActionText(key: Int?, text: CharSequence?) = also { actionLayout.setText(key, text) }

    fun setActionTextColor(@ColorInt color: Int?) = also { actionLayout.setTextColor(color) }

    fun setActionTextColor(key: Int?, @ColorInt color: Int?) = also { actionLayout.setTextColor(key, color) }

    fun setActionTextSize(size: Float?) = also { actionLayout.setTextSize(size) }

    fun setActionTextSize(size: Float?, unit: SizeUnit) = also { actionLayout.setTextSize(size, unit) }

    fun setActionTextSize(key: Int?, size: Float?) = also { actionLayout.setTextSize(key, size) }

    fun setActionTextSize(key: Int?, size: Float?, unit: SizeUnit) = also {
        actionLayout.setTextSize(key, size, unit)
    }

    fun setActionTextTypeface(typeface: Typeface?) = also { actionLayout.setTextTypeface(typeface) }

    fun setActionTextTypeface(key: Int?, typeface: Typeface?) = also {
        actionLayout.setTextTypeface(key, typeface)
    }

    fun setActionIcon(key: Int?, @DrawableRes resId: Int?) = also { actionLayout.setIcon(key, resId) }

    fun setActionIcon(key: Int?, @DrawableRes resId: Int?, @ColorInt tintColor: Int?) = also {
        actionLayout.setIcon(key, resId, tintColor)
    }

    fun setActionIcon(key: Int?, drawable: Drawable?) = also { actionLayout.setIcon(key, drawable) }

    fun setActionIcon(key: Int?, drawable: Drawable?, @ColorInt tintColor: Int?) = also {
        actionLayout.setIcon(key, drawable, tintColor)
    }

    fun setActionIconTintColor(@ColorInt color: Int) = also { actionLayout.setIconTintColor(color) }

    fun setActionIconTintColor(key: Int?, @ColorInt tintColor: Int) = also {
        actionLayout.setIconTintColor(key, tintColor)
    }

    fun setActionListener(key: Int?, listener: OnClickListener?) = also {
        actionLayout.setListener(key, listener)
    }

    //============================================================//
    //=====================ACTION部分 END=========================//
    //===================DIVIDER部分 START=========================//
    //============================================================//

    fun setDividerColor(@ColorInt color: Int?) = also {
        // 无色值
        if (color == null) dividerView.background = null
        // 有色值
        else dividerView.setBackgroundColor(color)
    }

    fun setDividerDrawable(drawable: Drawable?) = also { dividerView.background = drawable }

    fun setDividerHeight(height: Float?) = also { return setDividerHeight(height, SizeUnit.DIP) }

    fun setDividerHeight(height: Float?, unit: SizeUnit) = also {
        dividerView.layoutParams.height =
            if (height == null) 0
            else Sizes.applyDimension(height, unit).toInt()
        dividerView.requestLayout()
    }

    fun setDividerMargin(margin: Float?) = also { setDividerMargin(margin, SizeUnit.DIP) }

    fun setDividerMargin(margin: Float?, unit: SizeUnit) = also {
        val newMargin =
            if (margin == null) 0
            else Sizes.applyDimension(margin, unit).toInt()
        val params = dividerView.layoutParams as LayoutParams
        params.setMargins(newMargin, 0, newMargin, 0)
        dividerView.requestLayout()
    }

    fun setDividerVisible(visible: Boolean?) = also { dividerView.visibility = if (visible == true) VISIBLE else GONE }

    //============================================================//
    //=====================DIVIDER部分 END========================//
    //============================================================//

    private fun createMaterialShapeDrawableBackground(background: Drawable): Drawable {
        return if (background is ColorDrawable) {
            val materialShapeDrawable = MaterialShapeDrawable()
            materialShapeDrawable.fillColor = ColorStateList.valueOf(background.color)
            materialShapeDrawable.initializeElevationOverlay(context)
            materialShapeDrawable
        } else background
    }

    private companion object {
        const val RIPPLE = 0
        const val CONTENT_PADDING = 1
        const val BACK_VISIBLE = 2
        const val TITLE_CENTER = 3
        const val TITLE_SPACE = 4
        const val TITLE_TEXT_COLOR = 5
        const val TITLE_TEXT_SIZE = 6
        const val TITLE_TEXT_STYLE = 7
    }
}