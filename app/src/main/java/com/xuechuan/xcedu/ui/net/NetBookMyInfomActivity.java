package com.xuechuan.xcedu.ui.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvBitRate;
import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.marquee.PolyvMarqueeItem;
import com.easefun.polyvsdk.marquee.PolyvMarqueeView;
import com.easefun.polyvsdk.video.PolyvPlayErrorReason;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.listener.IPolyvOnAdvertisementEventListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnErrorListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureClickListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureLeftDownListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureLeftUpListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureRightDownListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureRightUpListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureSwipeLeftListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureSwipeRightListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnPreparedListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnQuestionAnswerTipsListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnVideoPlayErrorListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnVideoStatusListener;
import com.easefun.polyvsdk.vo.PolyvADMatterVO;
import com.easefun.polyvsdk.vo.PolyvVideoVO;
import com.xuechuan.xcedu.Event.BookTableEvent;
import com.xuechuan.xcedu.Event.NetMyPlayEvent;
import com.xuechuan.xcedu.Event.NetMyPlayTrySeeEvent;
import com.xuechuan.xcedu.Event.VideoIdEvent;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.adapter.MyNetBookIndicatorAdapter;
import com.xuechuan.xcedu.adapter.MyTagPagerAdapter;
import com.xuechuan.xcedu.adapter.NetMyDownTableAdapter;
import com.xuechuan.xcedu.base.BaseActivity;
import com.xuechuan.xcedu.db.DbHelp.DbHelperAssist;
import com.xuechuan.xcedu.db.DbHelp.DbHelperDownAssist;
import com.xuechuan.xcedu.db.DownVideoDb;
import com.xuechuan.xcedu.fragment.NetMyBokTableFragment;
import com.xuechuan.xcedu.fragment.NetMyBookVualueFragment;
import com.xuechuan.xcedu.player.BaolIHttp.PolyvVlmsHelper;
import com.xuechuan.xcedu.player.player.PolyvPlayerLightView;
import com.xuechuan.xcedu.player.player.PolyvPlayerMediaController;
import com.xuechuan.xcedu.player.player.PolyvPlayerProgressView;
import com.xuechuan.xcedu.player.player.PolyvPlayerVolumeView;
import com.xuechuan.xcedu.player.util.MyDownloadListener;
import com.xuechuan.xcedu.player.util.PolyvErrorMessageUtils;
import com.xuechuan.xcedu.player.util.PolyvScreenUtils;
import com.xuechuan.xcedu.service.SubmitProgressService;
import com.xuechuan.xcedu.utils.ArrayToListUtil;
import com.xuechuan.xcedu.utils.DialogUtil;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.vo.ChaptersBeanVo;
import com.xuechuan.xcedu.vo.CoursesBeanVo;
import com.xuechuan.xcedu.vo.Db.DownVideoVo;
import com.xuechuan.xcedu.vo.Db.UserLookVideoVo;
import com.xuechuan.xcedu.vo.VideosBeanVo;
import com.xuechuan.xcedu.weight.CommonPopupWindow;
import com.xuechuan.xcedu.weight.NoScrollViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: NetBookMyInfomActivity
 * @Package com.xuechuan.xcedu.ui.net
 * @Description: 我的网课
 * @author: L-BackPacker
 * @date: 2018/5/16 17:46
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/5/16
 */
