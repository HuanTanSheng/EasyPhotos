package com.huantansheng.easyphotos.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Constant;
import com.huantansheng.easyphotos.utils.file.FileUtils;
import com.huantansheng.easyphotos.utils.media.MediaScannerConnectionUtils;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EasyPhotosActivity extends AppCompatActivity {

    private final String TAG = "EasyPhotosActivity";

    private boolean isShowCamera, onlyStartCamera;
    private int count = 1;

    private String fileProviderText;//fileProvider的authorities字符串

    private File mTempImageFile;
    private ArrayList<String> resultList = new ArrayList<>();

    public static void start(Activity activity, boolean onlyStartCamera, boolean isShowCamera, int count, String fileProviderText, int requestCode) {
        Intent intent = new Intent(activity, EasyPhotosActivity.class);
        intent.putExtra(Constant.IS_SHOW_CAMERA, isShowCamera);
        intent.putExtra(Constant.COUNT, count);
        intent.putExtra(Constant.FILE_PROVIDER_TEXT, fileProviderText);
        intent.putExtra(Constant.ONLY_START_CAMERA, onlyStartCamera);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_photos);
        initConfig();
        if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
            hasPermissions();
        }

    }

    private void hasPermissions() {
        if (onlyStartCamera) {
            launchCamera(Constant.CODE_REQUEST_CAMERA);
            return;
        }
    }


    protected String[] getNeedPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onPermissionResult(this, permissions, grantResults, new PermissionUtil.PermissionCallBack() {
            @Override
            public void onSuccess() {
                hasPermissions();
            }

            @Override
            public void onShouldShow() {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void initConfig() {
        Intent intent = getIntent();
        isShowCamera = intent.getBooleanExtra(Constant.IS_SHOW_CAMERA, false);
        onlyStartCamera = intent.getBooleanExtra(Constant.ONLY_START_CAMERA, false);
        count = intent.getIntExtra(Constant.COUNT, 1);
        fileProviderText = intent.getStringExtra(Constant.FILE_PROVIDER_TEXT);
    }

    /**
     * 启动相机
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchCamera(int requestCode) {
        if (TextUtils.isEmpty(fileProviderText))
            throw new RuntimeException(TAG + " : please set fileProvider");
        toAndroidCamera(requestCode);
    }

    /**
     * 启动系统相机
     *
     * @param requestCode 请求相机的请求码
     */
    private void toAndroidCamera(int requestCode) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                mTempImageFile = FileUtils.createTmpFile(this);
            } catch (IOException e) {
                Log.e(TAG, "toAndroidCamera: ", e);
            }
            if (mTempImageFile != null && mTempImageFile.exists()) {
                Uri imageUri = FileProvider.getUriForFile(this, fileProviderText, mTempImageFile);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                startActivityForResult(cameraIntent, requestCode);
            } else {
                Toast.makeText(this, R.string.camera_temp_file_error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (null != mTempImageFile)
                onCameraResult(mTempImageFile);
        } else {
            // delete tmp file
            while (mTempImageFile != null && mTempImageFile.exists()) {
                boolean success = mTempImageFile.delete();
                if (success) {
                    mTempImageFile = null;
                }
            }
        }
    }

    private void onCameraResult(File imageFile) {
        if (imageFile != null) {
            // notify system the image has change
            MediaScannerConnectionUtils.refresh(this, imageFile);
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EasyPhotos.RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

}
