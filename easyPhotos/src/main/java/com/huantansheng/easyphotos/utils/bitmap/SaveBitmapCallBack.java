package com.huantansheng.easyphotos.utils.bitmap;

/**
 * 保存图片到本地的回调
 * Created by huan on 2017/12/6.
 */

public interface SaveBitmapCallBack {
    void onSuccess(String path);

    void onFailed(String errorInfo);
}
