package com.oushang.iqiyi.base;

import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oushang.lib_base.base.mvp.view.IView;
import com.oushang.lib_service.entries.UserInfo;

/**
 * @Author: zeelang
 * @Description: 导航基类接口
 * @Time: 2021/8/11 9:28
 * @Since: 1.0
 */
public interface IBaseNavView extends IView {

    void onLoadUserInfo(UserInfo userInfo);

    void onNavSelected(TextView view);

    void onJumpSelected(TextView view, Fragment fragment);

}
