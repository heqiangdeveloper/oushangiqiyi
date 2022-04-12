package com.oushang.iqiyi.voice.handler;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.chinatsp.ifly.aidlbean.CmdVoiceModel;
import com.chinatsp.ifly.aidlbean.NlpVoiceModel;
import com.chinatsp.ifly.voiceadapter.SpeechServiceAgent;
import com.google.gson.Gson;
import com.oushang.iqiyi.utils.AppUtils;
import com.oushang.voicebridge.StksCmdVoiceModel;
import com.oushang.voicebridge.VisibleData;

import org.greenrobot.eventbus.EventBus;

public class StksVoiceHandler  extends BaseHandler<CmdVoiceModel>{
    @Override
    public void handler(CmdVoiceModel cmdVoiceModel, Context context) {
        Log.w(TAG, "onStksAction..." + new Gson().toJson(cmdVoiceModel));
        //语音事件未处理完
        if (isForeground(context)) {
            EventBus.getDefault().post(new StksCmdVoiceModel(cmdVoiceModel));
        }
    }

    private static class StksVoiceHandlerHolder {
        static StksVoiceHandler HOLDER = new StksVoiceHandler();
    }

    public static StksVoiceHandler getInstance() {
        return StksVoiceHandler.StksVoiceHandlerHolder.HOLDER;
    }

    private boolean isForeground(Context mContext){
        return "com.oushang.iqiyi".equals(AppUtils.getTopPackage(mContext));
    }
}
