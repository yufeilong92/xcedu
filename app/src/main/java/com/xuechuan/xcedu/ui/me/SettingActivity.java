package com.xuechuan.xcedu.ui.me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.umeng.debug.log.D;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.service.DownloadService;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.base.BaseActivity;
import com.xuechuan.xcedu.jg.RegisterTag;
import com.xuechuan.xcedu.mvp.contract.SettingViewContract;
import com.xuechuan.xcedu.mvp.model.SettingViewModel;
import com.xuechuan.xcedu.mvp.presenter.SettingViewPresenter;
import com.xuechuan.xcedu.net.MeService;
import com.xuechuan.xcedu.ui.LoginActivity;
import com.xuechuan.xcedu.ui.RegisterActivity;
import com.xuechuan.xcedu.utils.CProgressDialogUtils;
import com.xuechuan.xcedu.utils.CompareVersionUtil;
import com.xuechuan.xcedu.utils.HProgressDialogUtils;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.OkGoUpdateHttpUtil;
import com.xuechuan.xcedu.utils.SaveUUidUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.AppUpDataVo;

import java.io.File;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: SettingActivity
 * @Package com.xuechuan.xcedu.ui.user
 * @Description: 设置界面
 * @author: L-BackPacker
 * @date: 2018/5/22 9:11
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/5/22
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, SettingViewContract.View {

    private TextView mTvMSettingWeixin;
    private LinearLayout mLlMSettingBindWei;
    private TextView mTvMSettingPaw;
    private TextView mTvMSettingCode;
    private LinearLayout mLlMSettingUpdata;
    private TextView mTvMSettingAbout;
    private Button mBtnBSOut;
    private Context mContext;
    private SettingViewPresenter mPresenter;
    private boolean isShowDownloadProgress;
/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }*/

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initData() {
        int versionCode = Utils.getVersionCode(mContext);
        mTvMSettingCode.setText(versionCode + "");
        mPresenter = new SettingViewPresenter();
        mPresenter.initModelView(new SettingViewModel(), this);
    }

    private void initView() {
        mContext = this;
        mTvMSettingWeixin = (TextView) findViewById(R.id.tv_m_setting_weixin);
        mLlMSettingBindWei = (LinearLayout) findViewById(R.id.ll_m_setting_bindWei);
        mLlMSettingBindWei.setOnClickListener(this);
        mTvMSettingPaw = (TextView) findViewById(R.id.tv_m_setting_paw);
        mTvMSettingPaw.setOnClickListener(this);
        mTvMSettingCode = (TextView) findViewById(R.id.tv_m_setting_code);
        mLlMSettingUpdata = (LinearLayout) findViewById(R.id.ll_m_setting_updata);
        mLlMSettingUpdata.setOnClickListener(this);
        mTvMSettingAbout = (TextView) findViewById(R.id.tv_m_setting_about);
        mTvMSettingAbout.setOnClickListener(this);
        mBtnBSOut = (Button) findViewById(R.id.btn_b_s_out);
        mBtnBSOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_m_setting_about://关于
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                intent.putExtra(AddVauleActivity.CSTR_EXTRA_TITLE_STR, getStringWithId(R.string.about));
                startActivity(intent);
                break;
            case R.id.ll_m_setting_updata://更新
//                mPresenter.requestAppCode(mContext);
                upDataApp();
                break;
            case R.id.tv_m_setting_paw://修改密码
                startActivity(new Intent(SettingActivity.this, PawChangerActivity.class));
                break;
            case R.id.ll_m_setting_bindWei://绑定微信

                break;
            case R.id.btn_b_s_out:
                MyAppliction.getInstance().setUserInfom(null);
                SaveUUidUtil.getInstance().delectUUid();
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                //注册激光
                RegisterTag tag = RegisterTag.getInstance(getApplicationContext());
                tag.cancleTagAndAlias();
                this.finish();
                break;
        }
    }

    /**
     * 更新版本
     */
    private void upDataApp() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String hear = mContext.getResources().getString(R.string.app_content_heat);
        String updata = mContext.getResources().getString(R.string.http_upApp);
        String url = hear.concat(updata);
        new UpdateAppManager.Builder()
                .setActivity(this)
                .setHttpManager(new OkGoUpdateHttpUtil())
                .setUpdateUrl(url)
                .setPost(false)
                .setTargetPath(path)
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        try {
                            new JsonParser().parse(json);
                            OkGo.getInstance().cancelTag(mContext);
                            T.showToast(mContext, "服务器正在更新,请稍候点击");
                            return null;
                        } catch (JsonParseException e) {
                            L.e("数据异常");
                            e.printStackTrace();
                        }
                        Gson gson = new Gson();

                        AppUpDataVo vo = gson.fromJson(json, AppUpDataVo.class);
                        if (vo.getStatus().getCode() == 200) {
                            UpdateAppBean updateAppBean = new UpdateAppBean();
                            boolean isConstraint = false;
                            AppUpDataVo.DataBean data = vo.getData();
                            String versionCode = Utils.getVersionName(mContext);
                            int i = CompareVersionUtil.compareVersion(data.getVersion(), versionCode);
                            String updata = "No";
                            if (i == 0 || i == -1) {
                                updata = "No";
                            } else if (i == 1) {
                                updata = "Yes";
                                if (data.getType().equals("0")) {
                                    isConstraint = false;
                                } else if (data.getType().equals("1")) {
                                    isConstraint = true;
                                }
                            }
                            updateAppBean.setApkFileUrl(data.getUrl())
                                    //（必须）是否更新Yes,No
                                    .setUpdate(updata)
                                    .setNewVersion(data.getVersion())
                                    //（必须）下载地址
                                    .setApkFileUrl(data.getUrl())
                                    //（必须）更新内容
                                    .setUpdateLog(data.getDepict())
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(data.getAppsize())
                                    //是否强制更新，可以不设置
                                    .setConstraint(isConstraint);
                            return updateAppBean;
                        } else {
                            L.e(vo.getStatus().getMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
//                        super.hasNewApp(updateApp, updateAppManager);
                        showDiyDialog(updateApp, updateAppManager);
                    }

                    @Override
                    protected void onAfter() {
                        CProgressDialogUtils.cancelProgressDialog(SettingActivity.this);
                    }

                    @Override
                    protected void noNewApp(String error) {
                        T.showToast(mContext, getString(R.string.latest_version));
                    }

                    @Override
                    protected void onBefore() {
                        CProgressDialogUtils.showProgressDialog(SettingActivity.this);
                    }
                });
    }

    @Override
    public void AppCodeSuccess(String cont) {
        L.d("======请求app====" + cont);
        Gson gson = new Gson();
        AppUpDataVo vo = gson.fromJson(cont, AppUpDataVo.class);
        if (vo.getStatus().getCode() == 200) {
            AppUpDataVo.DataBean data = vo.getData();
        } else {
            L.e(vo.getStatus().getMessage());
        }
    }

    @Override
    public void AppCodeError(String msg) {
        L.d("======请求错误app====" + msg);
        L.e(msg);
    }

    /**
     * 自定义对话框
     *
     * @param updateApp
     * @param updateAppManager
     */
    private void showDiyDialog(final UpdateAppBean updateApp, final UpdateAppManager updateAppManager) {
        String targetSize = updateApp.getTargetSize();
        String updateLog = updateApp.getUpdateLog();

        String msg = "";

        if (!TextUtils.isEmpty(targetSize)) {
            msg = "新版本大小：" + targetSize + "\n\n";
        }

        if (!TextUtils.isEmpty(updateLog)) {
            msg += updateLog;
        }

        new AlertDialog.Builder(this)
                .setTitle(String.format("是否升级到%s版本？", updateApp.getNewVersion()))
                .setMessage(msg)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //显示下载进度
                        if (isShowDownloadProgress) {
                            updateAppManager.download(new DownloadService.DownloadCallback() {
                                @Override
                                public void onStart() {
                                    HProgressDialogUtils.showHorizontalProgressDialog(SettingActivity.this, "下载进度", false);
                                }

                                /**
                                 * 进度
                                 *
                                 * @param progress  进度 0.00 -1.00 ，总大小
                                 * @param totalSize 总大小 单位B
                                 */
                                @Override
                                public void onProgress(float progress, long totalSize) {
                                    HProgressDialogUtils.setProgress(Math.round(progress * 100));
                                }

                                /**
                                 *
                                 * @param total 总大小 单位B
                                 */
                                @Override
                                public void setMax(long total) {

                                }


                                @Override
                                public boolean onFinish(File file) {
                                    HProgressDialogUtils.cancel();
                                    return true;
                                }

                                @Override
                                public void onError(String msg) {
                                    Toast.makeText(SettingActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    HProgressDialogUtils.cancel();

                                }

                                @Override
                                public boolean onInstallAppAndAppOnForeground(File file) {
                                    return false;
                                }
                            });
                        } else {
                            //不显示下载进度
                            updateAppManager.download();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

}
