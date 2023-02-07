package cn.cqray.demo.android;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.util.ImageUtils;
import cn.cqray.android.util.KeyboardUtils;
import cn.cqray.android.util.ScreenUtils;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGetContentView(R.layout.activity_main);
        ImageView iv = getViewDelegate().findViewById(R.id.iv);
        TextView view = getViewDelegate().findViewById(R.id.tv);


        view.setText("6666666666666666666666");
        int height = ScreenUtils.getScreenHeight();// + ScreenUtils.INSTANCE.getNavBarHeight();
        Log.e("数据", "||->" + KeyboardUtils.getKeyboardHeight(requireActivity()) + "|" + KeyboardUtils.getContentViewInvisibleHeight(requireActivity().getWindow()));
        view.setOnClickListener(v -> {
//            Log.e("数据", "||" + ScreenUtils.INSTANCE.isFullScreen(requireActivity()));
//            ScreenUtils.INSTANCE.toggleFullScreen(requireActivity());
//            Log.e("数据", "||" + ScreenUtils.INSTANCE.isFullScreen(requireActivity()));
            //ScreenUtils.INSTANCE.setFullScreen(requireActivity());

//            iv.setImageBitmap(ImageUtils.INSTANCE.activity2Bitmap(requireActivity()));

            KeyboardUtils.showSoftInput();
//            new Handler().postDelayed(() -> Log.e("数据", "||->" + KeyboardUtils.getKeyboardHeight(requireActivity())), 1000);
//            KeyboardUtils.INSTANCE.showSoftInput(v);
////            Log.e("数据", "||22" + KeyboardUtils.INSTANCE.isSoftInputVisible(requireActivity()) + "|" + ScreenUtils.INSTANCE.isNavigationBarShow());
//
//            new Handler().postDelayed(() -> {
//                Log.e("数据", "||22" + KeyboardUtils.INSTANCE.isSoftInputVisible(requireActivity()));
//                //KeyboardUtils.INSTANCE.hideSoftInput(requireActivity());
//
//                KeyboardUtils.toggleSoftInput();
//
//            }, 3000);
        });
        KeyboardUtils.observeSoftInputChanged(requireActivity(), integer -> {

            Log.e("数据", "高度变化：" + integer + "|" + KeyboardUtils.getContentViewInvisibleHeight(requireActivity().getWindow()));
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