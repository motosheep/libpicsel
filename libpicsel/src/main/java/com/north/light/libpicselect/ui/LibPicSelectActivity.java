package com.north.light.libpicselect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.north.light.libpicselect.PicSelMain;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.adapter.LibPicSelAdapter;
import com.north.light.libpicselect.bean.LibPicDirecotryIntentInfo;
import com.north.light.libpicselect.bean.LibPicInfo;
import com.north.light.libpicselect.bean.LibPicSelIntentInfo;
import com.north.light.libpicselect.constant.LibPicIntentCode;
import com.north.light.libpicselect.databus.LibPicDataBusManager;
import com.north.light.libpicselect.model.LibPicSelConfig;
import com.north.light.libpicselect.model.LibPicSelectApi;
import com.north.light.libpicselect.model.LibPicSelectManager;
import com.north.light.libpicselect.utils.LibPicCloneUtils;
import com.north.light.libpicselect.widget.recyclerview.LibPicRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 需要注意由于对象引用，导致数据错乱问题
 * change by lzt 20200823 增加视频显示逻辑
 * change by lzt 20220404 增加自定义视频播放intent参数
 */
public class LibPicSelectActivity extends LibPicBaseActivity {

    private int mLimit = 9;

    private final int REQUEST_DIR_CODE = 0x1001;//目录页面request

    private LibPicRecyclerView mRecyContent;
    private LibPicSelAdapter mAdapter;

    private ImageView mBack;//返回
    private TextView mTitle;//标题
    private TextView mConfirm;//确定

    private Map<String, List<LibPicInfo>> mFilterData = new HashMap<>();//已经分类好的数据

