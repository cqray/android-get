package cn.cqray.demo.android;

import androidx.multidex.MultiDexApplication;

import cn.cqray.android.Get;

public class DemoApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Get.init(this);
    }
}
