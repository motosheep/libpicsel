package com.north.light.libpicselect.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.north.light.libpicselect.constant.PicConstant;

import java.io.Serializable;

/**
 * Created by lzt
 * time 2020/10/23
 * 描述：handler管理类
 */
public class HandlerManager implements Serializable {
    private static final String TAG = HandlerManager.class.getSimpleName();
    //io handler
    private Handler mIOCopyHandler;
    private HandlerThread mHandlerThread;
    private Handler mUIHandler;//ui handler

    private static final class SingleHolder {
        static final HandlerManager mInstance = new HandlerManager();
    }

    public static HandlerManager getInstance() {
        return SingleHolder.mInstance;
    }

    public void init() {

        if (mUIHandler == null) {
            mUIHandler = new Handler(Looper.getMainLooper());
        }
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("PIC_SEL_MAIN_COPY_HANDLER_THREAD");
            mHandlerThread.start();
        }
        if (mIOCopyHandler == null) {
            mIOCopyHandler = new Handler(mHandlerThread.getLooper());
        }

    }

    public void clearCache(){
        if (mIOCopyHandler != null) {
            mIOCopyHandler.post(new Runnable() {
                @Override
                public void run() {
                    //change by lzt 20210918 注释删除代码
//                    Log.d(TAG,"删除文件1：" + System.currentTimeMillis());
//                    FileUtils.delete(PicConstant.getInstance().getCopyPath());
//                    FileUtils.delete(PicConstant.getInstance().getCropPath());
//                    FileUtils.delete(PicConstant.getInstance().getCameraPath());
//                    Log.d(TAG,"删除文件2：" + System.currentTimeMillis());
                }
            });
        }
    }


    //获取io handler
    public Handler getIOHandler() {
        return mIOCopyHandler;
    }
    public Handler getUIHandler() {
        return mUIHandler;
    }
}
