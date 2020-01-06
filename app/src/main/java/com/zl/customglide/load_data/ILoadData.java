package com.zl.customglide.load_data;

import android.content.Context;

import com.zl.customglide.resource.Value;

/**
 * 加载外部资源  标准
 */
public interface ILoadData {


    public Value loadResource(String path, ResponseListener responseListener, Context context);
}
