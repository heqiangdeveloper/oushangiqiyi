package com.oushang.iqiyi.voice;

import android.app.Application;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chinatsp.ifly.aidlbean.CmdVoiceModel;
import com.chinatsp.ifly.aidlbean.MutualVoiceModel;
import com.chinatsp.ifly.aidlbean.NlpVoiceModel;
import com.chinatsp.ifly.voiceadapter.Business;
import com.chinatsp.ifly.voiceadapter.ISpeechClientListener;
import com.chinatsp.ifly.voiceadapter.SpeechServiceAgent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.oushang.iqiyi.voice.handler.CmdVoiceHandler;
import com.oushang.iqiyi.voice.handler.NlpVoiceHandler;
import com.oushang.iqiyi.voice.handler.StksVoiceHandler;
import com.oushang.lib_service.callback.IPlayerWindowListener;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.interfaces.PlayManager;
import com.oushang.voicebridge.AppStatus;
import com.oushang.voicebridge.VisibleDataProcessor;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @Author: zeelang
 * @Description: 语音交互管理
 * @Time: 2021/11/2 0002  17:28
 * @Since: 1.0
 */
public class VoiceManager {
    private static final String TAG = "xyj_iqiyi";
    private Application mApplication;
    private volatile boolean isBind = false;

    @Autowired(name = Constant.PATH_SERVICE_PLAY_MANAGER)
    PlayManager mPlayManager;

    private final VisibleDataProcessor visibleDataProcessor = new VisibleDataProcessor();
    private boolean enableStks = true;//可见即可说是否启用 true启用
    public static final String STATUS_FOREGROUND = "fg";
    public static final String STATUS_BACKGROUND = "bg";
    public static final int STATUS_NOT_PLAYING = 0;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_PAUSE = 2;

    private VoiceManager() {
        ARouter.getInstance().inject(this);
        init();
    }

    private static class VoiceManagerHolder {
        static VoiceManager VOICE_MANAGER_HOLDER = new VoiceManager();
    }

    public static VoiceManager getInstance() {
        return VoiceManagerHolder.VOICE_MANAGER_HOLDER;
    }

    public void init(Application application) {
        this.mApplication = application;
        initService();
    }

    public boolean isBindService() {
        return isBind;
    }

    private void initService() {
        Log.d(TAG, "initService");
        if (mApplication != null) {
            SpeechServiceAgent.getInstance().setServiceConnect(new SpeechServiceAgent.ServiceConnect() {
                @Override
                public void success() {
                    isBind = true;
                    Log.d(TAG, "已连接上SpeechServiceAgent");
                }

                @Override
                public void fail() {
                    isBind = false;
                    Log.e(TAG, "未连接SpeechServiceAgent");

                }
            });

            SpeechServiceAgent.getInstance().initService(mApplication, Business.IQIYI, speechClientListener);
        }
    }

