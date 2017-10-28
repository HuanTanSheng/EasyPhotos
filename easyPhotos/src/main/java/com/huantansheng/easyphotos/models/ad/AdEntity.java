package com.huantansheng.easyphotos.models.ad;

import android.view.View;

/**
 * 广告实体
 * Created by huan on 2017/10/24.
 */

public class AdEntity {
    public View adView;
    public int lineIndex;

    public AdEntity(View adView, int lineIndex) {
        this.adView = adView;
        this.lineIndex = lineIndex;
    }
}
