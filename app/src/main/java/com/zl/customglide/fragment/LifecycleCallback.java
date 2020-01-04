package com.zl.customglide.fragment;

public interface LifecycleCallback {

    //生命周期初始化
    public void glideInitAction();

    //生命周期停止
    public void glideStopAction();

    //生命周期释放
    public void glideRecycleAction();
}
