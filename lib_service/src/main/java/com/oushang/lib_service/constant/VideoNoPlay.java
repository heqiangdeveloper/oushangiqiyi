package com.oushang.lib_service.constant;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 视频禁止播放
 * @Time: 2022/1/8 10:40
 * @Since: 1.0
 */
public class VideoNoPlay {

    private volatile int mNoPlayReason = NO_PLAY_IDLE; //禁止播放原因
    private VideoNoPlayListener mVideoNoPlayListener; //禁止播放监听

    private static final int NO_PLAY_IDLE = 0x000000000; //空闲，可继续播放

    public static final int NO_PLAY_REASON_OVER_SPEED = 0x000000001; //超速 1
    public static final int NO_PLAY_REASON_POWER_OFF = 0x000000020; //息屏 32
    public static final int NO_PLAY_REASON_AVM_ON = 0x000000300; //- 768
    public static final int NO_PLAY_REASON_NETWORK_UNAVAILABLE = 0x000004000; //网络不可用 16384
    public static final int NO_PLAY_REASON_MASTER_MUTE = 0x000050000;//静音/方控(Mute) 327680
    public static final int NO_PLAY_REASON_BOOT_WELECOME = 0x000600000;//开机欢迎页

    private VideoNoPlay() {}

    static class VideoNoPlayHolder {
        static VideoNoPlay HOLDER = new VideoNoPlay();
    }

    public static VideoNoPlay getInstance() {
        return VideoNoPlayHolder.HOLDER;
    }

    @IntDef({NO_PLAY_REASON_OVER_SPEED,
            NO_PLAY_REASON_POWER_OFF,
            NO_PLAY_REASON_AVM_ON,
            NO_PLAY_REASON_NETWORK_UNAVAILABLE,
            NO_PLAY_REASON_MASTER_MUTE,
            NO_PLAY_REASON_BOOT_WELECOME
    })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Reason{}

    /**
     * 禁播状态是否是空闲
     * @return 是 true, 否 false
     */
    public boolean isIdle() {
        return mNoPlayReason == NO_PLAY_IDLE;
    }

    /**
     * 禁止播放
     * @param reason 禁止播放原因
     */
    public synchronized void noPlay(@Reason int reason) {
        mNoPlayReason |= reason;
        if(mVideoNoPlayListener != null) {
            mVideoNoPlayListener.onChange(mNoPlayReason);
        }
    }

    /**
     * 释放禁止播放原因
     * @param reason 禁止播放原因
     */
    public synchronized void release(@Reason int reason) {
        mNoPlayReason &= ~reason;
        if(mVideoNoPlayListener != null) {
            mVideoNoPlayListener.onChange(mNoPlayReason);
        }
    }

    /**
     * 是否存在禁止播放原因
     * @param reason 禁止播放原因
     * @return 是 true, 否 false
     */
    public boolean hasNoPlayReason(@Reason int reason) {
        return _hasNoPlayReason(reason);
    }

    /**
     * 是否存在禁播原因
     * @param reason 禁播原因
     * @return 是 true, 否 false
     */
    private boolean _hasNoPlayReason(@Reason int reason) {
        return (mNoPlayReason & reason) != 0;
    }

    /**
     * 获取当前禁播原因
     * @return 禁播原因
     */
    public int getNoPlayReason() {
        return mNoPlayReason;
    }

    /**
     * 清除禁止播放原因
     */
    public synchronized void clearNoPlayReason() {
        mNoPlayReason &= NO_PLAY_IDLE;
        if(mVideoNoPlayListener != null) {
            mVideoNoPlayListener.onChange(mNoPlayReason);
        }
    }

    /**
     * 获取所有禁播原因
     * @return 禁播原因列表
     */
    public List<Integer> getAllNoPlayReason() {
        List<Integer> noPlayReasonList = new ArrayList<>();
        if(_hasNoPlayReason(NO_PLAY_REASON_OVER_SPEED)) {
            noPlayReasonList.add(NO_PLAY_REASON_OVER_SPEED);
        }
        if(_hasNoPlayReason(NO_PLAY_REASON_POWER_OFF)) {
            noPlayReasonList.add(NO_PLAY_REASON_POWER_OFF);
        }
        if(_hasNoPlayReason(NO_PLAY_REASON_AVM_ON)) {
            noPlayReasonList.add(NO_PLAY_REASON_AVM_ON);
        }
        if(_hasNoPlayReason(NO_PLAY_REASON_NETWORK_UNAVAILABLE)) {
            noPlayReasonList.add(NO_PLAY_REASON_NETWORK_UNAVAILABLE);
        }
        if(_hasNoPlayReason(NO_PLAY_REASON_MASTER_MUTE)) {
            noPlayReasonList.add(NO_PLAY_REASON_MASTER_MUTE);
        }
        if (_hasNoPlayReason(NO_PLAY_REASON_BOOT_WELECOME)) {
            noPlayReasonList.add(NO_PLAY_REASON_BOOT_WELECOME);
        }
        return noPlayReasonList;
    }

    /**
     * 设置禁播监听
     * @param listener 禁播监听
     */
    public void setVideoNoPlayListener(VideoNoPlayListener listener) {
        this.mVideoNoPlayListener = listener;
    }

    /**
     * 移除禁播监听
     */
    public void removeVideoNoPlayListener() {
        mVideoNoPlayListener = null;
    }

    /**
     * 禁播监听
     */
    public interface VideoNoPlayListener {
        void onChange(int reason);
    }
}
