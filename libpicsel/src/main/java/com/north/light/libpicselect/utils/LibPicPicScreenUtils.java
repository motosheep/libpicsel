package com.north.light.libpicselect.utils;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * create by lzt
 * data 2019/12/8
 */
public class LibPicPicScreenUtils {

    public static int getScreenWidth(Context context) {
        try {
            WindowManager wm = ((Activity) new WeakReference<>(context).get()).getWindowManager();
            return wm.getDefaultDisplay().getWidth();
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getScreenHeight(Context context) {
        try {
            WindowManager wm = ((Activity) new WeakReference<>(context).get()).getWindowManager();
            return wm.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            return 0;
        }
    }
}
