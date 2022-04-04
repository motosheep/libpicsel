package com.north.light.picsel;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.north.light.libpicselect.PicCallbackListener;
import com.north.light.libpicselect.PicSelMain;
import com.north.light.libpicselect.model.PicSelConfig;

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
                String path = "/storage/emulated/0/Android/data/com.north.light.picsel/files/lib_pic_sel/camera/1649036977293.jpg";
                String path2 = "/storage/emulated/0/DCIM/Camera/VID_20220404_095827.mp4";
//                PicSelMain.getInstance().getPic(MainActivity.this, true, 3,
//                        true, true, true, true);
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
        //初始化
        PicSelMain.getInstance().init(this.getApplicationContext(), new PicSelConfig.BindImageViewListener() {
            @Override
            public void BindImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }

            @Override
            public void BindSmallImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }
        });
        //拍摄回调
        PicSelMain.getInstance().setPicCallBackListener(new PicCallbackListener() {
            @Override
            public void cameraResult(String path) {

            }

            @Override
            public void selectResult(ArrayList<String> result) {

            }

            @Override
            public void cropResult(String path) {

            }

            @Override
            public void recordVideoPath(String path) {

            }
        });
    }
}
