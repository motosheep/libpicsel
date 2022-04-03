package com.north.light.libpicselect;

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
    void selectResult(ArrayList<String> result);

    //剪裁图片
    void cropResult(String path);

    //录制视频
    void recordVideoPath(String path);
}
