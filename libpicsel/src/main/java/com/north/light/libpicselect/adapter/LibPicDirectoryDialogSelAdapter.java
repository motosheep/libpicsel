package com.north.light.libpicselect.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.north.light.libpicselect.R;
import com.north.light.libpicselect.bean.LibPicDirecotryBean;
import com.north.light.libpicselect.utils.LibPicPicScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * create by lzt
 * data 2019/12/8
 */
public class LibPicDirectoryDialogSelAdapter extends RecyclerView.Adapter<LibPicDirectoryDialogSelAdapter.PicHolder> {
    private static final String TAG = LibPicDirectoryDialogSelAdapter.class.getName();
    private Context mContext;
    private List<LibPicDirecotryBean> mResult = new ArrayList<>();
    private BindImageViewListener mBindListener;
    private OnClickListener mOnClick;
    private long mScreenWidth;

    public LibPicDirectoryDialogSelAdapter(Context mContext) {
        this.mContext = mContext;
        mScreenWidth = LibPicPicScreenUtils.getScreenWidth(mContext);
    }

    public void setData(List<LibPicDirecotryBean> data) {
        mResult.clear();
        if (data == null) {
            data = new ArrayList<>();
        }
        this.mResult = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PicHolder(LayoutInflater.from(mContext).inflate(R.layout.lib_pic_item_dialog_directory_content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PicHolder holder, final int position) {
        LinearLayout.LayoutParams mImgP = (LinearLayout.LayoutParams) holder.mImage.getLayoutParams();
        mImgP.height = (int) (mScreenWidth / 5);
        mImgP.width = (int) (mScreenWidth / 5);
        holder.mImage.setLayoutParams(mImgP);

        LinearLayout.LayoutParams mPLayoutP = (LinearLayout.LayoutParams) holder.mPLayout.getLayoutParams();
        mPLayoutP.height = (int) (mScreenWidth / 5);
        mPLayoutP.width = (int) (mScreenWidth);
        holder.mPLayout.setLayoutParams(mPLayoutP);

        if (mBindListener != null) {
            mBindListener.BindImageView(mResult.get(position).getCover(), holder.mImage);
        }
        holder.mTitle.setText(TextUtils.isEmpty(mResult.get(position).getName()) ? "暂无数据"
                : (mResult.get(position).getName() + ("(" + mResult.get(position).getCount() + ")")));
        holder.mPLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClick != null) {
                    mOnClick.click(mResult.get(position).getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mResult.size();
    }

    public class PicHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mTitle;
        private LinearLayout mPLayout;

        public PicHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.lib_pic_item_dialog_directory_content_image);
            mTitle = itemView.findViewById(R.id.lib_pic_item_dialog_directory_content_text);
            mPLayout = itemView.findViewById(R.id.lib_pic_item_dialog_directory_content_pLayout);
        }
    }

    public interface OnClickListener {
        void click(String directory);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClick = onClickListener;
    }

    public void removeOnClickListener() {
        this.mOnClick = null;
    }

    public interface BindImageViewListener {
        void BindImageView(String path, ImageView iv);
    }

    public void setOnBindImageViewListener(BindImageViewListener bindListener) {
        this.mBindListener = bindListener;
    }

    public void removeBindImageViewListener() {
        this.mBindListener = null;
    }
}
