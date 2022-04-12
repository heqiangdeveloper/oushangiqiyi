package com.oushang.iqiyi.base;

import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oushang.lib_base.base.mvp.model.IModel;
import com.oushang.lib_base.base.mvp.presenter.BasePresenter;
import com.oushang.lib_service.entries.UserInfo;

/**
 * @Author: DELL
 * @Description:
 * @Time: 2021/8/11 9:27
 * @Since:
 */
public class BaseNavPresenter extends BasePresenter<IBaseNavView,IModel> {

    @Override
    protected IModel createModel() {
        return null;
    }

    public void loadUserInfo() {
        UserInfo userInfo = new UserInfo();
        if (getView() != null) {
            getView().onLoadUserInfo(userInfo);
        }
    }

    public void navSelected(TextView view) {
        IBaseNavView navView = getView();
        if (navView != null) {
            navView.onNavSelected(view);
        }
    }

    public void jumpSelected(TextView view, Fragment fragment) {
        IBaseNavView navView = getView();
        if (navView != null) {
            navView.onJumpSelected(view, fragment);
        }
    }




}
