package com.huantansheng.easyphotos.models.Album.entity;

import java.util.ArrayList;

/**
 * 相册item实体类
 * Created by huan on 2017/10/20.
 */

public class AlbumItem {
    public String name;
    public String folderPath;
    public String coverImagePath;
    public ArrayList<ImageItem> images = new ArrayList<>();
}
