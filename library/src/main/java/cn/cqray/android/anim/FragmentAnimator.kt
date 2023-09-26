package cn.cqray.android.anim

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes

/**
 * Fragment动画
 * @author Cqray
 */
open class FragmentAnimator
/**
 * @param enter A跳转B时，B的动画
 * @param exit A跳转B时，A的动画
 * @param popEnter A跳转B后，后退到A，A的动画
 * @param popExit A跳转B后，后退到A，B的动画
 */(
    /** A跳转B时，B的动画  */
    @field:AnimatorRes @field:AnimRes @param:AnimRes @param:AnimatorRes val enter: Int,
    /** A跳转B时，A的动画  */
    @field:AnimatorRes @field:AnimRes @param:AnimRes @param:AnimatorRes val exit: Int,
    /** A跳转B后，后退到A，A的动画  */
    @field:AnimatorRes @field:AnimRes @param:AnimRes @param:AnimatorRes val popEnter: Int,
    /** A跳转B后，后退到A，B的动画  */
    @field:AnimatorRes @field:AnimRes @param:AnimRes @param:AnimatorRes val popExit: Int
)