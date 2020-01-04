package com.zl.customglide.disk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.zl.customglide.Tool;
import com.zl.customglide.resource.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruCacheImpl {

    private final String TAG = DiskLruCacheImpl.class.getSimpleName();
    private final String DISK_LRUCACHE_DIR = "disk_lrucache_dir";
    private final int APP_VERSION = 999;//版本号  一旦修改这个版本号，之前的缓存失效
    private final int VALUE_COUNT = 1;//通常情况下是1
    private final long MAX_SIZE = 1024 * 1024 * 10;

    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + DISK_LRUCACHE_DIR);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        DiskLruCache.Editor editor = null;
        OutputStream outputStream = null;
        try {
            editor = diskLruCache.edit(key);
            outputStream = editor.newOutputStream(0);// index 不能大于VALUE_COUNT
            Bitmap bitmap = value.getmBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);//把bitmap写入到outPutStream
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                editor.abort();
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(TAG, "put: editor.abort() e:" + e.getMessage());
            }
        } finally {
            try {
                editor.commit();//类似sp
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "put: editor.commit() e:" + e.getMessage());
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "put:   outputStream.close() e:" + e.getMessage());
                }
            }
        }
    }

    public Value get(String key) {
        Tool.checkNotEmpty(key);
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            //判断快照不为空的情况下   再去读取
            if (null != snapshot) {
                Value value = Value.getInstance();
                inputStream = snapshot.getInputStream(0);// index 不能大于VALUE_COUNT
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                value.setmBitmap(bitmap);
                //保存key  唯一标识
                value.setKey(key);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close()  e:" + e.getMessage());
                }
            }
        }
        return null;
    }

}
