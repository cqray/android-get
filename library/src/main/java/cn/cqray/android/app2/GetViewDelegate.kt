package cn.cqray.android.app2

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import cn.cqray.android.R
import cn.cqray.android.exception.ExceptionDispatcher
import cn.cqray.android.exception.ExceptionType
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
class GetViewDelegate(private val provider: GetViewProvider) {

    init {
        // 验证 ViewProvider 是否继承相关Fragment、Activity
        GetUtils.checkProvider(provider)
        // 缓存 ViewDelegate
        mViewDelegates[provider] = this
        // 结束生命周期监听
        (provider as LifecycleOwner).lifecycle.addObserver(
            LifecycleEventObserver { _: LifecycleOwner, event: Lifecycle.Event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // 确保资源回收时间晚于 Fragment、Activity 的 onDestroy
                    mHandler.post { onCleared() }
                }
            })
//        val strategy = Starter.getInstance().starterStrategy
//        if (strategy.activityBackground != null) {
//            setBackground(strategy.activityBackground)
//        }
    }

    /** 是否设置Get扩展界面 **/
    private var mSetGetContentView = true

    /** 根控件 **/
    var mRootView: View? = null
        private set

    /** 内容控件 **/
    private var mContentView: View? = null

    /** 标题 **/
    private var mToolbar: Toolbar? = null

    /** 刷新控件 **/
    private var mRefreshLayout: SmartRefreshLayout? = null

    /** 头部容器 **/
    private var mHeaderLayout: FrameLayout? = null

    /** 底部容器 **/
    private var mFooterLayout: FrameLayout? = null

    /** ButterKnife绑定 **/
    private var mUnBinder: Any? = null

    /** Fragment、Activity背景 **/
    private var mBackground: MutableLiveData<Any?>? = null

    /** Handler控制 **/
    private var mHandler = Handler(Looper.getMainLooper())

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
    fun ensureSetGetContentView() {
        mSetGetContentView = true
    }

    /**
     * 确认[setGetContentView]被[setNativeContentView]替代
     * 主要是考虑到以后可能兼容AndroidX Compose框架
     **/
    fun ensureSetNativeContentView() {
        mSetGetContentView = false
    }

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
        if (!mSetGetContentView) {
            setNativeContentView(view)
            return
        }
        mContentView = view
        mRootView = inflate(R.layout.starter_layout_default)
        mToolbar = view.findViewById(R.id.starter_toolbar)
        mHeaderLayout = view.findViewById(R.id.starter_header_layout)
        mFooterLayout = view.findViewById(R.id.starter_footer_layout)
        mRefreshLayout = view.findViewById(R.id.starter_refresh_layout)
        assert(mRefreshLayout != null)
        mRefreshLayout!!.addView(view)
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
        mSetGetContentView = false
        mContentView = view
        mRootView = inflate(R.layout.starter_layout_native)
        mToolbar = view.findViewById(R.id.starter_toolbar)
        mHeaderLayout = view.findViewById(R.id.starter_header_layout)
        mFooterLayout = view.findViewById(R.id.starter_footer_layout)
        mRefreshLayout = view.findViewById(R.id.starter_refresh_layout)
        (mRootView as FrameLayout?)!!.addView(view)
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
            val params = mRefreshLayout!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, if (floating) R.id.toolbar else R.id.header_layout)
            mRefreshLayout!!.requestLayout()
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
            val params = mRefreshLayout!!.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.ABOVE, if (floating) 0 else R.id.footer_layout)
            mRefreshLayout!!.requestLayout()
        }
    }

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
                    if (isTranslucentOrFloating && mRootView != null) {
                        // 根据内容设置背景
                        when (any) {
                            is Int -> mRootView!!.setBackgroundResource(any)
                            is Drawable -> mRootView!!.background = any
                            else -> mRootView!!.background = null
                        }
                    } else if (!isTranslucentOrFloating) {
                        // 根据内容设置背景
                        when (any) {
                            is Int -> provider.window.setBackgroundDrawableResource(any)
                            is Drawable -> provider.window.setBackgroundDrawable(any)
                            else -> provider.window.setBackgroundDrawable(null)
                        }
                    }
                } else if (mRootView != null) {
                    // 根据内容设置背景
                    when (any) {
                        is Int -> mRootView!!.setBackgroundResource(any)
                        is Drawable -> mRootView!!.background = any
                        else -> mRootView!!.background = null
                    }
                }
            })
        }
        mBackground!!.value = background
    }

