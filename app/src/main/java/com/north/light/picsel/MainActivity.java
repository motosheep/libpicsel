package com.north.light.picsel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.north.light.libpicselect.PicActionListener;
import com.north.light.libpicselect.PicCallbackListener;
import com.north.light.libpicselect.PicSelMain;
import com.north.light.libpicselect.callback.LibPicSelMediaInfo;
import com.north.light.libpicselect.model.LibPicSelConfig;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.activity_main_img);
        findViewById(R.id.activity_main_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加载图片
//                String path = "/storage/emulated/0/Android/data/com.north.light.picsel/files/lib_pic_sel/camera/1649036977293.jpg";
//                String path2 = "/storage/emulated/0/DCIM/Camera/VID_20220404_095827.mp4";
                PicSelMain.getInstance().getPic(MainActivity.this, true, 3,
                        true, true, false, false);
                //剪裁/storage/emulated/0/Android/data/com.north.light.picsel/files/lib_pic_sel/camera/1649036977293.jpg
//                PicSelMain.getInstance().cropPic(MainActivity.this,
//                        path,false,1,1);
//                PicSelMain.getInstance().recordVideo(MainActivity.this,20);
//                List<String> brList = new ArrayList<>();
//                brList.add(path);
//                brList.add(path2);
//                PicSelMain.getInstance().browsePic(brList, MainActivity.this, 0, 2);

            }
        });
//        //若调用此方法，记得及时释放，监听实例数量会作为是否交由外部处理的依据
//        PicSelMain.getInstance().setActionListener(new PicActionListener() {
//            @Override
//            public void cusVideoPlay(Activity activity, String path) {
//
//            }
//
//            @Override
//            public void cusCameraTake(Activity activity) {
//
//            }
//        });
        //初始化
        PicSelMain.getInstance().init(this.getApplicationContext(), new LibPicSelConfig.BindImageViewListener() {
            @Override
            public void BindImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }

            @Override
            public void BindSmallImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }

            @Override
            public void BindResume() {
                Glide.with(MainActivity.this.getApplicationContext()).resumeRequests();
            }

            @Override
            public void BindPause() {
                Glide.with(MainActivity.this.getApplicationContext()).pauseRequests();
            }
        });
        //拍摄回调
        PicSelMain.getInstance().setPicCallBackListener(new PicCallbackListener() {
            @Override
            public void cameraResult(String path) {

            }

            @Override
            public void selectResult(ArrayList<LibPicSelMediaInfo> result) {

            }

            @Override
            public void cropResult(String path) {

            }

            @Override
            public void recordVideoPath(String path) {

            }

            @Override
            public void error(String message) {

            }
        });
    }
}
