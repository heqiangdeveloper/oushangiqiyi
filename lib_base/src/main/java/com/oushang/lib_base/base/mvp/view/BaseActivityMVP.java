package com.oushang.lib_base.base.mvp.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.oushang.lib_base.base.mvp.presenter.BasePresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.Nullable;

public abstract class BaseActivityMVP<P extends BasePresenter> extends AppCompatActivity implements IBaseView {
    protected P presenter;

    private Unbinder unbinder;

    protected abstract int setLayout();

    protected void initView(){}

    protected void initListener(){}

    protected void initData(){}

    protected abstract P createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        unbinder = ButterKnife.bind(this);
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attach(this);
        }
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null) {
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
