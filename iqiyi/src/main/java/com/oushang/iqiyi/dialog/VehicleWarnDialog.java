package com.oushang.iqiyi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.R;

/**
 * @Author: zeelang
 * @Description: 行车提醒弹窗
 * @Time: 2022/4/29 0029  13:54
 * @Since: 1.0
 */
public class VehicleWarnDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = VehicleWarnDialog.class.getSimpleName();

    private static VehicleWarnDialog sVehicleWarnDialog;

    public VehicleWarnDialog(@NonNull Context context) {
        this(context, 0);
    }

    public VehicleWarnDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected VehicleWarnDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static VehicleWarnDialog getInstance(Context context) {
        if (sVehicleWarnDialog == null) {
            synchronized (VehicleWarnDialog.class) {
                if (sVehicleWarnDialog == null) {
                    sVehicleWarnDialog = new VehicleWarnDialog(context);
                }
            }
        }
        return sVehicleWarnDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vehicle_warn);
        Window window = getWindow();
        window.getDecorView().setPadding(0,0,0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        TextView mContinuePlay = findViewById(R.id.vehicle_warn_continue_tv);
        TextView mCancel = findViewById(R.id.vehicle_warn_cancel_tv);
        mContinuePlay.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.vehicle_warn_continue_tv:
                Log.d(TAG, "click continue play");
//                if(setVehicleDisable(true)) {
//                    dismiss();
//                }
                break;
            case R.id.vehicle_warn_cancel_tv:
                Log.d(TAG, "click cancel");
                dismiss();
                break;
        }

    }

    private boolean setVehicleDisable(boolean disable) {
       return Settings.System.putInt(MainApplication.getContext().getContentResolver(), "video_disable_on_driving", disable ? 1 : 0);
    }
}
