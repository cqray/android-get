package cn.cqray.demo.android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.KeyboardUtils;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetIntent;
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
//
//        new GetIntent(MainFragment2.class)
//                .setLaunchMode(10)
//                .launchMode
    }

//    @Override
//    public boolean onBackPress() {
//        return super.onBackPress();
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("数据", "我被销毁了");
    }
}