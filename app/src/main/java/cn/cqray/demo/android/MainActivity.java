package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.ui.line.GetLineItem;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        Log.e("数据", "MainActivity.onCreating|" + hashCode());
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);

        Log.e("数据", "MainFragment.Loaded");

        GetLineItem.button("6666")
                .height(10F);

//        TipFunctionsKt.showTip();
        periodicTask(aLong -> {

                    Log.e("数据", "任务执行" + aLong);

                }, 2000, 2000, a -> a == 10, TimeUnit.MILLISECONDS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("数据", "我被销毁了");
    }
}