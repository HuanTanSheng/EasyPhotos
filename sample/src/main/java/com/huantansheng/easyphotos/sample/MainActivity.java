package com.huantansheng.easyphotos.sample;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 相机
     */
    private Button btCamera;
    /**
     * 相册(无相机单选）
     */
    private Button btAlbumSingle;
    /**
     * 相册(无相机多选）
     */
    private Button btAlbumMulti;
    /**
     * 相册（有相机单选）
     */
    private Button btAllSingle;
    /**
     * 相册（有相机多选）
     */
    private Button btAllMulti;
    /**
     * 相册（带广告）
     */
    private Button btAd;
    /**
     * 相册（带默认勾选）
     */
    private Button btSelected;
    /**
     * 相册（尺寸限制）
     */
    private Button btSize;
    /**
     * 选择的图片集
     */
    private ArrayList<String> images = new ArrayList<>();
    private MainAdapter adapter;
    private RecyclerView rvImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            images.clear();
            images.addAll(data.getStringArrayListExtra(EasyPhotos.RESULT));
            adapter.notifyDataSetChanged();
            rvImage.smoothScrollToPosition(0);
        } else if (RESULT_CANCELED == resultCode) {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        rvImage = (RecyclerView) findViewById(R.id.rv_image);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new MainAdapter(this, images);
        rvImage.setLayoutManager(linearLayoutManager);
        rvImage.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvImage);

        //**************
        btCamera = (Button) findViewById(R.id.bt_camera);
        btCamera.setOnClickListener(this);
        btAlbumSingle = (Button) findViewById(R.id.bt_album_single);
        btAlbumSingle.setOnClickListener(this);
        btAlbumMulti = (Button) findViewById(R.id.bt_album_multi);
        btAlbumMulti.setOnClickListener(this);
        btAllSingle = (Button) findViewById(R.id.bt_all_single);
        btAllSingle.setOnClickListener(this);
        btAllMulti = (Button) findViewById(R.id.bt_all_multi);
        btAllMulti.setOnClickListener(this);
        btAd = (Button) findViewById(R.id.bt_ad);
        btAd.setOnClickListener(this);
        btSelected = (Button) findViewById(R.id.bt_selected);
        btSelected.setOnClickListener(this);
        btSize = (Button) findViewById(R.id.bt_size);
        btSize.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_camera://单独使用相机
                EasyPhotos.with(this, EasyPhotos.StartupType.CAMERA)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .start(101);
                break;
            case R.id.bt_album_single://相册单选，无相机功能
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM)
                        .start(101);
                break;
            case R.id.bt_album_multi://相册多选，无相机功能
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM)
                        .setCount(9)
                        .start(101);
                break;
            case R.id.bt_all_single://相册单选，有相机功能
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM_CAMERA)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .start(101);
                break;
            case R.id.bt_all_multi://相册多选，有相机功能
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM_CAMERA)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .start(101);
                break;
            case R.id.bt_ad://相册中包含广告
                //需要在启动前创建广告view
                // 广告view不能有父布局
                // 广告view可以包含子布局
                // 为了确保广告view地址不变，设置final会更安全
                final TextView photosAdView = new TextView(this);//图片集合中的广告view
                photosAdView.setTextColor(Color.WHITE);
                final TextView albumItemsAdView = new TextView(this);//专辑项目列表中的广告view

                //启动方法，装在广告view
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM_CAMERA)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .setAdView(photosAdView, albumItemsAdView)
                        .start(101);

                //模拟真实广告，网络回调广告数据后绑定广告数据
                btAd.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        photosAdView.setText("我是图片集广告");
                        albumItemsAdView.setText("我是专辑项目列表广告");
                    }
                }, 5000);
                break;
            case R.id.bt_selected://相册中包含默认勾选图片
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM_CAMERA)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .setSelectedPhotos(images)
                        .start(101);
                break;
            case R.id.bt_size://只显示限制尺寸以上的图片
                EasyPhotos.with(this, EasyPhotos.StartupType.ALBUM_CAMERA)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .setMinSize(500,500)
                        .start(101);
                break;
        }
    }
}
