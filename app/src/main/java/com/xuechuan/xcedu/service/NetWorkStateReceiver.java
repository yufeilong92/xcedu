package com.xuechuan.xcedu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.xuechuan.xcedu.XceuAppliciton.MyAppliction;
import com.xuechuan.xcedu.base.DataMessageVo;

/**
 * @version V 1.0 xxxxxxxx
 * @Title: NetWorkStateReceiver
 * @Package com.xuechuan.xcedu.service
 * @Description: 监听网络变化
 * @author: L-BackPacker
 * @date: 2018/6/8 14:39
 * @verdescript 版本号 修改时间  修改人 修改的概要说明
 * @Copyright: 2018/6/8
 */

public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("网络状态发生变化");
        //检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            /*if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                MyAppliction.getInstance().saveUserNetSatus("1");
//                Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
            } else*/ if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                MyAppliction.getInstance().saveUserNetSatus(DataMessageVo.WIFI);

//                Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                MyAppliction.getInstance().saveUserNetSatus(DataMessageVo.MONET);
//                Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
            } else {
                MyAppliction.getInstance().saveUserNetSatus(DataMessageVo.NONETWORK);
//                Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
            }
        } else {
            //这里的就不写了，前面有写，大同小异
            System.out.println("API level 大于21");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
         /*   if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//                MyAppliction.getInstance().saveUserNetSatus("1");
                Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
            } else */if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                MyAppliction.getInstance().saveUserNetSatus(DataMessageVo.WIFI);
//                Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                MyAppliction.getInstance().saveUserNetSatus(DataMessageVo.MONET);
//                Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
            } else {
                MyAppliction.getInstance().saveUserNetSatus(DataMessageVo.NONETWORK);
//                Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
            }
    /*        //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();

            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);

                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
            }
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();*/
        }
    }
}
