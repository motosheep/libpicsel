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
import com.north.light.libpicselect.bean.PicSelMidInfo;
import com.north.light.libpicselect.constant.IntentCode;
import com.north.light.libpicselect.constant.PicConstant;
import com.north.light.libpicselect.databus.DataBusManager;
import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.utils.FileUtils;
import com.north.light.libpicselect.utils.HandlerManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 图片选择中间件页面--所有onActivityResult回调都在该页面进行
 */
public class PicSelMidActivity extends PicBaseActivity {
    //系统图片拍照路径
    private String takePicPath;
    //系统图片剪裁路径
    private String cropPicPath;
    //自定义图片剪裁原图路径
    private String corpCusPicOrgPath;
    //自定义图片剪裁目标路径
    private String corpCusPicTarPath;
    //选择的图片中转list
    private static volatile ArrayList<String> finalList = new ArrayList<>();

    private static final String TAG = PicSelMidActivity.class.getSimpleName();

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
        Serializable passSerInfo = getIntent().getSerializableExtra(IntentCode.PIC_SEL_MID_PARAMS);
        if (passSerInfo == null || !(passSerInfo instanceof PicSelMidInfo)) {
            Toast.makeText(this.getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //初始化数据
        PicSelMidInfo midInfo = (PicSelMidInfo) passSerInfo;
        //封装intent，跳转activity------------------------------------------------------------
        int type = midInfo.getREQ_TYPE();
        Intent resultIntent;
        switch (type) {
            case IntentCode.PIC_SEL_REQ:
                resultIntent = new Intent(this, PicSelectActivity.class);
                break;
            case IntentCode.VIDEO_RECORD_REQ:
                resultIntent = new Intent(this, PicVideoRecordActivity.class);
                break;
            case IntentCode.BROWSER_CODE_REQUEST:
                resultIntent = new Intent(this, PicBrowserActivity.class);
                break;
            case IntentCode.PIC_CROP_CODE_REQ:
                //自定义剪裁图片---------------------------------------------------------------------
                resultIntent = new Intent(this, PicClipActivity.class);
                corpCusPicOrgPath = midInfo.getCusCropPicSourcePath();
                corpCusPicTarPath = midInfo.getCusCropPicTargetPath();
                if (TextUtils.isEmpty(corpCusPicOrgPath) || TextUtils.isEmpty(corpCusPicTarPath)) {
                    Toast.makeText(this.getApplicationContext(), "剪裁数据错误", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                break;
            case IntentCode.PIC_MAIN_CROP_PIC_REQUEST:
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
                            PicSelConfig.getInstance().getContext(),
                            PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", new File(cropPicSourcePath));
                } else {
                    inputUrl = Uri.fromFile(new File(cropPicSourcePath));
                }
                resultIntent.setDataAndType(inputUrl, "image/*");
                resultIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropPicFile));
                break;
            case IntentCode.PIC_MAIN_TAKE_PIC_REQUEST:
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
                            PicSelConfig.getInstance().getContext(),
                            PicSelConfig.getInstance().getContext().getPackageName() + ".fileProvider", file);
                } else {
                    //7.0以下的拍照
                    mCurUrl = Uri.fromFile(file);
                }
                resultIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurUrl);
                break;
            case IntentCode.CAMERAX_TAKE_PIC_REQ:
                resultIntent = new Intent(this, PicTakeCameraCusActivity.class);
                break;
            case IntentCode.CUS_VIDEO_PLAY_REQ:
                resultIntent = new Intent(this, PicPlayVideoActivity.class);
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
            case IntentCode.PIC_SEL_REQ:
                //图片选择
                startActivityForResult(resultIntent, IntentCode.PIC_SEL_REQ);
                break;
            case IntentCode.VIDEO_RECORD_REQ:
                //视频录制
                startActivityForResult(resultIntent, IntentCode.VIDEO_RECORD_REQ);
                break;
            case IntentCode.BROWSER_CODE_REQUEST:
                //浏览图片
                startActivityForResult(resultIntent, IntentCode.BROWSER_CODE_REQUEST);
                break;
            case IntentCode.PIC_CROP_CODE_REQ:
                //自定义图片剪裁
                startActivityForResult(resultIntent, IntentCode.PIC_CROP_CODE_REQ);
                break;
            case IntentCode.PIC_MAIN_CROP_PIC_REQUEST:
                //系统剪裁图片
                startActivityForResult(resultIntent, IntentCode.PIC_MAIN_CROP_PIC_REQUEST);
                break;
            case IntentCode.PIC_MAIN_TAKE_PIC_REQUEST:
                //系统拍摄图片--版本适配
                startActivityForResult(resultIntent, IntentCode.PIC_MAIN_TAKE_PIC_REQUEST);
                break;
            case IntentCode.CAMERAX_TAKE_PIC_REQ:
                //自定义图片拍摄
                startActivityForResult(resultIntent, IntentCode.CAMERAX_TAKE_PIC_REQ);
                break;
            case IntentCode.CUS_VIDEO_PLAY_REQ:
                //自定义视频播放页面
                startActivityForResult(resultIntent, IntentCode.CUS_VIDEO_PLAY_REQ);
                break;
            default:
                finish();
        }
    }

    @Override
    public void finish() {
        //设置返回值
        setResult(IntentCode.PIC_SEL_MID_RES_CODE);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IntentCode.PIC_MAIN_TAKE_PIC_REQUEST) {
            //系统拍摄图片
            try {
                Log.d(TAG, "添加图片返回: " + takePicPath);
                DataBusManager.getInstance().cameraResult(takePicPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
            return;
        }
        if (requestCode == IntentCode.PIC_SEL_REQ && resultCode == IntentCode.PIC_SEL_RES) {
            //选择图片
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
                                        finalList.add(finalList.size(), newPath);
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
                                HandlerManager.getInstance().getUIHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        DataBusManager.getInstance().selectResult(finalList);
                                        finish();
                                        return;
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
        if (requestCode == IntentCode.PIC_MAIN_CROP_PIC_REQUEST && resultCode == RESULT_OK) {
            //系统剪裁图片
            Log.d(TAG, "剪裁图片路径返回: " + cropPicPath);
            DataBusManager.getInstance().cropResult(cropPicPath);
            finish();
            return;
        }
        if (requestCode == IntentCode.PIC_CROP_CODE_REQ && resultCode == IntentCode.PIC_CROP_CODE_RES) {
            //自定义剪裁图片返回
            try {
                String cropPath = data.getStringExtra(IntentCode.PIC_CROP_DATA_CLIP_DATA);
                Log.d(TAG, "自定义剪裁图片原图: " + corpCusPicOrgPath);
                Log.d(TAG, "自定义剪裁图片目标: " + corpCusPicTarPath);
                Log.d(TAG, "自定义剪裁图片结果: " + cropPath);
                DataBusManager.getInstance().cropResult(cropPath);
            } catch (Exception e) {
                Log.d(TAG, "添加图片返回e: " + e);
                //直接返回原图好了--防止出错后，不能进行后续的步骤
                DataBusManager.getInstance().cropResult(corpCusPicOrgPath);
            }
            finish();
            return;
        }
        if (requestCode == IntentCode.VIDEO_RECORD_REQ && resultCode == IntentCode.VIDEO_RECORD_RES) {
            //系统录制视频
            Log.d(TAG, "录制视频返回");
            try {
                String path = data.getStringExtra(IntentCode.VIDEO_RECODE_PATH);
                DataBusManager.getInstance().recordVideoPath(path);
            } catch (Exception e) {
                Log.d(TAG, "录制视频返回e: " + e);
            }
            finish();
            return;
        }
        if (requestCode == IntentCode.CAMERAX_TAKE_PIC_REQ && resultCode == IntentCode.CAMERAX_TAKE_PIC_RES) {
            //自定义图片拍摄
            Log.d(TAG, "自定义图片拍摄返回");
            try {
                String path = data.getStringExtra(IntentCode.PATH_STRING_CAMERAX_TAKE_RES);
                if (!TextUtils.isEmpty(path)) {
                    DataBusManager.getInstance().cameraResult(path);
                }
            } catch (Exception e) {
                Log.d(TAG, "自定义图片拍摄返回e: " + e);
            }
            finish();
            return;
        }
        if (requestCode == IntentCode.CUS_VIDEO_PLAY_REQ && resultCode == IntentCode.CUS_VIDEO_PLAY_RES) {
            //图库内部视频播放页面回调
            finish();
            return;
        }
        finish();
    }
}