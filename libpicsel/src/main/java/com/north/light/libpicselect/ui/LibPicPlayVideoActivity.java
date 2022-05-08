package com.north.light.libpicselect.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.constant.LibPicIntentCode;


/**
 * 播放视频activity
 */
public class LibPicPlayVideoActivity extends LibPicBaseActivity {

    //返回按键
    private ImageView mBack;
    private LinearLayout mPlayerRoot;
    private VideoView mPlayView;

    //视频路径
    private String mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pic_activity_pic_play_video);
        mVideoPath = getIntent().getStringExtra(LibPicIntentCode.CUS_VIDEO_PLAY_PATH);
        if (TextUtils.isEmpty(mVideoPath)) {
            Toast.makeText(this.getApplicationContext(), "播放视频的数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initView();
        initEvent();
    }

    private void initView() {
        mBack = findViewById(R.id.activity_pic_play_video_back);
        mPlayerRoot = findViewById(R.id.activity_pic_play_video_content);

        initVideoView();
    }

    @Override
    public void finish() {
        setResult(LibPicIntentCode.CUS_VIDEO_PLAY_RES);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        releaseVideoView();
        super.onDestroy();
    }

    private void initEvent() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化video view
     */
    private void initVideoView() {
        //动态把video view添加到布局里面
        try {
            mPlayView = new VideoView(getApplicationContext());
            mPlayerRoot.addView(mPlayView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayView.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            mPlayView.setLayoutParams(params);
            MediaController mediaController = new MediaController(this);
            mPlayView.setMediaController(mediaController);
            mPlayView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });
            mPlayView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
            mPlayView.setVideoPath(mVideoPath);
            mPlayView.start();
        } catch (Exception e) {

        }
    }

    /**
     * 释放video view
     */
    private void releaseVideoView() {
        try {
            mPlayView.stopPlayback();
            mPlayView.suspend();
            mPlayView.setOnErrorListener(null);
            mPlayView.setOnPreparedListener(null);
            mPlayView.setOnCompletionListener(null);
            mPlayView.setMediaController(null);
            mPlayView = null;
            mPlayerRoot.removeAllViews();
        } catch (Exception e) {

        }
    }


}