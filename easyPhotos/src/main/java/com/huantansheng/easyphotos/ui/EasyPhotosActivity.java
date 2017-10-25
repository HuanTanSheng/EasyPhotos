package com.huantansheng.easyphotos.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.ad.AdEntity;
import com.huantansheng.easyphotos.ad.AdListener;
import com.huantansheng.easyphotos.adapter.AlbumItemsAdapter;
import com.huantansheng.easyphotos.adapter.PhotosAdapter;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.Album.AlbumModel;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.view.PressedImageView;
import com.huantansheng.easyphotos.ui.view.PressedTextView;
import com.huantansheng.easyphotos.utils.file.FileUtils;
import com.huantansheng.easyphotos.utils.media.MediaScannerConnectionUtils;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EasyPhotosActivity extends AppCompatActivity implements AlbumModel.CallBack, View.OnClickListener, AlbumItemsAdapter.OnClickListener, PhotosAdapter.OnClickListener, AdListener {

    private static final String TAG = "EasyPhotosActivity";

    private boolean isShowCamera, onlyStartCamera;

    private String fileProviderText;//fileProvider的authorities字符串
    private File mTempImageFile;

    private AlbumModel albumModel;

    private ArrayList<String> resultList = new ArrayList<>();

    private RecyclerView rvPhotos;
    private PhotosAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView rvAlbumItems;
    private RelativeLayout rootViewAlbumItems;
    private View mBottomBar;
    private PressedTextView tvAlbumItems;
    private PressedTextView tvDone;

    private AnimatorSet setHide;
    private AnimatorSet setShow;

    public static void start(Activity activity, boolean onlyStartCamera, boolean isShowCamera, String fileProviderText, int requestCode) {
        Intent intent = new Intent(activity, EasyPhotosActivity.class);
        intent.putExtra(Key.IS_SHOW_CAMERA, isShowCamera);
        intent.putExtra(Key.FILE_PROVIDER_TEXT, fileProviderText);
        intent.putExtra(Key.ONLY_START_CAMERA, onlyStartCamera);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_photos);
        if (Setting.needResetOrientation()) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(Setting.orientation);
                if (Setting.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    if (Setting.shouldDestroy) {
                        Setting.shouldDestroy = false;
                        return;
                    }
                }

            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(Setting.orientation);
                if (Setting.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    if (Setting.shouldDestroy) {
                        Setting.shouldDestroy = false;
                        return;
                    }
                }
            }
        }
        hideActionBar();
        EasyPhotos.setAdListener(this);
        initConfig();
        if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
            hasPermissions();
        }

    }

    private void hasPermissions() {
        if (onlyStartCamera) {
            launchCamera(Code.CODE_REQUEST_CAMERA);
            return;
        }
        albumModel = new AlbumModel(this, isShowCamera, this);
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
        isShowCamera = intent.getBooleanExtra(Key.IS_SHOW_CAMERA, false);
        onlyStartCamera = intent.getBooleanExtra(Key.ONLY_START_CAMERA, false);
        fileProviderText = intent.getStringExtra(Key.FILE_PROVIDER_TEXT);
    }

    /**
     * 启动相机
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchCamera(int requestCode) {
        if (TextUtils.isEmpty(fileProviderText))
            throw new RuntimeException(TAG + " : please set fileProviderText");
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
                Uri imageUri = FileProvider.getUriForFile(this, fileProviderText, mTempImageFile);//通过FileProvider创建一个content类型的Uri
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //对目标应用临时授权该Uri所代表的文件
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

        switch (resultCode) {
            case RESULT_OK:
                if (Code.CODE_REQUEST_CAMERA == requestCode) {
                    if (null != mTempImageFile) onCameraResult(mTempImageFile);
                    return;
                }


                break;
            case RESULT_FIRST_USER:
                break;
            case RESULT_CANCELED:
                if (Code.CODE_REQUEST_CAMERA == requestCode) {
                    // 删除临时文件
                    while (mTempImageFile != null && mTempImageFile.exists()) {
                        boolean success = mTempImageFile.delete();
                        if (success) {
                            mTempImageFile = null;
                        }
                    }
                    return;
                }
                break;
            default:
                break;
        }
    }

    private void onCameraResult(File imageFile) {
        if (imageFile != null) {
            // 更新媒体库
            MediaScannerConnectionUtils.refresh(this, imageFile);
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EasyPhotos.RESULT, resultList);
            setResult(RESULT_OK, data);
            done();
        }
    }

    @Override
    public void onAlbumWorkedCallBack() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAlbumWorkedDo();
            }
        });
    }

    private void onAlbumWorkedDo() {
        initView();
    }

    private void initView() {
        tvDone = (PressedTextView) findViewById(R.id.tv_done);
        tvDone.setOnClickListener(this);
        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        ((SimpleItemAnimator) rvPhotos.getItemAnimator()).setSupportsChangeAnimations(false);//去除item更新的闪光
        adapter = new PhotosAdapter(this, albumModel.getCurrAlbumItemPhotos(0), this);
        int columns = getResources().getInteger(R.integer.photos_columns);
        gridLayoutManager = new GridLayoutManager(this, columns);
        rvPhotos.setLayoutManager(gridLayoutManager);
        rvPhotos.setAdapter(adapter);
        PressedImageView ivBack = (PressedImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        mBottomBar = findViewById(R.id.m_bottom_bar);
        mBottomBar.setOnClickListener(this);
        tvAlbumItems = (PressedTextView) findViewById(R.id.tv_album_items);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(0).name);
        tvAlbumItems.setOnClickListener(this);
        initAlbumItems();
        shouldShowMenuDone();
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initAlbumItems() {
        rootViewAlbumItems = (RelativeLayout) findViewById(R.id.root_view_album_items);
        rootViewAlbumItems.setOnClickListener(this);
        rvAlbumItems = (RecyclerView) findViewById(R.id.rv_album_items);
        AlbumItemsAdapter albumItemsAdapter = new AlbumItemsAdapter(this, albumModel.getAlbumItems(), 0, this);
        rvAlbumItems.setLayoutManager(new LinearLayoutManager(this));
        rvAlbumItems.setAdapter(albumItemsAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_album_items == id) {
            showAlbumItems(View.GONE == rootViewAlbumItems.getVisibility());
        } else if (R.id.root_view_album_items == id) {
            showAlbumItems(false);
        } else if (R.id.iv_back == id) {
            setResult(RESULT_CANCELED);
            done();
        } else if (R.id.tv_done == id) {
            Intent intent = new Intent();
            resultList.addAll(Result.photos);
            intent.putStringArrayListExtra(EasyPhotos.RESULT, resultList);
            setResult(RESULT_OK, intent);
            done();
        }

    }

    private void showAlbumItems(boolean isShow) {
        if (null == setShow) {
            newAnimators();
        }
        if (isShow) {
            rootViewAlbumItems.setVisibility(View.VISIBLE);
            setShow.start();
        } else {
            setHide.start();
        }

    }

    private void newAnimators() {
        newHideAnim();
        newShowAnim();
    }

    private void newShowAnim() {
        ObjectAnimator translationShow = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", mBottomBar.getTop() - rvAlbumItems.getY(), rvAlbumItems.getY());
        ObjectAnimator alphaShow = ObjectAnimator.ofFloat(rootViewAlbumItems, "alpha", 0.0f, 1.0f);
        translationShow.setDuration(300);
        setShow = new AnimatorSet();
        setShow.setInterpolator(new AccelerateDecelerateInterpolator());
        setShow.play(translationShow).with(alphaShow);
    }

    private void newHideAnim() {
        ObjectAnimator translationHide = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", rvAlbumItems.getY(), mBottomBar.getTop() - rvAlbumItems.getY());
        ObjectAnimator alphaHide = ObjectAnimator.ofFloat(rootViewAlbumItems, "alpha", 1.0f, 0.0f);
        translationHide.setDuration(200);
        setHide = new AnimatorSet();
        setHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootViewAlbumItems.setVisibility(View.GONE);
            }
        });
        setHide.setInterpolator(new AccelerateInterpolator());
        setHide.play(translationHide).with(alphaHide);
    }

    @Override
    public void onAlbumItemClick(int position) {
        updatePhotos(position);
        showAlbumItems(false);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(position).name);
    }

    private void updatePhotos(int currAlbumItemIndex) {
        adapter.setData(albumModel.getCurrAlbumItemPhotos(currAlbumItemIndex));
    }

    private void shouldShowMenuDone() {
        if (Result.photos.size() == 0) {
            if (View.VISIBLE == tvDone.getVisibility()) {
                ScaleAnimation scaleHide = new ScaleAnimation(1f, 0f, 1f, 0f);
                scaleHide.setDuration(200);
                tvDone.startAnimation(scaleHide);
            }
            tvDone.setVisibility(View.GONE);
        } else {
            if (View.GONE == tvDone.getVisibility()) {
                ScaleAnimation scaleShow = new ScaleAnimation(0f, 1f, 0f, 1f);
                scaleShow.setDuration(200);
                tvDone.startAnimation(scaleShow);
            }
            tvDone.setVisibility(View.VISIBLE);
        }
        tvDone.setText(getString(R.string.selector_action_done, Result.photos.size(), Setting.count));
    }

    @Override
    public void onPhotoClick(int position) {
        int realPosition = isShowCamera ? position - 1 : position;
        Toast.makeText(this, "" + realPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectorOutOfMax() {
        Toast.makeText(this, getString(R.string.selector_reach_max_image_hint, Setting.count), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectorChanged() {
        shouldShowMenuDone();
    }

    @Override
    public void onCameraClicked() {
        launchCamera(Code.CODE_REQUEST_CAMERA);
    }

    private void done() {
        EasyPhotos.clear();
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        done();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAdLoaded(final AdEntity adEntity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EasyPhotosActivity.this, adEntity.imageUrl + adEntity.content + adEntity.title, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
