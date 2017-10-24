package com.huantansheng.easyphotos.ad;

/**
 * 广告实体
 * Created by huan on 2017/10/24.
 */

public class AdEntity {
    public String imageUrl;
    public String title;
    public String content;

    public AdEntity(String imageUrl, String title, String content) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
    }
}
