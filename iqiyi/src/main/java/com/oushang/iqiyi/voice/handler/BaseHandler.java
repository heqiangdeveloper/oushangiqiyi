package com.oushang.iqiyi.voice.handler;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chinatsp.ifly.ISpeechTtsResultListener;
import com.chinatsp.ifly.ISpeechTtsStopListener;
import com.chinatsp.ifly.voiceadapter.Business;
import com.chinatsp.ifly.voiceadapter.SpeechServiceAgent;
import com.chinatsp.ifly.voiceadapter.SpeechTtsResultListener;
import com.chinatsp.ifly.voiceadapter.SpeechTtsStopListener;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.utils.RxUtils;
import com.oushang.iqiyi.voice.VoiceManager;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.interfaces.PlayListManager;
import com.oushang.lib_service.interfaces.PlayManager;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * @Author: zeelang
 * @Description: 语音处理
 * @Time: 2021/11/2 0002  18:42
 * @Since: 1.0
 */
public abstract class BaseHandler<T> implements EventBusHelper.EventListener {
    protected static final String TAG = "xyj_iqiyi";

    @Autowired(name = Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager;

    @Autowired(name = Constant.PATH_SERVICE_PLAY_LIST_MANAGER)
    PlayListManager mPlayListManager;

    RxUtils rxUtils;

    EventBusHelper mEventBusHelper;

    public BaseHandler() {
        ARouter.getInstance().inject(this);
        rxUtils = RxUtils.newInstance();
        mEventBusHelper = new EventBusHelper(this);
        mEventBusHelper.register();
    }

    /**
     * 处理方法
     *
     * @param t       数据模型
     * @param context context
     */
    public abstract void handler(T t, Context context);

    /**
     * 事件处理
     *
     * @param event 事件
     */
    @Override
    public void onEvent(EventBusHelper.Event event) {

    }

    /**
     * 语音播报
     * @param shownIfly 是否显示（语音形象）
     * @param secondsr 是否二次交互
     * @param conditionId 播报方案id
     * @param data  播报携带数据
     * @param tts  播报文案回调
     * @param complete 播报完成回调
     */
    public void speak(boolean shownIfly, boolean secondsr, String conditionId, Map data, Consumer<String> tts, Consumer<Boolean> complete) {
        _speak(shownIfly, secondsr, conditionId, data, tts, complete);
    }

    /**
     * 语音播报,播报完自动退出
     *
     * @param shownIfly   是否显示（语音形象）
     * @param secondsr    是否二次交互
     * @param conditionId 播报方案id
     * @param data        播报携带数据
     */
    public void speak(boolean shownIfly, boolean secondsr, String conditionId, Map data) {
        _speak(shownIfly, secondsr, conditionId, data, null, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    hideVoice();
                }
            }
        });
    }

    /**
     * 语音播报,播报完自动退出
     * @param shownIfly   是否显示（语音形象）
     * @param secondsr    是否二次交互
     * @param conditionId 播报方案id
     * @param data        播报携带数据
     * @param tts         播报文案回调
     */
    public void speak(boolean shownIfly, boolean secondsr, String conditionId, Map data, Consumer<String> tts) {
        _speak(shownIfly, secondsr, conditionId, data, tts, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    hideVoice();
                }
            }
        });
    }


    /**
     * 语音播报
     *
     * @param shownIfly   是否显示（语音形象）
     * @param secondsr    是否二次交互
     * @param conditionId 播报方案id
     * @param data        播报携带数据
     * @param tts         播报文案回调
     * @param complete    播报完成回调
     */
    private void _speak(boolean shownIfly, boolean secondsr, String conditionId, Map data, Consumer<String> tts, Consumer<Boolean> complete) {
        try {
            if (VoiceManager.getInstance().isBindService()) {
                Log.d(TAG, "is bind SpeechServiceAgent");
                SpeechServiceAgent.getInstance().ttsSpeakListener(shownIfly, secondsr, conditionId, data, new SpeechTtsResultListener(new TtxClient(tts)), new SpeechTtsStopListener(new TtsStopClient(complete)));
            } else {
                Log.d(TAG, "is not bind SpeechServiceAgent, loop wait SpeechServiceAgent connect");
                rxUtils.executeTimeOut(0, 10, 0, 500, aLong -> {
                    Log.d(TAG, "along:" + aLong);
                    return Observable.just(VoiceManager.getInstance().isBindService());
                }, aBoolean -> {
                    Log.d(TAG, "takeUtil isBindService:" + aBoolean);
                    return aBoolean;
                }, aBoolean -> {
                    Log.d(TAG, "result isBindService:" + aBoolean);
                    if (aBoolean) {
                        try {
                            SpeechServiceAgent.getInstance().ttsSpeakListener(shownIfly, secondsr, conditionId, data, new SpeechTtsResultListener(new TtxClient(tts)), new SpeechTtsStopListener(new TtsStopClient(complete)));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "_speak exception:" + Log.getStackTraceString(e));
        }
    }

    /**
     * 通知当前在搜索节目页面
     */
    public void noticeVoiceSearch() {
        noticeShownWithTts(Business.IQIYI, Business.MODEL.IQIYI_SELECT, false,false,null,null,null,null);
    }

    /**
     * 通知当前所在业务
     * @param business  app
     * @param module    业务模块
     * @param shownIfly  是否显示
     * @param secondSr   是否二次交互
     * @param conditionId  播报方案id
     * @param data       数据
     * @param listener   播报文案回调
     * @param listener2  播报完成回调
     */
    private void noticeShownWithTts(int business, int module, boolean shownIfly, boolean secondSr, String conditionId, Map data, ISpeechTtsResultListener listener, ISpeechTtsStopListener listener2) {
        try {
            SpeechServiceAgent.getInstance().noticeShownWithTts(business, module, shownIfly, secondSr, conditionId, data, listener, listener2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 语音形象是否隐藏
     *
     * @return 是 true , 否 false
     */
    public boolean isHide() {
        try {
            return SpeechServiceAgent.getInstance().floatViewIsHide();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 隐藏语音形象
     */
    public void hideVoice() {
        try {
            SpeechServiceAgent.getInstance().hideVoiceAssistant();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * tts方案播报回调
     */
    static class TtxClient extends ISpeechTtsResultListener.Stub {
        private Consumer<String> mTtsConsumer;

        public TtxClient(Consumer<String> ttsConsumer) {
            this.mTtsConsumer = ttsConsumer;
        }

        public TtxClient(){}

        @Override
        public void onTtsCallback(String s) throws RemoteException { //方案
            Log.d(TAG, "onTtsCallback:" + s);
            if (mTtsConsumer != null) {
                try {
                    mTtsConsumer.accept(s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 播报完后退出回调
     */
    static class TtsStopClient extends ISpeechTtsStopListener.Stub {
        private Consumer<Boolean> mHideConsumer;

        public TtsStopClient(Consumer<Boolean> consumer) {
            this.mHideConsumer = consumer;
        }

        @Override
        public void onPlayStopped() throws RemoteException {
            Log.d(TAG, "onPlayStopped:");
            if (mHideConsumer != null) {
                try {
                    mHideConsumer.accept(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