//    fun setIdle() {
//        mStateDelegate.setIdle()
//    }
//
//    fun setBusy(vararg texts: String?) {
//        mStateDelegate.setBusy(*texts)
//    }
//
//    fun setEmpty(vararg texts: String?) {
//        mStateDelegate.setEmpty(*texts)
//    }
//
//    fun setError(vararg texts: String?) {
//        mStateDelegate.setError(*texts)
//    }
//
//    fun setState(state: ViewState?, vararg texts: String?) {
//        mStateDelegate.setState(state, *texts)
//    }

    /**
     * 查找控件
     * @param resId 控件Id
     * @param <T> 控件类型
     **/
    fun <T : View> findViewById(@IdRes resId: Int): T? {
        return if (isRootViewExist) mRootView!!.findViewById(resId) else null
    }

    /** 清理资源 **/
    private fun onCleared() {
        ButterKnifeUtils.unbind(mUnBinder)
        mHandler.removeCallbacksAndMessages(null)
        mRefreshLayout = null
        mHeaderLayout = null
        mFooterLayout = null
        mToolbar = null
        mContentView = null
        mRootView = null
        mViewDelegates.remove(provider)
        System.gc()
    }

    /** 设置内容界面 **/
    private fun initContentView() {
        if (provider is AppCompatActivity) {
            provider.delegate.setContentView(mRootView)
        } else if (provider is ComponentActivity) {
            // 考虑到有可能重写了 setContentView
            // 所以需要临时改变 mSetGetContentView 的值
            val tmp = mSetGetContentView
            mSetGetContentView = false
            provider.setContentView(mRootView)
            mSetGetContentView = tmp
        }
    }

    /** 初始化ButterKnife **/
    private fun initUnBinder() {
        if (isRootViewExist) {
            ButterKnifeUtils.unbind(mUnBinder)
            mUnBinder = ButterKnifeUtils.bind(provider, mRootView!!)
        }
    }

    /**
     * 初始化界面相关控件
     **/
    private fun initGetView() {
        if (provider is GetActivity) {
            provider.mToolbar = mToolbar
            provider.mContentView = mContentView
            provider.mRefreshLayout = mRefreshLayout
        } else if (provider is GetFragment) {
            provider.mToolbar = mToolbar
            provider.mContentView = mContentView
            provider.mRefreshLayout = mRefreshLayout
        }
//        if (mRefreshLayout != null) {
//            mStateDelegate.attachLayout(mRefreshLayout)
//        } else if (mRootView is FrameLayout) {
//            mStateDelegate.attachLayout(mRootView as FrameLayout?)
//        }

        // 初始化标题
        initToolbar()
        // 初始化ButterKnife
        initUnBinder()
    }

    fun initToolbar() {
        // 初始化标题栏监听事件
        if (mToolbar != null && provider is GetNavProvider) {
            mToolbar!!.setBackListener {
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
            if (mRootView == null) {
                ExceptionDispatcher.dispatchThrowable(
                    provider,
                    ExceptionType.CONTENT_VIEW_NULL
                )
                return false
            }
            return true
        }

    companion object {
        /** 委托缓存 [GetNavDelegate] **/
        private val mViewDelegates =
            Collections.synchronizedMap(HashMap<GetViewProvider, GetViewDelegate>())

        @JvmStatic
        fun get(provider: GetViewProvider): GetViewDelegate {
            var delegate = mViewDelegates[provider]
            synchronized(GetViewDelegate::class.java) {
                if (delegate == null) {
                    delegate = GetViewDelegate(provider)
                }
            }
            return delegate!!
        }
    }
}