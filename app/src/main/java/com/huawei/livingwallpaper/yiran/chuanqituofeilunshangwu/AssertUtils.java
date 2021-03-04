package com.huawei.livingwallpaper.yiran.chuanqituofeilunshangwu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AssertUtils {

    private static final int DEFAULT_VALUE = 1;

    private static final String SCREEN_DEFAULT = "video.mp4";

    private static final String NAME = "wallpaper_name";
    private static final String COLOR_INDEX = "color_index";
    private static final int MAX = 5;

    public static int getCureentPathIndex(Context context) {
        if(context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
        int index = sp.getInt(COLOR_INDEX, DEFAULT_VALUE);
        return index;
    }

    public static int getNextPathIndex(Context context) {
        if(context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
        int index = sp.getInt(COLOR_INDEX, DEFAULT_VALUE);
        index ++;
        if(index >= MAX) {
            index = 0;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(COLOR_INDEX, index);
        editor.commit();
        WLog.i("AssertUtils", "index:" + index);
        return index;
    }

    public static int getPrePathIndex(Context context) {
        if(context == null) {
            return 0;
        }
        SharedPreferences sp = context.getSharedPreferences(NAME, Activity.MODE_PRIVATE);
        int index = sp.getInt(COLOR_INDEX, DEFAULT_VALUE);
        index --;
        if(index < 0) {
            index = MAX - 1;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(COLOR_INDEX, index);
        editor.commit();
        WLog.i("AssertUtils", "index:" + index);
        return index;
    }
}
