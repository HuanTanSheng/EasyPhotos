package com.huantansheng.easyphotos.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.models.album.entity.PhotoItem;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 选择的图片集
     */
    private ArrayList<PhotoItem> images = new ArrayList<>();
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
            ArrayList<PhotoItem> result = data.getParcelableArrayListExtra(EasyPhotos.RESULT);
            images.clear();
            images.addAll(result);
            adapter.notifyDataSetChanged();
            rvImage.smoothScrollToPosition(0);
        } else if (RESULT_CANCELED == resultCode) {
            Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        rvImage = (RecyclerView) findViewById(R.id.iv_image);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapter = new MainAdapter(this, images);
        rvImage.setLayoutManager(linearLayoutManager);
        rvImage.setAdapter(adapter);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvImage);

        l(R.id.bt_album_single
                , R.id.bt_album_multi
                , R.id.bt_all_single
                , R.id.bt_all_multi
                , R.id.bt_ad
                , R.id.bt_selected
                , R.id.bt_size
                , R.id.bt_original_unusable
                , R.id.bt_original_usable
        );
    }

    /**
     * 在此推荐我的另一个开源库：L
     * L是一个android开发便捷库，适用于任何android项目
     * 它的唯一目的就是减轻你的代码量，避免写一堆重复的大家都在写的代码
     * 除此之外，L包含了我们经常使用的各种工具类
     *
     * @param viewIds 资源id
     */
    private void l(@IdRes int... viewIds) {
        for (int viewId : viewIds) {
            findViewById(viewId).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_camera://单独使用相机
                EasyPhotos.createCamera(this)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .start(101);
                break;
            case R.id.bt_album_single://相册单选，无相机功能
                EasyPhotos.createAlbum(this, false)
                        .start(101);
                break;
            case R.id.bt_album_multi://相册多选，无相机功能
                EasyPhotos.createAlbum(this, false)
                        .setCount(9)
                        .start(101);
                break;
            case R.id.bt_all_single://相册单选，有相机功能
                EasyPhotos.createAlbum(this, true)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .start(101);
                break;
            case R.id.bt_all_multi://相册多选，有相机功能
                EasyPhotos.createAlbum(this, true)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(22)
                        .start(101);
                break;
            case R.id.bt_ad://相册中包含广告
                // 需要在启动前创建广告view
                // 广告view不能有父布局
                // 广告view可以包含子布局
                // 广告View的数据可以在任何时候绑定
                initAdViews();

                //启动方法，装载广告view
                EasyPhotos.createAlbum(this, true)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .setAdView(photosAdView, photosAdLoaded, albumItemsAdView, albumItemsAdLoaded)//参数说明：相册中的广告view，相册中的广告View数据是否绑定完毕，专辑列表广告View，专辑列表广告View数据是否绑定完毕
                        .start(101);

                break;
            case R.id.bt_selected://相册中包含默认勾选图片
                EasyPhotos.createAlbum(this, true)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .setSelectedPhotos(images)//参数说明：用户上一次勾选过的图片集合
                        .start(101);
                break;
            case R.id.bt_size://只显示限制尺寸以上的图片
                EasyPhotos.createAlbum(this, true)
                        .setFileProviderAuthoritiesText("com.huantansheng.easyphotos.sample.fileprovider")
                        .setCount(9)
                        .setMinSize(500, 500)//参数说明：最小宽度，最小高度
                        .start(101);
                break;
            case R.id.bt_original_usable://显示原图按钮，并且按钮可用
                EasyPhotos.createAlbum(this, false)
                        .setCount(9)
                        .setOriginalMenu(true, true, null)//参数说明：是否显示，是否可用，不可用将toast信息
                        .start(101);
                break;
            case R.id.bt_original_unusable://显示原图按钮，按钮不可用。举例使用场景：仅VIP可以上传原图
                boolean isVip = false;//假设获取用户信息发现该用户不是vip
                EasyPhotos.createAlbum(this, true)
                        .setCount(9)
                        .setOriginalMenu(true, isVip, "该功能为VIP会员特权功能")//参数说明：是否显示，是否可用，不可用将toast信息
                        .start(101);
        }
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
        //
        initAlbumItemsAd();
    }

    /**
     * 模拟启动EasyPhotos前广告已经装载完毕
     */
    private void initPhotosAd() {
        photosAdView = (RelativeLayout) getLayoutInflater().inflate(R.layout.ad_photos, null, false);//不可以有父布局，所以inflate第二个参数必须为null，并且布局文件必须独立
//        ((ImageView) photosAdView.findViewById(R.id.iv_image)).setImageResource(R.mipmap.ic_launcher);
        ((TextView) photosAdView.findViewById(R.id.tv_title)).setText("photosAd广告");
        ((TextView) photosAdView.findViewById(R.id.tv_content)).setText("github上star一下了解EasyPhotos的最新动态,这个布局和数据都是由你定制的");
        photosAdLoaded = true;
    }

    /**
     * 模拟不确定启动EasyPhotos前广告是否装载完毕
     * 模拟5秒后网络回调
     */
    private void initAlbumItemsAd() {
        albumItemsAdView = (RelativeLayout) getLayoutInflater().inflate(R.layout.ad_album_items, null, false);//不可以有父布局，所以inflate第二个参数必须为null，并且布局文件必须独立

        //模拟5秒后网络回调
        rvImage.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((ImageView) albumItemsAdView.findViewById(R.id.iv_image)).setImageResource(R.mipmap.ad);
                ((TextView) albumItemsAdView.findViewById(R.id.tv_title)).setText("albumItemsAd广告");
                photosAdLoaded = true;//正常情况可能不知道是先启动EasyPhotos还是数据先回来，所以这里加个标识，如果是后启动EasyPhotos那么EasyPhotos会直接加载广告
                EasyPhotos.notifyAlbumItemsAdLoaded();//通知EasyPhotos刷新广告，如果你能确定在启动EasyPhotos前已经装载好广告，那么请忽略EasyPhotos.notifyAlbumItemsAdLoaded()这个方法。
            }
        }, 5000);
    }
}
