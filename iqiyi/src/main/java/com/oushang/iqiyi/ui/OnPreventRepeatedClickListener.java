package com.oushang.iqiyi.ui;

import android.util.Log;
import android.view.View;

import com.youth.banner.listener.OnBannerListener;

/**
 * @Author: zeelang
 * @Description: 防止重复点击
 * @Time: 2021/12/21 0021  13:46
 * @Since: 1.0
 */
public abstract class OnPreventRepeatedClickListener implements View.OnClickListener {
    private static final long INTERVAL_TIME = 2000;
    private long lastTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        Log.d("OnPreventRepeatedClickListener", "currentTime:" + currentTime + ",lastTime:" + lastTime + ",time:" + (currentTime - lastTime));
        if(currentTime - lastTime > INTERVAL_TIME) {
            lastTime = currentTime;
            Log.d("OnPreventRepeatedClickListener","onNoRepeatedClick");
            onNoRepeatedClick(v);
        }
    }
    public abstract void onNoRepeatedClick(View view);

}
