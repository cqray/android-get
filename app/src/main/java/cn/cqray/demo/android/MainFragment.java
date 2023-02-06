package cn.cqray.demo.android;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.util.ScreenUtils;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGetContentView(R.layout.activity_main);

        TextView view = getViewDelegate().findViewById(R.id.tv);
        view.setText(requireArguments().getInt("index") + "12313213");
        view.setOnClickListener(v -> {

            ScreenUtils.INSTANCE.toggleFullScreen(requireActivity());
            //ScreenUtils.INSTANCE.setFullScreen(requireActivity());
        });
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