package com.north.light.libpicselect;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.north.light.libpicselect.bean.PicSelIntentInfo;
import com.north.light.libpicselect.constant.IntentCode;
import com.north.light.libpicselect.constant.PicConstant;
import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.ui.PicBrowserActivity;
import com.north.light.libpicselect.ui.PicClipActivity;
import com.north.light.libpicselect.ui.PicSelectActivity;
import com.north.light.libpicselect.ui.VideoRecordActivity;
import com.north.light.libpicselect.utils.FileUtils;
import com.north.light.libpicselect.utils.HandlerManager;
import com.north.light.libpicselect.utils.LibPicSelStringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
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
 */

public class PicSelMain {
    private static final String TAG = PicSelMain.class.getName();
    private Uri mCurUrl;//图片拍照url
    private String takePicPath;//系统图片拍照路径
    private String corpPicPath;//系统图片剪裁路径
    private String corpCusPicOrgPath;//自定义图片剪裁原图路径
    private String corpCusPicTarPath;//自定义图片剪裁目标路径
    //选择的图片中转list
    private static volatile ArrayList<String> finalList = new ArrayList<>();
    private CopyOnWriteArrayList<PlayVideoListener> videoListener = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<CusCameraListener> cameraListener = new CopyOnWriteArrayList<>();

    private static final class SingleHolder {
        private static final PicSelMain mInstance = new PicSelMain();
    }

    public static PicSelMain getInstance() {
        return SingleHolder.mInstance;
    }


    /**
     * 获取图片
     * 单个
     *
     * @param isTakeCamera 是否使用相机拍照
     */
    public void getPicSingle(boolean isTakeCamera, Activity activity, boolean showCamera, boolean cusCamera) {
        getPicVideoMul(isTakeCamera, activity, 1, showCamera, false, cusCamera);
    }

    /**
     * 获取图片--前置拍照
     * 单个
     */
    public void getFontPic(Activity activity) {
        takePic(activity, 1);
    }

    /**
     * 获取图片
     * 多个
     */
    public void getPicMul(boolean isTakeCamera, Activity activity, int size, boolean showCamera) {
        getPicVideoMul(isTakeCamera, activity, size, showCamera, false, false);
    }

    /**
     * 获取多个图片or视频
     */
    public void getPicVideoMul(boolean isTakeCamera, Activity activity, int size, boolean showCamera
            , boolean showVideo, boolean cusCamera) {
        if (isTakeCamera) {
            takePic(activity, 0);
        } else {
            //进入三方图片选择
            Intent intent1 = new Intent(activity, PicSelectActivity.class);
            intent1.putExtra(IntentCode.PIC_SEL_DATA_LIMIT, size);
            intent1.putExtra(IntentCode.PIC_SEL_DATA_NEED_CAMERA, showCamera);
            intent1.putExtra(IntentCode.PIC_SEL_DATA_SHOW_VIDEO, showVideo);
            intent1.putExtra(IntentCode.PIC_SEL_DATA_CUS_CAMERA, cusCamera);
            activity.startActivityForResult(intent1, IntentCode.PIC_SEL_REQ);
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
    public boolean recordVideo(Activity activity, int second) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Intent intent1 = new Intent(activity, VideoRecordActivity.class);
        intent1.putExtra(IntentCode.VIDEO_RECODE_SECOND, second);
        activity.startActivityForResult(intent1, IntentCode.VIDEO_REQ);
        return true;
    }

    //调起相机拍照
    //change by lzt 20200922 增加是否打开前置摄像头的入参
    //type:1打开前置
    public void takePic(Activity activity, int type) {
        try {
            WeakReference<Activity> weakAct = new WeakReference<Activity>(activity);
            Intent intent = new Intent();
            takePicPath = PicConstant.getInstance().getCameraPath() + System.currentTimeMillis() + ".jpg";
            File file = new File(takePicPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                //7.0以上的拍照
                mCurUrl = FileProvider.getUriForFile(
                        PicSelConfig.getInstance().getContext(),
                        PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurUrl);//将拍取的照片保存到指定URI
                if (type == 1) {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                }
            } else {
                //7.0以下的拍照
                mCurUrl = Uri.fromFile(file);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurUrl);//将拍取的照片保存到指定URI
                if (type == 1) {
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                }
            }
            //进入拍照页
            weakAct.get().startActivityForResult(intent, IntentCode.PIC_MAIN_TAKE_PIC_RESULT);
        } catch (Exception e) {
            Log.d(TAG, "拍照异常： " + e);
        }
    }

