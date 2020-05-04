package com.north.light.libpicselect.model;

import android.content.Context;
import android.widget.ImageView;

/**
 * create by lzt
 * data 2019/12/8
 * 图片配置信息类
 * 使用前必须配置
 */
public class PicSelConfig {
    private BindImageViewListener mbindListener;
    private Context mContext;

    private static final class SingleHolder {
        static final PicSelConfig mInstance = new PicSelConfig();
    }

    public static PicSelConfig getInstance() {
        return SingleHolder.mInstance;
    }

    //必须实现
    public void setLoaderManager(Context context,BindImageViewListener bindListener) {
        this.mbindListener = bindListener;
        this.mContext = context.getApplicationContext();
    }

    public interface BindImageViewListener {
        void BindImageView(String path, ImageView iv);//绑定大图

        void BindSmallImageView(String path, ImageView iv);//绑定小图
    }

    public BindImageViewListener getbindListener() {
        return mbindListener;
    }

    public Context getContext() {
        return mContext;
    }
}
