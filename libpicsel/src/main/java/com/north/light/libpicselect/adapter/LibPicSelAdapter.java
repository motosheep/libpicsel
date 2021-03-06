package com.north.light.libpicselect.adapter;

import android.content.Context;
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
import com.north.light.libpicselect.bean.LibPicInfo;
import com.north.light.libpicselect.utils.LibPicCloneUtils;
import com.north.light.libpicselect.utils.LibPicPicScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * create by lzt
 * data 2019/12/8
 * <p>
 * change by lzt 20200823 增加视频数据源显示适配
 * change by lzt 20210809 修复没有选择图片时，相机图标显示不全问题
 */
public class LibPicSelAdapter extends RecyclerView.Adapter<LibPicSelAdapter.PicHolder> {
    private Context mContext;
    private List<LibPicInfo> mResult = new ArrayList<>();
    private BindImageViewListener mBindListener;
    private OnClickListener mOnClick;
    private long mScreenWidth;

    private int mSelectLimit = 9;//默认可选9个
    private boolean isShowCamera;//是否显示相机的标识


    public LibPicSelAdapter(Context mContext, boolean isShowCamera) {
        this.mContext = mContext;
        this.isShowCamera = isShowCamera;
        mScreenWidth = LibPicPicScreenUtils.getScreenWidth(mContext);
    }

    public void setSelectLimit(int selectLimit) {
        this.mSelectLimit = selectLimit;
    }

    public void setData(List<LibPicInfo> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        List<LibPicInfo> result = LibPicCloneUtils.cloneObjectSer(data);
        mResult.clear();
        //需要防止对象引用
        for (LibPicInfo cache : result) {
            LibPicInfo arg = new LibPicInfo();
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
        List<String> result = new ArrayList<>();
        List<LibPicInfo> cacheList = new ArrayList<>();
        for (LibPicInfo cache : mResult) {
            if (cache.isSelect()) {
                cacheList.add(cache);
            }
        }
        //排序
        Collections.sort(cacheList, new Comparator<LibPicInfo>() {
            @Override
            public int compare(LibPicInfo o1, LibPicInfo o2) {
                return (int) (o1.getSelTime() - o2.getSelTime());
            }
        });
        //转换
        for (LibPicInfo cache : cacheList) {
            if (cache.isSelect()) {
                result.add(cache.getPath());
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
                        for (LibPicInfo cache : mResult) {
                            if (cache.isSelect()) {
                                selectCount++;
                            }
                        }
                        if (selectCount < mSelectLimit) {
                            //可以选择
                            mResult.get(position).setSelect(true);
                            String path = mResult.get(position).getPath();
                        } else {
                            holder.mCheckBox.setChecked(false);
                            //超出了上限
                            Toast.makeText(mContext.getApplicationContext(), "最多只能选择" + mSelectLimit + "个", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //设置选中数据
                        String path = mResult.get(position).getPath();
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
        void click(List<LibPicInfo> data, int pos, int source);

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
