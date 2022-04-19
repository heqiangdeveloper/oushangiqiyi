package com.oushang.iqiyi.statistics;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Author: zeelang
 * @Description: 语音埋点模型
 * @Time: 2022/4/7 0007  15:45
 * @Since: 1.0
 */
public class VoiceAsssit implements Parcelable {
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

    protected VoiceAsssit(Parcel in) {
        appName = in.readString();
        scene = in.readString();
        primitive = in.readString();
        object = in.readString();
        response = in.readString();
        condition = in.readString();
        conditionId = in.readString();
        tts = in.readString();
        provider = in.readString();
        in.readMap(extend, Map.class.getClassLoader());
    }

    public static final Creator<VoiceAsssit> CREATOR = new Creator<VoiceAsssit>() {
        @Override
        public VoiceAsssit createFromParcel(Parcel in) {
            return new VoiceAsssit(in);
        }

        @Override
        public VoiceAsssit[] newArray(int size) {
            return new VoiceAsssit[size];
        }
    };

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public void setPrimitive(String primitive) {
        this.primitive = primitive;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setConditionId(String conditionId) {
        this.conditionId = conditionId;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setExtend(Map<String, String> extend) {
        this.extend = extend;
    }

    public String getAppName() {
        return appName;
    }

    public String getScene() {
        return scene;
    }

    public String getPrimitive() {
        return primitive;
    }

    public String getObject() {
        return object;
    }

    public String getResponse() {
        return response;
    }

    public String getCondition() {
        return condition;
    }

    public String getConditionId() {
        return conditionId;
    }

    public String getTts() {
        return tts;
    }

    public String getProvider() {
        return provider;
    }

    public Map<String, String> getExtend() {
        return extend;
    }

    @Override
    public String toString() {
        return "VoiceAsssit{" +
                "appName='" + appName + '\'' +
                ", scene='" + scene + '\'' +
                ", primitive='" + primitive + '\'' +
                ", object='" + object + '\'' +
                ", response='" + response + '\'' +
                ", condition='" + condition + '\'' +
                ", conditionId='" + conditionId + '\'' +
                ", tts='" + tts + '\'' +
                ", provider='" + provider + '\'' +
                ", extend=" + extend +
                '}';
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(scene);
        dest.writeString(primitive);
        dest.writeString(object);
        dest.writeString(response);
        dest.writeString(condition);
        dest.writeString(conditionId);
        dest.writeString(tts);
        dest.writeString(provider);
        dest.writeMap(extend);
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
