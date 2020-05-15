package com.north.light.libpicselect.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.adapter.DirectoryDialogSelAdapter;
import com.north.light.libpicselect.bean.DirecotryBean;
import com.north.light.libpicselect.bean.DirecotryIntentInfo;
import com.north.light.libpicselect.bean.PicInfo;
import com.north.light.libpicselect.model.PicSelConfig;
import com.north.light.libpicselect.utils.CloneUtils;
import com.north.light.libpicselect.utils.PicScreenUtils;
import com.north.light.libpicselect.widget.DialogItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 目录选择activity
 */
public class PicDirectoryActivity extends PicBaseActivity {
    private RecyclerView mContent;
    private DirectoryDialogSelAdapter mAdapter;
    private Map<String, List<PicInfo>> data = new HashMap<>();
    //根布局
    private LinearLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_pic_top_in, R.anim.anim_pic_top_out);
        setContentView(R.layout.activity_pic_deirectoryctivity);
        initView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_pic_top_in, R.anim.anim_pic_top_out);
    }

    private void initView() {
        try {
            Map<String, List<PicInfo>> data = DirecotryIntentInfo.getData();
            if (data == null) {
                Toast.makeText(this.getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mRoot = findViewById(R.id.activity_pic_directory_root);
                mContent = findViewById(R.id.activity_pic_directory_content);
                mContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mContent.addItemDecoration(new DialogItemDecoration(4));
                mAdapter = new DirectoryDialogSelAdapter(this);
                mContent.setAdapter(mAdapter);
                //设置recyclerview高度
                LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) mContent.getLayoutParams();
                contentParams.height = PicScreenUtils.getScreenHeight(this) / 2;
                mContent.setLayoutParams(contentParams);
                //监听
                mAdapter.setOnBindImageViewListener(new DirectoryDialogSelAdapter.BindImageViewListener() {
                    @Override
                    public void BindImageView(String path, ImageView iv) {
                        if (PicSelConfig.getInstance().getbindListener() != null)
                            PicSelConfig.getInstance().getbindListener().BindSmallImageView(path, iv);
                    }
                });
                mAdapter.setOnClickListener(new DirectoryDialogSelAdapter.OnClickListener() {
                    @Override
                    public void click(String directory) {
                        //设置选中路径，并返回上一页
                        Intent intent = new Intent();
                        intent.putExtra("path", directory);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                mRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                //设置数据
                setData(data);
            }
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //设置数据源
    private void setData(Map<String, List<PicInfo>> data) {
        if (data == null || data.size() == 0) return;
        //复制数据，防止引用
        this.data.clear();
        for (Map.Entry<String, List<PicInfo>> arg : data.entrySet()) {
            if (arg.getValue().size() != 0) {
                List<PicInfo> picList = CloneUtils.cloneObjectSer(arg.getValue());
                String key = arg.getKey();
                this.data.put(key, picList);
            }
        }
        //设置adapter__转化数据
        List<DirecotryBean> result = new ArrayList<>();
        for (Map.Entry<String, List<PicInfo>> arg : this.data.entrySet()) {
            DirecotryBean cache = new DirecotryBean();
            if (arg.getValue().size() != 0) {
                cache.setName(arg.getValue().get(0).getDirectory());
                cache.setCover(arg.getValue().get(0).getPath());
                cache.setCount(arg.getValue().get(0).getDirectoryCount());
                result.add(cache);
            }
        }
        if (mAdapter != null) {
            Collections.sort(result, new Comparator<DirecotryBean>() {
                @Override
                public int compare(DirecotryBean o1, DirecotryBean o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            mAdapter.setData(result);
        }
    }

    @Override
    protected void onDestroy() {
        mAdapter.removeBindImageViewListener();
        mAdapter.removeOnClickListener();
        super.onDestroy();
    }

    /**
     * 启动
     */
    public static void launch(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PicDirectoryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
