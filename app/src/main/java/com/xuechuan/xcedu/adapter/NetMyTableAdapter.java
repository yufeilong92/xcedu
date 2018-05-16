package com.xuechuan.xcedu.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.recyclerview.BaseRecyclerAdapter;
import com.xuechuan.xcedu.Event.NetPlayTrySeeEvent;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.vo.ChaptersBeanVo;
import com.xuechuan.xcedu.vo.VideosBeanVo;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.adapter
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/5/15 15:52
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class NetMyTableAdapter extends BaseRecyclerAdapter<NetMyTableAdapter.ViewHolder> {
    private Context mContext;
    private List<ChaptersBeanVo> mData;
    private  LayoutInflater mInflater;
    private CheckBox mChbNetPlay;
    private TextView mTvNetTitle;
    private ImageView mIvNetGoorbuy;

    private HashMap<Integer, HashMap<Integer, Boolean>> mSelectMap;

    public NetMyTableAdapter(Context mContext, List<ChaptersBeanVo> mData) {
        this.mContext = mContext;
        this.mData = mData;
        mInflater = LayoutInflater.from(mContext);
        mSelectMap = new HashMap<>();
    }

    public NetMyTableAdapter() {
    }

    public void setOnClick(HashMap<Integer, HashMap<Integer, Boolean>> mSelectMap) {
        this.mSelectMap = mSelectMap;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        View view = mInflater.inflate(R.layout.item_net_mytable_rlv, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position, boolean isItem) {
        ChaptersBeanVo vo = mData.get(position);
        holder.mIvNetGoorbuy.setImageResource(R.mipmap.ic_more_go);

        holder.mTvNetTitle.setText(vo.getChaptername());
        List<VideosBeanVo> videos = vo.getVideos();
        if (videos != null && !videos.isEmpty()) {
            for (int i = 0; i < videos.size(); i++) {
                HashMap<Integer, Boolean> map = new HashMap<>();
                map.put(i, false);
                mSelectMap.put(position, map);
            }
            if (position == 0) {
                VideosBeanVo videosBeanVo = videos.get(0);
                if (videosBeanVo.isIstrysee()) {
                    EventBus.getDefault().postSticky(new NetPlayTrySeeEvent(videosBeanVo));
                }
            }
            holder.mRlvNetBookJie.setVisibility(View.VISIBLE);
            bindjieAdaper(holder, videos,position);
        } else {
            holder.mRlvNetBookJie.setVisibility(View.GONE);
        }

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mRlvNetBookJie.getVisibility() == View.GONE) {
                    holder.mRlvNetBookJie.setVisibility(View.VISIBLE);
                    holder.mIvNetGoorbuy.setImageResource(R.mipmap.ic_spread_gray);
                } else {
                    holder.mRlvNetBookJie.setVisibility(View.GONE);
                    holder.mIvNetGoorbuy.setImageResource(R.mipmap.ic_more_go);
                }
            }
        });*/

    }

    private void bindjieAdaper(ViewHolder holder, List<VideosBeanVo> videos, int position) {
        GridLayoutManager manager = new GridLayoutManager(mContext, 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        holder.mRlvNetBookJie.setLayoutManager(manager);
        NetMyTablejiEAdapter adapter = new NetMyTablejiEAdapter(mContext,new NetMyTableAdapter(), videos,position,mSelectMap);
        holder.mRlvNetBookJie.setAdapter(adapter);

    }

    @Override
    public int getAdapterItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvNetTitle;
        public ImageView mIvNetGoorbuy;
        public RecyclerView mRlvNetBookJie;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mRlvNetBookJie = (RecyclerView) itemView.findViewById(R.id.rlv_net_book_jie);
            this.mTvNetTitle = (TextView) itemView.findViewById(R.id.tv_net_title);
            this.mIvNetGoorbuy = (ImageView) itemView.findViewById(R.id.iv_net_goorbuy);
        }
    }


}
