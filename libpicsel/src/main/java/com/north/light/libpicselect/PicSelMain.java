package com.north.light.libpicselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.north.light.libpicselect.bean.LibPicSelIntentInfo;
import com.north.light.libpicselect.bean.LibPicSelMidInfo;
import com.north.light.libpicselect.callback.LibPicSelMediaInfo;
import com.north.light.libpicselect.constant.LibPicConstant;
import com.north.light.libpicselect.constant.LibPicIntentCode;
import com.north.light.libpicselect.constant.LibPicPathConstant;
import com.north.light.libpicselect.databus.LibPicDataBusListener;
import com.north.light.libpicselect.databus.LibPicDataBusManager;
import com.north.light.libpicselect.model.LibPicSelConfig;
import com.north.light.libpicselect.permission.LibPicPermissionCheck;
import com.north.light.libpicselect.permission.LibPicPermissionType;
import com.north.light.libpicselect.ui.LibPicSelMidActivity;
import com.north.light.libpicselect.utils.LibPicSelStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lzt
 * on 2019/11/14
 * <p>
 * <p>
 * 使用该类前，必须调用图片加载函数，选择加载的方式
 * 使用该类时，必须调用init函数  --  PicSelConfig->setLoaderManager
 * change by lzt 20201020 修改传入的浏览图片对象，保存在内存中，不再以intent形式传递，否则会报TransactionTooLargeException
 * <p>
 * review by lzt 20220402 所有页面调用，都进入中间件页面
 */

public class PicSelMain {
    private static final String TAG = PicSelMain.class.getName();
    private CopyOnWriteArrayList<PicCallbackListener> picListener = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<PicActionListener> actionListener = new CopyOnWriteArrayList<>();

    private static final class SingleHolder {
        private static final PicSelMain mInstance = new PicSelMain();
    }

    public static PicSelMain getInstance() {
        return SingleHolder.mInstance;
    }

    /**
     * 初始化--application init-----------------------------------------------------------------
     */
    public void init(Context context, LibPicSelConfig.BindImageViewListener listener) {
        LibPicSelConfig.getInstance().setLoaderManager(context.getApplicationContext(), listener);
        //设置存储路径---------------------------------------------------------------------------
        setCameraPicPath(LibPicPathConstant.getInstance().getLibPicCamera(context));
        setCopyPicPath(LibPicPathConstant.getInstance().getLibPicCopy(context));
        setCropPicPath(LibPicPathConstant.getInstance().getLibPicCopy(context));
        setRecordVideoPath(LibPicPathConstant.getInstance().getLibPicRecordVideo(context));
        //数据bus监听---------------------------------------------------------------------------
        LibPicDataBusManager.getInstance().setDataBusListener(new LibPicDataBusListener() {

            @Override
            public void cameraResult(String path) {
                Log.e(TAG, "cameraResult: " + path);
                for (PicCallbackListener callbackListener : picListener) {
                    callbackListener.cameraResult(path);
                }
            }

            @Override
            public void selectResult(ArrayList<LibPicSelMediaInfo> result) {
                Log.e(TAG, "selectResult");
                for (PicCallbackListener callbackListener : picListener) {
                    callbackListener.selectResult(result);
                }
            }

            @Override
            public void cropResult(String path) {
                Log.e(TAG, "cropResult： " + path);
                for (PicCallbackListener callbackListener : picListener) {
                    callbackListener.cropResult(path);
                }
            }

            @Override
            public void recordVideoPath(String path) {
                Log.e(TAG, "recordVideoPath： " + path);
                for (PicCallbackListener callbackListener : picListener) {
                    callbackListener.recordVideoPath(path);
                }
            }

            @Override
            public void playCusVideoInner(Activity activity, String path) {
                //播放自定义界面视频--图库内部
                Log.e(TAG, "playCusVideo： " + path);
                playCusVideoUI(activity, path);
            }

            @Override
            public void playCusVideoOuter(Activity activity, String path) {
                //播放自定义界面视频--开发者自定义
                for (PicActionListener callbackListener : actionListener) {
                    callbackListener.cusVideoPlay(activity, path);
                }
                activity.finish();
            }

            @Override
            public void takeCameraInnerCus(Activity activity, int org) {
                Log.e(TAG, "takeCameraCus");
                //图库自带自定义相机
                takeCamera(activity, 2, org);
            }

            @Override
            public void takeCameraOuterCus(Activity activity, int org) {
                //开发者自定义相机
                takeCamera(activity, 3, org);
                activity.finish();
            }

            @Override
            public void playVideoSystem(Activity activity, String path) {
                Log.e(TAG, "playVideoSystem");
                playSystemVideo(path);
            }
        });
    }

