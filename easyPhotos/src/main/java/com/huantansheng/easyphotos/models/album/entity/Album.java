package com.huantansheng.easyphotos.models.album.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专辑模型实体类
 * Created by huan on 2017/10/20.
 */

public class Album {
    final public List<AlbumItem> albumItems;
    private ConcurrentHashMap<String, AlbumItem> hasAlbumItems;//用于记录专辑项目

    public Album() {
        albumItems = Collections.synchronizedList(new ArrayList<AlbumItem>());
        hasAlbumItems = new ConcurrentHashMap<>();
    }

    private void addAlbumItem(AlbumItem albumItem) {
        synchronized (albumItems) {
            boolean absent = !albumItems.contains(albumItem);
            if (absent) {
                this.albumItems.add(albumItem);
                this.hasAlbumItems.put(albumItem.name, albumItem);
            }
        }

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

    public void clear() {
        albumItems.clear();
        hasAlbumItems.clear();
    }
}
