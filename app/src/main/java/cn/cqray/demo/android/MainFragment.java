package cn.cqray.demo.android;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import cn.cqray.android.Get;
import cn.cqray.android.log.GetLog;
import cn.cqray.android.tip.GetTipInit;
import cn.cqray.android.ui.page.GetPaginationFragment;

public class MainFragment extends GetPaginationFragment<Object> {

    private int s = 0;

    public MainFragment() {
        super();
        Log.e("数据", "MainFragmnet初始化了");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("数据", "MainFragment-" + "onCreate-" + requireActivity().hashCode());

        setGetContentView(R.layout.activity_main);

        findViewById(R.id.tv).setOnClickListener(v -> {

//            ToolbarInit init = Get.getInit().getToolbarInit();
//            assert init != null;
//            init.setBackgroundColor(Color.GRAY);
//            init.saveToLocal();

//            this.to(new GetIntent(MainFragment2.class));
//            this.to(new GetIntent(MainFragment2.class).setLaunchMode(GetIntent.SINGLE_TASK));
//            new Handler().postDelayed(() -> {
//                this.to(new GetIntent(MainFragment2.class).setLaunchMode(GetIntent.SINGLE_TASK));
//
//            }, 0);

//            TipInit tip = Get.getInit().getTipInit();
////            tip.clearLocal();
//            tip.setBackgroundColor(Color.BLUE);
//            tip.setTextSize(30);
//            tip.saveToLocal();
//
            GetTipInit tipInit = new GetTipInit();
            tipInit.setTextSize(15);

            Get.showTip("666666666", (GetTipInit) tipInit, () -> {

                Log.e("数据", "弹窗消失了");
                return null;
            });
        });

//        showSoftInput();
//        showSoftInput();
//        new Handler().postDelayed(() -> {
//            hideSoftInput();
//        }, 5000);
//
//        observeSoftInputChanged(this, h -> {
//            Log.e("数据", "导读变化：" + h);
//        });

    }


    @Override
    public void onStop() {
        super.onStop();
        GetLog.d("onStop");
    }


//    @Override
//    public void onNewBundleGet(@Nullable Bundle bundle) {
//        super.onNewBundleGet(bundle);
//        GetLog.e("onNewBundleGet!!!!!!!!");
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        KeyboardUtils.unregisterSoftInputChangedListener(requireActivity().getWindow());
        GetLog.d("onDestroy");
    }

    @NonNull
    @Override
    protected BaseQuickAdapter<Object, ? extends BaseViewHolder> onCreateAdapter() {
        return new BaseQuickAdapter<Object, BaseViewHolder>(0) {
            @Override
            protected void convert(@NonNull BaseViewHolder holder, Object o) {

            }
        };
    }
}