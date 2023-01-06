package cn.cqray.android.anim;

import cn.cqray.android.R;

/**
 * 默认纵向动画
 * @author Cqray
 */
public class DefaultVerticalAnimator extends FragmentAnimator {

    public DefaultVerticalAnimator() {
        super(R.anim._starter_vertical_to_top,
                R.anim._starter_vertical_from_top,
                R.anim._starter_vertical_to_top,
                R.anim._starter_vertical_from_top);
    }
}
