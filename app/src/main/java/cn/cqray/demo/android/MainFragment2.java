package cn.cqray.demo.android;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.ui.multi.GetMultiFragment;
import cn.cqray.android.ui.multi.GetMultiItem;

public class MainFragment2 extends GetMultiFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTabAtTop(false);
        loadMultiFragments(
                new GetMultiItem(MainFragment3.class, "我的"),
                new GetMultiItem(MainFragment3.class, "你的")

        );
//        addFragment(new GetMultiItem(MainFragment.class,"我的").put("index", 0), 0);
//        addFragment(new GetMultiItem(MainFragment.class,"ta").put("index", 1), 0);
//        addFragment(new GetMultiItem(MainFragment.class,"tata").put("index", 2), 1);
//
//        showFragment(2);
//        removeFragment(2);

    }


}