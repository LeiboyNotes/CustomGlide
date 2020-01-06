package com.zl.customglide.cache;

import android.os.Looper;
import android.util.Log;

import com.zl.customglide.Tool;
import com.zl.customglide.resource.Value;
import com.zl.customglide.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ActiveCache {


    private Map<String, WeakReference<Value>> mapList = new HashMap<>();
    private ReferenceQueue<Value> queue;//为了监听弱引用是否被回收
    private boolean isCloseThread;
    private Thread thread;
    private boolean isHandRemove;
    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * 添加活动缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        //绑定value监听
        value.setCallback(valueCallback);
        //存储
        CustomWeakreference customWeakreference = new CustomWeakreference(value, getQueue(), key);
        mapList.put(key, new CustomWeakreference(value, getQueue(), key));
        Log.d("mapList:put", "put--->  " + key+"--customWeakreference:  "+customWeakreference);
        Log.d("thread", "thread--->  " + Looper.myLooper());
//        mapList.put(key, new CustomoWeakReference(value, getQueue(), key));
    }

    /**
     * 外部调用获取value
     */
    public Value get(String key) {
        Log.d("mapList.size", "size--->" + mapList.size());
        WeakReference<Value> weakReference = mapList.get(key);
        if (null != weakReference) {
            return weakReference.get();//返回value
        }
        return null;
    }

    /**
     * 手动移除
     */
    public Value remove(String key) {
        isHandRemove = true;
        WeakReference<Value> weakReference = mapList.remove(key);
        isHandRemove = false;//还原  让GC继续自动移除工作
        if (null != weakReference) {
            return weakReference.get();
        }
        return null;
    }

    /**
     * 释放关闭线程
     */
    public void closeThread() {
        isCloseThread = true;
       /* if (null != thread) {
            thread.interrupt();//中断线程
            try {
                thread.join(TimeUnit.SECONDS.toMillis(5));
                if (thread.isAlive()) {//证明线程没有结束
                    throw new IllegalArgumentException("活动缓存中  关闭线程  线程没有停下来");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        mapList.clear();
        Log.d("mapList.clear", "clear--->");
        System.gc();
    }

    //监听弱引用  成为其子类
    public class CustomWeakreference extends WeakReference<Value> {

        private String key;

        public CustomWeakreference(Value referent, ReferenceQueue<? super Value> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

    /**
     * 监听弱引用被回收
     *
     * @return
     */
    private ReferenceQueue<Value> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isCloseThread) {
                        try {
                            if (!isHandRemove) {
                                //queue.remove()阻塞式方法
                                Reference<? extends Value> remove = queue.remove();//如果被回收了  就执行此方法
                                CustomWeakreference weakreference = (CustomWeakreference) remove;
                                //移除容器   区分手动、被动移除
                                if (mapList != null && !mapList.isEmpty()) {
                                    mapList.remove(weakreference.key);
                                    Log.d("mapList:remove", "remove--->" + weakreference.key);
                                }
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            thread.start();
        }
        return queue;
    }

}
