package com.oushang.iqiyi.mcu.handler;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.lib_service.interfaces.PlayManager;

/**
 * @Author: DELL
 * @Description:
 * @Time: 2022/1/21 16:33
 * @Since:
 */
public abstract class BaseHandler<T> {
    public static final String TAG = "MCU_EVENT";

    @Autowired(name = com.oushang.lib_service.constant.Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager; //播放管理

    public BaseHandler() {
        ARouter.getInstance().inject(this);
    }

    public abstract void handler(T t);

}
