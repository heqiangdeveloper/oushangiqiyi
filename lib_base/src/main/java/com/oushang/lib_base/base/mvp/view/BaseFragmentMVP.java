package com.oushang.lib_base.base.mvp.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.oushang.lib_base.base.mvp.presenter.BasePresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragmentMVP <P extends BasePresenter> extends Fragment implements IBaseView {

    protected P presenter;

    private Unbinder unbinder;

    protected View rootView;

    protected abstract int setLayout();

    protected void initView(){}

    protected void initListener(){}

    protected void initData(){}

    protected abstract P createPresenter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(rootView == null) {
            rootView = inflater.inflate(setLayout(), container, false);
        }
        unbinder = ButterKnife.bind(this,rootView);
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attach(this);
        }
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
        initListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (presenter != null) {
            presenter.detach();
            presenter = null;
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void showToast(int resId) {

    }

    @Override
    public void showToast(String msg) {

    }

    @Override
    public void showLoadNetErrorView() {

    }

    @Override
    public void showLoadNetErroView(boolean isTimeOut) {

    }

    @Override
    public void hideLoadNetErrorView() {

    }

    @Override
    public void showLoadEmptyView() {

    }

    @Override
    public void hideLoadEmptyView(String emptyText) {

    }

    @Override
    public void showQrCodeLoadingView() {

    }

    @Override
    public void showLogingQrCode(String code, int expire) {

    }

    @Override
    public void hideLoginQrCode() {

    }

    @Override
    public void showLoginQrCodeFail() {

    }

    @Override
    public void ShowLoginQrCodeInvalid() {

    }

    @Override
    public void AfterlogoutSuccess() {

    }

    @Override
    public void showBindLogingQrCode(Bitmap bitmap,int expire) {

    }
}
