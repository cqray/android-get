package cn.cqray.android.app2

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

import android.view.View
import android.view.ViewGroup
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
import androidx.viewbinding.ViewBinding
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android._Get
import cn.cqray.android.databinding.GetViewDefaultLayoutBinding
import cn.cqray.android.lifecycle.GetLiveData
import cn.cqray.android.state.GetStateDelegate
import cn.cqray.android.util.*
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * 界面布局代理
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unused"
)
class GetViewDelegate internal constructor(provider: GetViewProvider) : GetDelegate<GetViewProvider>(provider) {

    /** [ViewBinding]实例缓存 **/
    private val bindingRef = AtomicReference<GetViewDefaultLayoutBinding>()

    /** 获取[ViewBinding]实例 **/
    val binding: GetViewDefaultLayoutBinding
        get() {
            return bindingRef.get() ?: run {
                val temp = GetViewDefaultLayoutBinding.inflate(Contexts.layoutInflater)
                synchronized(bindingRef) { bindingRef.set(temp) }
                temp
            }
        }

    /** ButterKnife绑定 **/
    private val knifeUnBinderRef = AtomicReference<Any>()

    /** 是否设置Get扩展界面 **/
    private val setGetContentViewRef = AtomicBoolean(true)

    /** 布局内容 **/
    private val contentViewRef = AtomicReference<View>()

    /** 头部视图 **/
    private val headerViewRef = AtomicReference<View>()

    /** 底部视图 **/
    private val footerViewRef = AtomicReference<View>()

    /** 刷新布局缓存 **/
    private val refreshLayoutRef = AtomicReference<SmartRefreshLayout>()

    /** 状态管理委托 **/
    val stateDelegate by lazy { GetStateDelegate() }

    /** 根控件 */
    val rootView get() = binding.root

    /** 标题布局 */
    val toolbarLayout get() = binding.getToolbar.parent as AppBarLayout

    /** 标题 **/
    val toolbar get() = binding.getToolbar

    /** 头部布局 */
    val headerLayout get() = binding.getHeader

    /** 底部布局 */
    val footerLayout get() = binding.getFooter

    /** 内容布局 **/
    val contentLayout get() = binding.getContent

    /** 头部视图 */
    val headerView: View? get() = headerViewRef.get()

    /** 底部视图 */
    val footerView: View? get() = footerViewRef.get()

    /** 内容视图 **/
    val contentView: View get() = contentViewRef.get()

    /** 刷新布局 **/
    val refreshLayout: SmartRefreshLayout? get() = refreshLayoutRef.get()

