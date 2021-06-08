package com.north.light.libpicselect.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.north.light.libpicselect.PicSelMain;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.adapter.PicSelAdapter;
import com.north.light.libpicselect.bean.DirecotryIntentInfo;
import com.north.light.libpicselect.bean.PicInfo;
import com.north.light.libpicselect.bean.PicSelIntentInfo;
import com.north.light.libpicselect.constant.PicConstant;
import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.model.PicSelectApi;
import com.north.light.libpicselect.model.PicSelectManager;
import com.north.light.libpicselect.utils.CloneUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 需要注意由于对象引用，导致数据错乱问题
 * change by lzt 20200823 增加视频显示逻辑
 */
public class PicSelectActivity extends PicBaseActivity {
    public static final int CODE_REQUEST = 0x0001;
    public static final int CODE_RESULT = 0x0002;
    public static final String CODE_LIMIT = "PicSelectActivity_CODE_LIMIT";
    public static final String CODE_NEEDCAMERA = "PicSelectActivity_CODE_NEEDCAMERA";
    public static final String CODE_SHOWGIF = "PicSelectActivity_CODE_SHOWGIF";
    public static final String CODE_SHOWVIDEO = "PicSelectActivity_CODE_SHOWVIDEO";
    public static final String CODE_SELECT = "PicSelectActivity_CODE_SELECT";

    private int mLimit = 9;

    private final int REQUEST_DIR_CODE = 0x1001;//目录页面request

    private RecyclerView mContent;
    private PicSelAdapter mAdapter;

    private ImageView mBack;//返回
    private TextView mTitle;//标题
    private TextView mConfirm;//确定

    private Map<String, List<PicInfo>> mFilterData = new HashMap<>();//已经分类好的数据

