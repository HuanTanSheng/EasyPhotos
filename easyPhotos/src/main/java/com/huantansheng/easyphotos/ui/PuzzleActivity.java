package com.huantansheng.easyphotos.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.ui.widget.PressedImageView;
import com.huantansheng.easyphotos.ui.widget.PressedTextView;
import com.huantansheng.easyphotos.utils.system.SystemUtils;

import java.util.ArrayList;

/**
 * 拼图界面
 * Created by huan on 2017/12/4.
 */

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener {

    public static void startWithPhotos(Activity act, ArrayList<Photo> photos) {
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, true);
        intent.putParcelableArrayListExtra(Key.PUZZLE_FILES, photos);
        act.startActivityForResult(intent, Code.REQUEST_PUZZLE);
    }

    public static void startWithPaths(Activity act, ArrayList<String> paths) {
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, false);
        intent.putStringArrayListExtra(Key.PUZZLE_FILES, paths);
        act.startActivityForResult(intent, Code.REQUEST_PUZZLE);
    }

    ArrayList<Photo> photos = null;
    ArrayList<String> paths = null;
    boolean fileTypeIsPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_easy_photos);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initData();
        initView();
    }

    private void initView() {


    }

    private void initData() {
        Intent intent = getIntent();
        fileTypeIsPhoto = intent.getBooleanExtra(Key.PUZZLE_FILE_IS_PHOTO, false);
        if (fileTypeIsPhoto) {
            photos = intent.getParcelableArrayListExtra(Key.PUZZLE_FILES);
        } else {
            paths = intent.getStringArrayListExtra(Key.PUZZLE_FILES);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (R.id.iv_back == id) {
            finish();
        } else if (R.id.tv_done == id) {
            Intent intent = new Intent();

            intent.putExtra(EasyPhotos.RESULT_PUZZLE, );
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
