package cn.cqray.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.core.view.GravityCompat
import com.hjq.bar.TitleBar

class GetTitleBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TitleBar(context, attrs, defStyleAttr) {

    val actionLayout by lazy {
        GetActionLayout(context).also {
            this.addView(it)
            val params = layoutParams as LayoutParams
            params.gravity = GravityCompat.END or Gravity.CENTER_VERTICAL
        }
    }
}