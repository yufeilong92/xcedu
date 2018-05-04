package com.xuechuan.xcedu.mvp.view;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: xcedu
 * @Package com.xuechuan.xcedu.mvp.view
 * @Description: 获取评价
 * @author: L-BackPacker
 * @date: 2018/5/3 15:50
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018
 */
public interface EvalueView  {
    public void submitEvalueSuccess(String con);
    public void submitEvalueError(String con);
    public void GetEvalueSuccess(String con);
    public void GetEvalueError(String con);

}
