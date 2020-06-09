package com.north.light.picsel;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
                PicSelMain.getIntance().getPicMul(false, MainActivity.this, 5, true);
            }
        });


        //图片加载库
        PicSelConfig.getInstance().setLoaderManager(this.getApplicationContext(), new PicSelConfig.BindImageViewListener() {
            @Override
            public void BindImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }

            @Override
            public void BindSmallImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }
        });

        //test---------------------------------------------------------------------------------
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //获取图片的名称
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            if (TextUtils.isEmpty(name)) {
                continue;
            }
            //日期
            int date = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED));
            //获取图片的生成日期
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d(TAG, "数据:data1 " + date + "  data2: " + (new String(data, 0, data.length - 1)) + "  name:" + name);

        }
        //test---------------------------------------------------------------------------------


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PicSelMain.getIntance().ActivityForResult(requestCode, resultCode, data, new PicSelMain.PicCallbackListener() {
            @Override
            public void cameraResult(String path) {
                Glide.with(MainActivity.this).load(path).into(img);
            }

            @Override
            public void selectResult(ArrayList<String> result) {
                Glide.with(MainActivity.this).load(result.get(0)).into(img);
            }

            @Override
            public void cropResult(String path) {
                Glide.with(MainActivity.this).load(path).into(img);
            }
        });
    }
}
