package com.oushang.lib_service.player.iqiyi;

import androidx.annotation.IntDef;

import com.oushang.lib_service.player.PlayerState;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author: DELL
 * @Description: 爱奇艺播放状态
 * @Time: 2021/7/29 10:16
 * @Since:
 */
public class IqiyiPlayerState extends PlayerState {

    public static final int MEDIA_PLAYER_AD_START = 9;

    public static final int MEDIA_PLAYER_AD_END = 10;

    public static final int MEDIA_PLAYER_STOPPING = 11;

    public static final int MEDIA_PLAYER_SLEEPED = 12;

    public static final int MEDIA_PLAYER_WAKEUPED = 13;


    @IntDef({MEDIA_PLAYER_AD_START,
            MEDIA_PLAYER_AD_END,
            MEDIA_PLAYER_STOPPING,
            MEDIA_PLAYER_SLEEPED,
            MEDIA_PLAYER_WAKEUPED
    })
    @Retention(RetentionPolicy.SOURCE)
    @State
    public @interface IqiyiState {}


}
