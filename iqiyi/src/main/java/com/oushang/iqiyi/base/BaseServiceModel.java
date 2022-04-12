package com.oushang.iqiyi.base;

import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.lib_base.base.mvp.model.BaseModel;

/**
 * @Author: zeelang
 * @Description: 连接各种服务管理的数据模型
 * @Time: 2021/8/31 15:57
 * @Since: 1.0
 */
public class BaseServiceModel extends BaseModel {

    public BaseServiceModel() {
        ARouter.getInstance().inject(this);
    }
}
