package com.xuechuan.xcedu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xuechuan.xcedu.event.ShowItemEvent;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.adapter.MyTagPagerAdapter;
import com.xuechuan.xcedu.base.BaseActivity;
import com.xuechuan.xcedu.db.DbHelp.DBHelper;
import com.xuechuan.xcedu.fragment.BankFragment;
import com.xuechuan.xcedu.fragment.HomesFragment;
import com.xuechuan.xcedu.fragment.NetFragment;
import com.xuechuan.xcedu.fragment.PersionalFragment;
import com.xuechuan.xcedu.ui.AgreementActivity;
import com.xuechuan.xcedu.ui.InfomDetailActivity;
import com.xuechuan.xcedu.utils.ArrayToListUtil;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.vo.VideoSettingVo;
import com.xuechuan.xcedu.weight.NoScrollViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: HomeActivity
 * @Package com.xuechuan.xcedu
 * @Description: 主页
 * @author: L-BackPacker
 * @date: 2018/4/17 8:30
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/4/17
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<Fragment> mFragmentLists;
    private FragmentManager mSfm;
    private Context mContext;
    /**
     * 填充的布局
     */
    private int mFragmentLayout = R.id.fl_content;
    private static String PARAMS = "Params";
    private static String TYPE = "type";
    public final static String BOOK = "1";
    public final static String VIDEO = "2";
    private String mType;
    private NoScrollViewPager mViewpageContetn;
    private MagicIndicator mMagicindicatorHome;
    public final static String LOGIN_HOME = "loginhome";
    private String mLoginType;
    public final static String mHomeMeType = "4";
    public final static String Type = "4";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(mContext);
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    public static void newInstance(Context context, String params1) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(PARAMS, params1);
        context.startActivity(intent);
    }

    public static void startInstance(Context context, String type) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }


