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
import android.widget.RelativeLayout;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.ui.adapter.PreviewPhotosAdapter;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PreviewEasyPhotosActivity extends AppCompatActivity implements PreviewPhotosAdapter.OnClickListener {
    public static void start(Activity act, ArrayList<String> prePhotos) {
        Intent intent = new Intent(act, PreviewEasyPhotosActivity.class);
        intent.putStringArrayListExtra(Key.PREVIEW_PHOTOS, prePhotos);
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
            mBottomBar.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    private RecyclerView rvPhotos;
    private PreviewPhotosAdapter adapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_easy_photos);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = true;
        mBottomBar = (RelativeLayout) findViewById(R.id.m_bottom_bar);

        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        adapter = new PreviewPhotosAdapter(this, getIntent().getStringArrayListExtra(Key.PREVIEW_PHOTOS), this);
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(lm);
        rvPhotos.setAdapter(adapter);
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
                    View view = snapHelper.findSnapView(lm);
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // 启动800毫秒后全屏显示
        delayedHide(800);
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
        mBottomBar.setVisibility(View.GONE);
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

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onPhotoClick() {
        toggle();
    }
}
