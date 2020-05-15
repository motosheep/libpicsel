package com.north.light.libpicselect.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;


import com.north.light.libpicselect.bean.PicInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by lzt
 * data 2019/12/8
 * 图片选择库
 * Cursor获取数据
 */
public class PicSelectManager implements PicSelectApi {
    private static final String TAG = PicSelectManager.class.getName();
    private Context mContext;
    private volatile boolean isInit = false;
    private Handler mIOHandler;//io handler
    private OnResultListener mCallBack;
    //是否显示gid
    private boolean isShowGif = false;

    private static final class SingleHolder {
        static final PicSelectManager mInstance = new PicSelectManager();
    }

    public static PicSelectManager getInstance() {
        return SingleHolder.mInstance;
    }


    @Override
    public void init(Context context, InitCallBack callBack) {
        this.mContext = context.getApplicationContext();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (callBack != null) {
                callBack.NoPermission();
            }
            return;
        }
        if (isInit) return;
        mIOHandler = new Handler();
        isInit = true;
        if (callBack != null) {
            callBack.Success();
        }
    }

    @Override
    public void load(boolean isShowGif) {
        if (!isInit) {
            return;
        }
        this.isShowGif = isShowGif;
        mIOHandler.removeCallbacksAndMessages(null);
        mIOHandler.post(loadRunnable);
    }

    @Override
    public void release() {
        removeResultListener();
        if (mIOHandler != null) {
            mIOHandler.removeCallbacksAndMessages(null);
        }
        mIOHandler = null;
        isInit = false;
    }

    //加载数据的runnable
    private Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            loadDataByCursor();
        }
    };

    /**
     * 加载数据的函数
     * change by lzt 20200515 增加是否过滤gif的处理逻辑
     */
    private void loadDataByCursor() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<String> picName = new ArrayList();
        List<String> picFileName = new ArrayList();
        List<Integer> picDate = new ArrayList<>();
        while (cursor.moveToNext()) {
            //获取图片的名称
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            if (TextUtils.isEmpty(name)) {
                continue;
            } else {
                //判断是否包含gif
                if (name.toLowerCase().contains(".gif") && !isShowGif) {
                    continue;
                }
            }
            //日期
            int date = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            //获取图片的生成日期
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片的详细信息
            picName.add(name);
            picDate.add(date);
            picFileName.add(new String(data, 0, data.length - 1));
        }
        List<PicInfo> result = new ArrayList<>();
        for (int i = 0; i < picName.size(); i++) {
            PicInfo info = new PicInfo(picName.get(i), picFileName.get(i), picDate.get(i));
            result.add(info);
        }
        //统计目录下文件个数
        Map<String, Long> directoryCount = new HashMap<>();
        for (int j = 0; j < result.size(); j++) {
            if (!TextUtils.isEmpty(result.get(j).getDirectory())) {
                directoryCount.put(result.get(j).getDirectory(),
                        directoryCount.get(result.get(j).getDirectory()) == null ? 1 :
                                directoryCount.get(result.get(j).getDirectory()) + 1);
            }
        }
        for (PicInfo info : result) {
            if (!TextUtils.isEmpty(info.getDirectory()) && directoryCount.get(info.getDirectory()) != null) {
                info.setDirectoryCount(directoryCount.get(info.getDirectory()));
            }
        }
        //最后的结果
        if (mCallBack != null) {
            mCallBack.Data(result);
        }
        //过滤的结果
        Map<String, List<PicInfo>> filterMap = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            String directoryName = result.get(i).getDirectory();
            if (!TextUtils.isEmpty(directoryName)) {
                List<PicInfo> cahce = filterMap.get(directoryName);
                if (cahce != null) {
                    cahce.add(result.get(i));
                } else {
                    cahce = new ArrayList<>();
                    cahce.add(result.get(i));
                }
                filterMap.put(directoryName, cahce);
            }
        }
        //修改时间排序
        for (Map.Entry<String, List<PicInfo>> arg : filterMap.entrySet()) {
            Collections.sort(arg.getValue(), new Comparator<PicInfo>() {
                @Override
                public int compare(PicInfo o1, PicInfo o2) {
                    return o2.getDate() - o1.getDate();
                }
            });
        }
        if (mCallBack != null) {
            mCallBack.FilterData(filterMap);
        }
    }


    //结果回调
    public interface OnResultListener {
        void Data(List<PicInfo> result);//没有过滤的，是全部的结果

        void FilterData(Map<String, List<PicInfo>> filterMap);//过滤了的结果 key为目录，value为该目录下的数据
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.mCallBack = onResultListener;
    }

    private void removeResultListener() {
        this.mCallBack = null;
    }

}