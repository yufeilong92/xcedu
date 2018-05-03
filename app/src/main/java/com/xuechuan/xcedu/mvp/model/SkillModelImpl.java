package com.xuechuan.xcedu.mvp.model;

import android.content.Context;

import com.lzy.okgo.model.Response;
import com.xuechuan.xcedu.mvp.view.RequestResulteView;
import com.xuechuan.xcedu.net.BankService;
import com.xuechuan.xcedu.net.view.StringCallBackView;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: SkillModelImpl.java
 * @Package com.xuechuan.xcedu.mvp.model
 * @Description: todo
 * @author: YFL
 * @date: 2018/5/4 00:02
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/5/4 星期五
 * 注意：本内容仅限于学川教育有限公司内部传阅，禁止外泄以及用于其他的商业目
 */
public class SkillModelImpl implements SkillModel {
    @Override
    public void reqeustErrOrCoNumber(Context context, String courseid, final RequestResulteView view) {
        BankService service = new BankService(context);
        service.requestErrSetandFav(courseid, new StringCallBackView() {
            @Override
            public void onSuccess(Response<String> response) {
                view.success(response.body().toString());
            }

            @Override
            public void onError(Response<String> response) {
                view.error(response.message());
            }
        });
    }
}
