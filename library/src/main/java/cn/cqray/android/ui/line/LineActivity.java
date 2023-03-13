package cn.cqray.android.ui.line;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collection;

import cn.cqray.android.app.GetActivity;

/**
 * 列型UI界面
 * @author Cqray
 */
public class LineActivity extends GetActivity {

    protected RecyclerView mRecyclerView;
    protected LineAdapter mLineAdapter;

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        mLineAdapter = new LineAdapter();
        mRecyclerView = new RecyclerView(this);
        mRecyclerView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mLineAdapter);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        setContentView(mRecyclerView);
    }

    public void addLineItem(LineItem<?> item) {
        mLineAdapter.addData(item);
    }

    public void setLineItems(Collection<? extends LineItem<?>> items) {
        mLineAdapter.setList(items);
    }

    public LineItem<?> getLineItem(int index) {
        return mLineAdapter.getItem(index);
    }

    public LineItem<?> getLineItemByTag(int index) {
        return mLineAdapter.getItemByTag(index);
    }

    public void notifyDataSetChanged() {
        mLineAdapter.notifyDataSetChanged();
    }
}
