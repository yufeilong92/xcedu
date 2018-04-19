package com.xuechuan.xcedu.vo;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.vo
 * @Description: 文章
 * @author: L-BackPacker
 * @date: 2018/4/18 20:37
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class ArticleBean extends HomePageVo {
    /**
     * 图片地址
     */
    private String gourl;
    /**
     * id
     */
    private int id;
    /**
     *
     */
    private boolean issupport;
    /**
     * 发布时间
     */
    private String publishdate;
    /**
     *
     */
    private int supportcount;
    /***
     *
     */
    private String thumbnailimg;
    /***
     * 标题
     */
    private String title;
    /**
     * 类型
     */
    private int type;
    private int viewcount;

    public String getGourl() {
        return gourl;
    }

    public void setGourl(String gourl) {
        this.gourl = gourl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIssupport() {
        return issupport;
    }

    public void setIssupport(boolean issupport) {
        this.issupport = issupport;
    }

    public String getPublishdate() {
        return publishdate;
    }

    public void setPublishdate(String publishdate) {
        this.publishdate = publishdate;
    }

    public int getSupportcount() {
        return supportcount;
    }

    public void setSupportcount(int supportcount) {
        this.supportcount = supportcount;
    }

    public String getThumbnailimg() {
        return thumbnailimg;
    }

    public void setThumbnailimg(String thumbnailimg) {
        this.thumbnailimg = thumbnailimg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getViewcount() {
        return viewcount;
    }

    public void setViewcount(int viewcount) {
        this.viewcount = viewcount;
    }
}