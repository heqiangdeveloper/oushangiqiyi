package com.oushang.iqiyi.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.oushang.iqiyi.R;
import com.oushang.iqiyi.base.BasePlayerActivity;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.entries.SelectionEntry;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.fragments.PlayVideoInfoDefinitionFragment;
import com.oushang.iqiyi.fragments.PlayVideoInfoMoreSelectionsFragment;
import com.oushang.iqiyi.fragments.PlayVideoInfoRatioFragment;
import com.oushang.iqiyi.fragments.PlayVideoInfoSynopsisFragment;
import com.oushang.iqiyi.fragments.fragmentUtils.FragmentHelper;
import com.oushang.iqiyi.mcu.CarManager;
import com.oushang.iqiyi.mvp.presenter.PlayerPresenter;
import com.oushang.iqiyi.mvp.view.IPlayerView;
import com.oushang.iqiyi.player.PlayerContentEvent;
import com.oushang.iqiyi.player.PlayerControlEvent;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.StatConstant;
import com.oushang.iqiyi.thirdscreen.ThirdScreenViewListener;
import com.oushang.iqiyi.ui.PlayerVideoGestureListener;
import com.oushang.iqiyi.ui.VideoGestureListener;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.RxUtils;
import com.oushang.iqiyi.utils.StatusBarUtil;
import com.oushang.iqiyi.utils.SteeringWheelControl;
import com.oushang.lib_base.utils.NetworkUtils;
import com.oushang.lib_base.utils.SPUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.callback.IPlayListLoadCompleteListener;
import com.oushang.lib_service.callback.IPlayVideoInfoChangeListener;
import com.oushang.lib_service.callback.IPlayerFullScreenListener;
import com.oushang.lib_service.constant.VideoNoPlay;
import com.oushang.lib_service.entries.PlayerInfo;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.entries.VideoRate;
import com.oushang.lib_service.interfaces.PlayManager;
import com.oushang.lib_service.player.IPlayer;
import com.oushang.lib_service.player.iqiyi.IqiyiPlayView;
import com.oushang.lib_service.player.iqiyi.IqiyiPlayerState;
import com.oushang.lib_service.response.ReturnCode;
import com.oushang.lib_service.utils.VideoRateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * @Author: zeelang
 * @Description: 播放界面
 * @Time: 2021/7/20 11:26
 * @Since: 1.0
 */
@Route(path = Constant.PATH_ACTIVITY_PLAYER)
public class PlayerActivity extends BasePlayerActivity<PlayerPresenter> implements IPlayerView {
    private static final String TAG = PlayerActivity.class.getSimpleName();

    @Autowired(name = Constant.PLAY_VIDEO_ID)
    long mQipuId; //视频id

    @Autowired(name = Constant.PLAY_POSITION)
    long playPosition; //起播位置

    @Autowired(name = Constant.PLAY_ALBUM_ID)
    long mAlbumId; //专辑Id

    @Autowired(name = Constant.PLAY_PUBLISH_YEAR)
    int year; //年份

    @Autowired(name = Constant.PLAY_IS_FULL_SCREEN)
    boolean isFullScreen; //是否全屏

    @BindView(R.id.player_layout)
    FrameLayout mPlayerLayout; //播放器界面嘏

    @BindView(R.id.video_player)
    IqiyiPlayView mVideoPlayer; //播放界面

    @BindView(R.id.video_play_title_bar)
    RelativeLayout mVideoTitleBar;  //视频头部信息

    @BindView(R.id.video_progress_time)
    TextView mVideoProgressTime; //播放进度时间

    @BindView(R.id.video_play_control)
    RelativeLayout mVideoPlayControl; //播放控制布局

    @BindView(R.id.video_play_name)
    TextView mVideoName;  //视频名称

    @BindView(R.id.video_play_progress_bar)
    RelativeLayout mSeekBarLayout; //播放进度布局

    @BindView(R.id.video_play_current_time)
    TextView mCurrentTime; //当前播放时长

    @BindView(R.id.video_play_seek_bar)
    AppCompatSeekBar mSeekBar; //播放进度条

    @BindView(R.id.video_play_total_time)
    TextView mTotalTime;  //视频总时长

    @BindView(R.id.video_play)
    ImageView mVideoPlay; //播放

    @BindView(R.id.video_play_next)
    ImageView mVideoNext; //下一集

    @BindView(R.id.video_play_hd)
    TextView mVideoHD; //清晰度选择

    @BindView(R.id.video_play_select)
    TextView mVideoSelect; //选集

    @BindView(R.id.video_play_ratio)
    TextView mVideoRatio; //显示比例

    @BindView(R.id.video_play_store)
    ImageView mVideoStore; //收藏

    @BindView(R.id.video_play_full_screen)
    ImageView mVideoFullScreen; //全屏

    @BindView(R.id.video_play_center)
    ImageView mMidPlay;  //播放/暂停图标显示

    @BindView(R.id.video_pause_center)
    ImageView mMidPause;

    @BindView((R.id.video_info_container))
    FrameLayout mVideoInfoContainer; //侧边栏视频信息简介

    @BindView(R.id.non_member_loading)
    ImageView mNonMemberLoading; //非会员/会员加载

    @BindView(R.id.non_member_tip)
    TextView mNonMemberTip; //非会员试看提示

    @BindView(R.id.open_membership_layout)
    LinearLayout mPreviewFinishLayout; //试看结束提示

    @BindView(R.id.open_membership_tips)
    TextView mOpenMemberShipTips; //开通会员提示

    @BindView(R.id.open_membership)
    Button mOpenMemberShipBtn;//开通会员

    @BindView(R.id.right_drawer_layout)
    DrawerLayout mRightDrawerLayout; //右侧滑布局

    @BindView(R.id.player_back)
    ImageView mPlayerBack; //返回键

    @BindView(R.id.network_exception_layout)
    LinearLayout mNetWorkException; //网络异常

    @BindView(R.id.player_float_layout)
    RelativeLayout mPlayFloatLayout; //播放界面浮层，用于显示播放控制信息

    @BindView(R.id.player_driving_safety_tips)
    TextView playerDrivingSafetyTipsTv; //行车提醒安全提示

    @BindView(R.id.video_buffer_layout)
    RelativeLayout mVideoBufferLayout; //缓冲界面

    @BindView(R.id.video_buffer_logo)
    ImageView mVideoBufferLogo;  //缓冲logo

    @BindView(R.id.video_buffer_anim)
    ImageView mVideoBufferAnim; //缓冲动画



    @BindView(R.id.play_shimmer)
    ShimmerLayout mShimmerLayout;

    @BindView(R.id.player_vip_concurrent_tip)
    TextView mVipConcurrnetTip; //并发用户提示

    private volatile boolean isTracking = false;

    private SeekBar.OnSeekBarChangeListener mSeekBarChaneListener;//进度条监听
    private DrawerLayout.DrawerListener mDrawerListener; //侧边栏监听

    RxUtils rxUtils;

    private int mOriginalProgress = 0;

    private boolean isInDrivingTip = false;

    private boolean isHidePreviewFinishLayout = false; //是否隐藏试看结束弹窗

    private Disposable mSdkInitDisposable;//sdk初始化Disposable

    private long mLastTrackingTime = 0;

    private int ratio = 0;

    @Override
    protected int setLayout() {
        return R.layout.activity_player;
    }

    @Override
    protected PlayerPresenter createPresenter() {
        return new PlayerPresenter();
    }

