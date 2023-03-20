package cn.cqray.demo.android;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.cache.GetCache;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);

        showToolbar();
        toolbar.setPaddingSE(30F);

        GetCache cache = new GetCache();
        cache.put("123", 10);

        showTip("6666666", () -> {});

        Log.e("数据", "" + cache.getInt("123"));


    }

    @Override
    public boolean onBackPressedGet() {
        return false;
    }
}