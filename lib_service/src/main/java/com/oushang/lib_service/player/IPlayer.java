package com.oushang.lib_service.player;

import android.app.Activity;
import android.content.res.Configuration;

import com.oushang.lib_service.entries.VideoRate;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放器接口
 * @Time: 2021/7/28 10:58
 * @Since: 1.0
 */
public interface IPlayer {

    void start();

    void pause();

    void seekTo(long milliSec);

    void stop();

    void stop(boolean val);

    void release();

    int getCurrentPosition();

    int getDuration();

    int getBufferLength();

    boolean isPlaying();

    boolean isPause();

    boolean isSleeping();

    @PlayerState.State int getCurrentState();

    void setDataSource(IPlayDataSource dataSource);

    void setDisplay(IPlayView view);

    void prepareAsync();

    void prepare();

    void wakeUp();

    void sleep();

    void capturePicture();

    void doPlay();

    List<VideoRate> getAllBitStream();

    int getCurrentBitStream();

    String getRateName(int rt);

    boolean switchBitStream(int rt);

    int getVideoRadioSize();

    void setVideoRadioSize(int radioSize);

    void setSkipHeadAndTail(boolean b);

    boolean isSkipSlide();

    void configVideoViewSize(Configuration configuration, Activity activity);

    default void onActivityCreate(){};

    default void onActivityStart(){};

    default void onActivityResume(){};

    default void onActivityPause(){};

    default void onActivityStop(){}

    default void onActivityDestroy(){}

}