    @Override
    protected void initView() {
        super.initView();
        Log.d(TAG, "initView");
        rxUtils = RxUtils.newInstance();
        mRightDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);//关闭手势
        mRightDrawerLayout.setScrimColor(Color.TRANSPARENT);//去掉遮罩
        initEnable(false);
        //获取播放器
        tryAcquirePlayer();
    }

    private void initEnable(boolean enable) {
        mVideoHD.setEnabled(enable); //清晰度是否可用
        mVideoPlay.setEnabled(enable); //播放按钮是否可用
        mVideoRatio.setEnabled(enable);//画面比例是否可用
        setSeekBarEnable(enable); //进度条是否可用
        if (!NetworkUtils.isNetworkAvailable()) { //网络不可用
            mNetWorkException.setVisibility(View.VISIBLE); //显示网络异常
        }
    }

    @Override
    protected void initData() {
        super.initData();
        //获取视频id
        Log.d(TAG, "qipuId:" + mQipuId + ",albumId:" + mAlbumId + ",playPosition:" + playPosition + ",year:" + year);
        //是否跳过片头片尾
        initSkipHeadAndTail();
        //加载剧集列表
        loadEpisodeInfoList(mAlbumId, mQipuId, year);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        super.initListener();
        Log.d(TAG, "initListener");
        //进度条监听
        mSeekBar.setLongClickable(false);
        mSeekBarChaneListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { //当拖动条发生改变时触发
//                Log.d(TAG, "onProgressChanged:" + seekBar.getProgress() + ",progress:" + progress + ",fromUser:" + fromUser);
                if (fromUser) {
                    mPlayManager.seekTo(progress);
                }
                String time = AppUtils.parseMills(progress);
                mVideoProgressTime.setText(time); //中间显示播放时间
                mCurrentTime.setText(time); //左边显示当前进度
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { //当按住拖动条时会触发
                Log.d(TAG, "seekbar onStartTrackingTouch:" + seekBar.getProgress() + ",duration" + mVideoPlayer.getDuration());
                mSeekBarLayout.setVisibility(View.VISIBLE);
                showSeekBar(mVideoProgressTime, seekBar);
                isTracking = true;
                showBufferAnimation(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { //当松开拖动条时触发
                Log.d(TAG, "seekbar onStopTrackingTouch:" + seekBar.getProgress() + ",duration" + mVideoPlayer.getDuration());
                isTracking = false;
                mVideoProgressTime.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mVideoProgressTime != null) {
                            mVideoProgressTime.setVisibility(View.GONE);
                        }
                        if (mSeekBarLayout != null) {
                            mSeekBarLayout.setVisibility(View.GONE);
                        }
                    }
                }, 1000);
            }
        };
        //侧边栏监听
        mDrawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                FragmentHelper.popBackAll(getSupportFragmentManager());
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        //添加侧边栏监听
        mRightDrawerLayout.addDrawerListener(mDrawerListener);
        //注册方控
        SteeringWheelControl.register();
        //注册mcu事件
