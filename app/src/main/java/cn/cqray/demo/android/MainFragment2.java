package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.ui.multi.GetMultiFragment;
import cn.cqray.android.ui.multi.GetMultiItem;

public class MainFragment2 extends GetMultiFragment {

    @Override
    public void onCreating(Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setTabAtTop(false);


//        toolbar.setActionIcon(0, R.drawable.def_back_common_dark);
//        loadMultiFragments(
//                new GetMultiItem(MainFragment3.class, "我的"),
//                new GetMultiItem(MainFragment3.class, "你的")
//
//        );
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setActionIcon(0, R.drawable.def_back_common_dark);
        toolbar.setActionText(1, "123132");
//        addFragment(new GetMultiItem(MainFragment.class,"我的").put("index", 0), 0);
//        addFragment(new GetMultiItem(MainFragment.class,"ta").put("index", 1), 0);
//        addFragment(new GetMultiItem(MainFragment.class,"tata").put("index", 2), 1);
//
//        showFragment(2);
//        removeFragment(2);

    }


    @Override
    public void onNewBundleGet(@Nullable Bundle bundle) {
        super.onNewBundleGet(bundle);
        Log.e("数据","onNewBundleGet2222");


    }


}