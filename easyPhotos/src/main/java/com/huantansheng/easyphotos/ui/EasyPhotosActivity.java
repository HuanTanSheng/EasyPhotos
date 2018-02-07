package com.huantansheng.easyphotos.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.ad.AdListener;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.adapter.AlbumItemsAdapter;
import com.huantansheng.easyphotos.ui.adapter.PhotosAdapter;
import com.huantansheng.easyphotos.ui.widget.PressedTextView;
import com.huantansheng.easyphotos.utils.Color.ColorUtils;
import com.huantansheng.easyphotos.utils.String.StringUtils;
import com.huantansheng.easyphotos.utils.media.MediaScannerConnectionUtils;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.huantansheng.easyphotos.utils.settings.SettingsUtils;
import com.huantansheng.easyphotos.utils.system.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EasyPhotosActivity extends AppCompatActivity implements AlbumItemsAdapter.OnClickListener, PhotosAdapter.OnClickListener, AdListener, View.OnClickListener {

    private File mTempImageFile;

    private AlbumModel albumModel;
    private ArrayList<Object> photoList = new ArrayList<>();
    private ArrayList<Object> albumItemList = new ArrayList<>();

    private ArrayList<Photo> resultList = new ArrayList<>();

    private RecyclerView rvPhotos;
    private PhotosAdapter photosAdapter;
    private GridLayoutManager gridLayoutManager;

    private RecyclerView rvAlbumItems;
    private AlbumItemsAdapter albumItemsAdapter;
    private RelativeLayout rootViewAlbumItems;

    private PressedTextView tvAlbumItems, tvDone, tvPreview;
    private TextView tvOriginal;
    private AnimatorSet setHide;
    private AnimatorSet setShow;

    private int currAlbumItemIndex = 0;

    private ImageView ivCamera;

    private LinearLayout mSecondMenus;

    private RelativeLayout permissionView;
    private TextView tvPermission;
    private View mBottomBar;

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, EasyPhotosActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), EasyPhotosActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void start(android.support.v4.app.Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), EasyPhotosActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_photos);
        hideActionBar();
        adaptationStatusBar();
        initSomeViews();
        if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
            hasPermissions();
        }
    }

    private void adaptationStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int statusColor = getWindow().getStatusBarColor();
            if (ColorUtils.isWhiteColor(statusColor)) {
                SystemUtils.getInstance().setStatusDark(this, true);
            }
        }
    }

    private void initSomeViews() {
        mBottomBar = findViewById(R.id.m_bottom_bar);
        permissionView = (RelativeLayout) findViewById(R.id.rl_permissions_view);
        tvPermission = (TextView) findViewById(R.id.tv_permission);
        rootViewAlbumItems = (RelativeLayout) findViewById(R.id.root_view_album_items);
        setClick(R.id.iv_back);
    }

    private void hasPermissions() {
        if (Setting.onlyStartCamera) {
            launchCamera(Code.REQUEST_CAMERA);
            return;
        }
        permissionView.setVisibility(View.GONE);
        AlbumModel.clear();
        AlbumModel.CallBack albumModelCallBack = new AlbumModel.CallBack() {
            @Override
            public void onAlbumWorkedCallBack() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onAlbumWorkedDo();
                    }
                });
            }
        };
        albumModel = AlbumModel.getInstance(this, albumModelCallBack);
    }

    protected String[] getNeedPermissions() {
        if (Setting.isShowCamera) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionUtil.onPermissionResult(this, permissions, grantResults, new PermissionUtil.PermissionCallBack() {
            @Override
            public void onSuccess() {
                hasPermissions();
            }

            @Override
            public void onShouldShow() {
                tvPermission.setText(R.string.permissions_again_easy_photos);
                permissionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (PermissionUtil.checkAndRequestPermissionsInActivity(EasyPhotosActivity.this, getNeedPermissions())) {
                            hasPermissions();
                        }
                    }
                });

            }

            @Override
            public void onFailed() {
                tvPermission.setText(R.string.permissions_die_easy_photos);
                permissionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SettingsUtils.startMyApplicationDetailsForResult(EasyPhotosActivity.this, getPackageName());
                    }
                });

            }
        });
    }


    /**
     * 启动相机
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchCamera(int requestCode) {
        if (TextUtils.isEmpty(Setting.fileProviderAuthority))
            throw new RuntimeException("AlbumBuilder" + " : 请执行 setFileProviderAuthority()方法");
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
            if (mTempImageFile != null && mTempImageFile.exists()) {

                Uri imageUri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(this, Setting.fileProviderAuthority, mTempImageFile);//通过FileProvider创建一个content类型的Uri
                } else {
                    imageUri = Uri.fromFile(mTempImageFile);
                }
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //对目标应用临时授权该Uri所代表的文件

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

        try {
            mTempImageFile = File.createTempFile("IMG", ".jpg", dir);
        } catch (IOException e) {
            e.printStackTrace();
            mTempImageFile = null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Code.REQUEST_SETTING_APP_DETAILS) {
            if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
                hasPermissions();
            }
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                if (Code.REQUEST_CAMERA == requestCode) {
                    if (mTempImageFile == null || !mTempImageFile.exists()) {
                        throw new RuntimeException("EasyPhotos拍照保存的图片不存在");
                    }
                    onCameraResult();
                    return;
                }

                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    if (data.getBooleanExtra(Key.PREVIEW_CLICK_DONE, false)) {
                        done();
                        return;
                    }
                    photosAdapter.change();
                    processOriginalMenu();
                    shouldShowMenuDone();
                    return;
                }

                if (Code.REQUEST_PUZZLE_SELECTOR == requestCode) {
                    Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS);
                    addNewPhoto(puzzlePhoto);
                    return;
                }

                break;
            case RESULT_CANCELED:
                if (Code.REQUEST_CAMERA == requestCode) {
                    // 删除临时文件
                    while (mTempImageFile != null && mTempImageFile.exists()) {
                        boolean success = mTempImageFile.delete();
                        if (success) {
                            mTempImageFile = null;
                        }
                    }
                    if (Setting.onlyStartCamera) {
                        finish();
                    }
                    return;
                }

                if (Code.REQUEST_PREVIEW_ACTIVITY == requestCode) {
                    processOriginalMenu();
                    return;
                }
                break;
            default:
                break;
        }
    }

    private void addNewPhoto(Photo photo) {
        MediaScannerConnectionUtils.refresh(this, photo.path);
        photo.selectedOriginal = Setting.selectedOriginal;

        String albumItem_all_name = getString(R.string.selector_folder_all_easy_photos);
        albumModel.album.getAlbumItem(albumItem_all_name).addImageItem(0, photo);
        String folderPath = new File(photo.path).getParentFile().getAbsolutePath();
        String albumName = StringUtils.getLastPathSegment(folderPath);
        albumModel.album.addAlbumItem(albumName, folderPath, photo.path);
        albumModel.album.getAlbumItem(albumName).addImageItem(0, photo);

        albumItemList.clear();
        albumItemList.addAll(albumModel.getAlbumItems());
        if (Setting.hasAlbumItemsAd()) {
            int albumItemsAdIndex = 2;
            if (albumItemList.size() < albumItemsAdIndex + 1) {
                albumItemsAdIndex = albumItemList.size() - 1;
            }
            albumItemList.add(albumItemsAdIndex, Setting.albumItemsAdView);
        }
        albumItemsAdapter.notifyDataSetChanged();

        if (Setting.count == 1) {
            Result.clear();
            Result.addPhoto(photo);
        } else {
            if (Result.count() >= Setting.count) {
                Toast.makeText(this, getString(R.string.selector_reach_max_image_hint_easy_photos, Setting.count), Toast.LENGTH_SHORT).show();
            } else {
                Result.addPhoto(photo);
            }
        }
        rvAlbumItems.scrollToPosition(0);
        albumItemsAdapter.setSelectedPosition(0);
        shouldShowMenuDone();
    }

    private void onCameraResult() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH:mm:ss", Locale.getDefault());
        String imageName = "IMG_%s.jpg";
        String filename = String.format(imageName, dateFormat.format(new Date()));
        File reNameFile = new File(mTempImageFile.getParentFile(), filename);
        if (!reNameFile.exists()) {
            if (mTempImageFile.renameTo(reNameFile)) {
                mTempImageFile = reNameFile;
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mTempImageFile.getAbsolutePath(), options);
        if (Setting.onlyStartCamera || albumModel.getAlbumItems().isEmpty()) {
            MediaScannerConnectionUtils.refresh(this, mTempImageFile);// 更新媒体库
            Intent data = new Intent();
            Photo photo = new Photo(mTempImageFile.getName(), mTempImageFile.getAbsolutePath(), mTempImageFile.lastModified() / 1000, options.outWidth, options.outHeight, mTempImageFile.length(), options.outMimeType);
            photo.selectedOriginal = Setting.selectedOriginal;
            resultList.add(photo);

            data.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS, resultList);

            data.putExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal);

            ArrayList<String> pathList = new ArrayList<>();
            pathList.add(photo.path);

            data.putStringArrayListExtra(EasyPhotos.RESULT_PATHS, pathList);

            setResult(RESULT_OK, data);
            finish();
            return;
        }

        Photo photo = new Photo(mTempImageFile.getName(), mTempImageFile.getAbsolutePath(), mTempImageFile.lastModified() / 1000, options.outWidth, options.outHeight, mTempImageFile.length(), options.outMimeType);
        addNewPhoto(photo);

    }


    private void onAlbumWorkedDo() {
        initView();
    }

    private void initView() {

        if (albumModel.getAlbumItems().isEmpty()) {
            Toast.makeText(this, R.string.no_photos_easy_photos, Toast.LENGTH_LONG).show();
            if (Setting.isShowCamera) launchCamera(Code.REQUEST_CAMERA);
            else finish();
            return;
        }

        EasyPhotos.setAdListener(this);
        if (Setting.hasPhotosAd()) {
            findViewById(R.id.m_tool_bar_bottom_line).setVisibility(View.GONE);
        }
        ivCamera = (ImageView) findViewById(R.id.fab_camera);
        if (Setting.isShowCamera) {
            ivCamera.setVisibility(View.VISIBLE);
        }
        if (!Setting.showPuzzleMenu) {
            findViewById(R.id.tv_puzzle).setVisibility(View.GONE);
        }
        mSecondMenus = (LinearLayout) findViewById(R.id.m_second_level_menu);
        int columns = getResources().getInteger(R.integer.photos_columns_easy_photos);
        tvAlbumItems = (PressedTextView) findViewById(R.id.tv_album_items);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(0).name);
        tvDone = (PressedTextView) findViewById(R.id.tv_done);
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
        tvOriginal = (TextView) findViewById(R.id.tv_original);
        if (Setting.showOriginalMenu) {
            processOriginalMenu();
        } else {
            tvOriginal.setVisibility(View.GONE);
        }
        tvPreview = (PressedTextView) findViewById(R.id.tv_preview);

        initAlbumItems();
        shouldShowMenuDone();
        setClick(R.id.iv_album_items, R.id.tv_clear, R.id.iv_second_menu, R.id.tv_puzzle);
        setClick(tvAlbumItems, rootViewAlbumItems, tvDone, tvOriginal, tvPreview, ivCamera);

    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initAlbumItems() {

        rvAlbumItems = (RecyclerView) findViewById(R.id.rv_album_items);
        albumItemList.clear();
        albumItemList.addAll(albumModel.getAlbumItems());

        if (Setting.hasAlbumItemsAd()) {
            int albumItemsAdIndex = 2;
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
            done();
        } else if (R.id.tv_clear == id) {
            if (Result.isEmpty()) {
                processSecondMenu();
                return;
            }
            Result.removeAll();
            photosAdapter.change();
            shouldShowMenuDone();
            processSecondMenu();
        } else if (R.id.tv_original == id) {
            if (!Setting.originalMenuUsable) {
                Toast.makeText(this, Setting.originalMenuUnusableHint, Toast.LENGTH_SHORT).show();
                return;
            }
            Setting.selectedOriginal = !Setting.selectedOriginal;
            processOriginalMenu();
            processSecondMenu();
        } else if (R.id.tv_preview == id) {
            PreviewActivity.start(EasyPhotosActivity.this, -1, 0);
        } else if (R.id.fab_camera == id) {
            launchCamera(Code.REQUEST_CAMERA);
        } else if (R.id.iv_second_menu == id) {
            processSecondMenu();
        } else if (R.id.tv_puzzle == id) {
            processSecondMenu();
            PuzzleSelectorActivity.start(this);
        }
    }

    public void processSecondMenu() {
        if (mSecondMenus == null) {
            return;
        }
        if (View.VISIBLE == mSecondMenus.getVisibility()) {
            mSecondMenus.setVisibility(View.INVISIBLE);
            if (Setting.isShowCamera)
                ivCamera.setVisibility(View.VISIBLE);
        } else {
            mSecondMenus.setVisibility(View.VISIBLE);
            if (Setting.isShowCamera)
                ivCamera.setVisibility(View.INVISIBLE);
        }
    }

    private void done() {
        Intent intent = new Intent();
        Result.processOriginal();
        resultList.addAll(Result.photos);
        intent.putParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS, resultList);
        ArrayList<String> resultPaths = new ArrayList<>();
        for (Photo photo : resultList) {
            resultPaths.add(photo.path);
        }
        intent.putStringArrayListExtra(EasyPhotos.RESULT_PATHS, resultPaths);
        intent.putExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, Setting.selectedOriginal);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void processOriginalMenu() {
        if (!Setting.showOriginalMenu) return;
        if (Setting.selectedOriginal) {
            tvOriginal.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_accent));
        } else {
            if (Setting.originalMenuUsable) {
                tvOriginal.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_primary));
            } else {
                tvOriginal.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_primary_dark));
            }
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
        ObjectAnimator translationShow = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", mBottomBar.getTop(), 0);
        ObjectAnimator alphaShow = ObjectAnimator.ofFloat(rootViewAlbumItems, "alpha", 0.0f, 1.0f);
        translationShow.setDuration(300);
        setShow = new AnimatorSet();
        setShow.setInterpolator(new AccelerateDecelerateInterpolator());
        setShow.play(translationShow).with(alphaShow);
    }

    private void newHideAnim() {
        ObjectAnimator translationHide = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", 0, mBottomBar.getTop());
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
    public void onAlbumItemClick(int position, int realPosition) {
        updatePhotos(realPosition);
        showAlbumItems(false);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(realPosition).name);
    }

    private void updatePhotos(int currAlbumItemIndex) {
        this.currAlbumItemIndex = currAlbumItemIndex;
        photoList.clear();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(currAlbumItemIndex));
        if (Setting.hasPhotosAd()) {
            photoList.add(0, Setting.photosAdView);
        }
        photosAdapter.change();
        rvPhotos.scrollToPosition(0);
    }

    private void shouldShowMenuDone() {
        if (Result.isEmpty()) {
            if (View.VISIBLE == tvDone.getVisibility()) {
                ScaleAnimation scaleHide = new ScaleAnimation(1f, 0f, 1f, 0f);
                scaleHide.setDuration(200);
                tvDone.startAnimation(scaleHide);
            }
            tvDone.setVisibility(View.INVISIBLE);
            tvPreview.setVisibility(View.INVISIBLE);
        } else {
            if (View.INVISIBLE == tvDone.getVisibility()) {
                ScaleAnimation scaleShow = new ScaleAnimation(0f, 1f, 0f, 1f);
                scaleShow.setDuration(200);
                tvDone.startAnimation(scaleShow);
            }
            tvDone.setVisibility(View.VISIBLE);
            tvPreview.setVisibility(View.VISIBLE);
        }
        tvDone.setText(getString(R.string.selector_action_done_easy_photos, Result.count(), Setting.count));
    }

    @Override
    public void onPhotoClick(int position, int realPosition) {
        PreviewActivity.start(EasyPhotosActivity.this, currAlbumItemIndex, realPosition);

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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (null != rootViewAlbumItems && rootViewAlbumItems.getVisibility() == View.VISIBLE) {
            showAlbumItems(false);
            return;
        }

        if (null != mSecondMenus && View.VISIBLE == mSecondMenus.getVisibility()) {
            processSecondMenu();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onPhotosAdLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                photosAdapter.change();
            }
        });
    }

    @Override
    public void onAlbumItemsAdLoaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                albumItemsAdapter.notifyDataSetChanged();
            }
        });
    }


    private void setClick(@IdRes int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

    private void setClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
