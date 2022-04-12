package com.oushang.lib_service.player;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.ViewGroup;

import com.oushang.lib_service.entries.VideoRate;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放视图接口
 * @Time: 2021/7/28 11:43
 * @Since: 1.0
 */
public interface IPlayView {

    void attach(IPlayer player);

    void detach();

    void doPlay();

    void start();

    void pause();

    void seekTo(long milliSec);

    void stop();

    void stop(boolean val);

    void sleep();

    void wakeUp();

    void release();

    void capturePicture();

    int getCurrentPosition();

    int getDuration();

    int getBufferLength();

    boolean isAttach();

    boolean isPlaying();

    boolean isPause();

    boolean isSleeping();

    void setDataSource(IPlayDataSource source);

    ViewGroup obtainDisplay();

    @PlayerState.State int getCurrentState();

    List<VideoRate> getAllBitStream();

    int getCurrentBitStream();

    String getRateName(int rt);

    boolean switchBitStream(int rt);

    void setVideoRadioSize(int radioSize);

    int getVideoRadioSize();

    void setSkipHeadAndTail(boolean skip);

    boolean isSkipSlide();

    void configVideoViewSize(Configuration configuration, Activity activity);

}
