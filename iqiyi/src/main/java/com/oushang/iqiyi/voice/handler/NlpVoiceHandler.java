package com.oushang.iqiyi.voice.handler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatsp.ifly.aidlbean.NlpVoiceModel;
import com.oushang.iqiyi.MainApplication;
import com.oushang.iqiyi.activities.SearchActivity;
import com.oushang.iqiyi.common.Constant;
import com.oushang.iqiyi.events.EventBusHelper;
import com.oushang.iqiyi.events.EventConstant;
import com.oushang.iqiyi.manager.AppManager;
import com.oushang.iqiyi.statistics.DataStatistics;
import com.oushang.iqiyi.statistics.VoiceAsssit;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.iqiyi.voice.constants.NlpVoiceConstants;
import com.oushang.iqiyi.voice.constants.TtsConstants;
import com.oushang.lib_base.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * @Author: zeelang
 * @Description: 语义协议回调处理
 * @Time: 2021/11/2 0002  18:16
 * @Since: 1.0
 */
public class NlpVoiceHandler extends BaseHandler<NlpVoiceModel>{

    public NlpVoiceHandler() {
        super();
    }

    public static class NlpVoiceHandlerHolder {
        static NlpVoiceHandler HOLDER = new NlpVoiceHandler();
    }

    public static NlpVoiceHandler getInstance() {
        return NlpVoiceHandlerHolder.HOLDER;
    }

