package com.oushang.lib_base.net.state;


/**
 * @Author: zeelang
 * @Description: 网络状态监听
 * @Time: 2021/7/12 18:13
 * @Since: 1.0
 */
public interface NetworkObserver{

    void onNetworkChanged(boolean isConnected);

}
