package com.north.light.libpicselect.model;

import android.content.Context;
import android.widget.ImageView;

import com.north.light.libpicselect.utils.LibPicHandlerManager;

/**
 * create by lzt
 * data 2019/12/8
 * 图片配置信息类
 * 使用前必须配置
 */
public class LibPicSelConfig {
    private BindImageViewListener mbindListener;
    private Context mContext;

    private static final class SingleHolder {
        static final LibPicSelConfig mInstance = new LibPicSelConfig();
    }

    public static LibPicSelConfig getInstance() {
        return SingleHolder.mInstance;
    }

    //必须实现
    public void setLoaderManager(Context context,BindImageViewListener bindListener) {
        this.mbindListener = bindListener;
        this.mContext = context.getApplicationContext();
        //初始化线程
        LibPicHandlerManager.getInstance().init();
        //初始化后，删除复制的图片/拍照缓存图片的目录
        LibPicHandlerManager.getInstance().clearCache();
    }

    public interface BindImageViewListener {
        void BindImageView(String path, ImageView iv);//绑定大图

        void BindSmallImageView(String path, ImageView iv);//绑定小图

        void BindResume();//恢复加载

        void BindPause();//暂停加载
    }

    public BindImageViewListener getBindListener() {
        return mbindListener;
    }

    public Context getContext() {
        return mContext;
    }
}
