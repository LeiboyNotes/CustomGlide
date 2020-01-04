package com.zl.customglide;

import android.util.Log;
import android.widget.ImageView;

import com.zl.customglide.fragment.LifecycleCallback;

public class RequestTargetEngine implements LifecycleCallback {

    private final String TAG = RequestTargetEngine.class.getSimpleName();

    @Override
    public void glideInitAction() {
        Log.e(TAG,"glideInitAction：Glide生命周期----->已开启   初始化...");
    }

    @Override
    public void glideStopAction() {
        Log.e(TAG,"glideStopAction：Glide生命周期----->   停止...");
    }

    @Override
    public void glideRecycleAction() {
        Log.e(TAG,"glideRecycleAction：Glide生命周期----->   释放...");
    }

    public void into(ImageView imageView1) {

    }
}
