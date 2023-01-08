package cn.cqray.android.app.delegate

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log

import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import cn.cqray.android.R
import cn.cqray.android.app.*
import cn.cqray.android.app.GetUtils
import cn.cqray.android.app.provider.GetNavProvider
import cn.cqray.android.app.provider.GetViewProvider
import cn.cqray.android.exc.ExceptionDispatcher
import cn.cqray.android.exc.ExceptionType
import cn.cqray.android.state.StateDelegate
import cn.cqray.android.state.StateProvider
import cn.cqray.android.util.ActivityUtils
import cn.cqray.android.util.ButterKnifeUtils
import cn.cqray.android.util.ContextUtils.inflate
import cn.cqray.android.widget.Toolbar
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.util.*
import kotlin.collections.HashMap

/**
 * 界面布局代理
 * @author Cqray
 */
@Suppress("unused")
class GetViewDelegate(provider: GetViewProvider) :
    GetDelegate<GetViewProvider>(provider) {

    init {
//        // 验证 ViewProvider 是否继承相关Fragment、Activity
//        GetUtils.checkProvider(provider)
//        // 缓存 ViewDelegate
//        cacheDelegates[provider] = this
//        // 资源回收事件订阅
//        (provider as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver {
//            override fun onDestroy(owner: LifecycleOwner) {
//                super.onDestroy(owner)
//                // 从缓存中移除
//                cacheDelegates.remove(provider)
//                // 确保资源回收时间晚于 Fragment、Activity 的 onDestroy
//                GetManager.runOnUiThreadDelayed({ onCleared() }, 0)
//            }
//        })
        // 初始化状态委托
        if (provider is StateProvider) StateDelegate(provider).also { stateDelegate = it }
    }

    /** 是否设置Get扩展界面 **/
    private var setGetContentView = true

    /** 根控件 **/
    @Suppress("unused")
    var rootView: View? = null
        private set

    /** 内容控件 **/
    @Suppress("unused")
    var contentView: View? = null
        private set

    /** 标题 **/
    @Suppress("unused")
    private var toolbar: Toolbar? = null

    /** 刷新控件 **/
    @Suppress("unused")
    var refreshLayout: SmartRefreshLayout? = null
        private set

    /** 头部容器 **/
    private var mHeaderLayout: FrameLayout? = null

    /** 底部容器 **/
    private var mFooterLayout: FrameLayout? = null

    /** ButterKnife绑定 **/
    private var mUnBinder: Any? = null

    /** Fragment、Activity背景 **/
    private var mBackground: MutableLiveData<Any?>? = null

//    /** Handler控制 **/
//    private var mHandler = Handler(Looper.getMainLooper())

    private var stateDelegate: StateDelegate? = null

    /**
     * 上下文
     **/
    val context: Context
        get() = if (provider is FragmentActivity) {
            provider
        } else {
            (provider as Fragment).requireContext()
        }

    /**
     * 确认[setGetContentView]不被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     **/
    fun ensureSetGetContentView(): () -> Unit = { setGetContentView = true }

    /**
     * 确认[setGetContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     **/
    fun ensureSetNativeContentView(): () -> Unit = { setGetContentView = false }

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
        if (!setGetContentView) {
            setNativeContentView(view)
            return
        }
        contentView = view
        rootView = inflate(R.layout.starter_layout_default)
        rootView?.let {
            toolbar = it.findViewById(R.id.starter_toolbar)
            mHeaderLayout = it.findViewById(R.id.starter_header_layout)
            mFooterLayout = it.findViewById(R.id.starter_footer_layout)
            refreshLayout = it.findViewById(R.id.starter_refresh_layout)
            refreshLayout?.addView(view)
        }
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
        setGetContentView = false
        contentView = view
        rootView = inflate(R.layout.starter_layout_native)
        (rootView as FrameLayout).addView(view)
        toolbar = view.findViewById(R.id.starter_toolbar)
        mHeaderLayout = view.findViewById(R.id.starter_header_layout)
        mFooterLayout = view.findViewById(R.id.starter_footer_layout)
        refreshLayout = view.findViewById(R.id.starter_refresh_layout)
        initContentView()
        initGetView()
    }

    /**
     * 设置头部布局
     * @param id 布局ID
     **/
    fun setHeaderView(@LayoutRes id: Int) = setHeaderView(inflate(id))

    /**
     * 设置头部布局
     * @param view 布局
     **/
    fun setHeaderView(view: View?) {
        if (mHeaderLayout == null) {
            ExceptionDispatcher.dispatchThrowable(provider, ExceptionType.HEADER_UNSUPPORTED)
        } else {
            mHeaderLayout!!.removeAllViews()
            mHeaderLayout!!.addView(view)
            initUnBinder()
        }
    }

    /**
     * 设置头部布局悬浮
     * @param floating 是否悬浮
     **/
    fun setHeaderFloating(floating: Boolean) {
        if (mHeaderLayout == null) {
            ExceptionDispatcher.dispatchThrowable(provider, ExceptionType.HEADER_UNSUPPORTED)
        } else {
            val params = refreshLayout!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, if (floating) R.id.toolbar else R.id.header_layout)
            refreshLayout!!.requestLayout()
        }
    }

    /**
     * 设置底部布局
     * @param id 布局ID
     **/
    fun setFooterView(@LayoutRes id: Int) = setFooterView(inflate(id))

    /**
     * 设置底部布局
     * @param view 布局
     **/
    fun setFooterView(view: View?) {
        if (mFooterLayout == null) {
            ExceptionDispatcher.dispatchThrowable(provider, ExceptionType.FOOTER_UNSUPPORTED)
        } else {
            mFooterLayout!!.removeAllViews()
            mFooterLayout!!.addView(view)
            initUnBinder()
        }
    }

    /**
     * 设置底部布局悬浮
     * @param floating 是否悬浮
     **/
    fun setFooterFloating(floating: Boolean) {
        if (mFooterLayout == null) {
            ExceptionDispatcher.dispatchThrowable(provider, ExceptionType.FOOTER_UNSUPPORTED)
        } else {
            val params = refreshLayout!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ABOVE, if (floating) 0 else R.id.footer_layout)
            refreshLayout!!.requestLayout()
        }
    }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     **/
    fun <T : View> findViewById(@IdRes resId: Int): T? = contentView?.findViewById(resId)

    /**
     * 获取[Toolbar]，NULL会抛出异常
     */
    fun requireToolbar(): Toolbar = when (provider) {
        is GetActivity -> provider.toolbar
        is GetFragment -> provider.toolbar
        else -> toolbar
    } ?: throw RuntimeException("Please make sure toolbar is exist.")

    /**
     * 获取[SmartRefreshLayout]，NULL会抛出异常
     */
    fun requireRefreshLayout(): SmartRefreshLayout = when (provider) {
        is GetActivity -> provider.refreshLayout
        is GetFragment -> provider.refreshLayout
        else -> refreshLayout
    } ?: throw RuntimeException("Please make sure refreshLayout is exist.")

    /**
     * 获取内容布局，NULL会抛出异常
     */
    fun requireContentView(): View =
        contentView ?: throw RuntimeException("Please make sure contentView is exist.")

    /**
     * 设置背景
     * @param resId 资源ID
     **/
    fun setBackgroundResource(@DrawableRes resId: Int) =
        setBackground(ContextCompat.getDrawable(context, resId))

    /**
     * 设置背景颜色
     * @param color 颜色
     **/
    fun setBackgroundColor(color: Int) = setBackground(ColorDrawable(color))

    /**
     * 设置背景颜色
     * @param drawable 图像
     **/
    fun setBackground(drawable: Drawable) = setBackground(drawable as Any?)

    /**
     * 设置背景
     * @param background 背景
     **/
    @Synchronized
    private fun setBackground(background: Any?) {
        if (mBackground == null) {
            mBackground = MutableLiveData()
            mBackground!!.observe(provider as LifecycleOwner, { any: Any? ->
                if (provider is ComponentActivity) {
                    val isTranslucentOrFloating = ActivityUtils.isTranslucentOrFloating(provider)
                    if (isTranslucentOrFloating && rootView != null) {
                        // 根据内容设置背景
                        when (any) {
                            is Int -> rootView!!.setBackgroundResource(any)
                            is Drawable -> rootView!!.background = any
                            else -> rootView!!.background = null
                        }
                        Log.e("数据", "88888888888888888")
                    } else if (!isTranslucentOrFloating) {
                        // 根据内容设置背景
                        when (any) {
                            is Int -> provider.window.setBackgroundDrawableResource(any)
                            is Drawable -> provider.window.setBackgroundDrawable(any)
                            else -> provider.window.setBackgroundDrawable(null)
                        }
                        Log.e("数据", "777777777777777777")
                    }
                } else if (rootView != null) {
                    // 根据内容设置背景
                    when (any) {
                        is Int -> rootView!!.setBackgroundResource(any)
                        is Drawable -> rootView!!.background = any
                        else -> rootView!!.background = null
                    }
                }
            })
        }
        mBackground!!.value = background
    }


    /** 清理资源 **/
    protected override fun onCleared() {
        ButterKnifeUtils.unbind(mUnBinder)
        refreshLayout = null
        mHeaderLayout = null
        mFooterLayout = null
        toolbar = null
        contentView = null
        rootView = null
        System.gc()
    }

    /** 设置内容界面 **/
    private fun initContentView() {
        if (provider is AppCompatActivity) {
            provider.delegate.setContentView(rootView)
            Log.e("数据u", "施工方")
        } else if (provider is ComponentActivity) {
            // 考虑到有可能重写了 setContentView
            // 所以需要临时改变 mSetGetContentView 的值
            val tmp = setGetContentView
            setGetContentView = false
            provider.setContentView(rootView)
            setGetContentView = tmp
        }
    }

    /** 初始化ButterKnife **/
    private fun initUnBinder() {
        if (isRootViewExist) {
            ButterKnifeUtils.unbind(mUnBinder)
            mUnBinder = ButterKnifeUtils.bind(provider, rootView!!)
        }
    }

    /**
     * 初始化界面相关控件
     **/
    private fun initGetView() {
        if (provider is GetActivity) {
            toolbar?.let { provider.toolbar = it }
            refreshLayout?.let { provider.refreshLayout = it }
        } else if (provider is GetFragment) {
            toolbar?.let { provider.toolbar = it }
            refreshLayout?.let { provider.refreshLayout = it }
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

    fun initToolbar() {
        // 初始化标题栏监听事件
        if (toolbar != null && provider is GetNavProvider) {
            toolbar!!.setBackListener {
                val delegate = (provider as GetNavProvider).navDelegate
                delegate.back()
            }
        }

//        val strategy = Starter.getInstance().starterStrategy
//        if (mToolbar != null) {
//            mToolbar!!.setUseRipple(strategy.isToolbarUserRipple).background =
//                strategy.toolbarBackground
//            // 设置标题栏标题属性
//            mToolbar!!.setTitleCenter(strategy.isToolbarTitleCenter)
//                .setTitleTextColor(strategy.toolbarTitleTextColor)
//                .setTitleTextSize(strategy.toolbarTitleTextSize)
//                .setTitleTypeface(strategy.toolbarTitleTypeface)
//                .setTitleSpace(strategy.toolbarTitleSpace)
//            // 设置标题栏返回控件属性
//            mToolbar!!.setBackText(strategy.toolbarBackText)
//                .setBackIcon(strategy.toolbarBackIcon)
//                .setBackTextColor(strategy.toolbarBackTextColor)
//                .setBackTextSize(strategy.toolbarBackTextSize)
//                .setBackTypeface(strategy.toolbarBackTypeface)
//            if (strategy.toolbarBackIconTintColor != null) {
//                mToolbar!!.setBackIconTintColor(strategy.toolbarBackIconTintColor)
//            }
//            // 设置标题栏Action控件属性
//            mToolbar!!.setDefaultActionTextColor(strategy.toolbarActionTextColor)
//                .setDefaultActionTextSize(strategy.toolbarActionTextSize)
//                .setDefaultActionTypeface(strategy.toolbarActionTypeface)
//            // 设置标题栏分割线属性
//            mToolbar!!.setDividerColor(strategy.toolbarDividerColor)
//                .setDividerHeight(strategy.toolbarDividerHeight)
//                .setDividerMargin(strategy.toolbarDividerMargin)
//                .setDividerVisible(strategy.isToolbarDividerVisible)
//        }
    }

    /**
     * RootView是否存在
     **/
    private val isRootViewExist: Boolean
        get() {
            if (rootView == null) {
                ExceptionDispatcher.dispatchThrowable(
                    provider,
                    ExceptionType.CONTENT_VIEW_NULL
                )
                return false
            }
            return true
        }

//    companion object {
//        /** 委托缓存 [GetNavDelegate] **/
//        private val cacheDelegates =
//            Collections.synchronizedMap(HashMap<GetViewProvider, GetViewDelegate>())
//
//        @JvmStatic
//        fun get(provider: GetViewProvider): GetViewDelegate =
//            cacheDelegates[provider] ?: GetViewDelegate(provider)
//    }
}