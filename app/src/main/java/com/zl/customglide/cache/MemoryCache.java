package com.zl.customglide.cache;


import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.zl.customglide.resource.Value;

public class MemoryCache extends LruCache<String, Value> {


    private boolean manualRemove;

    private MemoryCacheCallback memoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    //手动移除
    public Value manualRemove(String key) {
        manualRemove = true;
        Value value = remove(key);
        manualRemove = false;
        return value;
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Value value) {
        Bitmap bitmap = value.getmBitmap();
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    /**
     * 重复的key  最少使用的元素都会被移除
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, Value oldValue, Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (memoryCacheCallback != null && !manualRemove) {//被动
            memoryCacheCallback.entryRemoveMemoryCache(key, oldValue);
        }
    }
}
