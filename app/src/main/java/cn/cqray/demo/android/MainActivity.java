package cn.cqray.demo.android;

import android.os.Bundle;


import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import cn.cqray.android.app.GetNavActivity;
import cn.cqray.android.ui.multi.GetMultiItem;
import cn.cqray.android.ui.multi.GetMultiActivity;

public class MainActivity extends GetNavActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);


        loadRootFragment(MainFragment2.class);

    }

}