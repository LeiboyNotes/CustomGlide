package com.zl.customglide.live_test_pool;

import android.graphics.Bitmap;

/**
 * 复用池标准
 * 任何的池子都有  添加  获取的特点
 */
public interface BitmapPool {

    /**
     * 存入到服用池
     */
    void put(Bitmap bitmap);

    /**
     * 计算公式：宽 * 高 * 每个像素点的大小字节
     * @param width
     * @param height
     * @param config
     * @return
     */
    Bitmap get(int width,int height,Bitmap.Config config);
}
