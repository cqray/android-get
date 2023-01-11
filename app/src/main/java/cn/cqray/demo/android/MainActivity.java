package cn.cqray.demo.android;

import android.graphics.Color;
import android.os.Bundle;


import org.jetbrains.annotations.Nullable;


import cn.cqray.android.app.GetMultiItem;
import cn.cqray.android.ui.multi.GetMultiActivity;

public class MainActivity extends GetMultiActivity {



    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
//        loadRootFragment(MainFragment.class);
//        setBackgroundColor(Color.YELLOW);
//
//        findViewById(R.id.get_nav_content).setOnClickListener(v -> {});

        setTabAtTop(true);
        setTabElevation(90F);
        setTabHeight(200F);

        tabLayout.setBackgroundColor(Color.BLUE);
        
        

        loadMultiFragments(
                new GetMultiItem(MainFragment.class, "我的"),
                new GetMultiItem(MainFragment.class, "你的"),
                new GetMultiItem(MainFragment.class, "你的"),
                new GetMultiItem(MainFragment.class, "你的"),
                new GetMultiItem(MainFragment.class, "她的")
        );

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setTabAtTop(false);
//            }
//        }, 3000);
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