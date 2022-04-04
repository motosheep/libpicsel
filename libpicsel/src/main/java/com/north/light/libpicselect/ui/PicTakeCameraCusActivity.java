package com.north.light.libpicselect.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.constant.IntentCode;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 自定义相机拍摄页面
 * 使用camera x 进行拍照
 */
public class PicTakeCameraCusActivity extends PicBaseActivity {
    //线程池
    private ExecutorService cameraExecutor;

    //相机对象
    private PreviewView mCameraPreview;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private Preview preview;

    //相机控制类
    private CameraControl cameraControl;
    private CameraInfo cameraInfo;

    //是否对焦中的标识
    private boolean mFocus = true;

    //当前:true前置 false后置
    private boolean mCurrentCameraeFace = false;

    //初始化相机成功
    private boolean mInitCamera = false;

    //拍照控件
    private ImageView mTakePhotoIV;
    private ImageView mCancelIV;
    private ImageView mChangeCameraIV;

    //拍摄图片的路径
    private String mTakePicPath;
    //摄像头初始化方向
    private int mCameraOrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pic_activity_pic_take_camera_cus);
        mTakePicPath = getIntent().getStringExtra(IntentCode.PATH_STRING_CAMERAX_TAKE_PIC);
        mCameraOrg = getIntent().getIntExtra(IntentCode.PATH_STRING_CAMERAX_TAKE_ORG, 0);
        if (TextUtils.isEmpty(mTakePicPath)) {
            Toast.makeText(this.getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initView();
        initEvent();
        startCamera();
    }

    private void initView() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        mCameraPreview = findViewById(R.id.lib_pic_camerax_preview);
        mTakePhotoIV = findViewById(R.id.lib_pic_camerax_preview_take_photo);
        mChangeCameraIV = findViewById(R.id.lib_pic_camerax_preview_change_camera);
        mCancelIV = findViewById(R.id.lib_pic_camerax_preview_cancel);
    }

    private void initEvent() {
        mChangeCameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换摄像头
                changeCamera();
            }
        });
        mCancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束当前页面
                finish();
            }
        });
        mTakePhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照
                takePhoto();
            }
        });
        mCameraPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        float width = (v.getRight() - v.getLeft());
                        float height = (v.getBottom() - v.getTop());
                        float x = event.getX();
                        float y = event.getY();
                        focus(width, height, x, y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 启动相机
     */
    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获取camera provider
                    cameraProvider = cameraProviderFuture.get();
                    // 初始化preview
                    preview = new Preview.Builder()
                            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                            .build();
                    preview.setSurfaceProvider(mCameraPreview.getSurfaceProvider());
                    // image cap
                    imageCapture = new ImageCapture.Builder()
                            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                            .build();
                    OrientationEventListener orientationEventListener = new OrientationEventListener(PicTakeCameraCusActivity.this) {
                        @Override
                        public void onOrientationChanged(int orientation) {
                            int rotation = 0;
                            if (orientation >= 45 && orientation <= 134) {
                                rotation = Surface.ROTATION_270;
                            } else if (orientation >= 135 && orientation <= 224) {
                                rotation = Surface.ROTATION_180;
                            } else if (orientation >= 225 && orientation <= 314) {
                                rotation = Surface.ROTATION_90;
                            } else {
                                rotation = Surface.ROTATION_0;
                            }
                            imageCapture.setTargetRotation(rotation);
                        }
                    };
                    orientationEventListener.enable();
                    // 使用后摄像头
                    CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                    if (mCameraOrg == 1) {
                        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                    }
                    //在重新绑定之前取消绑定用例
                    cameraProvider.unbindAll();
                    //将用例绑定到摄像机
                    Camera camera = cameraProvider.bindToLifecycle(
                            PicTakeCameraCusActivity.this, cameraSelector,
                            preview, imageCapture);
                    //用来聚焦、手势、闪光灯、手电等操作
                    cameraControl = camera.getCameraControl();
                    cameraInfo = camera.getCameraInfo();
                    //允许对焦
                    mFocus = false;
                    mInitCamera = true;
                } catch (Exception exc) {

                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (!mInitCamera) {
            return;
        }
        mTakePhotoIV.setEnabled(false);
        try {
            // 保证相机可用
            ImageCapture imageCap = imageCapture;
            // 保存路径
            final File photoFile = new File(mTakePicPath);
            // 创建包含文件和metadata的输出选项对象
            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
            // 设置图像捕获监听器，在拍照后触发
            imageCap.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(this),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull @NotNull ImageCapture.OutputFileResults outputFileResults) {
                            mTakePhotoIV.setEnabled(true);
                            Intent intent = new Intent();
                            intent.putExtra(IntentCode.PATH_STRING_CAMERAX_TAKE_RES, mTakePicPath);
                            setResult(IntentCode.CAMERAX_TAKE_PIC_RES, intent);
                            finish();
                        }

                        @Override
                        public void onError(@NonNull @NotNull ImageCaptureException exception) {
                            Uri savedUri = Uri.fromFile(photoFile);
                            mTakePhotoIV.setEnabled(true);
                            finish();
                        }
                    }
            );
        } catch (Exception e) {
            mTakePhotoIV.setEnabled(true);
        }
    }

    /**
     * 对焦
     */
    private void focus(float width, float height, float xPos, float yPos) {
        if (!mInitCamera) {
            return;
        }
        try {
            if (mFocus) {
                return;
            }
            mFocus = true;
            SurfaceOrientedMeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(width, height);
            MeteringPoint point = factory.createPoint(xPos, yPos);
            FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(2, TimeUnit.SECONDS)
                    .build();
            final ListenableFuture<FocusMeteringResult> future = cameraControl.startFocusAndMetering(action);
            future.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        mFocus = false;
                        // process the result
                    } catch (Exception e) {
                    }
                }
            }, cameraExecutor);
        } catch (Exception e) {

        }
    }

    /**
     * 开关手电筒
     **/
    private void toggleTorch() {
        if (!mInitCamera) {
            return;
        }
        try {
            if (cameraInfo != null && cameraInfo.getTorchState().getValue() != null) {
                if (cameraInfo.getTorchState().getValue() == TorchState.ON) {
                    cameraControl.enableTorch(false);
                } else {
                    cameraControl.enableTorch(true);
                }
            }

        } catch (Exception e) {
        }
    }

    /**
     * 切换摄像头
     */
    private void changeCamera() {
        if (!mInitCamera) {
            return;
        }
        try {
            mCurrentCameraeFace = !mCurrentCameraeFace;
            cameraProvider.unbindAll();
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing((mCurrentCameraeFace) ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                    .build();
            Camera camera = cameraProvider.bindToLifecycle(PicTakeCameraCusActivity.this, cameraSelector,
                    preview, imageCapture);
            //用来聚焦、手势、闪光灯、手电等操作
            cameraControl = camera.getCameraControl();
            cameraInfo = camera.getCameraInfo();
        } catch (Exception e) {
        }
    }
}