package com.oushang.iqiyi.mvp.view;

import android.widget.TextView;

import com.oushang.lib_base.base.mvp.view.IView;

/**
 * @Author: zeelang
 * @Description: 导航接口
 * @Time: 2021/7/6 14:50
 * @Since: 1.0
 */
public interface INavView extends IView {

    void onNavSelected(TextView view);


}
