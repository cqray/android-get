package cn.cqray.demo.android;

import android.graphics.Color;
import android.os.Bundle;

import com.blankj.utilcode.util.KeyboardUtils;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetIntent;
import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.ui.line.GetLineItem;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);

        GetLineItem.button("6666")
                .height(10F)
                .background(Color.BLACK);
//
//        new GetIntent(MainFragment2.class)
//                .setLaunchMode(10)
//                .launchMode
    }

//    @Override
//    public boolean onBackPress() {
//        return super.onBackPress();
//    }
}