    /**
     * V2-----------------------------------------------------------------------------------------
     * */

    /**
     * 调用摄像头拍照
     *
     * @param customMode 1系统相机 2图库自定义相机 3开发者自定义相机
     * @param org        1前置 0非前置
     */
    public void takeCamera(Activity activity, int customMode, int org) {
        if (!LibPicPermissionCheck.getInstance().check(LibPicPermissionType.TYPE_CAMERA_EXTERNAL)) {
            noPermissionNotify();
            return;
        }
        if (org != 1 && org != 0) {
            return;
        }
        switch (customMode) {
            case 1:
                takePic(activity, org);
                break;
            case 2:
                takePicCus(activity, org);
                break;
            case 3:
                //开发者自定义UI
                for (PicActionListener callbackListener : actionListener) {
                    callbackListener.cusCameraTake(activity);
                }
                break;
        }
    }

    /**
     * 获取图片
     *
     * @param showCamera    是否显示相机
     * @param cusCameraMode 拍照情况下，是否使用自定义相机
     * @param limit         选择图片数量
     */
    public void getPic(Activity activity, boolean showCamera,
                       int limit, boolean showVideo,
                       boolean showGif, int cusCameraMode, int cusPlayerMode) {
        if (!LibPicPermissionCheck.getInstance().check(LibPicPermissionType.TYPE_CAMERA_EXTERNAL)) {
            noPermissionNotify();
            return;
        }
        getPicVideoMul(false, activity, limit, showCamera, showVideo, showGif, cusCameraMode, cusPlayerMode);
    }

    /**
     * 录制视频
     */
    public void recordVideo(Activity activity, int second) {
        if (!LibPicPermissionCheck.getInstance().check(LibPicPermissionType.TYPE_CAMERA_RECORD)) {
            noPermissionNotify();
            return;
        }
        //系统录制视频
        recordVideoInner(activity, second);
    }

    /**
     * 剪裁图片
     */
    public void cropPic(Activity activity, String filePath, boolean custom, int widthRate, int heightRite) {
        if (!LibPicPermissionCheck.getInstance().check(LibPicPermissionType.TYPE_EXTERNAL)) {
            noPermissionNotify();
            return;
        }
        if (!custom) {
            crop(activity, filePath, widthRate, heightRite, widthRate, heightRite);
        } else {
            cropCus(activity, filePath, widthRate, heightRite);
        }
    }


    /**
     * 浏览图片
     */
    public void browsePic(List<String> picList, Activity activity, int pos) {
        browsePic(picList, activity, pos, 1);
    }

    /**
     * 浏览图片
     *
     * @param videoWay 浏览视频时的方式：1系统自带 2图库自带 3开发者自定义
     */
    public void browsePic(List<String> picList, Activity activity, int pos, int videoWay) {
        if (picList == null || picList.size() == 0) {
            return;
        }
        Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
        LibPicSelIntentInfo.getInstance().setPicList(picList);
        LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
        midInfo.putWrapper(LibPicIntentCode.BROWSER_POSITION, pos);
        midInfo.putWrapper(LibPicIntentCode.BROWSER_VIDEO_WAY, videoWay);
        midInfo.setREQ_TYPE(LibPicIntentCode.BROWSER_CODE_REQUEST);
        intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
        activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
    }

    //内部实现func----------------------------------------------------------------------------

    /**
     * 图片库播放视频页面
     */
    private void playCusVideoUI(Activity activity, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
        midInfo.putWrapper(LibPicIntentCode.CUS_VIDEO_PLAY_PATH, path);
        midInfo.setREQ_TYPE(LibPicIntentCode.CUS_VIDEO_PLAY_REQ);
        Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
        intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
        activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
    }

