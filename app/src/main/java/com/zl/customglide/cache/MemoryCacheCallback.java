package com.zl.customglide.cache;

import com.zl.customglide.resource.Value;

/**
 * 内存缓存中  元素被移除的回调接口
 */
public interface MemoryCacheCallback {


    public void entryRemoveMemoryCache(String key, Value oldValue);
}
