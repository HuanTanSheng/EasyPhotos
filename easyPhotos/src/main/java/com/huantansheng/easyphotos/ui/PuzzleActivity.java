package com.huantansheng.easyphotos.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Code;
import com.huantansheng.easyphotos.constant.Key;
import com.huantansheng.easyphotos.engine.ImageEngine;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.models.puzzle.Area;
import com.huantansheng.easyphotos.models.puzzle.DegreeSeekBar;
import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;
import com.huantansheng.easyphotos.models.puzzle.PuzzlePiece;
import com.huantansheng.easyphotos.models.puzzle.PuzzleUtils;
import com.huantansheng.easyphotos.models.puzzle.PuzzleView;
import com.huantansheng.easyphotos.models.sticker.StickerModel;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.adapter.PuzzleAdapter;
import com.huantansheng.easyphotos.ui.adapter.TextStickerAdapter;
import com.huantansheng.easyphotos.utils.bitmap.SaveBitmapCallBack;
import com.huantansheng.easyphotos.utils.media.DurationUtils;
import com.huantansheng.easyphotos.utils.permission.PermissionUtil;
import com.huantansheng.easyphotos.utils.settings.SettingsUtils;
import com.huantansheng.easyphotos.utils.uri.UriUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 拼图界面
 * Created by huan on 2017/12/4.
 */

