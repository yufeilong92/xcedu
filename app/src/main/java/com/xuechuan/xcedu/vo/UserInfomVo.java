package com.xuechuan.xcedu.vo;

import com.xuechuan.xcedu.base.BaseVo;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.vo
 * @Description: 用户信息
 * @author: L-BackPacker
 * @date: 2018/4/17 14:07
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class UserInfomVo extends BaseVo {
    /**
     * 用户数据
     */
    private DataBean data;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }
    public static class DataBean {

        /**
         *是否绑定手机号注册
         */

        private String info;
        /**
         *
         */
        private int status;
        private UserBean user;
        private int code;
        /**
         * 平台id
         */
        private String unionid;
        /**
         * 微信标识
         */
        private String openid;
        /**
         * 是否绑定手机号（true 绑定 false 未绑定）
         */
        private boolean isbinduser;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public boolean isIsbinduser() {
            return isbinduser;
        }

        public void setIsbinduser(boolean isbinduser) {
            this.isbinduser = isbinduser;
        }

        /**
         * 用户信息
         */

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

    }
}
