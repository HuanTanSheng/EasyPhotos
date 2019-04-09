package com.huantansheng.easyphotos.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.util.ArrayList;


public class HolderFragment extends Fragment {

    private static final int HOLDER_REQUEST_CODE = 0x44;
    private SparseArray<EasyPhotos.Callback> mCallbacks = new SparseArray<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startEasyPhoto(EasyPhotos.Callback callback) {
        mCallbacks.put(HOLDER_REQUEST_CODE, callback);
        EasyPhotosActivity.start(this, HOLDER_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode && requestCode == HOLDER_REQUEST_CODE) {
            EasyPhotos.Callback callback = mCallbacks.get(HOLDER_REQUEST_CODE);
            if (callback != null) {
                //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                ArrayList<Photo> resultPhotos = data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                //返回图片地址集合：如果你只需要获取图片的地址，可以用这个
                ArrayList<String> resultPaths = data.getStringArrayListExtra(EasyPhotos.RESULT_PATHS);
                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal = data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);
                callback.onResult(resultPhotos, resultPaths, selectedOriginal);
            }
        }
    }
}
