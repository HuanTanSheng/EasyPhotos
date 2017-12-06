package com.huantansheng.easyphotos.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.models.puzzle.DegreeSeekBar;
import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;
import com.huantansheng.easyphotos.models.puzzle.PuzzlePiece;
import com.huantansheng.easyphotos.models.puzzle.PuzzleUtils;
import com.huantansheng.easyphotos.models.puzzle.PuzzleView;
import com.huantansheng.easyphotos.ui.adapter.PuzzleAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 拼图界面
 * Created by huan on 2017/12/4.
 */

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener, PuzzleAdapter.OnItemClickListener {

    private static WeakReference<Class<? extends Activity>> toClass;


    public PuzzleActivity() {
    }

    public static void startWithPhotos(Activity act, ArrayList<Photo> photos, int requestCode, boolean replaceCustom) {
        if (null != toClass) {
            toClass.clear();
            toClass = null;
        }
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, true);
        intent.putParcelableArrayListExtra(Key.PUZZLE_FILES, photos);
        if (replaceCustom) {
            toClass = new WeakReference<Class<? extends Activity>>(act.getClass());
        }
        act.startActivityForResult(intent, requestCode);
    }

    public static void startWithPaths(Activity act, ArrayList<String> paths, int requestCode, boolean replaceCustom) {
        if (null != toClass) {
            toClass.clear();
            toClass = null;
        }
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, false);
        intent.putStringArrayListExtra(Key.PUZZLE_FILES, paths);
        if (replaceCustom) {
            toClass = new WeakReference<Class<? extends Activity>>(act.getClass());
        }
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

    private LinearLayout llMenu;
    private DegreeSeekBar degreeSeekBar;
    private ArrayList<ImageView> ivMenus = new ArrayList<>();

    private ArrayList<Integer> degrees = new ArrayList<>();
    private int degreeIndex = -1;
    private boolean hideSeekBar = false;
    private int controlFlag;
    private static final int FLAG_CONTROL_PADDING = 0;
    private static final int FLAG_CONTROL_CORNER = 1;
    private static final int FLAG_CONTROL_ROTATE = 2;

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
        initIvMenu();
        initPuzzleView();
        initRecyclerView();
        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_done).setOnClickListener(this);
    }

    private void initIvMenu() {
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        ImageView ivReplace = (ImageView) findViewById(R.id.iv_replace);
        ImageView ivRotate = (ImageView) findViewById(R.id.iv_rotate);
        ImageView ivMirror = (ImageView) findViewById(R.id.iv_mirror);
        ImageView ivFlip = (ImageView) findViewById(R.id.iv_flip);
        ImageView ivCorner = (ImageView) findViewById(R.id.iv_corner);
        ImageView ivPadding = (ImageView) findViewById(R.id.iv_padding);
        ivReplace.setOnClickListener(this);
        ivRotate.setOnClickListener(this);
        ivMirror.setOnClickListener(this);
        ivFlip.setOnClickListener(this);
        ivCorner.setOnClickListener(this);
        ivPadding.setOnClickListener(this);
//        ivMenus.add(ivReplace);
        ivMenus.add(ivRotate);
//        ivMenus.add(ivMirror);
//        ivMenus.add(ivFlip);
        ivMenus.add(ivCorner);
        ivMenus.add(ivPadding);

        degreeSeekBar = (DegreeSeekBar) findViewById(R.id.degree_seek_bar);
        degreeSeekBar.setScrollingListener(new DegreeSeekBar.ScrollingListener() {
            @Override
            public void onScrollStart() {

            }

            @Override
            public void onScroll(int currentDegrees) {
                switch (controlFlag) {
                    case FLAG_CONTROL_PADDING:
                        puzzleView.setPiecePadding(currentDegrees);
                        break;
                    case FLAG_CONTROL_CORNER:
                        if (currentDegrees < 0) {
                            currentDegrees = 0;
                        }
                        puzzleView.setPieceRadian(currentDegrees);
                        break;
                    case FLAG_CONTROL_ROTATE:
                        puzzleView.rotate(currentDegrees - degrees.get(degreeIndex));
                        degrees.remove(degreeIndex);
                        degrees.add(degreeIndex, currentDegrees);
                        break;
                }
            }

            @Override
            public void onScrollEnd() {

            }
        });
    }

    private void initRecyclerView() {
        rvPuzzleTemplet = (RecyclerView) findViewById(R.id.rv_puzzle_template);
        puzzleAdapter = new PuzzleAdapter();
        puzzleAdapter.setOnItemClickListener(this);
        rvPuzzleTemplet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPuzzleTemplet.setAdapter(puzzleAdapter);
        puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(bitmaps.size()), bitmaps);
    }

    private void initPuzzleView() {
        int themeType = fileCount > 3 ? 1 : 0;
        puzzleLayout = PuzzleUtils.getPuzzleLayout(themeType, fileCount, 0);
        puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        puzzleView.setPuzzleLayout(puzzleLayout);
        puzzleView.setOnPieceSelectedListener(new PuzzleView.OnPieceSelectedListener() {
            @Override
            public void onPieceSelected(PuzzlePiece piece, int position) {
                hideSeekBar = degreeIndex == position;
                toggleIvMenu(R.id.iv_replace);
                degreeSeekBar.setVisibility(View.INVISIBLE);
                controlFlag = -1;
                if (hideSeekBar) {
                    llMenu.setVisibility(View.INVISIBLE);
                    degreeIndex = -1;
                    return;
                }
                llMenu.setVisibility(View.VISIBLE);
                degreeIndex = position;
            }
        });
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
            fileCount = photos.size() > 9 ? 9 : photos.size();
            for (int i = 0; i < fileCount; i++) {
                Bitmap b = BitmapFactory.decodeFile(photos.get(i).path);
                bitmaps.add(b);
                degrees.add(0);
            }

        } else {
            paths = intent.getStringArrayListExtra(Key.PUZZLE_FILES);
            fileCount = paths.size() > 0 ? 9 : paths.size();
            for (int i = 0; i < fileCount; i++) {
                Bitmap b = BitmapFactory.decodeFile(paths.get(i));
                bitmaps.add(b);
                degrees.add(0);
            }

        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (R.id.tv_back == id) {
            finish();
        } else if (R.id.tv_done == id) {
            savePhoto();
            Intent intent = new Intent();
//            intent.putExtra(EasyPhotos.RESULT_PUZZLE, );
            setResult(RESULT_OK, intent);
            finish();
        } else if (R.id.iv_replace == id) {
            controlFlag = -1;
            degreeSeekBar.setVisibility(View.INVISIBLE);
            toggleIvMenu(R.id.iv_replace);
        } else if (R.id.iv_rotate == id) {
            if (controlFlag == FLAG_CONTROL_ROTATE) {
                if (degrees.get(degreeIndex) % 90 != 0) {
                    puzzleView.rotate(-degrees.get(degreeIndex));
                    degrees.remove(degreeIndex);
                    degrees.add(degreeIndex, 0);
                    degreeSeekBar.setCurrentDegrees(0);
                    return;
                }
                puzzleView.rotate(90);
                int degree = degrees.get(degreeIndex) + 90;
                if (degree == 360 || degree == -360) {
                    degree = 0;
                }
                degrees.remove(degreeIndex);
                degrees.add(degreeIndex, degree);
                degreeSeekBar.setCurrentDegrees(degrees.get(degreeIndex));
                return;
            }
            handleSeekBar(FLAG_CONTROL_ROTATE, -360, 360, degrees.get(degreeIndex));
            toggleIvMenu(R.id.iv_rotate);
        } else if (R.id.iv_mirror == id) {
            degreeSeekBar.setVisibility(View.INVISIBLE);
            controlFlag = -1;
            toggleIvMenu(R.id.iv_mirror);
            puzzleView.flipHorizontally();
        } else if (R.id.iv_flip == id) {
            controlFlag = -1;
            degreeSeekBar.setVisibility(View.INVISIBLE);
            toggleIvMenu(R.id.iv_flip);
            puzzleView.flipVertically();
        } else if (R.id.iv_corner == id) {
            handleSeekBar(FLAG_CONTROL_CORNER, 0, 1000, puzzleView.getPieceRadian());
            toggleIvMenu(R.id.iv_corner);
        } else if (R.id.iv_padding == id) {
            handleSeekBar(FLAG_CONTROL_PADDING, 0, 100, puzzleView.getPiecePadding());
            toggleIvMenu(R.id.iv_padding);
        }
    }

    private void handleSeekBar(int controlFlag, int rangeStart, int rangeEnd, float degrees) {
        this.controlFlag = controlFlag;
        degreeSeekBar.setVisibility(View.VISIBLE);
        degreeSeekBar.setDegreeRange(rangeStart, rangeEnd);
        degreeSeekBar.setCurrentDegrees((int) degrees);
    }

    private void savePhoto() {

    }

    private void toggleIvMenu(@IdRes int resId) {
        for (ImageView ivMenu : ivMenus) {
            if (ivMenu.getId() == resId) {
                ivMenu.setColorFilter(ContextCompat.getColor(this, R.color.puzzle_selected_frame_easy_photos));
            } else {
                ivMenu.clearColorFilter();
            }
        }

    }

    @Override
    public void onItemClick(int themeType, int themeId) {
        puzzleView.setPuzzleLayout(PuzzleUtils.getPuzzleLayout(themeType, fileCount, themeId));
        puzzleView.addPieces(bitmaps);
        resetDegress();

    }

    private void resetDegress() {
        degreeIndex = -1;
        llMenu.setVisibility(View.INVISIBLE);
        degreeSeekBar.setVisibility(View.INVISIBLE);

        for (int i = 0; i < degrees.size(); i++) {
            degrees.remove(i);
            degrees.add(i, 0);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != toClass) {
            toClass.clear();
            toClass = null;
        }
        super.onDestroy();
    }
}
