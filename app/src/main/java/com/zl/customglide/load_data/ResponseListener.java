package com.zl.customglide.load_data;

import com.zl.customglide.resource.Value;

public interface ResponseListener {

    public void responseSuccess(Value value);

    public void responseException(Exception e);
}
