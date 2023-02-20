package cn.cqray.demo.android;

import android.os.Bundle;

import cn.cqray.android.ui.multi.GetMultiFragment;
import cn.cqray.android.ui.multi.GetMultiItem;

public class MainFragment3 extends GetMultiFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabAtTop(false);
        loadMultiFragments(
                new GetMultiItem(MainFragment.class, "我的"),
                new GetMultiItem(MainFragment.class, "你的"),
                new GetMultiItem(MainFragment.class, "你的"),
                new GetMultiItem(MainFragment.class, "你的"),
                new GetMultiItem(MainFragment.class, "她的")
        );
//        addFragment(new GetMultiItem(MainFragment.class,"我的").put("index", 0), 0);
//        addFragment(new GetMultiItem(MainFragment.class,"ta").put("index", 1), 0);
//        addFragment(new GetMultiItem(MainFragment.class,"tata").put("index", 2), 1);
//
//        showFragment(2);
//        removeFragment(2);

    }


}