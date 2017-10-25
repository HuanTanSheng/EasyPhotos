package com.huantansheng.easyphotos.sample;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.ad.AdEntity;
import com.huantansheng.easyphotos.ad.AdListener;
import com.huantansheng.easyphotos.sample.thisAppGlideModule.GlideApp;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonC, buttonA, buttonAll;
    private ImageView ivImage;
    private int position = 0;
    private ArrayList<String> images = new ArrayList<>();
    private AdListener adListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        buttonC = (Button) findViewById(R.id.buttonC);
        buttonC.setOnClickListener(this);
        buttonA = (Button) findViewById(R.id.buttonA);
        buttonA.setOnClickListener(this);
        buttonAll = (Button) findViewById(R.id.buttonAll);
        buttonAll.setOnClickListener(this);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        ivImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonC:
                EasyPhotos.from(this, EasyPhotos.StartupType.CAMERA)
                        .count(1)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")//fileProvider的authorities字符串
                        .start(101);
                break;
            case R.id.buttonA:
                EasyPhotos.from(this, EasyPhotos.StartupType.ALBUM)
                        .count(16)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")//fileProvider的authorities字符串
                        .start(101);
                EasyPhotos.setAd(new AdEntity("s", "d", "3"));

                break;
            case R.id.buttonAll:
                EasyPhotos.from(this, EasyPhotos.StartupType.ALL)
                        .count(1)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")//fileProvider的authorities字符串
                        .setOrientationLandscape()
                        .start(101);
                break;
            case R.id.iv_image:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            images.clear();
            images.addAll(data.getStringArrayListExtra(EasyPhotos.RESULT));
            if (images.size() == 0) return;
            GlideApp.with(this).load(images.get(position)).into(ivImage);
        } else if (RESULT_CANCELED == resultCode) {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }
}
