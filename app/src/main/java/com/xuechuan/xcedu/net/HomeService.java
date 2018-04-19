package com.xuechuan.xcedu.net;

import android.content.Context;

import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.base.BaseHttpServcie;
import com.xuechuan.xcedu.net.view.StringCallBackView;
import com.xuechuan.xcedu.utils.T;
import com.xuechuan.xcedu.vo.GetParamVo;
import com.xuechuan.xcedu.vo.UserInfomVo;

import java.util.ArrayList;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.net
 * @Description: 热词接口
 * @author: L-BackPacker
 * @date: 2018/4/18 9:56
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class HomeService extends BaseHttpServcie {
    private Context mContext;
    private static HomeService service;
    private ArrayList<GetParamVo> vos;

    public HomeService(Context mContext) {
        this.mContext = mContext;
    }

    public static HomeService getInstance(Context context) {
        if (service == null) {
            service = new HomeService(context);
        }
        return service;
    }

    @Override
    public void setIsShowDialog(boolean show) {
        super.setIsShowDialog(show);
    }

    public void setDialogContext(String title, String cont) {
        super.setDialogContext(mContext, title, cont);
    }

    /**
     * 请求热词
     *
     * @param num          数量
     * @param callBackView
     */
    public void requestHost(String num, StringCallBackView callBackView) {
        ArrayList<GetParamVo> vos = getGetParamList();
        GetParamVo vo = getParamVo();
        vo.setParam("num");
        vo.setValue(num);
        vos.add(vo);
        String url = getUrl(R.string.http_getHotkey);
        requestHttpServiceGet(mContext, url, vos, true, callBackView);
    }

    /**
     * @param province     省
     * @param callBackView
     */
    public void requestInfom(String province, StringCallBackView callBackView) {
        ArrayList<GetParamVo> list = getGetParamList();
        GetParamVo vo = getParamVo();
        vo.setParam("provincecode");
        vo.setValue(province);
        list.add(vo);
        String url = getUrl(R.string.http_infom);
        requestHttpServiceGet(mContext, url, list, true, callBackView);

    }

    /**
     * 请求文章
     *
     * @param staffid
     * @param callBackView
     */
    public void requestArticleList(String staffid, String page, StringCallBackView callBackView) {
        ArrayList<GetParamVo> list = getGetParamList();
        GetParamVo paramVo = getParamVo();
        paramVo.setParam("staffid");
        paramVo.setValue(staffid);
        list.add(paramVo);
        GetParamVo paramVo1 = getParamVo();
        paramVo1.setParam("page");
        paramVo1.setValue(page);
        list.add(paramVo1);
        GetParamVo paramVo2 = getParamVo();
        paramVo2.setParam("pagesize");
        paramVo2.setValue("10");
        list.add(paramVo2);
        String url = getUrl(R.string.http_WenZhan);
        requestHttpServiceGet(mContext, url, list, true, callBackView);
    }

    /**
     *请求文章资讯列表数据
     * @param provinceCode
     * @param page
     * @param callBackView
     */
    public void requestAdvisoryList(String provinceCode, String page, StringCallBackView callBackView) {
        ArrayList<GetParamVo> list = getGetParamList();
        GetParamVo paramVo = getParamVo();
        paramVo.setParam("page");
        paramVo.setValue(page);
        list.add(paramVo);
        GetParamVo paramVo2 = getParamVo();
        paramVo2.setParam("pagesize");
        paramVo2.setValue("10");
        list.add(paramVo2);
        GetParamVo paramVo1 = getParamVo();
        paramVo1.setParam("provincecode");
        paramVo1.setValue(provinceCode);
        list.add(paramVo1);
        String url = getUrl(R.string.http_infom);
        requestHttpServiceGet(mContext, url, list, true, callBackView);
    }


    /**
     * 请求主页内容
     *
     * @param provice      省份
     * @param callBackView
     */
    public void requestHomePager(String provice, StringCallBackView callBackView) {
        ArrayList<GetParamVo> list = getGetParamList();
        GetParamVo vo = getParamVo();
        vo.setParam("provincecode");
        vo.setValue(provice);
        UserInfomVo userInfom = MyAppliction.getInstance().getUserInfom();
        if (userInfom == null) {
            T.showToast(mContext, mContext.getResources().getString(R.string.please_login));
            return;
        }
        int id = userInfom.getData().getUser().getId();
        GetParamVo vo1 = getParamVo();
        vo1.setParam("staffid");
        vo1.setValue(String.valueOf(id));
        list.add(vo1);
        list.add(vo);
        String url = getUrl(R.string.http_HomePage);
        requestHttpServiceGet(mContext, url, list, true, callBackView);
    }

    private GetParamVo getParamVo() {
        return new GetParamVo();
    }

    private ArrayList<GetParamVo> getGetParamList() {
        if (vos == null) {
            vos = new ArrayList<>();
        } else {
            vos.clear();
        }
        return vos;
    }

    private String getUrl(int str) {
        return mContext.getResources().getString(str);
    }

}