    /**
     * 调用安卓的图片剪裁程序对用户选择的头像进行剪裁
     *
     * @param filePath 用户选取的头像在SD上的地址
     * @param aspectX  宽比例
     * @param aspectY  高比例
     * @param outputX  输出宽
     * @param outputY  输出高
     */
    public void crop(Activity activity, String filePath, int aspectX, int aspectY, int outputX, int outputY) {
        try {
            WeakReference<Activity> weakAct = new WeakReference<Activity>(activity);
            //输出路径
            corpPicPath = PicConstant.getInstance().getCropPath() + System.currentTimeMillis() + ".jpg";
            File file = new File(corpPicPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //隐式intent
            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri inputUrl = null;
            if (Build.VERSION.SDK_INT >= 24) {
                inputUrl = FileProvider.getUriForFile(
                        PicSelConfig.getInstance().getContext(),
                        PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", new File(filePath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                inputUrl = Uri.fromFile(new File(filePath));
            }
            // 设置剪裁数据
            intent.putExtra("scale", true);//去除黑边
            intent.putExtra("scaleUpIfNeeded", true);//去除黑边
            intent.setDataAndType(inputUrl, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("return-data", false);

            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
            if (aspectX == aspectY) {
                //对称
                String huawei = "huawei";
                String honor = "honor";
                String model = LibPicSelStringUtils.toNoEmptyStr(android.os.Build.MODEL).toLowerCase();
                String manufacturer = LibPicSelStringUtils.toNoEmptyStr(android.os.Build.MANUFACTURER).toLowerCase();
                if (model.contains(huawei) || manufacturer.contains(huawei) ||
                        model.contains(honor) || manufacturer.contains(honor)) {
                    //华为特殊处理 不然会显示圆
                    intent.putExtra("aspectX", 9998);
                    intent.putExtra("aspectY", 9999);
                } else {
                    intent.putExtra("aspectX", aspectX);
                    intent.putExtra("aspectY", aspectY);
                }
            }
            // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            weakAct.get().startActivityForResult(intent, IntentCode.PIC_MAIN_CROP_PIC_REQUEST);
        } catch (Exception e) {
            Log.d(TAG, "剪裁图片异常： " + e);
        }
    }


    /**
     * 必须重写
     * activity结果返回
     * change by lzt 20201023 增加选择图片时，特殊字符图片处理逻辑
     */
    public <T extends PicCallbackListener> void ActivityForResult(int requestCode, int resultCode, Intent data, final T listener) {
        if (resultCode == RESULT_OK && requestCode == IntentCode.PIC_MAIN_TAKE_PIC_RESULT) {
            try {
                Log.d(TAG, "添加图片返回: " + takePicPath);
                if (listener != null) {
                    listener.cameraResult(takePicPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == IntentCode.PIC_SEL_REQ && resultCode == IntentCode.PIC_SEL_RES) {
            //添加图片返回
            try {
                final ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(IntentCode.PIC_SEL_DATA_SELECT);
                if (images != null && images.size() > 0) {
                    //复制图片
                    if (HandlerManager.getInstance().getCopyHandler() != null) {
                        HandlerManager.getInstance().getCopyHandler().removeCallbacksAndMessages(null);
                        HandlerManager.getInstance().getCopyHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                finalList.clear();
                                Log.d(TAG, "复制图片时间1: " + System.currentTimeMillis());
                                for (String pic : images) {
                                    String newPath = FileUtils.copyFileUsingFileStreams(pic, PicConstant.getInstance().getCopyPath());
                                    if (!TextUtils.isEmpty(newPath)) {
                                        finalList.add(finalList.size(),newPath);
                                    }
                                }
                                //add by lzt 20211109 增加情况，假设图片路径复制失败，则直接获取原图路径，兜底
                                if ((finalList != null && finalList.size() == 0)) {
                                    if (images != null && images.size() != 0) {
                                        Log.d(TAG, "复制图片失败，直接获取原图路径");
                                        finalList.addAll(images);
                                    }
                                }
                                Log.d(TAG, "复制图片时间2: " + System.currentTimeMillis());
                                //主线程--notify
                                if (HandlerManager.getInstance().getUIHandler() != null) {
                                    HandlerManager.getInstance().getUIHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (listener != null) {
                                                listener.selectResult(finalList);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
            }
        }
        if (requestCode == IntentCode.PIC_MAIN_CROP_PIC_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "剪裁图片路径返回: " + corpPicPath);
            try {
                if (listener != null) {
                    listener.cropResult(corpPicPath);
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
            }
        }
        if (requestCode == IntentCode.PIC_CROP_CODE_REQ && resultCode == IntentCode.PIC_CROP_CODE_RES) {
            //自定义剪裁图片返回
            try {
                String cropPath = data.getStringExtra(IntentCode.PIC_CROP_DATA_CLIP_DATA);
                Log.d(TAG, "自定义剪裁图片原图: " + corpCusPicOrgPath);
                Log.d(TAG, "自定义剪裁图片目标: " + corpCusPicTarPath);
                Log.d(TAG, "自定义剪裁图片结果: " + cropPath);
                if (listener != null && !TextUtils.isEmpty(cropPath)) {
                    listener.cropResult(cropPath);
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
                //直接返回原图好了--防止出错后，不能进行后续的步骤
                if (listener != null) {
                    listener.cropResult(corpCusPicOrgPath);
                }
            }
        }
        if (requestCode == IntentCode.VIDEO_REQ && resultCode == IntentCode.VIDEO_RES) {
            Log.d(TAG, "录制视频返回");
            try {
                String path = data.getStringExtra(IntentCode.VIDEO_RECODE_PATH);
                if (listener != null) {
                    listener.recordVideoPath(path);
                }
            } catch (Exception e) {
                Log.d(TAG, "录制视频返回e: " + e);
            }
        }

    }

    //浏览图片
    public void browsePic(List<String> picList, Activity activity, int pos) {
        browsePic(picList, activity, pos, 1);
    }

    public void browsePic(List<String> picList, Activity activity, int pos, int videoWay) {
        if (picList == null || picList.size() == 0) {
            return;
        }
        Intent intent = new Intent(activity, PicBrowserActivity.class);
        PicSelIntentInfo.getInstance().setPicList(picList);
        intent.putExtra(IntentCode.BROWSER_POSITION, pos);
        intent.putExtra(IntentCode.BROWSER_VIDEO_WAY, videoWay);
        activity.startActivity(intent);
    }

    /**
     * 发送播放视频url的广播--内部调用
     */
    public void sendPlayUrlIntent(Context context, String path) {
        Intent dataIntent = new Intent();
        dataIntent.setAction(IntentCode.BROWSER_PIC_SEL_BROADCAST_INTENT);
        dataIntent.putExtra(IntentCode.BROWSER_PIC_SEL_BROADCAST_TYPE, 1);
        dataIntent.putExtra(IntentCode.BROWSER_VIDEO_BROADCAST_DATA, path);
        context.getApplicationContext().sendBroadcast(dataIntent);
    }
    /**
     * 发送使用自定义相机--内部调用
     */
    public void sendCusCameraIntent(Context context) {
        Intent dataIntent = new Intent();
        dataIntent.setAction(IntentCode.BROWSER_PIC_SEL_BROADCAST_INTENT);
        dataIntent.putExtra(IntentCode.BROWSER_PIC_SEL_BROADCAST_TYPE, 2);
        context.getApplicationContext().sendBroadcast(dataIntent);
    }


    //播放本地视频
    public void playLocalVideo(String path, Activity activity) {
        try {
            WeakReference<Activity> weakAct = new WeakReference<Activity>(activity);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(path);
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                //7.0以上的拍照
                uri = FileProvider.getUriForFile(
                        PicSelConfig.getInstance().getContext(),
                        PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", file);
            } else {
                //7.0以下的拍照
                mCurUrl = Uri.fromFile(file);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "video/*");
            //进入拍照页
            weakAct.get().startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "播放视频异常： " + e);
        }
    }

    //播放网络视频
    public void playNetVideo(String url, Activity activity) {
        try {
            WeakReference<Activity> weakAct = new WeakReference<Activity>(activity);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String type = "video/*";
            Uri uri = Uri.parse(url);
            intent.setDataAndType(uri, type);
            weakAct.get().startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "播放视频异常： " + e);
        }
    }

    /**
     * 初始化广播接收
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                int type = intent.getIntExtra(IntentCode.BROWSER_PIC_SEL_BROADCAST_TYPE, -1);
                if (!TextUtils.isEmpty(action) && type != -1) {
                    if (type == 1) {
                        String playLink = intent.getStringExtra(IntentCode.BROWSER_VIDEO_BROADCAST_DATA);
                        if (TextUtils.isEmpty(playLink)) {
                            return;
                        }
                        //播放视频
                        for (PlayVideoListener listener : videoListener) {
                            listener.playVideo(playLink);
                        }
                    } else if (type == 2) {
                        //自定义相机
                        for (CusCameraListener listener : cameraListener) {
                            listener.cusCamera();
                        }
                    }
                }
            } catch (Exception e) {

            }
        }
    };

    /**
     * 初始化广播
     */
    public void initBroadCast(Context context) {
        releaseBroadCast(context);
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(IntentCode.BROWSER_PIC_SEL_BROADCAST_INTENT);
            context.getApplicationContext().registerReceiver(broadcastReceiver, filter);
        } catch (Exception e) {

        }
    }

    /**
     * 释放广播
     */
    public void releaseBroadCast(Context context) {
        try {
            context.getApplicationContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {

        }
    }

    /**
     * 自定义view剪裁图片--未经充分测试，慎用
     */
    public void cropCus(Activity activity, String filePath, int widthRate, int heightRate) {
        try {
            corpCusPicOrgPath = filePath;
            corpCusPicTarPath = PicConstant.getInstance().getCropPath() + System.currentTimeMillis() + ".jpg";
            Intent intent = new Intent(activity, PicClipActivity.class);
            intent.putExtra(IntentCode.PIC_CROP_DATA_ORG_PATH, filePath);
            intent.putExtra(IntentCode.PIC_CROP_DATA_TAR_PATH, corpCusPicTarPath);
            intent.putExtra(IntentCode.PIC_CROP_PIC_RATE_WIDTH, widthRate);
            intent.putExtra(IntentCode.PIC_CROP_PIC_RATE_HEIGHT, heightRate);
            activity.startActivityForResult(intent, IntentCode.PIC_CROP_CODE_REQ);
        } catch (Exception e) {

        }
    }

    //路径--------------------------------------------------------------------------------------

    /**
     * 设置录像路径
     */
    public void setRecordVideoPath(String path) {
        PicConstant.getInstance().setRecordVideo(path);
    }

    /**
     * 设置拍摄图片的路径
     */
    public void setCameraPicPath(String path) {
        PicConstant.getInstance().setCameraPath(path);
    }

    /**
     * 设置剪裁的路径
     */
    public void setCropPicPath(String path) {
        PicConstant.getInstance().setCropPath(path);
    }

    /**
     * 设置图片复制路径
     */
    public void setCopyPicPath(String path) {
        PicConstant.getInstance().setCopyPath(path);
    }

    /**
     * bitmap保存本地方法
     * change by lzt 20201112 通知本地媒体类更新数据
     */
    public String saveBitmap(Bitmap bm, String path) {
        Log.e(TAG, "保存图片");
        File f = new File(path);
        if (!f.exists()) {
            FileUtils.createFile(f, true);
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            Log.i(TAG, "已经保存");
            try {
//                MediaStore.Images.Media.insertImage(
//                        PicSelConfig.getInstance().getContext().getContentResolver(),
//                        f.getAbsolutePath(), f.getName(), null);
//                PicSelConfig.getInstance().getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                        Uri.parse("file://" + f.getAbsolutePath())));
            } catch (Exception e) {

            }
            out.flush();
            out.close();
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //接口回调
    public interface PicCallbackListener {
        //拍照
        void cameraResult(String path);

        //选择图片
        void selectResult(ArrayList<String> result);

        //剪裁图片
        void cropResult(String path);

        //录制视频
        void recordVideoPath(String path);
    }

    public interface PlayVideoListener {
        void playVideo(String url);
    }

    public void setPlayVideoListener(PlayVideoListener playVideoListener) {
        videoListener.add(playVideoListener);
    }

    public void removePlayVideoListener(PlayVideoListener playVideoListener) {
        videoListener.remove(playVideoListener);
    }

    public interface CusCameraListener {
        void cusCamera();
    }

    public void setCusCameraListener(CusCameraListener cusCameraListener) {
        cameraListener.add(cusCameraListener);
    }

    public void removeCusCameraListener(CusCameraListener cusCameraListener) {
        cameraListener.remove(cusCameraListener);
    }


}
