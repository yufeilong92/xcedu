package com.xuechuan.xcedu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.google.gson.Gson;
import com.xuechuan.xcedu.Event.BookTableEvent;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.adapter.MyOrderAdapter;
import com.xuechuan.xcedu.adapter.NetMyTableAdapter;
import com.xuechuan.xcedu.base.BaseFragment;
import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.mvp.contract.PerOrderContract;
import com.xuechuan.xcedu.mvp.model.NetBookInfomModelImpl;
import com.xuechuan.xcedu.mvp.model.PayModelImpl;
import com.xuechuan.xcedu.mvp.model.PerOrderModel;
import com.xuechuan.xcedu.mvp.presenter.NetBookInfomPresenter;
import com.xuechuan.xcedu.mvp.presenter.PayPresenter;
import com.xuechuan.xcedu.mvp.presenter.PerOrderPresenter;
import com.xuechuan.xcedu.mvp.view.PayUtilView;
import com.xuechuan.xcedu.mvp.view.PayView;
import com.xuechuan.xcedu.ui.me.MyOrderInfomActivity;
import com.xuechuan.xcedu.utils.DialogUtil;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.PayUtil;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.vo.ChaptersBeanVo;
import com.xuechuan.xcedu.vo.MyOrderVo;
import com.xuechuan.xcedu.vo.NetBookTableVo;
import com.xuechuan.xcedu.vo.OrderDetailVo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: MyAllOrderFragment
 * @Package com.xuechuan.xcedu.fragment
 * @Description: 全部
 * @author: L-BackPacker
 * @date: 2018/5/26 13:11
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/5/26
 */
public class MyAllOrderFragment extends BaseFragment implements PerOrderContract.View, PayUtilView {
    /**
     * 状态码
     */
    private static final String STATUS = "status";
    private static final String ARG_PARAM2 = "param2";

    private String mStatus;
    private String mParam2;
    private RecyclerView mRlvMyOrderContentAll;
    private XRefreshView mXfvContentOrderAll;

    private List mArrary;
    private ImageView mIvContentEmpty;
    private Context mContext;
    private long lastRefreshTime;
    private MyOrderAdapter adapter;
    private boolean isRefresh;
    private PerOrderPresenter mPresenter;
    private PayPresenter mPayPresenter;
    private PayUtil payUtil;
    private AlertDialog mPayDialog;

    public MyAllOrderFragment() {
    }

