package com.xuechuan.xcedu.db.DbHelp;

import com.xuechuan.xcedu.base.DataMessageVo;
import com.xuechuan.xcedu.db.DownVideoDb;
import com.xuechuan.xcedu.db.DownVideoDbDao;
import com.xuechuan.xcedu.utils.SaveUUidUtil;
import com.xuechuan.xcedu.utils.StringUtil;
import com.xuechuan.xcedu.vo.Db.DownVideoVo;

import java.util.List;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.db.DbHelp
 * @Description: 下载数据库
 * @author: L-BackPacker
 * @date: 2018/5/18 13:59
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public class DbHelperDownAssist {

    private  DownVideoDbDao dao;
    private static DbHelperDownAssist assist;

    public DbHelperDownAssist() {
        if (dao == null)
            dao = DBHelper.getDaoSession().getDownVideoDbDao();
    }

    public static DbHelperDownAssist getInstance() {
        if (assist == null) {
            assist = new DbHelperDownAssist();
        }
        return assist;
    }

    /**
     * 添加数据
     *
     * @param db
     */
    public void addDownItem(DownVideoDb db) {
        if (db == null) {
            return;
        }
        //判断当前课目是否有缓存过
        DownVideoDb list = queryUserDownInfomWithKid(db.getKid());
        if (list == null) {
            savaUserId(db);
            addDownItem(db);
            return;
        }
        List<DownVideoVo> downlist = list.getDownlist();
        if (downlist != null && !downlist.isEmpty()) {//已有情况
            for (DownVideoVo vo : db.getDownlist()) {
                if (!downlist.contains(vo)) {
                    downlist.add(vo);
                }
            }
        } else {//没有
            downlist = db.getDownlist();
        }
        list.setDownlist(downlist);
        dao.update(list);

    }

    /**
     * 删除章节
     *
     * @param kid
     * @param zid
     */
    public void delectZItem(String kid, String zid) {
        if (StringUtil.isEmpty(zid) || StringUtil.isEmpty(kid)) {
            return;
        }
        List<DownVideoDb> dbs = queryUserDownInfom();
        if (dbs == null || dbs.isEmpty()) {
            return;
        }
        for (int i = 0; i < dbs.size(); i++) {
            DownVideoDb db = dbs.get(i);
            if (db.getKid().equals(kid)) {//找到下载的科目
                List<DownVideoVo> downlist = db.getDownlist();
                for (int j = 0; j < downlist.size(); j++) {
                    DownVideoVo vo = downlist.get(j);
                    if (vo.getZid().equals(zid)) {//找到该篇
                        downlist.remove(j);
                    }
                }
                dao.update(db);
            }
        }
    }


    /**
     * 删除科目
     *
     * @param kid
     */
    public void delectKItem(String kid) {
        if (StringUtil.isEmpty(kid)) {
            return;
        }
        List<DownVideoDb> dbs = queryUserDownInfom();
        if (dbs == null || dbs.isEmpty()) {
            return;
        }
        for (int i = 0; i < dbs.size(); i++) {
            DownVideoDb db = dbs.get(i);
            if (db.getKid().equals(kid)) {//找到下载的科目
                dao.deleteByKey(db.getId());
            }
        }
    }


    /**
     * 删除全部
     */
    public void delectAll() {
        List<DownVideoDb> dbs = queryUserDownInfom();
        if (dbs == null || dbs.isEmpty()) {
            return;
        }
        dao.detachAll();

    }

    /**
     * 更新用户选中数据
     *
     * @param db
     */
    public void addUpDataItem(DownVideoDb db, DownVideoVo vo, long current, long total) {
        if (db == null || vo == null) {
            return;
        }
        DownVideoDb videoDb = queryUserDownInfomWithKid(db.getKid());
        if (videoDb == null) {
            return;
        }
        List<DownVideoVo> downlist = videoDb.getDownlist();
        if (downlist == null || downlist.isEmpty()) {
            return;
        }
        for (int i = 0; i < downlist.size(); i++) {
            DownVideoVo videoVo = downlist.get(i);
            if (videoVo.getPid().equals(vo.getPid()) && videoVo.getZid().equals(vo.getZid())) {
                if (current == 0) {
                    videoVo.setStatus("2");
                }
                if (current == total) {
                    videoVo.setStatus("0");
                } else {
                    videoVo.setStatus("1");
                }

                videoVo.setPercent(current);
                videoVo.setTotal(total);
            }
        }
        dao.update(videoDb);


    }

    public boolean isAdd(DownVideoDb db, DownVideoVo vo) {
        if (db == null || vo == null) {
            return false;
        }
        DownVideoDb videoDb = queryUserDownInfomWithKid(db.getKid());
        if (videoDb == null) {
            return false;
        }
        List<DownVideoVo> downlist = videoDb.getDownlist();
        if (downlist == null || downlist.isEmpty()) {
            return false;
        }
        for (int i = 0; i < downlist.size(); i++) {
            DownVideoVo videoVo = downlist.get(i);
            if (videoVo.getZid().equals(vo.getZid()) && videoVo.getPid().equals(vo.getPid())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除某条数据
     *
     * @param db
     * @param vo
     */
    public void delectItem(DownVideoDb db, DownVideoVo vo) {
        DownVideoDb videoDb = queryUserDownInfomWithKid(db.getKid());
        if (videoDb == null) {
            return;
        }
        List<DownVideoVo> downlist = videoDb.getDownlist();
        if (downlist == null || downlist.isEmpty()) {
            return;
        }
        for (int i = 0; i < downlist.size(); i++) {
            DownVideoVo vo1 = downlist.get(i);
            if (vo1.getPid().equals(vo.getPid()) && vo1.getZid().equals(vo.getZid())) {
                downlist.remove(i);
            }
        }
        dao.update(videoDb);
    }

    /**
     * 删除某课目下其中一个
     *
     * @param kid
     * @param pid
     * @param zid
     */
    public void delectItem(String kid, String pid, String zid) {
        DownVideoDb videoDb = queryUserDownInfomWithKid(kid);
        if (videoDb == null) {
            return;
        }
        List<DownVideoVo> downlist = videoDb.getDownlist();
        if (downlist == null || downlist.isEmpty()) {
            return;
        }
        for (int i = 0; i < downlist.size(); i++) {
            DownVideoVo vo1 = downlist.get(i);
            if (vo1.getPid().equals(pid) && vo1.getZid().equals(zid)) {
                downlist.remove(i);
            }
        }

        if (downlist.size() > 0) {//有
            videoDb.setDownlist(downlist);
            dao.update(videoDb);
        } else {
            dao.deleteByKey(videoDb.getId());
        }


    }

    /**
     * 更新其中一条下载数据状态
     *
     * @param kid
     * @param pid
     * @param zid
     * @param status
     */
    public void upDataItemStatus(String kid, String pid, String zid, String status) {
        DownVideoDb videoDb = queryUserDownInfomWithKid(kid);
        if (videoDb == null) {
            return;
        }
        List<DownVideoVo> downlist = videoDb.getDownlist();
        if (downlist == null || downlist.isEmpty()) {
            return;
        }
        for (int i = 0; i < downlist.size(); i++) {
            DownVideoVo vo1 = downlist.get(i);
            if (vo1.getPid().equals(pid) && vo1.getZid().equals(zid)) {
                vo1.setStatus(status);
            }
        }
    }


    public void savaUserId(DownVideoDb db) {
        String userId = SaveUUidUtil.getInstance().getUserId();
//        DownVideoDb videoDb = dao.queryBuilder().where(DownVideoDbDao.Properties.Staffid.eq(userId)).unique();
//        if (videoDb == null) {
        DownVideoDb downVideoDb = new DownVideoDb();
        downVideoDb.setStaffid(userId);
        downVideoDb.setKid(db.getKid());
        downVideoDb.setUrlImg(db.getUrlImg());
        downVideoDb.setKName(db.getKName());
        dao.insert(downVideoDb);
//        }
//        else {
//            videoDb.setStaffid(userId);
//            videoDb.setKid(db.getKid());
//            videoDb.setUrlImg(db.getUrlImg());
//            videoDb.setKName(db.getKName());
//            dao.update(videoDb);
//        }

    }

    /**
     * 查询该用户的所有下载数据
     *
     * @return
     */
    public List<DownVideoDb> queryUserDownInfom() {
//        String userId = SaveUUidUtil.getInstance().getUserId();
        DBHelper.getDaoSession().clear();
        return dao.loadAll();
    }

    /**
     * 查询该用户的所有下载数据
     *
     * @return
     */
    public DownVideoDb queryUserDownInfomWithKid(String kid) {
        if (StringUtil.isEmpty(kid)) {
            return null;
        }
        DBHelper.getDaoSession().clear();
        return dao.queryBuilder().where(DownVideoDbDao.Properties.Kid.eq(kid)).unique();
    }


}
