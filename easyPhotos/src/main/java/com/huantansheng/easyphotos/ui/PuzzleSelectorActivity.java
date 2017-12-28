package com.huantansheng.easyphotos.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.ui.adapter.AlbumItemsAdapter;
import com.huantansheng.easyphotos.ui.adapter.PuzzleSelectorAdapter;
import com.huantansheng.easyphotos.ui.adapter.PuzzleSelectorPreviewAdapter;
import com.huantansheng.easyphotos.ui.widget.PressedTextView;

import java.util.ArrayList;

public class PuzzleSelectorActivity extends AppCompatActivity implements View.OnClickListener, AlbumItemsAdapter.OnClickListener, PuzzleSelectorAdapter.OnClickListener, PuzzleSelectorPreviewAdapter.OnClickListener {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, PuzzleSelectorActivity.class);
        activity.startActivityForResult(intent, Code.REQUEST_PUZZLE_SELECTOR);
    }

    private AlbumModel albumModel;

    private AnimatorSet setShow;
    private AnimatorSet setHide;

    private RelativeLayout rootViewAlbumItems, rootSelectorView;
    private RecyclerView rvAlbumItems;
    private AlbumItemsAdapter albumItemsAdapter;
    private PressedTextView tvAlbumItems;

    private ArrayList<Photo> photoList = new ArrayList<>();
    private PuzzleSelectorAdapter photosAdapter;
    private RecyclerView rvPhotos, rvPreview;
    private PuzzleSelectorPreviewAdapter previewAdapter;
    private ArrayList<Photo> selectedPhotos = new ArrayList<>();

    private PressedTextView tvDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_selector_easy_photos);
        albumModel = AlbumModel.getInstance(this, null);
        initView();
    }

    private void initView() {
        tvAlbumItems = (PressedTextView) findViewById(R.id.tv_album_items);
        tvAlbumItems.setText(albumModel.getAlbumItems().get(0).name);
        rootSelectorView = (RelativeLayout) findViewById(R.id.m_selector_root);
        tvDone = (PressedTextView) findViewById(R.id.tv_done);
        initAlbumItems();
        initPhotos();
        initPreview();
    }

    private void initPreview() {
        rvPreview = (RecyclerView) findViewById(R.id.rv_preview_selected_photos);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        previewAdapter = new PuzzleSelectorPreviewAdapter(this, selectedPhotos, this);
        rvPreview.setLayoutManager(lm);
        rvPreview.setAdapter(previewAdapter);
    }

    private void initPhotos() {
        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        ((SimpleItemAnimator) rvPhotos.getItemAnimator()).setSupportsChangeAnimations(false);//去除item更新的闪光

        photoList.addAll(albumModel.getCurrAlbumItemPhotos(0));
        photosAdapter = new PuzzleSelectorAdapter(this, photoList, this);

        int columns = getResources().getInteger(R.integer.photos_columns_easy_photos);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);
        rvPhotos.setLayoutManager(gridLayoutManager);
        rvPhotos.setAdapter(photosAdapter);
    }

    private void initAlbumItems() {
        rootViewAlbumItems = (RelativeLayout) findViewById(R.id.root_view_album_items);
        rvAlbumItems = (RecyclerView) findViewById(R.id.rv_album_items);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        ArrayList<Object> list = new ArrayList<Object>();
        list.addAll(albumModel.getAlbumItems());
        albumItemsAdapter = new AlbumItemsAdapter(this, list, 0, this);
        rvAlbumItems.setLayoutManager(lm);
        rvAlbumItems.setAdapter(albumItemsAdapter);
    }

    private void setClick(@IdRes int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.iv_back == id) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (R.id.tv_album_items == id || R.id.iv_album_items == id) {
            showAlbumItems(View.GONE == rootViewAlbumItems.getVisibility());
        } else if (R.id.root_view_album_items == id) {
            showAlbumItems(false);
        } else if (R.id.tv_done == id) {
            PuzzleActivity.startWithPhotos(this, selectedPhotos, Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM", "IMG", Code.REQUEST_PUZZLE, false);

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
        ObjectAnimator translationShow = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", rootSelectorView.getTop(), 0);
        ObjectAnimator alphaShow = ObjectAnimator.ofFloat(rootViewAlbumItems, "alpha", 0.0f, 1.0f);
        translationShow.setDuration(300);
        setShow = new AnimatorSet();
        setShow.setInterpolator(new AccelerateDecelerateInterpolator());
        setShow.play(translationShow).with(alphaShow);
    }

    private void newHideAnim() {
        ObjectAnimator translationHide = ObjectAnimator.ofFloat(rvAlbumItems, "translationY", 0, rootSelectorView.getTop());
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
        photoList.clear();
        photoList.addAll(albumModel.getCurrAlbumItemPhotos(currAlbumItemIndex));

        photosAdapter.notifyDataSetChanged();
        rvPhotos.scrollToPosition(0);
    }


    @Override
    public void onBackPressed() {

        if (null != rootViewAlbumItems && rootViewAlbumItems.getVisibility() == View.VISIBLE) {
            showAlbumItems(false);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onPhotoClick(int position) {
        if (selectedPhotos.size() > 8) {
            Toast.makeText(this, getString(R.string.selector_reach_max_image_hint_easy_photos, 9), Toast.LENGTH_SHORT).show();
            return;
        }

        selectedPhotos.add(photoList.get(position));
        previewAdapter.notifyDataSetChanged();
        rvPreview.smoothScrollToPosition(selectedPhotos.size() - 1);

        tvDone.setText(getString(R.string.selector_action_done_easy_photos, selectedPhotos.size(), 9));
        if (selectedPhotos.size() > 1) {
            tvDone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteClick(int position) {
        selectedPhotos.remove(position);
        previewAdapter.notifyDataSetChanged();
        tvDone.setText(getString(R.string.selector_action_done_easy_photos, selectedPhotos.size(), 9));
        if (selectedPhotos.size() < 2) {
            tvDone.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Code.REQUEST_PUZZLE) {
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
