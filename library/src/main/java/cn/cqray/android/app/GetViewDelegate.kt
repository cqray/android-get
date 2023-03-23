package cn.cqray.android.app

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

import android.view.View
import android.view.ViewGroup
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
import androidx.viewbinding.ViewBinding
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.databinding.GetViewDefaultLayoutBinding
import cn.cqray.android.state.GetStateDelegate
import cn.cqray.android.state.GetStateLayout
import cn.cqray.android.util.*
import cn.cqray.android.util.ContextUtils.inflate
import cn.cqray.android.widget.GetToolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * 界面布局代理
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetViewDelegate internal constructor(provider: GetViewProvider) : GetDelegate<GetViewProvider>(provider) {

    /** ButterKnife绑定 **/
    private var knifeUnBinder: Any? = null

    /** 是否设置Get扩展界面 **/
    private var setGetContentView: Boolean = true

    /** [ViewBinding]实例 **/
    private val binding by lazy { GetViewDefaultLayoutBinding.inflate(ContextUtils.layoutInflater) }

    /** 状态委托 **/
    val stateDelegate by lazy { GetStateDelegate() }

    /** 根控件 */
    val root: View by lazy { binding.root }

    /** 标题 */
    val toolbar: GetToolbar by lazy { binding.getToolbar }

    /** 头部容器 */
    val headerLayout: FrameLayout by lazy { binding.getHeader }

    /** 底部容器 */
    val footerLayout: FrameLayout by lazy { binding.getFooter }

    /** 刷新视图 **/
    var refreshLayout: SmartRefreshLayout? = null
        private set

    /** 内容容器 */
    internal val contentLayout: FrameLayout by lazy { binding.getContent }

    /** 界面背景 */
    private val background: MutableLiveData<Any?> by lazy {
        MutableLiveData<Any?>().also {
            it.observe(provider as LifecycleOwner) { any ->
                // 常规设置背景
                val normalSet = { any_: Any? ->
                    when (any_) {
                        is Int -> root.setBackgroundResource(any_)
                        is Drawable -> root.background = any_
                        else -> root.background = null
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

    /**
     * 上下文
     */
    val context: Context
        get() = if (provider is FragmentActivity) provider
        else (provider as Fragment).requireContext()

    /** 清理资源 */
    override fun onCleared() {
        ButterKnifeUtils.unbind(knifeUnBinder)
        System.gc()
    }

    /**
     * 确认[GetActivity.setContentView]被[setGetContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    fun ensureSetGetContentView() = run { setGetContentView = true }

    /**
     * 确认[GetActivity.setContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    fun ensureSetNativeContentView() = run { setGetContentView = false }

    /**
     * 设置默认布局
     * @param id 布局Id
     */
    fun setGetContentView(@LayoutRes id: Int) = setGetContentView(inflate(id))

    /**
     * 设置默认布局
     * @param view 布局
     */
    fun setGetContentView(view: View) {
        (view.parent as? ViewGroup)?.removeView(view)
        if (!setGetContentView) {
            setNativeContentView(view)
            return
        }
        removeContentView()
        initRefreshLayout()
        initContentView()
        initGetView()
        addContentView(view)
    }

    /**
     * 设置原生布局
     * @param id 布局Id
     */
    fun setNativeContentView(@LayoutRes id: Int) = setNativeContentView(inflate(id))

    /**
     * 设置原生布局
     * @param view 布局
     */
    fun setNativeContentView(view: View) {
        (view.parent as? ViewGroup)?.removeView(view)
        removeContentView()
        ensureSetNativeContentView()
        initRefreshLayout()
        initContentView()
        initGetView()
        addContentView(view)
    }

    /**
     * 移除内容视图
     */
    fun removeContentView() {
        val layout = binding.getContent.getChildAt(0)
        if (layout is GetStateLayout) {
            layout.removeAllViews()
        } else if (layout is SmartRefreshLayout) {
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)
                if (child is GetStateLayout) {
                    child.removeAllViews()
                }
            }
        }
    }

    /**
     * 添加内容视图
     */
    fun addContentView(view: View) {
        val layout = binding.getContent.getChildAt(0)
        if (layout is GetStateLayout) {
            layout.addView(view)
        } else if (layout is SmartRefreshLayout) {
            for (i in 0 until layout.childCount) {
                val child = layout.getChildAt(i)
                if (child is GetStateLayout) {
                    child.addView(view)
                }
            }
        }
    }

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     */
    fun setHeaderView(@LayoutRes id: Int) = setHeaderView(id, null)

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    fun setHeaderView(@LayoutRes id: Int, floating: Boolean?) = setHeaderView(inflate(id), floating)

    /**
     * 设置顶部视图
     * @param view 视图
     */
    fun setHeaderView(view: View?) = setHeaderView(view, false)

    /**
     * 设置顶部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    fun setHeaderView(view: View?, floating: Boolean?) {
        // 添加或移除Header
        headerLayout.removeAllViews()
        view?.let { headerLayout.addView(it) }
        initUnBinder()
        // 更新Header位置
        floating?.let {
            val params = binding.getContent.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, if (it) R.id.get_toolbar else R.id.get_header)
            binding.getContent.requestLayout()
        }
    }

    /**
     * 设置底部视图
     * @param id 视图资源ID
     */
    fun setFooterView(@LayoutRes id: Int) = setFooterView(id, null)

    /**
     * 设置底部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    fun setFooterView(@LayoutRes id: Int, floating: Boolean?) = setFooterView(inflate(id), floating)

    /**
     * 设置底部视图
     * @param view 视图
     */
    fun setFooterView(view: View?) = setFooterView(view, false)

    /**
     * 设置底部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    fun setFooterView(view: View?, floating: Boolean?) {
        footerLayout.removeAllViews()
        view?.let { footerLayout.addView(it) }
        initUnBinder()
        // 更新Footer位置
        floating?.let {
            val params = binding.getContent.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ABOVE, if (it) 0 else R.id.get_footer)
            binding.getContent.requestLayout()
        }
    }

    /** 隐藏标题栏 */
    fun showToolbar() = run { toolbar.visibility = View.VISIBLE }

    /** 显示标题栏 */
    fun hideToolbar() = run { toolbar.visibility = View.GONE }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     */
    fun <T : View> findViewById(@IdRes resId: Int): T = root.findViewById(resId)!!

    /**
     * 设置背景
     * @param id 资源ID
     */
    fun setBackgroundResource(@DrawableRes id: Int) = id.also { background.value = it }

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    fun setBackgroundColor(@ColorInt color: Int) = setBackground(ColorDrawable(color))

    /**
     * 设置背景资源ID或颜色
     * @param any 资源ID或颜色
     */
    fun setBackground(any: Int) = setBackground(GetCompat.getDrawable(any))

    /**
     * 设置背景
     * @param drawable 图像
     */
    fun setBackground(drawable: Drawable?) = drawable.also { background.value = it }

    /** 设置内容界面 */
    private fun initContentView() {
        if (provider is AppCompatActivity) {
            // 设置界面
            provider.delegate.setContentView(root)
        } else if (provider is ComponentActivity) {
            // 考虑到有可能重写了 setContentView
            // 所以需要临时改变 mSetGetContentView 的值
            setGetContentView.let {
                // 暂时设置为False
                setGetContentView = false
                // 设置界面
                provider.setContentView(root)
                // 重置为原始状态
                setGetContentView = it
            }
        }
    }

    /**
     * 初始化界面相关控件
     */
    private fun initGetView() {
        if (provider is GetActivity || provider is GetFragment) {
            // Toolbar赋值
            ReflectUtil.setField(provider, "toolbar", toolbar)
            // 使用了SmartRefreshLayout才赋值
            if (setGetContentView) ReflectUtil.setField(provider, "refreshLayout", refreshLayout)
        }
        // 状态委托连接界面
        if (provider is Activity) {
            stateDelegate.attachActivity(provider)
        } else if (provider is Fragment) {
            stateDelegate.attachFragment(provider)
            background.value = Get.init.fragmentBackgroundGet()
        }
        // 初始化标题
        initToolbar()
        // 初始化ButterKnife
        initUnBinder()
    }

    /**
     * 初始化标题
     */
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
            // 原始界面默认不显示标题
            visibility = if (setGetContentView) View.VISIBLE else View.GONE
            setToolbarInit(Get.init.toolbarInit!!)
        }
    }

    /**
     * 初始化刷新视图
     */
    private fun initRefreshLayout() {
        if (setGetContentView) {
            // 初始化容器
            refreshLayout = refreshLayout ?: SmartRefreshLayout(context).also {
                it.overScrollMode = View.OVER_SCROLL_NEVER
                it.setEnableLoadMore(false)
                it.setEnableOverScrollDrag(true)
                it.setEnablePureScrollMode(true)
                it.setEnableScrollContentWhenRefreshed(false)
                // SetGetContentView布局，则添加视图
                contentLayout.addView(it)
            }
        } else if (refreshLayout != null) {
            // 移除容器
            contentLayout.removeView(refreshLayout!!)
            refreshLayout = null
        }
    }

    /** 初始化ButterKnife */
    private fun initUnBinder() {
        ButterKnifeUtils.unbind(knifeUnBinder)
        knifeUnBinder = ButterKnifeUtils.bind(provider, root)
    }
}