package com.north.light.libpicselect.model;

import android.content.Context;
import android.widget.ImageView;

/**
 * create by lzt
 * data 2019/12/8
 */
public interface LibPicSelectApi {

    void init(Context context, InitCallBack callBack);

    /**
     * change by lzt  20200823增加是否显示视频的标识
     * */
    void load(boolean isShowGif, boolean isShowVideo);

    void release();

    interface InitCallBack {
        void NoPermission();

        void Success();
    }
}
