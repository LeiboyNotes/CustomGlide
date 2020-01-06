package com.zl.customglide.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.zl.customglide.Tool;

/**
 * 对Bitmap封装
 */
public class Value {

    private static final String TAG = Value.class.getSimpleName();
    private Bitmap mBitmap;

    private static Value value;

    public static Value getInstance() {
        if (null == value) {
            synchronized (Value.class) {
                if (null == value) {
                    value = new Value();
                }
            }
        }
        return value;
    }

    //使用计数 +1
    private int count;

    //监听
    private ValueCallback callback;

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    /**
     * 使用就加1
     */
    public void useAction() {
        Tool.checkNotEmpty(mBitmap);
        if (mBitmap.isRecycled()) {//已经被回收了
            Log.e(TAG, "useAction: 加一 count:" + count);
            return;
        }
        Log.e(TAG, "useAction: 加一 count:" + count);
        count++;
    }

    /**
     * 不使用就减一
     */
    public void nonUseAction() {
        count--;
        if (count <= 0 && callback != null) {
            //回调  通知  不再使用
            callback.valueNonUseListener(key, this);
        }
        Log.d(TAG, "useAction: 减一 count:" + count);
    }

    /**
     * 释放
     */
    public void recycleBitmap() {
        if (count > 0) {
            Log.e(TAG, "recycleBitmap:引用计数>0,说明还在使用，不能释放...");
            return;
        }
        if (mBitmap.isRecycled()) {
            Log.e(TAG, "recycleBitmap:mBitmap.isRecycled()已经被释放...");
            return;
        }

        mBitmap.recycle();
        value = null;
        System.gc();
    }
}
