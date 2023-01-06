package cn.cqray.android.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet

import android.util.TypedValue
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

/**
 * 标题栏
 * @author Cqray
 */
@Suppress("unused")
class Toolbar2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(context, attrs, defStyleAttr), LifecycleOwner {

    private val mLifecycleRegistry: LifecycleRegistry

    /** 标题控件  */
    @Suppress("unused")
    lateinit var titleView: TextView
        private set

    @Suppress("unused")
    lateinit var dividerView: View
        private set

    @Suppress("unused")
    lateinit var backView: IconTextView
        private set
    var actionLayout: ActionLayout? = null
        private set
    private var mTitleSpace = 0
    private var mTitleCenter = false

    //    private val mLifecycleOwner: LifecycleOwner
    private val mPadding = MutableLiveData<Int?>()
    private val mUseRipple = MutableLiveData<Boolean>()
    private val mTitleCenterData = MutableLiveData<Boolean>()
    private val mTitleEditable = MutableLiveData<Boolean>()

    init {
        //mLifecycleOwner = LifecycleOwner { mLifecycleRegistry }
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
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

    override fun getLifecycle(): Lifecycle = mLifecycleRegistry

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.RESUMED
        MaterialShapeUtils.setParentAbsoluteElevation(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }

    private fun initToolbarBasic(attrs: AttributeSet?) {
        val context = context
        val size = Sizes.content()
        val elev = Sizes.px(R.dimen.elevation)
        // 获取默认属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val padding = ta.getDimensionPixelSize(R.styleable.Toolbar_sPadding, size)
        val elevation = ta.getDimension(R.styleable.Toolbar_sElevation, elev.toFloat())
        val useRipple = ta.getBoolean(R.styleable.Toolbar_sUseRipple, true)
        val titleEditable = ta.getBoolean(R.styleable.Toolbar_sTitleEditable, false)
        mTitleCenter = ta.getBoolean(R.styleable.Toolbar_sTitleCenter, false)
        mTitleSpace = ta.getDimensionPixelSize(R.styleable.Toolbar_sTitleSpace, size)
        ta.recycle()
        // 设置标题栏背景
        val primaryColor = ContextCompat.getColor(context, R.color.colorPrimary)
        val background = if (background == null) ColorDrawable(primaryColor) else background
        ViewCompat.setBackground(this, createMaterialShapeDrawableBackground(background))
        // 其他属性
        mTitleCenterData.value = mTitleCenter
        mTitleEditable.value = titleEditable
        mUseRipple.value = useRipple
        mPadding.value = padding
        setElevation(elevation)
    }


    private fun initToolbarBack(attrs: AttributeSet?) {
        val context = context
        // 获取属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val drawable = ta.getDrawable(R.styleable.Toolbar_sBackIcon)
        val text = ta.getString(R.styleable.Toolbar_sBackText)
        val iconVisible = ta.getBoolean(R.styleable.Toolbar_sBackIconVisible, true)
        val textColor = ta.getColor(R.styleable.Toolbar_sBackTextColor, Color.WHITE)
        val textSize =
            ta.getDimensionPixelSize(R.styleable.Toolbar_sBackTextSize, resources.getDimensionPixelSize(R.dimen.h3))
        val backTextStyle = ta.getInt(R.styleable.Toolbar_sBackTextStyle, 0)
        ta.recycle()
        // 设置Nav布局
        val params = LayoutParams(-2, -1)
        backView = IconTextView(context)
        backView.id = R.id.starter_toolbar_back_view
        backView.layoutParams = params
        backView.setText(text)
        backView.setTextColor(textColor)
        backView.setTextSize(textSize.toFloat(), TypedValue.COMPLEX_UNIT_PX)
        backView.setTypeface(Typeface.defaultFromStyle(backTextStyle))
        backView.iconView.visibility = if (iconVisible) VISIBLE else GONE
        if (drawable == null) {
            backView.setIconResource(R.drawable.def_back_material_light)
        } else {
            backView.setIconDrawable(drawable)
        }
        addView(backView)
    }

    private fun initToolbarAction(attrs: AttributeSet?) {
        val context = context
        // 设置Action布局
        val params = LayoutParams(-2, -1)
        params.addRule(if (Build.VERSION.SDK_INT >= 17) ALIGN_PARENT_END else ALIGN_PARENT_RIGHT)
        actionLayout = ActionLayout(context, attrs)
        actionLayout!!.id = R.id.starter_toolbar_action_layout
        actionLayout!!.layoutParams = params
        addView(actionLayout)
        actionLayout!!.requestLayout()
    }

    private fun initToolbarTittle(attrs: AttributeSet?) {
        val context = context
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val text = ta.getString(R.styleable.Toolbar_sTitleText)
        val textColor = ta.getColor(R.styleable.Toolbar_sTitleTextColor, Color.WHITE)
        val textSize =
            ta.getDimensionPixelSize(R.styleable.Toolbar_sTitleTextSize, resources.getDimensionPixelSize(R.dimen.h2))
        val textStyle = ta.getInt(R.styleable.Toolbar_sTitleTextStyle, 0)
        ta.recycle()
        // 设置标题
        titleView = AppCompatEditText(context)
        titleView.layoutParams = LayoutParams(-1, -1)
        titleView.id = R.id.starter_toolbar_title
        titleView.text = text
        titleView.setTextColor(textColor)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        titleView.setTypeface(Typeface.defaultFromStyle(textStyle))
        addView(titleView)
    }

    private fun initToolbarDivider(attrs: AttributeSet?) {
        val context = context
        val ta = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
        val color =
            ta.getColor(R.styleable.Toolbar_sDividerColor, ContextCompat.getColor(context, R.color.divider))
        val height = ta.getDimensionPixelSize(R.styleable.Toolbar_sDividerHeight, 0)
        val margin = ta.getDimensionPixelSize(R.styleable.Toolbar_sDividerMargin, 0)
        val visible = ta.getBoolean(R.styleable.Toolbar_sDividerVisible, true)
        ta.recycle()
        // 设置分割线
        val params = LayoutParams(-1, height)
        params.addRule(ALIGN_PARENT_BOTTOM)
        params.setMargins(margin, 0, margin, 0)
        dividerView = View(context)
        dividerView.setBackgroundColor(color)
        dividerView.layoutParams = params
        dividerView.visibility = if (visible) VISIBLE else INVISIBLE
        addView(dividerView)
    }

    @SuppressLint("InlinedApi")
    private fun initLiveData() {
        // 标题居中监听
        mTitleCenterData.observe(this, { center: Boolean ->
            val isNewApi = Build.VERSION.SDK_INT >= 17
            val padding = if (mPadding.value == null) 0 else mPadding.value!!
            val iconVisible = backView.iconView.visibility == VISIBLE
            val params = titleView.layoutParams as LayoutParams
            if (center) {
                val leftWidth = backView.width + if (iconVisible) mTitleSpace - padding else 0
                val actionWidth = actionLayout!!.width + mTitleSpace - actionLayout!!.actionSpace
                val m = leftWidth.coerceAtLeast(actionWidth)
                params.addRule(if (isNewApi) START_OF else LEFT_OF, -1)
                params.addRule(if (isNewApi) END_OF else RIGHT_OF, -1)
                params.addRule(CENTER_IN_PARENT)
                params.setMargins(m, 0, m, 0)
                titleView.gravity = Gravity.CENTER
            } else {
                params.addRule(if (isNewApi) START_OF else LEFT_OF, R.id.starter_toolbar_action_layout)
                params.addRule(if (isNewApi) END_OF else RIGHT_OF, R.id.starter_toolbar_back_view)
                params.leftMargin = if (iconVisible) mTitleSpace - padding else 0
                params.rightMargin = mTitleSpace - actionLayout!!.actionSpace
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.marginStart = if (iconVisible) mTitleSpace - padding else 0
                    params.marginEnd = mTitleSpace - actionLayout!!.actionSpace
                }
                params.addRule(CENTER_VERTICAL)
                titleView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }
            titleView.layoutParams = params
        })
        // 监听设置标题栏是否可编辑
        mTitleEditable.observe(this, { aBoolean: Boolean ->
            titleView.isFocusableInTouchMode = aBoolean
            titleView.isClickable = aBoolean
            titleView.isFocusable = aBoolean
            titleView.isEnabled = aBoolean
            if (aBoolean) {
                val ta = context.obtainStyledAttributes(intArrayOf(android.R.attr.editTextBackground))
                val drawable = ta.getDrawable(0)
                ta.recycle()
                ViewCompat.setBackground(titleView, drawable)
            } else {
                ViewCompat.setBackground(titleView, null)
            }
            backView.bringToFront()
            actionLayout!!.bringToFront()
        })
        // 是否使用水波纹
        mUseRipple.observe(this, { aBoolean: Boolean? ->
            backView.setUseRipple(aBoolean!!)
            actionLayout!!.setDefaultUseRipple(aBoolean)
        })
        // 间隔大小监听
        mPadding.observe(this, { aInteger: Int? ->
            // 设置BackLayout内部间隔
            val iconVisible = backView.iconView.visibility == VISIBLE
            backView.setPadding(aInteger!!, 0, (if (iconVisible) aInteger else 0), 0)
            // 设置ActionLayout右部间隔
            val params = actionLayout!!.layoutParams as LayoutParams
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.marginEnd = aInteger - actionLayout!!.actionSpace
            }
            params.rightMargin = aInteger - actionLayout!!.actionSpace
        })
    }

    override fun setElevation(elevation: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setElevation(elevation)
        }
        MaterialShapeUtils.setElevation(this, elevation)
    }

    override fun setGravity(gravity: Int) {}

    fun setPadding(padding: Float): Toolbar2 {
        setPadding(padding, TypedValue.COMPLEX_UNIT_DIP)
        return this
    }

    fun setPadding(padding: Float, unit: Int): Toolbar2 {
        mPadding.value = Sizes.applyDimension(padding, unit).toInt()
        return this
    }

    fun setUseRipple(useRipple: Boolean): Toolbar2 {
        mUseRipple.value = useRipple
        return this
    }

    fun setTitleCenter(center: Boolean): Toolbar2 {
        mTitleCenter = center
        mTitleCenterData.value = center
        return this
    }

    fun setBackViewSpace(space: Float) = also { backView.setViewSpace(space) }

    fun setBackIcon(@DrawableRes resId: Int) = also { backView.setIconResource(resId) }

    fun setBackIcon(drawable: Drawable?) = also { backView.setIconDrawable(drawable) }

    fun setBackIcon(bitmap: Bitmap?) = also { backView.setIconBitmap(bitmap) }

    fun setBackIconTintColor(@ColorInt color: Int) = also { backView.setIconTintColor(color) }

    fun setBackIconTintList(tintList: ColorStateList?) = also { backView.setIconTintList(tintList) }

    fun setBackIconVisible(visible: Boolean): Toolbar2 {
        if (visible != (backView.iconView.visibility == VISIBLE)) {
            backView.iconView.visibility = if (visible) VISIBLE else GONE
            mPadding.value = mPadding.value
        }
        return this
    }

    fun setBackText(text: CharSequence?) = also { backView.setText(text) }

    fun setBackText(@StringRes id: Int) = also { backView.setText(id) }

    fun setBackTextColor(@ColorInt color: Int) = also { backView.setTextColor(color) }

    fun setBackTextSize(size: Float) = also { backView.setTextSize(size) }

    fun setBackTextSize(size: Float, unit: Int): Toolbar2 {
        backView.setTextSize(size, unit)
        return this
    }

    fun setBackTypeface(typeface: Typeface?) = also { backView.setTypeface(typeface) }

    fun setBackListener(listener: OnClickListener?) = also { backView.setOnClickListener(listener) }

    fun setTitle(@StringRes id: Int) = also { titleView.setText(id) }

    fun setTitle(text: CharSequence?) = also { titleView.text = text }

    fun setTitleTextColor(@ColorInt color: Int) = also { titleView.setTextColor(color) }

    fun setTitleTextSize(size: Float) = also { titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size) }

    fun setTitleTextSize(size: Float, unit: Int) = also { titleView.setTextSize(unit, size) }

    fun setTitleTypeface(typeface: Typeface?) = also { titleView.typeface = typeface }

    fun setTitleEditable(editable: Boolean) = also { mTitleEditable.value = editable }

    fun setTitleSpace(space: Float): Toolbar2 {
        return setTitleSpace(space, TypedValue.COMPLEX_UNIT_DIP)
    }

    fun setTitleSpace(space: Float, unit: Int): Toolbar2 {
        mTitleSpace = Sizes.applyDimension(space, unit).toInt()
        mTitleCenterData.value = mTitleCenter
        return this
    }

    fun setActionText(key: Int, text: CharSequence?): Toolbar2 {
        actionLayout!!.setActionText(key, text)
        return this
    }

    fun setActionText(key: Int, @StringRes resId: Int): Toolbar2 {
        actionLayout!!.setActionText(key, resId)
        return this
    }

    fun setDefaultActionTextColor(@ColorInt color: Int): Toolbar2 {
        actionLayout!!.setDefaultActionTextColor(color)
        return this
    }

    fun setActionTextColor(key: Int, @ColorInt color: Int): Toolbar2 {
        actionLayout!!.setActionTextColor(key, color)
        return this
    }

    fun setDefaultActionTextSize(size: Float): Toolbar2 {
        actionLayout!!.setDefaultActionTextSize(size)
        return this
    }

    fun setDefaultActionTextSize(size: Float, unit: Int): Toolbar2 {
        actionLayout!!.setDefaultActionTextSize(size, unit)
        return this
    }

    fun setActionTextSize(key: Int, size: Float): Toolbar2 {
        actionLayout!!.setActionTextSize(key, size)
        return this
    }

    fun setActionTextSize(key: Int, size: Float, unit: Int): Toolbar2 {
        actionLayout!!.setActionTextSize(key, size, unit)
        return this
    }

    fun setDefaultActionTypeface(typeface: Typeface?): Toolbar2 {
        actionLayout!!.setDefaultActionTypeface(typeface)
        return this
    }

    fun setActionTypeface(key: Int, typeface: Typeface?): Toolbar2 {
        actionLayout!!.setActionTypeface(key, typeface)
        return this
    }

    fun setActionIcon(key: Int, @DrawableRes resId: Int): Toolbar2 {
        actionLayout!!.setActionIcon(key, resId)
        return this
    }

    fun setActionIcon(key: Int, @DrawableRes resId: Int, @ColorInt tintColor: Int): Toolbar2 {
        actionLayout!!.setActionIcon(key, resId, tintColor)
        return this
    }

    fun setActionIcon(key: Int, drawable: Drawable?): Toolbar2 {
        actionLayout!!.setActionIcon(key, drawable)
        return this
    }

    fun setActionIcon(key: Int, drawable: Drawable?, @ColorInt tintColor: Int): Toolbar2 {
        actionLayout!!.setActionIcon(key, drawable, tintColor)
        return this
    }

    fun setActionIconColor(@ColorInt tintColor: Int): Toolbar2 {
        actionLayout!!.setActionIconColor(tintColor)
        return this
    }

    fun setActionIconColor(key: Int, @ColorInt tintColor: Int): Toolbar2 {
        actionLayout!!.setActionIconColor(key, tintColor)
        return this
    }

    fun setActionVisible(visible: Boolean): Toolbar2 {
        actionLayout!!.setActionVisible(visible)
        return this
    }

    fun setActionVisible(key: Int, visible: Boolean): Toolbar2 {
        actionLayout!!.setActionVisible(key, visible)
        return this
    }

    fun setActionUseRipple(key: Int, useRipple: Boolean): Toolbar2 {
        actionLayout!!.setUseRipple(key, useRipple)
        return this
    }

    fun setActionSpace(space: Float): Toolbar2 {
        return setActionSpace(space, TypedValue.COMPLEX_UNIT_DIP)
    }

    fun setActionSpace(space: Float, unit: Int): Toolbar2 {
        actionLayout!!.setActionSpace(space, unit)
        mPadding.value = mPadding.value
        mTitleCenterData.value = mTitleCenter
        return this
    }

    fun setActionListener(key: Int, listener: OnClickListener?) = also {
        actionLayout?.setActionListener(key, listener)
    }

    fun setDividerColor(@ColorInt color: Int) = also { dividerView.setBackgroundColor(color) }

    fun setDividerHeight(height: Float) = also { setDividerHeight(height, TypedValue.COMPLEX_UNIT_DIP) }

    @Suppress("all")
    fun setDividerHeight(height: Float, unit: Int) = also {
        dividerView.layoutParams.height = Sizes.applyDimension(height, unit).toInt()
        dividerView.requestLayout()
    }

    fun setDividerMargin(margin: Float) = also { setDividerMargin(margin, TypedValue.COMPLEX_UNIT_DIP) }

    @Suppress("all")
    fun setDividerMargin(margin: Float, unit: Int) = also {
        val m = Sizes.applyDimension(margin, unit).toInt()
        val params = dividerView.layoutParams as LayoutParams
        params.setMargins(m, 0, m, 0)
        dividerView.requestLayout()
    }

    fun setDividerVisible(visible: Boolean) = also { dividerView.visibility = if (visible) VISIBLE else INVISIBLE }

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