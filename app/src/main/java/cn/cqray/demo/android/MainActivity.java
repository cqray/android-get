package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetInit;
import cn.cqray.android.app.GetIntent;
import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.cache.GetCache;
import cn.cqray.android.log.GetLog;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);

    }

    @Override
    public boolean onBackPressedGet() {
        return false;
    }
}