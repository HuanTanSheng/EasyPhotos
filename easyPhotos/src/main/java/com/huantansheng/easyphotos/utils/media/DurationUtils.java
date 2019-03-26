package com.huantansheng.easyphotos.utils.media;

import android.media.MediaMetadataRetriever;
import android.text.format.DateUtils;

/**
 * DurationUtils
 * Create By lishilin On 2019/3/25
 */
public class DurationUtils {

    /**
     * 获取时长
     *
     * @param path path
     * @return duration
     */
    public static long getDuration(String path) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            return Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
//            e.printStackTrace();
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        return 0;
    }

    /**
     * 格式化时长（不足一秒则显示为一秒）
     *
     * @param duration duration
     * @return "MM:SS" or "H:MM:SS"
     */
    public static String format(long duration) {
        long seconds = duration / 1000;
        if (seconds == 0) {
            seconds++;
        }
        return DateUtils.formatElapsedTime(seconds);
    }

}