public class PuzzleActivity extends AppCompatActivity implements View.OnClickListener,
        PuzzleAdapter.OnItemClickListener, TextStickerAdapter.OnItemClickListener {

    private static WeakReference<Class<? extends Activity>> toClass;


    public static void startWithPhotos(Activity act, ArrayList<Photo> photos,
                                       String puzzleSaveDirPath, String puzzleSaveNamePrefix,
                                       int requestCode, boolean replaceCustom,
                                       @NonNull ImageEngine imageEngine) {
        if (null != toClass) {
            toClass.clear();
            toClass = null;
        }
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        Intent intent = new Intent(act, PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, true);
        intent.putParcelableArrayListExtra(Key.PUZZLE_FILES, photos);
        intent.putExtra(Key.PUZZLE_SAVE_DIR, puzzleSaveDirPath);
        intent.putExtra(Key.PUZZLE_SAVE_NAME_PREFIX, puzzleSaveNamePrefix);
        if (replaceCustom) {
            toClass = new WeakReference<Class<? extends Activity>>(act.getClass());
        }
        act.startActivityForResult(intent, requestCode);
    }

    public static void startWithPhotos(Fragment fragment, ArrayList<Photo> photos,
                                       String puzzleSaveDirPath, String puzzleSaveNamePrefix,
                                       int requestCode, boolean replaceCustom,
                                       @NonNull ImageEngine imageEngine) {
        if (null != toClass) {
            toClass.clear();
            toClass = null;
        }
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        Intent intent = new Intent(fragment.getActivity(), PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, true);
        intent.putParcelableArrayListExtra(Key.PUZZLE_FILES, photos);
        intent.putExtra(Key.PUZZLE_SAVE_DIR, puzzleSaveDirPath);
        intent.putExtra(Key.PUZZLE_SAVE_NAME_PREFIX, puzzleSaveNamePrefix);
        if (replaceCustom) {
            toClass =
                    new WeakReference<Class<? extends Activity>>(fragment.getActivity().getClass());
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startWithPhotos(androidx.fragment.app.Fragment fragmentV,
                                       ArrayList<Photo> photos, String puzzleSaveDirPath,
                                       String puzzleSaveNamePrefix, int requestCode,
                                       boolean replaceCustom, @NonNull ImageEngine imageEngine) {
        if (null != toClass) {
            toClass.clear();
            toClass = null;
        }
        if (Setting.imageEngine != imageEngine) {
            Setting.imageEngine = imageEngine;
        }
        Intent intent = new Intent(fragmentV.getActivity(), PuzzleActivity.class);
        intent.putExtra(Key.PUZZLE_FILE_IS_PHOTO, true);
        intent.putParcelableArrayListExtra(Key.PUZZLE_FILES, photos);
        intent.putExtra(Key.PUZZLE_SAVE_DIR, puzzleSaveDirPath);
        intent.putExtra(Key.PUZZLE_SAVE_NAME_PREFIX, puzzleSaveNamePrefix);
        if (replaceCustom) {
            if (fragmentV.getActivity() != null) {
                toClass =
                        new WeakReference<Class<? extends Activity>>(fragmentV.getActivity().getClass());
            }
        }
        fragmentV.startActivityForResult(intent, requestCode);
    }

    ArrayList<Photo> photos = null;

    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    String saveDirPath, saveNamePrefix;

    private PuzzleView puzzleView;
    private RecyclerView rvPuzzleTemplet;
    private PuzzleAdapter puzzleAdapter;
    private ProgressBar progressBar;
    private int fileCount = 0;

    private LinearLayout llMenu;
    private DegreeSeekBar degreeSeekBar;
    private ArrayList<ImageView> ivMenus = new ArrayList<>();

    private ArrayList<Integer> degrees = new ArrayList<>();
    private int degreeIndex = -1;
    private int controlFlag;
    private static final int FLAG_CONTROL_PADDING = 0;
    private static final int FLAG_CONTROL_CORNER = 1;
    private static final int FLAG_CONTROL_ROTATE = 2;

    private int deviceWidth = 0;
    private int deviceHeight = 0;

    private TextView tvTemplate, tvTextSticker;
    private RelativeLayout mRootView, mBottomLayout;
    private TextStickerAdapter textStickerAdapter;

    private StickerModel stickerModel;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        setContentView(R.layout.activity_puzzle_easy_photos);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (null == Setting.imageEngine) {
            finish();
            return;
        }
        initData();
        initView();
    }

    private void initView() {
        initIvMenu();
        initPuzzleView();
        initRecyclerView();
        progressBar = findViewById(R.id.progress);
        setClick(R.id.tv_back, R.id.tv_done);
    }

    private void initIvMenu() {
        fab = (FloatingActionButton) findViewById(R.id.fab);

        tvTemplate = (TextView) findViewById(R.id.tv_template);
        tvTextSticker = (TextView) findViewById(R.id.tv_text_sticker);
        mRootView = (RelativeLayout) findViewById(R.id.m_root_view);
        mBottomLayout = (RelativeLayout) findViewById(R.id.m_bottom_layout);

        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        ImageView ivRotate = (ImageView) findViewById(R.id.iv_rotate);
        ImageView ivCorner = (ImageView) findViewById(R.id.iv_corner);
        ImageView ivPadding = (ImageView) findViewById(R.id.iv_padding);

        setClick(R.id.iv_replace, R.id.iv_mirror, R.id.iv_flip);
        setClick(ivRotate, ivCorner, ivPadding, fab, tvTextSticker, tvTemplate);

        ivMenus.add(ivRotate);
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
        rvPuzzleTemplet.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        rvPuzzleTemplet.setAdapter(puzzleAdapter);
        puzzleAdapter.refreshData(PuzzleUtils.getPuzzleLayouts(fileCount));

        textStickerAdapter = new TextStickerAdapter(this, this);
    }

    private void initPuzzleView() {
        int themeType = fileCount > 3 ? 1 : 0;
        puzzleView = (PuzzleView) findViewById(R.id.puzzle_view);
        puzzleView.setPuzzleLayout(PuzzleUtils.getPuzzleLayout(themeType, fileCount, 0));
        puzzleView.setOnPieceSelectedListener(new PuzzleView.OnPieceSelectedListener() {
            @Override
            public void onPieceSelected(PuzzlePiece piece, int position) {

                if (null == piece) {
                    toggleIvMenu(R.id.iv_replace);
                    llMenu.setVisibility(View.GONE);
                    degreeSeekBar.setVisibility(View.GONE);
                    degreeIndex = -1;
                    controlFlag = -1;
                    return;
                }

                if (degreeIndex != position) {
                    controlFlag = -1;
                    toggleIvMenu(R.id.iv_replace);
                    degreeSeekBar.setVisibility(View.GONE);
                }
                llMenu.setVisibility(View.VISIBLE);
                degreeIndex = position;
            }
        });
    }

    private void loadPhoto() {
        puzzleView.addPieces(bitmaps);
    }

    private void initData() {
        stickerModel = new StickerModel();
        deviceWidth = getResources().getDisplayMetrics().widthPixels;
        deviceHeight = getResources().getDisplayMetrics().heightPixels;
        Intent intent = getIntent();
        saveDirPath = intent.getStringExtra(Key.PUZZLE_SAVE_DIR);
        saveNamePrefix = intent.getStringExtra(Key.PUZZLE_SAVE_NAME_PREFIX);

        photos = intent.getParcelableArrayListExtra(Key.PUZZLE_FILES);
        fileCount = photos.size() > 9 ? 9 : photos.size();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < fileCount; i++) {
                    Bitmap bitmap = getScaleBitmap(photos.get(i).path, photos.get(i).uri);
                    bitmaps.add(bitmap);
                    degrees.add(0);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        puzzleView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadPhoto();
                            }
                        });
                    }
                });

            }
        }).start();


    }

    private Bitmap getScaleBitmap(String path, Uri uri) {
        Bitmap bitmap = null;

        try {
            bitmap = Setting.imageEngine.getCacheBitmap(this, uri, deviceWidth / 2,
                    deviceHeight / 2);
        } catch (Exception e) {
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path),
                    deviceWidth / 2, deviceHeight / 2, true);
        }

        if (bitmap == null) {
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path),
                    deviceWidth / 2, deviceHeight / 2, true);
        }
        return bitmap;

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (R.id.tv_back == id) {
            finish();
        } else if (R.id.tv_done == id) {
            if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
                savePhoto();
            }

        } else if (R.id.iv_replace == id) {
            controlFlag = -1;
            degreeSeekBar.setVisibility(View.GONE);
            toggleIvMenu(R.id.iv_replace);
            if (null == toClass) {
                EasyPhotos.createAlbum(this, true, false, Setting.imageEngine).setCount(1).start(91);
            } else {
                Intent intent = new Intent(this, toClass.get());
                startActivityForResult(intent, 91);
            }
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
            degreeSeekBar.setVisibility(View.GONE);
            controlFlag = -1;
            toggleIvMenu(R.id.iv_mirror);
            puzzleView.flipHorizontally();
        } else if (R.id.iv_flip == id) {
            controlFlag = -1;
            degreeSeekBar.setVisibility(View.GONE);
            toggleIvMenu(R.id.iv_flip);
            puzzleView.flipVertically();
        } else if (R.id.iv_corner == id) {
            handleSeekBar(FLAG_CONTROL_CORNER, 0, 1000, puzzleView.getPieceRadian());
            toggleIvMenu(R.id.iv_corner);
        } else if (R.id.iv_padding == id) {
            handleSeekBar(FLAG_CONTROL_PADDING, 0, 100, puzzleView.getPiecePadding());
            toggleIvMenu(R.id.iv_padding);
        } else if (R.id.tv_template == id) {
            tvTemplate.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_accent));
            tvTextSticker.setTextColor(ContextCompat.getColor(this,
                    R.color.easy_photos_fg_primary));

            rvPuzzleTemplet.setAdapter(puzzleAdapter);

        } else if (R.id.tv_text_sticker == id) {
            tvTextSticker.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_accent));
            tvTemplate.setTextColor(ContextCompat.getColor(this, R.color.easy_photos_fg_primary));

            rvPuzzleTemplet.setAdapter(textStickerAdapter);
        } else if (R.id.fab == id) {
            processBottomLayout();
        }
    }

    private void processBottomLayout() {
        if (View.VISIBLE == mBottomLayout.getVisibility()) {
            mBottomLayout.setVisibility(View.GONE);
            fab.setImageResource(R.drawable.ic_arrow_up_easy_photos);
        } else {
            mBottomLayout.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_arrow_down_easy_photos);
        }
    }

    private void handleSeekBar(int controlFlag, int rangeStart, int rangeEnd, float degrees) {
        this.controlFlag = controlFlag;
        degreeSeekBar.setVisibility(View.VISIBLE);
        degreeSeekBar.setDegreeRange(rangeStart, rangeEnd);
        degreeSeekBar.setCurrentDegrees((int) degrees);
    }

    private void savePhoto() {
        mBottomLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_done).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_frame).setVisibility(View.VISIBLE);

        puzzleView.clearHandling();
        puzzleView.invalidate();

        stickerModel.save(this, mRootView, puzzleView, puzzleView.getWidth(),
                puzzleView.getHeight(), saveDirPath, saveNamePrefix, true,
                new SaveBitmapCallBack() {
                    @Override
                    public void onSuccess(File file) {
                        Intent intent = new Intent();

                        Photo photo = new Photo(file.getName(), UriUtils.getUri(PuzzleActivity.this,
                                file), file.getAbsolutePath(), file.lastModified() / 1000,
                                puzzleView.getWidth(), puzzleView.getHeight(), 0, file.length(),
                                DurationUtils.getDuration(file.getAbsolutePath()), "image/png");
                        intent.putExtra(EasyPhotos.RESULT_PHOTOS, photo);
                        setResult(RESULT_OK, intent);
                        PuzzleActivity.this.finish();
                    }

                    @Override
                    public void onIOFailed(IOException exception) {
                        exception.printStackTrace();
                        setResult(RESULT_OK);
                        PuzzleActivity.this.finish();
                    }

                    @Override
                    public void onCreateDirFailed() {
                        setResult(RESULT_OK);
                        PuzzleActivity.this.finish();
                    }

                });
    }

    private void toggleIvMenu(@IdRes int resId) {
        int size = ivMenus.size();
        for (int i = 0; i < size; i++) {
            ImageView ivMenu = ivMenus.get(i);
            if (ivMenu.getId() == resId) {
                ivMenu.setColorFilter(ContextCompat.getColor(this, R.color.easy_photos_fg_accent));
            } else {
                ivMenu.clearColorFilter();
            }
        }

    }

    @Override
    public void onItemClick(int themeType, int themeId) {
        puzzleView.setPuzzleLayout(PuzzleUtils.getPuzzleLayout(themeType, fileCount, themeId));
        loadPhoto();
        resetDegress();

    }

    private void resetDegress() {
        llMenu.setVisibility(View.GONE);
        degreeSeekBar.setVisibility(View.GONE);
        degreeIndex = -1;
        int size = degrees.size();
        for (int i = 0; i < size; i++) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Code.REQUEST_SETTING_APP_DETAILS) {
            if (PermissionUtil.checkAndRequestPermissionsInActivity(this, getNeedPermissions())) {
                savePhoto();
            }
            return;
        }
        switch (resultCode) {
            case RESULT_OK:
                if (degreeIndex != -1) {
                    degrees.remove(degreeIndex);
                    degrees.add(degreeIndex, 0);
                }

                String tempPath = "";
                Uri tempUri = null;

                ArrayList<Photo> photos =
                        data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS);
                Photo photo = photos.get(0);
                tempPath = photo.path;
                tempUri = photo.uri;


                final String path = tempPath;
                final Uri uri = tempUri;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = getScaleBitmap(path, uri);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                puzzleView.replace(bitmap);
                            }
                        });
                    }
                }).start();

                break;
            case RESULT_CANCELED:
                break;
            default:
                break;
        }
    }

    protected String[] getNeedPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        return new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onPermissionResult(this, permissions, grantResults,
                new PermissionUtil.PermissionCallBack() {
                    @Override
                    public void onSuccess() {
                        savePhoto();
                    }

                    @Override
                    public void onShouldShow() {
                        Snackbar.make(rvPuzzleTemplet, R.string.permissions_again_easy_photos,
                                Snackbar.LENGTH_INDEFINITE).setAction("go",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (PermissionUtil.checkAndRequestPermissionsInActivity(PuzzleActivity.this, getNeedPermissions())) {
                                            savePhoto();
                                        }
                                    }
                                }).show();
                    }

                    @Override
                    public void onFailed() {
                        Snackbar.make(rvPuzzleTemplet, R.string.permissions_die_easy_photos,
                                Snackbar.LENGTH_INDEFINITE).setAction("go",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SettingsUtils.startMyApplicationDetailsForResult(PuzzleActivity.this,
                                                getPackageName());
                                    }
                                }).show();
                    }
                });
    }

    @Override
    public void onItemClick(String stickerValue) {
        if (stickerValue.equals("-1")) {

            PuzzleLayout puzzleLayout = puzzleView.getPuzzleLayout();
            int size = puzzleLayout.getAreaCount();
            for (int i = 0; i < size; i++) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String date = format.format(photos.get(i).time);
                stickerModel.addTextSticker(this, getSupportFragmentManager(), date, mRootView);
                stickerModel.currTextSticker.isChecked = true;
                Area area = puzzleLayout.getArea(i);
                stickerModel.currTextSticker.moveTo(area.centerX(), area.centerY());
            }

            return;
        }

        stickerModel.addTextSticker(this, getSupportFragmentManager(), stickerValue, mRootView);
    }

    @Override
    public void onBackPressed() {
        if (View.VISIBLE == mBottomLayout.getVisibility()) {
            processBottomLayout();
            return;
        }
        super.onBackPressed();
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
}