    private boolean isShowCamera;//是否显示相机图标
    private boolean isShowGif;//显示gif标识
    private boolean isShowVideo;//显示视频标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_select);
        initView();
        initData();
    }

    private void initView() {
        mLimit = getIntent().getIntExtra(CODE_LIMIT, 9);//默认为九个
        isShowCamera = getIntent().getBooleanExtra(CODE_NEEDCAMERA, false);//相机显示标识
        isShowGif = getIntent().getBooleanExtra(CODE_SHOWGIF, false);//gif显示标识
        isShowVideo = getIntent().getBooleanExtra(CODE_SHOWVIDEO, false);//视频显示标识
        if (mLimit > 9) mLimit = 9;
        mContent = findViewById(R.id.activity_pic_sel_recy);
        mBack = findViewById(R.id.activity_pic_sel_back);
        mTitle = findViewById(R.id.activity_pic_sel_title);
        mConfirm = findViewById(R.id.activity_pic_sel_confirm);
        mContent.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new PicSelAdapter(this, isShowCamera);
        mAdapter.setSelectLimie(mLimit);
        mContent.setAdapter(mAdapter);
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
                    result.putExtra(CODE_SELECT, (Serializable) mAdapter.getSelectInfo());
                    setResult(CODE_RESULT, result);
                }
                finish();
            }
        });
        mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFilterData == null || mFilterData.size() == 0) {
                    Toast.makeText(PicSelectActivity.this.getApplicationContext(), "数据异常", Toast.LENGTH_SHORT).show();
                } else {
                    //打开列表__数据太大，设置全局变量
                    DirecotryIntentInfo.setData(mFilterData);
                    PicDirectoryActivity.launch(PicSelectActivity.this, REQUEST_DIR_CODE);
                }
            }
        });
        mAdapter.setOnBindImageViewListener(new PicSelAdapter.BindImageViewListener() {
            @Override
            public void BindImageView(String path, ImageView iv) {
                if (PicSelConfig.getInstance().getbindListener() != null) {
                    PicSelConfig.getInstance().getbindListener().BindSmallImageView(path, iv);
                }
            }
        });
        mAdapter.setOnClickListener(new PicSelAdapter.OnClickListener() {

            //source 1图片 2视频。如果选择图片，则进入多选模式，若选择视频，则进入播放模式
            @Override
            public void click(List<PicInfo> data, int pos, int source) {
                if (data != null && data.size() != 0) {
                    List<String> result = new ArrayList<>();
                    List<PicInfo> local = CloneUtils.cloneObjectSer(data);
                    PicSelIntentInfo.getInstance().setPicSelList(local);
                    for (PicInfo cache : data) {
                        result.add(cache.getPath());
                    }
                    //赋值对象到内存中
                    PicConstant.getInstance().setPicList(result);
                    PicBrowserActivity.launch(PicSelectActivity.this, pos, mLimit);
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
                PicSelMain.getInstance().takePic(PicSelectActivity.this,0);
            }
        });
        PicSelectManager.getInstance().setOnResultListener(new PicSelectManager.OnResultListener() {
            @Override
            public void Data(final List<PicInfo> result) {
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
            public void FilterData(final Map<String, List<PicInfo>> filterMap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFilterData = filterMap;
                    }
                });
            }
        });
        PicSelectManager.getInstance().init(this, new PicSelectApi.InitCallBack() {
            @Override
            public void NoPermission() {
                Toast.makeText(PicSelectActivity.this.getApplicationContext(), "权限不足", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void Success() {

            }
        });
        PicSelectManager.getInstance().load(isShowGif, isShowVideo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIR_CODE && resultCode == RESULT_OK) {
            String directory = data.getStringExtra("path");
            //选中的目录
            mTitle.setText(!TextUtils.isEmpty(directory) ? directory : "暂无标题");
            if (!TextUtils.isEmpty(directory) && mFilterData.get(directory) != null) {
                mAdapter.setData(mFilterData.get(directory));
            }
            updateSelCount();
        }
        //全屏页面，普通返回
        if (requestCode == PicBrowserActivity.CODE_REQUEST && resultCode == PicBrowserActivity.CODE_RESULT) {
            if (mAdapter != null) {
                for (int i = 0; i < PicSelIntentInfo.getInstance().getPicSelList().size(); i++) {
                    if (PicSelIntentInfo.getInstance().getPicSelList().get(i).isSelect()) {
                        Log.d("PicSel", "pic sel: " + i);
                    }
                }
                mAdapter.setData(PicSelIntentInfo.getInstance().getPicSelList());
                updateSelCount();
            }
        }
        //全屏页面，确认返回
        if (requestCode == PicBrowserActivity.CODE_REQUEST && resultCode == PicBrowserActivity.CODE_RESULT_CONFIRM) {
            if (mAdapter != null) {
                for (int i = 0; i < PicSelIntentInfo.getInstance().getPicSelList().size(); i++) {
                    if (PicSelIntentInfo.getInstance().getPicSelList().get(i).isSelect()) {
                        Log.d("PicSel", "pic sel: " + i);
                    }
                }
                mAdapter.setData(PicSelIntentInfo.getInstance().getPicSelList());
                updateSelCount();
                //设置返回结果
                if (mAdapter != null) {
                    Intent result = new Intent();
                    result.putExtra(CODE_SELECT, (Serializable) mAdapter.getSelectInfo());
                    setResult(CODE_RESULT, result);
                }
                finish();
            }
        }
        PicSelMain.getInstance().ActivityForResult(requestCode, resultCode, data, new PicSelMain.PicCallbackListener() {
            @Override
            public void cameraResult(String path) {
                //拍照回调
                List<String> pList = new ArrayList<>();
                pList.add(path);
                Intent result = new Intent();
                result.putExtra(CODE_SELECT, (Serializable) pList);
                setResult(CODE_RESULT, result);
                finish();
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

    @Override
    protected void onDestroy() {
        PicSelectManager.getInstance().release();
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
            mConfirm.setText(String.format(getResources().getString(R.string.pic_select_activity_checkbox_count),
                    selSize, mLimit));
        }
    }
}