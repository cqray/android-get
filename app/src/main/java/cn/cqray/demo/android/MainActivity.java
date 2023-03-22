package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.cache.GetCache;
import cn.cqray.android.log.GetLog;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);

        showToolbar();
        toolbar.setPaddingSE(30F);

        GetCache cache = new GetCache();
        cache.put("123", 10);

//        showTip("6666666");

        Log.e("数据", "" + cache.getInt("123"));

        GetLog.e("6666");

        GetLog.eTag(null, new float[]{0f, 1f, 2f, 3f});

        GetLog.xml(GetLog.A, "6666");
    }

    @Override
    public boolean onBackPressedGet() {
        return false;
    }
}