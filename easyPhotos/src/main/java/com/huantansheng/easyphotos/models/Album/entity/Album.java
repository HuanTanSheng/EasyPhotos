package com.huantansheng.easyphotos.models.Album.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 相册实体类
 * Created by huan on 2017/10/20.
 */

public class Album {
    public ArrayList<AlbumItem> albumList = new ArrayList<>();
    public LinkedHashMap<String, AlbumItem> linkedHashMap = new LinkedHashMap<>();
}
