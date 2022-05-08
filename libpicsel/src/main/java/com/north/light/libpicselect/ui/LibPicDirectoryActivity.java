package com.north.light.libpicselect.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.adapter.LibPicDirectoryDialogSelAdapter;
import com.north.light.libpicselect.bean.LibPicDirecotryBean;
import com.north.light.libpicselect.bean.LibPicDirecotryIntentInfo;
import com.north.light.libpicselect.bean.LibPicInfo;
import com.north.light.libpicselect.model.LibPicSelConfig;
import com.north.light.libpicselect.utils.LibPicCloneUtils;
import com.north.light.libpicselect.utils.LibPicPicScreenUtils;
import com.north.light.libpicselect.widget.ItemDecoration.LibPicDialogItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 目录选择activity
 */
public class LibPicDirectoryActivity extends LibPicBaseActivity {
    private RecyclerView mContent;
    private LibPicDirectoryDialogSelAdapter mAdapter;
    private Map<String, List<LibPicInfo>> data = new HashMap<>();
    //根布局
    private LinearLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.lib_pic_anim_pic_top_in, R.anim.lib_pic_anim_pic_top_out);
        setContentView(R.layout.lib_pic_activity_pic_deirectoryctivity);
        initView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.lib_pic_anim_pic_top_in, R.anim.lib_pic_anim_pic_top_out);
    }

    private void initView() {
        try {
            Map<String, List<LibPicInfo>> data = LibPicDirecotryIntentInfo.getData();
            if (data == null) {
                Toast.makeText(this.getApplicationContext(), "数据错误", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                mRoot = findViewById(R.id.lib_pic_activity_pic_directory_root);
                mContent = findViewById(R.id.lib_pic_activity_pic_directory_content);
                mContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mContent.addItemDecoration(new LibPicDialogItemDecoration(4));
                mAdapter = new LibPicDirectoryDialogSelAdapter(this);
                mContent.setAdapter(mAdapter);
                //设置recyclerview高度
                LinearLayout.LayoutParams contentParams = (LinearLayout.LayoutParams) mContent.getLayoutParams();
                contentParams.height = LibPicPicScreenUtils.getScreenHeight(this) / 2;
                mContent.setLayoutParams(contentParams);
                //监听
                mAdapter.setOnBindImageViewListener(new LibPicDirectoryDialogSelAdapter.BindImageViewListener() {
                    @Override
                    public void BindImageView(String path, ImageView iv) {
                        if (LibPicSelConfig.getInstance().getBindListener() != null)
                            LibPicSelConfig.getInstance().getBindListener().BindSmallImageView(path, iv);
                    }
                });
                mAdapter.setOnClickListener(new LibPicDirectoryDialogSelAdapter.OnClickListener() {
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
    private void setData(Map<String, List<LibPicInfo>> data) {
        if (data == null || data.size() == 0) return;
        //复制数据，防止引用
        this.data.clear();
        for (Map.Entry<String, List<LibPicInfo>> arg : data.entrySet()) {
            if (arg.getValue().size() != 0) {
                List<LibPicInfo> picList = LibPicCloneUtils.cloneObjectSer(arg.getValue());
                String key = arg.getKey();
                this.data.put(key, picList);
            }
        }
        //设置adapter__转化数据
        List<LibPicDirecotryBean> result = new ArrayList<>();
        for (Map.Entry<String, List<LibPicInfo>> arg : this.data.entrySet()) {
            LibPicDirecotryBean cache = new LibPicDirecotryBean();
            if (arg.getValue().size() != 0) {
                cache.setName(arg.getValue().get(0).getDirectory());
                cache.setCover(arg.getValue().get(0).getPath());
                cache.setCount(arg.getValue().get(0).getDirectoryCount());
                result.add(cache);
            }
        }
        if (mAdapter != null) {
            Collections.sort(result, new Comparator<LibPicDirecotryBean>() {
                @Override
                public int compare(LibPicDirecotryBean o1, LibPicDirecotryBean o2) {
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
        Intent intent = new Intent(activity, LibPicDirectoryActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
