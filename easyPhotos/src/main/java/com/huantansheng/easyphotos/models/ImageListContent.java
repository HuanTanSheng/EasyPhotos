package com.huantansheng.easyphotos.models;

import com.huantansheng.easyphotos.constant.Path;

import java.util.ArrayList;

public class ImageListContent {
    public static final ArrayList<ImageItem> IMAGES = new ArrayList<ImageItem>();
    public static final ArrayList<String> SELECTED_IMAGES = new ArrayList<>();
    public static final ImageItem cameraItem = new ImageItem("", Path.CAMERA_ITEM_PATH, 0, 0, 0, "");
    // ImageRecyclerViewAdapter.OnClick will set it to true
    // Activity.OnImageInteraction will show the alert, and set it to false
    public static boolean bReachMaxNumber = false;

    public static void clear() {
        IMAGES.clear();
    }

    public static void addItem(ImageItem item) {
        IMAGES.add(item);
    }

    public static boolean isImageSelected(String filename) {
        return SELECTED_IMAGES.contains(filename);
    }

    public static void toggleImageSelected(String filename) {
        if (SELECTED_IMAGES.contains(filename)) {
            SELECTED_IMAGES.remove(filename);
        } else {
            SELECTED_IMAGES.add(filename);
        }
    }
}
