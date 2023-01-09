package cn.cqray.demo.android;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.cache.GetCache;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.ui.multi.MultiItem;
import cn.cqray.android.ui.multi.MultiNavActivity;

public class MainActivity extends MultiNavActivity {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
//        loadRootFragment(MainFragment.class);
//        setBackgroundColor(Color.YELLOW);
//
//        findViewById(R.id.get_nav_content).setOnClickListener(v -> {});

        loadMultiFragments(
                new MultiItem(MainFragment.class, "我的"),
                new MultiItem(MainFragment.class, "你的"),
                new MultiItem(MainFragment.class, "你的"),
                new MultiItem(MainFragment.class, "你的"),
                new MultiItem(MainFragment.class, "她的")
        );


    }
}