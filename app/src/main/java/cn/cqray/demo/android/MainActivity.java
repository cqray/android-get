package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.function.TipFunctionsKt;
import cn.cqray.android.ui.line.GetLineItem;
import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("数据", "我被销毁了");
    }
}