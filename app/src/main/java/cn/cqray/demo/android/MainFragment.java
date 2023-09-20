package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import cn.cqray.android.Get;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.tip.GetTipInit;
import cn.cqray.android.ui.page.GetPaginationFragment;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MainFragment extends GetPaginationFragment<Object> {

    private int s = 0;

    public MainFragment() {
        super();
        Log.e("数据", "MainFragmnet初始化了");
    }

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        finish(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        GetLog.d("onStop");
    }


//    @Override
//    public void onNewBundleGet(@Nullable Bundle bundle) {
//        super.onNewBundleGet(bundle);
//        GetLog.e("onNewBundleGet!!!!!!!!");
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        KeyboardUtils.unregisterSoftInputChangedListener(requireActivity().getWindow());
        GetLog.d("onDestroy");
    }

    @NonNull
    @Override
    protected BaseQuickAdapter<Object, ? extends BaseViewHolder> onCreateAdapter() {
        return new BaseQuickAdapter<Object, BaseViewHolder>(0) {
            @Override
            protected void convert(@NonNull BaseViewHolder holder, Object o) {

            }
        };
    }
}