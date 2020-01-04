package com.zl.customglide;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class Glide {

    RequestManagerRetriever retriever;

    public Glide(RequestManagerRetriever retriever) {
        this.retriever = retriever;
    }

    public static RequestManager width(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }
    public static RequestManager width(Activity activity) {
        return getRetriever(activity).get(activity);
    }
    public static RequestManager width(Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * RequestManager由RequestManagerRetriever创建
     * @param context
     * @return
     */
    public static RequestManagerRetriever getRetriever(Context context){
        return Glide.get(context).getRetriever();
    }

    //Glide new出来的  --  转变
    public static Glide get(Context context){
        return new GlideBuilder().build();
    }

    public RequestManagerRetriever getRetriever(){
        return retriever;
    }
}
