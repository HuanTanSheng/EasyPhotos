package com.huantansheng.easyphotos.models.album.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 专辑模型实体类
 * Created by huan on 2017/10/20.
 */

public class Album {
    public ArrayList<AlbumItem> albumItems;
    private LinkedHashMap<String, AlbumItem> hasAlbumItems;//用于记录专辑项目

    public Album() {
        albumItems = new ArrayList<>();
        hasAlbumItems = new LinkedHashMap<>();
    }

    private void addAlbumItem(AlbumItem albumItem) {
        this.hasAlbumItems.put(albumItem.name, albumItem);
        this.albumItems.add(albumItem);
    }

    public void addAlbumItem(String name, String folderPath, String coverImagePath) {
        if (null == hasAlbumItems.get(name)) {
            addAlbumItem(new AlbumItem(name, folderPath, coverImagePath));
        }
    }

    public AlbumItem getAlbumItem(String name) {
        return hasAlbumItems.get(name);
    }

    public AlbumItem getAlbumItem(int currIndex) {
        return albumItems.get(currIndex);
    }

    public boolean isEmpty() {
        return albumItems.isEmpty();
    }
}
