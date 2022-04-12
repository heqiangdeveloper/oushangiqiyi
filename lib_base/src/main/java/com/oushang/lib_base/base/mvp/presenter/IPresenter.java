package com.oushang.lib_base.base.mvp.presenter;


import com.oushang.lib_base.base.mvp.view.IView;

public interface IPresenter<T extends IView> {

    void attach(T view);

    void detach();

    boolean isAttach();

}