public class NetBookMyInfomActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = NetBookMyInfomActivity.class.getSimpleName();
    /**
     * 播放器的parentView
     */
    private RelativeLayout viewLayout = null;
    /**
     * 播放主视频播放器
     */
    private PolyvVideoView videoView = null;
    /**
     * 跑马灯控件
     */
    private PolyvMarqueeView marqueeView = null;
    private PolyvMarqueeItem marqueeItem = null;
    /**
     * 视频控制栏
     */
    private PolyvPlayerMediaController mediaController = null;
    /**
     * 手势出现的亮度界面
     */
    private PolyvPlayerLightView lightView = null;
    /**
     * 手势出现的音量界面
     */
    private PolyvPlayerVolumeView volumeView = null;
    /**
     * 手势出现的进度界面
     */
    private PolyvPlayerProgressView progressView = null;
    /**
     * 视频加载缓冲视图
     */
    private ProgressBar loadingProgress = null;

    private int fastForwardPos = 0;
    private boolean isPlay = false;

    private LinearLayout ll_title_bar;

    /***
     * 数据类型
     */
    public static final String SERIALIZABLELIST = "person_data";
    private CoursesBeanVo dataVo;
    private TextView mTvNetBookTitle;
    private MagicIndicator mNetMagicIndicator;
    private NoScrollViewPager mVpNetBar;
    private Context mContext;
    private ImageView mIvNetPlay;
    private ImageView mIvNetBookPlay;
    private RelativeLayout mRlPlaylayout;
    private String vid;
    private CommonPopupWindow popDown;
    private LinearLayout mLlNetPlayRoot;
    private ImageView mIvNetMybookDown;
    /**
     * 科目章集合
     */
    private List mTableList;
    private NetMyDownTableAdapter mDownAdapter;
    /**
     * 保利视频请求
     */
    private PolyvVlmsHelper helper;
    private int mVideoid;
    private ImageView mIvBack;
    private DbHelperDownAssist mDao;
    private int mPid;
    private int mZid;
    private String mTitleName;

    @Override
    protected void onResume() {
        super.onResume();
        //回来后继续播放
        if (isPlay) {
            videoView.onActivityResume();
//            danmuFragment.resume();

        }
        mediaController.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearGestureInfo();
        mediaController.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //弹出去暂停
        isPlay = videoView.onActivityStop();
//        danmuFragment.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int position = videoView.getCurrentPosition();
        if (position != 0) {
            SubmitProgressService.startActionFoo(mContext, String.valueOf(position), String.valueOf(dataVo.getId()),vid);
        }
        videoView.destroy();
        EventBus.getDefault().removeAllStickyEvents();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mediaController.disable();
        saveLookVideo();

    }

    public static Intent newInstance(Context context, CoursesBeanVo o) {
        Intent intent = new Intent(context, NetBookMyInfomActivity.class);
        intent.putExtra(SERIALIZABLELIST, o);
        return intent;
    }

    public static Intent newIntent(Context context, PlayMode playMode, String vid) {
        return newIntent(context, playMode, vid, PolyvBitRate.ziDong.getNum());
    }

    public static Intent newIntent(Context context, PlayMode playMode, String vid, int bitrate) {
        return newIntent(context, playMode, vid, bitrate, false);
    }

    public static Intent newIntent(Context context, PlayMode playMode, String vid, int bitrate, boolean startNow) {
        return newIntent(context, playMode, vid, bitrate, startNow, false);
    }

    public static Intent newIntent(Context context, PlayMode playMode, String vid, int bitrate, boolean startNow,
                                   boolean isMustFromLocal) {
        Intent intent = new Intent(context, NetBookMyInfomActivity.class);
        intent.putExtra("playMode", playMode.getCode());
        intent.putExtra("value", vid);
        intent.putExtra("bitrate", bitrate);
        intent.putExtra("startNow", startNow);
        intent.putExtra("isMustFromLocal", isMustFromLocal);
        return intent;
    }

    public static void intentTo(Context context, PlayMode playMode, String vid) {
        intentTo(context, playMode, vid, PolyvBitRate.ziDong.getNum());
    }

    public static void intentTo(Context context, PlayMode playMode, String vid, int bitrate) {
        intentTo(context, playMode, vid, bitrate, false);
    }

    public static void intentTo(Context context, PlayMode playMode, String vid, int bitrate, boolean startNow) {
        intentTo(context, playMode, vid, bitrate, startNow, false);
    }

    public static void intentTo(Context context, PlayMode playMode, String vid, int bitrate, boolean startNow,
                                boolean isMustFromLocal) {
        context.startActivity(newIntent(context, playMode, vid, bitrate, startNow, isMustFromLocal));
    }

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_book_infom);
        initView();
    }*/

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        setContentView(R.layout.activity_net_mybook_infom);

        if (getIntent() != null) {
            dataVo = (CoursesBeanVo) getIntent().getSerializableExtra(SERIALIZABLELIST);
        }
        initView();
        initViewData();
        PolyvScreenUtils.generateHeight16_9(this);
        PolyvScreenUtils.initTitleBar(ll_title_bar, mRlPlaylayout, null);
        int playModeCode = getIntent().getIntExtra("playMode", PlayMode.portrait.getCode());
        PlayMode playMode = PlayMode.getPlayMode(playModeCode);
        if (playMode == null)
            playMode = PlayMode.portrait;
        //视频id
        String vid = getIntent().getStringExtra("value");
        int bitrate = getIntent().getIntExtra("bitrate", PolyvBitRate.ziDong.getNum());
        boolean startNow = getIntent().getBooleanExtra("startNow", false);
        boolean isMustFromLocal = getIntent().getBooleanExtra("isMustFromLocal", false);

        switch (playMode) {
            case landScape: //横屏
                ll_title_bar.setVisibility(View.GONE);
                mediaController.changeToLandscape();
                break;
            case portrait://竖屏
                ll_title_bar.setVisibility(View.VISIBLE);
                mediaController.changeToPortrait();
                break;
        }
        initData();
        EventBus.getDefault().register(this);