    /**
     * 获取多个图片or视频
     *
     * @param cusPlayerMode 1系统默认 2使用图片自带的播放 3使用开发者自定义的播放
     * @param cusCameraMode 1系统默认 2使用图片自带的相机 3使用开发者自定义的相机
     */
    private void getPicVideoMul(boolean isTakeCamera, Activity activity, int size, boolean showCamera
            , boolean showVideo, boolean showGif, int cusCameraMode, int cusPlayerMode) {
        if (isTakeCamera) {
            takeCamera(activity, cusCameraMode, 0);
        } else {
            //进入三方图片选择
            LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
            midInfo.putWrapper(LibPicIntentCode.PIC_SEL_DATA_LIMIT, size);
            midInfo.putWrapper(LibPicIntentCode.PIC_SEL_DATA_NEED_CAMERA, showCamera);
            midInfo.putWrapper(LibPicIntentCode.PIC_SEL_DATA_SHOW_VIDEO, showVideo);
            midInfo.putWrapper(LibPicIntentCode.PIC_SEL_DATA_CUS_CAMERA, cusCameraMode);
            midInfo.putWrapper(LibPicIntentCode.PIC_SEL_DATA_SHOW_GIF, showGif);
            midInfo.putWrapper(LibPicIntentCode.PIC_SEL_DATA_CUS_VIDEO_PLAYER, cusPlayerMode);
            midInfo.setREQ_TYPE(LibPicIntentCode.PIC_SEL_REQ);
            Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
            intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
            activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
        }
    }

    /**
     * 调起原生相机录制视频
     * MediaStore.EXTRA_OUTPUT：设置媒体文件的保存路径。
     * MediaStore.EXTRA_VIDEO_QUALITY：设置视频录制的质量，0为低质量，1为高质量。
     * MediaStore.EXTRA_DURATION_LIMIT：设置视频最大允许录制的时长，单位为毫秒。
     * MediaStore.EXTRA_SIZE_LIMIT：指定视频最大允许的尺寸，单位为byte。
     *
     * @return false没有权限 true有权限
     */
    private boolean recordVideoInner(Activity activity, int second) {
        Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
        LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
        midInfo.putWrapper(LibPicIntentCode.VIDEO_RECODE_SECOND, second);
        midInfo.setREQ_TYPE(LibPicIntentCode.VIDEO_RECORD_REQ);
        intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
        activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
        return true;
    }

    /**
     * 调起相机拍照
     * change by lzt 20200922 增加是否打开前置摄像头的入参
     * type:1打开前置
     */
    private void takePic(Activity activity, int type) {
        try {
            String path = LibPicConstant.getInstance().getCameraPath() + System.currentTimeMillis() + ".jpg";
            //intent wrapper
            Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
            LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
            if (Build.VERSION.SDK_INT >= 24) {
                midInfo.addFlag(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            midInfo.addAction(MediaStore.ACTION_IMAGE_CAPTURE);
            if (type == 1) {
                midInfo.putWrapper("android.intent.extras.CAMERA_FACING", 1);
            }
            midInfo.setREQ_TYPE(LibPicIntentCode.PIC_MAIN_TAKE_PIC_REQUEST);
            midInfo.setTakePicTargetPath(path);
            intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
            activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, "拍照异常： " + e);
        }
    }

    /**
     * 调起自定义相机拍摄
     */
    private void takePicCus(Activity activity, int type) {
        try {
            String path = LibPicConstant.getInstance().getCameraPath() + System.currentTimeMillis() + ".jpg";
            //intent wrapper
            Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
            LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
            midInfo.putWrapper(LibPicIntentCode.PATH_STRING_CAMERAX_TAKE_PIC, path);
            midInfo.putWrapper(LibPicIntentCode.PATH_STRING_CAMERAX_TAKE_ORG, type);
            midInfo.setREQ_TYPE(LibPicIntentCode.CAMERAX_TAKE_PIC_REQ);
            intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
            activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, "拍照异常： " + e);
        }
    }

