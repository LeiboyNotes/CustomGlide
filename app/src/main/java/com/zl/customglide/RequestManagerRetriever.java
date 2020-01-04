package com.zl.customglide;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class RequestManagerRetriever {
    public RequestManager get(FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }

    public RequestManager get(Activity activity) {
        return new RequestManager(activity);
    }
    public RequestManager get(Context context) {
        return new RequestManager(context);
    }
}
