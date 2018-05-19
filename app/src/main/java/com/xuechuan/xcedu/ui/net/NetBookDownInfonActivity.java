package com.xuechuan.xcedu.ui.net;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easefun.polyvsdk.download.util.PolyvDownloaderUtils;
import com.xuechuan.xcedu.Event.NetDownDoneEvent;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.adapter.DownDoneInfomAdapter;
import com.xuechuan.xcedu.base.BaseActivity;
import com.xuechuan.xcedu.db.DbHelp.DbHelperDownAssist;
import com.xuechuan.xcedu.db.DownVideoDb;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.Db.DownVideoVo;
import com.xuechuan.xcedu.vo.DownInfomSelectVo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: NetBookDownInfonActivity
 * @Package com.xuechuan.xcedu.ui.net
 * @Description: 我的下载详情
 * @author: L-BackPacker
 * @date: 2018/5/16 11:49
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/5/16
 */
public class NetBookDownInfonActivity extends BaseActivity implements View.OnClickListener {


    private TextView mTvNetDownInfomMake;
    private ImageView mIvNetDownInfomImg;
    private TextView mTvDownBookInfomeName;
    private TextView mTvNetDownInfomeNumber;
    private TextView mTvNetDownInfomeCout;
    private RecyclerView mRlvNetDownInfomeList;
    private CheckBox mChbNetDownInfomeAll;
    private Button mBtnNetDownInfomeDelect;
    private LinearLayout mLlNetDownInfomeAll;
    private DownVideoDb mVideoDb;
    private List<DownInfomSelectVo> mDataSelectList;
    private Context mContext;
    private DownDoneInfomAdapter mInfomAdapter;
    private String mCount = "0";
    private int mNumber = 0;
    private DbHelperDownAssist mDao;
    private TextView mTvInfomEmpty;

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_book_down_infon);
        initView();
    }*/

    private static String KID = "kid";
    private String mKid;

    /**
     * @param context
     * @param kid     课目id
     * @return
     */
    public static Intent newInstance(Context context, String kid) {
        Intent intent = new Intent(context, NetBookDownInfonActivity.class);
        intent.putExtra(KID, kid);
        return intent;
    }

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_net_book_down_infon);
        if (getIntent() != null) {
            mKid = getIntent().getStringExtra(KID);
        }
        initView();
        mDao = DbHelperDownAssist.getInstance();
        initData();

    }

    private void initData() {
        mVideoDb = mDao.queryUserDownInfomWithKid(mKid);
        if (mVideoDb == null) {
            mTvInfomEmpty.setVisibility(View.VISIBLE);
            return;
        }
        List<DownVideoVo> downlist = mVideoDb.getDownlist();
        if (downlist != null && !downlist.isEmpty()) {
            long count = 0;
            for (DownVideoVo vo : downlist) {
                long fileSize = vo.getFileSize();
                count += fileSize;
            }
            mCount = Utils.convertFileSizeB(count);
            mNumber = downlist.size();
        }
        mDataSelectList = new ArrayList<>();
        for (DownVideoVo vo : mVideoDb.getDownlist()) {
            DownInfomSelectVo selectVo = new DownInfomSelectVo();
            selectVo.setPid(vo.getPid());
            selectVo.setZid(vo.getZid());
            selectVo.setVid(vo.getVid());
            selectVo.setChbSelect(false);
            selectVo.setShowChb(false);
            selectVo.setShowPlay(true);
            selectVo.setPlaySelect(false);
            mDataSelectList.add(selectVo);
        }
        bindAdapter();
        mTvDownBookInfomeName.setText(mVideoDb.getKName());
        mTvNetDownInfomeCout.setText(mCount);
        mTvNetDownInfomeNumber.setText(mNumber + "");
        MyAppliction.getInstance().displayImages(mIvNetDownInfomImg, mVideoDb.getUrlImg(), false);
        mChbNetDownInfomeAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    initShow(false, false, true, true);
                } else {
                    initShow(false, false, true, false);
                }
                mInfomAdapter.notifyDataSetChanged();
            }
        });

    }

    private void bindAdapter() {
        GridLayoutManager manager = new GridLayoutManager(NetBookDownInfonActivity.this, 1);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        mRlvNetDownInfomeList.setLayoutManager(manager);
        mInfomAdapter = new DownDoneInfomAdapter(mContext, mVideoDb, mDataSelectList);
        mRlvNetDownInfomeList.setAdapter(mInfomAdapter);
        mInfomAdapter.setChbClickListener(new DownDoneInfomAdapter.onItemChbClickListener() {
            @Override
            public void onChecaListener(DownVideoVo db, boolean isCheck, int position) {
                if (mDataSelectList == null || mDataSelectList.isEmpty()) {
                    return;
                }
                if (isCheck) {
                    for (DownInfomSelectVo vo : mDataSelectList) {
                        vo.setChbSelect(true);
                    }
                } else {
                    for (DownInfomSelectVo vo : mDataSelectList) {
                        vo.setChbSelect(false);
                    }
                }
                if (mInfomAdapter != null)
                    mInfomAdapter.notifyDataSetChanged();
            }
        });
        mInfomAdapter.setClickListener(new DownDoneInfomAdapter.onItemClickListener() {
            @Override
            public void onClickListener(Object obj, int position) {

            }
        });

    }

    private void initView() {
        mContext = this;
        mTvNetDownInfomMake = (TextView) findViewById(R.id.tv_net_down_infom_make);
        mTvNetDownInfomMake.setOnClickListener(this);
        mIvNetDownInfomImg = (ImageView) findViewById(R.id.iv_net_down_infom_img);
        mTvDownBookInfomeName = (TextView) findViewById(R.id.tv_down_book_infome_name);
        mTvNetDownInfomeNumber = (TextView) findViewById(R.id.tv_net_down_infome_number);
        mTvNetDownInfomeCout = (TextView) findViewById(R.id.tv_net_down_infome_cout);
        mRlvNetDownInfomeList = (RecyclerView) findViewById(R.id.rlv_net_down_infome_list);
        mChbNetDownInfomeAll = (CheckBox) findViewById(R.id.chb_net_down_infome_all);
        mBtnNetDownInfomeDelect = (Button) findViewById(R.id.btn_net_down_infome_delect);
        mLlNetDownInfomeAll = (LinearLayout) findViewById(R.id.ll_net_down_infome_all);
        mBtnNetDownInfomeDelect.setOnClickListener(this);
        mTvInfomEmpty = (TextView) findViewById(R.id.tv_infom_empty);
        mTvInfomEmpty.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_net_down_infome_delect:
                //选中的vid 集合
                for (int i = 0; i < mDataSelectList.size(); i++) {
                    DownInfomSelectVo selectVo = mDataSelectList.get(i);
                    if (selectVo.isChbSelect()) {
                        mDao.delectItem(mVideoDb.getKid(), selectVo.getPid(), selectVo.getZid());
                        delectVideo(selectVo.getVid());
                    }
                }
                initData();
                mInfomAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_net_down_infom_make:
                String str = getTextStr(mTvNetDownInfomMake);
                if (str.equals(getString(R.string.edit))) {
                    mTvNetDownInfomMake.setText(R.string.complete);
                    mLlNetDownInfomeAll.setVisibility(View.VISIBLE);
                    if (mDataSelectList != null && !mDataSelectList.isEmpty())
                        initShow(false, false, true, false);
                } else {
                    mLlNetDownInfomeAll.setVisibility(View.GONE);
                    mTvNetDownInfomMake.setText(R.string.edit);
                    if (mDataSelectList != null && !mDataSelectList.isEmpty()) {
                        initShow(true, false, false, false);
                    }
                }
                mInfomAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * @param paly       是否展示播放按钮
     * @param selectplay 是否选中
     * @param chb        是否展示选择
     * @param selelctchb 是否选中
     */
    private void initShow(boolean paly, boolean selectplay, boolean chb, boolean selelctchb) {
        for (DownInfomSelectVo vo : mDataSelectList) {
            vo.setShowPlay(paly);
            vo.setPlaySelect(selectplay);
            vo.setShowChb(chb);
            vo.setChbSelect(selelctchb);
        }
    }
//    static int	BITRATE_ERROR
//    码率（清晰度）错误
//    static int	DELETE_VIDEO_FILE_FAIL
//    删除文件失败
//    static int	DELETE_VIDEO_SUCCESS
//    删除视频成功
//    static int	DOWNLOADER_DIR_NULL
//    下载文件夹为null（未设置）
//    static int	VIDEO_ID_ERROR
//    视频id错误

    /**
     * 保利视频id
     *
     * @param vid
     */
    private void delectVideo(String vid) {
        AsyncTask<String, Void, Void> asyncTask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                String string = strings[0];
                Log.e("=====", "删除视频id: " + string);
                PolyvDownloaderUtils utils = new PolyvDownloaderUtils();
                int i = utils.deleteVideo(string);
                Log.e("yfl", "删除视频结果: " + i);
                return null;
            }
        };
        asyncTask.execute(vid);
    }
}
