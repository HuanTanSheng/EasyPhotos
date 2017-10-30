package com.huantansheng.easyphotos.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.models.album.entity.PhotoItem;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.adapter.PreviewPhotosAdapter;
import com.huantansheng.easyphotos.ui.widget.PressedImageView;
import com.huantansheng.easyphotos.ui.widget.PressedTextView;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PreviewEasyPhotosActivity extends AppCompatActivity implements PreviewPhotosAdapter.OnClickListener, View.OnClickListener {

    public static void start(Activity act, int albumItemIndex, int currIndex) {
        Intent intent = new Intent(act, PreviewEasyPhotosActivity.class);
        intent.putExtra(Key.PREVIEW_ALBUM_ITEM_INDEX, albumItemIndex);
        intent.putExtra(Key.PREVIEW_PHOTO_INDEX, currIndex);
        act.startActivityForResult(intent, Code.REQUEST_PREVIEW_ACTIVITY);
    }


    /**
     * 一些旧设备在UI小部件更新之间需要一个小延迟
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            rvPhotos.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private RelativeLayout mBottomBar;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // 延迟显示UI元素
            AlphaAnimation showAnimation = new AlphaAnimation(0.0f, 1.0f);
            showAnimation.setDuration(UI_ANIMATION_DELAY);
            mBottomBar.setVisibility(View.VISIBLE);
            mBottomBar.startAnimation(showAnimation);
        }
    };
    private boolean mVisible;

    private PressedImageView ivBack;
    private PressedTextView tvEdit;
    private TextView tvSelector;
    private ImageView ivSelector;
    private RecyclerView rvPhotos;
    private PreviewPhotosAdapter adapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager lm;
    private int index;
    private ArrayList<PhotoItem> photos;
    private int resultCode = RESULT_CANCELED;
    private int lastPosition = 0;//记录recyclerView最后一次角标位置，用于判断是否转换了item
    private boolean isSingle = Setting.count == 1;
    private boolean unable = Result.count() == Setting.count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_easy_photos);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        int albumItemIndex = intent.getIntExtra(Key.PREVIEW_ALBUM_ITEM_INDEX, 0);
        photos = AlbumModel.getInstance(this, false, null).getCurrAlbumItemPhotos(albumItemIndex);
        index = intent.getIntExtra(Key.PREVIEW_PHOTO_INDEX, 0);
        if (photos.get(0).isCamera) {
            photos.remove(0);
            index--;
        }
        lastPosition = index;
        mVisible = true;
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideAnimation.setDuration(UI_ANIMATION_DELAY);
        mBottomBar.startAnimation(hideAnimation);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        rvPhotos.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onPhotoClick() {
        toggle();
    }

    @Override
    public void onBackPressed() {
        doBack();
    }

    private void doBack() {
        setResult(resultCode);
        finish();
    }

    private void initView() {
        tvEdit = (PressedTextView) findViewById(R.id.tv_edit);
        mBottomBar = (RelativeLayout) findViewById(R.id.m_bottom_bar);
        ivBack = (PressedImageView) findViewById(R.id.iv_back);
        tvSelector = (TextView) findViewById(R.id.tv_selector);
        ivSelector = (ImageView) findViewById(R.id.iv_selector);
        mBottomBar.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvSelector.setOnClickListener(this);
        ivSelector.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        adapter = new PreviewPhotosAdapter(this, photos, this);
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(lm);
        rvPhotos.setAdapter(adapter);
        rvPhotos.scrollToPosition(index);
        toggleSelector();
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvPhotos);
        rvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                int leftViewPosition = snapHelper.findTargetSnapPosition(lm, 1, rvPhotos.getHeight() / 2);
                int rightViewPosition = snapHelper.findTargetSnapPosition(lm, rvPhotos.getWidth() - 1, rvPhotos.getHeight() / 2);
                if (leftViewPosition == rightViewPosition) {
                    if (lastPosition == leftViewPosition - 1) {
                        return;
                    }
                    lastPosition = leftViewPosition - 1;
                    View view = snapHelper.findSnapView(lm);
                    toggleSelector();
                    if (null == view) {
                        return;
                    }
                    PreviewPhotosAdapter.PreviewPhotosViewHolder viewHolder = (PreviewPhotosAdapter.PreviewPhotosViewHolder) rvPhotos.getChildViewHolder(view);
                    if (viewHolder == null || viewHolder.ivPhoto == null) {
                        return;
                    }
                    if (viewHolder.ivPhoto.getScale() != 1f)
                        viewHolder.ivPhoto.setScale(1f, true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            doBack();
        } else if (R.id.tv_selector == id) {
            updateSelector();
        } else if (R.id.iv_selector == id) {
            updateSelector();
        } else if (R.id.m_bottom_bar == id) {

        } else if (R.id.tv_edit == id) {

        }

    }

    private void toggleSelector() {
        if (photos.get(lastPosition).selected) {
            ivSelector.setImageResource(R.drawable.ic_selector_true);
        } else {
            ivSelector.setImageResource(R.drawable.ic_selector);
        }
    }

    private void updateSelector() {
        resultCode = RESULT_OK;
        PhotoItem item = photos.get(lastPosition);
        if (isSingle) {
            singleSelector(item);
            return;
        }
        if (unable) {
            if (item.selected) {
                Result.removePhoto(item);
                if (unable) {
                    unable = false;
                }
                return;
            }
            Toast.makeText(this, getString(R.string.selector_reach_max_image_hint_easy_photos, Setting.count), Toast.LENGTH_SHORT).show();
            return;
        }
        item.selected = !item.selected;
        if (item.selected) {
            Result.addPhoto(item);
            if (Result.count() == Setting.count) {
                unable = true;
            }
        } else {
            Result.removePhoto(item);
            if (unable) {
                unable = false;
            }
        }
        toggleSelector();
    }

    private void singleSelector(PhotoItem photoItem) {
        if (!Result.isEmpty()) {
            if (Result.getPhotoPath(0).equals(photoItem.path)) {
                Result.removePhoto(photoItem);
                toggleSelector();
            } else {
                Result.removePhoto(0);
                Result.addPhoto(photoItem);
                toggleSelector();
            }
        } else {
            Result.addPhoto(photoItem);
            toggleSelector();
        }
    }
}
