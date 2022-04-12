package com.oushang.lib_base.base.mvp.view;

public interface IView {

    void showLoading();

    void hideLoading();

    void showDialog();

    void dismissDialog();

    void showToast(int resId);

    void showToast(String msg);

    void showLoadNetErrorView();

    void showLoadNetErroView(boolean isTimeOut);

    void hideLoadNetErrorView();

    void showLoadEmptyView();

    void hideLoadEmptyView(String emptyText);

}
