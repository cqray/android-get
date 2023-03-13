package cn.cqray.android.ui.line

import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
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
open class LineAdapter : BaseMultiItemQuickAdapter<LineItem<*>, BaseViewHolder>() {

    init {
        addItemType(LineItem.BUTTON, R.layout.get_line_item_button)
        addItemType(LineItem.ICON, R.layout.get_line_item_text)
        addItemType(LineItem.TEXT, R.layout.get_line_item_text)
    }

    fun getItemByTag(tag: Any): LineItem<*>? {
        val data: List<LineItem<*>> = data
        for (item in data) {
            if (tag is String && tag == item.tag) {
                return item
            } else if (tag === item.tag) {
                return item
            }
        }
        return null
    }

    protected override fun convert(holder: BaseViewHolder, item: LineItem<*>) {
        // 设置ItemView
        convertItemView(holder, item)
        when (item.itemType) {
            LineItem.BUTTON ->                 // 按钮布局
                convertButton(holder, item as ButtonLineItem<*>)
            LineItem.TEXT ->                 // 文本布局
                convertText(holder, item as TextLineItem)
            LineItem.ICON -> {}
            else -> {}
        }
    }

    /**
     * 转换成按钮布局
     * @param holder ViewHolder
     * @param item 按钮行
     */
    protected fun convertButton(holder: BaseViewHolder, item: ButtonLineItem<*>) {
        val btn = holder.getView<TextView>(R.id._ui_item_btn)
        btn.text = item.text
        btn.setTextColor(item.textColor)
        btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
    }

    /**
     * 转换成通用布局
     * @param holder ViewHolder
     * @param item 按钮行
     */
    protected fun convertText(holder: BaseViewHolder, item: TextLineItem) {
        val icon = holder.getView<ImageView>(R.id._ui_item_icon)
        val next = holder.getView<ImageView>(R.id._ui_item_next)
        val left = holder.getView<TextView>(R.id._ui_item_left)
        val right = holder.getView<TextView>(R.id._ui_item_right)

        // 左端文本信息
        left.text = item.text
        left.setTextColor(item.textColor)
        left.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.textSize)
        left.typeface = Typeface.defaultFromStyle(item.textStyle)
        // 右端文本信息
        right.text = item.endText
        right.setTextColor(item.endTextColor)
        right.setTextSize(TypedValue.COMPLEX_UNIT_PX, item.endTextSize)
        right.typeface = Typeface.defaultFromStyle(item.endTextStyle)
        right.hint = item.endHint
        right.setHintTextColor(item.endHintColor)

        // 左边部分
        var ivParams: MarginLayoutParams = icon.layoutParams as MarginLayoutParams
        var tvParams: MarginLayoutParams = left.layoutParams as MarginLayoutParams
        if (item.icon == null) {
            icon.setImageDrawable(null)
            ivParams.leftMargin = 0
            tvParams.leftMargin = item.paddings[0].toInt()
        } else {
            icon.setImageResource(item.icon!!)
            ivParams.leftMargin = item.paddings[0].toInt()
            tvParams.leftMargin = item.paddings[0].toInt() / 2
        }
        icon.requestLayout()
        left.requestLayout()
        // 右边部分
        ivParams = next.layoutParams as MarginLayoutParams
        tvParams = right.layoutParams as MarginLayoutParams
        if (item.next == null) {
            next.setImageDrawable(null)
            ivParams.rightMargin = 0
            tvParams.rightMargin = item.paddings[2].toInt()
        } else {
            next.setImageResource(item.next!!)
            ivParams.rightMargin = item.paddings[2].toInt()
            tvParams.rightMargin = item.paddings[2].toInt() / 2
        }
        next.requestLayout()
        right.requestLayout()
    }

    /**
     * 转换成行布局
     * @param holder ViewHolder
     * @param item 通用行
     */
    protected fun convertItemView(holder: BaseViewHolder, item: LineItem<*>) {
        //        // 设置背景
//        holder.itemView.setBackgroundResource(item.getBackgroundRes())
        // 设置外间隔
        var params: MarginLayoutParams = holder.itemView.layoutParams as MarginLayoutParams
        // 设置行高
        params.height = item.height.toInt()
        // 设置外间隔
        params.marginStart = item.margins[0].toInt()
        params.marginEnd = item.margins[2].toInt()
        params.topMargin = item.margins[1].toInt()
        params.bottomMargin = item.margins[3].toInt()

        // 设置分割线
//        val dividerMargin: IntArray = item.getDividerMargin()
        val divider = holder.getView<View>(R.id._ui_item_divider)
        // 设置分割线颜色
        divider.setBackgroundColor(item.dividerColor)
        params = divider.layoutParams as MarginLayoutParams
        // 设置分割高
        params.height = item.dividerHeight.toInt()
        // 设置外间隔
        params.marginStart = item.dividerMargins[0].toInt()
        params.marginEnd = item.dividerMargins[2].toInt()
        params.topMargin = item.dividerMargins[1].toInt()
        params.bottomMargin = item.dividerMargins[3].toInt()

    }
}