package com.north.light.libpicselect.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.bean.LibPicSelMidInfo;
import com.north.light.libpicselect.callback.LibPicSelMediaInfo;
import com.north.light.libpicselect.constant.LibPicIntentCode;
import com.north.light.libpicselect.constant.LibPicConstant;
import com.north.light.libpicselect.databus.LibPicDataBusManager;
import com.north.light.libpicselect.model.LibPicSelConfig;
import com.north.light.libpicselect.utils.LibPicFileUtils;
import com.north.light.libpicselect.utils.LibPicHandlerManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 图片选择中间件页面--所有onActivityResult回调都在该页面进行
 */
public class LibPicSelMidActivity extends LibPicBaseActivity {
    //系统图片拍照路径
    private String takePicPath;
    //系统图片剪裁路径
    private String cropPicPath;
    //自定义图片剪裁原图路径
    private String corpCusPicOrgPath;
    //自定义图片剪裁目标路径
    private String corpCusPicTarPath;


    private static final String TAG = LibPicSelMidActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pic_activity_pic_sel_mid);
        initData();
    }

    /**
     * 获取传值过来的数据
     */
    private void initData() {
        Serializable passSerInfo = getIntent().getSerializableExtra(LibPicIntentCode.PIC_SEL_MID_PARAMS);
        if (passSerInfo == null || !(passSerInfo instanceof LibPicSelMidInfo)) {
            Toast.makeText(this.getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //初始化数据
        LibPicSelMidInfo midInfo = (LibPicSelMidInfo) passSerInfo;
        //封装intent，跳转activity------------------------------------------------------------
        int type = midInfo.getREQ_TYPE();
        Intent resultIntent;
        switch (type) {
            case LibPicIntentCode.PIC_SEL_REQ:
                resultIntent = new Intent(this, LibPicSelectActivity.class);
                break;
            case LibPicIntentCode.VIDEO_RECORD_REQ:
                resultIntent = new Intent(this, LibPicVideoRecordActivity.class);
                break;
            case LibPicIntentCode.BROWSER_CODE_REQUEST:
                resultIntent = new Intent(this, LibPicBrowserActivity.class);
                break;
            case LibPicIntentCode.PIC_CROP_CODE_REQ:
                //自定义剪裁图片---------------------------------------------------------------------
                resultIntent = new Intent(this, LibPicClipActivity.class);
                corpCusPicOrgPath = midInfo.getCusCropPicSourcePath();
                corpCusPicTarPath = midInfo.getCusCropPicTargetPath();
                if (TextUtils.isEmpty(corpCusPicOrgPath) || TextUtils.isEmpty(corpCusPicTarPath)) {
                    Toast.makeText(this.getApplicationContext(), "剪裁数据错误", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                break;
            case LibPicIntentCode.PIC_MAIN_CROP_PIC_REQUEST:
                //系统剪裁图片--特别处理---------------------------------------------------------------
                resultIntent = new Intent();
                cropPicPath = midInfo.getCropPicTargetPath();
                String cropPicSourcePath = midInfo.getCropPicSourcePath();
                if (TextUtils.isEmpty(cropPicPath) || TextUtils.isEmpty(cropPicSourcePath)) {
                    Toast.makeText(this.getApplicationContext(), "剪裁数据错误", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                File cropPicFile = new File(cropPicPath);
                if (!cropPicFile.getParentFile().exists()) {
                    cropPicFile.getParentFile().mkdirs();
                }
                Uri inputUrl;
                if (Build.VERSION.SDK_INT >= 24) {
                    inputUrl = FileProvider.getUriForFile(
                            LibPicSelConfig.getInstance().getContext(),
                            LibPicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", new File(cropPicSourcePath));
                } else {
                    inputUrl = Uri.fromFile(new File(cropPicSourcePath));
                }
                resultIntent.setDataAndType(inputUrl, "image/*");
                resultIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropPicFile));
                break;
            case LibPicIntentCode.PIC_MAIN_TAKE_PIC_REQUEST:
                //系统相机拍摄--特别处理--------------------------------------------------------
                resultIntent = new Intent();
                //设置拍摄目录
                takePicPath = midInfo.getTakePicTargetPath();
                if (TextUtils.isEmpty(takePicPath)) {
                    Toast.makeText(this.getApplicationContext(), "拍摄数据错误", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                File file = new File(takePicPath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                Uri mCurUrl;
                if (Build.VERSION.SDK_INT >= 24) {
                    //7.0以上的拍照
                    mCurUrl = FileProvider.getUriForFile(
                            LibPicSelConfig.getInstance().getContext(),
                            LibPicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", file);
                } else {
                    //7.0以下的拍照
                    mCurUrl = Uri.fromFile(file);
                }
                resultIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurUrl);
                break;
            case LibPicIntentCode.CAMERAX_TAKE_PIC_REQ:
                resultIntent = new Intent(this, LibPicTakeCameraCusActivity.class);
                break;
            case LibPicIntentCode.CUS_VIDEO_PLAY_REQ:
                resultIntent = new Intent(this, LibPicPlayVideoActivity.class);
                break;
            default:
                Toast.makeText(this.getApplicationContext(), "不支持类型", Toast.LENGTH_SHORT).show();
                finish();
                return;
        }
        Map<String, Object> mapResult = midInfo.getWrapper();
        Bundle bundle = getBundle(mapResult);
        //设置bundle
        resultIntent.putExtras(bundle);
        //action
        List<String> intentActionList = midInfo.getREQ_ACTION();
        if (intentActionList != null && intentActionList.size() != 0) {
            for (String act : intentActionList) {
                resultIntent.setAction(act);
            }
        }
        //flag
        List<Integer> flagActionList = midInfo.getREQ_FLAG();
        if (flagActionList != null && flagActionList.size() != 0) {
            for (Integer flag : flagActionList) {
                if (flag != null) {
                    resultIntent.addFlags(flag);
                }
            }
        }
        startActivityWithParams(resultIntent, bundle, type);
    }

    private void startActivityWithParams(Intent resultIntent, Bundle bundle, int type) {
        switch (type) {
            case LibPicIntentCode.PIC_SEL_REQ:
                //图片选择
                startActivityForResult(resultIntent, LibPicIntentCode.PIC_SEL_REQ);
                break;
            case LibPicIntentCode.VIDEO_RECORD_REQ:
                //视频录制
                startActivityForResult(resultIntent, LibPicIntentCode.VIDEO_RECORD_REQ);
                break;
            case LibPicIntentCode.BROWSER_CODE_REQUEST:
                //浏览图片
                startActivityForResult(resultIntent, LibPicIntentCode.BROWSER_CODE_REQUEST);
                break;
            case LibPicIntentCode.PIC_CROP_CODE_REQ:
                //自定义图片剪裁
                startActivityForResult(resultIntent, LibPicIntentCode.PIC_CROP_CODE_REQ);
                break;
            case LibPicIntentCode.PIC_MAIN_CROP_PIC_REQUEST:
                //系统剪裁图片
                startActivityForResult(resultIntent, LibPicIntentCode.PIC_MAIN_CROP_PIC_REQUEST);
                break;
            case LibPicIntentCode.PIC_MAIN_TAKE_PIC_REQUEST:
                //系统拍摄图片--版本适配
                startActivityForResult(resultIntent, LibPicIntentCode.PIC_MAIN_TAKE_PIC_REQUEST);
                break;
            case LibPicIntentCode.CAMERAX_TAKE_PIC_REQ:
                //自定义图片拍摄
                startActivityForResult(resultIntent, LibPicIntentCode.CAMERAX_TAKE_PIC_REQ);
                break;
            case LibPicIntentCode.CUS_VIDEO_PLAY_REQ:
                //自定义视频播放页面
                startActivityForResult(resultIntent, LibPicIntentCode.CUS_VIDEO_PLAY_REQ);
                break;
            default:
                finish();
        }
    }

    @Override
    public void finish() {
        //设置返回值
        setResult(LibPicIntentCode.PIC_SEL_MID_RES_CODE);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == LibPicIntentCode.PIC_MAIN_TAKE_PIC_REQUEST) {
            //系统拍摄图片
            try {
                Log.d(TAG, "添加图片返回: " + takePicPath);
                LibPicDataBusManager.getInstance().cameraResult(takePicPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            return;
        }
        if (requestCode == LibPicIntentCode.PIC_SEL_REQ && resultCode == LibPicIntentCode.PIC_SEL_RES) {
            //选择图片
            try {
                final ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(LibPicIntentCode.PIC_SEL_DATA_SELECT);
                if (images != null && images.size() > 0) {
                    //复制图片
                    if (LibPicHandlerManager.getInstance().getCopyHandler() != null) {
                        LibPicHandlerManager.getInstance().getCopyHandler().removeCallbacksAndMessages(null);
                        LibPicHandlerManager.getInstance().getCopyHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<LibPicSelMediaInfo> trainList = new ArrayList<>();
                                Log.d(TAG, "复制图片时间1: " + System.currentTimeMillis());
                                for (String pic : images) {
                                    String newPath = LibPicFileUtils.copyFileUsingFileStreams(pic, LibPicConstant.getInstance().getCopyPath());
                                    LibPicSelMediaInfo cacheInfo = new LibPicSelMediaInfo(pic, newPath);
                                    if (!TextUtils.isEmpty(newPath)) {
                                        trainList.add(trainList.size(), cacheInfo);
                                    }
                                }
                                //add by lzt 20211109 增加情况，假设图片路径复制失败，则直接获取原图路径，兜底
                                if (trainList != null && trainList.size() == 0) {
                                    if (images != null && images.size() != 0) {
                                        Log.d(TAG, "复制图片失败，直接获取原图路径");
                                        for (String pic : images) {
                                            LibPicSelMediaInfo cacheInfo = new LibPicSelMediaInfo(pic, pic);
                                            trainList.add(cacheInfo);
                                        }
                                    }
                                }
                                Log.d(TAG, "复制图片时间2: " + System.currentTimeMillis());
                                //主线程--notify
                                LibPicHandlerManager.getInstance().getUIHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        LibPicDataBusManager.getInstance().selectResult(trainList);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                } else {
                    finish();
                    return;
                }
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
            }
        }
        if (requestCode == LibPicIntentCode.PIC_MAIN_CROP_PIC_REQUEST && resultCode == RESULT_OK) {
            //系统剪裁图片
            Log.d(TAG, "剪裁图片路径返回: " + cropPicPath);
            LibPicDataBusManager.getInstance().cropResult(cropPicPath);
            finish();
            return;
        }
        if (requestCode == LibPicIntentCode.PIC_CROP_CODE_REQ && resultCode == LibPicIntentCode.PIC_CROP_CODE_RES) {
            //自定义剪裁图片返回
            try {
                String cropPath = data.getStringExtra(LibPicIntentCode.PIC_CROP_DATA_CLIP_DATA);
                Log.d(TAG, "自定义剪裁图片原图: " + corpCusPicOrgPath);
                Log.d(TAG, "自定义剪裁图片目标: " + corpCusPicTarPath);
                Log.d(TAG, "自定义剪裁图片结果: " + cropPath);
                LibPicDataBusManager.getInstance().cropResult(cropPath);
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
                //直接返回原图好了--防止出错后，不能进行后续的步骤
                LibPicDataBusManager.getInstance().cropResult(corpCusPicOrgPath);
            }
            finish();
            return;
        }
        if (requestCode == LibPicIntentCode.VIDEO_RECORD_REQ && resultCode == LibPicIntentCode.VIDEO_RECORD_RES) {
            //系统录制视频
            Log.d(TAG, "录制视频返回");
            try {
                String path = data.getStringExtra(LibPicIntentCode.VIDEO_RECODE_PATH);
                LibPicDataBusManager.getInstance().recordVideoPath(path);
            } catch (Exception e) {
                Log.d(TAG, "录制视频返回e: " + e);
            }
            finish();
            return;
        }
        if (requestCode == LibPicIntentCode.CAMERAX_TAKE_PIC_REQ && resultCode == LibPicIntentCode.CAMERAX_TAKE_PIC_RES) {
            //自定义图片拍摄
            Log.d(TAG, "自定义图片拍摄返回");
            try {
                String path = data.getStringExtra(LibPicIntentCode.PATH_STRING_CAMERAX_TAKE_RES);
                if (!TextUtils.isEmpty(path)) {
                    LibPicDataBusManager.getInstance().cameraResult(path);
                }
            } catch (Exception e) {
                Log.d(TAG, "自定义图片拍摄返回e: " + e);
            }
            finish();
            return;
        }
        if (requestCode == LibPicIntentCode.CUS_VIDEO_PLAY_REQ && resultCode == LibPicIntentCode.CUS_VIDEO_PLAY_RES) {
            //图库内部视频播放页面回调
            finish();
            return;
        }
        finish();
    }
}