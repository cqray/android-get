package cn.cqray.demo.android;

import android.os.Bundle;

import com.blankj.utilcode.util.KeyboardUtils;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment.class);
    }

//    @Override
//    public boolean onBackPress() {
//        return super.onBackPress();
//    }
}