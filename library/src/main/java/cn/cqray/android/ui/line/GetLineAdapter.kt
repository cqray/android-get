package cn.cqray.android.ui.line

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import cn.cqray.android.R
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 行适配器
 * @author Cqray
 */
@Suppress(
    "MemberVisibilityCanBePrivate",
    "Unchecked_cast",
    "Unused"
)
open class GetLineAdapter : BaseMultiItemQuickAdapter<GetLineItem<*>, BaseViewHolder>() {

    init {
        addItemType(GetLineItem.BUTTON, R.layout.get_line_item_button)
        addItemType(GetLineItem.ICON, R.layout.get_line_item_text)
        addItemType(GetLineItem.TEXT, R.layout.get_line_item_text)
    }

    fun getItemByTag(tag: Any?): GetLineItem<*>? {
        for (item in data) {
            val equal = item.tag?.equals(tag)
            if (equal == true) return item
        }
        return null
    }

    override fun convert(holder: BaseViewHolder, item: GetLineItem<*>) {
        // 设置ItemView
        convertItemView(holder, item)
        when (item.itemType) {
            // 按钮布局
            GetLineItem.BUTTON -> convertButton(holder, item as GetButtonLineItem<*>)
            // 文本布局
            GetLineItem.TEXT -> convertText(holder, item as GetTextLineItem)
            GetLineItem.ICON -> convertText(holder, item as GetTextLineItem)
        }
    }

    /**
     * 转换成按钮布局
     * @param holder ViewHolder
     * @param item 按钮行
     */
    protected fun convertButton(holder: BaseViewHolder, item: GetButtonLineItem<*>) {
        val btn = holder.getView<TextView>(R.id.get_item_button)
        btn.text = item.text
        btn.setTextColor(item.textColor)
        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
    }

    /**
     * 转换成通用布局
     * @param holder ViewHolder
     * @param item 按钮行
     */
    protected fun convertText(holder: BaseViewHolder, item: GetTextLineItem) {
        val icon = holder.getView<ImageView>(R.id.get_item_icon)
        val next = holder.getView<ImageView>(R.id.get_item_next)
        val start = holder.getView<TextView>(R.id.get_item_start_text)
        val end = holder.getView<TextView>(R.id.get_item_end_text)
        // 左端文本信息
        start.text = item.text
        start.setTextColor(item.textColor)
        start.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
        start.typeface = Typeface.defaultFromStyle(item.textStyle)
        // 右端文本信息
        end.text = item.endText
        end.setTextColor(item.endTextColor)
        end.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.endTextSize)
        end.typeface = Typeface.defaultFromStyle(item.endTextStyle)
        end.hint = item.endHint
        end.setHintTextColor(item.endHintColor)
        // 左边部分
        var ivParams: MarginLayoutParams = icon.layoutParams as MarginLayoutParams
        var tvParams: MarginLayoutParams = start.layoutParams as MarginLayoutParams
        if (item.icon == null) {
            icon.setImageDrawable(null)
            ivParams.leftMargin = 0
            tvParams.leftMargin = item.paddings[0]
        } else {
            icon.setImageDrawable(item.icon!!)
            ivParams.leftMargin = item.paddings[0]
            tvParams.leftMargin = item.paddings[0] / 2
        }
        icon.requestLayout()
        start.requestLayout()
        // 右边部分
        ivParams = next.layoutParams as MarginLayoutParams
        tvParams = end.layoutParams as MarginLayoutParams
        if (item.next == null) {
            next.setImageDrawable(null)
            ivParams.rightMargin = 0
            tvParams.rightMargin = item.paddings[2]
        } else {
            next.setImageDrawable(item.next!!)
            ivParams.rightMargin = item.paddings[2]
            tvParams.rightMargin = item.paddings[2] / 2
        }
        next.requestLayout()
        end.requestLayout()
    }

    /**
     * 转换成行布局
     * @param holder ViewHolder
     * @param item 通用行
     */
    protected fun convertItemView(holder: BaseViewHolder, item: GetLineItem<*>) {
        // 自定义背景
        ViewCompat.setBackground(holder.itemView, item.background)
        // 设置外间隔
        var params: MarginLayoutParams = holder.itemView.layoutParams as MarginLayoutParams
        // 设置行高
        params.height = item.height
        // 设置外间隔
        params.marginStart = item.margins[0]
        params.marginEnd = item.margins[2]
        params.topMargin = item.margins[1]
        params.bottomMargin = item.margins[3]
        // 设置分割线
        val divider = holder.getView<View>(R.id.get_item_divider)
        // 设置分割线颜色
        divider.setBackgroundColor(item.dividerColor)
        params = divider.layoutParams as MarginLayoutParams
        // 设置分割高
        params.height = item.dividerHeight
        // 设置外间隔
        params.marginStart = item.dividerMargins[0]
        params.marginEnd = item.dividerMargins[2]
        params.topMargin = item.dividerMargins[1]
        params.bottomMargin = item.dividerMargins[3]
    }
}