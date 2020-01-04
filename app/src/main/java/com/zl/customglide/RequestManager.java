package com.zl.customglide;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.zl.customglide.fragment.ActivityFramentManager;
import com.zl.customglide.fragment.FragmentActivityFragmentManager;


public class RequestManager {


    private final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_NAME";
    private final String ACTIVITY_NAME = "Activity_NAME";
    private final int NEXT_HANDLER_MSG = 995465;

    private Context requestManagerContext;
    private RequestTargetEngine requestTargetEngine;

    //构造代码块  不用在所有的构造方法里面去实例化  统一的去写
    {
        if (requestTargetEngine == null) {
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    /**
     * 可以管理生命周期
     *
     * @param fragmentActivity
     */
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        //拿到Fragment
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (null == fragment) {
            fragment = new FragmentActivityFragmentManager(requestTargetEngine);
            //添加到supportFragmentManager
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }
        //发送一次handler
        handler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    /**
     * 可以管理生命周期
     *
     * @param activity
     */
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if (null == fragment) {
            fragment = new ActivityFramentManager(requestTargetEngine);
            //添加到管理
            fragmentManager.beginTransaction().add(fragment,ACTIVITY_NAME).commitAllowingStateLoss();
        }
        //发送一次handler
        handler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });
    /**
     * 不可以管理生命周期 --- 因为application生命周期太长  无法管理
     *
     * @param context
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    public RequestTargetEngine load(String path) {
        return requestTargetEngine;
    }
}
