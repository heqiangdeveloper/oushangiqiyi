package com.oushang.lib_service.player.iqiyi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.iqy.iv.CodeRateUtil;
import com.iqy.iv.VideoScaleType;
import com.iqy.iv.player.IMedia;
import com.iqy.iv.player.IMediaPlayer;
import com.iqy.iv.player.SDKPayInfo;
import com.iqy.iv.player.SDKPlayerErrorV2;
import com.iqy.iv.player.SDKPlayerInfo;
import com.iqy.iv.player.SDKPlayerInfoAlbum;
import com.iqy.iv.player.SDKPlayerInfoVideo;
import com.iqy.iv.player.onlyyou.SDKRate;
import com.iqy.iv.sdk.PlayerSdk;
import com.iqy.iv.sdk.PlayerSdkProvider;
import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_service.entries.DataSource;
import com.oushang.lib_service.entries.PlayerInfo;
import com.oushang.lib_service.entries.VideoRate;
import com.oushang.lib_service.player.IPlayDataSource;
import com.oushang.lib_service.player.IPlayView;
import com.oushang.lib_service.player.IPlayer;
import com.oushang.lib_service.player.PlayerState;
import com.oushang.lib_service.response.ReturnCode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author: zeelang
 * @Description: 爱奇艺播放器
 * @Time: 2021/7/28 14:04
 * @Since: 1.0
 */
public class IqiyiPlayer implements IPlayer {
    private static final String TAG = IqiyiPlayer.class.getSimpleName();

    private static final int DEFALUT = PlayerState.MEDIA_PLAYER_IDLE;

    private AtomicInteger mCurrentState = new AtomicInteger(DEFALUT);

    private IMediaPlayer mPlayer;

    private IqiyiPlayView mIqiyiPlayView;

    private DataSource dataSource;

    public IqiyiPlayer(IMediaPlayer mediaPlayer) {
        this.mPlayer = mediaPlayer;
        initPlayer();
    }

    public IqiyiPlayer() {
        mPlayer = PlayerSdk.getInstance().createPlayer(null);
        initPlayer();
    }

    private void initPlayer() {
        if (mPlayer != null) {
            mPlayer.setOnStateChangedListener(stateChangedListener);
            mPlayer.setOnSeekChangedListener(seekChangedListener);
            mPlayer.setOnPreviewInfoListener(previewInfoListener);
            mPlayer.setOnBitStreamChangedListener(bitStreamChangedListener);
        }
    }

