package com.oushang.lib_service.player.iqiyi;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.oushang.lib_service.R;
import com.oushang.lib_service.entries.BitStream;
import com.oushang.lib_service.entries.PlayerInfo;
import com.oushang.lib_service.entries.VideoRate;
import com.oushang.lib_service.player.IPlayDataSource;
import com.oushang.lib_service.player.IPlayView;
import com.oushang.lib_service.player.IPlayer;
import com.oushang.lib_service.response.ReturnCode;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 爱奇艺播放视图
 * @Time: 2021/7/28 11:50
 * @Since: 1.0
 */
public class IqiyiPlayView extends RelativeLayout implements IPlayView {
    private static final String TAG = IqiyiPlayView.class.getSimpleName();

    private IPlayer mIPlayer;

    private boolean isAttach;

    public OnStateChangedListener mOnStateChangedListener;
    public OnBitStreamInfoListener mOnBitStreamInfoListener;
    public OnHeaderTailerInfoListener mOnHeaderTailerInfoListener;
    public OnBufferChangedListener mOnBufferChangedListener;
    public OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    public OnSeekChangedListener mOnSeekChangedListener;
    public OnPreviewInfoListener mOnPreviewInfoListener;
    public OnInfoListener mOnInfoListener;
    public OnBitStreamChangeListener mOnBitStreamChangeListener;
    public OnPlayNextListener mOnPlayNextListener;
    public OnCapturePictureListener mOnCapturePictureListener;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public IqiyiPlayView(@NonNull Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public IqiyiPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public IqiyiPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public IqiyiPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IqiyiPlayView, defStyleAttr, defStyleRes);
        Drawable background = a.getDrawable(R.styleable.IqiyiPlayView_back_ground);
        setBackground(background);
        a.recycle();
    }

    private void checkAttach() {
        if (!isAttach || mIPlayer == null) {
            throw new IllegalArgumentException("play view must be attach player!");
        }
    }

    @Override
    public void attach(IPlayer player) {
        this.mIPlayer = player;
        isAttach = true;
        mIPlayer.setDisplay(this);
    }

    @Override
    public void detach() {
        checkAttach();
        mIPlayer.release();
        mIPlayer = null;
        isAttach = false;
    }

    @Override
    public void doPlay() {
        checkAttach();
        mIPlayer.doPlay();
    }

    @Override
    public void start() {
        checkAttach();
        mIPlayer.prepareAsync();
        mIPlayer.start();
    }

    @Override
    public void pause() {
        checkAttach();
        mIPlayer.pause();
    }

    @Override
    public void seekTo(long milliSec) {
        checkAttach();
        mIPlayer.seekTo(milliSec);
    }

    @Override
    public void stop() {
        checkAttach();
        mIPlayer.stop();
    }

    @Override
    public void stop(boolean val) {
        checkAttach();
        mIPlayer.stop(val);
    }

    @Override
    public void sleep() {
        checkAttach();
        mIPlayer.sleep();
    }

    @Override
    public void wakeUp() {
        checkAttach();
        mIPlayer.wakeUp();
    }

    @Override
    public void release() {
        checkAttach();
        mOnStateChangedListener = null;
        mOnPreviewInfoListener = null;
        mOnBitStreamChangeListener = null;
        mIPlayer.release();
    }

    @Override
    public void capturePicture() {
        checkAttach();
        mIPlayer.capturePicture();
    }

    @Override
    public int getCurrentPosition() {
        checkAttach();
        return mIPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        checkAttach();
        return mIPlayer.getDuration();
    }

    @Override
    public int getBufferLength() {
        checkAttach();
        return mIPlayer.getBufferLength();
    }

    @Override
    public boolean isAttach() {
        return isAttach;
    }

    @Override
    public boolean isPlaying() {
        checkAttach();
        return mIPlayer.isPlaying();
    }

    @Override
    public boolean isPause() {
        checkAttach();
        return mIPlayer.isPause();
    }

    @Override
    public boolean isSleeping() {
        checkAttach();
        return mIPlayer.isSleeping();
    }

    @Override
    public void setDataSource(IPlayDataSource source) {
        if (source instanceof IqiyiPlayDataSource) {
            checkAttach();
            mIPlayer.setDataSource(source);
        }
    }

    @Override
    public ViewGroup obtainDisplay() {
        return this;
    }

    @Override
    public int getCurrentState() {
        checkAttach();
        return mIPlayer.getCurrentState();
    }

    @Override
    public List<VideoRate> getAllBitStream() {
        checkAttach();
        return mIPlayer.getAllBitStream();
    }

    @Override
    public int getCurrentBitStream() {
        checkAttach();
        return mIPlayer.getCurrentBitStream();
    }

    @Override
    public String getRateName(int rt) {
        checkAttach();
        return mIPlayer.getRateName(rt);
    }

    @Override
    public boolean switchBitStream(int rt) {
        checkAttach();
        return mIPlayer.switchBitStream(rt);
    }

    public int getVideoRadioSize() {
        checkAttach();
        return mIPlayer.getVideoRadioSize();
    }

    @Override
    public void setSkipHeadAndTail(boolean skip) {
        checkAttach();
        mIPlayer.setSkipHeadAndTail(skip);
    }

    @Override
    public boolean isSkipSlide() {
        checkAttach();
        return mIPlayer.isSkipSlide();
    }

    @Override
    public void configVideoViewSize(Configuration configuration, Activity activity) {
        checkAttach();
        mIPlayer.configVideoViewSize(configuration, activity);
    }


    public void setVideoRadioSize(int radioSize) {
        checkAttach();
        mIPlayer.setVideoRadioSize(radioSize);
    }

    public void setOnStateChangedListener(OnStateChangedListener listener){
        this.mOnStateChangedListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && mOnVideoSizeChangedListener != null && isAttach) {
            mOnVideoSizeChangedListener.onVideoSizeChanged();
        }
    }

    public void setOnBitStreamInfoListener(OnBitStreamInfoListener listener){
        this.mOnBitStreamInfoListener = listener;
    }

    public void setOnHeaderTailerInfoListener(OnHeaderTailerInfoListener listener) {
        this.mOnHeaderTailerInfoListener = listener;
    }

    public void setOnBufferChangedListener(OnBufferChangedListener listener){
        this.mOnBufferChangedListener = listener;
    }

    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener){
        this.mOnVideoSizeChangedListener = listener;
    }

    public void setOnSeekChangedListener(OnSeekChangedListener listener){
        this.mOnSeekChangedListener = listener;
    }

    public void setOnPreviewInfoListener(OnPreviewInfoListener listener){
        this.mOnPreviewInfoListener = listener;
    }

    public void setOnInfoListener(OnInfoListener listener) {
        this.mOnInfoListener = listener;
    }

    public void setOnBitStreamChangeListener(OnBitStreamChangeListener listener){
        this.mOnBitStreamChangeListener = listener;
    }

    public void setOnPlayNextListener(OnPlayNextListener listener){
        this.mOnPlayNextListener = listener;
    }

    public void setOnCapturePictureListener(OnCapturePictureListener listener) {
        this.mOnCapturePictureListener = listener;
    }

    /**
     * 播放状态监听
     */
    public interface OnStateChangedListener {

        void onPrepared(int state); //Prepared

        void onMovieStart(PlayerInfo playerInfo, int state); //movieStart

        void onPaused(int state); //Paused

        void onPlaying(int state);//playing

        void onCompleted(int state); //Completed

        void onStopped(int state); //Stopped

        void onError(ReturnCode.ErrorCode errorCode, int state); //Error

        void onProgressChanged(long progress);

        void onBufferingUpdate(boolean update);//视频是否需要缓冲

        void onConcurrentTip();//并发用户提示

        default void onDrmChange(boolean change){}

        void onFetchPlayerInfo(PlayerInfo playerInfo);

    }

    public interface OnBitStreamInfoListener{

        void onBitStreamListUpdated(List<BitStream> bitStreamList);

        void onBitStreamSelected(BitStream bitStream);

    }

    public interface OnHeaderTailerInfoListener{
        void onHeaderTailerInfoReady(int headerTime, int tailerTime);
    }

    public interface OnBufferChangedListener{
        void onBufferStart();
        void onBufferEnd();
    }

    public interface OnVideoSizeChangedListener{
        void onVideoSizeChanged();
    }

    public interface OnSeekChangedListener {
        void onSeekStarted();
        void onSeekCompleted();
    }

    public interface OnPreviewInfoListener{
        void onPreviewInfoReady(int previewType, int previewTimeMs, int videoRightTipType);
        void showVipMask(String previewInfo);
        void onPreviewInfoFinish();
    }

    public interface OnInfoListener{
        void onInfo();
    }

    public interface OnBitStreamChangeListener{
        void OnBitStreamChanging(int from, int to);
        void OnBitStreamChanged(int i);
        void onRateChangeFail(int i, int i1, int i2);
    }

    public interface OnPlayNextListener{
        void onPlayNext();
    }

    public interface OnCapturePictureListener {
        void onCapturePicture(Bitmap bitmap);
    }

}
