package com.zl.customglide.resource;

import com.zl.customglide.Tool;

public class Key {

    private String key;

    public Key(String key) {
        //加密
        key =Tool.getSHA256StrJava(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
