package com.oushang.iqiyi.voice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chinatsp.ifly.aidlbean.CmdVoiceModel;
import com.chinatsp.ifly.aidlbean.NlpVoiceModel;
import com.chinatsp.ifly.voiceadapter.Business;
import com.oushang.iqiyi.voice.handler.NlpVoiceHandler;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/11/15 0015  11:44
 * @Since: 1.0
 */
public class VoiceReceiver extends BroadcastReceiver {
    private static final String TAG = "xyj_iqiyi";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "receiver action:" + action);
        if (Business.ACTION_IFLY_SR_TO_APP.equals(action)) {
            Log.d(TAG, "VoiceReceiver");
            int type = intent.getIntExtra(Business.EXTRA_TYPE, -1);
            Log.d(TAG, "type:" + type);
            if (type == Business.EXTRA.SR) {
                NlpVoiceModel model = intent.getParcelableExtra(Business.EXTRA_IFLY);
                Log.d(TAG, "NlpVoiceModel:" + model);
                NlpVoiceHandler.getInstance().handler(model,context);
            }

            if(type == Business.EXTRA.MVW) {
                CmdVoiceModel model = intent.getParcelableExtra(Business.EXTRA_IFLY);
                Log.d(TAG, "CmdVoiceModel：" + model);
            }
        }
    }
}
