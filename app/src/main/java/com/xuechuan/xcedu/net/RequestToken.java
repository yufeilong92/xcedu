package com.xuechuan.xcedu.net;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.lzy.okgo.model.Response;
import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.base.BaseHttpServcie;
import com.xuechuan.xcedu.net.view.StringCallBackView;
import com.xuechuan.xcedu.utils.L;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.utils.Utils;
import com.xuechuan.xcedu.vo.HttpInfomVo;
import com.xuechuan.xcedu.vo.ResultBeanVo;

import java.util.Date;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.net.Presenter
 * @Description: String类型回掉
 * @author: L-BackPacker
 * @date: 2018/4/12 13:31
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class RequestToken extends BaseHttpServcie {

    private static RequestToken callBack;
    private final HttpInfomVo mInfomVo;

    public RequestToken() {
        mInfomVo = MyAppliction.getHttpInfomInstance();
    }

    public static RequestToken getInstance() {
        if (callBack == null)
            callBack = new RequestToken();
        return callBack;
    }

    public void requestToken(final Context context, final String aciton, String stffid) {
        String url = context.getResources().getString(R.string.app_content_token);
        String time = String.valueOf(new Date().getTime());
        String random8 = String.valueOf(Utils.getRandom8());
        mInfomVo.setTimeStamp(String.valueOf(time));
        mInfomVo.setNonce(String.valueOf(random8));
        if (StringUtil.isEmpty(stffid)) {
            stffid = "0";
        }
        mInfomVo.setStaffid(stffid);
        final String id = stffid;
        sendOkGoGetToken(url, stffid, time, random8, null, new StringCallBackView() {
            @Override
            public void onSuccess(Response<String> response) {
                Gson gson = new Gson();
                String s = response.body().toString();
                L.d(s);
                ResultBeanVo resultBeanVo = gson.fromJson(s, ResultBeanVo.class);
                mInfomVo.setToken(resultBeanVo.getData().getSignToken());
                Intent intent = new Intent();
                intent.setAction(aciton);
                context.sendBroadcast(intent);

            }
            @Override
            public void onError(Response<String> response) {
                L.d(response.message().toString());
                requestToken(context,aciton, id);
            }
        });
    }

}
