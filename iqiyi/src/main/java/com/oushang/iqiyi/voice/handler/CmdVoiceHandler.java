package com.oushang.iqiyi.voice.handler;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.chinatsp.ifly.aidlbean.CmdVoiceModel;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.VoiceAsssit;
import com.oushang.iqiyi.voice.constants.CmdVoiceConstants;
import com.oushang.iqiyi.voice.constants.TtsConstants;

import io.reactivex.functions.Consumer;

/**
 * @Author: zeelang
 * @Description: 免唤醒、可见即可说回调处理
 * @Time: 2021/11/2 0002  18:24
 * @Since: 1.0
 */
public class CmdVoiceHandler extends BaseHandler<CmdVoiceModel> {

    public CmdVoiceHandler(){
        super();
    }

    private static class CmdVoiceHandlerHolder {
        static CmdVoiceHandler HOLDER = new CmdVoiceHandler();
    }

    public static CmdVoiceHandler getInstance() {
        return CmdVoiceHandlerHolder.HOLDER;
    }

    @Override
    public void handler(CmdVoiceModel cmdVoiceModel,Context context) {
        if (cmdVoiceModel != null) {
            String text = cmdVoiceModel.text;//获取免唤醒词
            Log.d(TAG, "text:" + text);
            switch (text) {
                case CmdVoiceConstants.PRE:
                case CmdVoiceConstants.PRE_1:
                case CmdVoiceConstants.PRE_2:
                case CmdVoiceConstants.PRE_3:
                case CmdVoiceConstants.PRE_4:
                    Log.d(TAG, "play prev");
                    if (!mPlayListManager.isFirstPos()) { //当前非第一集
                        mPlayManager.playPrev(); //切换上一集节目
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C7, null); //语音播报
                        }
                    } else { //当前为第一集或无上一集
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C8, null);//语音播报
                        }
                    }
                    break;
                case CmdVoiceConstants.NEXT:
                case CmdVoiceConstants.NEXT_1:
                case CmdVoiceConstants.NEXT_2:
                case CmdVoiceConstants.NEXT_3:
                case CmdVoiceConstants.NEXT_4:
                    Log.d(TAG, "play next");
                    if (!mPlayListManager.isLastPos()) { //当前非最后一集
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C9, null); //语音播报
                        }
                        mPlayManager.playNext(); //切换下一集
                    } else {
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C10, null);//语音播报
                        }
                    }
                    break;
                case CmdVoiceConstants.PAUSE:
                case CmdVoiceConstants.PAUSE_1:
                    Log.d(TAG, "play pause");
                    mPlayManager.pause(true);//相当于用户手动暂停
                    if (mPlayManager.isPlaying()) { //当前未暂停
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C11, null);//语音播报
                        }
                    } else {
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C11, null);//语音播报
                        }
                    }

                    break;
                case CmdVoiceConstants.PLAY:
                case CmdVoiceConstants.PLAY_1:
                    Log.d(TAG, "play start");
                    if (!isHide()) { //语音是否在前台显示
                        speak(true, false, TtsConstants.IQIYI_C13, null);//语音播报
                    }
                    mPlayManager.start(true);

                    break;
                case CmdVoiceConstants.FULL_SCREEN:
                    Log.d(TAG, "full screen");
                    if (!mPlayManager.isFullScreen()) { //当前非全屏
                        mPlayManager.setFullScreen(true);
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C14, null, new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                            .setAppName(TtsConstants.APP_NAME)
                                            .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                            .setObject(TtsConstants.INTENT_WINDOW_SIZE_CONTROL)
                                            .setResponse(cmdVoiceModel.response)
                                            .setProvider(TtsConstants.PROVIDER)
                                            .setTts(s)
                                            .setCondition(TtsConstants.CONDITION_NO_FULL_SCREEN)
                                            .setConditionId(TtsConstants.IQIYI_C14)
                                            .setPrimitive(cmdVoiceModel.text)
                                            .build());

                                }
                            }, null);//语音播报
                        } else {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_WINDOW_SIZE_CONTROL)
                                    .setResponse(cmdVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts("")
                                    .setCondition(TtsConstants.CONDITION_NO_FULL_SCREEN)
                                    .setConditionId(TtsConstants.IQIYI_C14)
                                    .setPrimitive(cmdVoiceModel.text)
                                    .build());
                        }
                    } else {
                        if (!isHide()) { //语音是否在前台显示
                            speak(true, false, TtsConstants.IQIYI_C15, null, new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                            .setAppName(TtsConstants.APP_NAME)
                                            .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                            .setObject(TtsConstants.INTENT_WINDOW_SIZE_CONTROL)
                                            .setResponse(cmdVoiceModel.response)
                                            .setProvider(TtsConstants.PROVIDER)
                                            .setTts(s)
                                            .setCondition(TtsConstants.CONDITION_IS_FULL_SCREEN)
                                            .setConditionId(TtsConstants.IQIYI_C15)
                                            .setPrimitive(cmdVoiceModel.text)
                                            .build());
                                }
                            }, null);//语音播报
                        } else {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_WINDOW_SIZE_CONTROL)
                                    .setResponse(cmdVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts("")
                                    .setCondition(TtsConstants.CONDITION_IS_FULL_SCREEN)
                                    .setConditionId(TtsConstants.IQIYI_C15)
                                    .setPrimitive(cmdVoiceModel.text)
                                    .build());
                        }
                    }
                    break;
                case CmdVoiceConstants.FIRST:
                case CmdVoiceConstants.FIRST_1:
                case CmdVoiceConstants.FIRST_2:
                case CmdVoiceConstants.FIRST_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,1);
                    break;
                case CmdVoiceConstants.SECOND:
                case CmdVoiceConstants.SECOND_1:
                case CmdVoiceConstants.SECOND_2:
                case CmdVoiceConstants.SECOND_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,2);
                    break;
                case CmdVoiceConstants.THIRD:
                case CmdVoiceConstants.THIRD_1:
                case CmdVoiceConstants.THIRD_2:
                case CmdVoiceConstants.THIRD_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,3);
                    break;
                case CmdVoiceConstants.FOURTH:
                case CmdVoiceConstants.FOURTH_1:
                case CmdVoiceConstants.FOURTH_2:
                case CmdVoiceConstants.FOURTH_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,4);
                    break;
                case CmdVoiceConstants.FIFTH:
                case CmdVoiceConstants.FIFTH_1:
                case CmdVoiceConstants.FIFTH_2:
                case CmdVoiceConstants.FIFTH_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,5);
                    break;
                case CmdVoiceConstants.SIXTH:
                case CmdVoiceConstants.SIXTH_1:
                case CmdVoiceConstants.SIXTH_2:
                case CmdVoiceConstants.SIXTH_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,6);
                    break;
                case CmdVoiceConstants.SEVENTH:
                case CmdVoiceConstants.SEVENTH_1:
                case CmdVoiceConstants.SEVENTH_2:
                case CmdVoiceConstants.SEVENTH_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,7);
                    break;
                case CmdVoiceConstants.EIGHTH:
                case CmdVoiceConstants.EIGHTH_1:
                case CmdVoiceConstants.EIGHTH_2:
                case CmdVoiceConstants.EIGHTH_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,8);
                    break;
                case CmdVoiceConstants.NINTH:
                case CmdVoiceConstants.NINTH_1:
                case CmdVoiceConstants.NINTH_2:
                case CmdVoiceConstants.NINTH_3:
                    selectNumOfVoiceSearch(cmdVoiceModel,9);
                    break;

            }
        }
    }

    public void selectNumOfVoiceSearch(CmdVoiceModel cmdVoiceModel,int num) {
        Log.d(TAG, "selectNumOfVoiceSearch:" + num);
        Bundle eventParams = new Bundle();
        eventParams.putInt(EventConstant.EVENT_PARAMS_VOICE_SEARCH_RESULT_SELECT, num);
        EventBusHelper.post(EventBusHelper.newEvent(EventConstant.EVENT_TYPE_VOICE_SEARCH_SELECT, eventParams));
        if (!isHide()) {
            speak(true, false, TtsConstants.IQIYI_C18, null, new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                            .setAppName(TtsConstants.APP_NAME)
                            .setScene(TtsConstants.SCENE_SELECT_VIDEO)
                            .setObject(TtsConstants.INTENT_SELECT_VIDEO)
                            .setResponse(cmdVoiceModel.response)
                            .setProvider(TtsConstants.PROVIDER)
                            .setTts(s)
                            .setCondition(TtsConstants.CONDITION_SELECT_IN_RANGE)
                            .setConditionId(TtsConstants.IQIYI_C18)
                            .setPrimitive(cmdVoiceModel.text)
                            .build());
                }
            }, new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        hideVoice();
                    }
                }
            });
        }
    }
}
