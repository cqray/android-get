package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.Arrays;

import cn.cqray.android.ui.page.GetPaginationFragment;

public class MainFragment4 extends GetPaginationFragment<Integer> {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setPaginationEnable(false);
    }

    @NonNull
    @Override
    protected BaseQuickAdapter<Integer, ? extends BaseViewHolder> onCreateAdapter() {
        return new InnerAdapter();
    }

    @Override
    protected void onRefresh(int pageNum, int pageSize) {

        Log.e("数据", "12a1sd3fa");
        finish(Arrays.asList(1, 2, 3, 4));

    }

    @Override
    public boolean onBackPress() {
        return super.onBackPress();
    }
}
