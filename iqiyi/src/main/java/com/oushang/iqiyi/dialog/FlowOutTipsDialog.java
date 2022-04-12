package com.oushang.iqiyi.dialog;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseDialog;
import com.oushang.iqiyi.base.DialogViewHolder;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.manager.AppManager;

/**
 * @Author: zeelang
 * @Description: 流量到期提示
 * @Time: 2021/8/3 16:22
 * @Since: 1.0
 */
public class FlowOutTipsDialog extends BaseDialog {
    private static final String TAG = "FlowOutTipsDialog";

    @Override
    public int setDialogLayoutId() {
        return R.layout.dialog_flow_out_tips;
    }

    @Override
    public void convertView(DialogViewHolder holder, BaseDialog dialog) {
        dialog.setSize(630,296);
        Button toShopBtn = holder.getView(R.id.to_shop_btn);
        Button cancelBtn = holder.getView(R.id.cancel_btn);
        toShopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchStore(v.getContext(), Constant.GOODSID);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppManager.getAppManager().exitApp();
            }
        });
        dialog.setOutCancel(false);
    }


    /**
     * 跳转流量充值商店
     */
    public void launchStore(Context context, String goodsId) {
        Log.d(TAG, "launchStore: goodsId=" + goodsId);
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
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
