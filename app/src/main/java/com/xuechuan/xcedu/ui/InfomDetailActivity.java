package com.xuechuan.xcedu.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xuechuan.xcedu.Event.EvalueTwoEvent;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.adapter.InfomDetailAdapter;
import com.xuechuan.xcedu.base.BaseActivity;
import com.xuechuan.xcedu.base.BaseVo;
import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.mvp.contract.InfomDetailContract;
import com.xuechuan.xcedu.mvp.model.InfomDetailModel;
import com.xuechuan.xcedu.mvp.presenter.InfomDetailPresenter;
import com.xuechuan.xcedu.net.CurrencyService;
import com.xuechuan.xcedu.net.HomeService;
import com.xuechuan.xcedu.net.view.StringCallBackView;
import com.xuechuan.xcedu.utils.Defaultcontent;
import com.xuechuan.xcedu.utils.DialogBgUtil;
import com.xuechuan.xcedu.utils.DialogUtil;
import com.xuechuan.xcedu.utils.KeyboardUtil;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.ScreenShot;
import com.xuechuan.xcedu.utils.ShareUtils;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.ArticleBean;
import com.xuechuan.xcedu.vo.EvalueVo;
import com.xuechuan.xcedu.vo.InfomDetailVo;
import com.xuechuan.xcedu.vo.ResultVo;
import com.xuechuan.xcedu.weight.CommonPopupWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class InfomDetailActivity extends BaseActivity implements View.OnClickListener, InfomDetailContract.View {
    /**
     * 资讯id
     */
    private static final String MOID = "oid";
    /**
     * 类型
     */
    private static final String MTYPE = "USERTYPE";
    /**
     * 用户赞
     */
    private static final String SUPPER = "supper";

    /**
     * 地址
     */
    private static String URLPARAM = "urlparam";

    /**
     * 标题
     */
    private static String TITLE = "title";
    /**
     * 数据
     */
    private List mArray;
    private RecyclerView mRlvInfomdetailContent;
    private XRefreshView mXfvContent;
    private String mUrl;
    private String mTargetid;
    private String mType;
    private String mSupperNumber;
    private Context mContext;
    private InfomDetailAdapter adapter;
    private EditText mEtInfomContent;
    private Button mBtnInfomSend;
    private AlertDialog mDialog;
    private RelativeLayout mRlInfomLayout;
    private LinearLayout mLlInfomSend;
    private long lastRefreshTime;
    private static String ISSHOWEDITE = "misshowedite";
    private CheckBox chb_select;
    private LinearLayout mliSupper;
    private TextView tvNumber;
    private TextView tvHearNumber;
    private InfomDetailPresenter mPresenter;
    private ImageView mIvTitleMore;
    private XRefreshView mXfvContentDetail;
    private CommonPopupWindow popShare;
    private String mViewTitle;
    private String mTitle;

    /***
     *
     * @param context
     * @param url 网址
     * @param id  targid
     * @param usertype 类型
     * @param supper 点赞数
     * @return
     */
    public static Intent startInstance(Context context, String url, String id, String usertype, String supper,String title ) {
        Intent intent = new Intent(context, InfomDetailActivity.class);
        intent.putExtra(URLPARAM, url);
        intent.putExtra(MOID, id);
        intent.putExtra(MTYPE, usertype);
        intent.putExtra(SUPPER, supper);
        intent.putExtra(TITLE, title);
        return intent;
    }

    public static Intent startInstance(Context context, String id, String url, String usertype) {
        Intent intent = new Intent(context, InfomDetailActivity.class);
        intent.putExtra(URLPARAM, url);
        intent.putExtra(MOID, id);
        intent.putExtra(MTYPE, usertype);
        return intent;
    }


/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infom_detail);
        initView();
    }*/

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_infom_detail);
        if (getIntent() != null) {
            mUrl = getIntent().getStringExtra(URLPARAM);
            mTargetid = getIntent().getStringExtra(MOID);
            mType = getIntent().getStringExtra(MTYPE);
            mSupperNumber = getIntent().getStringExtra(SUPPER);
            mTitle = getIntent().getStringExtra(TITLE);

        }
        initView();
        clearData();
        bindAdapter();
        initxfvView();
        initData();
        reqesstEvaleData();
    }

    private void initData() {
        mEtInfomContent.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                } else {
                    Utils.hideInputMethod(mContext, mEtInfomContent);
                }
            }
        });
        mPresenter = new InfomDetailPresenter();
        mPresenter.initModelView(new InfomDetailModel(), this);
        KeyboardUtil keyboardUtil = KeyboardUtil.getInstance(mContext);
        keyboardUtil.setEditTextSendKey(mEtInfomContent);
        keyboardUtil.setSendClickListener(new KeyboardUtil.onKeySendClickListener() {
            @Override
            public void onSendClickListener() {
                doSubmitEvalue();
            }
        });

    }


    private void reqesstEvaleData() {
        if (StringUtil.isEmpty(mTargetid)) {
            return;
        }
        mDialog = DialogUtil.showDialog(mContext, "", getStringWithId(R.string.loading));
        HomeService bankService = new HomeService(mContext);
        bankService.requestArticleCommentList(mTargetid, 1, new StringCallBackView() {
            @Override
            public void onSuccess(Response<String> response) {
                mRlInfomLayout.setVisibility(View.VISIBLE);
                mLlInfomSend.setVisibility(View.VISIBLE);

                String message = response.body().toString();
                L.w(message);
                Gson gson = new Gson();
                EvalueVo vo = gson.fromJson(message, EvalueVo.class);
                if (vo.getStatus().getCode() == 200) {
                    List<EvalueVo.DatasBean> datas = vo.getDatas();
                    clearData();
                    if (datas != null && !datas.isEmpty()) {
                        addListData(datas);
                    } else {
                        mXfvContent.setLoadComplete(true);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    T.showToast(mContext, vo.getStatus().getMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                T.showToast(mContext, response.message());
            }
        });
    }

    private void initView() {
        mContext = this;
        mEtInfomContent = (EditText) findViewById(R.id.et_infom_content);
        mEtInfomContent.setOnClickListener(this);
        mBtnInfomSend = (Button) findViewById(R.id.btn_infom_send);
        mBtnInfomSend.setOnClickListener(this);
        mRlvInfomdetailContent = (RecyclerView) findViewById(R.id.rlv_infomdetail_content);
        mXfvContent = (XRefreshView) findViewById(R.id.xfv_content_detail);
        mRlInfomLayout = (RelativeLayout) findViewById(R.id.rl_infom_layout);
        mRlInfomLayout.setOnClickListener(this);
        mLlInfomSend = (LinearLayout) findViewById(R.id.ll_infom_send);
        mLlInfomSend.setOnClickListener(this);
        mIvTitleMore = (ImageView) findViewById(R.id.iv_title_more);
        mIvTitleMore.setOnClickListener(this);
    }

    private void initxfvView() {
        mXfvContent.restoreLastRefreshTime(lastRefreshTime);
        mXfvContent.setPullLoadEnable(true);
        mXfvContent.setAutoLoadMore(true);
        mXfvContent.setPullRefreshEnable(false);
        mXfvContent.setMoveForHorizontal(true);
    }


    /**
     * 资讯
     */
    private void bindAdapter() {
        adapter = new InfomDetailAdapter(mContext, mArray);
        //头
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.item_infom_webview, null);
        adapter.setHeaderView(view, mRlvInfomdetailContent);
        initWebView(view);
        bindHearData();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRlvInfomdetailContent.setLayoutManager(layoutManager);
        mRlvInfomdetailContent.setAdapter(adapter);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        mXfvContent.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onLoadMore(boolean isSilence) {
                loadMoreData();
            }
        });
      /*  adapter.setChbClickListener(new InfomDetailAdapter.onItemChbClickListener() {
            @Override
            public void onChbClickListener(Object obj, boolean isChecak, int position) {
                EvalueVo.DatasBean bean = (EvalueVo.DatasBean) obj;
                SuppertUtil util = SuppertUtil.getInstance(mContext);
                EvalueVo.DatasBean vo = (EvalueVo.DatasBean) mArray.get(position);
                vo.setIssupport(isChecak);
                if (isChecak) {
                    vo.setSupportcount(bean.getSupportcount() + 1);
                    util.submitSupport(String.valueOf(bean.getTargetid()), "true", DataMessageVo.USERTYPEAC);
                    adapter.notifyItemChanged(position);
                } else {
                    vo.setSupportcount(bean.getSupportcount() - 1);
                    util.submitSupport(String.valueOf(bean.getTargetid()), "false", DataMessageVo.USERTYPEAC);
                    adapter.notifyItemChanged(position);
                }
            }
        });
*/
        adapter.setClickListener(new InfomDetailAdapter.onItemClickListener() {
            @Override
            public void onClickListener(Object obj, int position) {
                EvalueVo.DatasBean bean = (EvalueVo.DatasBean) obj;
                EventBus.getDefault().postSticky(new EvalueTwoEvent(bean));
                Intent intent = EvalueTwoActivity.newInstance(mContext,
                        String.valueOf(bean.getTargetid()),
                        String.valueOf(bean.getId()),
                        DataMessageVo.USERTYPEAC
                        ,DataMessageVo.ARTICLE);
                startActivity(intent);
            }
        });

    }

    private void loadMoreData() {
        if (StringUtil.isEmpty(mTargetid)) {
            return;
        }
        HomeService bankService = new HomeService(mContext);
        bankService.requestArticleCommentList(mTargetid, getNowPage() + 1, new StringCallBackView() {
            @Override
            public void onSuccess(Response<String> response) {
                String message = response.body().toString();
                L.w(message);
                Gson gson = new Gson();
                EvalueVo vo = gson.fromJson(message, EvalueVo.class);
                if (vo.getStatus().getCode() == 200) {
                    List<EvalueVo.DatasBean> datas = vo.getDatas();
                    if (datas != null && !datas.isEmpty()) {
                        addListData(datas);
                    } else {
                        mXfvContent.setLoadComplete(true);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    if (!mArray.isEmpty() && mArray.size() % DataMessageVo.CINT_PANGE_SIZE == 0) {
                        mXfvContent.setLoadComplete(false);
                        mXfvContent.setPullLoadEnable(true);
                    } else {
                        mXfvContent.setLoadComplete(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    T.showToast(mContext, vo.getStatus().getMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {
                T.showToast(mContext, mContext.getResources().getString(R.string.net_error));
//                T.showToast(mContext, response.message());
            }
        });

    }

    /**
     * 绑定头布局
     *
     * @param view
     */
    int integer;

    private void bindHearData() {
        tvHearNumber.setVisibility(View.VISIBLE);
        tvHearNumber.setText("评论(" + mArray.size() + ")");
        mliSupper.setVisibility(View.VISIBLE);
        integer = Integer.parseInt(mSupperNumber);
        chb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                L.e(isChecked + "是否点赞");
                if (!chb_select.isPressed()) return;
                if (isChecked) {
                    requestSupper("true");
                    integer += 1;
                    chb_select.setText(integer + "");
                } else {
                    integer -= 1;
                    requestSupper("false");
                    chb_select.setText(integer + "");
                }
            }
        });
        chb_select.setText(mSupperNumber);
    }

    /**
     * c初始化web
     *
     * @param view
     */
    private void initWebView(View view) {
        chb_select = view.findViewById(R.id.chb_iofom_detial_supper);
        mliSupper = view.findViewById(R.id.li_supperNumber);
        tvNumber = view.findViewById(R.id.tv_iofom_detail_suppernumber);
        tvHearNumber = view.findViewById(R.id.tv_h_evalue);
        WebView webview = view.findViewById(R.id.web_infom_detail);
        final LinearLayout li = view.findViewById(R.id.ll_webview_after);
        li.setVisibility(View.VISIBLE);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        //设置自适应屏幕，两者合用
        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setDisplayZoomControls(true); //隐藏原生的缩放控件
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webview.loadUrl(mUrl);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) return false;

                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url);
                        return true;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                li.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                li.setVisibility(View.VISIBLE);
                mPresenter.requestGetDetail(mContext, mTargetid);
            }
        });
    }

    private void clearData() {
        if (mArray == null) {
            mArray = new ArrayList();
        } else {
            mArray.clear();
        }
    }

    private void addListData(List<?> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mArray == null) {
            clearData();
        }
        mArray.addAll(list);
    }

    /**
     * 点赞
     *
     * @param isSupper
     */
    private void requestSupper(String isSupper) {
        if (StringUtil.isEmpty(mTargetid)) {
            return;
        }
        CurrencyService currencyService = new CurrencyService(mContext);
        currencyService.subimtSpport(mTargetid, isSupper, DataMessageVo.USERTYPEA, new StringCallBackView() {
            @Override
            public void onSuccess(Response<String> response) {
                String s = response.body().toString();
                Gson gson = new Gson();
                ResultVo vo = gson.fromJson(s, ResultVo.class);
                if (vo.getStatus().getCode() == 200) {
                    T.showToast(mContext, getString(R.string.suppr_suc));
                } else {
                    L.e(s);
                    T.showToast(mContext, getStringWithId(R.string.net_error));
                }
            }

            @Override
            public void onError(Response<String> response) {
                L.e(response.message());
                T.showToast(mContext, getStringWithId(R.string.net_error));
            }
        });
    }

    /**
     * 当前数据有几页
     *
     * @return
     */
    private int getNowPage() {
        if (mArray == null || mArray.isEmpty())
            return 0;
        if (mArray.size() % DataMessageVo.CINT_PANGE_SIZE == 0)
            return mArray.size() / DataMessageVo.CINT_PANGE_SIZE;
        else
            return mArray.size() / DataMessageVo.CINT_PANGE_SIZE + 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_infom_send:
                doSubmitEvalue();
                break;
            case R.id.iv_title_more:
                showShareLayout();
                break;
        }
    }

    private void doSubmitEvalue() {
        String textStr = getTextStr(mEtInfomContent);
        if (StringUtil.isEmpty(textStr)) {
            T.showToast(mContext, getString(R.string.content_is_empty));
            return;
        }
        Utils.hideInputMethod(mContext, mEtInfomContent);
        submit(textStr);
    }

    private void submit(final String content) {
        CurrencyService currencyService = new CurrencyService(mContext);
        currencyService.setIsShowDialog(true);
        currencyService.setDialogContext(mContext, "", getString(R.string.submit_loading));
        currencyService.submitConmment(mTargetid, content, null, DataMessageVo.USERTYOEARTICLE, new StringCallBackView() {
            @Override
            public void onSuccess(Response<String> response) {
                String message = response.body().toString();
                L.d(message);
                Gson gson = new Gson();
                BaseVo vo = gson.fromJson(message, BaseVo.class);
                if (vo.getStatus().getCode() == 200) {
                    T.showToast(mContext, getString(R.string.evelua_sucee));
                    mEtInfomContent.setText(null);
                } else {
                    T.showToast(mContext, vo.getStatus().getMessage());
                }
            }

            @Override
            public void onError(Response<String> response) {

            }
        });
    }

    @Override
    public void GetDetailSuccess(String con) {
        Gson gson = new Gson();
        InfomDetailVo vo = gson.fromJson(con, InfomDetailVo.class);
        if (vo.getStatus().getCode() == 200) {
            ArticleBean data = vo.getData();
            bindData(data);
        } else {
            L.e(vo.getStatus().getMessage());
        }
    }

    private void bindData(ArticleBean data) {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        integer = data.getSupportcount();
        chb_select.setChecked(data.isIssupport());
        bindHearData();
    }

    /**
     * 分享布局
     */
    private void showShareLayout() {
        popShare = new CommonPopupWindow(this, R.layout.pop_item_share, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

            private TextView qqzon;
            private TextView qq;
            private TextView weibo;
            private TextView circle;
            private TextView weixin;

            @Override
            protected void initView() {
                View view = getContentView();
                weixin = (TextView) view.findViewById(R.id.tv_pop_weixin_share);
                circle = (TextView) view.findViewById(R.id.tv_pop_crile_share);
                weibo = (TextView) view.findViewById(R.id.tv_pop_weibo_share);
                qq = (TextView) view.findViewById(R.id.tv_pop_qq_share);
                qqzon = (TextView) view.findViewById(R.id.tv_pop_qqzon_share);
            }

            @Override
            protected void initEvent() {
                qq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        String pic = ScreenShot.savePic(ScreenShot.getBitmapByView(mSloViewShow));
                        ShareUtils.shareWeb(InfomDetailActivity.this, mUrl,mTitle
                                , "","", R.mipmap.appicon
                                , SHARE_MEDIA.QQ);
//                        ShareUtils.shareImg(InfomDetailActivity.this, mResultData.getQuestion(),
//                                pic, SHARE_MEDIA.QQ);
                        getPopupWindow().dismiss();
                    }
                });
                qqzon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String pic = ScreenShot.savePic(ScreenShot.getBitmapByView(mSloViewShow));
                        ShareUtils.shareWeb(InfomDetailActivity.this, mUrl,mTitle
                                , "","", R.mipmap.appicon, SHARE_MEDIA.QZONE
                        );
