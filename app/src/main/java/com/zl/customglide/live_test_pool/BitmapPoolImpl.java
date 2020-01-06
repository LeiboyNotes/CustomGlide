package com.zl.customglide.live_test_pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

/**
 * 复用池使用LRU算法来保存
 */
public class BitmapPoolImpl extends LruCache<Integer, Bitmap> implements BitmapPool {

    private final String TAG = BitmapPoolImpl.class.getSimpleName();

    /**
     * LRU 允许最大存储容量
     *
     * @param maxSize
     */
    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    //保存的容器  为了get的时候进行筛选
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    @Override
    public void put(Bitmap bitmap) {

        //todo 复用池复用条件  bitmap.isMutable = true
        if (!bitmap.isMutable()) {
            Log.d(TAG, "put: 条件一 bitmap.isMutable()= false 不能满足复用的机制");
            return;
        }
        //todo 条件二： 不能大于LRU 最大允许的存储容量的大小
        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize >= maxSize()) {
            Log.d(TAG, "put:条件二  大于maxsize 不能满足复用的机制");
            return;
        }

        //开始添加到复用池
        put(bitmapSize, bitmap);

        //第二次保存，容器
        treeMap.put(bitmapSize, null);
        Log.d(TAG, "put: 添加到复用池成功....");
    }

    //计算bitmap的大小
    private int getBitmapSize(Bitmap bitmap) {
        //早期的计算方式
        //getRowBytes()*getHeight()

        //3.0 12  API 版本
        //bitmap.getByteCount()

        //4.4 19 API 版本  native进行计算的
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        /**
         * 位图  == Bitmap.config
         */
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        //去复用池里寻找   是否匹配我计算出来的图片大小
        //可以和getSize一样大或者比getSize大的key
        Integer key = treeMap.ceilingKey(getSize);
        //如果没有保存到复用池   得到的key为null
        if (key == null) {
            return null;//没有找到合适的可以复用的key
        }
        //找到
        Bitmap bitmapResult = remove(key);
        Log.d(TAG, "get: 从复用池了获取到了Bitmap的内存空间....");
        return bitmapResult;
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
//        return super.sizeOf(key, value);
        return getBitmapSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}
