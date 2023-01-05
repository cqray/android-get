package cn.cqray.demo.android;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import cn.cqray.android.app.GetActivity;
import cn.cqray.android.app.GetManager;
import cn.cqray.android.log.GetLog;



public class MainActivity extends GetActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGetContentView(R.layout.activity_main);
        GetManager.runOnUiThreadDelayed(() -> {

            GetLog.d("666666666");
        }, 5000);

        toolbar.setTitle("我是谁啊");

        getToolbar2();








        GetLog.d("onCreate");
        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull @NotNull LifecycleOwner owner) {
                GetLog.d("onCreate2");
            }

            @Override
            public void onStop(@NonNull @NotNull LifecycleOwner owner) {
                GetLog.d("onStop2");
            }

            @Override
            public void onDestroy(@NonNull @NotNull LifecycleOwner owner) {
                GetLog.d("onDestroy2");
            }
        });
        //GetTip.show("666666666");

        showTip("666666");

        setEmpty();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GetLog.d("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GetLog.d("onDestroy");
    }

    @Override
    public boolean onGetBackPressed() {
        back();
        return true;
    }
}