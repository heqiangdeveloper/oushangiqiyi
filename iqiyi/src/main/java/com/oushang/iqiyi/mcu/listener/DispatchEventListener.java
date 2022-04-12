package com.oushang.iqiyi.mcu.listener;

import com.oushang.iqiyi.mcu.handler.BaseHandler;
/**
 * @Author: zeelang
 * @Description: mcu事件分发
 * @Time: 2022/1/21 17:03
 * @Since: 1.0
 */
public interface DispatchEventListener {
    String TAG = "MCU_EVENT";

    default <T> void dispatchHandler(BaseHandler<T> handler, T t) {
        if(handler != null) {
            handler.handler(t);
        }
    }


}
