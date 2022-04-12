package com.oushang.lib_service.impls;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.iqy.iv.ICallback;
import com.iqy.iv.player.RecordSubscriptionManager;
import com.iqy.iv.player.SDKVideoAlbum;
import com.iqy.iv.player.SDKViewHistory;
import com.iqy.iv.sdk.PlayerSdk;
import com.oushang.lib_base.utils.RxUtils;
import com.oushang.lib_service.callback.IDeleteRecordListCallback;
import com.oushang.lib_service.callback.IDownloadCallback;
import com.oushang.lib_service.callback.IPlayVideoInfoChangeListener;
import com.oushang.lib_service.callback.IPlayerFullScreenListener;
import com.oushang.lib_service.callback.IPlayerWindowListener;
import com.oushang.lib_service.callback.ISubscriptionCallback;
import com.oushang.lib_service.callback.ISubscriptionListCallback;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.constant.VideoNoPlay;
import com.oushang.lib_service.entries.DataSource;
import com.oushang.lib_service.entries.HistoryRecord;
import com.oushang.lib_service.entries.FavoriteRecord;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;
import com.oushang.lib_service.player.IPlayView;
import com.oushang.lib_service.player.IPlayer;
import com.oushang.lib_service.player.iqiyi.IqiyiPlayDataSource;
import com.oushang.lib_service.player.iqiyi.IqiyiPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: zeelang
 * @Description: 播放管理服务, 全局操作
 * @Date: 2021/6/24
 */
@Route(path = Constant.PATH_SERVICE_PLAY_MANAGER)
public class PlayManagerService implements PlayManager {
    private static final String TAG = PlayManagerService.class.getSimpleName();

    //播放器接口
    private IPlayer mPlayer;

    //播放视图接口
    private WeakReference<IPlayView> mIPlayViewWeak;

    //当前播放的视频信息
    private VideoInfo mCurrentVideoInfo;

    //当前播放状态
    private volatile int mCurrentPlayState;

    //播放列表
    private PlayListManager mPlayListManager;

    //播放记录
    private RecordSubscriptionManager mRecordSubscriptionManager;

    //播放视频监听
    private List<IPlayVideoInfoChangeListener> mPlayVideoInfoChangeListenerList;

    private List<IPlayerWindowListener> mPlayerWindowListenerList;

    //是否全屏
    private boolean isFullScreen;

    //全屏播放监听
    private IPlayerFullScreenListener mPlayerFullScreenListener;

    //音频管理服务
    private AudioManager mAudioManager;

    //音频焦点请求
    private AudioFocusRequest mFocusRequest;
    private RxUtils mRxUtils;
    private volatile boolean hasAudioFocus = false; //是否有音频焦点
    private volatile boolean fromUser = false; //暂停是否来自用户手动操作
    private volatile boolean isDoPlay = false; //是否已起播
    private boolean isSkipHeadAndTail = false; //是否跳过片头片尾
    private volatile boolean isFrontShow = false; //是否前台显示
    public static final String BTPHONE_ACTIVE = "btphone_active";
    public static final int BTPHONE_NO_CALL = 0, BTPHONE_CALL = 1;

    private Context mContext;

    public PlayManagerService() {
        mPlayVideoInfoChangeListenerList = new ArrayList<>();
        mPlayerWindowListenerList = new ArrayList<>();

        if (mPlayListManager == null) {
            mPlayListManager = (PlayListManager) ARouter.getInstance().build(Constant.PATH_SERVICE_PLAY_LIST_MANAGER).navigation();
        }
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
    }

    @Override
    public void init(Context context) {
        mContext = context;
        if (mPlayListManager == null) {
            mPlayListManager = (PlayListManager) ARouter.getInstance().build(Constant.PATH_SERVICE_PLAY_LIST_MANAGER).navigation();
        }
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mRxUtils = new RxUtils();
    }

