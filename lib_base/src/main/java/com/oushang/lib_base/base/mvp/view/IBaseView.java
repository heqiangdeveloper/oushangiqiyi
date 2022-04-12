package com.oushang.lib_base.base.mvp.view;

import android.graphics.Bitmap;

public interface IBaseView extends IView {

    void showQrCodeLoadingView();

    void showLogingQrCode(String code, int expire);

    void hideLoginQrCode();

    void showLoginQrCodeFail();

    void ShowLoginQrCodeInvalid();

    void AfterlogoutSuccess();

    void showBindLogingQrCode(Bitmap bitmap,int expire);
}
