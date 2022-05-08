package com.north.light.libpicselect.databus;

import android.app.Activity;

import com.north.light.libpicselect.callback.LibPicSelMediaInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * author:li
 * date:2022/4/2
 * desc:数据bus--图片库内部数据传递，跨越页面，同一进程
 */
public class LibPicDataBusManager implements Serializable {
    private CopyOnWriteArrayList<LibPicDataBusListener> mListener = new CopyOnWriteArrayList<>();


    private static class SingleHolder {
        static LibPicDataBusManager mInstance = new LibPicDataBusManager();
    }

    public static LibPicDataBusManager getInstance() {
        return SingleHolder.mInstance;
    }

    public void setDataBusListener(LibPicDataBusListener listener) {
        if (listener == null) {
            return;
        }
        mListener.add(listener);
    }

    public void removeDataBusListener(LibPicDataBusListener listener) {
        if (listener == null) {
            return;
        }
        mListener.remove(listener);
    }

    //外部调用通知-----------------------------------------------------
    //拍照
    public void cameraResult(String path) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.cameraResult(path);
            }
        }
    }

    //选择图片
    public void selectResult(ArrayList<LibPicSelMediaInfo> path) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.selectResult(path);
            }
        }
    }

    //剪裁图片
    public void cropResult(String path) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.cropResult(path);
            }
        }
    }

    //录制视频
    public void recordVideoPath(String path) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.recordVideoPath(path);
            }
        }
    }

    //播放视频--自定义
    public void playVideoCus(Activity activity, String path) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.playCusVideo(activity, path);
            }
        }
    }

    //拍照--自定义
    public void takeCameraCus(Activity activity,int org) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.takeCameraCus(activity,org);
            }
        }
    }

    //播放视频--系统
    public void playVideoSystem(Activity activity, String path) {
        for (LibPicDataBusListener cache : mListener) {
            if (cache != null) {
                cache.playVideoSystem(activity, path);
            }
        }
    }


}
