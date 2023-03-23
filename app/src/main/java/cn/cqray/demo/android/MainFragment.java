package cn.cqray.demo.android;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.app.GetIntent;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.widget.TextDrawable;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setGetContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(v  -> {

            this.to(new GetIntent(MainFragment2.class));
            this.to(new GetIntent(MainFragment2.class).setLaunchMode(GetIntent.SINGLE_TASK));
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        GetLog.d("onStop");
    }

    @Override
    public void onNewBundleGet(@Nullable Bundle bundle) {
        super.onNewBundleGet(bundle);
        GetLog.e("onNewBundleGet!!!!!!!!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GetLog.d("onDestroy");
    }

}