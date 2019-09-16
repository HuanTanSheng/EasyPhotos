package com.huantansheng.easyphotos.models.album.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 专辑项目实体类
 * Created by huan on 2017/10/20.
 */

public class AlbumItem {
    public String name;
    public String folderPath;
    public String coverImagePath;
    public final List<Photo> photos;

    AlbumItem(String name, String folderPath, String coverImagePath) {
        this.name = name;
        this.folderPath = folderPath;
        this.coverImagePath = coverImagePath;
        this.photos = Collections.synchronizedList(new ArrayList<Photo>());
    }

    public void addImageItem(Photo imageItem) {
        synchronized (photos) {
            boolean absent = !photos.contains(imageItem);
            if (absent) {
                this.photos.add(imageItem);
            }
        }
    }

    public void addImageItem(int index, Photo imageItem) {
        this.photos.add(index, imageItem);
    }
}
