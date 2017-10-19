package com.huantansheng.easyphotos.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderListContent {

    public static final List<FolderItem> FOLDERS = new ArrayList<FolderItem>();
    public static final Map<String, FolderItem> FOLDERS_MAP = new HashMap<>();

    // used to locate item in popupwindow
    public static FolderItem selectedFolder;
    public static int selectedFolderIndex;

    public static FolderItem getSelectedFolder() {
        return selectedFolder;
    }

    public static void setSelectedFolder(FolderItem currentFolder, int index) {
        FolderListContent.selectedFolder = currentFolder;
        FolderListContent.selectedFolderIndex = index;
    }

    public static void clear() {
        FOLDERS.clear();
        FOLDERS_MAP.clear();
    }

    public static void addItem(FolderItem item) {
        FOLDERS.add(item);
        FOLDERS_MAP.put(item.path, item);
    }

    public static FolderItem getItem(String folderPath) {
        if (FOLDERS_MAP.containsKey(folderPath)) {
            return FOLDERS_MAP.get(folderPath);
        } else {
            return null;
        }
    }
}
