package com.north.light.libpicselect;

import com.north.light.libpicselect.callback.LibPicSelMediaInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author:li
 * date:2022/4/2
 * desc:图片库监听回调
 */
public interface PicCallbackListener extends Serializable {
    //拍照
    void cameraResult(String path);

    //选择图片
    void selectResult(ArrayList<LibPicSelMediaInfo> result);

    //剪裁图片
    void cropResult(String path);

    //录制视频
    void recordVideoPath(String path);

    void error(String message);
}
