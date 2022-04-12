package com.oushang.iqiyi.dialog;

import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.activities.DisclaimersActivity;
import com.oushang.iqiyi.base.BaseDialog;
import com.oushang.iqiyi.base.DialogViewHolder;
import com.oushang.iqiyi.manager.AppManager;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;

/**
 * @Author: zeelang
 * @Description: 温馨提示
 * @Time: 2021/8/3 16:22
 * @Since: 1.0
 */
public class IqiyiTipsDialog extends BaseDialog {

    @Override
    public int setDialogLayoutId() {
        return R.layout.dialog_iqiyi_tips;
    }

    @Override
    public void convertView(DialogViewHolder holder, BaseDialog dialog) {
        dialog.setSize(630,376);
        Button iKnow = holder.getView(R.id.dialog_i_know_btn);
        iKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5903);
                dialog.dismiss();
                AppManager.getAppManager().exitApp();
            }
        });
    }
}
