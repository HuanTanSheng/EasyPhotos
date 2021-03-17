package com.huantansheng.easyphotos.demo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.navigation.NavigationView;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.PuzzleCallback;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.EasyPhotosActivity;
import com.huantansheng.easyphotos.ui.dialog.LoadingDialog;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.huantansheng.easyphotos.utils.settings.SettingsUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SampleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * 选择的图片集
     */
    private ArrayList<Photo> selectedPhotoList = new ArrayList<>();
    private MainAdapter adapter;
    private RecyclerView rvImage;

    /**
     * 图片列表和专辑项目列表的广告view
     */
    private RelativeLayout photosAdView, albumItemsAdView;

    /**
     * 广告是否加载完成
     */
    private boolean photosAdLoaded = false, albumItemsAdLoaded = false;

    /**
     * 展示bitmap功能的
     */
    private Bitmap bitmap = null;
    private ImageView bitmapView = null;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        initView();
        if (PermissionUtil.checkAndRequestPermissionsInActivity(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            preLoadAlbums();
        }
    }

    /**
     * 预加载相册扫描，可以增加点速度，写不写都行
     * 该方法如果没有授权读取权限的话，是无效的，所以外部加不加权限控制都可以，加的话保证执行，不加也不影响程序正常使用。
     */
    private void preLoadAlbums() {
        EasyPhotos.preLoad(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionUtil.onPermissionResult(this, permissions, grantResults,
                new PermissionUtil.PermissionCallBack() {
                    @Override
                    public void onSuccess() {
                        preLoadAlbums();
                    }

                    @Override
                    public void onShouldShow() {
                    }

                    @Override
                    public void onFailed() {
                    }
                });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
        drawer.clearAnimation();
        drawer.setAnimation(null);
        drawer.setLayoutAnimation(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.clearAnimation();
        navigationView.setAnimation(null);
        navigationView.setLayoutAnimation(null);

        bitmapView = findViewById(R.id.iv_image);
        bitmapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapView.setVisibility(View.GONE);
            }
        });


        rvImage = (RecyclerView) findViewById(R.id.rv_image);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        adapter = new MainAdapter(this, selectedPhotoList);
        rvImage.setLayoutManager(linearLayoutManager);
        rvImage.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SampleFragments.class);
            startActivity(intent);
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        bitmapView.setVisibility(View.GONE);

        int id = item.getItemId();
        switch (id) {
            case R.id.camera://单独使用相机

                EasyPhotos.createCamera(this,false)
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .start(101);//也可以选择链式调用写法

                break;

            case R.id.album_single://相册单选，无相机功能

                EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                        .start(101);//也可以选择链式调用写法

                break;

            case R.id.album_multi://相册多选，无相机功能

                EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                        .setCount(9)
                        .start(101);//也可以选择链式调用写法
                break;

            case R.id.album_camera_single://相册单选，有相机功能

                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .start(101);//也可以选择链式调用写法
                break;

            case R.id.album_camera_multi://相册多选，有相机功能

                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(22)
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                selectedPhotoList.clear();
                                selectedPhotoList.addAll(photos);
                                adapter.notifyDataSetChanged();
                                rvImage.smoothScrollToPosition(0);
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(SampleActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.album_camera_multi_use_width:// 正确的宽高数据，链式调用写法

                EasyPhotos.createAlbum(this, true, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(22)
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                selectedPhotoList.clear();
                                selectedPhotoList.addAll(photos);
                                adapter.notifyDataSetChanged();
                                rvImage.smoothScrollToPosition(0);
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(SampleActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.album_ad://相册中包含广告

                // 需要在启动前创建广告view
                // 广告view不能有父布局
                // 广告view可以包含子布局
                // 广告View的数据可以在任何时候绑定
                initAdViews();

                //启动方法，装载广告view
                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .setCameraLocation(Setting.LIST_FIRST)
                        .setAdView(photosAdView, photosAdLoaded, albumItemsAdView,
                                albumItemsAdLoaded)
                        .start(101);

                break;

            case R.id.album_size://只显示限制尺寸或限制文件大小以上的图片

                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .setMinWidth(500)
                        .setMinHeight(500)
                        .setMinFileSize(1024 * 10)
                        .start(101);

                break;

            case R.id.album_original_usable://显示原图按钮，并且默认选中，按钮可用

                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .setOriginalMenu(true, true, null)
                        .start(101);

                break;

            case R.id.album_original_unusable://显示原图按钮，并且默认不选中，按钮不可用。使用场景举例：仅VIP可以上传原图

                boolean isVip = false;//假设获取用户信息发现该用户不是vip

                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .setOriginalMenu(false, isVip, "该功能为VIP会员特权功能")
                        .start(101);

                break;

            case R.id.album_has_video_gif://相册中显示视频和gif图
                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .setVideo(true)
                        .setGif(true)
                        .start(101);
                break;

            case R.id.album_only_video://相册中只选择视频(相册只有视频 会禁用相机和拼图)
                EasyPhotos.createAlbum(this, true, true, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .filter(Type.VIDEO)
                        .start(101);
                break;

            case R.id.album_no_menu://相册中不显示底部的编辑图标按钮
                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setCount(9)
                        .setPuzzleMenu(false)
                        .setCleanMenu(false)
                        .start(101);
                break;

            case R.id.album_selected://相册中包含默认勾选图片

                EasyPhotos.createAlbum(this, true, false, GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setPuzzleMenu(false)
                        .setCount(9)
                        .setSelectedPhotos(selectedPhotoList)
//                        .setSelectedPhotoPaths(selectedPhotoPathList)两种方式参数类型不同，根据情况任选
                        .start(101);

                break;

            case R.id.addWatermark: //给图片添加水印

                EasyPhotos.createAlbum(this,false,true,GlideEngine.getInstance())
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .setPuzzleMenu(false)
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                selectedPhotoList.clear();
                                adapter.notifyDataSetChanged();

                                //这一步如果图大的话会耗时，但耗时不长，建议在异步操作。另外copy出来的bitmap在确定不用的时候记得回收，如果你用Glide操作过copy
                                // 出来的bitmap那就不要回收了，否则Glide会报错。
                                Bitmap watermark = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.watermark).copy(Bitmap.Config.RGB_565, true);
                                try {
                                    bitmap =
                                            BitmapFactory.decodeStream(getContentResolver().openInputStream(photos.get(0).uri)).copy(Bitmap.Config.ARGB_8888, true);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                //给图片添加水印的api
                                bitmap = EasyPhotos.addWatermark(watermark, bitmap, 1080, 20, 20, true,photos.get(0).orientation);

                                bitmapView.setVisibility(View.VISIBLE);
                                bitmapView.setImageBitmap(bitmap);
                                Toast.makeText(SampleActivity.this, "水印在左下角", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancel() {

                            }
                        });

                break;

            case R.id.puzzle:
                EasyPhotos.createAlbum(this, false, false, GlideEngine.getInstance())
                        .setCount(9)
                        .setPuzzleMenu(false)
                        .setFileProviderAuthority("com.huantansheng.easyphotos.demo.fileprovider")
                        .start(new SelectCallback() {
                            @Override
                            public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                                EasyPhotos.startPuzzleWithPhotos(SampleActivity.this, photos,
                                        Environment.getExternalStorageDirectory().getAbsolutePath(), "AlbumBuilder", false, GlideEngine.getInstance(), new PuzzleCallback() {
                                            @Override
                                            public void onResult(Photo photo) {
                                                selectedPhotoList.clear();
                                                selectedPhotoList.add(photo);
                                                adapter.notifyDataSetChanged();
                                                rvImage.smoothScrollToPosition(0);
                                            }

                                            @Override
                                            public void onCancel() {
                                                Toast.makeText(SampleActivity.this, "Cancel",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(SampleActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });

            case R.id.face_detection://人脸检测，目前仅支持正脸检测
                //暂时不做了。会导致lib过大，而且并不稳定
                break;
            default:
                break;

        }

        return true;
    }

    /**
     * 需要在启动前创建广告view
     * 广告view不能有父布局
     * 广告view可以包含子布局
     * 为了确保广告view地址不变，设置final会更安全
     */

    private void initAdViews() {

        //模拟启动EasyPhotos前广告已经装载完毕
        initPhotosAd();

        //模拟不确定启动EasyPhotos前广告是否装载完毕
        initAlbumItemsAd();

    }

    /**
     * 模拟启动EasyPhotos前广告已经装载完毕
     */
    private void initPhotosAd() {
        photosAdView = (RelativeLayout) getLayoutInflater().inflate(R.layout.ad_photos, null,
                false);//不可以有父布局，所以inflate第二个参数必须为null，并且布局文件必须独立
        ((TextView) photosAdView.findViewById(R.id.tv_title)).setText("photosAd广告");
        ((TextView) photosAdView.findViewById(R.id.tv_content)).setText("github上star" +
                "一下了解EasyPhotos的最新动态,这个布局和数据都是由你定制的");
        photosAdLoaded = true;
    }

    /**
     * 模拟不确定启动EasyPhotos前广告是否装载完毕
     * 模拟5秒后网络回调
     */
    private void initAlbumItemsAd() {
        albumItemsAdView = (RelativeLayout) getLayoutInflater().inflate(R.layout.ad_album_items,
                null, false);//不可以有父布局，所以inflate第二个参数必须为null，并且布局文件必须独立

        //模拟5秒后网络回调
        rvImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ImageView) albumItemsAdView.findViewById(R.id.iv_image)).setImageResource(R.mipmap.ad);
                ((TextView) albumItemsAdView.findViewById(R.id.tv_title)).setText("albumItemsAd广告");
                photosAdLoaded = true;//正常情况可能不知道是先启动EasyPhotos还是数据先回来，所以这里加个标识，如果是后启动EasyPhotos
                // ，那么EasyPhotos会直接加载广告
                EasyPhotos.notifyAlbumItemsAdLoaded();
            }
        }, 5000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (albumItemsAdView != null) {
            if (albumItemsAdView.getParent() != null) {
                ((FrameLayout) (albumItemsAdView.getParent())).removeAllViews();
            }
        }
        if (photosAdView != null) {
            if (photosAdView.getParent() != null) {
                ((FrameLayout) (photosAdView.getParent())).removeAllViews();
            }
        }
        if (RESULT_OK == resultCode) {
            //相机或相册回调
            if (requestCode == 101) {
                //返回对象集合：如果你需要了解图片的宽、高、大小、用户是否选中原图选项等信息，可以用这个
                ArrayList<Photo> resultPhotos =
                        data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);

                //返回图片地址集合时如果你需要知道用户选择图片时是否选择了原图选项，用如下方法获取
                boolean selectedOriginal =
                        data.getBooleanExtra(EasyPhotos.RESULT_SELECTED_ORIGINAL, false);


                selectedPhotoList.clear();
                selectedPhotoList.addAll(resultPhotos);
                adapter.notifyDataSetChanged();
                rvImage.smoothScrollToPosition(0);

                return;
            }

            //为拼图选择照片的回调
            if (requestCode == 102) {

                ArrayList<Photo> resultPhotos =
                        data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                if (resultPhotos.size() == 1) {
                    resultPhotos.add(resultPhotos.get(0));
                }
                selectedPhotoList.clear();
                selectedPhotoList.addAll(resultPhotos);

                EasyPhotos.startPuzzleWithPhotos(this, selectedPhotoList,
                        Environment.getExternalStorageDirectory().getAbsolutePath(),
                        "AlbumBuilder", 103, false, GlideEngine.getInstance());
                return;
            }

            //拼图回调
            if (requestCode == 103) {
                Photo puzzlePhoto = data.getParcelableExtra(EasyPhotos.RESULT_PHOTOS);
                selectedPhotoList.clear();
                selectedPhotoList.add(puzzlePhoto);
                adapter.notifyDataSetChanged();
                rvImage.smoothScrollToPosition(0);
            }


        } else if (RESULT_CANCELED == resultCode) {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();

    }


}
