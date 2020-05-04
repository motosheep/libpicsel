package com.north.light.libpicselect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.north.light.libpicselect.PicSelMain;
import com.north.light.libpicselect.R;
import com.north.light.libpicselect.adapter.PicSelAdapter;
import com.north.light.libpicselect.bean.DirecotryIntentInfo;
import com.north.light.libpicselect.bean.PicInfo;
import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.model.PicSelectApi;
import com.north.light.libpicselect.model.PicSelectManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 需要注意由于对象引用，导致数据错乱问题
 */
public class PicSelectActivity extends PicBaseActivity {
    public static final int CODE_REQUEST = 0x0001;
    public static final int CODE_RESULT = 0x0002;
    public static final String CODE_LIMIT = "PicSelectActivity_CODE_LIMIT";
    public static final String CODE_NEEDCAMERA = "PicSelectActivity_CODE_NEEDCAMERA";
    public static final String CODE_SHOWGIF = "PicSelectActivity_CODE_SHOWGIF";
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
        if (mLimit > 9) mLimit = 9;
        mContent = findViewById(R.id.activity_pic_sel_recy);
        mBack = findViewById(R.id.activity_pic_sel_back);
        mTitle = findViewById(R.id.activity_pic_sel_title);
        mConfirm = findViewById(R.id.activity_pic_sel_confirm);
        //设置初始值
        mConfirm.setText(String.format(getResources().getString(R.string.pic_select_activity_checkbox_count), 0, mLimit));
        mContent.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter = new PicSelAdapter(this, isShowCamera);
        mAdapter.setSelectLimie(mLimit);
        mContent.setAdapter(mAdapter);
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
            @Override
            public void click(String directory) {
                //点击
                if (!TextUtils.isEmpty(directory)) {
                    List<String> picList = new ArrayList<>();
                    picList.add(directory);
                    PicBrowserActivity.launch(PicSelectActivity.this, picList);
                }
            }

            @Override
            public void check() {
                //check box 事件
                if (mAdapter != null) {
                    int selSize = mAdapter.getSelectInfo().size();
                    mConfirm.setText(String.format(getResources().getString(R.string.pic_select_activity_checkbox_count),
                            selSize, mLimit));
                }
            }

            @Override
            public void camera() {
                //拍照
                PicSelMain.getIntance().takePic(PicSelectActivity.this);
            }
        });
        PicSelectManager.getInstance().setOnResultListener(new PicSelectManager.OnResultListener() {
            @Override
            public void Data(final List<PicInfo> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            mAdapter.setData(result, isShowGif);
                        }
                    }
                });
            }

            @Override
            public void FilterData(Map<String, List<PicInfo>> filterMap) {
                mFilterData = filterMap;
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
        PicSelectManager.getInstance().load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIR_CODE && resultCode == RESULT_OK) {
            String directory = data.getStringExtra("path");
            //选中的目录
            mTitle.setText(!TextUtils.isEmpty(directory) ? directory : "暂无标题");
            mConfirm.setText(String.format(getResources().getString(R.string.pic_select_activity_checkbox_count), 0, mLimit));
            if (!TextUtils.isEmpty(directory) && mFilterData.get(directory) != null) {
                mAdapter.setData(mFilterData.get(directory),isShowGif);
            }
        }
        PicSelMain.getIntance().ActivityForResult(requestCode, resultCode, data, new PicSelMain.PicCallbackListener() {
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
        });
    }

    @Override
    protected void onDestroy() {
        PicSelectManager.getInstance().release();
        mAdapter.reomveBindImageViewListener();
        mAdapter.removeOnClickListener();
        super.onDestroy();
    }
}
