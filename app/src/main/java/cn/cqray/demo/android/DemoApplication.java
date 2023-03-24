package cn.cqray.demo.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;

import cn.cqray.android.Get;
import cn.cqray.android.app.GetInit;
import cn.cqray.android.widget.GetToolbarInit;

public class DemoApplication extends MultiDexApplication {
    
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            //全局设置主题颜色
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));指定为经典Header，默认是 贝塞尔雷达Header
            return new ClassicsHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Application app = Utils.getApp();

        Log.e("数据", "APPlication是否相同：" + (app == this));

        GetToolbarInit toolbarInit = new GetToolbarInit();
        toolbarInit.setElevation(100F);
        toolbarInit.setBackIcon(R.drawable.def_back_common_dark);
        toolbarInit.setBackText("返回");
        GetInit getInit = new GetInit();
        getInit.setToolbarInit(toolbarInit);

        Get.init(this, getInit);

    }
}