//        play("d740a56357c361f76cdd800b204e9800_d", 0, true, false);
    }

    private void initData() {
        if (dataVo != null) {
            helper = new PolyvVlmsHelper();
            mTvNetBookTitle.setText(dataVo.getName());
            List<String> list = ArrayToListUtil.arraytoList(mContext, R.array.net_mybook_title);
            mNetMagicIndicator.setBackgroundColor(Color.parseColor("#ffffff"));
            CommonNavigator commonNavigator = new CommonNavigator(this);
            commonNavigator.setScrollPivotX(0.25f);
            commonNavigator.setAdjustMode(true);
            MyNetBookIndicatorAdapter adapter = new MyNetBookIndicatorAdapter(list, mVpNetBar);
            mNetMagicIndicator.setNavigator(commonNavigator);
            commonNavigator.setAdapter(adapter);
            List<Fragment> fragments = creartFragment(list);
            MyTagPagerAdapter tagPagerAdapter = new MyTagPagerAdapter(getSupportFragmentManager(), fragments);
            mVpNetBar.setAdapter(tagPagerAdapter);
            mVpNetBar.setOffscreenPageLimit(4);
            ViewPagerHelper.bind(mNetMagicIndicator, mVpNetBar);
            if (!StringUtil.isEmpty(dataVo.getCoverimg())) {
                MyAppliction.getInstance().displayImages(mIvNetPlay, dataVo.getCoverimg(), false);
            }

        }
    }

    /**
     * 添加fragment
     *
     * @param list
     * @return
     */
    private List<Fragment> creartFragment(List<String> list) {
        if (list.size() < 2) {
            mNetMagicIndicator.setVisibility(View.GONE);
        }
        List<Fragment> fragments = new ArrayList<>();
        NetMyBokTableFragment tableFragment = NetMyBokTableFragment.newInstance(String.valueOf(dataVo.getId()));
        NetMyBookVualueFragment bookVualueFragment = NetMyBookVualueFragment.newInstance("", "");
        fragments.add(tableFragment);
        fragments.add(bookVualueFragment);
        return fragments;
    }

    /**
     * 播放视频
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onNetMainPlayId(NetMyPlayEvent event) {
        VideosBeanVo vo = event.getVo();
        L.e(vo.getVid());
        L.e("调用===========" + vo.getVid());
        vid = vo.getVid();
        mVideoid = vo.getVideoid();
        mPid = vo.getChapterid();
        mZid = vo.getVideoid();
        mTitleName = vo.getVideoname();
        play();
//        play(vo.getVid(), 3, true, false);
//        mRlPlaylayout.setVisibility(View.GONE);
    }

    /**
     * 播放视频
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onNetMainTryPlayId(NetMyPlayTrySeeEvent event) {
        VideosBeanVo vo = event.getVo();
        L.e(vo.getVid());
        mVideoid = vo.getVideoid();
        mPid = vo.getChapterid();
        mZid = vo.getVideoid();
        vid = vo.getVid();
        mTitleName = vo.getVideoname();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getBookTableList(BookTableEvent event) {
        List list = event.getArrary();
        if (list != null && list.size() > 0) {
            mTableList = list;
            if (mDownAdapter != null) {
                mDownAdapter.notifyDataSetChanged();
            }
        }

    }

    private void initView() {
        mContext = this;
        mIvBack = findViewById(R.id.iv_net_book_back);
        mIvBack.setOnClickListener(this);
        mLlNetPlayRoot = (LinearLayout) findViewById(R.id.ll_net_play_my_root);
        mLlNetPlayRoot.setOnClickListener(this);
        mIvNetBookPlay = (ImageView) findViewById(R.id.iv_net_my_book_play);
        mIvNetBookPlay.setOnClickListener(this);
        mIvNetMybookDown = (ImageView) findViewById(R.id.iv_net_icon_my_down);
        mIvNetMybookDown.setOnClickListener(this);
        mRlPlaylayout = (RelativeLayout) findViewById(R.id.rl_play_my_layout);
        mRlPlaylayout.setOnClickListener(this);
        mIvNetPlay = (ImageView) findViewById(R.id.iv_net_my_play);
        mTvNetBookTitle = (TextView) findViewById(R.id.tv_net_my_book_title);
        mTvNetBookTitle.setOnClickListener(this);
        mNetMagicIndicator = (MagicIndicator) findViewById(R.id.net_magic_my_indicator);
        mNetMagicIndicator.setOnClickListener(this);
        mVpNetBar = (NoScrollViewPager) findViewById(R.id.vp_net_my_bar);
        mVpNetBar.setOnClickListener(this);
        ll_title_bar =(LinearLayout) findViewById(R.id.activity_title_container);
        viewLayout = (RelativeLayout) findViewById(R.id.view_layout);
        videoView = (PolyvVideoView) findViewById(R.id.polyv_video_view);
        marqueeView = (PolyvMarqueeView) findViewById(R.id.polyv_marquee_view);
        mediaController = (PolyvPlayerMediaController) findViewById(R.id.polyv_player_media_controller);

        lightView = (PolyvPlayerLightView) findViewById(R.id.polyv_player_light_view);
        volumeView = (PolyvPlayerVolumeView) findViewById(R.id.polyv_player_volume_view);
        progressView = (PolyvPlayerProgressView) findViewById(R.id.polyv_player_progress_view);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        mediaController.initTitltBar(ll_title_bar, mRlPlaylayout, null);
        mediaController.initConfig(viewLayout);
        videoView.setMediaController(mediaController);
        videoView.setPlayerBufferingIndicator(loadingProgress);
        // 设置跑马灯
        videoView.setMarqueeView(marqueeView, marqueeItem = new PolyvMarqueeItem()
                .setStyle(PolyvMarqueeItem.STYLE_ROLL_FLICK) //样式
                .setDuration(10000) //时长
                .setText("POLYV Android SDK") //文本
                .setSize(16) //字体大小
                .setColor(Color.YELLOW) //字体颜色
                .setTextAlpha(70) //字体透明度
                .setInterval(1000) //隐藏时间
                .setLifeTime(1000) //显示时间
                .setTweenTime(1000) //渐隐渐现时间
                .setHasStroke(true) //是否有描边
                .setBlurStroke(true) //是否模糊描边
                .setStrokeWidth(3) //描边宽度
                .setStrokeColor(Color.MAGENTA) //描边颜色
                .setStrokeAlpha(70)); //描边透明度

    }


    private void initViewData() {
        videoView.setOpenAd(true);
        videoView.setOpenTeaser(true);
        videoView.setOpenQuestion(true);
        videoView.setOpenSRT(true);
        videoView.setOpenPreload(true, 2);
        videoView.setOpenMarquee(true);
        videoView.setAutoContinue(true);
        videoView.setNeedGestureDetector(true);

        videoView.setOnPreparedListener(new IPolyvOnPreparedListener2() {
            @Override
            public void onPrepared() {
                mediaController.preparedView();
                progressView.setViewMaxValue(videoView.getDuration());
                // 没开预加载在这里开始弹幕
                // danmuFragment.start();
            }
        });
        videoView.setOnVideoStatusListener(new IPolyvOnVideoStatusListener() {
            @Override
            public void onStatus(int status) {
                if (status < 60) {
                    Toast.makeText(NetBookMyInfomActivity.this, "状态错误 " + status, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, String.format("状态正常 %d", status));
                }
            }
        });

        videoView.setOnVideoPlayErrorListener(new IPolyvOnVideoPlayErrorListener2() {
            @Override
            public boolean onVideoPlayError(@PolyvPlayErrorReason.PlayErrorReason int playErrorReason) {
                String message = PolyvErrorMessageUtils.getPlayErrorMessage(playErrorReason);
                message += "(error code " + playErrorReason + ")";
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(NetBookMyInfomActivity.this);
                builder.setTitle("错误");
                builder.setMessage(message);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                if (videoView.getWindowToken() != null) {
                    builder.show();
                }
                return true;
            }
        });

        videoView.setOnErrorListener(new IPolyvOnErrorListener2() {
            @Override
            public boolean onError() {
                Toast.makeText(NetBookMyInfomActivity.this, "当前视频无法播放，请向管理员反馈(error code " + PolyvPlayErrorReason.VIDEO_ERROR + ")", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        videoView.setOnAdvertisementEventListener(new IPolyvOnAdvertisementEventListener2() {
            @Override
            public void onShow(PolyvADMatterVO adMatter) {
                Log.i(TAG, "开始播放视频广告");
            }

            @Override
            public void onClick(PolyvADMatterVO adMatter) {
                if (!TextUtils.isEmpty(adMatter.getAddrUrl())) {
                    try {
                        new URL(adMatter.getAddrUrl());
                    } catch (MalformedURLException e1) {
                        Log.e(TAG, PolyvSDKUtil.getExceptionFullMessage(e1, -1));
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(adMatter.getAddrUrl()));
                    startActivity(intent);
                }
            }
        });


        videoView.setOnQuestionAnswerTipsListener(new IPolyvOnQuestionAnswerTipsListener() {

            @Override
            public void onTips(@NonNull String msg) {
            }
        });


        videoView.setOnGestureLeftUpListener(new IPolyvOnGestureLeftUpListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("LeftUp %b %b brightness %d", start, end, videoView.getBrightness(NetBookMyInfomActivity.this)));
                int brightness = videoView.getBrightness(NetBookMyInfomActivity.this) + 5;
                if (brightness > 100) {
                    brightness = 100;
                }

                videoView.setBrightness(NetBookMyInfomActivity.this, brightness);
                lightView.setViewLightValue(brightness, end);
            }
        });

        videoView.setOnGestureLeftDownListener(new IPolyvOnGestureLeftDownListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("LeftDown %b %b brightness %d", start, end, videoView.getBrightness(NetBookMyInfomActivity.this)));
                int brightness = videoView.getBrightness(NetBookMyInfomActivity.this) - 5;
                if (brightness < 0) {
                    brightness = 0;
                }

                videoView.setBrightness(NetBookMyInfomActivity.this, brightness);
                lightView.setViewLightValue(brightness, end);
            }
        });

        videoView.setOnGestureRightUpListener(new IPolyvOnGestureRightUpListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("RightUp %b %b volume %d", start, end, videoView.getVolume()));
                // 加减单位最小为10，否则无效果
                int volume = videoView.getVolume() + 10;
                if (volume > 100) {
                    volume = 100;
                }

                videoView.setVolume(volume);
                volumeView.setViewVolumeValue(volume, end);
            }
        });

        videoView.setOnGestureRightDownListener(new IPolyvOnGestureRightDownListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("RightDown %b %b volume %d", start, end, videoView.getVolume()));
                // 加减单位最小为10，否则无效果
                int volume = videoView.getVolume() - 10;
                if (volume < 0) {
                    volume = 0;
                }

                videoView.setVolume(volume);
                volumeView.setViewVolumeValue(volume, end);
            }
        });

        videoView.setOnGestureSwipeLeftListener(new IPolyvOnGestureSwipeLeftListener() {

            @Override
            public void callback(boolean start, boolean end) {
                // 左滑事件
                Log.d(TAG, String.format("SwipeLeft %b %b", start, end));
                if (fastForwardPos == 0) {
                    fastForwardPos = videoView.getCurrentPosition();
                }

                if (end) {
                    if (fastForwardPos < 0)
                        fastForwardPos = 0;
                    videoView.seekTo(fastForwardPos);
                    if (videoView.isCompletedState()) {
                        videoView.start();
                    }
                    fastForwardPos = 0;
                } else {
                    fastForwardPos -= 10000;
                    if (fastForwardPos <= 0)
                        fastForwardPos = -1;
                }
                progressView.setViewProgressValue(fastForwardPos, videoView.getDuration(), end, false);
            }
        });

        videoView.setOnGestureSwipeRightListener(new IPolyvOnGestureSwipeRightListener() {

            @Override
            public void callback(boolean start, boolean end) {
                // 右滑事件
                Log.d(TAG, String.format("SwipeRight %b %b", start, end));
                if (fastForwardPos == 0) {
                    fastForwardPos = videoView.getCurrentPosition();
                }

                if (end) {
                    if (fastForwardPos > videoView.getDuration())
                        fastForwardPos = videoView.getDuration();
                    if (!videoView.isCompletedState()) {
                        videoView.seekTo(fastForwardPos);
                    } else if (videoView.isCompletedState() && fastForwardPos != videoView.getDuration()) {
                        videoView.seekTo(fastForwardPos);
                        videoView.start();
                    }
                    fastForwardPos = 0;
                } else {
                    fastForwardPos += 10000;
                    if (fastForwardPos > videoView.getDuration())
                        fastForwardPos = videoView.getDuration();
                }
                progressView.setViewProgressValue(fastForwardPos, videoView.getDuration(), end, true);
            }
        });

        videoView.setOnGestureClickListener(new IPolyvOnGestureClickListener() {
            @Override
            public void callback(boolean start, boolean end) {
                if (videoView.isInPlaybackState() && mediaController != null)
                    if (mediaController.isShowing())
                        mediaController.hide();
                    else
                        mediaController.show();
            }
        });


    }


    /**
     * 播放视频
     *
     * @param vid             视频id
     * @param bitrate         码率（清晰度）
     * @param startNow        是否现在开始播放视频
     * @param isMustFromLocal 是否必须从本地（本地缓存的视频）播放
     */
    public void play(final String vid, final int bitrate, boolean startNow, final boolean isMustFromLocal) {
        if (TextUtils.isEmpty(vid)) return;
        videoView.release();
        mediaController.hide();
        loadingProgress.setVisibility(View.GONE);
        progressView.resetMaxValue();
        if (startNow) {
            //调用setVid方法视频会自动播放
            videoView.setVid(vid, bitrate, isMustFromLocal);

        }
    }


    private void clearGestureInfo() {
        videoView.clearGestureInfo();
        progressView.hide();
        volumeView.hide();
        lightView.hide();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (PolyvScreenUtils.isLandscape(this) && mediaController != null) {
                mediaController.changeToPortrait();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_net_my_book_play://播放按钮
                if (StringUtil.isEmpty(vid)) {
                    T.showToast(mContext, "暂无课观看");
                    return;
                }
                play();
                break;
            case R.id.iv_net_icon_my_down://我的下载
                if (mTableList == null || mTableList.isEmpty()) {
                    T.showToast(mContext, "暂无课下载");
                    return;
                }
                showPopwindow();
                break;
            case R.id.iv_net_book_back:
                int position = videoView.getCurrentPosition();
//                String s = PolyvTimeUtils.generateTime(position);
                if (position != 0) {
                    SubmitProgressService.startActionFoo(mContext, String.valueOf(position), String.valueOf(dataVo.getId()),vid);
                }
                this.finish();
                break;
            default:

        }
    }

    private void play() {
        mRlPlaylayout.setVisibility(View.GONE);
        play(vid, 3, true, false);
        mediaController.setIsPlay(true);
        PolyvScreenUtils.IsPlay(true);
        saveLookVideo();
        EventBus.getDefault().postSticky(new VideoIdEvent(String.valueOf(mVideoid)));
    }

    private void saveLookVideo() {
        DbHelperAssist mUserDao = DbHelperAssist.getInstance();
        UserLookVideoVo vo = new UserLookVideoVo();
        vo.setKid(String.valueOf(dataVo.getId()));
        vo.setPid(String.valueOf(mPid));
        vo.setZid(String.valueOf(mZid));
        vo.setTitleName(mTitleName);
        int position = videoView.getCurrentPosition();
//                String s = PolyvTimeUtils.generateTime(position);
        vo.setProgress(String.valueOf(position));
        mUserDao.saveLookVideo(vo);
    }


    /**
     * 播放模式
     *
     * @author TanQu
     */
    public enum PlayMode {
        /**
         * 横屏
         */
        landScape(3),
        /**
         * 竖屏
         */
        portrait(4);

        private final int code;

        private PlayMode(int code) {
            this.code = code;
        }

        /**
         * 取得类型对应的code
         *
         * @return
         */
        public int getCode() {
            return code;
        }

        public static PlayMode getPlayMode(int code) {
            switch (code) {
                case 3:
                    return landScape;
                case 4:
                    return portrait;
            }

            return null;
        }
    }

    /**
     * 显示pop
     */
    private void showPopwindow() {
        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        mDao = DbHelperDownAssist.getInstance();
        popDown = new CommonPopupWindow(this, R.layout.pop_net_down_layout, ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenHeight * 0.7)) {
            private boolean isRuning = true;
            private Thread thread;
            private LinearLayout mLlPopDownDown;
            private Button mBtnPopDownRun;
            private Button mBtnPopDownLook;
            private Button mBtnPopDownAll;
            private Button mBtnPopDownSureAll;
            private Button mBtnPopDownCancel;
            private TextView mTvNetPopEmpty;
            private RecyclerView mRlvTableList;
            private ImageView mIvNetPopBack;
            private RadioButton mChbNetPopDownLiu;
            private RadioButton mChbNetPopDownGao;
            private RadioButton mChbNetPopDownChao;
            int bitrer = 3;
            boolean isSure = false;
            private DownVideoDb vo;
            private AlertDialog dialog;
            private Handler handler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.arg1 == 1) {
                        vo = (DownVideoDb) msg.obj;
//                      DbHelperDownAssist.getInstance().addDownItem(vo);
                        if (dialog != null) {
                            dialog.dismiss();
                            isRuning = false;
                            isShow(false, true, false, true, false);
                            mBtnPopDownSureAll.setText("全部缓存(" + vo.getDownlist().size() + ")");
                            mDownAdapter.notifyDataSetChanged();
                        } else {
                            isRuning = false;
//                            DbHelperDownAssist.getInstance().addDownItem(vo);
                            startDown(vo);
                            mBtnPopDownRun.setText("正在缓存(" + vo.getDownlist().size() + ")");
                            mDownAdapter.notifyDataSetChanged();

                        }
                    }
                }
            };

            @Override
            protected void initView() {
                View view = getContentView();
                mBtnPopDownLook = view.findViewById(R.id.btn_pop_down_look);
                mLlPopDownDown = view.findViewById(R.id.ll_pop_down_down);
                mBtnPopDownAll = view.findViewById(R.id.btn_pop_down_all);
                mBtnPopDownSureAll = view.findViewById(R.id.btn_pop_down_sure_all);
                mBtnPopDownCancel = view.findViewById(R.id.btn_pop_down_cancel);
                mBtnPopDownRun = view.findViewById(R.id.btn_pop_down_run);
                mRlvTableList = view.findViewById(R.id.rlv_table_list);
                mIvNetPopBack = view.findViewById(R.id.iv_net_pop_back);
                mChbNetPopDownLiu = view.findViewById(R.id.chb_net_pop_down_liu);
                mChbNetPopDownGao = view.findViewById(R.id.chb_net_pop_down_gao);
                mChbNetPopDownChao = view.findViewById(R.id.chb_net_pop_down_chao);
                mTvNetPopEmpty = view.findViewById(R.id.tv_net_pop_empty);
                if (mTableList == null || mTableList.isEmpty()) {
                    mTvNetPopEmpty.setVisibility(View.VISIBLE);
                    mRlvTableList.setVisibility(View.GONE);
                }
                isShow(true, false, true, false, false);
            }

            @Override
            protected void initEvent() {
                mIvNetPopBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popDown.getPopupWindow().dismiss();
                    }
                });
                bindAdapter();
                mChbNetPopDownChao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            bitrer = 3;
                        }
                    }
                });
                mChbNetPopDownGao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            bitrer = 2;
                        }
                    }
                });
                mChbNetPopDownLiu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            bitrer = 1;
                        }
                    }
                });
                //全部缓存确认
                mBtnPopDownSureAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isShow(true, false, true, false, false);
