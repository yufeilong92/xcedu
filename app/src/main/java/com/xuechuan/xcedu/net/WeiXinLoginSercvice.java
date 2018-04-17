package com.xuechuan.xcedu.net;

import android.content.Context;

import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.base.OkPostRequestService;
import com.xuechuan.xcedu.net.view.StringCallBackView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.net
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/4/16 17:38
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class WeiXinLoginSercvice extends OkPostRequestService {

    private static WeiXinLoginSercvice sercvice;
    private Context mContext;

    public WeiXinLoginSercvice(Context mContext) {
        this.mContext = mContext;
    }

    public static WeiXinLoginSercvice getInstance(Context context) {
        if (sercvice == null)
            sercvice = new WeiXinLoginSercvice(context);
        return sercvice;
    }

    public void requestWeiCode( String code, StringCallBackView backView) {
        String weixin = mContext.getResources().getString(R.string.http_url_weixin);
        String url =weixin;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestPostWithOutToken(mContext, url, jsonObject, backView);

    }
}
