package com.north.light.libpicselect.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.constant.IntentCode;
import com.north.light.libpicselect.crop.ClipBitmapUtils;
import com.north.light.libpicselect.crop.ClipImageLayout;

import java.io.File;

/**
 * 图片剪切页面
 */
public class PicClipActivity extends Activity {
    private static final String TAG = PicClipActivity.class.getSimpleName();
    //取消，选中按钮
    private Button mCancelBt, mConfirmBt;
    //剪裁路径
    private String tempCropFilePath;
    //剪裁控件
    private ClipImageLayout mClipImageLayout;
    //剪裁工具类
    private ClipBitmapUtils bitmapUtils;
    //bitmap缓存
    private Bitmap bitmap;
    //旋转按钮
    private ImageView mLeftNinety, mRightNinety;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pic_activity_pic_clip);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        tempCropFilePath = intent.getStringExtra(IntentCode.PIC_CROP_DATA_TAR_PATH);
        if (TextUtils.isEmpty(tempCropFilePath)) {
            Toast.makeText(this.getApplicationContext(), "传入图片不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mCancelBt = findViewById(R.id.lib_pic_activity_clip_photo_cancel);
        mConfirmBt = findViewById(R.id.lib_pic_activity_clip_photo_ok);
        mLeftNinety = findViewById(R.id.lib_pic_activity_clip_left_ninety);
        mRightNinety = findViewById(R.id.lib_pic_activity_clip_right_ninety);
        String path = intent.getStringExtra(IntentCode.PIC_CROP_DATA_ORG_PATH);
        int widthRate = intent.getIntExtra(IntentCode.PIC_CROP_PIC_RATE_WIDTH, 1);
        int heightRate = intent.getIntExtra(IntentCode.PIC_CROP_PIC_RATE_HEIGHT, 1);
        bitmapUtils = new ClipBitmapUtils();
        mClipImageLayout = findViewById(R.id.lib_pic_activity_clip_image_layout);
        mClipImageLayout.setProportion(widthRate, heightRate);//直接设置比例
        mClipImageLayout.setImageDrawable(path);
        //图片选择 需要去裁剪的图片路径
        mCancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确认
        boolean exist = new File("/storage/emulated/0/Android/data/com.kktohome.master/cache/kkgj/crop/1638788272738.jpg").exists();
        mConfirmBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConfirmBt.setEnabled(false);
                mCancelBt.setEnabled(false);
                // 剪切图片
                try {
                    bitmap = mClipImageLayout.clip();
                    if (bitmap != null) {
                        // 压缩保存图片
                        bitmapUtils.saveBitmapInSD(tempCropFilePath, bitmap);
                        // sendData();//上传
                        Intent intent = new Intent();
                        intent.putExtra(IntentCode.PIC_CROP_DATA_CLIP_DATA, tempCropFilePath);
                        setResult(IntentCode.PIC_CROP_CODE_RES, intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                mConfirmBt.setEnabled(true);
                mCancelBt.setEnabled(true);
            }
        });
        //左旋转90度
        mLeftNinety.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClipImageLayout != null) {
                    mClipImageLayout.setLeftRotaImage();
                }
            }
        });
        //右旋转90度
        mRightNinety.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClipImageLayout != null) {
                    mClipImageLayout.setRightRotaImage();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        recycle();
        bitmapUtils = null;
        super.onDestroy();
    }

    /**
     * 回收资源
     */
    private void recycle() {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();// 回收bitmap
            bitmap = null;
            System.gc();
        }
    }

}
