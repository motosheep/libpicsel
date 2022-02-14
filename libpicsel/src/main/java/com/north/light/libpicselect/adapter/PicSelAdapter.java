package com.north.light.libpicselect.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.bean.PicInfo;
import com.north.light.libpicselect.bean.PicSelCacheInfo;
import com.north.light.libpicselect.utils.CloneUtils;
import com.north.light.libpicselect.utils.PicScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by lzt
 * data 2019/12/8
 * <p>
 * change by lzt 20200823 增加视频数据源显示适配
 * change by lzt 20210809 修复没有选择图片时，相机图标显示不全问题
 */
public class PicSelAdapter extends RecyclerView.Adapter<PicSelAdapter.PicHolder> {
    private Context mContext;
    private List<PicInfo> mResult = new ArrayList<>();
    private BindImageViewListener mBindListener;
    private OnClickListener mOnClick;
    private long mScreenWidth;

    private int mSelectLimit = 9;//默认可选9个
    private boolean isShowCamera;//是否显示相机的标识
    //选择的图片数据---中间缓存
    private Map<String, Long> mSelInfo = new HashMap<>();


    public PicSelAdapter(Context mContext, boolean isShowCamera) {
        this.mContext = mContext;
        this.isShowCamera = isShowCamera;
        mScreenWidth = PicScreenUtils.getScreenWidth(mContext);
    }

    public void setSelectLimie(int selectLimit) {
        this.mSelectLimit = selectLimit;
    }

    public void setData(List<PicInfo> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        List<PicInfo> result = CloneUtils.cloneObjectSer(data);
        mResult.clear();
        mSelInfo.clear();
        //需要防止对象引用
        for (PicInfo cache : result) {
            PicInfo arg = new PicInfo();
            arg.setPath(cache.getPath());
            arg.setDirectory(cache.getDirectory());
            arg.setDirectoryCount(cache.getDirectoryCount());
            arg.setName(cache.getName());
            arg.setSelect(cache.isSelect());
            arg.setSource(cache.getSource());
            mResult.add(arg);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取选中的数据
     * 遍历选中数据的集合--直接返回
     */
    public List<String> getSelectInfo() {
        if (mSelInfo.size() == 0) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        List<PicSelCacheInfo> picSelInfoList = new ArrayList<>();
        for (Map.Entry<String, Long> cache : mSelInfo.entrySet()) {
            long clickTime = cache.getValue();
            String clickPath = cache.getKey();
            picSelInfoList.add(new PicSelCacheInfo(clickPath, clickTime));
        }
        //排序
        Collections.sort(picSelInfoList, new Comparator<PicSelCacheInfo>() {
            @Override
            public int compare(PicSelCacheInfo o1, PicSelCacheInfo o2) {
                return (int) (o1.getClickTime() - o2.getClickTime());
            }
        });
        //遍历数据，取值
        for (PicSelCacheInfo info : picSelInfoList) {
            String path = info.getPath();
            if (!TextUtils.isEmpty(path)) {
                result.add(path);
            }
        }
        return result;
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PicHolder(LayoutInflater.from(mContext).inflate(R.layout.lib_pic_item_main_content, parent, false));
    }

    @Override
    public void onViewRecycled(@NonNull PicHolder holder) {
        super.onViewRecycled(holder);
        holder.mCheckBox.setOnCheckedChangeListener(null);
    }

    @Override
    public void onBindViewHolder(@NonNull final PicHolder holder, final int i) {
        //设置图片显示通用宽高-----------------
        RelativeLayout.LayoutParams mImgP = (RelativeLayout.LayoutParams) holder.mImage.getLayoutParams();
        mImgP.height = (int) (mScreenWidth / 4);
        holder.mImage.setLayoutParams(mImgP);
        //设置图片显示通用宽高-----------------
        if (isShowCamera && i == 0) {
            holder.mCheckBox.setVisibility(View.GONE);
            holder.mImage.setImageResource(R.drawable.lib_pic_ic_camera_alt_black_24dp);
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClick != null) {
                        mOnClick.camera();
                    }
                }
            });
            holder.mSource.setVisibility(View.GONE);
        } else {
            final int position = isShowCamera ? (i - 1) : i;
            holder.mCheckBox.setVisibility(View.VISIBLE);
            if (mBindListener != null) {
                mBindListener.BindImageView(mResult.get(position).getPath(), holder.mImage);
            }
            if (mResult.get(position).isSelect()) {
                holder.mCheckBox.setChecked(true);
            } else {
                holder.mCheckBox.setChecked(false);
            }
            //数据源判断
            if (mResult.get(position).getSource() == 2) {
                //视频源
                holder.mSource.setText("视频");
                holder.mSource.setVisibility(View.VISIBLE);
            } else {
                //非视频源
                holder.mSource.setVisibility(View.GONE);
            }
            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        int selectCount = 0;
                        for (PicInfo cache : mResult) {
                            if (cache.isSelect()) {
                                selectCount++;
                            }
                        }
                        if (selectCount < mSelectLimit) {
                            //可以选择
                            mResult.get(position).setSelect(true);
                            String path = mResult.get(position).getPath();
                            if (!TextUtils.isEmpty(path)) {
                                mSelInfo.put(path, System.currentTimeMillis());
                            }
                        } else {
                            holder.mCheckBox.setChecked(false);
                            //超出了上限
                            Toast.makeText(mContext.getApplicationContext(), "最多只能选择" + mSelectLimit + "个", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //设置选中数据
                        String path = mResult.get(position).getPath();
                        if (!TextUtils.isEmpty(path)) {
                            mSelInfo.remove(path);
                        }
                        mResult.get(position).setSelect(false);
                    }
                    //回调事件
                    if (mOnClick != null) {
                        mOnClick.check();
                    }
                }
            });
            holder.mImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClick != null) {
                        //区分点击的是图片还是视频
                        mOnClick.click(mResult, position, mResult.get(position).getSource());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mResult.size() + (isShowCamera ? 1 : 0);
    }

    public class PicHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private CheckBox mCheckBox;
        //数据源
        private TextView mSource;

        public PicHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.lib_pic_item_main_content_image);
            mCheckBox = itemView.findViewById(R.id.lib_pic_item_main_content_checkbox);
            mSource = itemView.findViewById(R.id.lib_pic_item_main_content_source);
        }
    }

    //点击事件
    public interface OnClickListener {
        //点击事件__20200823增加数据源入参
        void click(List<PicInfo> data, int pos, int source);

        //check box事件
        void check();

        //相机点击事件
        void camera();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClick = onClickListener;
    }

    public void removeOnClickListener() {
        this.mOnClick = null;
    }

    //绑定外部图片加载的方法
    public interface BindImageViewListener {
        void BindImageView(String path, ImageView iv);
    }

    public void setOnBindImageViewListener(BindImageViewListener bindListener) {
        this.mBindListener = bindListener;
    }

    public void reomveBindImageViewListener() {
        this.mBindListener = null;
    }
}
