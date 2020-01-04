package com.zl.customglide;

public class GlideBuilder {

    /**
     * 创建Glide
     * @return
     */
    public Glide build(){
        RequestManagerRetriever requestManagerRetriever= new RequestManagerRetriever();
        Glide glide = new Glide(requestManagerRetriever);
        return glide;
    }
}
