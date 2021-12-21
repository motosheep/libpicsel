package com.north.light.libpicselect.utils;

import android.text.TextUtils;

/**
 * @Author: lzt
 * @Date: 2021/11/10 17:56
 * @Description:字符处理类
 */
public class LibPicSelStringUtils {

    /**
     * 字符转换
     */
    public static String toNoEmptyStr(String str) {
        if (!TextUtils.isEmpty(str)) {
            return str;
        }
        return "";
    }
}
