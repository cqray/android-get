package cn.cqray.android.app

import androidx.viewbinding.ViewBinding
import cn.cqray.android.databinding.GetDefaultLayoutBinding
import cn.cqray.android.util.Contexts

/**
 * 界面实现委托
 * @author Cqray
 */
class GetViewDelegate {

    /** [ViewBinding]实例 **/
    private val binding by lazy { GetDefaultLayoutBinding.inflate(Contexts.layoutInflater) }

    /** 根控件 */
    val root get() = binding.root

    /** 标题容器 */
    val toolbarLayout get() = binding.getToolbar

    /** 标题 **/
    val titleBar get() = binding.getTitle

    /** 内容容器 **/
    val contentLayout get() = binding.getContent

    /** 头部容器 */
    val headerLayout get() = binding.getHeader

    /** 底部容器 */
    val footerLayout get() = binding.getFooter
}