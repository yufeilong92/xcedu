package com.xuechuan.xcedu.net;

import android.content.Context;

import com.xuechuan.xcedu.R;
import com.xuechuan.xcedu.base.OkPostRequestService;
import com.xuechuan.xcedu.net.view.StringCallBackView;
import com.xuechuan.xcedu.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.net
 * @Description: 注册接口
 * @author: L-BackPacker
 * @date: 2018/4/16 12:39
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class RegisterService extends OkPostRequestService {

    private static RegisterService registerService;

    private Context mContext;

    public RegisterService(Context context) {
        this.mContext = context;
    }

    public static RegisterService getInstance(Context context) {
        if (registerService == null)
            registerService = new RegisterService(context);
        return registerService;
    }

    /**
     * 是否显示弹框
     *
     * @param show
     */
    public void setIsShowDialog(boolean show) {
        super.setIsShowDialog(show);
    }

    /**
     * 摊开
     *
     * @param title
     * @param cont
     */
    public void setDialogContext(String title, String cont) {
        super.setDialogContext(mContext, title, cont);
    }

    /**
     * 请求验证码
     *
     * @param phone        手机号
     * @param callBackView 回调
     */
    public void requestRegisterCode(String phone, StringCallBackView callBackView) {
        String url = mContext.getResources().getString(R.string.http_code);
        JSONObject object = new JSONObject();
        try {
            object.put("usetype", "reg");
            object.put("telphone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestPostWithOutToken(mContext, url, object, callBackView);

    }

    /***
     *
     * @param phone 电话
     * @param code  验证码
     * @param paw 密码
     * @param openid 微信标识
     * @param unionid 平台标识
     */
    public void requestRegister(String type,String phone, String code, String paw, String openid, String unionid,
                                StringCallBackView callBackView) {
        String url = mContext.getResources().getString(R.string.http_register);
        JSONObject object = new JSONObject();
        try {
            object.put("telphone", phone);
            object.put("usetype", type);
            object.put("openid", StringUtil.isEmpty(openid) ? null : openid);
            object.put("unionid", StringUtil.isEmpty(unionid) ? null : unionid);
            object.put("securitycode", code);
            object.put("password", paw);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestPostWithOutToken(mContext, url, object, callBackView);
    }

}
