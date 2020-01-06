package com.zl.customglide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.zl.customglide.cache.ActiveCache;
import com.zl.customglide.cache.MemoryCache;
import com.zl.customglide.cache.MemoryCacheCallback;
import com.zl.customglide.disk.DiskLruCacheImpl;
import com.zl.customglide.fragment.LifecycleCallback;
import com.zl.customglide.load_data.LoadDataManager;
import com.zl.customglide.load_data.ResponseListener;
import com.zl.customglide.resource.Key;
import com.zl.customglide.resource.Value;
import com.zl.customglide.resource.ValueCallback;

public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseListener {

    private final String TAG = RequestTargetEngine.class.getSimpleName();

    @Override
    public void glideInitAction() {
        Log.e(TAG, "glideInitAction：Glide生命周期----->已开启   初始化...");
    }

    @Override
    public void glideStopAction() {
        Log.e(TAG, "glideStopAction：Glide生命周期----->   停止...");
    }

    @Override
    public void glideRecycleAction() {
        Log.e(TAG, "glideRecycleAction：Glide生命周期----->   释放...");
        if (activeCache != null) {
            activeCache.closeThread();//释放活动缓存
        }
    }


    private ActiveCache activeCache;//活动缓存
    private MemoryCache memoryCache;//内存缓存
    private DiskLruCacheImpl diskLruCache;//磁盘缓存

    private final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this);//回调告诉外界，Value资源不再使用了
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE);
            memoryCache.setMemoryCacheCallback(this);

        }
        diskLruCache = new DiskLruCacheImpl();
    }

    private String path;
    private Context glideContext;
    private String key;
    private ImageView imageView;

    /**
     * RequestManager传递的值
     *
     * @param path
     * @param
     */
    public void loadValueInitAction(String path, Context glideContext) {
        this.path = path;
        this.glideContext = glideContext;
        key = new Key(path).getKey();
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();
        //TODO 加载资源--->缓存 --->网络  成功后----->资源保存到缓存中>>>
        Value value = cacheAction();
        if (null != value) {
            value.nonUseAction();
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    private Value cacheAction() {
        Log.d(TAG, "cacheAction--->key-->  "+key);
        //1、判断活动缓存是否有资源  如果有就返回  没有就继续往下找
        Value value = activeCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction--->本次加载是在  活动缓存  中获取的");
            //返回代表使用value一次
            value.useAction();//使用一次加一
            return value;
        }
        //2、从内存缓存中查找   如果有   从内存缓存中移动到活动缓存  然后返回
        value = memoryCache.get(key);
        if (null != value) {
            memoryCache.manualRemove(key);//移除内存缓存
            activeCache.put(key, value);//把内存缓存中的元素   加入到活动缓存中
            Log.d(TAG, "cacheAction--->本次加载是在  内存缓存  中获取的");
            //返回代表使用value一次
            value.useAction();//使用一次加一
            return value;
        }
        //3、从磁盘缓存中去找  如果有  把磁盘缓存中的元素加入到活动缓存中
        value = diskLruCache.get(key);
        if (null != value) {
            Log.d(TAG, "cacheAction--->value-->  "+value);
            //把磁盘缓存中的元素--->加入到活动缓存
            activeCache.put(key, value);

            //把磁盘缓存中的元素--->加入到内存缓存
            //memoryCache.put(key,value);

            Log.d(TAG, "cacheAction--->本次加载是在  磁盘缓存  中获取的");
            //返回代表使用value一次
            value.useAction();//使用一次加一
            return value;
        }
        //4、真正加载外部资源   去网络上加载  本地加载
        value = new LoadDataManager().loadResource(path, this, glideContext);
        if (value != null) {
            return value;
        }
        return null;
    }

    /**
     * 回调告诉外界，Value资源不再使用了
     *
     * @param key
     * @param value
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        //把活动缓存操作的Value资源 加入到内存缓存
        if (key != null && value != null) {
            memoryCache.put(key,value);
        }
    }

    /**
     * LRU最少使用的元素会被移除
     *
     * @param key
     * @param oldValue
     */
    @Override
    public void entryRemoveMemoryCache(String key, Value oldValue) {

    }

    //加载外部资源成功
    @Override
    public void responseSuccess(Value value) {
        if (null != value) {
            saveCache(key,value);
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    //加载外部资源失败
    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException--->加载外部资源失败" +e.getMessage());
    }

    /**
     * 保存到缓存中
     * @param key
     * @param value
     */
    public void saveCache(String key,Value value){
        Log.d(TAG, "saveCache--->加载外部资源成功  保存到缓存中 key:"+key+",value:" +value);
        value.setKey(key);
        if (diskLruCache != null) {
            diskLruCache.put(key,value);//保存到磁盘缓存
        }
    }
}
