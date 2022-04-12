package com.oushang.lib_service.player;

import java.io.Serializable;

/**
 * @Author: zeelang
 * @Description: 播放数据接口
 * @Time: 2021/7/28 15:00
 * @Since: 1.0
 */
public interface IPlayDataSource<T> extends Serializable {

    T getData();
}
