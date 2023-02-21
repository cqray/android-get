package cn.cqray.demo.android;

import android.os.Bundle;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment4.class);

    }

    @Override
    public boolean onBackPressedGet() {
        return false;
    }
}