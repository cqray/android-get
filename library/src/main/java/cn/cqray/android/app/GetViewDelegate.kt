package cn.cqray.android.app

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.util.ActivityUtils
import cn.cqray.android.third.ButterKnifeUtils
import cn.cqray.android.util.ContextUtils.inflate
import cn.cqray.android.util.ReflectUtil
import cn.cqray.android.util.Sizes
import cn.cqray.android.widget.Toolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * 界面布局代理
 * @author Cqray
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class GetViewDelegate(provider: GetViewProvider) :
    GetDelegate<GetViewProvider>(provider) {

    /** ButterKnife绑定（原子对象，无特殊作用，为了让变量变为 final） **/
    private val knifeUnBinder = AtomicReference<Any?>()

    /** 是否设置Get扩展界面（原子性，无特殊作用，为了让变量变为 final） **/
    private val setGetContentView = AtomicBoolean(true)

    /** 关联的内容控件（原子对象，无特殊作用，为了让变量变为 final） **/
    private val attachedContentView = AtomicReference<View?>()

    /** 根控件 **/
    val rootView: View by lazy { inflate(R.layout.get_layout_view_default) }

    /** 标题 **/
    val toolbar: Toolbar by lazy { rootView.findViewById(R.id.get_toolbar) }

    /** 内容容器 **/
    val contentLayout: FrameLayout by lazy { rootView.findViewById(R.id.get_content) }

    /** 头部容器 **/
    val headerLayout: FrameLayout by lazy { rootView.findViewById(R.id.get_header) }

    /** 底部容器 **/
    val footerLayout: FrameLayout by lazy { rootView.findViewById(R.id.get_footer) }

    /** 刷新控件 **/
    val refreshLayout: SmartRefreshLayout by lazy {
        SmartRefreshLayout(context).also {
            it.overScrollMode = View.OVER_SCROLL_NEVER
            it.setEnableLoadMore(false)
            it.setEnableOverScrollDrag(true)
            it.setEnablePureScrollMode(true)
            it.setEnableScrollContentWhenRefreshed(false)
        }
    }

    /** 界面背景 **/
    private val background: MutableLiveData<Any?> by lazy {
        MutableLiveData<Any?>().also {
            it.observe(provider as LifecycleOwner) { any ->
                // 常规设置背景
                val normalSet = { any_: Any? ->
                    when (any_) {
                        is Int -> rootView.setBackgroundResource(any_)
                        is Drawable -> rootView.background = any_
                        else -> rootView.background = null
                    }
                }
                if (provider is ComponentActivity) {
                    val isTranslucentOrFloating = ActivityUtils.isTranslucentOrFloating(provider)
                    if (isTranslucentOrFloating) normalSet(any) else {
                        // 在Window窗口上设置背景
                        when (any) {
                            is Int -> provider.window.setBackgroundDrawableResource(any)
                            is Drawable -> provider.window.setBackgroundDrawable(any)
                            else -> provider.window.setBackgroundDrawable(null)
                        }
                    }
                } else normalSet(any)
            }
        }
    }

    /** 状态管理器 **/
    private val stateDelegate: StateDelegate? by lazy {
        if (provider !is StateProvider) null
        else StateDelegate(provider)
    }

    /** 内容控件 **/
    val contentView: View? get() = attachedContentView.get()

    /**
     * 上下文
     **/
    val context: Context
        get() = if (provider is FragmentActivity) provider
        else (provider as Fragment).requireContext()

    /** 清理资源 **/
    override fun onCleared() {
        ButterKnifeUtils.unbind(knifeUnBinder.get())
        System.gc()
    }

    /**
     * 确认[GetActivity.setContentView]被[setGetContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     **/
    fun ensureSetGetContentView(): () -> Unit = { setGetContentView.set(true) }

    /**
     * 确认[GetActivity.setContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     **/
    fun ensureSetNativeContentView(): () -> Unit = { setGetContentView.set(false) }

    /**
     * 设置默认布局
     * @param id 布局Id
     **/
    fun setGetContentView(@LayoutRes id: Int) = setGetContentView(inflate(id))

    /**
     * 设置默认布局
     * @param view 布局
     **/
    fun setGetContentView(view: View) {
        if (!setGetContentView.get()) {
            setNativeContentView(view)
            return
        }
        attachedContentView.set(view)
        contentLayout.removeAllViews()
        contentLayout.addView(refreshLayout.also { it.addView(view) })
        toolbar.visibility = View.VISIBLE
        initContentView()
        initGetView()
    }

    /**
     * 设置原生布局
     * @param id 布局Id
     **/
    fun setNativeContentView(@LayoutRes id: Int) = setNativeContentView(inflate(id))

    /**
     * 设置原生布局
     * @param view 布局
     **/
    fun setNativeContentView(view: View) {
        setGetContentView.set(false)
        attachedContentView.set(view)
        contentLayout.removeAllViews()
        contentLayout.addView(view)
        toolbar.visibility = View.GONE
        initContentView()
        initGetView()
    }

    fun setHeaderView(@LayoutRes id: Int) = setHeaderView(id, null)

    fun setHeaderView(@LayoutRes id: Int, floating: Boolean?) = setHeaderView(inflate(id), floating)

    fun setHeaderView(view: View?) = setHeaderView(view, false)

    fun setHeaderView(view: View?, floating: Boolean?) {
        // 添加或移除Header
        headerLayout.removeAllViews()
        view?.let { headerLayout.addView(it) }
        initUnBinder()
        // 更新Header位置
        floating?.let {
            val params = contentLayout.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, if (it) R.id.get_toolbar else R.id.get_header)
            contentLayout.requestLayout()
        }
    }

    fun setFooterView(@LayoutRes id: Int) = setFooterView(id, null)

    fun setFooterView(@LayoutRes id: Int, floating: Boolean?) = setFooterView(inflate(id), floating)

    fun setFooterView(view: View?) = setFooterView(view, false)

    fun setFooterView(view: View?, floating: Boolean?) {
        footerLayout.removeAllViews()
        view?.let { footerLayout.addView(it) }
        initUnBinder()
        // 更新Footer位置
        floating?.let {
            val params = contentLayout.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ABOVE, if (it) 0 else R.id.get_footer)
            contentLayout.requestLayout()
        }
    }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     **/
    fun <T : View> findViewById(@IdRes resId: Int): T = contentView?.findViewById(resId)!!

    /**
     * 设置背景
     * @param id 资源ID
     **/
    fun setBackgroundResource(@DrawableRes id: Int) = id.also { background.value = it }

    /**
     * 设置背景颜色
     * @param color 颜色
     **/
    fun setBackgroundColor(@ColorInt color: Int) = setBackground(ColorDrawable(color))

    /**
     * 设置背景颜色
     * @param drawable 图像
     **/
    fun setBackground(drawable: Drawable?) = drawable.also { background.value = it }

    /** 设置内容界面 **/
    private fun initContentView() {
        if (provider is AppCompatActivity) {
            // 设置界面
            provider.delegate.setContentView(rootView)
        } else if (provider is ComponentActivity) {
            // 考虑到有可能重写了 setContentView
            // 所以需要临时改变 mSetGetContentView 的值
            setGetContentView.get().let {
                // 暂时设置为False
                setGetContentView.set(false)
                // 设置界面
                provider.setContentView(rootView)
                // 重置为原始状态
                setGetContentView.set(it)
            }
        }
    }

    /**
     * 初始化界面相关控件
     **/
    private fun initGetView() {
        if (provider is GetActivity || provider is GetFragment) {
            // Toolbar赋值
            ReflectUtil.setField(provider, "toolbar", toolbar)
            // 使用了SmartRefreshLayout才赋值
            if (setGetContentView.get()) ReflectUtil.setField(provider, "refreshLayout", refreshLayout)
        }
        // 状态委托连接界面
        stateDelegate?.let {
            if (provider is Activity) it.attachActivity(provider)
            else if (provider is Fragment) it.attachFragment(provider)
        }
        // 初始化标题
        initToolbar()
        // 初始化ButterKnife
        initUnBinder()
    }

    private fun initToolbar() {
        // 初始化标题栏监听事件
        if (provider is GetNavProvider) {
            toolbar.setBackListener {
                val delegate = (provider as GetNavProvider).navDelegate
                delegate.back()
            }
        }
        // 设置全局属性
        with(toolbar) {
            val init = Get.init.toolbarInit!!
            elevation = init.elevation ?: Sizes.dp(R.dimen.elevation)
            setContentPadding(init.contentPadding)
            // 回退按钮部分
            setBackRipple(init.backRipple)
            setBackIcon(init.backIcon)
            setBackIconSpace(init.backIconSpace)
            setBackIconTintColor(init.backIconTintColor)
            setBackText(init.backText)
            setBackTextColor(init.backTextColor)
            setBackTextSize(init.backTextSize)
            setBackTextTypeface(init.backTextTypeface)
            // 标题部分
            setTitleCenter(init.titleCenter)
            setTitleSpace(init.titleSpace)
            setTitleTextColor(init.titleTextColor)
            setTitleTextSize(init.titleTextSize)
            setTitleTextTypeface(init.titleTextTypeFace)
            // Action部分
            setActionRipple(init.actionRipple)
            setActionSpace(init.actionSpace)
            setActionTextColor(init.actionTextColor)
            setActionTextSize(init.actionTextSize)
            setActionTextTypeface(init.actionTextTypeface)
            // 分割线部分
            setDividerDrawable(init.dividerDrawable)
            setDividerHeight(init.dividerHeight)
            setDividerMargin(init.dividerMargin)
            setDividerVisible(init.dividerVisible)
        }
    }

    /** 初始化ButterKnife **/
    private fun initUnBinder() {
        ButterKnifeUtils.unbind(knifeUnBinder.get())
        knifeUnBinder.set(ButterKnifeUtils.bind(provider, rootView))
    }
}