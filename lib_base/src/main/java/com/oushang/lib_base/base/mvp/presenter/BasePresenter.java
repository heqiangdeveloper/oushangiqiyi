package com.oushang.lib_base.base.mvp.presenter;


import com.oushang.lib_base.base.mvp.model.IModel;
import com.oushang.lib_base.base.mvp.view.IView;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IView, M extends IModel> implements IPresenter<V> {
    protected M model;
    private WeakReference<V> weakView;

    @Override
    public void attach(V view) {
        weakView = new WeakReference<>(view);
        if (model == null) {
            model = createModel();
        }
    }

    @Override
    public void detach() {
        if (isAttach()) {
            weakView.clear();
            weakView = null;
        }
        if (model != null) {
            model = null;
        }
    }

    @Override
    public boolean isAttach() {
        return weakView != null && weakView.get() != null;
    }

    protected V getView() {
        return weakView != null ? weakView.get() : null;
    }

    protected abstract M createModel();

    public void showLoading() {
        if (isAttach()) {
            getView().showLoading();
        }
    }

    public void hideLoading() {
        if (isAttach()) {
            getView().hideLoading();
        }
    }

    public void showToast(String msg) {
        if (isAttach()) {
            getView().showToast(msg);
        }
    }
}
