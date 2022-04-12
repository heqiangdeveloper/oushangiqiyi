package com.oushang.iqiyi.statistics;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Author: zeelang
 * @Description: 语音埋点模型
 * @Time: 2022/4/7 0007  15:45
 * @Since: 1.0
 */
public class VoiceAsssit {
    private String appName; //技能
    private String scene; //场景
    private String primitive; //原语
    private String object; //意图
    private String response; //语音助手回报
    private String condition; //条件名
    private String conditionId; //条件id
    private String tts; //文案
    private String provider; //语音服务提供商
    private Map<String,String> extend; //扩展数据

    public VoiceAsssit(Builder builder) {
        this.appName = builder.appName;
        this.scene = builder.scene;
        this.primitive = builder.primitive;
        this.object = builder.object;
        this.response = builder.response;
        this.condition = builder.condition;
        this.conditionId = builder.conditionId;
        this.tts = builder.tts;
        this.provider = builder.provider;
        this.extend = builder.extend;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appName", appName);
        jsonObject.put("scene", scene);
        jsonObject.put("object", object);
        jsonObject.put("response", response);
        jsonObject.put("provider", provider);
        jsonObject.put("tts", tts);
        jsonObject.put("condition", condition);
        jsonObject.put("conditionId", conditionId);
        jsonObject.put("primitive", primitive);
        if (extend != null && !extend.isEmpty()) {
            extend.forEach(jsonObject::put);
        }
        return jsonObject.toJSONString();
    }

    public static class Builder {
        private String appName; //技能
        private String scene; //场景
        private String primitive; //原语
        private String object; //意图
        private String response; //语音助手回报
        private String condition; //条件名
        private String conditionId; //条件id
        private String tts; //文案
        private String provider; //语音服务提供商
        private Map<String,String> extend; //扩展数据

        public Builder setAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder setScene(String scene) {
            this.scene = scene;
            return this;
        }

        public Builder setPrimitive(String primitive) {
            this.primitive = primitive;
            return this;
        }

        public Builder setObject(String object) {
            this.object = object;
            return this;
        }

        public Builder setResponse(String response) {
            this.response = response;
            return this;
        }

        public Builder setCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder setConditionId(String conditionId) {
            this.conditionId = conditionId;
            return this;
        }

        public Builder setTts(String tts) {
            this.tts = tts;
            return this;
        }

        public Builder setProvider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder setExtend(Map<String,String> extend) {
            this.extend = extend;
            return this;
        }

        public VoiceAsssit build() {
            return new VoiceAsssit(this);
        }
    }

}
