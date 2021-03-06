package com.xuechuan.xcedu.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.google.gson.Gson;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.adapter.MyOrderAdapter;
import com.xuechuan.xcedu.base.BaseFragment;
import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.mvp.contract.PerOrderContract;
import com.xuechuan.xcedu.mvp.model.PerOrderModel;
import com.xuechuan.xcedu.mvp.presenter.PayPresenter;
import com.xuechuan.xcedu.mvp.presenter.PerOrderPresenter;
import com.xuechuan.xcedu.mvp.view.PayUtilView;
import com.xuechuan.xcedu.ui.me.DelectSuceessActivity;
import com.xuechuan.xcedu.utils.DialogUtil;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.PayUtil;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.vo.MyOrderVo;
import com.xuechuan.xcedu.vo.OrderDetailVo;
import com.xuechuan.xcedu.vo.ResultVo;

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
    private RecyclerView mRlvMyOrderContentAll;
    private XRefreshView mXfvContentOrderAll;

    private List mArrary;
    private ImageView mIvContentEmpty;
    private Context mContext;
    private long lastRefreshTime;
    private MyOrderAdapter adapter;
    private boolean isRefresh;
    private PerOrderPresenter mPresenter;
    private PayUtil payUtil;
    private AlertDialog mPayDialog;
    /**
     * 取消项的位置
     */
    private int mCancelpostion = -1;
    /**
     * 删除项的位置
     */
    private int mDelPostion = -1;

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
//        mXfvContentOrderAll.startRefresh();
    }

    private void initData() {
        mPresenter = new PerOrderPresenter();
        mPresenter.BasePresenter(new PerOrderModel(), this);
        payUtil = PayUtil.getInstance(mContext, getActivity());
        payUtil.init(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mXfvContentOrderAll.startRefresh();
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

                showData(obj, position, false);
            }
        });
        adapter.setClickListener(new MyOrderAdapter.onItemDelClickListener() {
            @Override
            public void onDelClickListener(MyOrderVo.DatasBean obj, int position) {
                showData(obj, position, true);
//                mDelPostion = position;
//                mPresenter.submitDelOrd(mContext, obj.getOrdernum(), DataMessageVo.DELETEORDER);
            }
        });
        adapter.setClickListener(new MyOrderAdapter.onItemPayClickListener() {
            @Override
            public void onPayClickListener(MyOrderVo.DatasBean obj, int position) {
                showDialog(obj, position);
            }
        });
    }

    /**
     * 取消订单
     *
     * @param obj
     * @param position
     */
    private void showData(final MyOrderVo.DatasBean obj, final int position, final boolean isDel) {
        DialogUtil dialogUtil = DialogUtil.getInstance();
        dialogUtil.showTitleDialog(mContext, getString(R.string.iscancelorder), getStrWithId(R.string.sure)
                , getStrWithId(R.string.cancel), true);
        dialogUtil.setTitleClickListener(new DialogUtil.onTitleClickListener() {
            @Override
            public void onSureClickListener() {
                if (isDel) {
                    mDelPostion = position;
                } else {
                    mCancelpostion = position;
                }
                mPresenter.submitDelOrd(mContext, obj.getOrdernum(), isDel ? DataMessageVo.DELETEORDER : DataMessageVo.CANCELORDER);
            }

            @Override
            public void onCancelClickListener() {
            }
        });

    }

    private void showDialog(final MyOrderVo.DatasBean data, int position) {
//        double discounts = data.getDiscounts();
//        double totalprice = data.getTotalprice();
//        final ArrayList<Integer> list = new ArrayList<>();
//        final List<OrderDetailVo> details = data.getDetails();
//        for (int i = 0; i < details.size(); i++) {
//            list.add(details.get(i).getProductid());
//        }
        DialogUtil instance = DialogUtil.getInstance();
        instance.showPayDialog(mContext);
        instance.setPayDialogClickListener(new DialogUtil.onItemPayDialogClickListener() {
            @Override
            public void onPayDialogClickListener(int obj, int position) {
                mPayDialog = DialogUtil.showDialog(mContext, "", getStrWithId(R.string.submit_loading));
                payUtil.showDiaolog(mPayDialog);
                if (obj == 1) {//微信
                    payUtil.SubmitfromPay(PayUtil.WEIXIN, data.getOrdernum());
                } else if (obj == 2) {//支付宝
                    payUtil.SubmitfromPay(PayUtil.ZFB, data.getOrdernum());
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
        mPresenter.requestOrderMore(mContext, getNowPage() + 1, mStatus);
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
            if(!mArrary.isEmpty()&&mArrary.size()%DataMessageVo.CINT_PANGE_SIZE==0) {
                // 设置是否可以上拉加载
                mXfvContentOrderAll.setPullLoadEnable(true);
                mXfvContentOrderAll.setLoadComplete(false);
            }
            else
                mXfvContentOrderAll.setLoadComplete(true);
//            if ( mArrary.size() == orderVo.getTotal().getTotal()) {
//                mXfvContentOrderAll.setPullRefreshEnable(true);
//                mXfvContentOrderAll.setLoadComplete(false);
//            } else {
//            mXfvContentOrderAll.setPullLoadEnable(true);
//            mXfvContentOrderAll.setLoadComplete(false);
//            }
            adapter.notifyDataSetChanged();
        } else {
            isRefresh = false;
            L.e(orderVo.getStatus().getMessage());
        }

    }

    @Override
    public void OrderError(String con) {
        isRefresh = false;
        T.showToast(mContext,getStrWithId(R.string.net_error));

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
        T.showToast(mContext,getStrWithId(R.string.net_error));

    }

    @Override
    public void submitSuccess(String con) {
        Gson gson = new Gson();
        ResultVo vo = gson.fromJson(con, ResultVo.class);
        if (vo.getStatus().getCode() == 200) {
            if (mDelPostion != -1 && mDelPostion >= 0) {
                mArrary.remove(mDelPostion);
                mRlvMyOrderContentAll.setItemAnimator(new DefaultItemAnimator());
                adapter.notifyDataSetChanged();
                mDelPostion = -1;
                DelectSuceessActivity.newInstance(mContext, DelectSuceessActivity.DELECTSUCCESS);
            }
            if (mCancelpostion != -1 && mCancelpostion >= 0) {
                mArrary.remove(mCancelpostion);
                mRlvMyOrderContentAll.setItemAnimator(new DefaultItemAnimator());
                adapter.notifyDataSetChanged();
                mCancelpostion = -1;
                DelectSuceessActivity.newInstance(mContext, DelectSuceessActivity.CANCELSUCCESS);
            }
        } else {
            L.e(vo.getStatus().getMessage());
        }

    }

    @Override
    public void submitError(String con) {
        T.showToast(mContext,getStrWithId(R.string.net_error));

    }


    @Override
    public void PaySuccess(String type) {
        if (mXfvContentOrderAll != null) {
            mXfvContentOrderAll.startRefresh();
        }
    }

    @Override
    public void PayError(String type) {
//        T.showToast(mContext,getStrWithId(R.string.net_error));

    }

    @Override
    public void Dialog() {
        if (mPayDialog != null && mPayDialog.isShowing()) {
            mPayDialog.dismiss();
        }
    }
}
