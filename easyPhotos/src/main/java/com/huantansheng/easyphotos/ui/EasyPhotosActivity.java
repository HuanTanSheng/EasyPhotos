package com.huantansheng.easyphotos.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.adapter.AlbumItemsAdapter;
import com.huantansheng.easyphotos.ui.adapter.PhotosAdapter;
import com.huantansheng.easyphotos.ui.widget.PressedImageView;
import com.huantansheng.easyphotos.ui.widget.PressedTextView;
import com.huantansheng.easyphotos.utils.media.MediaScannerConnectionUtils;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.huantansheng.easyphotos.utils.settings.SettingsUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EasyPhotosActivity extends AppCompatActivity implements AlbumModel.CallBack, View.OnClickListener, AlbumItemsAdapter.OnClickListener, PhotosAdapter.OnClickListener {

    private static final String TAG = "EasyPhotosActivity";

    private boolean isShowCamera, onlyStartCamera;

    private String fileProviderText;//fileProvider的authorities字符串
    private File mTempImageFile;

    private AlbumModel albumModel;
    private ArrayList<Object> photoList = new ArrayList<>();
    private ArrayList<Object> albumItemList = new ArrayList<>();

    private ArrayList<String> resultList = new ArrayList<>();

    private RecyclerView rvPhotos;
    private PhotosAdapter photosAdapter;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView rvAlbumItems;
    private AlbumItemsAdapter albumItemsAdapter;
    private RelativeLayout rootViewAlbumItems;
    private View mBottomBar;
    private PressedTextView tvAlbumItems;
    private PressedTextView tvDone;

    private AnimatorSet setHide;
    private AnimatorSet setShow;

    private int columns = 3;
    private int albumItemsAdIndex = 0;
    private PressedTextView tvClear;
    private int currAlbumItemIndex = 0;

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
        hideActionBar();
//        EasyPhotos.setAdListener(this);
        initConfig();
        mBottomBar = findViewById(R.id.m_bottom_bar);
        rootViewAlbumItems = (RelativeLayout) findViewById(R.id.root_view_album_items);
        if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
            hasPermissions();
        }

    }

    private void hasPermissions() {
        if (onlyStartCamera) {
            launchCamera(Code.CODE_REQUEST_CAMERA);
            return;
        }
        albumModel = AlbumModel.getInstance(this, isShowCamera, this);
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
                Snackbar.make(mBottomBar, R.string.permissions_again, Snackbar.LENGTH_INDEFINITE)
                        .setAction("go", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (PermissionUtil.checkAndRequestPermissionsInActivity(EasyPhotosActivity.this, getNeedPermissions())) {
                                    hasPermissions();
                                }
                            }
                        })
                        .show();
            }

            @Override
            public void onFailed() {
                Snackbar.make(mBottomBar, R.string.permissions_die, Snackbar.LENGTH_INDEFINITE)
                        .setAction("go", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SettingsUtils.startMyApplicationDetailsForResult(EasyPhotosActivity.this, getPackageName());
                                EasyPhotosActivity.this.finish();
                            }
                        })
                        .show();
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
            createCameraTempImageFile();
            if (mTempImageFile != null) {
                Uri imageUri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this, fileProviderText, mTempImageFile);//通过FileProvider创建一个content类型的Uri
                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //对目标应用临时授权该Uri所代表的文件
                } else {
                    imageUri = Uri.fromFile(mTempImageFile);
                }
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                startActivityForResult(cameraIntent, requestCode);
            } else {
                Toast.makeText(this, R.string.camera_temp_file_error_easy_photos, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.msg_no_camera_easy_photos, Toast.LENGTH_SHORT).show();
        }
    }

    private void createCameraTempImageFile() {
        File dir = new File(Environment.getExternalStorageDirectory(), File.separator + "DCIM" + File.separator + "Camera" + File.separator);
        if (!dir.exists() || !dir.isDirectory()) {
            if (!dir.mkdirs()) {
                dir = getExternalFilesDir(null);
                if (null == dir || !dir.exists()) {
                    dir = getFilesDir();
                    if (null == dir || !dir.exists()) {
                        String cacheDirPath = File.separator + "data" + File.separator + "data" + File.separator + getPackageName() + File.separator + "cache" + File.separator;
                        dir = new File(cacheDirPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                    }
                }
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.getDefault());
        String imageName = "IMG_%s.jpg";
        String filename = String.format(imageName, dateFormat.format(new Date()));
        mTempImageFile = new File(dir, filename);
//        if (!mTempImageFile.exists()) {
//            try {
//                if (mTempImageFile.createNewFile()) {
//                    mTempImageFile = null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case RESULT_OK:
                if (Code.CODE_REQUEST_CAMERA == requestCode) {
                    if (mTempImageFile == null || !mTempImageFile.exists()) {
                        throw new RuntimeException("EasyPhotos拍照保存的图片不存在");
                    }
                    onCameraResult(mTempImageFile);
                    return;
                }

                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    photosAdapter.notifyDataSetChanged();
                    shouldShowMenuDone();
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
        MediaScannerConnectionUtils.refresh(this, imageFile);// 更新媒体库
        Intent data = new Intent();
        resultList.add(imageFile.getAbsolutePath());
        data.putStringArrayListExtra(EasyPhotos.RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();

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
        if (albumModel.getAlbumItems().isEmpty()) {
            Toast.makeText(this, R.string.no_photos, Toast.LENGTH_SHORT).show();
            if (isShowCamera) launchCamera(Code.CODE_REQUEST_CAMERA);
            else finish();
            return;
        }
        columns = getResources().getInteger(R.integer.photos_columns_easy_photos);
        tvAlbumItems = (PressedTextView) findViewById(R.id.tv_album_items);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(0).name);
        PressedImageView ivAlbumItems = (PressedImageView) findViewById(R.id.iv_album_items);
        PressedImageView ivBack = (PressedImageView) findViewById(R.id.iv_back);
        tvDone = (PressedTextView) findViewById(R.id.tv_done);
        tvClear = (PressedTextView) findViewById(R.id.tv_clear);
        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        ((SimpleItemAnimator) rvPhotos.getItemAnimator()).setSupportsChangeAnimations(false);//去除item更新的闪光
        photoList.clear();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(0));
        if (Setting.hasPhotosAd()) {
            photoList.add(0, Setting.photosAdView);
        }
        photosAdapter = new PhotosAdapter(this, photoList, this);

        gridLayoutManager = new GridLayoutManager(this, columns);
        if (Setting.hasPhotosAd()) {
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (position == 0) {
                        return gridLayoutManager.getSpanCount();//独占一行
                    } else {
                        return 1;//只占一行中的一列
                    }
                }
            });
        }
        rvPhotos.setLayoutManager(gridLayoutManager);
        rvPhotos.setAdapter(photosAdapter);

        tvClear.setOnClickListener(this);
        tvDone.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        mBottomBar.setOnClickListener(this);
        ivAlbumItems.setOnClickListener(this);
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

        rootViewAlbumItems.setOnClickListener(this);
        rvAlbumItems = (RecyclerView) findViewById(R.id.rv_album_items);
        albumItemList.clear();
        albumItemList.addAll(albumModel.getAlbumItems());

        if (Setting.hasAlbumItemsAd()) {
            albumItemsAdIndex = 2;
            if (albumItemList.size() < albumItemsAdIndex + 1) {
                albumItemsAdIndex = albumItemList.size() - 1;
            }
            albumItemList.add(albumItemsAdIndex, Setting.albumItemsAdView);
        }
        albumItemsAdapter = new AlbumItemsAdapter(this, albumItemList, 0, this);
        rvAlbumItems.setLayoutManager(new LinearLayoutManager(this));
        rvAlbumItems.setAdapter(albumItemsAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_album_items == id || R.id.iv_album_items == id) {
            showAlbumItems(View.GONE == rootViewAlbumItems.getVisibility());
        } else if (R.id.root_view_album_items == id) {
            showAlbumItems(false);
        } else if (R.id.iv_back == id) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (R.id.tv_done == id) {
            Intent intent = new Intent();
            resultList.addAll(Result.photos);
            intent.putStringArrayListExtra(EasyPhotos.RESULT, resultList);
            setResult(RESULT_OK, intent);
            finish();
        } else if (R.id.tv_clear == id) {
            Result.removeAll();
            photosAdapter.notifyDataSetChanged();
            shouldShowMenuDone();
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
    public void onAlbumItemClick(int position,int realPosition) {
        updatePhotos(realPosition);
        showAlbumItems(false);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(position).name);
    }

    private void updatePhotos(int currAlbumItemIndex) {
        this.currAlbumItemIndex = currAlbumItemIndex;
        photoList.clear();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(currAlbumItemIndex));
        if (Setting.hasPhotosAd()) {
            photoList.add(0, Setting.photosAdView);
        }
        photosAdapter.notifyDataSetChanged();
        rvPhotos.scrollToPosition(0);
    }

    private void shouldShowMenuDone() {
        if (Result.isEmpty()) {
            if (View.VISIBLE == tvDone.getVisibility()) {
                ScaleAnimation scaleHide = new ScaleAnimation(1f, 0f, 1f, 0f);
                scaleHide.setDuration(200);
                tvDone.startAnimation(scaleHide);
            }
            tvDone.setVisibility(View.GONE);
            tvClear.setVisibility(View.GONE);
        } else {
            if (View.GONE == tvDone.getVisibility()) {
                ScaleAnimation scaleShow = new ScaleAnimation(0f, 1f, 0f, 1f);
                scaleShow.setDuration(200);
                tvDone.startAnimation(scaleShow);
            }
            tvDone.setVisibility(View.VISIBLE);
            if (Setting.count > 1)
                tvClear.setVisibility(View.VISIBLE);
        }
        tvDone.setText(getString(R.string.selector_action_done_easy_photos, Result.count(), Setting.count));
    }

    @Override
    public void onPhotoClick(int position, int realPosition) {
//        Toast.makeText(this, "position+" + position + "------realPosition+" + realPosition, Toast.LENGTH_LONG).show();
        PreviewEasyPhotosActivity.start(EasyPhotosActivity.this, currAlbumItemIndex, realPosition);

    }

    @Override
    public void onSelectorOutOfMax() {
        Toast.makeText(this, getString(R.string.selector_reach_max_image_hint_easy_photos, Setting.count), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelectorChanged() {
        shouldShowMenuDone();
    }

    @Override
    public void onCameraClicked() {
        launchCamera(Code.CODE_REQUEST_CAMERA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (rootViewAlbumItems.getVisibility() == View.VISIBLE) {
            showAlbumItems(false);
            return;
        }
        super.onBackPressed();
    }

//    @Override
//    public void onPhotosAdLoaded(final View adView) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updatePhotosAd(adView);
//            }
//        });
//    }

//    private void updatePhotosAd(View adView) {
//        if (!Setting.photosAdView) {
//            throw new RuntimeException("EasyPhotos:使用广告必须执行useAd方法");
//        }
//        photosAdView = adView;
//        resetPhotoListAdView();
//    }

//    private void resetPhotoListAdView() {
//        photoList.remove(0);
//        photoList.add(0, photosAdView);
//        photosAdapter.notifyDataSetChanged();
//    }

//    @Override
//    public void onAlbumItemsAdLoaded(final View adView) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                updateAlbumItemsAdView(adView);
//            }
//        });
//
//    }

//    private void updateAlbumItemsAdView(View adView) {
//        if (!Setting.albumItemsAdView) {
//            throw new RuntimeException("EasyPhotos:使用广告必须执行useAd方法");
//        }
//        albumItemsAdView = adView;
//        resetAlbumItemListAdView();
//    }

//    private void resetAlbumItemListAdView() {
//        albumItemList.remove(albumItemsAdIndex);
//        albumItemList.add(albumItemsAdIndex, albumItemsAdView);
//        albumItemsAdapter.notifyDataSetChanged();
//    }
}
