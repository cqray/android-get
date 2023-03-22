package cn.cqray.android.exc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import cn.cqray.android.Get
import cn.cqray.android.R
import cn.cqray.android.app.GetActivity
import cn.cqray.android.util.Sizes

/**
 * 问题展示界面
 * @author Cqray
 */
class GetExcActivity : GetActivity() {
    
    private var introText: String? = null
    private var sourceText: String? = null
    private var throwable: Throwable? = null

    fun dispatchThrowable(source: Any?, intro: String?, throwable: Throwable?) {
        val intent = Intent()
        var context: Context? = Get.topActivity
        // 如果没有获取到Activity
        if (context == null) {
            // 则取全局Context
            context = Get.context
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        intent.putExtra("intro", intro)
        intent.putExtra("throwable", throwable)
        intent.putExtra("source", source?.javaClass?.name)
        intent.component = ComponentName(context, GetExcActivity::class.java)
        context.startActivity(intent)
    }
    
    override fun onCreating(savedInstanceState: Bundle?) {
        super.onCreating(savedInstanceState)
        setContentView(R.layout.get_activity_layout_exc)
        introText = intent.getStringExtra("intro")
        sourceText = intent.getStringExtra("source")
        throwable = intent.getSerializableExtra("throwable") as Throwable?
        notifyViewChanged()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        introText = intent.getStringExtra("intro")
        sourceText = intent.getStringExtra("source")
        throwable = intent.getSerializableExtra("throwable") as Throwable?
        notifyViewChanged()
    }

    private fun notifyViewChanged() {
        // 获取控件
        val desc = findViewById<TextView>(R.id._starter_desc)
        val intro = findViewById<TextView>(R.id._starter_intro)
        val source = findViewById<TextView>(R.id._starter_source)
        // 设置相关文本内容
        desc.text = if (throwable == null) null else throwable!!.message
        intro.text = introText
        source.text = String.format("问题发生在[%s]", sourceText)
        source.visibility = if (TextUtils.isEmpty(sourceText)) View.GONE else View.VISIBLE
        // 初始化分割线
        initDivider(findViewById(R.id._starter_divider1))
        initDivider(findViewById(R.id._starter_divider2))
        if (throwable is GetException) {
            toolbar!!.setTitle("启动相关问题")
        } else {
            toolbar!!.setTitle("其他问题")
        }
    }

    /**
     * 初始化分割线
     * @param view 分割线控件
     */
    private fun initDivider(view: View) {
        // 设置虚线背景
        val size = Sizes.smaller()
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val background = GradientDrawable()
        background.shape = GradientDrawable.LINE
        //        background.setStroke(1, ColorUtils.getColor(R.color.tint), size, size);
        ViewCompat.setBackground(view, background)
        // 设置显示与否
        if (view.id == R.id._starter_divider1) {
            view.visibility = if (TextUtils.isEmpty(sourceText)) View.GONE else View.VISIBLE
        } else if (view.id == R.id._starter_divider2) {
            view.visibility = if (throwable == null) View.GONE else View.VISIBLE
        }
    }
    
}