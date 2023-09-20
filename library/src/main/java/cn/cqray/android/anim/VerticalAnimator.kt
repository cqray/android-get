package cn.cqray.android.anim

import cn.cqray.android.R

/**
 * 默认纵向动画
 * @author Cqray
 */
@Suppress("unused")
class VerticalAnimator : FragmentAnimator(
    R.anim.get_vertical_to_top,
    R.anim.get_vertical_from_top,
    R.anim.get_vertical_to_top,
    R.anim.get_vertical_from_top
), java.io.Serializable