package cn.cqray.demo.android;

import androidx.multidex.MultiDexApplication;

import cn.cqray.android.Get;
import cn.cqray.android.app.GetInit;
import cn.cqray.android.widget.GetToolbarInit;

public class DemoApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        GetToolbarInit toolbarInit = new GetToolbarInit();
        toolbarInit.setElevation(100F);
        toolbarInit.setBackIcon(R.drawable.def_back_common_dark);
        toolbarInit.setBackText("返回");
        GetInit getInit = new GetInit();
        getInit.setToolbarInit(toolbarInit);

        Get.init(this, getInit);

    }
}