/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }*/

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        if (getIntent() != null) {
            mType = getIntent().getStringExtra(TYPE);
            mLoginType = getIntent().getStringExtra(PARAMS);
        }
        DBHelper.initDb(MyAppliction.getInstance());
        initView();
        initData();
        initMagicIndicator1();
 /*       if (!StringUtil.isEmpty(mType)) {
            if (mType.equals(BOOK)) {
                mViewpageContetn.setCurrentItem(1);
            } else if (mType.equals(VIDEO)) {
                mViewpageContetn.setCurrentItem(2);
            }
        }*/
        doShareActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void MianSelectShowItem(ShowItemEvent event) {
        if (mViewpageContetn != null)
            mViewpageContetn.setCurrentItem(0);
        doShareActivity();

    }

    private void doShareActivity() {
        if (!StringUtil.isEmpty(MyAppliction.getInstance().getIsAtricle())) {
            if (MyAppliction.getInstance().getIsAtricle().equals("0")) {
                doIntentAct(new Infom(), MyAppliction.getInstance().getShareParems());
                return;
            }
            if (MyAppliction.getInstance().getIsAtricle().equals("1")) {
                doIntentAct(new WenZhang(), MyAppliction.getInstance().getShareParems());
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!StringUtil.isEmpty(mType)) {
            if (mType.equals(BOOK)) {
                mViewpageContetn.setCurrentItem(1);
            } else if (mType.equals(VIDEO)) {
                mViewpageContetn.setCurrentItem(2);
            }
        }/* else if (!StringUtil.isEmpty(mLoginType) && mLoginType.equals(LOGIN_HOME)) {
            mViewpageContetn.setCurrentItem(0);
        }*/ else if (!StringUtil.isEmpty(mType) && mType.equals(mHomeMeType)) {
            mViewpageContetn.setCurrentItem(3);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoginType = "";
        mType = "";
    }

    private void initData() {
        List<Fragment> fragments = getcreateFragment();
        MyTagPagerAdapter adapter = new MyTagPagerAdapter(getSupportFragmentManager(), fragments);
        mViewpageContetn.setAdapter(adapter);
        requestVideoSetting();

    }

    private void requestVideoSetting() {
        String videoSetting = mContext.getResources().getString(R.string.http_video_setting);
        String http = mContext.getResources().getString(R.string.app_content_heat);
        String concat = http.concat(videoSetting);
        OkGo.<String>get(concat)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String com = response.body().toString();
                        L.e("视频设置==" + com);
                        Gson gson = new Gson();
                        VideoSettingVo vo = gson.fromJson(com, VideoSettingVo.class);
                        if (vo.getStatus().getCode() == 200) {
                            if (vo.getData() == null) {
                                return;
                            }
                            MyAppliction.getInstance().saveVideoSetting(vo.getData());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void initMagicIndicator1() {
        final ArrayList<String> list = ArrayToListUtil.arraytoList(mContext, R.array.home_order_title);
        mMagicindicatorHome.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(context);
                View customLayout = LayoutInflater.from(context).inflate(R.layout.simple_pager_title_layout, null);
                final ImageView radioButton = (ImageView) customLayout.findViewById(R.id.rdb_home);
                final TextView titleText = (TextView) customLayout.findViewById(R.id.title_text);
                if (index == 0) {
                    radioButton.setImageResource(R.drawable.ic_tab_home_n);
                } else if (index == 1) {
                    radioButton.setImageResource(R.drawable.ic_home_tab_bank_n);
                } else if (index == 2) {
                    radioButton.setImageResource(R.drawable.ic_home_tab_course_n);
                } else if (index == 3) {
                    radioButton.setImageResource(R.drawable.ic_home_tab_mine_n);
                }
                titleText.setText(list.get(index));
                commonPagerTitleView.setContentView(customLayout);
                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {

                    @Override
                    public void onSelected(int index, int totalCount) {
                        switch (index) {
                            case 0:
                                radioButton.setImageResource(R.drawable.ic_tab_home_s);
                                break;
                            case 1:
                                radioButton.setImageResource(R.drawable.ic_tab_bank_s);
                                break;
                            case 2:
                                radioButton.setImageResource(R.drawable.ic_home_tab_course_s);
                                break;
                            case 3:
                                radioButton.setImageResource(R.drawable.ic_home_tab_mine_s);
                                break;
                        }
                        titleText.setTextColor(getResources().getColor(R.color.red_text));
                    }

                    @Override
                    public void onDeselected(int index, int totalCount) {
                        switch (index) {
                            case 0:
                                radioButton.setImageResource(R.drawable.ic_tab_home_n);
                                break;
                            case 1:
                                radioButton.setImageResource(R.drawable.ic_home_tab_bank_n);
                                break;
                            case 2:
                                radioButton.setImageResource(R.drawable.ic_home_tab_course_n);
                                break;
                            case 3:
                                radioButton.setImageResource(R.drawable.ic_home_tab_mine_n);
                                break;
                        }
                        titleText.setTextColor(getResources().getColor(R.color.text_fu_color));
                    }

                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
//                        titleImg.setScaleX(1.3f + (0.8f - 1.3f) * leavePercent);
//                        titleImg.setScaleY(1.3f + (0.8f - 1.3f) * leavePercent);
                    }

                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
//                        titleImg.setScaleX(0.8f + (1.3f - 0.8f) * enterPercent);
//                        titleImg.setScaleY(0.8f + (1.3f - 0.8f) * enterPercent);
                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewpageContetn.setCurrentItem(index);
                    }
                });

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        mMagicindicatorHome.setNavigator(commonNavigator);
        mViewpageContetn.setOffscreenPageLimit(4);
        ViewPagerHelper.bind(mMagicindicatorHome, mViewpageContetn);
        mViewpageContetn.setCurrentItem(0);
    }


    protected void initView() {
        mContext = this;
        mViewpageContetn = (NoScrollViewPager) findViewById(R.id.viewpage_contetn);
        mViewpageContetn.setOnClickListener(this);
        mViewpageContetn.setPagingEnabled(false);
        mMagicindicatorHome = (MagicIndicator) findViewById(R.id.magicindicator_home);
        mMagicindicatorHome.setOnClickListener(this);
    }


    private void addFragmentData() {
        mFragmentLists = new ArrayList<>();
//        HomeFragment homeFragment = new HomeFragment();
        HomesFragment homeFragment = new HomesFragment();
        BankFragment bankFragment = new BankFragment();
        NetFragment netFragment = new NetFragment();
        PersionalFragment persionalFragment = new PersionalFragment();
        mFragmentLists.add(homeFragment);
        mFragmentLists.add(bankFragment);
        mFragmentLists.add(netFragment);
        mFragmentLists.add(persionalFragment);
        mSfm = getSupportFragmentManager();
        for (int i = 0; i < mFragmentLists.size(); i++) {
            FragmentTransaction transaction = mSfm.beginTransaction();
            Fragment fragment = mFragmentLists.get(i);
            transaction.add(mFragmentLayout, fragment).hide(fragment).commit();
        }
        FragmentTransaction transaction = mSfm.beginTransaction();
        transaction.show(homeFragment).commit();
    }

    @Override
    public void onClick(final View v) {

    }

    public List<Fragment> getcreateFragment() {
        ArrayList<Fragment> list = new ArrayList<>();
        HomesFragment homeFragment = new HomesFragment();
        BankFragment bankFragment = new BankFragment();
        NetFragment netFragment = new NetFragment();
        PersionalFragment persionalFragment = new PersionalFragment();
        list.add(homeFragment);
        list.add(bankFragment);
        list.add(netFragment);
        list.add(persionalFragment);
        return list;
    }


}
