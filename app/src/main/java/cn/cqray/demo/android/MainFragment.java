package cn.cqray.demo.android;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.widget.SpinView;
import cn.cqray.android.widget.TextDrawable;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setGetContentView(R.layout.activity_main);
//        setBusy();
//        ImageView iv = getViewDelegate().findViewById(R.id.iv);
//        SpinView sv = getViewDelegate().findViewById(R.id.sv);
//        TextView view = getViewDelegate().findViewById(R.id.tv);
//        sv.setArcCount(2);
//        sv.setArcStrokeWidth(10);
//        sv.setRoundUseTime(2000);

        getStateDelegate()
                .getEmptyAdapter()
                .setTextColor(Color.YELLOW);
        setBusy();
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