package cn.cqray.demo.android;

import android.os.Bundle;
import android.widget.TextView;

//import androidx.annotation.NonNull;
//import androidx.lifecycle.DefaultLifecycleObserver;
//import androidx.lifecycle.LifecycleOwner;
//
//import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import cn.cqray.android.app.GetFragment;
import cn.cqray.android.log.GetLog;

public class MainFragment extends GetFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setGetContentView(R.layout.activity_main);

        toolbar.setTitle((Integer) null);

        toolbar.setTitleSpace(0);

        toolbar.setActionSpace(0);
        toolbar.setActionText(0, "666");
//        setBusy();

        TextView tv = requireContentView().findViewById(R.id.tv);
        tv.setText(UUID.randomUUID().toString());
        tv.append("6666666");



//        getLifecycle().addObserver(new DefaultLifecycleObserver() {
//            @Override
//            public void onCreate(@NonNull @NotNull LifecycleOwner owner) {
//                GetLog.d("onCreate2");
//            }
//
//            @Override
//            public void onStop(@NonNull @NotNull LifecycleOwner owner) {
//                GetLog.d("onStop2");
//            }
//
//            @Override
//            public void onDestroy(@NonNull @NotNull LifecycleOwner owner) {
//                GetLog.d("onDestroy2");
//            }
//        });


//
//        setBusy();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        showTip("666666|" + requireContentView() );
//    }

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

//    @Override
//    public boolean onBackPressedGet() {
//        back();
//        return true;
//    }
}