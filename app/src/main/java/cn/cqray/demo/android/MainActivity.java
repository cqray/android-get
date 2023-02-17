package cn.cqray.demo.android;

import android.os.Bundle;


import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import cn.cqray.android.ui.multi.GetMultiItem;
import cn.cqray.android.ui.multi.GetMultiActivity;

public class MainActivity extends GetMultiActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);



//        VarString string = new VarString("123");

//        loadRootFragment(MainFragment.class);
//        setBackgroundColor(Color.YELLOW);
//
//        findViewById(R.id.get_nav_content).setOnClickListener(v -> {});

//        setTabAtTop(true);
//
//        Double a = null;
//        GetIntent intent = new GetIntent();
//        intent.put("44", a);
//        intent.setTo(MainActivity.class);
//        intent.putBoolean("44", a);

//        new GetFragment();

        setTabAtTop(false);
        loadMultiFragments(
//                new GetMultiItem(MainFragment.class, "我的"),
//                new GetMultiItem(MainFragment.class, "你的"),
//                new GetMultiItem(MainFragment.class, "你的"),
//                new GetMultiItem(MainFragment.class, "你的"),
//                new GetMultiItem(MainFragment.class, "她的")
        );
        addFragment(new GetMultiItem(MainFragment.class,"我的").put("index", 0), 0);
        addFragment(new GetMultiItem(MainFragment.class,"ta").put("index", 1), 0);
        addFragment(new GetMultiItem(MainFragment.class,"tata").put("index", 2), 1);

        showFragment(2);
        removeFragment(2);









    }

    @Override
    public void onFragmentPageSelected(@NotNull ViewPager2 vp, int position) {

    }
}