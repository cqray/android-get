package cn.cqray.demo.android;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

public class InnerAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    public InnerAdapter() {
        super(R.layout.main_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Integer integer) {

    }
}
