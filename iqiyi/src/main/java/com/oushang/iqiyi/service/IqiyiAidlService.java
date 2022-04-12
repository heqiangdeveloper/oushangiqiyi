package com.oushang.iqiyi.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.oushang.iqiyi.IqiyiBanner;
import com.oushang.iqiyi.IqiyiBannerCallback;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.entries.BannerEntry;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.utils.RetryWithDelay;
import com.oushang.lib_base.log.LogUtils;
import com.oushang.lib_service.MediatorServiceFactory;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.RecommendInfo;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.VideoManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class IqiyiAidlService extends BaseAidlService {
    private static final String TAG = IqiyiAidlService.class.getSimpleName();
    private final IBinder mAidlServiceBinder = new IqiyiAidlServiceStub(this);
    private final RemoteCallbackList<IqiyiBannerCallback> mRemoteCallbackList = new RemoteCallbackList<>();

    @Autowired(name = Constant.PATH_SERVICE_VIDEO_MANAGER)
    VideoManager mVideoManager;

    List<BannerEntry> bannerEntryList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        sendServiceStartBroadcast();
        if (mVideoManager == null) {
            mVideoManager = (VideoManager) ARouter.getInstance().build(Constant.PATH_SERVICE_VIDEO_MANAGER).navigation();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mAidlServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }


    private void sendServiceStartBroadcast() {
        Log.d(TAG, "send broadcast");
        Intent intent = new Intent();
        intent.setAction("com.oushang.iqiyi.iqiyiAidlService.ready");
        sendBroadcast(intent);
    }

    private void requestBanner() {
        Log.d(TAG, "requestBanner");
        if (bannerEntryList != null && !bannerEntryList.isEmpty()) {
            Log.d(TAG, "bannerEntryList:" + bannerEntryList);
            remoteCallback(bannerEntryList);
        } else {
            postData(true);
        }
    }

    private synchronized void postData(boolean isRemote) {
        Log.d(TAG, "postData:" + isRemote);
        if (bannerEntryList.isEmpty()) {
            mRxUtils.addDisposable(
                    execute(mVideoManager.getRecommendInfo(null, null, -1, -1), recommendInfos -> {
                        for (RecommendInfo recommendInfo : recommendInfos) {
                            List<VideoInfo> videoList = recommendInfo.getVideoList();
                            if (videoList != null && !videoList.isEmpty()) {
                                VideoInfo videoInfo = videoList.get(0);
                                long qipuId = videoInfo.getQipuId();
                                long albumId = videoInfo.getAlbumId();
                                String albumPic = videoInfo.getAlbumPic();
                                String title = videoInfo.getShortName();

                                if (albumPic != null && albumPic.isEmpty()) {
                                    albumPic = videoInfo.getPosterPic();
                                }
                                if (title != null && title.isEmpty()) {
                                    title = videoInfo.getName();
                                }
                                bannerEntryList.add(new BannerEntry(qipuId, albumId, albumPic, title));
                            }
                        }
                        Log.d(TAG, "requestBanner success: bannerEntries:" + bannerEntryList);
                        if (isRemote) {
                            remoteCallback(bannerEntryList);
                        }
                    }, throwable -> {
                        Log.e(TAG, "requestBanner error:" + Log.getStackTraceString(throwable));
                        remoteCallback(bannerEntryList);
                        return true;
                    }));
        } else {
            if (isRemote) {
                remoteCallback(bannerEntryList);
            }
        }

    }


    private void remoteCallback(List<BannerEntry> bannerEntries) {
        Log.d(TAG, "remoteCallback:" + bannerEntries);
        int size = mRemoteCallbackList.beginBroadcast();
        Log.d(TAG, "size:" + size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                try {
                    Log.d(TAG, "onBannerCallBack:" + bannerEntries);
                    mRemoteCallbackList.getBroadcastItem(i).onBannerCallBack(bannerEntries);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mRemoteCallbackList.finishBroadcast();
        }
    }

    public class IqiyiAidlServiceStub extends IqiyiBanner.Stub {
        private final WeakReference<IqiyiAidlService> mService;

        public IqiyiAidlServiceStub(IqiyiAidlService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public boolean hasPausePlayVideo() throws RemoteException {
            return false;
        }

        @Override
        public boolean hasRecentPlayVideo() throws RemoteException {
            return false;
        }

        @Override
        public void requestBannerData() throws RemoteException {
            Log.d(TAG, "call requestBannerData()");
            requestBanner();
        }

        @Override
        public void registerCallback(IqiyiBannerCallback callback) throws RemoteException {
            Log.d(TAG, "registerCallback()");
            if (mRemoteCallbackList != null) {
                mRemoteCallbackList.register(callback);
            }
        }

        @Override
        public void unRegisterCallback(IqiyiBannerCallback callback) throws RemoteException {
            Log.d(TAG, "unRegisterCallback()");
            if (mRemoteCallbackList != null) {
                mRemoteCallbackList.unregister(callback);
            }
        }

    }


    private static final int DEFAULT_MAX_RETRY_TIMES = 3;
    private static final int DEFAULT_RETRY_DELAY = 3;

    private <T> Observable<T> executeObserver(Observable<T> observable) {
        return observable
                .observeOn(AndroidSchedulers.mainThread())
//                .retryWhen(new RetryWithDelay(DEFAULT_RETRY_DELAY, DEFAULT_MAX_RETRY_TIMES))
                .subscribeOn(Schedulers.io())
                .onErrorReturn(new io.reactivex.functions.Function<Throwable, T>() {
                    @Override
                    public T apply(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "observer error");
                        return null;
                    }
                });
    }

    protected <T> Disposable execute(Observable<T> observable, @NonNull Consumer<? super T> onSuccess, @NonNull Function<Throwable, Boolean> handleError) {
        return executeObserver(observable)
                .subscribe(onSuccess, handleError::apply);
    }
}