package com.oushang.lib_service.player;

import androidx.annotation.IntDef;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: zeelang
 * @Description: 播放器状态
 * @Time: 2021/7/28 11:05
 * @Since: 1.0
 */
public class PlayerState {

    /**
     * 播放器释放结束
     */
    public static final int MEDIA_PLAYER_STATE_END = -1;

    /**
     * 播放器错误
     */
    public static final int MEDIA_PLAYER_STATE_ERROR = 0;

    /**
     * 播放器空闲
     */
    public static final int MEDIA_PLAYER_IDLE = 1;

    /**
     * 播放器初始化
     */
    public static final int MEDIA_PLAYER_INITIALIZED = 2;

    /**
     * 播放器开始准备
     */
    public static final int MEDIA_PLAYER_PREPARING = 3;

    /**
     * 播放器准备完毕
     */
    public static final int MEDIA_PLAYER_PREPARED = 4;

    /**
     * 开始播放
     */
    public static final int MEDIA_PLAYER_STARTED = 5;

    /**
     * 暂停
     */
    public static final int MEDIA_PLAYER_PAUSED = 6;

    /**
     * 停止
     */
    public static final int MEDIA_PLAYER_STOPPED = 7;

    /**
     * 完成
     */
    public static final int MEDIA_PLAYER_PLAYBACK_COMPLETE = 8;

    @IntDef({MEDIA_PLAYER_STATE_END,
            MEDIA_PLAYER_STATE_ERROR,
            MEDIA_PLAYER_IDLE,
            MEDIA_PLAYER_INITIALIZED,
            MEDIA_PLAYER_PREPARING,
            MEDIA_PLAYER_PREPARED,
            MEDIA_PLAYER_STARTED,
            MEDIA_PLAYER_PAUSED,
            MEDIA_PLAYER_STOPPED,
            MEDIA_PLAYER_PLAYBACK_COMPLETE
    })
    @Retention(RetentionPolicy.SOURCE)
    @Inherited
    public @interface State{}


}