//                        DbHelperDownAssist.getInstance().addDownItem(vo);
                        startDown(vo);
                    }
                });
                //全选
                mBtnPopDownAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog = DialogUtil.showDialog(mContext, "", getStringWithId(R.string.loading));
                        GetNetBookService(mTableList, bitrer);
                    }
                });
                //取消按钮
                mBtnPopDownCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isShow(true, false, true, false, false);
                    }
                });
                //正在缓存
                mBtnPopDownRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popDown.getPopupWindow().dismiss();
                        Intent intent = NetBookDowningActivity.newInstance(mContext, String.valueOf(dataVo.getId()));
                        startActivity(intent);
                    }
                });
                //查看缓存
                mBtnPopDownLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popDown.getPopupWindow().dismiss();
                        Intent intent = NetBookDowningActivity.newInstance(mContext, String.valueOf(dataVo.getId()));
                        startActivity(intent);
                    }
                });


            }

            private void bindAdapter() {
                GridLayoutManager manager = new GridLayoutManager(mContext, 1);
                manager.setOrientation(GridLayoutManager.VERTICAL);
                mRlvTableList.setLayoutManager(manager);
                mDownAdapter = new NetMyDownTableAdapter(mContext, mTableList, dataVo.getId());
                mRlvTableList.setAdapter(mDownAdapter);
                mDownAdapter.setClickListener(new NetMyDownTableAdapter.onItemClickListener() {
                    @Override
                    public void onClickListener(ChaptersBeanVo obj, int position) {
                        //添加到数据库
                        List<ChaptersBeanVo> list = new ArrayList<>();
                        ChaptersBeanVo vo = new ChaptersBeanVo();
                        vo.setChapterid(obj.getChapterid());
                        vo.setChaptername(obj.getChaptername());
                        vo.setCourseid(obj.getCourseid());
                        List<VideosBeanVo> vos = new ArrayList<>();
                        VideosBeanVo beanVo = obj.getVideos().get(position);
                        vos.add(beanVo);
                        vo.setVideos(vos);
                        list.add(vo);
                        isRuning = true;
                        GetNetBookService(list, bitrer);


                    }
                });
            }

            /**
             * @param list
             * @param bitrate 编码
             */
            private void GetNetBookService(final List list, final int bitrate) {
                if (mTableList == null || mTableList.isEmpty()) {
                    return;
                }
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isRuning) {
                            DownVideoDb db = addListData(list, bitrate);
                            Message message = new Message();
                            message.arg1 = 1;
                            message.obj = db;
                            handler.sendMessage(message);
                        }
                    }
                });
                thread.start();
                //NetBookService.startActionBaz(mContext, list, bitrate, String.valueOf(dataVo.getId()));
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance = getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setBackgroundAlpha(1f, NetBookMyInfomActivity.this);
                    }
                });
            }

            private void isShow(boolean all, boolean cancel, boolean look, boolean sureall, boolean run) {
                mBtnPopDownAll.setVisibility(all ? View.VISIBLE : View.GONE);
                mBtnPopDownCancel.setVisibility(cancel ? View.VISIBLE : View.GONE);
                mBtnPopDownLook.setVisibility(look ? View.VISIBLE : View.GONE);
                mBtnPopDownSureAll.setVisibility(sureall ? View.VISIBLE : View.GONE);
                mBtnPopDownRun.setVisibility(run ? View.VISIBLE : View.GONE);
            }
        };

        popDown.showAtLocation(mLlNetPlayRoot, Gravity.BOTTOM, 0, 0);
        setBackgroundAlpha(0.5f, NetBookMyInfomActivity.this);
    }

    private void startDown(DownVideoDb vo) {
        for (int i = 0; i < vo.getDownlist().size(); i++) {
            DownVideoVo vo1 = vo.getDownlist().get(i);
            if (mDao != null && !mDao.isAdd(vo, vo1)) {
                mDao.addDownItem(vo);
                PolyvDownloader polyvDownloader = PolyvDownloaderManager.getPolyvDownloader(vid, Integer.parseInt(vo1.getBitRate()));
                polyvDownloader.setPolyvDownloadProressListener(new MyDownloadListener(mContext, vo, vo1));
                polyvDownloader.start(mContext);
            }
        }
    }


    /**
     * 添加数据
     *
     * @param table
     * @param bitrate
     * @return
     */
    public DownVideoDb addListData(List table, int bitrate) {
        List<ChaptersBeanVo> mDataList = (List<ChaptersBeanVo>) table;
        DownVideoDb db = new DownVideoDb();
        db.setKid(String.valueOf(dataVo.getId()));
        db.setKName(dataVo.getName());
        db.setUrlImg(dataVo.getCoverimg());
        List<DownVideoVo> list = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++) {
            ChaptersBeanVo vo = mDataList.get(i);
            List<VideosBeanVo> videos = vo.getVideos();
            if (videos != null && !videos.isEmpty()) {
                for (int j = 0; j < videos.size(); j++) {
                    final VideosBeanVo beanVo = videos.get(j);
                    try {
                        addData(bitrate, beanVo, list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        db.setDownlist(list);
//        DbHelperDownAssist.getInstance().addDownItem(db);
        return db;
    }

    /**
     * 设置背景颜色
     *
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(float bgAlpha, Context mContext) {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) mContext).getWindow().setAttributes(lp);

    }

    private void addData(int mBitrate, VideosBeanVo beanVo, List<DownVideoVo> db) throws JSONException {
        DownVideoVo vo = new DownVideoVo();
        PolyvSDKUtil sdkUtil = new PolyvSDKUtil();
        PolyvVideoVO video = sdkUtil.loadVideoJSON2Video(beanVo.getVid());
        //总时长
        if (video == null) {
            return;
        }
        String duration = video.getDuration();
        //大小
        long type = video.getFileSizeMatchVideoType(mBitrate);
        vo.setDuration(duration);

        vo.setBitRate(String.valueOf(mBitrate));

        vo.setFileSize(type);
        //视频id
        vo.setZid(String.valueOf(beanVo.getVideoid()));
        //篇id
        vo.setPid(String.valueOf(beanVo.getChapterid()));
        //保利视频id
        vo.setVid(beanVo.getVid());
        //视频名字
        vo.setTitle(beanVo.getVideoname());
        //该视频id
        vo.setVideoOid(String.valueOf(beanVo.getVideoid()));
        vo.setStatus("2");
        db.add(vo);
        Log.e("==视频信息==", mBitrate + "\naddData:总时长 " + duration + "\n"
                + "总大小" + type + "\n"
                + beanVo.getVideoname() + "\n"
                + beanVo.getVid() + "\n");

    }


}
