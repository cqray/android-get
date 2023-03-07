package cn.cqray.demo.android;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.widget.GetTitleBar;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setGetContentView(R.layout.activity_main);
        setBusy();
//        ImageView iv = getViewDelegate().findViewById(R.id.iv);
//        TextView view = getViewDelegate().findViewById(R.id.tv);

        GetTitleBar bar = getViewDelegate().findViewById(R.id.bar);
        bar.getActionLayout().setText(0, "6666");
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