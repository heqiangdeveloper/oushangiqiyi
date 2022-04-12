package com.oushang.iqiyi.statistics;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.oushang.datastat.datastatadapter.DataStatServiceAgent;

import java.util.Map;

/**
 * @Author: zeelang
 * @Description: 埋点数据
 * @Time: 2022/2/12 23:31
 * @Since: 1.0
 */
public class DataStatistics {
    private static final String TAG = DataStatistics.class.getSimpleName();
    private static final String CATAGRAY_UI_EVENT = "ui_event";
    private static final String CATAGRAY_VOICE_ASSSIT = "voice_asssit";
    private static final String EVENT_ID = "event_id";
    private static final String VALUE = "value";

    /**
     * 埋点初始化，连接远程服务
     * @param context context
     */
    public static void init(Context context) {
        DataStatServiceAgent.getInstance().bindService(context);
    }

    /**
     * 记录埋点数据
     * @param catagray 类别
     * @param json     数据字符串
     */
    private static void record(String catagray, String json) {
        Log.d(TAG, "record: catagray:" + catagray + ",json:" + json);
        DataStatServiceAgent.getInstance().record(catagray, json);
    }

    /**
     * 记录UI事件
     * @param id  事件ID
     * @param value 事件取值，键值对
     */
    public static void recordUiEvent(int id, Map<String,String> value) {
        if(value != null) {
            JSONObject jsonObject = new JSONObject();
            value.forEach(jsonObject::put);
            recordUiEvent(id, jsonObject.toJSONString());
        } else {
            recordUiEvent(id);
        }
    }

    /**
     * 记录UI事件
     * @param id    事件ID
     * @param value 事件取值
     */
    public static void recordUiEvent(int id, String value) {
        String json = "";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVENT_ID, id);
        jsonObject.put(VALUE, value);
        json = jsonObject.toJSONString();
        record(CATAGRAY_UI_EVENT, json);
    }

    /**
     * 记录UI埋点事件
     * @param id 事件Id
     */
    public static void recordUiEvent(int id) {
        recordUiEvent(id, "");
    }

    /**
     * 语音埋点
     * @param voiceAsssit 语音埋点模型
     */
    public static void recordVoiceAsssit(VoiceAsssit voiceAsssit) {
        if (voiceAsssit != null) {
            record(CATAGRAY_VOICE_ASSSIT, voiceAsssit.toJson());
        }
    }
}
