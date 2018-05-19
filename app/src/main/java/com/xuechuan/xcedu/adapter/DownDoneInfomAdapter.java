package com.xuechuan.xcedu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.andview.refreshview.callback.IFooterCallBack;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.db.DownVideoDb;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.Db.DownVideoVo;
import com.xuechuan.xcedu.vo.DownInfomSelectVo;

import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.adapter
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/5/19 18:39
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class DownDoneInfomAdapter extends RecyclerView.Adapter<DownDoneInfomAdapter.ViewHolde> implements View.OnClickListener {

    private final DownVideoDb mDownVideoDb;
    private final List<DownInfomSelectVo> mSelectVo;
    private Context mContext;
    private List<DownVideoVo> mData;
    private final LayoutInflater mInflater;

    public DownDoneInfomAdapter(Context mContext, DownVideoDb db, List<DownInfomSelectVo> mDataSelectList) {
        this.mContext = mContext;
        this.mData = db.getDownlist();
        this.mDownVideoDb = db;
        this.mSelectVo = mDataSelectList;
        mInflater = LayoutInflater.from(mContext);
    }

    private onItemClickListener clickListener;

    public interface onItemClickListener {
        public void onClickListener(Object obj, int position);
    }

    public void setClickListener(onItemClickListener clickListener) {
        this.clickListener = clickListener;
    }


    private onItemChbClickListener chbClickListener;

    public interface onItemChbClickListener {
        public void onChecaListener(DownVideoVo db, boolean isCheck, int position);
    }

    public void setChbClickListener(onItemChbClickListener clickListener) {
        this.chbClickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.net_down_infom, null);
        view.setOnClickListener(this);
        ViewHolde holde = new ViewHolde(view);
        return holde;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolde holder, final int position) {
        final DownVideoVo vo = mData.get(position);
        holder.mTvItemDownInfomTitle.setText(vo.getTitle());
        String s = Utils.convertFileSizeB(vo.getFileSize());
        holder.mChbItemDownInfomSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (chbClickListener != null) {
                    chbClickListener.onChecaListener(vo, isChecked, position);
                }
            }
        });
        holder.mTvItemDownInfomCout.setText(s);
        if (mSelectVo != null && !mSelectVo.isEmpty())
            for (DownInfomSelectVo selectVo : mSelectVo) {
                if (selectVo.getPid().equals(vo.getPid()) &&
                        selectVo.getZid().equals(vo.getZid())) {
                    if (selectVo.isShowChb()) {//选择按钮
                        holder.mChbItemDownInfomSelect.setVisibility(View.VISIBLE);
                        if (selectVo.isChbSelect()) {
                            holder.mChbItemDownInfomSelect.setChecked(true);
                        }
                    } else {
                        holder.mChbItemDownInfomSelect.setVisibility(View.GONE);
                    }
                    if (selectVo.isShowPlay()) {//播放按钮
                        holder.mChbItemDownInfomPlay.setVisibility(View.VISIBLE);
                        if (selectVo.isPlaySelect()) {
                            holder.mChbItemDownInfomPlay.setChecked(true);
                        }
                    } else {
                        holder.mChbItemDownInfomPlay.setVisibility(View.GONE);
                    }
                }
            }
        holder.itemView.setTag(vo);
        holder.itemView.setId(position);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            clickListener.onClickListener(v.getTag(), v.getId());
        }

    }

    public class ViewHolde extends RecyclerView.ViewHolder {
        public CheckBox mChbItemDownInfomSelect;
        public CheckBox mChbItemDownInfomPlay;
        public TextView mTvItemDownInfomTitle;
        public TextView mTvItemDownInfomCout;

        public ViewHolde(View itemView) {
            super(itemView);
            this.mChbItemDownInfomSelect = (CheckBox) itemView.findViewById(R.id.chb_item_down_infom_select);
            this.mChbItemDownInfomPlay = (CheckBox) itemView.findViewById(R.id.chb_item_down_infom_play);
            this.mTvItemDownInfomTitle = (TextView) itemView.findViewById(R.id.tv_item_down_infom_title);
            this.mTvItemDownInfomCout = (TextView) itemView.findViewById(R.id.tv_item_down_infom_cout);
        }
    }


}
