package com.north.light.libpicselect.databus;

import android.app.Activity;

import java.util.ArrayList;

/**
 * author:li
 * date:2022/4/2
 * desc:数据监听管理类
 */
public interface DataBusListener {

    //操作结果------------------------------------------------------------------
    //拍照
    void cameraResult(String path);

    //选择图片
    void selectResult(ArrayList<String> result);

    //剪裁图片
    void cropResult(String path);

    //录制视频
    void recordVideoPath(String path);

    //操作行为---------------------------------------------------------

    //播放视频
    void playCusVideo(Activity activity, String path);

    //使用自定义相机
    void takeCameraCus(Activity activity,int org);

    //使用手机系统播放视频
    void playVideoSystem(Activity activity, String path);
}
