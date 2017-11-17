package com.huantansheng.easyphotos.utils.bitmap.face;

import java.util.ArrayList;

/**
 * 面部检测回调接口,无需理会线程问题
 * Created by huan on 2017/11/16.
 */

public interface FaceCallBackOnUiThread {

    void onSuccess(ArrayList<FaceInformation> faces);

    void onFailed();

}
