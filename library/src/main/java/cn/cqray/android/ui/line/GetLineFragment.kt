package cn.cqray.android.ui.line;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;

import cn.cqray.android.app.GetFragment;

/**
 * 列型UI界面
 * @author Cqray
 */
public class GetLineFragment extends GetFragment {

    protected RecyclerView mRecyclerView;
    protected GetLineAdapter mLineAdapter;

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        mLineAdapter = new GetLineAdapter();
        mRecyclerView = new RecyclerView(requireContext());
        mRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mRecyclerView.setAdapter(mLineAdapter);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setContentView(mRecyclerView);
    }

    public void addLineItem(GetLineItem<?> item) {
        mLineAdapter.addData(item);
    }

    public void setLineItems(Collection<? extends GetLineItem<?>> items) {
        mLineAdapter.setList(items);
    }
}
