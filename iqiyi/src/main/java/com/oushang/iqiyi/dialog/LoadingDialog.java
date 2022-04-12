package com.oushang.iqiyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseDialog;
import com.oushang.iqiyi.base.DialogViewHolder;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * @Author: zeelang
 * @Description: 加载loading
 * @Time: 2022/1/18 11:55
 * @Since: 1.0
 */
public class LoadingDialog extends Dialog {
    private AVLoadingIndicatorView loadingIndicatorView;

    public LoadingDialog(@NonNull Context context) {
        this(context, 0);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        Window window = getWindow();
        window.getDecorView().setPadding(0,0,0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        loadingIndicatorView = findViewById(R.id.dialog_loading);
    }

    @Override
    public void show() {
        super.show();
        loadingIndicatorView.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        loadingIndicatorView.hide();
    }
}
