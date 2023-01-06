package cn.cqray.android.ui.multi;

import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;

import cn.cqray.android.app.GetIntent;

/**
 * Tab项
 * @author Cqray
 */
@SuppressWarnings("unchecked")
public class MultiItem {

    private String mName;
    private int mIcon;
    private GetIntent mIntent;

    public MultiItem(Class<? extends Fragment> clazz, String name) {
        mIntent = new GetIntent(clazz);
        mName = name;
    }

    public MultiItem(Class<? extends Fragment> clazz, String name, @DrawableRes int icon) {
        mIntent = new GetIntent( clazz);
        mName = name;
        mIcon = icon;
    }

    public MultiItem(GetIntent intent, String name) {
        mIntent = intent;
        mName = name;
    }

    public MultiItem(GetIntent intent, String name, @DrawableRes int icon) {
        mIntent = intent;
        mName = name;
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public int getIcon() {
        return mIcon;
    }

    public GetIntent getIntent() {
        return mIntent;
    }
}
