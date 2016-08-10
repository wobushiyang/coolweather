package com.coolweather.util;

/**
 * Created by ASUS-PC on 2016/7/31.
 */
public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);

}
