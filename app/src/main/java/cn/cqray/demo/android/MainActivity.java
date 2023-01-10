package cn.cqray.demo.android;

import android.os.Bundle;


import org.jetbrains.annotations.Nullable;


import cn.cqray.android.ui.multi.MultiItem2;

import cn.cqray.android.ui.multi.MultiTabActivity;

public class MainActivity extends MultiTabActivity {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
//        loadRootFragment(MainFragment.class);
//        setBackgroundColor(Color.YELLOW);
//
//        findViewById(R.id.get_nav_content).setOnClickListener(v -> {});

        loadMultiFragments(
                new MultiItem2(MainFragment.class, "我的"),
                new MultiItem2(MainFragment.class, "你的"),
                new MultiItem2(MainFragment.class, "你的"),
                new MultiItem2(MainFragment.class, "你的"),
                new MultiItem2(MainFragment.class, "她的")
        );
//
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//
//        setFragmentDragEnable(false);


//        GetLayoutMultiTabBinding binding = GetLayoutMultiTabBinding.inflate(getLayoutInflater());
//
//        setContentView(binding.getRoot());

        
//        ft.add("")
    }
}