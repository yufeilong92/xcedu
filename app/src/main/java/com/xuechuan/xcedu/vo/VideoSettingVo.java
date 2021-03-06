package com.xuechuan.xcedu.vo;

import com.xuechuan.xcedu.base.BaseVo;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.vo
 * @Description: todo
 * @author: L-BackPacker
 * @date: 2018/6/12 18:38
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class VideoSettingVo extends BaseVo {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        /**
         * 1从右至左滚动 ，2屏幕内随机位置闪烁 3从右至左渐隐渐现的滚动
         */
        private int style;
        /**
         * 总时长
         */
        private int duration;
        /**
         * 跑马灯文本显示时间，单位为毫秒。与屏幕内随机位置闪烁的跑马灯样式相关
         */
        private int lifetime;
        /**
         *文字大小
         */
        private int textsize;
        /**
         *文字颜色
         */
        private String textcolor;
        /**
         * 文字透明度
         */
        private int textalpha;
        /**
         *
         */
        private int font;
        /**
         *设置跑马灯文本隐藏间隔时间，单位为毫秒。与屏幕内随机位置闪烁的跑马灯样式相关。默认为1000
         */
        private int interval;
        /**
         * 渐隐渐显时间
         */
         private int tweentime;

        public int getTweentime() {
            return tweentime;
        }

        public void setTweentime(int tweentime) {
            this.tweentime = tweentime;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getLifetime() {
            return lifetime;
        }

        public void setLifetime(int lifetime) {
            this.lifetime = lifetime;
        }

        public int getTextsize() {
            return textsize;
        }

        public void setTextsize(int textsize) {
            this.textsize = textsize;
        }

        public String getTextcolor() {
            return textcolor;
        }

        public void setTextcolor(String textcolor) {
            this.textcolor = textcolor;
        }

        public int getTextalpha() {
            return textalpha;
        }

        public void setTextalpha(int textalpha) {
            this.textalpha = textalpha;
        }

        public int getFont() {
            return font;
        }

        public void setFont(int font) {
            this.font = font;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }
    }
}
