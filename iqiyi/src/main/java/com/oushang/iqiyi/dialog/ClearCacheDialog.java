package com.oushang.iqiyi.dialog;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BaseDialog;
import com.oushang.iqiyi.base.DialogViewHolder;
import com.oushang.iqiyi.fragments.MySettingFragment;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.utils.CacheUtils;
import com.oushang.lib_base.utils.ToastUtils;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: 清除缓存弹窗
 * @Time: 2021/8/3 11:41
 * @Since: 1.0
 */
public class ClearCacheDialog extends BaseDialog {
    private static final String TAG = ClearCacheDialog.class.getSimpleName();
    private Disposable clearCacheDisposable;

    @Override
    public int setDialogLayoutId() {
        return R.layout.dialog_clear_cache;
    }

    @Override
    public void convertView(DialogViewHolder holder, BaseDialog dialog) {
        dialog.setSize(630,296);
        TextView clearConfim = holder.getView(R.id.clear_confirm_tv);
        TextView clearCancel = holder.getView(R.id.clear_cancel_tv);
        ProgressBar clearPb = holder.getView(R.id.clear_pb);
        TextView clearTitleTv = holder.getView(R.id.clear_title_tv);
        TextView clearingTv = holder.getView(R.id.clearing_tv);

        clearConfim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click clear cache");
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5947);//埋点数据
                clearCacheDisposable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(0);
                        CacheUtils.clearApplicationCache(getContext());
                        Thread.sleep(2000);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(integer -> {
                                    clearPb.setVisibility(View.VISIBLE);
                                    clearTitleTv.setVisibility(View.GONE);
                                    clearingTv.setVisibility(View.VISIBLE);
                                    clearCancel.setVisibility(View.GONE);
                                    clearConfim.setVisibility(View.GONE);
                                },
                                throwable -> {
                                    Log.d(TAG, Log.getStackTraceString(throwable));
                                },
                                () -> {
                                    Log.d(TAG, "onComplete");
                                    dismiss();
                                    ((MySettingFragment)getParentFragment()).notifyDataChanged(0);
                                    ToastUtils.showToastNew(R.layout.toast_layout, R.id.toast_content, "清除成功！");
                                }, new Consumer<Disposable>() {
                                    @Override
                                    public void accept(Disposable disposable) throws Exception {
                                        Log.d(TAG,"accept");
                                    }
                                });
            }
        });

        clearCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click clear cancel");
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5948);//埋点数据
                dismiss();
            }
        });
        dialog.setOutCancel(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (clearCacheDisposable != null && !clearCacheDisposable.isDisposed()) {
            clearCacheDisposable.dispose();
        }
    }
}
