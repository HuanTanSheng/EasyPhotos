package com.huantansheng.easyphotos.utils.uri;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.huantansheng.easyphotos.setting.Setting;

import java.io.File;

public class UriUtils {
    public static Uri getUri(Context cxt, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (null==Setting.fileProviderAuthority){
                throw new NullPointerException("Setting.fileProviderAuthority must not be null.");
            }
            return FileProvider.getUriForFile(cxt, Setting.fileProviderAuthority, file);
        } else {
            return Uri.fromFile(file);
        }
    }
}