    /**
     * 调用安卓的图片剪裁程序对用户选择的头像进行剪裁
     *
     * @param filePath 源文件地址
     * @param aspectX  宽比例
     * @param aspectY  高比例
     * @param outputX  输出宽
     * @param outputY  输出高
     */
    private void crop(Activity activity, String filePath, int aspectX, int aspectY, int outputX, int outputY) {
        try {
            Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
            LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
            midInfo.setCropPicTargetPath(LibPicConstant.getInstance().getCropPath() + System.currentTimeMillis() + ".jpg");
            midInfo.setCropPicSourcePath(filePath);
            midInfo.addAction("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= 24) {
                midInfo.addFlag(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            midInfo.putWrapper("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            midInfo.putWrapper("scale", true);//去除黑边
            midInfo.putWrapper("scaleUpIfNeeded", true);//去除黑边
            midInfo.putWrapper("crop", true);
            midInfo.putWrapper("return-data", false);
            midInfo.putWrapper("outputX", outputX);
            midInfo.putWrapper("outputY", outputY);
            if (aspectX == aspectY) {
                //对称
                String huawei = "huawei";
                String honor = "honor";
                String model = LibPicSelStringUtils.toNoEmptyStr(android.os.Build.MODEL).toLowerCase();
                String manufacturer = LibPicSelStringUtils.toNoEmptyStr(android.os.Build.MANUFACTURER).toLowerCase();
                if (model.contains(huawei) || manufacturer.contains(huawei) ||
                        model.contains(honor) || manufacturer.contains(honor)) {
                    //华为特殊处理 不然会显示圆
                    midInfo.putWrapper("aspectX", 9998);
                    midInfo.putWrapper("aspectY", 9999);
                } else {
                    midInfo.putWrapper("aspectX", aspectX);
                    midInfo.putWrapper("aspectY", aspectY);
                }
            }
            midInfo.setREQ_TYPE(LibPicIntentCode.PIC_MAIN_CROP_PIC_REQUEST);
            intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
            activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
        } catch (Exception e) {
            Log.e(TAG, "剪裁图片异常： " + e);
        }
    }


    /**
     * 自定义view剪裁图片
     */
    private void cropCus(Activity activity, String filePath, int widthRate, int heightRate) {
        try {
            Intent intent1 = new Intent(activity, LibPicSelMidActivity.class);
            LibPicSelMidInfo midInfo = new LibPicSelMidInfo();
            String corpCusPicTarPath = LibPicConstant.getInstance().getCropPath() + System.currentTimeMillis() + ".jpg";
            midInfo.setCusCropPicSourcePath(filePath);
            midInfo.setCusCropPicTargetPath(corpCusPicTarPath);
            midInfo.putWrapper(LibPicIntentCode.PIC_CROP_DATA_ORG_PATH, filePath);
            midInfo.putWrapper(LibPicIntentCode.PIC_CROP_DATA_TAR_PATH, corpCusPicTarPath);
            midInfo.putWrapper(LibPicIntentCode.PIC_CROP_PIC_RATE_WIDTH, widthRate);
            midInfo.putWrapper(LibPicIntentCode.PIC_CROP_PIC_RATE_HEIGHT, heightRate);
            midInfo.setREQ_TYPE(LibPicIntentCode.PIC_CROP_CODE_REQ);
            intent1.putExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS, midInfo);
            activity.startActivityForResult(intent1, LibPicIntentCode.PIC_SEL_MID_REQ_CODE);
        } catch (Exception e) {

        }
    }

    /**
     * 播放视频
     */
    private void playSystemVideo(String path) {
        try {
            //系统默认
            if (path.startsWith("http")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String type = "video/*";
                Uri uri = Uri.parse(path);
                intent.setDataAndType(uri, type);
                LibPicSelConfig.getInstance().getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                File file = new File(path);
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= 24) {
                    //7.0以上的拍照
                    uri = FileProvider.getUriForFile(LibPicSelConfig.getInstance().getContext(),
                            LibPicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", file);
                } else {
                    //7.0以下的拍照
                    uri = Uri.fromFile(file);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "video/*");
                //进入拍照页
                LibPicSelConfig.getInstance().getContext().startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "播放视频异常： " + e);
        }
    }

    /**
     * 没有权限的通知
     */
    private void noPermissionNotify() {
        for (PicCallbackListener callbackListener : picListener) {
            callbackListener.error("no permission,please grant");
        }
    }

    //路径--------------------------------------------------------------------------------------

    /**
     * 设置录像路径
     */
    public void setRecordVideoPath(String path) {
        LibPicConstant.getInstance().setRecordVideo(path);
    }

    /**
     * 设置拍摄图片的路径
     */
    public void setCameraPicPath(String path) {
        LibPicConstant.getInstance().setCameraPath(path);
    }

    /**
     * 设置剪裁的路径
     */
    public void setCropPicPath(String path) {
        LibPicConstant.getInstance().setCropPath(path);
    }

    /**
     * 设置图片复制路径
     */
    public void setCopyPicPath(String path) {
        LibPicConstant.getInstance().setCopyPath(path);
    }

    //监听--------------------------------------------------------------------------------------

    public void setPicCallBackListener(PicCallbackListener t) {
        picListener.add(t);
    }

    public void removePicCallBackListener(PicCallbackListener t) {
        picListener.remove(t);
    }


    public void setActionListener(PicActionListener t) {
        actionListener.add(t);
    }

    public void removeActionListener(PicActionListener t) {
        actionListener.remove(t);
    }


}
