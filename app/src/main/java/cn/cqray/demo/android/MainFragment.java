package cn.cqray.demo.android;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.util.KeyboardUtils;
import cn.cqray.android.util.ScreenUtils;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGetContentView(R.layout.activity_main);
        //ScreenUtils.INSTANCE.setFullScreen(requireActivity());

        TextView view = getViewDelegate().findViewById(R.id.tv);
        view.setText(requireArguments().getInt("index") + "12313213");
        Log.e("数据", "||22" + KeyboardUtils.INSTANCE.isSoftInputVisible(requireActivity()));

        int height = ScreenUtils.INSTANCE.getScreenHeight();// + ScreenUtils.INSTANCE.getNavBarHeight();
        Log.e("数据", "||->" + height);
        view.setOnClickListener(v -> {
//            Log.e("数据", "||" + ScreenUtils.INSTANCE.isFullScreen(requireActivity()));
//            ScreenUtils.INSTANCE.toggleFullScreen(requireActivity());
//            Log.e("数据", "||" + ScreenUtils.INSTANCE.isFullScreen(requireActivity()));
            //ScreenUtils.INSTANCE.setFullScreen(requireActivity());

//            KeyboardUtils.INSTANCE.toggleSoftInput();
            KeyboardUtils.INSTANCE.showSoftInput(v);

            new Handler().postDelayed(() -> {
                Log.e("数据", "||22" + KeyboardUtils.INSTANCE.isSoftInputVisible(requireActivity()));
                //KeyboardUtils.INSTANCE.hideSoftInput(requireActivity());

            }, 3000);
        });

//        new Handler().postDelayed(() -> {
//            Log.e("数据", "||22" + KeyboardUtils.INSTANCE.isSoftInputVisible(requireActivity()));
//            KeyboardUtils.INSTANCE.hideSoftInput(requireActivity());
//
//        }, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        GetLog.d("onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GetLog.d("onDestroy");
    }

}