package com.huantansheng.easyphotos.models.ad;

import android.view.View;

/**
 * 广告监听
 * Created by huan on 2017/10/24.
 */

public interface AdListener {
    void onPhotosAdLoaded(View adView);
    void onAlbumItemsAdLoaded(View adView);
}
