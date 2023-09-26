package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        Log.e("数据", "MainActivity.onCreating|" + hashCode());
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);
//        showToolbar();

//        setBusy();
//
//        timerTask(aLong -> {
//            setIdle();
//        }, 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("数据", "我被销毁了");
    }
}