    private boolean isInitSuccess() {
        if (mPlayer == null) {
            Log.e(TAG,"IMediaPlayer init is fail!");
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        if (isInitSuccess()) mPlayer.start();
    }

    @Override
    public void pause() {
        if (isInitSuccess()) mPlayer.pause();
    }

    @Override
    public void seekTo(long milliSec) {
        if (isInitSuccess()) mPlayer.seekTo(milliSec);
    }

    @Override
    public void stop() {
        if (isInitSuccess()) mPlayer.stop(true);
    }

    @Override
    public void stop(boolean val) {
        if (isInitSuccess()) mPlayer.stop(val);
    }

    @Override
    public void release() {
        if (isInitSuccess()) {
            stateChangedListener = null;
            seekChangedListener = null;
            previewInfoListener = null;
            bitStreamChangedListener = null;

            mPlayer.release();
            mCurrentState.set(PlayerState.MEDIA_PLAYER_STATE_END);
        }
    }

    @Override
    public int getCurrentPosition() {
        return isInitSuccess()? mPlayer.getCurrentPosition():0;
    }

    @Override
    public int getDuration() {
        return isInitSuccess()?mPlayer.getDuration():0;
    }

    @Override
    public int getBufferLength() {
        return isInitSuccess()?mPlayer.getBufferLength():0;
    }

    @Override
    public boolean isPlaying() {
        return isInitSuccess() && mPlayer.isPlaying();
    }

    @Override
    public boolean isPause() {
        return isInitSuccess() && !mPlayer.isPlaying();  //&& mPlayer.isPaused();
    }

    @Override
    public boolean isSleeping() {
        return isInitSuccess(); //&& mPlayer.isSleeping();
    }

    @Override
    public int getCurrentState() {
        return mCurrentState.get();
    }

    @Override
    public void setDataSource(IPlayDataSource dataSource) {
        this.dataSource = (DataSource) dataSource.getData();
        mCurrentState.set(PlayerState.MEDIA_PLAYER_INITIALIZED);
    }

    @Override
    public void setDisplay(IPlayView view) {
        if (view instanceof IqiyiPlayView) mIqiyiPlayView = (IqiyiPlayView) view;
        Context pluginContext = PlayerSdkProvider.getInstance().pluginContext;
        if (pluginContext == null) {
            Log.e(TAG, "playersdk provider context is null!");
            return;
        }
        if (isInitSuccess()) {
            View videoView = mPlayer.initPlayer((Activity) mIqiyiPlayView.getContext(), pluginContext);
            Log.d(TAG, "videoView:" + videoView);
            mIqiyiPlayView.addView(videoView);
        }
    }

    @Override
    public void prepareAsync() {
//        if (isInitSuccess()) mPlayer.prepareAsync();
    }

    @Override
    public void prepare() {

    }

    @Override
    public void wakeUp() {
//        if (isInitSuccess()) mPlayer.wakeUp();
    }

    @Override
    public void sleep() {
//        if (isInitSuccess()) mPlayer.sleep();
    }

    @Override
    public void capturePicture() {
        if(isInitSuccess()) {
            mPlayer.capturePicture();
        }
    }

    @Override
    public void doPlay() {
        if (dataSource != null && isInitSuccess()) {
            String aid = dataSource.getAid();
            String tvId = dataSource.getTvid();
            int rcCheckPolicy = dataSource.getRcCheckPolicy();
            int cType = dataSource.getcType();
            Log.d(TAG, "doPlay:{aid:" + aid + ",tvId:" + tvId + ",rcCheckPolicy:" + rcCheckPolicy + ",cType:" + cType + "}");
            mPlayer.doPlay(aid, tvId, rcCheckPolicy, cType);
        } else {
            LogUtils.e("doPlay fail, cause may be dataSource is null or IMediaPlayer init fail!");
        }
    }

    /**
     *  获取所有的清晰度
     * @return 清晰度列表
     */
    @Override
    public List<VideoRate> getAllBitStream() {
        List<VideoRate> videoRateList = new ArrayList<>();
        if (isInitSuccess()) {
            List<SDKRate> sdkRates = mPlayer.getAllBitStream();
            videoRateList = sdkRates.stream()
                    .map(sdkRate -> {
                        int rt = sdkRate.getRt();
                        boolean isVip = sdkRate.isVIP();
                        return new VideoRate(rt, isVip);
                    }).collect(Collectors.toList());
        }

        return videoRateList;
    }

    /**
     * 获取当前清晰度
     * @return 当前清晰度
     */
    @Override
    public int getCurrentBitStream() {
        return isInitSuccess()?mPlayer.getCurrentBitStream():0;
    }

    /**
     * 获取清晰度名称
     * @param rt 清晰度
     * @return 清晰度名称
     */
    @Override
    public String getRateName(int rt) {
        return CodeRateUtil.getRateName(rt);
    }

    /**
     * 切换码率
     * @param rt 清晰度
     * @return 成功true, 失败false
     */
    @Override
    public boolean switchBitStream(int rt) {
        return isInitSuccess() && mPlayer.switchBitStream(rt);
    }

    @Override
    public int getVideoRadioSize() {
        return isInitSuccess()?mPlayer.getVideoRatioSize():0;
    }

    @Override
    public void setVideoRadioSize(int radioSize) {
        if(isInitSuccess()) {
            mPlayer.setVideoRatio(radioSize);
        }
    }

    @Override
    public void setSkipHeadAndTail(boolean b) {
        if(isInitSuccess()) mPlayer.setSkipHeadAndTail(b);
    }

    @Override
    public boolean isSkipSlide() {
        return isInitSuccess() && mPlayer.isSkipSlide();
    }

    @Override
    public void configVideoViewSize(Configuration configuration, Activity activity) {
        if(isInitSuccess()) {
            mPlayer.configVideoViewSize(configuration, activity);
        }
    }

    private PlayerInfo convert(SDKPlayerInfo sdkPlayerInfo) {
        if (sdkPlayerInfo == null) {
            return null;
        }
        PlayerInfo playerInfo = null;
        SDKPlayerInfoVideo playerInfoVideo =  sdkPlayerInfo.sdkPlayerInfoVideo;
        SDKPlayerInfoAlbum playerInfoAlbum = sdkPlayerInfo.sdkPlayerInfoAlbum;
        Log.d(TAG, "onMovieStart SDKPlayerInfoVideo:{[album:" + playerInfoAlbum + "],[" + playerInfoVideo + "]}");

        PlayerInfo.Builder builder = new PlayerInfo.Builder();
        builder.setAl_id(playerInfoAlbum.id);
        builder.setAl_img(playerInfoAlbum.img);
        builder.setAl_v2Img(playerInfoAlbum.v2Img);
        builder.setAl_desc(playerInfoAlbum.desc);
        builder.setAl_year(playerInfoAlbum.year);

        builder.setTv_id(playerInfoVideo.id);
        builder.setTv_title(playerInfoVideo.title);
        builder.setTv_desc(playerInfoVideo.desc);
        builder.setTv_duration(playerInfoVideo.duration);
        builder.setTv_img(playerInfoVideo.img);
        builder.setTv_order(playerInfoVideo.order);
        playerInfo = builder.build();

        return playerInfo;
    }

    /**
     * 播放状态监听
     */
    IMediaPlayer.OnStateChangedListener stateChangedListener = new IMediaPlayer.OnStateChangedListener() {

        @Override
        public void onPrepared() {
            Log.d(TAG, "onPrepared");
            mCurrentState.set(PlayerState.MEDIA_PLAYER_PREPARED);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onPrepared(mCurrentState.get());
            }
        }

        @Override
        public void onMovieStart() {
            Log.d(TAG, "onMovieStart");
            mCurrentState.set(PlayerState.MEDIA_PLAYER_STARTED);

            PlayerInfo playerInfo = null;
            if(mPlayer != null) {
                SDKPlayerInfo sdkPlayerInfo = mPlayer.getPlayerInfo();
                if(sdkPlayerInfo != null) {
                    SDKPlayerInfoVideo playerInfoVideo =  sdkPlayerInfo.sdkPlayerInfoVideo;
                    SDKPlayerInfoAlbum playerInfoAlbum = sdkPlayerInfo.sdkPlayerInfoAlbum;
                    Log.d(TAG, "onMovieStart SDKPlayerInfoVideo:{[album:" + playerInfoAlbum + "],[" + playerInfoVideo + "]}");

                    PlayerInfo.Builder builder = new PlayerInfo.Builder();
                    builder.setAl_id(playerInfoAlbum.id);
                    builder.setAl_img(playerInfoAlbum.img);
                    builder.setAl_v2Img(playerInfoAlbum.v2Img);
                    builder.setAl_desc(playerInfoAlbum.desc);
                    builder.setAl_year(playerInfoAlbum.year);

                    builder.setTv_id(playerInfoVideo.id);
                    builder.setTv_title(playerInfoVideo.title);
                    builder.setTv_desc(playerInfoVideo.desc);
                    builder.setTv_duration(playerInfoVideo.duration);
                    builder.setTv_img(playerInfoVideo.img);
                    builder.setTv_order(playerInfoVideo.order);
                    playerInfo = builder.build();
                }
            }
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onMovieStart(playerInfo, mCurrentState.get());
            }

        }

