package com.north.light.libpicselect;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.ui.PicBrowserActivity;
import com.north.light.libpicselect.ui.PicSelectActivity;
import com.north.light.libpicselect.ui.VideoRecordActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lzt
 * on 2019/11/14
 * <p>
 * <p>
 * 使用该类前，必须调用图片加载函数，选择加载的方式
 * 使用该类时，必须调用init函数
 */

public class PicSelMain {
    private static final int TAKEPIC_RESULT = 0x1111;
    private static final int SELECTPIC_RESULT = 0x1112;
    private static final int CROPPIC_REQUEST = 0x1113;
    private static final int VIDEOREC_RESULT = 0x1114;
    private static final String TAG = PicSelMain.class.getName();

    private Uri mCurUrl;//图片拍照url
    private String takePicPath;//图片拍照路径

    private String corpPicPath;//图片剪裁路径

    private static final class SingleHolder {
        private static final PicSelMain mInstance = new PicSelMain();
    }

    public static PicSelMain getIntance() {
        return SingleHolder.mInstance;
    }

    /**
     * 获取图片
     * 单个
     *
     * @param isTakeCamera 是否使用相机拍照
     */
    public void getPicSingle(boolean isTakeCamera, Activity activity, boolean showCamera) {
        if (isTakeCamera) {
            takePic(activity);
        } else {
            //进入三方图片选择
            Intent intent1 = new Intent(activity, PicSelectActivity.class);
            intent1.putExtra(PicSelectActivity.CODE_LIMIT, 1);
            intent1.putExtra(PicSelectActivity.CODE_NEEDCAMERA, showCamera);
            activity.startActivityForResult(intent1, PicSelectActivity.CODE_REQUEST);
        }
    }

    /**
     * 获取图片
     * 多个
     */
    public void getPicMul(boolean isTakeCamera, Activity activity, int size, boolean showCamera) {
        if (isTakeCamera) {
            takePic(activity);
        } else {
            //进入三方图片选择
            Intent intent1 = new Intent(activity, PicSelectActivity.class);
            intent1.putExtra(PicSelectActivity.CODE_LIMIT, size);
            intent1.putExtra(PicSelectActivity.CODE_NEEDCAMERA, showCamera);
            activity.startActivityForResult(intent1, PicSelectActivity.CODE_REQUEST);
        }
    }

    /**
     * 获取多个图片or视频
     */
    public void getPicVideoMul(boolean isTakeCamera, Activity activity, int size, boolean showCamera
            , boolean showVideo) {
        if (isTakeCamera) {
            takePic(activity);
        } else {
            //进入三方图片选择
            Intent intent1 = new Intent(activity, PicSelectActivity.class);
            intent1.putExtra(PicSelectActivity.CODE_LIMIT, size);
            intent1.putExtra(PicSelectActivity.CODE_NEEDCAMERA, showCamera);
            intent1.putExtra(PicSelectActivity.CODE_SHOWVIDEO, showVideo);
            activity.startActivityForResult(intent1, PicSelectActivity.CODE_REQUEST);
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
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Intent intent1 = new Intent(activity, VideoRecordActivity.class);
        intent1.putExtra(VideoRecordActivity.CODE_RECODE_SECOND, second);
        activity.startActivityForResult(intent1, VideoRecordActivity.CODE_REQUEST);
        return true;
    }

    //调起相机拍照
    public void takePic(Activity activity) {
        try {
            WeakReference<Activity> weakAct = new WeakReference<Activity>(activity);
            Intent intent = new Intent();
            takePicPath = Environment.getExternalStorageDirectory() + "/pic/" + System.currentTimeMillis() + ".jpg";
            File file = new File(takePicPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                //7.0以上的拍照
                mCurUrl = FileProvider.getUriForFile(
                        PicSelConfig.getInstance().getContext(),
                        PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurUrl);//将拍取的照片保存到指定URI
            } else {
                //7.0以下的拍照
                mCurUrl = Uri.fromFile(file);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurUrl);//将拍取的照片保存到指定URI
            }
            //进入拍照页
            weakAct.get().startActivityForResult(intent, TAKEPIC_RESULT);
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
            corpPicPath = Environment.getExternalStorageDirectory() + "/pic/crop/" + System.currentTimeMillis() + ".jpg";
            File file = new File(corpPicPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            //隐式intent
            Intent intent = new Intent("com.android.camera.action.CROP");
            Uri inputUrl = null;
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                inputUrl = FileProvider.getUriForFile(
                        PicSelConfig.getInstance().getContext(),
                        PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", new File(filePath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                inputUrl = Uri.fromFile(new File(filePath));
            }
            // 设置剪裁数据 150*150
            intent.setDataAndType(inputUrl, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("return-data", false);
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
            // 上面设为false的时候将MediaStore.EXTRA_OUTPUT关联一个Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            weakAct.get().startActivityForResult(intent, CROPPIC_REQUEST);
        } catch (Exception e) {
            Log.d(TAG, "剪裁图片异常： " + e);
        }
    }


    /**
     * 必须重写
     * activity结果返回
     */
    public void ActivityForResult(int requestCode, int resultCode, Intent data, PicCallbackListener listener) {
        if (resultCode == RESULT_OK && requestCode == TAKEPIC_RESULT) {
            try {
                Log.d(TAG, "添加图片返回: " + takePicPath);
                if (listener != null) {
                    listener.cameraResult(takePicPath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PicSelectActivity.CODE_REQUEST && resultCode == PicSelectActivity.CODE_RESULT) {
            //添加图片返回
            try {
                ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(PicSelectActivity.CODE_SELECT);
                if (images != null && images.size() > 0) {
                    if (listener != null) {
                        listener.selectResult(images);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
            }
        }
        if (requestCode == CROPPIC_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "剪裁图片路径返回: " + corpPicPath);
            try {
                if (listener != null) {
                    listener.cropResult(corpPicPath);
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
            }
        }
        if (requestCode == VideoRecordActivity.CODE_REQUEST && resultCode == VideoRecordActivity.CODE_RESULT) {
            Log.d(TAG, "录制视频返回");
            try {
                String path = data.getStringExtra(VideoRecordActivity.CODE_RECODE_PATH);
                if (listener != null) {
                    listener.recordVideoPath(path);
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
            }
        }

    }

    //浏览图片
    public void browsePic(List<String> picList, Activity activity, int pos) {
        Intent intent = new Intent(activity, PicBrowserActivity.class);
        intent.putExtra(PicBrowserActivity.CODE_BROWSERLIST, (Serializable) picList);
        intent.putExtra(PicBrowserActivity.CODE_BROWSERPOS, pos);
        activity.startActivity(intent);
    }

    //播放本地视频
    public void playLocalVideo(String path, Activity activity) {
        try {
            WeakReference<Activity> weakAct = new WeakReference<Activity>(activity);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(path);
            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= 24) {
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
}