    /** 界面背景 */
    private val background: GetLiveData<Any?> by lazy {
        GetLiveData<Any?>().also {
            it.observe(provider as LifecycleOwner) { any ->
                // 常规设置背景
                val normalSet = { res: Any? ->
                    when (res) {
                        is Int -> rootView.setBackgroundResource(res)
                        is Drawable -> rootView.background = res
                        else -> rootView.background = null
                    }
                }
                if (provider is ComponentActivity) {
                    val isTranslucentOrFloating = _Get.isTranslucentOrFloating(provider)
                    if (isTranslucentOrFloating) normalSet(any)
                    else {
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


    override fun onCleared() {
        super.onCleared()
        bindingRef.set(null)
        ButterKnifeUtils.unbind(knifeUnBinderRef.get())
        System.gc()
    }

    /**
     * 确认[GetActivity.setContentView]被[setGetContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    fun ensureSetGetContentView() = synchronized(setGetContentViewRef) { setGetContentViewRef.set(true) }

    /**
     * 确认[GetActivity.setContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     */
    fun ensureSetNativeContentView() = synchronized(setGetContentViewRef) { setGetContentViewRef.set(false) }

    /**
     * 设置默认布局
     * @param id 布局Id
     */
    fun setGetContentView(@LayoutRes id: Int) = setGetContentView(Views.inflate(id))

    /**
     * 设置原生布局
     * @param id 布局Id
     */
    fun setNativeContentView(@LayoutRes id: Int) = setNativeContentView(Views.inflate(id))

    /**
     * 设置默认布局
     * @param view 布局
     */
    fun setGetContentView(view: View) {
        (view.parent as? ViewGroup)?.removeView(view)
        if (!setGetContentViewRef.get()) {
            setNativeContentView(view)
            return
        }
        // 移除历史界面
        removeContentView()
        // 确定是使用原始布局
        ensureSetNativeContentView()
        // 初始化刷新布局
        initRefreshLayout()
        // 初始化内容布局
        initContentView(view)
        // 初始化Get扩展控件
        initGetView()
    }

    /**
     * 设置原生布局
     * @param view 布局
     */
    fun setNativeContentView(view: View) {
        (view.parent as? ViewGroup)?.removeView(view)
        // 移除历史界面
        removeContentView()
        // 确定是使用原始布局
        ensureSetNativeContentView()
        // 初始化刷新布局
        initRefreshLayout()
        // 初始化内容布局
        initContentView(view)
        // 初始化Get扩展控件
        initGetView()
    }

    /**
     * 移除内容视图
     */
    private fun removeContentView() {
        // 移除历史内容布局
        contentViewRef.get()?.let {
            binding.getContent.removeView(it)
            synchronized(contentViewRef) { contentViewRef.set(null) }
        }
        // 移除刷新布局
        refreshLayoutRef.get()?.let {
            (it.parent as? ViewGroup)?.removeView(it)
            synchronized(refreshLayoutRef) { refreshLayoutRef.set(null) }
        }
    }

    /**
     * 设置顶部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    @JvmOverloads
    fun setHeaderView(@LayoutRes id: Int, floating: Boolean? = null) = setHeaderView(Views.inflate(id), floating)

    /**
     * 设置顶部视图
     * @param view 视图，为 null 则清除视图
     * @param floating 是否悬浮，为 null 则维持原状
     */
    @JvmOverloads
    fun setHeaderView(view: View?, floating: Boolean? = null) {
        // 添加或移除Header
        headerLayout.removeAllViews()
        view?.let { headerLayout.addView(it) }
        synchronized(headerViewRef) { headerViewRef.set(view) }
        // 初始化ButterKnife
        initUnBinder()
        // 更新Header位置
        floating?.let {
            val params = binding.getContent.layoutParams as RelativeLayout.LayoutParams
            // 悬浮状态，则移除BELOW属性
            if (it) params.removeRule(RelativeLayout.BELOW)
            else params.addRule(RelativeLayout.BELOW, R.id.get_header)
            // 请求更新
            binding.getContent.requestLayout()
        }
    }

    /**
     * 设置底部视图
     * @param id 视图资源ID
     * @param floating 是否悬浮
     */
    @JvmOverloads
    fun setFooterView(@LayoutRes id: Int, floating: Boolean? = null) = setFooterView(Views.inflate(id), floating)

    /**
     * 设置底部视图
     * @param view 视图
     * @param floating 是否悬浮
     */
    @JvmOverloads
    fun setFooterView(view: View?, floating: Boolean? = null) {
        // 添加或移除Footer
        footerLayout.removeAllViews()
        view?.let { footerLayout.addView(it) }
        synchronized(footerViewRef) { footerViewRef.set(view) }
        // 初始化ButterKnife
        initUnBinder()
        // 更新Header位置
        floating?.let {
            val params = binding.getContent.layoutParams as RelativeLayout.LayoutParams
            // 悬浮状态，则移除ABOVE属性
            if (it) params.removeRule(RelativeLayout.ABOVE)
            else params.addRule(RelativeLayout.ABOVE, R.id.get_footer)
            // 请求更新
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
    fun <T : View> findViewById(@IdRes resId: Int): T = rootView.findViewById(resId)!!

    /**
     * 设置背景
     * @param id 资源ID
     */
    fun setBackgroundResource(@DrawableRes id: Int) = id.also { background.setValue(it) }

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    fun setBackgroundColor(@ColorInt color: Int) = setBackground(ColorDrawable(color))

    /**
     * 设置背景
     * @param drawable 图像
     */
    fun setBackground(drawable: Drawable?) = drawable.also { background.setValue(it) }

    /** 设置内容界面 */
    private fun initContentView(view: View) {
        // 没有刷新布局，则直接添加到内容容器中
        if (refreshLayoutRef.get() == null) contentLayout.addView(view)
        // 否则添加到刷新容器中
        else refreshLayoutRef.get().setRefreshContent(view)

        // 绑定到Activity或者Fragment中
        if (provider is AppCompatActivity) {
            // 设置界面
            provider.delegate.setContentView(rootView)
        } else if (provider is ComponentActivity) {
            // 考虑到有可能重写了 setContentView
            // 所以需要临时改变 setGetContentView 的值
            setGetContentViewRef.get().let {
                // 暂时设置为False
                synchronized(setGetContentViewRef) { setGetContentViewRef.set(false) }
                // 设置界面
                provider.setContentView(rootView)
                // 重置为原始状态
                synchronized(setGetContentViewRef) { setGetContentViewRef.set(it) }
            }
        }
    }

    /**
     * 初始化界面相关控件
     */
    private fun initGetView() {
        // 没有刷新控件，则关联内容容器
        if (refreshLayoutRef.get() == null) stateDelegate.attachLayout(contentLayout)
        // 否者关联刷新控件
        else stateDelegate.attachLayout(refreshLayoutRef.get())
        // 设置Fragment背景
        if (provider is Fragment) background.setValue(Get.init.fragmentBackground)
        // 初始化标题
        initToolbar()
        // 初始化ButterKnife
        initUnBinder()
    }

    /** 初始化标题 **/
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
            visibility = if (setGetContentViewRef.get()) View.VISIBLE else View.GONE
            setToolbarInit(Get.init.toolbarInit)
            //TODO 这里有个未确定原因的BUG，从RecyclerView项启动的Fragment，标题栏无法点击
            //无法知道为什么，暂时没有精力去研究，暂时通过以下方式可处理BUG
            postDelayed({
                dividerView.bringToFront()
                titleView.bringToFront()
                backView.bringToFront()
                actionLayout.bringToFront()
            }, 120)
        }

    }

    /** 初始化刷新视图 **/
    private fun initRefreshLayout() {
        if (setGetContentViewRef.get()) {
            synchronized(refreshLayoutRef.get()) {
                val layout = SmartRefreshLayout(context).also {
                    it.overScrollMode = View.OVER_SCROLL_NEVER
                    it.setEnableLoadMore(false)
                    it.setEnableOverScrollDrag(true)
                    it.setEnablePureScrollMode(true)
                    it.setEnableScrollContentWhenRefreshed(false)
                    // SetGetContentView布局，则添加视图
                    contentLayout.addView(it)
                }
                refreshLayoutRef.set(layout)
            }
        }
    }

    /** 初始化ButterKnife */
    private fun initUnBinder() {
        ButterKnifeUtils.unbind(knifeUnBinderRef.get())
        synchronized(knifeUnBinderRef) {
            knifeUnBinderRef.set(ButterKnifeUtils.bind(provider, rootView))
        }
    }
}