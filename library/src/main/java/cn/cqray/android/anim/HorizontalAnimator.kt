package cn.cqray.android.anim

import cn.cqray.android.R

/**
 * 默认横向动画
 * @author Cqray
 */
@Suppress("unused")
class HorizontalAnimator : FragmentAnimator(
    R.anim.get_horizontal_from_right,
    R.anim.get_horizontal_to_left,
    R.anim.get_horizontal_from_left,
    R.anim.get_horizontal_to_right
)