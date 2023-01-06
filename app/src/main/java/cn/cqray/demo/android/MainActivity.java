package cn.cqray.demo.android;


import android.graphics.Color;
import android.os.Bundle;

import org.jetbrains.annotations.Nullable;

import cn.cqray.android.app.GetNavActivity;


public class MainActivity extends GetNavActivity {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        loadRootFragment(MainFragment.class);
        setBackgroundColor(Color.YELLOW);

        findViewById(R.id.get_nav_content).setOnClickListener(v -> {});
    }
}