package cn.cqray.android.ui.multi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Fragment适配器
 * @author Cqray
 */
public class MultiFragmentAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList;

    public MultiFragmentAdapter(@NonNull Fragment fragment, List<Fragment> fragmentList) {
        super(fragment);
        this.fragmentList = fragmentList;
    }

    public MultiFragmentAdapter(@NonNull FragmentActivity activity, List<Fragment> fragmentList) {
        super(activity);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    @NonNull
    public List<Fragment> getFragmentList() {
        return fragmentList;
    }
}