    private boolean isShowCamera;//是否显示相机图标
    private boolean isShowGif;//显示gif标识
    private boolean isShowVideo;//显示视频标识
    private boolean isCusCamera;//自定义相机标识
    private boolean isCusVideoPlayUI;//自定义视频播放界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lib_pic_activity_pic_select);
        initView();
        initData();
    }

    private void initView() {
        mLimit = getIntent().getIntExtra(LibPicIntentCode.PIC_SEL_DATA_LIMIT, 9);//默认为九个
        isShowCamera = getIntent().getBooleanExtra(LibPicIntentCode.PIC_SEL_DATA_NEED_CAMERA, false);//相机显示标识
        isShowGif = getIntent().getBooleanExtra(LibPicIntentCode.PIC_SEL_DATA_SHOW_GIF, false);//gif显示标识
        isShowVideo = getIntent().getBooleanExtra(LibPicIntentCode.PIC_SEL_DATA_SHOW_VIDEO, false);//视频显示标识
        isCusCamera = getIntent().getBooleanExtra(LibPicIntentCode.PIC_SEL_DATA_CUS_CAMERA, false);//自定义相机标识
        isCusVideoPlayUI = getIntent().getBooleanExtra(LibPicIntentCode.PIC_SEL_DATA_CUS_VIDEO_PLAYER, false);//自定义视频播放界面
        if (mLimit > 9) mLimit = 9;
        mRecyContent = findViewById(R.id.lib_pic_activity_pic_sel_recy);
        mBack = findViewById(R.id.lib_pic_activity_pic_sel_back);
        mTitle = findViewById(R.id.lib_pic_activity_pic_sel_title);
        mConfirm = findViewById(R.id.lib_pic_activity_pic_sel_confirm);
        mRecyContent.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new LibPicSelAdapter(this, isShowCamera);
        mAdapter.setSelectLimit(mLimit);
        mRecyContent.setAdapter(mAdapter);
        //设置初始值
        updateSelCount();
    }

    private void initData() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //确定回调
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    Intent result = new Intent();
                    result.putExtra(LibPicIntentCode.PIC_SEL_DATA_SELECT, (Serializable) mAdapter.getSelectInfo());
                    setResult(LibPicIntentCode.PIC_SEL_RES, result);
                }
                finish();
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilterData == null || mFilterData.size() == 0) {
                    Toast.makeText(LibPicSelectActivity.this.getApplicationContext(), "数据异常", Toast.LENGTH_SHORT).show();
                } else {
                    //打开列表__数据太大，设置全局变量
                    LibPicDirecotryIntentInfo.setData(mFilterData);
                    LibPicDirectoryActivity.launch(LibPicSelectActivity.this, REQUEST_DIR_CODE);
                }
            }
        });
        mAdapter.setOnBindImageViewListener(new LibPicSelAdapter.BindImageViewListener() {
            @Override
            public void BindImageView(String path, ImageView iv) {
                if (LibPicSelConfig.getInstance().getBindListener() != null) {
                    LibPicSelConfig.getInstance().getBindListener().BindSmallImageView(path, iv);
                }
            }
        });
        mAdapter.setOnClickListener(new LibPicSelAdapter.OnClickListener() {

            //source 1图片 2视频。如果选择图片，则进入多选模式，若选择视频，则进入播放模式
            @Override
            public void click(List<LibPicInfo> data, int pos, int source) {
                if (data != null && data.size() != 0) {
                    List<String> result = new ArrayList<>();
                    List<LibPicInfo> local = LibPicCloneUtils.cloneObjectSer(data);
                    LibPicSelIntentInfo.getInstance().setPicSelList(local);
                    for (LibPicInfo cache : data) {
                        result.add(cache.getPath());
                    }
                    //赋值对象到内存中
                    LibPicSelIntentInfo.getInstance().setPicList(result);
                    LibPicBrowserActivity.launch(LibPicSelectActivity.this, pos, mLimit,isCusVideoPlayUI);
                }
            }

            @Override
            public void check() {
                //check box 事件
                updateSelCount();
            }

            @Override
            public void camera() {
                //拍照
                if (isCusCamera) {
                    //回调，并结束当前页面
                    LibPicDataBusManager.getInstance().takeCameraCus(LibPicSelectActivity.this,0);
                } else {
                    PicSelMain.getInstance().takeCamera(LibPicSelectActivity.this, false, 0);
                }
            }
        });
        LibPicSelectManager.getInstance().setOnResultListener(new LibPicSelectManager.OnResultListener() {
            @Override
            public void Data(final List<LibPicInfo> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            mAdapter.setData(result);
                        }
                    }
                });
            }

            @Override
            public void FilterData(final Map<String, List<LibPicInfo>> filterMap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFilterData = filterMap;
                    }
                });
            }
        });
        LibPicSelectManager.getInstance().init(this, new LibPicSelectApi.InitCallBack() {
            @Override
            public void NoPermission() {
                finish();
            }

            @Override
            public void Success() {
                //初始化成功，则加载数据
                LibPicSelectManager.getInstance().load(isShowGif, isShowVideo);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //选中的目录
        if (requestCode == REQUEST_DIR_CODE && resultCode == RESULT_OK) {
            String directory = data.getStringExtra("path");
            mTitle.setText(!TextUtils.isEmpty(directory) ? directory : "暂无标题");
            if (!TextUtils.isEmpty(directory) && mFilterData.get(directory) != null) {
                mAdapter.setData(mFilterData.get(directory));
            }
            updateSelCount();
        }
        //全屏页面，普通返回
        if (requestCode == LibPicIntentCode.BROWSER_CODE_REQUEST && resultCode == LibPicIntentCode.PIC_SEL_RES) {
            if (mAdapter != null) {
                mAdapter.setData(LibPicSelIntentInfo.getInstance().getPicSelList());
                updateSelCount();
            }
        }
        //全屏页面，确认返回
        if (requestCode == LibPicIntentCode.BROWSER_CODE_REQUEST && resultCode == LibPicIntentCode.BROWSER_CODE_RESULT_CONFIRM) {
            if (mAdapter != null) {
                mAdapter.setData(LibPicSelIntentInfo.getInstance().getPicSelList());
                updateSelCount();
                //设置返回结果
                if (mAdapter != null) {
                    Intent result = new Intent();
                    result.putExtra(LibPicIntentCode.PIC_SEL_DATA_SELECT, (Serializable) mAdapter.getSelectInfo());
                    setResult(LibPicIntentCode.PIC_SEL_RES, result);
                }
                finish();
            }
        }
        //全屏页面，没有确定返回
        if (requestCode == LibPicIntentCode.BROWSER_CODE_REQUEST && resultCode == LibPicIntentCode.BROWSER_CODE_RESULT) {
            if (mAdapter != null) {
                mAdapter.setData(LibPicSelIntentInfo.getInstance().getPicSelList());
                updateSelCount();
            }
        }
        //若是中间页面，直接finish
        if (requestCode == LibPicIntentCode.PIC_SEL_MID_REQ_CODE && resultCode == LibPicIntentCode.PIC_SEL_MID_RES_CODE) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        LibPicSelectManager.getInstance().release();
        mAdapter.reomveBindImageViewListener();
        mAdapter.removeOnClickListener();
        super.onDestroy();
    }

    /**
     * 更新选中个数
     */
    private void updateSelCount() {
        if (mAdapter != null) {
            int selSize = mAdapter.getSelectInfo().size();
            mConfirm.setText(String.format(getResources().getString(R.string.lib_pic_pic_select_activity_checkbox_count),
                    selSize, mLimit));
        }
    }
}
