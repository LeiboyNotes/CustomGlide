package com.zl.customglide.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;

/**
 * Activity 生命周期关联管理
 */
public class ActivityFramentManager extends Fragment {

    private LifecycleCallback lifecycleCallback;

    public ActivityFramentManager() {
    }

    @SuppressLint("ValidFragment")
    public ActivityFramentManager(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycleAction();
        }
    }
}
