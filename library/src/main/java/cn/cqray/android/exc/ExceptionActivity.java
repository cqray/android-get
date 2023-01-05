package cn.cqray.android.exc;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;


import cn.cqray.android.R;
import cn.cqray.android.app.GetActivity;
import cn.cqray.android.util.Sizes;

/**
 * 问题展示界面
 * @author Cqray
 */
public class ExceptionActivity extends GetActivity {

    String mIntroText;
    String mSourceText;
    Throwable mThrowable;

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setContentView(R.layout.starter_excption_layout);
        mIntroText = getIntent().getStringExtra("intro");
        mSourceText = getIntent().getStringExtra("source");
        mThrowable = (Throwable) getIntent().getSerializableExtra("throwable");
        initViews();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIntroText = intent.getStringExtra("intro");
        mSourceText = intent.getStringExtra("source");
        mThrowable = (Throwable) intent.getSerializableExtra("throwable");
        initViews();
    }

    private void initViews() {
        // 获取控件
        TextView desc = findViewById(R.id._starter_desc);
        TextView intro = findViewById(R.id._starter_intro);
        TextView source = findViewById(R.id._starter_source);
        // 设置相关文本内容
        desc.setText(mThrowable == null ? null : mThrowable.getMessage());
        intro.setText(mIntroText);
        source.setText(String.format("问题发生在[%s]", mSourceText));
        source.setVisibility(TextUtils.isEmpty(mSourceText) ? View.GONE : View.VISIBLE);
        // 初始化分割线
        initDivider(findViewById(R.id._starter_divider1));
        initDivider(findViewById(R.id._starter_divider2));

        if (mThrowable instanceof StarterException) {
            toolbar.setTitle("启动相关问题");
        } else {
            toolbar.setTitle("其他问题");
        }
    }

    /**
     * 初始化分割线
     * @param view 分割线控件
     */
    private void initDivider(@NonNull View view) {
        // 设置虚线背景
        int size = Sizes.smaller();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.LINE);
//        background.setStroke(1, ColorUtils.getColor(R.color.tint), size, size);
        ViewCompat.setBackground(view, background);
        // 设置显示与否
        if (view.getId() == R.id._starter_divider1) {
            view.setVisibility(TextUtils.isEmpty(mSourceText) ? View.GONE : View.VISIBLE);
        } else if (view.getId() == R.id._starter_divider2) {
            view.setVisibility(mThrowable == null ? View.GONE : View.VISIBLE);
        }
    }

}