        @Override
        public void onPaused() {
            Log.d(TAG, "onPaused");
            mCurrentState.set(PlayerState.MEDIA_PLAYER_PAUSED);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onPaused(mCurrentState.get());
            }
        }

        @Override
        public void onPlaying() {
            Log.d(TAG, "onPlaying");
            mCurrentState.set(PlayerState.MEDIA_PLAYER_STARTED);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onPlaying(mCurrentState.get());
            }
        }


        @Override
        public void onCompleted() {
            Log.d(TAG, "onCompleted");
            mCurrentState.set(PlayerState.MEDIA_PLAYER_PLAYBACK_COMPLETE);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onCompleted(mCurrentState.get());
            }
        }

        @Override
        public void onStopped() {
            Log.d(TAG, "onStopped");
            mCurrentState.set(PlayerState.MEDIA_PLAYER_STOPPED);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onStopped(mCurrentState.get());
            }

        }

        @Override
        public void onError(SDKPlayerErrorV2 sdkPlayerErrorV2) {
            Log.d(TAG, "onError：" + sdkPlayerErrorV2);
            ReturnCode.ErrorCode errorCode = new ReturnCode.ErrorCode(sdkPlayerErrorV2.getDetails(), sdkPlayerErrorV2.getDesc());
            mCurrentState.set(PlayerState.MEDIA_PLAYER_STATE_ERROR);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onError(errorCode, mCurrentState.get());
            }
        }


        @Override
        public void onProgressChanged(long l) {
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onProgressChanged(l);
            }
        }

        @Override
        public void onBufferingUpdate(boolean b) {
            Log.d(TAG, "onBufferingUpdate:" + b);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onBufferingUpdate(b);
            }
        }

        @Override
        public void onConcurrentTip() {
            Log.d(TAG, "onConcurrentTip");
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onConcurrentTip();
            }
        }

        @Override
        public void onDrmChange(boolean b) {
            Log.d(TAG, "onDrmChange:" + b);
        }

        @Override
        public void onFetchPlayerInfo(SDKPlayerInfo sdkPlayerInfo) {
            Log.d(TAG, "onFetchPlayerInfo:" + sdkPlayerInfo);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnStateChangedListener != null) {
                mIqiyiPlayView.mOnStateChangedListener.onFetchPlayerInfo(convert(sdkPlayerInfo));
            }
        }
    };

    /**
     * 快进快退完成回调
     */
    IMediaPlayer.OnSeekChangedListener seekChangedListener = new IMediaPlayer.OnSeekChangedListener() {
        @Override
        public void onSeekStarted() {
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnSeekChangedListener != null) {
                mIqiyiPlayView.mOnSeekChangedListener.onSeekStarted();
            }
        }

        @Override
        public void onSeekCompleted() {
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnSeekChangedListener != null) {
                mIqiyiPlayView.mOnSeekChangedListener.onSeekCompleted();
            }
        }
    };

    /**
     * 试看信息回调
     */
    IMediaPlayer.OnPreviewInfoListener previewInfoListener = new IMediaPlayer.OnPreviewInfoListener() {
        @Override
        public void onTrialWatchingStart(int previewType, int previewTimeMs, int videoRightTipType) {
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnPreviewInfoListener != null) {
                mIqiyiPlayView.mOnPreviewInfoListener.onPreviewInfoReady(previewType, previewTimeMs, videoRightTipType);
            }
        }

        @Override
        public void showVipTip(SDKPayInfo info) {
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnPreviewInfoListener != null) {
                mIqiyiPlayView.mOnPreviewInfoListener.showVipMask("info");
            }
        }

        @Override
        public void onTrialWatchingEnd() {
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnPreviewInfoListener != null) {
                mIqiyiPlayView.mOnPreviewInfoListener.onPreviewInfoFinish();
            }
        }
    };

    /**
     * 通知码流切换的信息
     */
    IMediaPlayer.OnBitStreamChangedListener bitStreamChangedListener = new IMediaPlayer.OnBitStreamChangedListener() {

        @Override
        public void onBitStreamChanging(int i, int i1) {
            Log.d(TAG, "OnBitStreamChanging:" + i + "," + i1 );
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnBitStreamChangeListener != null) {
                mIqiyiPlayView.mOnBitStreamChangeListener.OnBitStreamChanging(i,i1);
            }

        }

        @Override
        public void onBitStreamChanged(int i) {
            Log.d(TAG, "OnBitStreamChanged:" + i);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnBitStreamChangeListener != null) {
                mIqiyiPlayView.mOnBitStreamChangeListener.OnBitStreamChanged(i);
            }

        }

        @Override
        public void onRateChangeFail(int i, int i1, int i2) {
            Log.d(TAG, "onRateChangeFail:" + i + "," + i1 + "," +  i2);
            if (mIqiyiPlayView != null && mIqiyiPlayView.mOnBitStreamChangeListener != null) {
                mIqiyiPlayView.mOnBitStreamChangeListener.onRateChangeFail(i, i1,i2);
            }

        }
    };

    IMediaPlayer.ISDKCapturePictureListener capturePictureListener = new IMediaPlayer.ISDKCapturePictureListener() {
        @Override
        public void onCapturePicture(Bitmap bitmap) {
            if(mIqiyiPlayView != null && mIqiyiPlayView.mOnCapturePictureListener != null) {
                mIqiyiPlayView.mOnCapturePictureListener.onCapturePicture(bitmap);
            }
        }
    };

    @Override
    public void onActivityCreate() {
        if (isInitSuccess()) mPlayer.onActivityCreate();
    }

    @Override
    public void onActivityStart() {
        if (isInitSuccess()) mPlayer.onActivityStart();
    }

    @Override
    public void onActivityResume() {
        if (isInitSuccess()) mPlayer.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        if (isInitSuccess()) mPlayer.onActivityPause();
    }

    @Override
    public void onActivityStop() {
        if (isInitSuccess()) mPlayer.onActivityStop();
    }

    @Override
    public void onActivityDestroy() {
        if (isInitSuccess()) mPlayer.onActivityDestroy();
    }
}
