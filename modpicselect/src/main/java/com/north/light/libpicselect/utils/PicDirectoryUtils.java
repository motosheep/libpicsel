package com.north.light.libpicselect.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * create by lzt
 * data 2019/12/8
 * 目录提取类
 */
public class PicDirectoryUtils {
    public static String getDirectory(String path) {
        if (TextUtils.isEmpty(path)) return "";
        try {
            // /storage/emulated/0/tieba/06BD464884613396E7A8430D79458F6E.jpg
            String cache = path.trim();
            cache = cache.replace("/storage/emulated/0/", "");
            cache = cache.substring(0, cache.indexOf("/"));
            return cache;
        } catch (Exception e) {
            return "";
        }
    }
}
