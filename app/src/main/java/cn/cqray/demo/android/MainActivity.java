package cn.cqray.demo.android;

import android.os.Bundle;


import org.jetbrains.annotations.Nullable;


import cn.cqray.android.Get;
import cn.cqray.android.app.GetIntent;
import cn.cqray.android.multi.GetMultiItem;
import cn.cqray.android.multi.GetMultiActivity;

public class MainActivity extends GetMultiActivity {


    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
//        loadRootFragment(MainFragment.class);
//        setBackgroundColor(Color.YELLOW);
//
//        findViewById(R.id.get_nav_content).setOnClickListener(v -> {});

        setTabAtTop(true);

        Boolean a = null;
        GetIntent intent = new GetIntent();
        intent.put("44", a);

//        new GetFragment();


        loadMultiFragments(
//                new GetMultiItem(MainFragment.class, "我的"),
//                new GetMultiItem(MainFragment.class, "你的"),
//                new GetMultiItem(MainFragment.class, "你的"),
//                new GetMultiItem(MainFragment.class, "你的"),
//                new GetMultiItem(MainFragment.class, "她的")
        );
        addFragment(new GetMultiItem(MainFragment.class,"我的"), 0);
        addFragment(new GetMultiItem(MainFragment.class,"ta"), 0);
        addFragment(new GetMultiItem(MainFragment.class,"tata"), 1);


        removeFragment(1);


        viewPager.setCurrentItem(1);
//        showFragment(-1);
//
//        getMultiDelegate().removeFragment(R.id.get_nav_view, -1);

//        removeFragments();

//        removeFragments();
//        getMultiDelegate().removeAllFragments();

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