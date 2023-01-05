package cn.cqray.android.tip

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.cqray.android.Get
import cn.cqray.android.app.GetDelegate
import cn.cqray.android.app.GetUtils

class GetTipDelegate(provider: GetTipProvider) : GetDelegate<GetTipProvider>(provider) {

    init {
        // 检查Provider是否合法
        GetUtils.checkProvider(provider)
        // 加入缓存
        cacheDelegates[provider] = this
        // 资源回收事件订阅
        (provider as LifecycleOwner).lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                // 从缓存中移除
                cacheDelegates.remove(provider)
            }
        })
    }

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     */
    fun showTip(text: CharSequence?) = showTip(
        level = null,
        text = text,
        init = null,
        callback = null
    )

    /**
     * 显示Tip
     * @param text     文本内容 [CharSequence]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(text: CharSequence?, callback: GetTipCallback? = null) = showTip(
        level = null,
        text = text,
        init = null,
        callback = callback
    )

    /**
     * 显示Tip
     * @param level    提示等级 [TipLevel]
     * @param text     文本内容 [CharSequence]
     * @param init 配置属性 [TipInit]
     * @param callback 结束回调 [GetTipCallback]
     */
    fun showTip(
        level: TipLevel?,
        text: CharSequence?,
        init: TipInit?,
        callback: GetTipCallback?
    ) {
        val defInit = Get.init.tipInit!!
        val defAdapter = init?.tipAdapter ?: defInit.tipAdapter
        val newAdapter = defAdapter ?: TipAdapterImpl()
        newAdapter.show(this, level, text, init, callback)
    }

    companion object {

        /** 委托缓存 [GetTipDelegate] **/
        private val cacheDelegates = HashMap<GetTipProvider, GetTipDelegate>()

        /**
         * 获取并初始化[GetTipDelegate]
         * @param provider [GetTipProvider]实现实例
         */
        @JvmStatic
        @Synchronized
        fun get(provider: GetTipProvider): GetTipDelegate = cacheDelegates[provider] ?: GetTipDelegate(provider)
    }
}