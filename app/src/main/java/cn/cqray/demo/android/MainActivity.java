package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app2.GetNavActivity;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        Log.e("数据", "MainActivity.onCreating|" + hashCode());
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("数据", "我被销毁了");
    }
}