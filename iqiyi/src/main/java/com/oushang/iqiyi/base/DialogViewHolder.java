package com.oushang.iqiyi.base;

import android.view.View;

import androidx.annotation.NonNull;

import com.oushang.lib_base.base.rv.BaseViewHolder;

/**
 * @Author: zeelang
 * @Description: dialog view holder
 * @Time: 2021/8/3 10:23
 * @Since: 1.0
 */
public class DialogViewHolder extends BaseViewHolder {

    public DialogViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public static DialogViewHolder create(View view) {
        return new DialogViewHolder(view);
    }
}
