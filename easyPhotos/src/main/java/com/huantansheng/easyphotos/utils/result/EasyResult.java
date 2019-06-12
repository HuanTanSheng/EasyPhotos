package com.huantansheng.easyphotos.utils.result;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * EasyResult
 *
 * @author joker
 * @date 2019/4/9.
 */
public class EasyResult {
    private static final String TAG = "com.huantansheng.easyphotos";

    private EasyResult() {

    }

    public static HolderFragment get(FragmentActivity activity) {
        return new EasyResult().getHolderFragment(activity.getSupportFragmentManager());
    }

    public static HolderFragment get(Fragment fragment) {
        return new EasyResult().getHolderFragment(fragment.getChildFragmentManager());
    }

    private HolderFragment getHolderFragment(FragmentManager fragmentManager) {
        HolderFragment holderFragment = findHolderFragment(fragmentManager);
        if (holderFragment == null) {
            holderFragment = new HolderFragment();
            fragmentManager
                    .beginTransaction()
                    .add(holderFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return holderFragment;
    }

    private HolderFragment findHolderFragment(FragmentManager fragmentManager) {
        return (HolderFragment) fragmentManager.findFragmentByTag(TAG);
    }

}