//                        ShareUtils.shareImg(InfomDetailActivity.this, mResultData.getQuestion(),
//                                pic, SHARE_MEDIA.QZONE);
                        getPopupWindow().dismiss();
                    }
                });
                weibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String pic = ScreenShot.savePic(ScreenShot.getBitmapByView(mSloViewShow));
                        ShareUtils.shareWeb(InfomDetailActivity.this,mUrl, mTitle
                                , "","", R.mipmap.appicon
                                , SHARE_MEDIA.SINA
                        );
//                        ShareUtils.shareImg(InfomDetailActivity.this, mResultData.getQuestion(),
//                                pic, SHARE_MEDIA.SINA);
                        getPopupWindow().dismiss();
                    }
                });
                weixin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String pic = ScreenShot.savePic(ScreenShot.getBitmapByView(mSloViewShow));
                        ShareUtils.shareWeb(InfomDetailActivity.this, mUrl, mTitle
                                , "","", R.mipmap.appicon, SHARE_MEDIA.WEIXIN
                        );
//                        ShareUtils.shareImg(InfomDetailActivity.this, mResultData.getQuestion(),
//                                pic, SHARE_MEDIA.WEIXIN);
                        getPopupWindow().dismiss();
                    }
                });
                circle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String pic = ScreenShot.savePic(ScreenShot.getBitmapByView(mSloViewShow));
                        ShareUtils.shareWeb(InfomDetailActivity.this, mUrl,mTitle
                                , "","", R.mipmap.appicon, SHARE_MEDIA.WEIXIN_CIRCLE
                        );
//                        ShareUtils.shareImg(InfomDetailActivity.this, mResultData.getQuestion(),
//                                pic, SHARE_MEDIA.WEIXIN_CIRCLE);
                        getPopupWindow().dismiss();
                    }
                });
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                PopupWindow instance = getPopupWindow();
                instance.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        DialogBgUtil.setBackgroundAlpha(1f, InfomDetailActivity.this);
                    }
                });
            }
        };
        popShare.showAtLocation(mRlInfomLayout, Gravity.BOTTOM, 0, 0);
        DialogBgUtil.setBackgroundAlpha(0.5f, InfomDetailActivity.this);
    }

    @Override
    public void GetDetailError(String con) {

    }

    @Override
    public void EvalueMoreDetail(String con) {

    }

    @Override
    public void EvalueMoreDetailErr(String con) {

    }

    @Override
    public void EvalueDetail(String con) {

    }

    @Override
    public void EvalueDetailErr(String con) {

    }
}