    private void init() {
        Log.d(TAG, "init");
        mPlayManager.addPlayerWindowListener(new IPlayerWindowListener() {
            @Override
            public void onResume() {
                Log.d(TAG, "onResume");
                try {
                    SpeechServiceAgent.getInstance().noticeShownWithTts(Business.IQIYI,Business.MODEL.ONLINE_VIDEO,false,false,null,null,null,null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStop() {
                Log.d(TAG, "onStop");
                try {
                    SpeechServiceAgent.getInstance().noticeHide(Business.IQIYI,Business.MODEL.ONLINE_VIDEO,false,false,null,null,null,null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    final ISpeechClientListener speechClientListener = new ISpeechClientListener() {
        @Override
        public void onSrAction(NlpVoiceModel nlpVoiceModel) {
            Log.d(TAG, "onSrAction:" + nlpVoiceModel);
            NlpVoiceHandler.getInstance().handler(nlpVoiceModel,mApplication.getApplicationContext());
        }

        @Override
        public void onMvwAction(CmdVoiceModel cmdVoiceModel) {
            Log.d(TAG, "onMvwAction：" + cmdVoiceModel);
            CmdVoiceHandler.getInstance().handler(cmdVoiceModel,mApplication.getApplicationContext());
        }

        @Override
        public void onStksAction(CmdVoiceModel cmdVoiceModel) {
            Log.d(TAG, "onStksAction:" + cmdVoiceModel);
            StksVoiceHandler.getInstance().handler(cmdVoiceModel,mApplication.getApplicationContext());
        }

        @Override
        public void onSearchWeChatContact(String s) {
            Log.d(TAG, "onSearchWeChatContact:" + s);
        }

        @Override
        public void onMutualAction(MutualVoiceModel mutualVoiceModel) {
            Log.d(TAG, "onMutualAction:" + mutualVoiceModel);
        }

        @Override
        public void onStksBySrAction(NlpVoiceModel nlpVoiceModel) {
            Log.d(TAG, "onStksBySrAction:" + nlpVoiceModel);
            if (nlpVoiceModel == null) return;
            CmdVoiceModel cmdVoiceModel = parseSrStksData(nlpVoiceModel);
            if (cmdVoiceModel == null) return;
            StksVoiceHandler.getInstance().handler(cmdVoiceModel,mApplication.getApplicationContext());
        }

        @Override
        public void onStksByMvwAction(CmdVoiceModel cmdVoiceModel) {
            Log.d(TAG, "onStksByMvwAction:" + cmdVoiceModel);
            StksVoiceHandler.getInstance().handler(cmdVoiceModel,mApplication.getApplicationContext());
        }
    };

    private CmdVoiceModel parseSrStksData(NlpVoiceModel nlpVoiceModel) {
        if (nlpVoiceModel == null || !TextUtils.equals("VIEWCMD", nlpVoiceModel.operation))
            return null;
        try {
            String keySlots = "slots";
            String keyViewCmd = "viewCmd";
            JSONObject jobj = new JSONObject(nlpVoiceModel.semantic);
            if (jobj.has(keySlots)) {
                JSONObject jChild = jobj.getJSONObject(keySlots);
                if (jChild.has(keyViewCmd)) {
                    CmdVoiceModel model = new CmdVoiceModel();
                    model.text = jChild.getString(keyViewCmd);
                    return model;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 上传应用状态到讯飞 数据内容为：  {"changBa::default":{"activeStatus":"fg","data":{},"sceneStatus":"notPlaying"}}
     * 此方法会根据传入的 secondSr,isFg, playState和extra 自动生成对应的json数据
     * 然后调用 {@link #updateStatus(boolean, boolean, boolean, String)} 方法
     */
    public void updateStatus(boolean secondSr, boolean isFg, int playState, Object extra) {
        AppStatus statusData = new AppStatus();
        statusData.setActiveStatus(isFg ? STATUS_FOREGROUND : STATUS_BACKGROUND);
        String playStatus = playState == STATUS_PLAYING ? "playing" : playState == STATUS_PAUSE ? "paused" : "notPlaying";
        statusData.setSceneStatus(playStatus);
        statusData.setData(extra == null ? new Object() : extra);
        Gson gson = new Gson();
        JsonObject jobj = new JsonObject();
        jobj.add("iqiyi", gson.toJsonTree(statusData));
        String json = gson.toJson(jobj);
        Log.w(TAG, "new app status : " + json);
        updateStatus(secondSr, isFg, playState == STATUS_PLAYING, json);
    }

    /**
     * 上传应用状态给讯飞语音后台，
     *
     * @param secondSr   是否二次交互
     * @param fg         应用是否在前台
     * @param play       当前是否处于播放状态
     * @param statusJson 所有参数json格式化后的数据 各应用需要对对应的页面查找自己的数据格式 http://aquadev.iflytekauto.cn/vsp/#/
     */
    public void updateStatus(boolean secondSr, boolean fg, boolean play, String statusJson) {
        try {
            SpeechServiceAgent.getInstance().uploadAppStatusToIfly(Business.IQIYI, secondSr, fg, play, statusJson);
            if (secondSr)
                SpeechServiceAgent.getInstance().waitMultiInterface("stateFlag", "SING");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
