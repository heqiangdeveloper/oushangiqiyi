package com.oushang.iqiyi.dialog;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseDialog;
import com.oushang.iqiyi.base.DialogViewHolder;

/**
 * @Author: DELL
 * @Description: 服务提醒
 * @Time: 2021/8/3 16:24
 * @Since:
 */
public class ServiceWarnDialog extends BaseDialog {

    @Override
    public int setDialogLayoutId() {
        return R.layout.dialog_service_authentication;
    }

    @Override
    public void convertView(DialogViewHolder holder, BaseDialog dialog) {
        dialog.setSize(474,223);
    }
}
