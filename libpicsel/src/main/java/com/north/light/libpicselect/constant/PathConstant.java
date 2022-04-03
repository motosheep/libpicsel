package com.north.light.libpicselect.constant;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.north.light.libpicselect.utils.FileUtils;

/**
 * author:li
 * date:2022/4/3
 * desc:图片路径管理
 */
public class PathConstant {
    private static final String TAG = PathConstant.class.getSimpleName();
    //内部访问路径，无需权限----------------------------------------------------------------------------

    //图片选择库路径相机
    private String STRING_PATH_LIB_PICSEL_CAMERA = "/camera/";

    //图片选择库剪裁路径
    private String STRING_PATH_LIB_PICSEL_CROP = "/crop/";

    //图片选择库复制路径
    private String STRING_PATH_LIB_PICSEL_COPY = "/copy/";

    //图片选择库录制视频路径
    private String STRING_PATH_LIB_PICSEL_RECORD_VIDEO = "/recordVideo/";

    private static final class SingleHolder {
        static final PathConstant mInstance = new PathConstant();
    }

    public static PathConstant getInstance() {
        return PathConstant.SingleHolder.mInstance;
    }


    /**
     * 删除缓存的文件
     */
    public void deleteCachePath(Context context) {
        try {
            FileUtils.deleteDirectory(getLibPicCopy(context));
            FileUtils.deleteDirectory(getLibPicCamera(context));
            FileUtils.deleteDirectory(getLibPicCrop(context));
            FileUtils.deleteDirectory(getLibPicRecordVideo(context));
        } catch (Exception e) {
            Log.d(TAG, "删除目录文件:" + e.getMessage());
        }
    }

    /**
     * 内部访问路径
     */
    private String getInnerPath(Context context) {
        String externalDir = context.getExternalFilesDir("lib_pic_sel").getAbsolutePath();
        if (!TextUtils.isEmpty(externalDir)) {
            return externalDir;
        }
        //外部目录获取为空
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 图片选择库路径--相机
     */
    public String getLibPicCamera(Context context) {
        return getInnerPath(context) + STRING_PATH_LIB_PICSEL_CAMERA;
    }

    /**
     * 图片选择库剪裁路径
     */
    public String getLibPicCrop(Context context) {
        return getInnerPath(context) + STRING_PATH_LIB_PICSEL_CROP;
    }

    /**
     * 图片选择库复制路径
     */
    public String getLibPicCopy(Context context) {
        return getInnerPath(context) + STRING_PATH_LIB_PICSEL_COPY;
    }

    /**
     * 图片选择库录制视频路径
     */
    public String getLibPicRecordVideo(Context context) {
        return getInnerPath(context) + STRING_PATH_LIB_PICSEL_RECORD_VIDEO;
    }

}
