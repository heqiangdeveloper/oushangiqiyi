package com.oushang.iqiyi.dialog;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.manager.AppManager;
import com.oushang.iqiyi.utils.AppUtils;

/**
 * @Author: zeelang
 * @Description: 车联网流量提醒
 * @Time: 2022/2/22 21:29
 * @Since: 1.0
 */
public class VehicleFlowDialog extends Dialog {
    private static final String TAG = VehicleFlowDialog.class.getSimpleName();
    private Button mToShopBtn;
    private Button mCancelBtn;

    public VehicleFlowDialog(@NonNull Context context) {
        super(context);
    }

    public VehicleFlowDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected VehicleFlowDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private volatile static VehicleFlowDialog sVehicleFlowDialog;

    public static VehicleFlowDialog getInstance(Context context) {
        if(sVehicleFlowDialog == null) {
            synchronized (VehicleFlowDialog.class) {
                if(sVehicleFlowDialog == null) {
                    sVehicleFlowDialog = new VehicleFlowDialog(context);
                }
            }
        }
        return sVehicleFlowDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_flow_out_tips);
        Window window = getWindow();
        window.getDecorView().setPadding(0,0,0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        mToShopBtn = findViewById(R.id.to_shop_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mToShopBtn.setOnClickListener(v -> {
            launchStore(v.getContext(), Constant.GOODSID);
            AppManager.getAppManager().exitApp();
        });
        mCancelBtn.setOnClickListener(v -> {
            dismiss();
            AppManager.getAppManager().exitApp();
        });

    }

    /**
     * 跳转流量充值商店
     */
    public void launchStore(Context context, String goodsId) {
        Log.d(TAG, "launchStore");
        try {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            String cls = "com.chinatsp.servicemall.MainActivity";
            if (!TextUtils.isEmpty(goodsId)) {
                cls = "com.chinatsp.servicemall.activity.GoodsDetailActivity";
                bundle.putString("goods_id", goodsId);
            } else {
                bundle.putInt("type", 1);
            }
            bundle.putString("package", context.getPackageName());
            intent.putExtras(bundle);
            ComponentName componetName = new ComponentName("com.chinatsp.servicemall", cls);
            intent.setComponent(componetName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (AppUtils.isExistActivity(context, "com.chinatsp.servicemall", cls)) {
                context.startActivity(intent);
            } else {
                Log.e(TAG, "not found activity!");
            }
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