    public synchronized boolean requestAudioFocus() {
        Log.d(TAG, "requestAudioFocus");
        if (mFocusRequest == null) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build();
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attributes)
                    .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                    .setAcceptsDelayedFocusGain(true)
                    .build();
        }
        if (mAudioManager != null) {
            int audioFocus = mAudioManager.requestAudioFocus(mFocusRequest);
            Log.d(TAG, "audioFocus:" + audioFocus);
            hasAudioFocus = audioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            return hasAudioFocus ;
        }
        return false;
    }

    public synchronized void abandonAudioFocus() {
        Log.d(TAG, "abandonAudioFocus");
        if (mAudioManager != null && mFocusRequest != null) {
            mAudioManager.abandonAudioFocusRequest(mFocusRequest);
            hasAudioFocus = false;
        }
    }

    private final AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = focusChange -> {
        Log.d(TAG, "focusChange:" + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: //音频焦点暂时丢失
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                hasAudioFocus = false;
                pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS: //音频焦点持续时间丢失
//                     失去了Audio Focus，并将会持续很长的时间。这里因为可能会停掉很长时间，
//                     所以不仅仅要停止Audio的播放，最好直接释放掉Media资源。而因为停止播放Audio的时间会很长，
//                     如果程序因为这个原因而失去AudioFocus，最好不要让它再次自动获得AudioFocus而继续播放，
//                     不然突然冒出来的声音会让用户感觉莫名其妙，感受很不好。
//                     这里直接放弃AudioFocus，当然也不用再侦听远程播放控制。
//                     要再次播放，除非用户再在界面上点击开始播放，才重新初始化Media，进行播放
                Log.d(TAG, "AUDIOFOCUS_LOSS");
                hasAudioFocus = false;
                pause();
                abandonAudioFocus();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: //暂时失去音频焦点，如果音频焦点的丢失者想要继续播放，可以降低其输出音量（也称为“闪避”），因为新的焦点所有者不要求其他人保持沉默。
                Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                hasAudioFocus = false;
                pause();
                break;
            case AudioManager.AUDIOFOCUS_GAIN: // 持续时间获得了音频焦点
                Log.d(TAG, "AUDIOFOCUS_GAIN");
                hasAudioFocus = true;
                Log.d(TAG, "fromUser:" + fromUser + ",isFrontShow:" + isFrontShow);
                if(fromUser) {
                    fromUser = false;
                } else if(isFrontShow){
                    start();
                }
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_DELAYED:
                Log.d(TAG, "AUDIOFOCUS_REQUEST_DELAYED");
                pause();
                break;
        }
    };

    /**
     * 创建播放器
     *
     * @param playerType 播放器类型
     * @return 播放器
     */
    @Override
    public IPlayer createPlayer(int playerType) {
        switch (playerType) {
            case PLAYER_TYPE_IQIYI:
                return new IqiyiPlayer();
            default:
                break;
        }
        return null;
    }

    /**
     * 设置播放器接口
     *
     * @param iPlayer 播放器接口
     */
    @Override
    public void setPlayer(IPlayer iPlayer) {
        this.mPlayer = iPlayer;
    }

    /**
     * 设置播放视图接口
     *
     * @param iPlayView 播放视图接口
     */
    @Override
    public void setPlayView(IPlayView iPlayView) {
        this.mIPlayViewWeak = new WeakReference<>(iPlayView);
    }

    /**
     * 设置当前播放视频信息
     *
     * @param videoInfo 视频信息
     */
    @Override
    public synchronized void setCurrentVideoInfo(VideoInfo videoInfo) {
        updatePlayVideoInfo(videoInfo);
    }

    /**
     * 获取当前播放的视频信息
     *
     * @return videoInfo 视频信息
     */
    @Override
    public VideoInfo getCurrentVideoInfo() {
        return mCurrentVideoInfo;
    }


    /**
     * 获取当前播放状态
     *
     * @return 播放状态
     */
    @Override
    public int getCurrentPlayState() {
        return mCurrentPlayState;
    }

    /**
     * 是否有音频焦点
     * @return true 是，false 否
     */
    @Override
    public boolean hasAudioFocus() {
        return hasAudioFocus;
    }

    /**
     * 更新播放状态
     *
     * @param state 播放状态
     */
    @Override
    public void updatePlayState(int state) {
        mCurrentPlayState = state;
    }


    @Override
    public void updatePlayVideoInfo(VideoInfo videoInfo) {
        mCurrentVideoInfo = videoInfo; //设置当前播放视频
        if (mPlayVideoInfoChangeListenerList != null && !mPlayVideoInfoChangeListenerList.isEmpty()) { //视频变更通知
            for (IPlayVideoInfoChangeListener listener : mPlayVideoInfoChangeListenerList) {
                listener.onChange(videoInfo);
            }
        }
    }

    @Override
    public void setPlayerFullScreenListener(IPlayerFullScreenListener listener) {
        this.mPlayerFullScreenListener = listener;
    }

    @Override
    public void addPlayVideoInfoChangeListener(IPlayVideoInfoChangeListener listener) {
        mPlayVideoInfoChangeListenerList.add(listener);
    }

    @Override
    public void removePlayVideoInfoChangeListener(IPlayVideoInfoChangeListener listener) {
        mPlayVideoInfoChangeListenerList.remove(listener);
    }

    @Override
    public void addPlayerWindowListener(IPlayerWindowListener listener) {
        mPlayerWindowListenerList.add(listener);

    }

    @Override
    public void removePlayerWindowListener(IPlayerWindowListener listener) {
        mPlayerWindowListenerList.remove(listener);
    }


    /**
     * 播放上一集
     */
    @Override
    public synchronized void playPrev() {
        if (mPlayListManager != null) {
            mPlayListManager.moveToPrev();
            play();
        }
    }

    /**
     * 播放下一集
     */
    @Override
    public synchronized void playNext() {
        if (mPlayListManager != null) {
            mPlayListManager.moveToNext();
            play();
        }
    }


    /**
     * 播放(剧集）
     */
    @Override
    public synchronized void play() {
        if (mIPlayViewWeak != null && mPlayer != null && mPlayListManager != null) {
            VideoInfo videoInfo = mPlayListManager.getCurrentVideoInfo();
            play(videoInfo);
        }
    }

    /**
     * 播放视频
     *
     * @param videoInfo 视频信息
     */
    @Override
    public synchronized void play(VideoInfo videoInfo) {
        Log.d(TAG, "play video:" + videoInfo);
        isDoPlay = false;
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach() && videoInfo != null) {
                long qipuId = videoInfo.getQipuId();
                long alid = videoInfo.getAlbumId();
                int rcCheckPolicy = DataSource.TV_CHECK_RC;

                if (videoInfo.isAlbum()) { //isAlbum 专辑视频
                    alid = qipuId;
                    qipuId = videoInfo.getDefaultEpi().getQipuId();
                    rcCheckPolicy = DataSource.ALBUM_CHECK_RC;
                }
                String aid = String.valueOf(alid);
                String tvid = String.valueOf(qipuId);

                if (videoInfo != mCurrentVideoInfo) {
                    updatePlayVideoInfo(videoInfo);
                }

                playView.setDataSource(new IqiyiPlayDataSource(new DataSource(aid, tvid, rcCheckPolicy, DataSource.NORMAL_VT)));
                _play(playView);
            }
        }
    }

    @Override
    public synchronized void doPlay(long albumId, long qipuId) {
        Log.d(TAG, "doPlay: albumId:" + albumId + ",qipuId:" + qipuId);
        isDoPlay = false;
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if(playView.isAttach()) {
                int rcCheckPolicy = DataSource.TV_CHECK_RC;
                if(VideoInfo.isAlbumId(albumId)) {
                    rcCheckPolicy = DataSource.ALBUM_CHECK_RC;
                }

                String aid = String.valueOf(albumId);
                String tvid = String.valueOf(qipuId);

                playView.setDataSource(new IqiyiPlayDataSource(new DataSource(aid, tvid, rcCheckPolicy, DataSource.NORMAL_VT)));
                _play(playView);
            }
        }
    }

    private void _play(IPlayView playView) {
        Log.d(TAG, "_Play");
        if (playView != null) {
            requestAudioFocus();
            playView.doPlay();
            playView.setSkipHeadAndTail(isSkipHeadAndTail);
            isDoPlay = true;
        } else {
            Log.e(TAG, "_play PlayView is null!");
        }
    }

    private void _start(IPlayView playView) {
        Log.d(TAG, "_start");
        if(playView != null) {
            if(hasAudioFocus) {
                playView.start();
            } else if(requestAudioFocus()){
                playView.start();
            } else {
                Log.e(TAG, "_start no request audio focus!");
            }
        } else {
            Log.e(TAG, "playView is null!");
        }
    }

    /**
     * 开始/继续播放
     */
    @Override
    public synchronized void start() {
        Log.d(TAG, "start");
        if(!VideoNoPlay.getInstance().isIdle()) {
            Log.e(TAG, "forbid play reason:" + VideoNoPlay.getInstance().getNoPlayReason());
            return;
        }
        if (fromUser) {
            Log.e(TAG, "user pause! return");
            return;
        }

        int active = Settings.System.getInt(mContext.getContentResolver(), BTPHONE_ACTIVE, 0);
        if (active == BTPHONE_CALL) { //如果在蓝牙电话通话中
            Log.e(TAG, "Bluetooth call! return");
            return;
        }

        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if(playView.isAttach()) {
                if (!isDoPlay) {
                    _play(playView);
                } else {
                    _start(playView);
                }
            } else {
                Log.e(TAG, "start playView is not attach!");
            }
        } else {
            Log.e(TAG, "start playView is null!");
        }
    }

    @Override
    public void start(boolean clearUserPause) {
        this.fromUser = !clearUserPause;
        start();
    }

    /**
     * 暂停播放
     */
    @Override
    public synchronized void pause() {
        pause(fromUser);
    }

    @Override
    public synchronized void pause(boolean fromUser) {
        Log.d(TAG, "pause");
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                this.fromUser = fromUser;
                Log.d(TAG, "pause fromUser:" + this.fromUser);
                playView.pause();
            } else {
                Log.e(TAG, "pause playView is not attach");
            }
        } else {
            Log.e(TAG, "pause playView is null!");
        }
    }

    /**
     * 停止播放
     */
    @Override
    public synchronized void stop() {
        Log.d(TAG, "stop");
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                playView.stop();
            }
        }
    }

    @Override
    public synchronized void stop(boolean val) {
        Log.d(TAG, "stop:" + val);
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                playView.stop(val);
            }
        }
    }


    /**
     * 当前视频是否是专辑
     *
     * @return 是 true ,否 false
     */
    @Override
    public boolean isAlbum() {
        if (mCurrentVideoInfo != null && (mCurrentVideoInfo.isAlbum() || mCurrentVideoInfo.getAlbumId() != 0)) {
            return true;
        }
        return false;
    }

    /**
     * 判断播放器是否正在播放
     *
     * @return true 是，false 否
     */
    @Override
    public boolean isPlaying() {
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                return playView.isPlaying();
            }
        }
        return false;
    }

    /**
     * 判断播放器是否暂停
     *
     * @return true 是，false 否
     */
    @Override
    public boolean isPaused() {
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                return playView.isPause();
            }
        }
        return false;
    }

    @Override
    public boolean isSleeping() {
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                return playView.isSleeping();
            }
        }
        return false;
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public synchronized void seekTo(long pos) {
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                playView.seekTo(pos);
            }
        }
    }

    /**
     * 跳过片头/片尾
     *
     * @param skip true 跳过， false 不跳过
     */
    @Override
    public void skipHeadAndTail(boolean skip) {
        isSkipHeadAndTail = skip;
    }

    @Override
    public void setFullScreen(boolean full) {
        if (full != isFullScreen) {
            this.isFullScreen = full;
            if (mPlayerFullScreenListener != null) {
                mPlayerFullScreenListener.onChange(isFullScreen);
            }
        }

    }

    /**
     * 切换清晰度
     * @param bitStream 清晰度
     */
    @Override
    public synchronized void switchBitStream(int bitStream) {
        if (mIPlayViewWeak != null) {
            IPlayView playView = mIPlayViewWeak.get();
            if (!playView.isAttach() && mPlayer != null) {
                playView.attach(mPlayer);
            }
            if (playView.isAttach()) {
                playView.switchBitStream(bitStream);
            }
        }
    }

    /**
     * 获取影片时长，单位毫秒
     *
     * @return 影片时长
     */
    @Override
    public long getDuration() {
        return mPlayer != null ? mPlayer.getDuration() : -1;
    }

    /**
     * 获取当前播放位置，单位毫秒
     *
     * @return 当前播放位置
     */
    @Override
    public long getCurrentPos() {
        return mPlayer != null ? mPlayer.getCurrentPosition() : -1;
    }

    @Override
    public void wakeUpPlayer() {
        if (mPlayer != null) {
            mPlayer.wakeUp();
        }
    }

    @Override
    public void sleepPlayer() {
        if (mPlayer != null) {
            mPlayer.sleep();
        }
    }


    @Override
    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if(mIPlayViewWeak != null) {
            mIPlayViewWeak = null;
        }
    }

    /**
     * 获取本地播放记录
     *
     * @return HistoryRecord
     */
    @Override
    public List<HistoryRecord> getLocalRecord() {
        Log.d(TAG, "getLocalRecord");
        List<HistoryRecord> historyRecordList = new ArrayList<>();
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            List<SDKViewHistory> localRecord = mRecordSubscriptionManager.getLocalRecord();
            Log.d(TAG, "LocalRecord:" + localRecord);
            if (localRecord == null || localRecord.isEmpty()) {
                return historyRecordList;
            }
            for (SDKViewHistory history : localRecord) {
                historyRecordList.add(convertHistoryRecord(history));
            }
        } else {
            Log.e(TAG, "sdk RecordSubscriptionManager is null");
        }
        return historyRecordList;
    }

    /**
     * 分页加载云端播放记录
     *
     * @param pageNum  分页
     * @param callback 加载回调
     * @param isMerge  是否合并本地数据
     */
    @Override
    public void downloadRecordByPage(int pageNum, IDownloadCallback callback, boolean isMerge) {
        Log.d(TAG, "downloadRecordByPage：" + pageNum);
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            mRecordSubscriptionManager.downloadRecordByPage(pageNum, new RecordSubscriptionManager.RecordCallback<List<SDKViewHistory>>() {
                @Override
                public void onSuccess(List<SDKViewHistory> sdkViewHistories, String msg) {
                    Log.d(TAG, " downloadRecordByPage onSuccess:" + msg);
                    if (callback != null) {
//                        if (isMerge) {
//                            List<HistoryRecord> localRecord = getLocalRecord();
//                            List<HistoryRecord> historyRecordList = convertHistoryRecordList(sdkViewHistories);
//                            if(localRecord != null && !localRecord.isEmpty()) {
//                                for(HistoryRecord record: localRecord) {
//                                    if(!historyRecordList.contains(record)) {
//                                        historyRecordList.add(0, record);
//                                    }
//                                }
//                            }
//                            Disposable disposable = Observable.just(historyRecordList)
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(callback::onSuccess, Log::getStackTraceString);
//                        } else {
//                            Disposable disposable = Observable
//                                    .create((ObservableOnSubscribe<List<HistoryRecord>>) emitter -> emitter.onNext(convertHistoryRecordList(sdkViewHistories)))
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(callback::onSuccess);
//
//                        }

                        Disposable disposable = Observable
                                .create((ObservableOnSubscribe<List<HistoryRecord>>) emitter -> emitter.onNext(convertHistoryRecordList(sdkViewHistories)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(callback::onSuccess);
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.d(TAG, "downloadRecordByPage onFailure");
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            });
        } else {
            Log.e(TAG, "sdk RecordSubscriptionManager is null");
        }
    }

    /**
     * 删除播放历史记录
     *
     * @param records   播放记录列表
     * @param needClear 是否清空
     * @param callback  删除回调，成功或失败
     */
    @Override
    public void deleteRecord(List<HistoryRecord> records, boolean needClear, IDeleteRecordListCallback callback) {
        Log.d(TAG, "deleteRecord");
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            mRecordSubscriptionManager.deleteRecord(convertSDKViewHistoryList(records), new ICallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "deleteRecord onSuccess");
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onSuccess());
                    }
                }

                @Override
                public void onError(String msg, int code) {
                    Log.d(TAG, "deleteRecord onError：msg:" + msg + ",code:" + code);
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            });
        } else {
            Log.e(TAG, "sdk RecordSubscriptionManager is null");
        }
    }

    /**
     * 清空历史记录
     *
     * @param callback 回调
     */
    @Override
    public void clearHistoryRecord(IDeleteRecordListCallback callback) {
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            mRecordSubscriptionManager.clearRecord(new ICallback() {
                @Override
                public void onSuccess() {
                    if(callback != null) {
//                        callback.onSuccess();
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onSuccess());
                    }
                }

                @Override
                public void onError(String msg, int code) {
                    if(callback != null) {
//                        callback.onFailure(code, msg);
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            });
        }
    }

    /**
     * 清空收藏记录
     *
     * @param callback 回调
     */
    @Override
    public void clearFavoriteRecord(ISubscriptionCallback callback) {
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            mRecordSubscriptionManager.clearSubList(new RecordSubscriptionManager.SubscriptCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if(callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onSuccess());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    if(callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            });
        }
    }

    /**
     * 收藏
     *
     * @param callback 收藏回调
     * @param record   收藏记录
     */
    @Override
    public void subScript(ISubscriptionCallback callback, FavoriteRecord record) {
        Log.d(TAG, "subScript：" + record);
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            if (record == null) {
                Log.d(TAG, "subScript record is null");
                return;
            }
            mRecordSubscriptionManager.subscript(new RecordSubscriptionManager.SubscriptCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "subScript onSuccess");
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onSuccess());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.d(TAG, "subScript onFailure: code:" + code + ",msg:" + msg);
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            }, convertSDKVideoAlbum(record));
        } else {
            Log.e(TAG, "subScript sdk RecordSubscriptionManager is null");
        }
    }

    /**
     * 取消收藏
     *
     * @param callback        收藏回调
     * @param favoriteRecords 收藏记录
     */
    @Override
    public void unSubscript(ISubscriptionCallback callback, FavoriteRecord... favoriteRecords) {
        Log.d(TAG, "unSubscript");
        if (mRecordSubscriptionManager != null) {
            if (favoriteRecords == null || favoriteRecords.length == 0) {
                return;
            }
            int size = favoriteRecords.length;
            SDKVideoAlbum[] sdkVideoAlbum = new SDKVideoAlbum[size];
            for (int i = 0; i < size; i++) {
                SDKVideoAlbum album = convertSDKVideoAlbum(favoriteRecords[i]);
                sdkVideoAlbum[i] = album;
            }

            mRecordSubscriptionManager.unSubscript(new RecordSubscriptionManager.SubscriptCallback<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "unSubscript onSuccess");
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onSuccess());
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.d(TAG, "unSubscript onFailure: code:" + code + ",msg:" + msg);
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            }, sdkVideoAlbum);
        }
    }

    /**
     * 获取收藏记录
     *
     * @param callback 收藏记录回调
     */
    @Override
    public void getSubscriptionList(ISubscriptionListCallback callback) {
        Log.d(TAG, "getSubscriptionList");
        if (mRecordSubscriptionManager == null) {
            mRecordSubscriptionManager = PlayerSdk.getInstance().getRecordSubscriptionManager();
        }
        if (mRecordSubscriptionManager != null) {
            mRecordSubscriptionManager.getSubscriptonList(new RecordSubscriptionManager.SubscriptCallback<List<SDKVideoAlbum>>() {
                @Override
                public void onSuccess(List<SDKVideoAlbum> sdkVideoAlbums) {
                    Log.d(TAG, "getSubscriptionList onSuccess");
                    if (callback != null) {
                        Disposable disposable = Observable.just(sdkVideoAlbums)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(sdkVideoAlbumList -> callback.onSuccess(convertFavoriteRecordList(sdkVideoAlbums)));
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    Log.d(TAG, "getSubscriptionList onFailure：code:" + code + ",msg:" + msg);
                    if (callback != null) {
                        Disposable disposable = Observable.just(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> callback.onFailure(code, msg));
                    }
                }
            });
        } else {
            Log.e(TAG, "sdk RecordSubscriptionManager is null");
        }
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        Log.d(TAG, "onCreate");
        if (mPlayerWindowListenerList != null && !mPlayerWindowListenerList.isEmpty()) {
            for (IPlayerWindowListener listener : mPlayerWindowListenerList) {
                listener.onCreate();
            }
        }

    }

    @Override
    public void onStart(LifecycleOwner owner) {
        Log.d(TAG, "onStart");
        if(mPlayer != null) {
            mPlayer.onActivityStart(); //只关联onStart()、onResume()、onPause()
        }
        if (mPlayerWindowListenerList != null && !mPlayerWindowListenerList.isEmpty()) {
            for (IPlayerWindowListener listener : mPlayerWindowListenerList) {
                listener.onStart();
            }
        }
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        Log.d(TAG, "onResume");
        isFrontShow = true;
        if(mPlayer != null) {
            mPlayer.onActivityResume();
        }
        if (mPlayerWindowListenerList != null && !mPlayerWindowListenerList.isEmpty()) {
            for (IPlayerWindowListener listener : mPlayerWindowListenerList) {
                listener.onResume();
            }
        }
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        Log.d(TAG, "onPause");
        isFrontShow = false;
        if(mPlayer != null) {
            mPlayer.onActivityPause();
        }
        if (mPlayerWindowListenerList != null && !mPlayerWindowListenerList.isEmpty()) {
            for (IPlayerWindowListener listener : mPlayerWindowListenerList) {
                listener.onPause();
            }
        }

    }

    @Override
    public void onStop(LifecycleOwner owner) {
        Log.d(TAG, "onStop");
        abandonAudioFocus();//释放焦点
        if (mPlayerWindowListenerList != null && !mPlayerWindowListenerList.isEmpty()) {
            for (IPlayerWindowListener listener : mPlayerWindowListenerList) {
                listener.onStop();
            }
        }

    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.d(TAG, "onDestroy");
        if (mPlayVideoInfoChangeListenerList != null && !mPlayVideoInfoChangeListenerList.isEmpty()) { //移除所有播放视频监听
            Log.d(TAG, "clear PlayVideoInfoChangeListener list");
            mPlayVideoInfoChangeListenerList.clear();
        }
        if (mPlayerWindowListenerList != null && !mPlayerWindowListenerList.isEmpty()) {
            for (IPlayerWindowListener listener : mPlayerWindowListenerList) {
                listener.onDestroy();
            }
        }
        mPlayerFullScreenListener = null; //移除全屏监听
        isFullScreen = false;
        mCurrentVideoInfo = null;
        if (mRxUtils != null) {
            mRxUtils.unDisposable();
        }
    }

    /**
     * HistoryRecord列表 转换 SDKViewHistory列表
     *
     * @param historyRecordList HistoryRecord列表
     * @return SDKViewHistory列表
     */
    private List<SDKViewHistory> convertSDKViewHistoryList(List<HistoryRecord> historyRecordList) {
        List<SDKViewHistory> sdkViewHistoryList = new ArrayList<>();
        if (historyRecordList != null && !historyRecordList.isEmpty()) {
            for (HistoryRecord record : historyRecordList) {
                sdkViewHistoryList.add(convertSDKViewHistory(record));
            }
        }
        return sdkViewHistoryList;
    }

    private List<SDKVideoAlbum> convertSDKVideoAlbumList(List<FavoriteRecord> favoriteRecordList) {
        List<SDKVideoAlbum> sdkVideoAlbumList = new ArrayList<>();
        if (favoriteRecordList != null && !favoriteRecordList.isEmpty()) {
            for (FavoriteRecord record : favoriteRecordList) {
                sdkVideoAlbumList.add(convertSDKVideoAlbum(record));
            }
        }
        return sdkVideoAlbumList;
    }

    private List<HistoryRecord> convertHistoryRecordList(List<SDKViewHistory> sdkViewHistoryList) {
        List<HistoryRecord> historyRecordList = new ArrayList<>();
        if (sdkViewHistoryList != null && !sdkViewHistoryList.isEmpty()) {
            for (SDKViewHistory history : sdkViewHistoryList) {
                historyRecordList.add(convertHistoryRecord(history));
            }
        }
        return historyRecordList;
    }

    private List<FavoriteRecord> convertFavoriteRecordList(List<SDKVideoAlbum> sdkVideoAlbumList) {
        List<FavoriteRecord> favoriteRecordList = new ArrayList<>();
        if (sdkVideoAlbumList != null && !sdkVideoAlbumList.isEmpty()) {
            for (SDKVideoAlbum album : sdkVideoAlbumList) {
                favoriteRecordList.add(convertFavoriteRecord(album));
            }
        }
        return favoriteRecordList;
    }

    private SDKViewHistory convertSDKViewHistory(HistoryRecord historyRecord) {
        Gson gson = new Gson();
        String json = gson.toJson(historyRecord);
        return gson.fromJson(json, SDKViewHistory.class);
    }

    private SDKVideoAlbum convertSDKVideoAlbum(FavoriteRecord favoriteRecord) {
        Gson gson = new Gson();
        String json = gson.toJson(favoriteRecord);
        return gson.fromJson(json, SDKVideoAlbum.class);
    }

    private HistoryRecord convertHistoryRecord(SDKViewHistory sdkViewHistory) {
        Gson gson = new Gson();
        String json = gson.toJson(sdkViewHistory);
        return gson.fromJson(json, HistoryRecord.class);
    }

    private FavoriteRecord convertFavoriteRecord(SDKVideoAlbum sdkVideoAlbum) {
        Gson gson = new Gson();
        String json = gson.toJson(sdkVideoAlbum);
        FavoriteRecord record = gson.fromJson(json, FavoriteRecord.class);
        Log.d(TAG, "sdkVideoAlbum playtime:" + sdkVideoAlbum.getPlaytime());
        record.setPlaytime(sdkVideoAlbum.getPlaytime());
        return record;
    }
}