    public static MyAllOrderFragment newInstance(String param1, String param2) {
        MyAllOrderFragment fragment = new MyAllOrderFragment();
        Bundle args = new Bundle();
        args.putString(STATUS, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStatus = getArguments().getString(STATUS);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /*  @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
          View view = inflater.inflate(R.layout.fragment_my_all_order, container, false);
          initView(view);
          return view;
      }
  */
    @Override
    protected int initInflateView() {
        return R.layout.fragment_my_all_order;
    }

    @Override
    protected void initCreateView(View view, Bundle savedInstanceState) {
        initView(view);
        initData();
        clearData();
        bindAdapterData();
        initXrfresh();
        mXfvContentOrderAll.startRefresh();
    }

    private void initData() {
        mPresenter = new PerOrderPresenter();
        mPresenter.BasePresenter(new PerOrderModel(), this);
        payUtil = PayUtil.getInstance(mContext, getActivity());
        payUtil.init(this);
    }


    private void initView(View view) {
        mContext = getActivity();
        mRlvMyOrderContentAll = (RecyclerView) view.findViewById(R.id.rlv_my_order_content_all);
        mXfvContentOrderAll = (XRefreshView) view.findViewById(R.id.xfv_content_order_all);
        mIvContentEmpty = (ImageView) view.findViewById(R.id.iv_content_empty);
    }

    private void bindAdapterData() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        adapter = new MyOrderAdapter(mContext, mArrary);
        mRlvMyOrderContentAll.setLayoutManager(gridLayoutManager);
        mRlvMyOrderContentAll.setAdapter(adapter);
        adapter.setClickListener(new MyOrderAdapter.onItemClickListener() {
            @Override
            public void onClickListener(MyOrderVo.DatasBean obj, int position) {
   /*             Intent intent = MyOrderInfomActivity.newInstance(mContext, obj);
                startActivity(intent);*/
            }
        });
        adapter.setClickListener(new MyOrderAdapter.onItemCancelClickListener() {
            @Override
            public void onCancelClickListener(MyOrderVo.DatasBean obj, int position) {
                mPresenter.submitDelOrd(mContext, obj.getOrdernum(), DataMessageVo.CANCELORDER);
            }
        });
        adapter.setClickListener(new MyOrderAdapter.onItemDelClickListener() {
            @Override
            public void onDelClickListener(MyOrderVo.DatasBean obj, int position) {
                mPresenter.submitDelOrd(mContext, obj.getOrdernum(), DataMessageVo.DELETEORDER);
            }
        });
        adapter.setClickListener(new MyOrderAdapter.onItemPayClickListener() {
            @Override
            public void onPayClickListener(MyOrderVo.DatasBean obj, int position) {
                showDialog(obj, position);
            }
        });
    }

    private void showDialog(MyOrderVo.DatasBean obj, int position) {
        double discounts = obj.getDiscounts();
        double totalprice = obj.getTotalprice();
        final double v = totalprice - discounts;
        final ArrayList<Integer> list = new ArrayList<>();
        final List<OrderDetailVo> details = obj.getDetails();
        for (int i = 0; i < details.size(); i++) {
            list.add(details.get(i).getProductid());
        }
        DialogUtil instance = DialogUtil.getInstance();
        instance.showPayDialog(mContext);
        instance.setPayDialogClickListener(new DialogUtil.onItemPayDialogClickListener() {
            @Override
            public void onPayDialogClickListener(int obj, int position) {
                mPayDialog = DialogUtil.showDialog(mContext, "", getStrWithId(R.string.submit_loading));
                if (obj == 1) {//微信
                    payUtil.showDiaolog(mPayDialog);
                    payUtil.Submitfrom(PayUtil.WEIXIN, String.valueOf(v), list, null);
                } else if (obj == 2) {//支付宝
                    payUtil.Submitfrom(PayUtil.ZFB, String.valueOf(v), list, null);
                }
            }
        });

    }


    private void clearData() {
        if (mArrary == null) {
            mArrary = new ArrayList();
        } else {
            mArrary.clear();
        }
    }

    private void addListData(List<?> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (mArrary == null) {
            clearData();
        }
        mArrary.addAll(list);
    }

    private void initXrfresh() {
        mXfvContentOrderAll.restoreLastRefreshTime(lastRefreshTime);
        mXfvContentOrderAll.setPullLoadEnable(true);
        mXfvContentOrderAll.setAutoLoadMore(true);
        mXfvContentOrderAll.setPullRefreshEnable(true);
        mXfvContentOrderAll.setEmptyView(mIvContentEmpty);
        adapter.setCustomLoadMoreView(new XRefreshViewFooter(mContext));
        mXfvContentOrderAll.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                loadNewData();
            }


            @Override
            public void onLoadMore(boolean isSilence) {
                LoadMoreData();
            }


        });

    }

    private void loadNewData() {
        if (isRefresh) {
            return;
        }
        isRefresh = true;

        mPresenter.requestOrder(mContext, 1, mStatus);
    }

    private void LoadMoreData() {
        if (isRefresh) {
            return;
        }
        isRefresh = true;
        mPresenter.requestOrder(mContext, getNowPage() + 1, mStatus);
    }

    /**
     * 当前数据有几页
     *
     * @return
     */
    private int getNowPage() {
        if (mArrary == null || mArrary.isEmpty())
            return 0;
        if (mArrary.size() % DataMessageVo.CINT_PANGE_SIZE == 0)
            return mArrary.size() / DataMessageVo.CINT_PANGE_SIZE;
        else
            return mArrary.size() / DataMessageVo.CINT_PANGE_SIZE + 1;
    }

    @Override
    public void OrderSuccess(String con) {
        L.d("all============" + con);
        mXfvContentOrderAll.stopRefresh();
        mXfvContentOrderAll.setPullRefreshEnable(false);
        isRefresh = false;
        Gson gson = new Gson();
        MyOrderVo orderVo = gson.fromJson(con, MyOrderVo.class);
        if (orderVo.getStatus().getCode() == 200) {
            List<MyOrderVo.DatasBean> datas = orderVo.getDatas();
            clearData();
            if (datas != null && !datas.isEmpty()) {
                addListData(datas);
            } else {
                mXfvContentOrderAll.setLoadComplete(true);
                adapter.notifyDataSetChanged();
                return;
            }
            if (mArrary.size() < DataMessageVo.CINT_PANGE_SIZE || mArrary.size() == orderVo.getTotal().getTotal()) {
                mXfvContentOrderAll.setLoadComplete(true);
            } else {
                mXfvContentOrderAll.setPullLoadEnable(true);
                mXfvContentOrderAll.setLoadComplete(false);
            }
            adapter.notifyDataSetChanged();
        } else {
            isRefresh = false;
            L.e(orderVo.getStatus().getMessage());
        }

    }

    @Override
    public void OrderError(String con) {
        isRefresh = false;
    }

    @Override
    public void OrderSuccessMore(String con) {
        isRefresh = false;
        Gson gson = new Gson();
        MyOrderVo orderVo = gson.fromJson(con, MyOrderVo.class);
        if (orderVo.getStatus().getCode() == 200) {
            List<MyOrderVo.DatasBean> datas = orderVo.getDatas();
//                    clearData();
            if (datas != null && !datas.isEmpty()) {
                addListData(datas);
            } else {
                mXfvContentOrderAll.setLoadComplete(true);
                adapter.notifyDataSetChanged();
                return;
            }
            //判断是否能整除
            if (!mArrary.isEmpty() && mArrary.size() % DataMessageVo.CINT_PANGE_SIZE == 0) {
                mXfvContentOrderAll.setLoadComplete(false);
                mXfvContentOrderAll.setPullLoadEnable(true);
            } else {
                mXfvContentOrderAll.setLoadComplete(true);
            }
            adapter.notifyDataSetChanged();
        } else {
            isRefresh = false;
            L.e(orderVo.getStatus().getMessage());
        }
    }

    @Override
    public void OrderErrorMore(String con) {
        isRefresh = false;
    }

    @Override
    public void submitSuccess(String con) {

    }

    @Override
    public void submitError(String con) {

    }


    @Override
    public void PaySuccess(String type) {

    }

    @Override
    public void PayError(String type) {

    }

    @Override
    public void Dialog() {
        if (mPayDialog != null && mPayDialog.isShowing()) {
            mPayDialog.dismiss();
        }
    }
}
