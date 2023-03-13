package cn.cqray.android.ui.line;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import cn.cqray.android.R;

/**
 * 行适配器
 * @author Cqray
 */
public class LineAdapter extends BaseMultiItemQuickAdapter<LineItem<?>, BaseViewHolder> {

    public LineAdapter() {
        addItemType(LineItem.BUTTON, R.layout.starter_item_line_button);
        addItemType(LineItem.TEXT, R.layout.starter_item_line_text);
    }

    public LineItem<?> getItemByTag(Object tag) {
        List<LineItem<?>> data = getData();
        for (LineItem<?> item : data) {
            if (tag instanceof String && tag.equals(item.getTag())) {
                return item;
            } else if (tag == item.getTag()) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @NonNull LineItem<?> item) {
        // 设置ItemView
        convertItemView(holder, item);
        // 根据ItemType设置界面
        switch (item.getItemType()) {
            case LineItem.BUTTON:
                // 按钮布局
                convertButton(holder, (ButtonLineItem) item);
                break;
            case LineItem.TEXT:
                // 文本布局
                convertText(holder, (TextLineItem) item);
                break;
            case LineItem.ICON:

                break;
            default:
        }
    }

    /**
     * 转换成按钮布局
     * @param holder ViewHolder
     * @param item 按钮行
     */
    protected void convertButton(@NonNull BaseViewHolder holder, @NonNull ButtonLineItem item) {
        TextView btn = holder.getView(R.id._ui_item_btn);
        btn.setText(item.getText());
        btn.setTextColor(item.getTextColor());
        btn.setTextSize(item.getTextSize());
    }

    /**
     * 转换成通用布局
     * @param holder ViewHolder
     * @param item 按钮行
     */
    protected void convertText(@NonNull BaseViewHolder holder, @NonNull TextLineItem item) {
        ImageView icon = holder.getView(R.id._ui_item_icon);
        ImageView next = holder.getView(R.id._ui_item_next);
        TextView left = holder.getView(R.id._ui_item_left);
        TextView right = holder.getView(R.id._ui_item_right);
        // 设置文本
        left.setText(item.getText());
        left.setTextColor(item.getTextColor());
        left.setTextSize(item.getTextSize());
        right.setText(item.getRightText());
        right.setTextColor(item.getRightTextColor());
        right.setTextSize(item.getRightTextSize());

        ViewGroup.MarginLayoutParams ivParams;
        ViewGroup.MarginLayoutParams tvParams;
        // 左边部分
        ivParams = (ViewGroup.MarginLayoutParams) icon.getLayoutParams();
        tvParams = (ViewGroup.MarginLayoutParams) left.getLayoutParams();
        if (item.getIconRes() == 0) {
            icon.setImageDrawable(null);
            ivParams.leftMargin = 0;
            tvParams.leftMargin = item.getPadding()[0];
        } else {
            icon.setImageResource(item.getIconRes());
            ivParams.leftMargin = item.getPadding()[0];
            tvParams.leftMargin = item.getPadding()[0] / 2;
        }
        icon.requestLayout();
        left.requestLayout();
        // 右边部分
        ivParams = (ViewGroup.MarginLayoutParams) next.getLayoutParams();
        tvParams = (ViewGroup.MarginLayoutParams) right.getLayoutParams();
        if (item.getNextRes() == 0) {
            next.setImageDrawable(null);
            ivParams.rightMargin = 0;
            tvParams.rightMargin = item.getPadding()[2];
        } else {
            next.setImageResource(item.getNextRes());
            ivParams.rightMargin = item.getPadding()[2];
            tvParams.rightMargin = item.getPadding()[2] / 2;
        }
        next.requestLayout();
        right.requestLayout();
    }

    /**
     * 转换成行布局
     * @param holder ViewHolder
     * @param item 通用行
     */
    protected void convertItemView(@NonNull BaseViewHolder holder, @NonNull LineItem<?> item) {
        ViewGroup.MarginLayoutParams params;
        // 设置背景
        holder.itemView.setBackgroundResource(item.getBackgroundRes());
        // 设置外间隔
        int[] margin = item.getMargin();
        params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        // 设置行高
        params.height = item.getHeight();
        // 设置外间隔
        params.setMargins(margin[0], margin[1], margin[2], margin[3]);

        // 设置分割线
        int[] dividerMargin = item.getDividerMargin();
        View divider = holder.getView(R.id._ui_item_divider);
        // 设置分割线颜色
        divider.setBackgroundColor(item.getDividerColor());
        params = (ViewGroup.MarginLayoutParams) divider.getLayoutParams();
        // 设置分割高
        params.height = item.getDividerHeight();
        // 设置外间隔
        params.setMargins(dividerMargin[0], dividerMargin[1], dividerMargin[2], dividerMargin[3]);
    }
}