//        CarManager.getInstance().register();
        //播放视频信息监听
        mPlayManager.addPlayVideoInfoChangeListener(new IPlayVideoInfoChangeListener() {
            @Override
            public void onChange(VideoInfo videoInfo) {
                if (videoInfo != null) {
                    String name = videoInfo.getName();
                    long len = videoInfo.getLen();
                    if (videoInfo.isAlbum()) {
                        name = videoInfo.getDefaultEpi().getName();
                        len = videoInfo.getDefaultEpi().getLen();
                    }
                    Log.d(TAG, "len:" + len + ",name:" + name);
                    mVideoName.setText(name); //设置视频名称
                    if (!videoInfo.isSeries() || mPlayListManager.isLastPos()) {
                        mVideoNext.setEnabled(false);
                    } else {
                        if (!mVideoNext.isEnabled()) {
                            mVideoNext.setEnabled(true);
                        }
                    }

//                    Bundle eventParams = new Bundle();
//                    eventParams.putParcelable(EventConstant.EVENT_PARAMS_UPDATE_PLAY_VIDEO_INFO, videoInfo);
//                    EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_PLAY_VIDEO_INFO, eventParams));
                    presenter.postContentEvent(PlayerContentEvent.PLAYER_EVENT_POSITIVE, null);
                }
            }
        });
        //剧集加载完成监听，更多
        mPlayListManager.addLoadCompleteListener(new IPlayListLoadCompleteListener() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "load episodeInfo completed");
                mVideoNext.setEnabled(true); //下一集按钮可用
                mVideoSelect.setVisibility(View.VISIBLE);

                VideoInfo videoInfo = mPlayManager.getCurrentVideoInfo(); //获取当前播放的视频
                int playPos = mPlayListManager.getCurrentPlayPos(); //获取播单列表的当前位置
                Log.d(TAG, "current playPos:" + playPos);
                if (videoInfo != null) {
                    if (videoInfo.getSourceCode() == 0) { //是来源专辑
                        int order = videoInfo.getOrder();//当前播放的第几集
                        Log.d(TAG, "current order:" + order);
                        if (order > 0 && order - 1 != playPos) { //当前播放的位置与播单位置不一致时
                            Log.d(TAG, "update play pos:" + (order - 1));
                            mPlayListManager.updatePlayPos(order - 1);//更新播单的播放位置
                        }
                    } else { //非来源专辑
                        int pos = mPlayListManager.getPos(videoInfo); //获取播单列表的视频位置
                        Log.d(TAG, "current pos:" + pos);
                        if (pos >= 0 && pos != playPos) { //当前播放的位置与播单位置不一致时
                            Log.d(TAG, "update play pos:" + pos);
                            mPlayListManager.updatePlayPos(pos);//更新播单的播放位置
                        }
                    }
                    mPlayListManager.updatePartitionPos(mPlayListManager.getPartitionPos(videoInfo));//更新分集的位置
                }

                EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_EPISODE_LOAD_COMPLETE));
            }
        });
        //全屏播放视频监听
        mPlayManager.setPlayerFullScreenListener(new IPlayerFullScreenListener() {
            @Override
            public void onChange(boolean isFull) {
                Log.d(TAG, "PlayerFullScreenListener:" + isFull);
                Bundle eventParams = new Bundle();
                eventParams.putBoolean(EventConstant.EVENT_PARAMS_FULL_SCREEN_CHANGED, isFull);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_FULL_SCREEN_CHANGED, eventParams));

                if (isFull) { //全屏
                    mVideoInfoContainer.setVisibility(View.INVISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayerLayout.getLayoutParams();
                    layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                    mPlayerLayout.setLayoutParams(layoutParams);
                    mVideoFullScreen.setBackground(getDrawable(R.drawable.ic_exit_full_screen));
                } else { //非全屏
                    mVideoInfoContainer.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayerLayout.getLayoutParams();
                    layoutParams.width = (int) getResources().getDimension(R.dimen.player_width);
                    mPlayerLayout.setLayoutParams(layoutParams);
                    mVideoFullScreen.setBackground(getDrawable(R.drawable.ic_full_screen));
                }
            }
        });
        mVideoPlayer.setOnVideoSizeChangedListener(new IqiyiPlayView.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged() {
                Log.d(TAG, "onVideoSizeChanged");
                mVideoPlayer.setVideoRadioSize(mVideoPlayer.getVideoRadioSize());
                setRatioSize(ratio);
            }
        });

        //是否全屏显示
        mPlayManager.setFullScreen(isFullScreen);
        //禁播监听
        VideoNoPlay.getInstance().setVideoNoPlayListener(new VideoNoPlay.VideoNoPlayListener() {
            @Override
            public void onChange(int reason) {
                Log.d(TAG, "no play reason:" + reason);
//                if(VideoNoPlay.getInstance().hasNoPlayReason(VideoNoPlay.NO_PLAY_REASON_AVM_ON)) {
//                    presenter.showToast("全景影像已打开，禁止播放！请退出全景影像，再继续播放");
//                }
                if (VideoNoPlay.getInstance().hasNoPlayReason(VideoNoPlay.NO_PLAY_REASON_OVER_SPEED)) { //车速
                    playerDrivingSafetyTipsTv.setVisibility(View.VISIBLE);
                    mPlayerLayout.setEnabled(false);
                    mVideoInfoContainer.setEnabled(false);
                } else {
                    playerDrivingSafetyTipsTv.setVisibility(View.GONE);
                    mPlayerLayout.setEnabled(true);
                    mVideoInfoContainer.setEnabled(true);
                }

            }
        });

    }

    //显示进度条
    private void showSeekBar(TextView progressTime, SeekBar seekBar) {
        if (progressTime != null && seekBar != null) {
            progressTime.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            float textWidth = progressTime.getMeasuredWidth();
            float seekBarLeft = seekBar.getLeft();
            float seekBarMax = seekBar.getMax();
            float seekBarWidth = seekBar.getWidth();
            float average = seekBarWidth/(seekBarMax/1000);
            float progress = (float)(seekBar.getProgress() / 1000);
            float pos = seekBarLeft - textWidth/2 + average * progress;
            progressTime.setX(pos);
        }
    }

    //尝试获取播放器
    private void tryAcquirePlayer() {
        if (mSdkInitDisposable != null && !mSdkInitDisposable.isDisposed()) {
            mSdkInitDisposable.dispose();
        }
        mSdkInitDisposable = rxUtils.executeTimeOut(0, 30, 0, 1000,
                aLong -> {
                    Log.d(TAG, "wait sdk init, long:" + aLong);
                    if (aLong == 3) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showBufferAnimation(true);
                            }
                        });
                    }
                    if (aLong == 6) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                presenter.showToast("首次加载时间有点长，请耐心等待");
                            }
                        });
                    }
                    return Observable.just(MediatorServiceFactory.getInstance().getIqiyiSdkManager().isSdkInitialized());
                },
                aBoolean -> aBoolean,
                aBoolean -> {
                    if (aBoolean) {
                        Log.d(TAG, "sdk is init success!");
                        showBufferAnimation(false);
                        //初始化播放器
                        initPlayer();
                    }
                });
        rxUtils.addDisposable(mSdkInitDisposable);
    }

    //初始化播放器
    private void initPlayer() {
        IPlayer iPlayer = mPlayManager.createPlayer(PlayManager.PLAYER_TYPE_IQIYI);
        mVideoPlayer.attach(iPlayer);
        mVideoPlayer.configVideoViewSize(new Configuration(), PlayerActivity.this);
        mPlayManager.setPlayView(mVideoPlayer);
        mPlayManager.setPlayer(iPlayer);
        initPlayerListener();
        doPlay(mAlbumId, mQipuId);
    }

    //起播
    private void doPlay(long albumId, long qipuId) {
        Log.d(TAG, "doPlay: albumId:" + albumId + ",qipuId:" + qipuId);
        long alId = 0;
        long tvId = 0;

        if (albumId != 0 && VideoInfo.isAlbumId(albumId)) {
            alId = albumId;
        } else if (albumId != 0 && VideoInfo.isVideoId(albumId)) {
            tvId = albumId;
        }
        if (qipuId != 0 && VideoInfo.isVideoId(qipuId)) {
            tvId = qipuId;
        } else if (qipuId != 0 && VideoInfo.isAlbumId(qipuId)) {
            alId = qipuId;
        }

        Log.d(TAG, "alId:" + alId + ",tvId:" + tvId);
        if (alId != 0 || tvId != 0) {
            mPlayManager.doPlay(alId, tvId);
        } else {
            presenter.showToast("参数错误!");
        }
    }

    //加载视频信息
    private void loadVideoInfo(long albumId, long qipuId) {
        Log.d(TAG, "loadVideoInfo: albumId:" + albumId + ",qipuId:" + qipuId);
        //加载专辑/节目详情信息
        if (qipuId != 0 && albumId != 0) {
            presenter.postLoadVideoInfo(qipuId); //加载视频信息
        } else if (qipuId != 0) { //视频id
            if (VideoInfo.isVideoId(qipuId)) { //如果是视频
                presenter.postLoadVideoInfo(qipuId);
            }
        } else if (albumId != 0) { //专辑id
            if (VideoInfo.isVideoId(albumId)) { //如果是视频
                presenter.postLoadVideoInfo(albumId);
            }
        } else {
            Log.e(TAG, "传参错误!" + ",qipuId:" + qipuId + ",albumId:" + albumId);
            presenter.showToast("参数错误!");
        }
    }

    //加载剧集列表
    private void loadEpisodeInfoList(long albumId, long qipuId, int year) {
        Log.d(TAG, "loadEpisodeInfoList: albumId:" + albumId + ",qipuId:" + qipuId + ",year:" + year);
        mPlayListManager.clear();//清空剧集列表
        long aid = 0;
        if (qipuId != 0 && albumId != 0) { //专辑id和视频id都不为0
            if (VideoInfo.isAlbumId(albumId)) { //如果是专辑id
                aid = albumId;
            } else if (VideoInfo.isAlbumId(qipuId)) { //如果是专辑id
                aid = qipuId;
            }
        } else if (qipuId != 0) { //视频id
            if (VideoInfo.isAlbumId(qipuId)) { //如果是专辑id
                aid = qipuId;
            }
        } else if (albumId != 0) { //专辑id
            if (VideoInfo.isAlbumId(albumId)) { //如果是专辑id
                aid = albumId;
            }
        }
        if (aid != 0 && VideoInfo.isAlbumId(aid)) { //专辑Id
            presenter.getAllEpisodeInfoList(aid, year, 0); //加载剧集列表
        }
    }

    //初始化播放器相关监听
    private void initPlayerListener() {
        //播放状态监听
        mVideoPlayer.setOnStateChangedListener(new IqiyiPlayView.OnStateChangedListener() {

            @Override
            public void onPrepared(int state) {
                Log.d(TAG, "onPrepared:" + state);
                mPlayManager.updatePlayState(state);
            }

            @Override
            public void onMovieStart(PlayerInfo playerInfo, int state) {
                Log.d(TAG, "onMovieStart:" + state + ",playerInfo:" + playerInfo);
                mPlayManager.updatePlayState(state);//更新播放状态
                int active = Settings.System.getInt(getContentResolver(), "btphone_active", 0);
                if (active == 1) {
                    Log.e(TAG, "Bluetooth call! pause");
                    mPlayManager.pause();
                }
                if (mPreviewFinishLayout.getVisibility() == View.VISIBLE) { //如果显示试看结束弹窗
                    mPreviewFinishLayout.setVisibility(View.GONE);//则隐藏
                }
                checkMute();//是否静音
//                loadVideoInfo(Long.parseLong(playerInfo.getAl_id()), Long.parseLong(playerInfo.getTv_id()));//加载视频信息

                initEnable(true);
                if (mVideoPlay != null) {
                    mVideoPlay.setActivated(!mVideoPlayer.isPause()); //设置播放状态
                }
                List<VideoRate> bitStreams = mVideoPlayer.getAllBitStream();
                mVideoHD.setEnabled(bitStreams == null || bitStreams.size() > 1); //如果只有一个清晰度，则不可点击

                String rateName = mVideoPlayer.getRateName(mVideoPlayer.getCurrentBitStream()); //获取当前清晰度
                mVideoHD.setText(rateName);//设置清晰度
                mSeekBar.setOnSeekBarChangeListener(mSeekBarChaneListener);

                if (mMyAccountManager.isLogin() && playerInfo != null) {
                    String alId = playerInfo.getAl_id();
                    String tvId = playerInfo.getTv_id();
                    Log.d(TAG, "alId:" + alId + ",tvId:" + tvId);

                    if (alId != null && !alId.isEmpty() && !alId.equals("0") && tvId != null && !tvId.isEmpty() && !tvId.equals("0")) {
                        presenter.checkIsFavorite(Long.parseLong(alId), Long.parseLong(tvId));
                    }
                }
                if (playerInfo != null) {
                    int order = playerInfo.getTv_order();
                    int pos = mPlayListManager.getCurrentPlayPos();
                    if (order >= 1 && order - 1 != pos) { //如果是记忆播放
                        Log.d(TAG, "remember play videoInfo");
                        mPlayListManager.updatePlayPos(order - 1);
                        mVideoName.setText(playerInfo.tv_title); //设置视频名称
                        VideoInfo videoInfo = mPlayListManager.getCurrentVideoInfo();

                        Bundle eventParams = new Bundle();
                        eventParams.putParcelable(EventConstant.EVENT_PARAMS_UPDATE_REMEMBER_PLAY_VIDEO_INFO, videoInfo);
                        EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_REMEMBER_PLAY_VIDEO_INFO, eventParams));
                        presenter.postContentEvent(PlayerContentEvent.PLAYER_EVENT_POSITIVE, null);
                    }

                }
                setRatioSize(mVideoPlayer.getVideoRadioSize());//设置画面比例
                initSeekBar(mVideoPlayer.getDuration());//设置播放进度条时间
                rxUtils.unDisposable();
                rxUtils.executeDelay(1, 5, 0, 1, aLong -> { //5s后自动隐藏播放控件
                    if (aLong == 5) {
                        hideView();
                    }
                });
                initGestureDetector();//播放手势
            }

            @Override
            public void onPaused(int state) {
                Log.d(TAG, "onPaused:" + state);
                mPlayManager.updatePlayState(state);
                if (mVideoPlay != null) {
                    mVideoPlay.setActivated(false);
                }
            }

            @Override
            public void onPlaying(int state) {
                Log.d(TAG, "onPlaying:" + state);
                mPlayManager.updatePlayState(state);
                if (mVideoPlay != null) {
                    mVideoPlay.setActivated(true);
                }
            }

            @Override
            public void onCompleted(int state) { //播放完成
                Log.d(TAG, "onCompleted:" + state);
                mPlayManager.updatePlayState(state);
                if (!mPlayListManager.isLastPos()) { //如果不是最后一集
                    mPlayManager.playNext(); //自动续播下一集
                } else { //如果是最后一集
                    if (mVideoNext != null)
                        mVideoNext.setEnabled(false);//下一集按钮显示不可点击状态
                }
            }

            @Override
            public void onStopped(int state) { //停止播放
                Log.d(TAG, "onStopped:" + state);
                mPlayManager.updatePlayState(state);
                mVideoPlay.setActivated(false);
                mVideoProgressTime.setVisibility(View.GONE);
                if (mNonMemberTip.getVisibility() == View.VISIBLE) {//试看提示仍显示，则隐藏
                    mNonMemberTip.setVisibility(View.GONE);
                }

                setSeekBarEnable(false);
                mSeekBar.setOnSeekBarChangeListener(null);
            }

            @Override
            public void onError(ReturnCode.ErrorCode errorCode, int state) {
                Log.d(TAG, "onError:" + errorCode);
                mPlayManager.updatePlayState(state);
                if(errorCode != null) {
                    if (ReturnCode.ErrorCode.isAccountError(errorCode.getCode())) {
                        String msg = errorCode.getMsg();
                        presenter.showToast(msg);
                    } else {
                        presenter.showToast("播放过程出现错误!");
                    }
                } else {
                    Log.d(TAG, "replay");
                    mPlayManager.play(mPlayManager.getCurrentVideoInfo());//重播当前视频
                }
            }

            @Override
            public void onProgressChanged(long progress) {
//                Log.d(TAG, "progress:" + progress);
                if (!isTracking) {
                    seekBarProgress((int) progress, 0);
                }
            }

            @Override
            public void onBufferingUpdate(boolean update) {
                Log.d(TAG, "onBufferingUpdate:" + update);
                showBufferAnimation(update);
            }

            @Override
            public void onConcurrentTip() {
                Log.e(TAG, "账号并发");
                if (mVipConcurrnetTip.getVisibility() == View.GONE) {
                    mVipConcurrnetTip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFetchPlayerInfo(PlayerInfo playerInfo) {
                Log.d(TAG, "onFetchPlayerInfo:" + playerInfo);
                if (playerInfo != null) {
                    long alid = 0;
                    long tvid = 0;
                    if (playerInfo.getAl_id() != null && !playerInfo.getAl_id().isEmpty()) {
                        alid = Long.parseLong(playerInfo.getAl_id());
                    }
                    if (playerInfo.getTv_id() != null && !playerInfo.getTv_id().isEmpty()) {
                        tvid = Long.parseLong(playerInfo.getTv_id());
                    }
                    loadVideoInfo(alid, tvid);//加载视频信息
                }
            }
        });
        //试看信息监听
        mVideoPlayer.setOnPreviewInfoListener(new IqiyiPlayView.OnPreviewInfoListener() {
            @Override
            public void onPreviewInfoReady(int previewType, int previewTimeMs, int videoRightTipType) {
                Log.d(TAG, "previewType:" + previewType + ",previewTimeMs:" + previewTimeMs + ",videoRightTipType:" + videoRightTipType);
                Bundle args = new Bundle();
                args.putInt("PreviewType", previewType);
                args.putInt("PreviewTimeMs", previewTimeMs);
                args.putInt("VideoRightTipType", videoRightTipType);
                presenter.postContentEvent(PlayerContentEvent.PLAYER_EVENT_TRAILERS, args);
            }

            @Override
            public void showVipMask(String previewInfo) { //VIP专享电影
                Log.d(TAG, "previewInfo：" + previewInfo);
                Bundle args = new Bundle();
                args.putString("PreviewInfo", previewInfo);
                presenter.postContentEvent(PlayerContentEvent.PLAYER_EVENT_VIP_MASK, args);
            }

            @Override
            public void onPreviewInfoFinish() { //试看结束
                Log.d(TAG, "onPreviewInfoFinish");
                if (mNonMemberTip.getVisibility() == View.VISIBLE) {
                    mNonMemberTip.setVisibility(View.GONE);
                }
                if (mPreviewFinishLayout.getVisibility() == View.GONE) {
                    mMidPlay.setVisibility(View.GONE);
                    mMidPause.setVisibility(View.GONE);
                    mPreviewFinishLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        //清晰度切换
        mVideoPlayer.setOnBitStreamChangeListener(new IqiyiPlayView.OnBitStreamChangeListener() {
            @Override
            public void OnBitStreamChanging(int from, int to) {
                Log.d(TAG, "from:" + from + ",to:" + to);
                mVideoHD.setText(VideoRateUtil.getRateName(to));
                Bundle ep = new Bundle();
                ep.putInt(EventConstant.EVENT_PARAMS_PLAY_SELECT_DEFINITION, to);
                EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_PLAY_UPDATE_DEFINITION, ep));

                mRightDrawerLayout.closeDrawers();
            }

            @Override
            public void OnBitStreamChanged(int i) {
                Log.d(TAG, "OnBitStreamChanged:" + i);
            }

            @Override
            public void onRateChangeFail(int i, int i1, int i2) {
                Log.d(TAG, "onRateChangeFail:" + i + "," + i1 + "," + i2);
                mRightDrawerLayout.closeDrawers();
            }
        });
    }

    //初始化进度条
    private void initSeekBar(int max) {
        Log.d(TAG, "initSeekBar:" + max);
        if (max > 0) {
            if (mSeekBar != null) {
                mSeekBar.setMax(max); //总进度
                mSeekBar.setProgress(0); //当前进度
                mSeekBar.setSecondaryProgress(0); //缓冲进度
            }
            if (mCurrentTime != null) {
                mCurrentTime.setText(AppUtils.parseMills(0)); //左边显示当前进度
            }
            if (mTotalTime != null)
                mTotalTime.setText(AppUtils.parseMills(max)); //右边显示总进度
        }
    }

    //是否跳过片头片尾
    private void initSkipHeadAndTail() {
        boolean isSkip = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_SKIP_START_END, false);
        Log.d(TAG, "initSkipHeadAndTail isSkip:" + isSkip);
        mPlayManager.skipHeadAndTail(isSkip);
    }

    //播放进度条
    private void seekBarProgress(int progress, int bufferProgress) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress); //当前进度
            mSeekBar.setSecondaryProgress(bufferProgress); //缓冲进度
            mCurrentTime.setText(AppUtils.parseMills(progress)); //左边显示当前进度
        }
    }

    //设置seekbar是否可用
    private void setSeekBarEnable(boolean isEnable) {
        if (mSeekBar != null) {
            Log.d(TAG, "setSeekBarEnable:" + isEnable);
            mSeekBar.setClickable(isEnable);
            mSeekBar.setEnabled(isEnable);
            mSeekBar.setSelected(isEnable);
            mSeekBar.setFocusable(isEnable);
        } else {
            Log.e(TAG, "mSeekBar is null!");
        }
    }

    //隐藏控件
    private void hideView() {
        mVideoPlayControl.setVisibility(View.GONE);//播放控制器
        mVideoTitleBar.setVisibility(View.GONE);//视频标题
//        mCurrentTime.setVisibility(View.GONE);//当前播放时间
//        mSeekBar.setVisibility(View.GONE);//播放进度
//        mTotalTime.setVisibility(View.GONE);//视频总时长
        mSeekBarLayout.setVisibility(View.GONE);
        mPlayerBack.setVisibility(View.GONE); //返回键
        if (mNonMemberTip.getVisibility() == View.VISIBLE) { //非会员提示
            mNonMemberTip.setVisibility(View.GONE);
        }
    }

    //显示控件
    private void showView() {
        if (null != mVideoPlayControl) mVideoPlayControl.setVisibility(View.VISIBLE);//播放控制器
        if (null != mVideoTitleBar) mVideoTitleBar.setVisibility(View.VISIBLE);//视频标题
        mSeekBarLayout.setVisibility(View.VISIBLE);
//        if (null != mCurrentTime) mCurrentTime.setVisibility(View.VISIBLE);//当前播放时间
//        if (null != mSeekBar) mSeekBar.setVisibility(View.VISIBLE);//播放进度
//        if (null != mTotalTime) mTotalTime.setVisibility(View.VISIBLE);//视频总时长
        if (null != mPlayerBack) mPlayerBack.setVisibility(View.VISIBLE); //返回键
    }

    //非会员试看提示
    private void tryWatchTipsByNonMember(int previewTimeMs, String action) {
        if (mNonMemberTip.getVisibility() == View.GONE) {
            mNonMemberTip.setVisibility(View.VISIBLE);
        }
        if (mPreviewFinishLayout.getVisibility() == View.VISIBLE) {
            mPreviewFinishLayout.setVisibility(View.GONE);
        }
        int previewMinute = previewTimeMs / (60 * 1000);
        String tip = "免费观看前 " + previewMinute + " 分钟，完整观看请开通会员 ";
        int start = tip.length();
        SpannableStringBuilder span = new SpannableStringBuilder(tip);
        span.append(action);
        //设置点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //ARouter.getInstance().build(Constant.PATH_ACTIVITY_MEMBER_QRCODE).navigation();
                onClickOpenMember();
            }
        };
        span.setSpan(clickableSpan, start, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //去掉下划线
        span.setSpan(new UnderlineSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, start, span.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        //设置文本颜色
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getColor(R.color.color_non_member_tip_click_text)) {
            @Override
            public void updateDrawState(@NonNull TextPaint textPaint) {
                textPaint.setColor(getColor(R.color.color_non_member_tip_click_text));
            }
        };
        span.setSpan(colorSpan, start, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文本的span
        mNonMemberTip.setMovementMethod(new LinkMovementMethod());
        mNonMemberTip.setText(span);
    }

    //显示画面比例,必须在onMoveStart中调用
    private void setRatioSize(int ratio) {
        String ratioSize = "";
        switch (ratio) {
            case 0:
                ratioSize = "等比缩放";
                break;
            case 3:
                ratioSize = "满屏";
                break;
            case 100:
                ratioSize = "50%";
                break;
            case 101:
                ratioSize = "75%";
                break;
        }
        mVideoRatio.setText(ratioSize);
    }

    //显示缓冲画面
    private void showBufferAnimation(boolean isShow) {
        if(isShow) {
            mVideoBufferLayout.setVisibility( View.VISIBLE);
            int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
            if (status == 1 || mMyAccountManager.isLogin()) {
                boolean isVip = SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false);
                if (isVip) {
                    mVideoBufferLogo.setImageDrawable(getDrawable(R.drawable.vip_member_loading));
                } else {
                    mVideoBufferLogo.setImageDrawable(getDrawable(R.drawable.non_member_loading));
                }
            } else {
                mVideoBufferLogo.setImageDrawable(getDrawable(R.drawable.non_member_loading));
            }
            mVideoBufferAnim.setImageResource(R.drawable.video_buffer_anim_list);
            AnimationDrawable animationDrawable = (AnimationDrawable) mVideoBufferAnim.getDrawable();
            animationDrawable.start();
        } else {
            AnimationDrawable animationDrawable = (AnimationDrawable) mVideoBufferAnim.getDrawable();
            if(animationDrawable != null) {
                animationDrawable.stop();
            }
            mVideoBufferLayout.setVisibility(View.GONE);
        }
    }

    //播放手势，必须onMovieStart中调用
    @SuppressLint("ClickableViewAccessibility")
    private void initGestureDetector() {
        //播放手势监听
        GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new PlayerVideoGestureListener(new VideoGestureListener() {
            @Override
            public void onSingleTapGesture() {
                Log.d(TAG, "onSingleTapGesture");
                if (null != mVideoPlayControl) {
                    if (mVideoPlayControl.getVisibility() == View.VISIBLE) {
                        hideView();
                        rxUtils.unDisposable();
                    } else {
                        showView();
                        rxUtils.unDisposable();
                        rxUtils.executeDelay(1, 5, 0, 1, aLong -> { //5s后自动隐藏播放控件
                            if (aLong == 5) {
                                hideView();
                            }
                        });
                    }
                }
            }

            @Override
            public void onDoubleTapGesture() {
                Log.d(TAG, "onDoubleTapGesture");
                if (isInDrivingTip) {
                    return;
                }
                if (mPlayManager.isPlaying()) {
                    if (mPreviewFinishLayout.getVisibility() != View.VISIBLE) {
                        mMidPlay.setVisibility(View.VISIBLE);
                        mMidPlay.setImageResource(R.drawable.ic_mid_play);
                    }
                } else {
                    if (mPreviewFinishLayout.getVisibility() != View.VISIBLE) {
                        mMidPlay.setVisibility(View.VISIBLE);
                        mMidPlay.setImageResource(R.drawable.ic_mid_pause);
                    }
                }
                presenter.clickPlay();
                mVideoPlayer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mMidPlay) mMidPlay.setVisibility(View.GONE);
                    }
                }, 1000);
            }

            @Override
            public void onSeekGesture(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "distanceX:" + distanceX);
                int currentProgress = mVideoPlayer.getCurrentPosition();
                if (mOriginalProgress != currentProgress && mVideoPlayer.getCurrentState() != IqiyiPlayerState.MEDIA_PLAYER_STOPPED) {
                    mOriginalProgress = mVideoPlayer.getCurrentPosition();

                    int newProgress = 0;
                    int scale = mVideoPlayer.getDuration() / mSeekBar.getWidth() / 1000;
                    Log.d(TAG, "width:" + mSeekBar.getWidth() + ",measureWidth:" + mSeekBar.getMeasuredWidth() + "scale:" + scale);
                    if (scale <= 0) {
                        scale = 4;
                    }

                    if (distanceX < 0) { //向右滑
                        newProgress = (int) (mVideoPlayer.getCurrentPosition() + Math.abs(distanceX / scale) * 1000);
                    } else {
                        newProgress = (int) (mVideoPlayer.getCurrentPosition() - Math.abs(distanceX / scale) * 1000);
                    }

                    Log.d(TAG, "original progress:" + mOriginalProgress + ",new progress:" + newProgress);

                    if (newProgress > 0 && newProgress <= mVideoPlayer.getDuration() && !isInDrivingTip) {
                        if (newProgress > mVideoPlayer.getDuration()) {
                            newProgress = mVideoPlayer.getDuration();
                        }

                        mVideoPlayer.seekTo(newProgress);
                        mSeekBar.setSelected(true);

                        mSeekBarLayout.setVisibility(View.VISIBLE);
                        mVideoProgressTime.setVisibility(View.VISIBLE);
                        mVideoProgressTime.setText(AppUtils.parseMills(newProgress));

                        showSeekBar(mVideoProgressTime, mSeekBar);
                    }

                }

            }
        }));
        mVideoPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) { //手指抬起
                    mSeekBar.setSelected(false);
                    mVideoProgressTime.setVisibility(View.GONE);
                    mSeekBarLayout.setVisibility(View.GONE);
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    //检查是否静音
    private void checkMute() {
        if (CarManager.getInstance().isMute()) { //系统静音
            CarManager.getInstance().cancelMute(); //解除静音
        }
    }

    //点击返回键
    @OnClick(R.id.player_back)
    public void onClickBack() {
        mPlayerBack.setVisibility(View.GONE);
        if (mPlayManager.isPlaying()) {
            mPlayManager.pause();
        }

        EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_PLAY_RECORD)); //通知更新播放记录
        EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_FAVORITE_RECORD));//通知更新收藏记录
        finish();
    }

    //点击开通会员
    @OnClick(R.id.open_membership)
    public void onClickOpenMember() {
        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5958);//埋点数据
        int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS, 0);
        if (status == 1) {
            ARouter.getInstance().build(Constant.PATH_ACTIVITY_MEMBER_QRCODE).navigation();
        } else {
            String targetCls = "com.oushang.iqiyi" + ".activities.AccountActivity";
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName("com.oushang.iqiyi", targetCls);
            intent.setComponent(componentName);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("driveCenterAction", Constant.ACTION_VIP);
            startActivity(intent);
        }
    }

    //刷新网络
    @OnClick(R.id.network_exception)
    public void onRefreshNet() {
        Log.d(TAG, "onRefreshNet");
        if (!NetworkUtils.isNetworkAvailable()) {
            presenter.showToast(getString(R.string.network_not_connect));
        } else {
            VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_NETWORK_UNAVAILABLE);
            if (mNetWorkException != null && mNetWorkException.getVisibility() == View.VISIBLE) {
                mNetWorkException.setVisibility(View.GONE);
            }
        }
    }

    //点击播放控制
    @SuppressLint("NonConstantResourceId")
    @OnClick({
            R.id.video_play, R.id.video_play_next,
            R.id.video_play_hd, R.id.video_play_select,
            R.id.video_play_ratio, R.id.video_play_store,
            R.id.video_play_dts, R.id.video_play_full_screen
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_play: //点击播放
                presenter.clickPlay();
                break;
            case R.id.video_play_next: //点击下一集
                VideoInfo videoInfo = mPlayManager.getCurrentVideoInfo();
                if (videoInfo != null) {
                    Map<String, String> statValue = new HashMap<>();
                    statValue.put("item", videoInfo.getName());
                    statValue.put("plate", videoInfo.getName());
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5913, statValue);
                }
                presenter.clickNext();
                break;
            case R.id.video_play_hd: //点击高清
                List<VideoRate> videoRates = mVideoPlayer.getAllBitStream();
                if (videoRates == null || videoRates.isEmpty()) {
                    return;
                }
                int bitStream = mVideoPlayer.getCurrentBitStream();
                if (mPlayManager.isFullScreen()) {
                    mRightDrawerLayout.openDrawer(Gravity.RIGHT);
                    FragmentHelper.loadFragment(R.id.right_drawer_contaniner, getSupportFragmentManager(), PlayVideoInfoDefinitionFragment.newInstance(videoRates, bitStream), true);
                } else {
                    FragmentHelper.loadFragment(R.id.video_info_container, getSupportFragmentManager(), PlayVideoInfoDefinitionFragment.newInstance(videoRates, bitStream), true);
                }
                break;
            case R.id.video_play_select: //点击选集
                DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5914);
                long alId = 0;
                if (mAlbumId != 0 && VideoInfo.isAlbumId(mAlbumId)) {
                    alId = mAlbumId;
                } else if (mQipuId != 0 && VideoInfo.isAlbumId(mQipuId)) {
                    alId = mQipuId;
                }
                Log.d(TAG, "select selection:" + alId);
                if (mPlayManager.isFullScreen()) {
                    mRightDrawerLayout.openDrawer(Gravity.RIGHT);
                    if (alId != 0) {
                        FragmentHelper.loadFragment(R.id.right_drawer_contaniner, getSupportFragmentManager(), PlayVideoInfoMoreSelectionsFragment.newInstance(alId), true);
                    }
                } else {
                    if (alId != 0) {
                        FragmentHelper.loadFragment(R.id.video_info_container, getSupportFragmentManager(), PlayVideoInfoMoreSelectionsFragment.newInstance(alId), true);
                    }
                }
                break;
            case R.id.video_play_ratio: //点击画面比例
                int radioSize = mVideoPlayer.getVideoRadioSize();
                if (mPlayManager.isFullScreen()) {
                    mRightDrawerLayout.openDrawer(Gravity.RIGHT);
                    FragmentHelper.loadFragment(R.id.right_drawer_contaniner, getSupportFragmentManager(), PlayVideoInfoRatioFragment.newInstance(radioSize), true);
                } else {
                    FragmentHelper.loadFragment(R.id.video_info_container, getSupportFragmentManager(), PlayVideoInfoRatioFragment.newInstance(radioSize), true);
                }
                break;
            case R.id.video_play_store: //点击收藏
                if (mVideoStore.isSelected()) {
                    presenter.removeFavorite(mPlayManager.getCurrentVideoInfo());
                } else {
                    if (mMyAccountManager.isLogin()) {
                        presenter.addFavorite(mPlayManager.getCurrentVideoInfo(), mVideoPlayer.getCurrentPosition());
                    } else {
                        presenter.showToast("请登录后再收藏");
                    }
                }
                break;
            case R.id.video_play_dts: //点击DTS
                Intent intent = new Intent();
                intent.setClassName("com.chinatsp.settings", "com.chinatsp.settings.MainActivity");
                intent.putExtra("open_setting", "open_audio_soundeffect");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.video_play_full_screen: //点击全屏
                mPlayManager.setFullScreen(!mPlayManager.isFullScreen());
                break;

        }
    }

    /**
     * 加载节目详情信息回调
     *
     * @param videoInfo 视频信息
     */
    @Override
    public void onPlayerVideoInfo(VideoInfo videoInfo) {
        Log.d(TAG, "onPlayerVideoInfo:" + videoInfo);
        initSkipHeadAndTail(); //是否跳过片头片尾
        checkMute();
        mPlayManager.setCurrentVideoInfo(videoInfo);
        //加载剧集简介
        FragmentHelper.loadFragment(R.id.video_info_container, getSupportFragmentManager(), PlayVideoInfoSynopsisFragment.newInstance(videoInfo), false);
    }

    /**
     * @param event 播放内容事件
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPlayerContentEventListener(int event, Bundle args) {
        switch (event) {
            case PlayerContentEvent.PLAYER_EVENT_ADVERTISEMENT://广告
                break;
            case PlayerContentEvent.PLAYER_EVENT_POSITIVE://正片
                if (mPreviewFinishLayout.getVisibility() == View.VISIBLE) {
                    mPreviewFinishLayout.setVisibility(View.GONE);
                }
                break;
            case PlayerContentEvent.PLAYER_EVENT_TRAILERS://试看
                if (args != null) {
                    int previewTimeMs = args.getInt("PreviewTimeMs");
                    tryWatchTipsByNonMember(previewTimeMs, "点击开通");
                }
                break;
            case PlayerContentEvent.PLAYER_EVENT_VIP_MASK://vip专享
                showBufferAnimation(false);
                if (mPreviewFinishLayout.getVisibility() == View.GONE) {
                    mMidPlay.setVisibility(View.GONE);
                    mMidPause.setVisibility(View.GONE);
                    mPreviewFinishLayout.setVisibility(View.VISIBLE);
                    mOpenMemberShipTips.setText("爱奇艺VIP专享影片， 请开通VIP会员后观看");
                }
                break;
        }

    }

    /**
     * @param event 播放控制事件
     */
    @SuppressLint("WrongConstant")
    @Override
    public void onPlayerControlEventListener(int event) {
        switch (event) {
            case PlayerControlEvent.PLAYER_EVENT_PLAY: //播放
                Log.d(TAG, "PlayerControlEvent:" + "点击播放/暂停按钮");
                if (!mPlayManager.isPlaying() && mPlayManager.getCurrentPlayState() != IqiyiPlayerState.MEDIA_PLAYER_STOPPED) {
                    Log.d(TAG, "继续播放");
                    checkMute();
                    mPlayManager.start(true);
                    if (mPlayManager.isFullScreen()) {
                        VideoInfo videoInfo = mPlayManager.getCurrentVideoInfo();
                        if (videoInfo != null) {
                            Map<String, String> statValue = new HashMap<>();
                            statValue.put("item", videoInfo.getName());
                            statValue.put("plate", videoInfo.getChnName());
                            DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5957, statValue);//埋点数据
                        }
                    }

                } else if (mPlayManager.getCurrentPlayState() == IqiyiPlayerState.MEDIA_PLAYER_STOPPED) {
                    Log.d(TAG, "重播");
                    checkMute();
                    mPlayManager.play(mPlayManager.getCurrentVideoInfo());
                } else if (!mPlayManager.isPaused()) {
                    Log.d(TAG, "暂停播放");
                    mPlayManager.pause(true);
                    VideoInfo videoInfo = mPlayManager.getCurrentVideoInfo();
                    if (videoInfo != null) {
                        Map<String, String> statValue = new HashMap<>();
                        statValue.put("item", videoInfo.getName());
                        statValue.put("plate", videoInfo.getChnName());
                        DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5912, statValue);
                    }
                }
                break;
            case PlayerControlEvent.PLAYER_EVENT_NEXT: //下一集
                Log.d(TAG, "PlayerControlEvent:" + "切换下一集");
                if (!mPlayListManager.isLastPos()) {
                    mPlayManager.playNext();
                } else {
                    presenter.showToast("没有下一集了");
                }
                break;
            case PlayerControlEvent.PLAYER_EVENT_PAUSE://暂停
                mPlayManager.pause();
                break;
            case PlayerControlEvent.PLAYER_EVENT_SUBSCRIPT: //收藏
                VideoInfo videoInfo = mPlayManager.getCurrentVideoInfo();
                if (videoInfo != null) {
                    Map<String, String> statValue = new HashMap<>();
                    statValue.put("item", videoInfo.getName());
                    statValue.put("plate", videoInfo.getChnName());
                    DataStatistics.recordUiEvent(StatConstant.EVENT_ID_5922, statValue);
                }
                presenter.showToast("收藏成功");
                mVideoStore.setSelected(true);
                EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_FAVORITE_RECORD));
                break;
        }

    }

    /**
     * 是否收藏
     *
     * @param isFavorite true 已收藏， false 未收藏
     */
    @Override
    public void onSubscription(boolean isFavorite) {
        Log.d(TAG, "onSubscription:" + isFavorite);
        mVideoStore.setSelected(isFavorite);
//        EventBusHelper.postStick(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_UPDATE_FAVORITE_RECORD));
    }

    private void releasePlayer() {
        Log.d(TAG, "releasePlayer");
        if (mVideoPlayer != null) {
            Log.d(TAG, "release Player");
            mPlayManager.updatePlayState(mVideoPlayer.getCurrentState());
            mVideoPlayer.release();
        }
        SteeringWheelControl.unRegister();//解除方控
//        CarManager.getInstance().unRegister();//解除mcu事件
//        getContentResolver().unregisterContentObserver(mSettingsValueContentObserver);
    }

    private void releaseListener() {
        Log.d(TAG, "releaseListener");
        mSeekBarChaneListener = null;
        if (mRightDrawerLayout != null) {
            mRightDrawerLayout.removeDrawerListener(mDrawerListener);
            mDrawerListener = null;
        }
        VideoNoPlay.getInstance().removeVideoNoPlayListener();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory:" + level);
        Glide.get(this).onTrimMemory(level);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        ThirdScreenViewListener.getInstance().register();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        long qid = intent.getLongExtra(Constant.PLAY_VIDEO_ID, 0);
        long aid = intent.getLongExtra(Constant.PLAY_ALBUM_ID, 0);
        int year = intent.getIntExtra(Constant.PLAY_PUBLISH_YEAR, 0);
        Log.d(TAG, "onNewIntent: qipuId:" + qid + ",albumId:" + aid + ",year:" + year);
        mAlbumId = aid;
        mQipuId = qid;
        doPlay(aid, qid); //起播
        loadEpisodeInfoList(mAlbumId, mQipuId, year);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        ThirdScreenViewListener.getInstance().unRegister();
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        releaseListener();
        if (rxUtils != null) {
            rxUtils.unDisposable();
        }
        super.onDestroy();
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        int eventType = event.getEventType();
        Bundle eventParams = event.getEventParams();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_PLAY_SELECT_DEFINITION: //清晰度选择
                if (eventParams != null) {
                    int rate = eventParams.getInt(EventConstant.EVENT_PARAMS_PLAY_SELECT_DEFINITION);
                    Log.d(TAG, "you select definition:" + rate);
                    mPlayManager.switchBitStream(rate);
                }
                break;
            case EventConstant.EVENT_TYPE_PLAY_SELECT_SELECTION: //选集选择
                if (eventParams != null) {
                    Parcelable parcelable = eventParams.getParcelable(EventConstant.EVENT_PARAMS_PLAY_SELECT_SELECTION);
                    if (parcelable instanceof SelectionEntry) { //数字型选集
                        Log.d(TAG, "you select DigitalType:" + parcelable.toString());
                        VideoInfo videoInfo = ((SelectionEntry) parcelable).getVideoInfo();
                        if (videoInfo != null) {
                            int pos = videoInfo.getOrder();
                            mPlayListManager.updatePlayPos(pos - 1);
                            mPlayManager.play(videoInfo);
                        }
                    } else if (parcelable instanceof VideoInfo) { //时间型选集
                        Log.d(TAG, "you select TimeType:" + parcelable.toString());
                        VideoInfo videoInfo = (VideoInfo) parcelable;
                        if (videoInfo != null) {
                            int pos = videoInfo.getOrder();
                            mPlayListManager.updatePlayPos(pos - 1);
                            mPlayManager.play(videoInfo);
                        }
                    }
                    if (mPlayManager.isFullScreen() && mRightDrawerLayout != null) {
                        mRightDrawerLayout.closeDrawers();
                    }
                }
                break;

            case EventConstant.EVENT_TYPE_PLAY_SELECT_RATIO: //画面比例
                if (eventParams != null) {
                    ratio = eventParams.getInt(EventConstant.EVENT_PARAMS_PLAY_SELECT_RATIO); //获取画面比例
                    Log.d(TAG, "you select ratio:" + ratio);
                    mVideoPlayer.setVideoRadioSize(ratio);
                    setRatioSize(ratio);
                    mPlayManager.setFullScreen(true);
                    if (mPlayManager.isFullScreen() && mRightDrawerLayout != null) {
                        mRightDrawerLayout.closeDrawers();
                    }
                }
                break;
            case EventConstant.EVENT_TYPE_PLAY_SELECT_RELATED: //相关推荐
                if (eventParams != null) {
                    VideoInfo videoInfo = eventParams.getParcelable(EventConstant.EVENT_PARAMS_PLAY_SELECT_RELATE);
                    Log.d(TAG, "you select relate video info:" + videoInfo);
                    if (videoInfo.isAlbum()) {
                        long qipuId = videoInfo.getQipuId();
                        mPlayListManager.clear();
                        presenter.getAllEpisodeInfoList(qipuId, 0, 0);
                    } else if (videoInfo.getAlbumId() != 0) {
                        long albumId = videoInfo.getAlbumId();
                        mPlayListManager.clear();
                        presenter.getAllEpisodeInfoList(albumId, 0, 0);
                    }
                    mPlayManager.play(videoInfo);
                }
                break;
            case EventConstant.EVENT_TYPE_UPDATE_IS_REVERSE://处于倒车中
            case EventConstant.EVENT_TYPE_OPEN_AVM://全景打开中
                Log.d(TAG, "打开全景");
                if (null != mVideoPlayer && !mVideoPlayer.isPause()) {
                    mVideoPlayer.pause();
                }
                break;
            case EventConstant.EVENT_TYPE_UPDATE_CAR_SPEED://车速的上报
                if (eventParams != null) {
                    boolean isEnable = eventParams.getBoolean(EventConstant.EVENT_PARAMS_IS_ENABLE);
                    Log.d("CarControlUtils", "isEnable = " + isEnable);
                    if (playerDrivingSafetyTipsTv == null) {
                        return;
                    }
                    if (isEnable) {//播放页播放按钮可点击
                        isInDrivingTip = false;
                        playerDrivingSafetyTipsTv.setVisibility(View.GONE);
                        mPlayFloatLayout.setBackgroundResource(0);
                        mVideoPlay.setEnabled(true);
                        mVideoNext.setEnabled(true);
                        mVideoHD.setEnabled(true);
                        mVideoSelect.setEnabled(true);
                        mVideoRatio.setEnabled(true);
                        setSeekBarEnable(true);
                    } else {//播放页播放按钮不可点击
                        isInDrivingTip = true;
                        playerDrivingSafetyTipsTv.setVisibility(View.VISIBLE);
                        mPlayFloatLayout.setBackgroundResource(R.drawable.warn_bg);
                        mVideoPlay.setEnabled(false);
                        mVideoNext.setEnabled(false);
                        mVideoHD.setEnabled(false);
                        mVideoSelect.setEnabled(false);
                        mVideoRatio.setEnabled(false);
                        setSeekBarEnable(false);
                    }
                }
                break;
        }
    }

    @Override
    protected void onNetworkChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkChanged:" + isConnected);
        //有时数据已经打开连上，但是isConnected仍然是false，需要再去主动获取isConnected值
        if (!isConnected) {
            isConnected = NetworkUtils.isNetworkAvailable();
            Log.d(TAG, "onNetworkChanged check again:" + isConnected);
        }
        mVideoNext.setEnabled(isConnected);
        if (!isConnected) {
            showBufferAnimation(false);
            VideoNoPlay.getInstance().noPlay(VideoNoPlay.NO_PLAY_REASON_NETWORK_UNAVAILABLE);
            mPlayManager.pause();
            if (mPreviewFinishLayout.getVisibility() == View.VISIBLE) { //隐藏试看结束
                mPreviewFinishLayout.setVisibility(View.GONE);
                isHidePreviewFinishLayout = true;
            }
        } else {
            VideoNoPlay.getInstance().release(VideoNoPlay.NO_PLAY_REASON_NETWORK_UNAVAILABLE);
            mPlayManager.start();

            if (isHidePreviewFinishLayout) {
                if (mPreviewFinishLayout.getVisibility() == View.GONE) { //显示试看结束
                    mPreviewFinishLayout.setVisibility(View.VISIBLE);
                    isHidePreviewFinishLayout = false;
                }
            }
        }
        mVideoPlay.setEnabled(isConnected);

        if (mNetWorkException != null)
            mNetWorkException.setVisibility(isConnected ? View.GONE : View.VISIBLE);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged:" + hasFocus);
        if (hasFocus) {
            CarManager.getInstance().register();
            StatusBarUtil.hideStatusBar(this);
            String videoRequestAvm = AppUtils.getSystemProperty("VIDEO_REQUEST_AVM");
            Log.d(TAG, "videoRequestAvm:" + videoRequestAvm);
            if (mPlayManager.isPaused() && (videoRequestAvm.equals("0") || videoRequestAvm.equals(""))) {
                mPlayManager.start();
            }
            EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_HIDE_BASENAVACTIVITY, null));
            //监听sp
            SPUtils.getSP(Constant.SP_LOGIN_SPACE).registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        } else {
            CarManager.getInstance().unRegister();//解除mcu事件
            if (mPlayManager.isPlaying()) {
                mPlayManager.pause();
            }
            SPUtils.getSP(Constant.SP_LOGIN_SPACE).unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }

    //登录状态监听
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(Constant.SP_KEY_LOGIN_STATUS)) {
                Log.d(TAG, "onSharedPreferenceChanged:" + key);
                int status = SPUtils.getShareInteger(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_STATUS,0);
                if (status == 1 || mMyAccountManager.isLogin()) { //播放过程登录了
                    Log.d(TAG, "account login in");
                    boolean isVip = SPUtils.getShareBoolean(Constant.SP_LOGIN_SPACE, Constant.SP_KEY_LOGIN_ISVIP, false);
                    if (isVip) {
                        Log.d(TAG, "account is vip");
                        mPlayManager.stop(false);
                        doPlay(mAlbumId, mQipuId);
                    }
                }
            }
        }
    };
}