    @Override
    public void handler(NlpVoiceModel nlpVoiceModel,Context context) {
        if (nlpVoiceModel != null && TextUtils.equals(nlpVoiceModel.service, NlpVoiceConstants.SERVICE_IQIYI)) { //判断服务id
            String semantic = nlpVoiceModel.semantic; //获取语义
            String operation = nlpVoiceModel.operation;//获取操作类型
            Log.d(TAG, "semantic:" + semantic + ",operation:" + operation);
            switch (operation) {
                case NlpVoiceConstants.OP_SEARCH_VIDEO_NAME: //视频搜索
                    JSONObject jsonObject = JSON.parseObject(semantic);
                    JSONObject slots = jsonObject.getJSONObject("slots");
                    String name = slots.getString("video_name");
                    gotoSearch(nlpVoiceModel, name, context);
                    break;
                case NlpVoiceConstants.OP_SEARCH_CHANNEL: //频道搜索
                    JSONObject channel_semantic = JSON.parseObject(semantic);
                    JSONObject channel_slots = channel_semantic.getJSONObject("slots");
                    String actor = channel_slots.getString("actor"); //主演
                    String channel = channel_slots.getString("channel"); //频道
                    String searchKeyWord = "";
                    if(actor != null) {
                        searchKeyWord += actor;
                    }
                    if(channel != null) {
                        searchKeyWord += channel;
                    }

                    gotoSearch(nlpVoiceModel, searchKeyWord, context);

                    break;
                case NlpVoiceConstants.OP_INSTRUCTION: // 播放控制
                    JSONObject instruction = JSON.parseObject(semantic);
                    JSONObject sls = instruction.getJSONObject("slots");
                    String insType = sls.getString("insType");
                    Log.d(TAG, "insType:" + insType);
                    playControl(nlpVoiceModel,insType);
                    break;
                case NlpVoiceConstants.OP_PLAY_NEXT:
                    playControl(nlpVoiceModel,NlpVoiceConstants.CMD_NEXT);
                    break;
                case NlpVoiceConstants.OP_ESC_SCREEN:
                    Log.d(TAG, "ESC_SCREEN");
                    if (!mPlayManager.isFullScreen()) { //当前非全屏
                        if (!isHide()) {
                            speak(true, false, TtsConstants.IQIYI_C15_2, null);
                        }
                    } else {
                        if (!isHide()) {
                            speak(true, false, TtsConstants.IQIYI_C15_1, null);
                        }
                        mPlayManager.setFullScreen(false); //退出全屏
                    }
                    break;
                default: //防呆
                    String aware =  Settings.System.getString(MainApplication.getContext().getContentResolver(),"aware");
                    Map<String, String> map = new HashMap<>();
                    map.put("#VOICENAME#",aware);
                    speak(false,false, TtsConstants.MAIN_C14, map);
                    break;
            }
        } else if (nlpVoiceModel != null && TextUtils.equals(nlpVoiceModel.service, NlpVoiceConstants.SERVICE_APP)) {
            String semantic = nlpVoiceModel.semantic; //获取语义
            String operation = nlpVoiceModel.operation;//获取操作类型
            Log.d(TAG, "semantic:" + semantic + ",operation:" + operation);

            switch (operation) {
                case NlpVoiceConstants.OP_LAUNCH: //打开爱奇艺
                case NlpVoiceConstants.OP_QUERY:
                    JSONObject launch_semantic = JSON.parseObject(semantic);
                    JSONObject launch_slots = launch_semantic.getJSONObject("slots");
                    String appName = launch_slots.getString("name");

                    if (TtsConstants.APP_NAME.equals(appName)) {
                        Log.d(TAG, "打开爱奇艺");

                        long expirationTime = SPUtils.getShareLong(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_BALANCE_VALUE, 0L);
                        long currentTime = System.currentTimeMillis();
                        if (expirationTime <= currentTime) { //爱奇艺车机流量已到期
                            speak(true, false, TtsConstants.IQIYI_C2, null, new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    Log.d(TAG, "voice statistics");
                                    DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                            .setAppName(TtsConstants.APP_NAME)
                                            .setScene(TtsConstants.SCENE_OPEN)
                                            .setObject(TtsConstants.INTENT_OPEN)
                                            .setResponse(nlpVoiceModel.response)
                                            .setProvider(TtsConstants.PROVIDER)
                                            .setTts(s)
                                            .setCondition(TtsConstants.CONDITION_FLOW_EXPIRED)
                                            .setConditionId(TtsConstants.IQIYI_C2)
                                            .setPrimitive(nlpVoiceModel.text)
                                            .build());
                                }
                            });
                            openServiceMall(context, Constant.GOODSID);
                        } else { //爱奇艺车机流量未到期
                            ARouter.getInstance().build(Constant.PATH_ACTIVITY_SPLASH)
                                    .navigation();

                            boolean isAgree = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
                            if (!isAgree) { //用户同意相关协议
                                speak(true, false, TtsConstants.IQIYI_C21, null, new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                                .setAppName(TtsConstants.APP_NAME)
                                                .setScene(TtsConstants.SCENE_OPEN)
                                                .setObject(TtsConstants.INTENT_OPEN)
                                                .setResponse(nlpVoiceModel.response)
                                                .setProvider(TtsConstants.PROVIDER)
                                                .setTts(s)
                                                .setCondition(TtsConstants.CONDITION_DEFAULT)
                                                .setConditionId(TtsConstants.IQIYI_C21)
                                                .setPrimitive(nlpVoiceModel.text)
                                                .build());
                                    }
                                });
                            } else { //未同意
                                speak(true, false, TtsConstants.IQIYI_C1, null, new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                                .setAppName(TtsConstants.APP_NAME)
                                                .setScene(TtsConstants.SCENE_OPEN)
                                                .setObject(TtsConstants.INTENT_OPEN)
                                                .setResponse(nlpVoiceModel.response)
                                                .setProvider(TtsConstants.PROVIDER)
                                                .setTts(s)
                                                .setCondition(TtsConstants.CONDITION_FLOW_UNEXPIRED)
                                                .setConditionId(TtsConstants.IQIYI_C1)
                                                .setPrimitive(nlpVoiceModel.text)
                                                .build());

                                    }
                                });
                            }
                        }


                    }

                    break;
                case NlpVoiceConstants.OP_EXIT: //关闭爱奇艺
                    JSONObject exit_semantic = JSON.parseObject(semantic);
                    JSONObject exit_slots = exit_semantic.getJSONObject("slots");
                    String exit_app_name = exit_slots.getString("name");

                    if (TtsConstants.APP_NAME.equals(exit_app_name)) {
                        Log.d(TAG, "关闭爱奇艺");

                        speak(true, false, TtsConstants.IQIYI_C16, null,new Consumer<String>(){

                            @Override
                            public void accept(String s) throws Exception {
                                DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                        .setAppName(TtsConstants.APP_NAME)
                                        .setScene(TtsConstants.SCENE_QUIT)
                                        .setObject(TtsConstants.INTENT_QUIT)
                                        .setResponse(nlpVoiceModel.response)
                                        .setProvider(TtsConstants.PROVIDER)
                                        .setTts(s)
                                        .setCondition(TtsConstants.CONDITION_DEFAULT)
                                        .setConditionId(TtsConstants.IQIYI_C16)
                                        .setPrimitive(nlpVoiceModel.text)
                                        .build());

                            }
                        }, new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                Log.d(TAG, "isSpeak:" + aBoolean);
                                if (aBoolean) {
                                    hideVoice();
                                    AppManager.getAppManager().exitApp();
                                }
                            }
                        });
                    }

                    break;
                default:
                    String aware =  Settings.System.getString(MainApplication.getContext().getContentResolver(),"aware");
                    Map<String, String> map = new HashMap<>();
                    map.put("#VOICENAME#",aware);
                    speak(false,false, TtsConstants.MAIN_C14, map);
                    break;
            }


        }
    }

    @Override
    public void onEvent(EventBusHelper.Event event) {
        super.onEvent(event);
        Log.d(TAG, "onEvent");
        int eventType = event.getEventType();
        switch (eventType) {
            case EventConstant.EVENT_TYPE_VOICE_SEARCH_RESULT:
                int count = event.getEventParams().getInt(EventConstant.EVENT_PARAMS_VOICE_SEARCH_RESULT_COUNT, 0);
                VoiceAsssit voiceAsssit = event.getEventParams().getParcelable(EventConstant.EVENT_PARAMS_VOICE_SEARCH_VOICE_ASSSIT);
                Log.d(TAG, "search result count:" + count + ",voiceAsssit:" + voiceAsssit);
                if (count == 1) { //二次交互-有搜索结果且只有一个
                    if (!isHide()) {
                        speak(true, false, TtsConstants.IQIYI_C3_1, null, new Consumer<String>(){

                            @Override
                            public void accept(String s) throws Exception {
                                if (voiceAsssit != null) {
                                    voiceAsssit.setTts(s);
                                    voiceAsssit.setCondition(TtsConstants.CONDITION_ONE_SEARCH_RESULTS);
                                    voiceAsssit.setConditionId(TtsConstants.IQIYI_C3_1);
                                }
                                DataStatistics.recordVoiceAsssit(voiceAsssit); //语音埋点
                            }
                        });
                    }
                } else if (count > 1) { //二次交互-有多个搜索结果
                    if (!isHide()) {
                        noticeVoiceSearch(); //不退出小欧形象，进行二次语音识别
                        speak(true, true, TtsConstants.IQIYI_C17, null, new Consumer<String>(){
                            @Override
                            public void accept(String s) throws Exception {
                                if (voiceAsssit != null) {
                                    voiceAsssit.setTts(s);
                                    voiceAsssit.setObject(TtsConstants.INTENT_SELECT_GUIDE);
                                    voiceAsssit.setCondition(TtsConstants.CONDITION_DEFAULT);
                                    voiceAsssit.setConditionId(TtsConstants.IQIYI_C17);
                                }
                                DataStatistics.recordVoiceAsssit(voiceAsssit);

                            }
                        }, null);
                    }
                } else { //无搜索结果
                    if (!isHide()) {
                        speak(true, false, TtsConstants.IQIYI_C4,null, new Consumer<String>(){
                            @Override
                            public void accept(String s) throws Exception {
                                if (voiceAsssit != null) {
                                    voiceAsssit.setTts(s);
                                    voiceAsssit.setCondition(TtsConstants.CONDITION_NO_SEARCH_RESULTS);
                                    voiceAsssit.setConditionId(TtsConstants.IQIYI_C4);
                                }
                                DataStatistics.recordVoiceAsssit(voiceAsssit); //语音埋点
                            }
                        });
                    }
                }
                break;
        }

    }

    /**
     * 播放控制
     * @param nlpVoiceModel 语义
     * @param insType 用户指令
     */
    private void playControl(NlpVoiceModel nlpVoiceModel, String insType) {
        switch (insType) {
            case NlpVoiceConstants.CMD_PRE: //上一集
                if (!mPlayListManager.isFirstPos()) { //当前非第一集
                    mPlayManager.playPrev();
                    speak(true, false, TtsConstants.IQIYI_C7, null, new Consumer<String>(){

                        @Override
                        public void accept(String s) throws Exception {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_EPISODES_CONTROL)
                                    .setResponse(nlpVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts(s)
                                    .setCondition(TtsConstants.CONDITION_NOT_FIRST)
                                    .setConditionId(TtsConstants.IQIYI_C7)
                                    .setPrimitive(nlpVoiceModel.text)
                                    .build());

                        }
                    }, null);
                } else { //当前为第一集或无上一集
                    speak(true, false, TtsConstants.IQIYI_C8, null, new Consumer<String>(){

                        @Override
                        public void accept(String s) throws Exception {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_EPISODES_CONTROL)
                                    .setResponse(nlpVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts(s)
                                    .setCondition(TtsConstants.CONDITION_IS_FIRST)
                                    .setConditionId(TtsConstants.IQIYI_C8)
                                    .setPrimitive(nlpVoiceModel.text)
                                    .build());

                        }
                    }, null);
                }
                break;
            case NlpVoiceConstants.CMD_NEXT: //下一集
                if (!mPlayListManager.isLastPos()) { //当前非最后一集
                    mPlayManager.playNext();
                    speak(true, false, TtsConstants.IQIYI_C9, null, new Consumer<String>(){
                        @Override
                        public void accept(String s) throws Exception {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_EPISODES_CONTROL)
                                    .setResponse(nlpVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts(s)
                                    .setCondition(TtsConstants.CONDITION_NOT_LAST)
                                    .setConditionId(TtsConstants.IQIYI_C9)
                                    .setPrimitive(nlpVoiceModel.text)
                                    .build());
                        }
                    }, null);
                } else { //当前为最后一集或下一集
                    speak(true, false, TtsConstants.IQIYI_C10, null, new Consumer<String>(){
                        @Override
                        public void accept(String s) throws Exception {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_EPISODES_CONTROL)
                                    .setResponse(nlpVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts(s)
                                    .setCondition(TtsConstants.CONDITION_IS_LAST)
                                    .setConditionId(TtsConstants.IQIYI_C10)
                                    .setPrimitive(nlpVoiceModel.text)
                                    .build());

                        }
                    }, null);
                }
                break;
            case NlpVoiceConstants.CMD_PAUSE: //暂停播放
                if (mPlayManager.isPlaying()) { //当前未暂停
                    speak(true, false, TtsConstants.IQIYI_C11, null, new Consumer<String>(){
                        @Override
                        public void accept(String s) throws Exception {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_STATE_CONTROL)
                                    .setResponse(nlpVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts(s)
                                    .setCondition(TtsConstants.CONDITION_NO_PAUSE)
                                    .setConditionId(TtsConstants.IQIYI_C11)
                                    .setPrimitive(nlpVoiceModel.text)
                                    .build());
                        }
                    }, null);
                    mPlayManager.pause();
                } else { //当前已暂停
                    speak(true, false, TtsConstants.IQIYI_C12, null, new Consumer<String>(){
                        @Override
                        public void accept(String s) throws Exception {
                            DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                    .setAppName(TtsConstants.APP_NAME)
                                    .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                    .setObject(TtsConstants.INTENT_STATE_CONTROL)
                                    .setResponse(nlpVoiceModel.response)
                                    .setProvider(TtsConstants.PROVIDER)
                                    .setTts(s)
                                    .setCondition(TtsConstants.CONDITION_IS_PAUSE)
                                    .setConditionId(TtsConstants.IQIYI_C12)
                                    .setPrimitive(nlpVoiceModel.text)
                                    .build());

                        }
                    }, null);
                }
                break;
            case NlpVoiceConstants.CMD_PLAY: //继续播放
                speak(true, false, TtsConstants.IQIYI_C13, null, new Consumer<String>(){
                    @Override
                    public void accept(String s) throws Exception {
                        DataStatistics.recordVoiceAsssit(new VoiceAsssit.Builder() //语音埋点
                                .setAppName(TtsConstants.APP_NAME)
                                .setScene(TtsConstants.SCENE_PLAY_CONTROL)
                                .setObject(TtsConstants.INTENT_STATE_CONTROL)
                                .setResponse(nlpVoiceModel.response)
                                .setProvider(TtsConstants.PROVIDER)
                                .setTts(s)
                                .setCondition(TtsConstants.CONDITION_DEFAULT)
                                .setConditionId(TtsConstants.IQIYI_C13)
                                .setPrimitive(nlpVoiceModel.text)
                                .build());
                    }
                }, null);
                break;
            default:
                String aware =  Settings.System.getString(MainApplication.getContext().getContentResolver(),"aware");
                Map<String, String> map = new HashMap<>();
                map.put("#VOICENAME#",aware);
                speak(false,false, TtsConstants.MAIN_C14, map);
                break;
        }

    }

    /**
     * 语音搜索跳转到搜索页面
     * @param nlpVoiceModel 语义模型数据
     * @param keyWord 搜索关键词
     * @param context context
     */
    private void gotoSearch(NlpVoiceModel nlpVoiceModel, String keyWord, Context context) {
        Log.d(TAG, "search keyword:" + keyWord);
        if(keyWord == null || keyWord.isEmpty()) {
            Log.e(TAG, "keyWord is null or empty!");
            return;
        }
        VoiceAsssit voiceAsssit = new VoiceAsssit.Builder()
                .setAppName(TtsConstants.APP_NAME)
                .setScene(TtsConstants.SCENE_SEARCH_VIDEO)
                .setProvider(TtsConstants.PROVIDER)
                .build();
        if (nlpVoiceModel != null) {
            voiceAsssit.setPrimitive(nlpVoiceModel.text);
            voiceAsssit.setResponse(nlpVoiceModel.response);
            String operation = nlpVoiceModel.operation;
            if (operation.equals(NlpVoiceConstants.OP_SEARCH_VIDEO_NAME)) {
                voiceAsssit.setObject(TtsConstants.INTENT_SEARCH_VIDEO_NAME);
            } else if (operation.equals(NlpVoiceConstants.OP_SEARCH_CHANNEL)) {
                voiceAsssit.setObject(TtsConstants.INTENT_SEARCH_CHANNEL_NAME);
            }
        }

        boolean isAgree = SPUtils.getShareBoolean(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
        if (isAgree) {
            Intent intent = new Intent(context, SearchActivity.class);
            intent.putExtra(Constant.SEARCH_KEYWORD, keyWord);
            intent.putExtra(Constant.SEARCH_TYPE, Constant.SEARCH_TYPE_VOICE);
            intent.putExtra(Constant.VIDEO_SEARCH_VOICE_ASSSIT, voiceAsssit);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent); //跳转到搜索界面
        } else {
            ARouter.getInstance().build(Constant.PATH_ACTIVITY_DISCLAIMERS).navigation();//未同意，转到免责声明界面Activity
            speak(true, false, TtsConstants.IQIYI_C21, null, new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    voiceAsssit.setTts(s);
                    voiceAsssit.setCondition(TtsConstants.CONDITION_DEFAULT);
                    voiceAsssit.setConditionId(TtsConstants.IQIYI_C21);
                    DataStatistics.recordVoiceAsssit(voiceAsssit);
                }
            });
        }
    }

    /**
     * 打开服务商城
     * @param context context
     * @param goodsId 商品id
     */
    private void openServiceMall(Context context, String goodsId) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        String packageName = "com.chinatsp.servicemall";
        String activityName = packageName + ".MainActivity";
        if (!TextUtils.isEmpty(goodsId)) {
            activityName = packageName + ".activity.GoodsDetailActivity";
            bundle.putString("goods_id", goodsId);
        } else {
            bundle.putInt("type", 1);
        }
        bundle.putString("package", context.getPackageName());
        intent.putExtras(bundle);
        ComponentName componentName = new ComponentName(packageName, activityName);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (AppUtils.isExistActivity(context, packageName, activityName)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "not found activity!");
        }
    }

}
