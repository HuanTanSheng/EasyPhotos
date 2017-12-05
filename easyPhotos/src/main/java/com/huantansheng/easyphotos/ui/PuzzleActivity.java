package com.huantansheng.easyphotos.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;
import com.huantansheng.easyphotos.models.puzzle.PuzzleUtils;
import com.huantansheng.easyphotos.models.puzzle.PuzzleView;
import com.huantansheng.easyphotos.ui.adapter.PuzzleAdapter;

import java.util.ArrayList;

/**
 * 拼图界面
 * Created by huan on 2017/12/4.
 */

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener, PuzzleAdapter.OnItemClickListener {

    public static void startWithPhotos(Activity act, ArrayList<Photo> photos, int requestCode) {
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, true);
        intent.putParcelableArrayListExtra(Key.PUZZLE_FILES, photos);
        act.startActivityForResult(intent, requestCode);
    }

    public static void startWithPaths(Activity act, ArrayList<String> paths, int requestCode) {
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, false);
        intent.putStringArrayListExtra(Key.PUZZLE_FILES, paths);
        act.startActivityForResult(intent, requestCode);
    }

    ArrayList<Photo> photos = null;
    ArrayList<String> paths = null;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    boolean fileTypeIsPhoto;

    private PuzzleView puzzleView;
    private PuzzleLayout puzzleLayout;
    private RecyclerView rvPuzzleTemplet;
    private PuzzleAdapter puzzleAdapter;
    private int fileCount = 0;

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
        initPuzzleView();
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvPuzzleTemplet = (RecyclerView) findViewById(R.id.rv_puzzle_templet);
        puzzleAdapter = new PuzzleAdapter();
        puzzleAdapter.setOnItemClickListener(this);
        rvPuzzleTemplet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPuzzleTemplet.setAdapter(puzzleAdapter);
        puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
    }

    private void initPuzzleView() {
        puzzleLayout = PuzzleUtils.getPuzzleLayout(1, fileCount, 0);
        puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        puzzleView.setBackgroundColor(ContextCompat.getColor(this, R.color.bottom_bar_easy_photos));
        puzzleView.setPuzzleLayout(puzzleLayout);
        puzzleView.setTouchEnable(true);
        puzzleView.setNeedDrawLine(false);
        puzzleView.setNeedDrawOuterLine(false);
        puzzleView.setLineSize(4);
        puzzleView.setLineColor(Color.GRAY);
        puzzleView.setSelectedLineColor(ContextCompat.getColor(this, R.color.puzzle_selected_frame_easy_photos));
        puzzleView.setHandleBarColor(ContextCompat.getColor(this, R.color.puzzle_selected_controller_easy_photos));
        puzzleView.setAnimateDuration(300);
        puzzleView.setPiecePadding(0);
        puzzleView.post(new Runnable() {
            @Override
            public void run() {
                loadPhoto();
            }
        });
    }

    private void loadPhoto() {
        puzzleView.addPieces(bitmaps);
    }

    private void initData() {
        Intent intent = getIntent();
        fileTypeIsPhoto = intent.getBooleanExtra(Key.PUZZLE_FILE_IS_PHOTO, false);
        if (fileTypeIsPhoto) {
            photos = intent.getParcelableArrayListExtra(Key.PUZZLE_FILES);
            fileCount = photos.size();
            for (Photo photo : photos) {
                Bitmap b = BitmapFactory.decodeFile(photo.path);
                bitmaps.add(b);
            }
        } else {
            paths = intent.getStringArrayListExtra(Key.PUZZLE_FILES);
            fileCount = paths.size();
            for (String path : paths) {
                Bitmap b = BitmapFactory.decodeFile(path);
                bitmaps.add(b);
            }
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (R.id.iv_back == id) {
            finish();
        } else if (R.id.tv_done == id) {
            Intent intent = new Intent();

//            intent.putExtra(EasyPhotos.RESULT_PUZZLE, );
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onItemClick(PuzzleLayout puzzleLayout, int themeId) {
        puzzleView.setPuzzleLayout(puzzleLayout);
    }
}
