package com.north.light.libpicselect;

import android.app.Activity;

/**
 * author:li
 * date:2022/4/4
 * desc:自定义行为监听
 */
public interface PicActionListener {


    /**
     * 自定义视频播放页面
     */
    public void cusVideoPlay(Activity activity, String path);

    /**
     * 自定义相机拍摄页面
     */
    public void cusCameraTake(Activity activity);
}
