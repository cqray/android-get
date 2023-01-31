package cn.cqray.demo.android;

import androidx.multidex.MultiDexApplication;

import cn.cqray.android.Get;
import cn.cqray.android.app.GetInit;
import cn.cqray.android.tip.GetTip;
import cn.cqray.android.widget.ToolbarInit;

public class DemoApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ToolbarInit toolbarInit = new ToolbarInit();
        toolbarInit.setElevation(100F);
        toolbarInit.setBackIcon(R.drawable.def_back_common_dark);
        toolbarInit.setBackText("返回");
        GetInit getInit = new GetInit();
        getInit.setToolbarInit(toolbarInit);

        Get.init(this, getInit);

    }
}
