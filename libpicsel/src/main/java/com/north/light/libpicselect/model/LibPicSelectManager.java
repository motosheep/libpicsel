package com.north.light.libpicselect.model;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.north.light.libpicselect.bean.LibPicInfo;
import com.north.light.libpicselect.permission.LibPicPermissionCheck;
import com.north.light.libpicselect.permission.LibPicPermissionType;
import com.north.light.libpicselect.utils.LibPicHandlerManager;

import java.io.File;
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
public class LibPicSelectManager implements LibPicSelectApi {
    private static final String TAG = LibPicSelectManager.class.getName();
    private Context mContext;
    private volatile boolean isInit = false;
    private OnResultListener mCallBack;
    //是否显示gif
    private boolean isShowGif = false;
    //是否显示视频
    private boolean isShowVideo = false;

    private static final class SingleHolder {
        static final LibPicSelectManager mInstance = new LibPicSelectManager();
    }

    public static LibPicSelectManager getInstance() {
        return SingleHolder.mInstance;
    }


    @Override
    public void init(Context context, InitCallBack callBack) {
        this.mContext = context.getApplicationContext();
        if (!LibPicPermissionCheck.getInstance().check(LibPicPermissionType.TYPE_EXTERNAL)) {
            if (callBack != null) {
                callBack.NoPermission();
            }
            return;
        }
        if (isInit) return;
        isInit = true;
        if (callBack != null) {
            callBack.Success();
        }
    }

    /**
     * change by lzt  20200823增加是否显示视频的标识
     */
    @Override
    public void load(boolean isShowGif, boolean isShowVideo) {
        if (!isInit) {
            return;
        }
        this.isShowVideo = isShowVideo;
        this.isShowGif = isShowGif;
        if (LibPicHandlerManager.getInstance().getIOHandler() != null) {
            LibPicHandlerManager.getInstance().getIOHandler().removeCallbacksAndMessages(null);
            LibPicHandlerManager.getInstance().getIOHandler().post(loadRunnable);
        }
    }

    @Override
    public void release() {
        removeResultListener();
        if (LibPicHandlerManager.getInstance().getIOHandler() != null) {
            LibPicHandlerManager.getInstance().getIOHandler().removeCallbacksAndMessages(null);
        }
        isInit = false;
    }

    //加载数据的runnable
    private Runnable loadRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                loadDataByCursor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 加载数据的函数
     * change by lzt 20200515 增加是否过滤gif的处理逻辑
     */
    private void loadDataByCursor() throws Exception {
        //获取图片数据
        Cursor picCursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        List<String> picName = new ArrayList();
        List<String> picFileName = new ArrayList();
        List<Integer> picDate = new ArrayList<>();
        List<LibPicInfo> cacheRes = new ArrayList<>();
        while (picCursor.moveToNext()) {
            //获取图片的名称
            String name = picCursor.getString(picCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            if (TextUtils.isEmpty(name)) {
                continue;
            } else {
                //判断是否包含gif
                if (name.toLowerCase().contains(".gif") && !isShowGif) {
                    continue;
                }
            }
            //日期
            int date = picCursor.getInt(picCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            //获取图片的生成日期
            byte[] data = picCursor.getBlob(picCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //获取图片的详细信息
            picName.add(name);
            picDate.add(date);
            picFileName.add(new String(data, 0, data.length - 1));
        }
        //合并数据__图片
        for (int i = 0; i < picName.size(); i++) {
            LibPicInfo info = new LibPicInfo(picName.get(i), picFileName.get(i), picDate.get(i), 1);
            cacheRes.add(info);
        }
        //获取视频数据
        if (isShowVideo) {
            List<String> videoName = new ArrayList();
            List<String> videoFileName = new ArrayList();
            List<Integer> videoDate = new ArrayList<>();
            Cursor videoCursor = mContext.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            while (videoCursor.moveToNext()) {
                //获取视频的名称
                String name = videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                //日期
                int date = videoCursor.getInt(videoCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
                //获取视频的生成日期
                byte[] data = videoCursor.getBlob(videoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                videoName.add(name);
                videoDate.add(date);
                videoFileName.add(new String(data, 0, data.length - 1));
            }
            //合并数据__视频
            for (int i = 0; i < videoName.size(); i++) {
                LibPicInfo info = new LibPicInfo(videoName.get(i), videoFileName.get(i), videoDate.get(i), 2);
                cacheRes.add(info);
            }
        }
        //查询文件是否存在--文件大小是否为0
        List<LibPicInfo> result = new ArrayList<>();
        try {
            for (int i = 0; i < cacheRes.size(); i++) {
                File existFile = new File(cacheRes.get(i).getPath());
                if (!existFile.exists()) {
                    continue;
                }
                if (existFile.length() == 0) {
                    continue;
                }
                result.add(cacheRes.get(i));
            }
            cacheRes.clear();
        } catch (Exception e) {
            Log.d(TAG, "处理文件错误:\n" + e.getMessage());
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
        for (LibPicInfo info : result) {
            if (!TextUtils.isEmpty(info.getDirectory()) && directoryCount.get(info.getDirectory()) != null) {
                info.setDirectoryCount(directoryCount.get(info.getDirectory()));
            }
        }
        //最后的结果
        Collections.sort(result, new Comparator<LibPicInfo>() {
            @Override
            public int compare(LibPicInfo o1, LibPicInfo o2) {
                return o2.getDate() - o1.getDate();
            }
        });
        if (mCallBack != null) {
            mCallBack.Data(result);
        }
        //过滤的结果
        Map<String, List<LibPicInfo>> filterMap = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            String directoryName = result.get(i).getDirectory();
            if (!TextUtils.isEmpty(directoryName)) {
                List<LibPicInfo> cahce = filterMap.get(directoryName);
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
        for (Map.Entry<String, List<LibPicInfo>> arg : filterMap.entrySet()) {
            Collections.sort(arg.getValue(), new Comparator<LibPicInfo>() {
                @Override
                public int compare(LibPicInfo o1, LibPicInfo o2) {
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
        void Data(List<LibPicInfo> result);//没有过滤的，是全部的结果

        void FilterData(Map<String, List<LibPicInfo>> filterMap);//过滤了的结果 key为目录，value为该目录下的数据
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        this.mCallBack = onResultListener;
    }

    private void removeResultListener() {
        this.mCallBack = null;
    }

}
