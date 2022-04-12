package com.oushang.iqiyi.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author: zeelang
 * @Description:
 * @Time: 2022/2/22 16:58
 * @Since: 1.0
 */
public class BalanceInfo {
    public static final String IQIYI_APP_CODE = "AIQIYIFLOW";

    @SerializedName("code")
    int code; //状态码

    @SerializedName("msg")
    String msg; //状态码描述

    @SerializedName("data")
    List<Data> data;

    @SerializedName("success")
    boolean success;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "BalanceInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", success=" + success +
                '}';
    }

    public static class Data {

        @SerializedName("total")
        double total; //该服务总量

        @SerializedName("left")
        double left; //该服务总剩余量

        @SerializedName("totalUnit")
        String totalUnit; //该服务下产品单位

        @SerializedName("balances")
        List<Balance> balances;

        @SerializedName("appInfo")
        AppInfo appInfo;

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getLeft() {
            return left;
        }

        public void setLeft(double left) {
            this.left = left;
        }

        public String getTotalUnit() {
            return totalUnit;
        }

        public void setTotalUnit(String totalUnit) {
            this.totalUnit = totalUnit;
        }

        public List<Balance> getBalances() {
            return balances;
        }

        public void setBalances(List<Balance> balances) {
            this.balances = balances;
        }

        public AppInfo getAppInfo() {
            return appInfo;
        }

        public void setAppInfo(AppInfo appInfo) {
            this.appInfo = appInfo;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "total=" + total +
                    ", left=" + left +
                    ", totalUnit='" + totalUnit + '\'' +
                    ", balances=" + balances +
                    ", appInfo=" + appInfo +
                    '}';
        }
    }

    public static class Balance {
        @SerializedName("total")
        double total; //每个balance总量

        @SerializedName("unit")
        String unit; //余量单位

        @SerializedName("left")
        double left; //每个balance剩余量

        @SerializedName("expirationTime")
        String expirationTime; //失效时间

        @SerializedName("appid")
        String appId; //服务id

        @SerializedName("percentage")
        double percentage; //剩余百分比

        @SerializedName("status")
        String status; //状态

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public double getLeft() {
            return left;
        }

        public void setLeft(double left) {
            this.left = left;
        }

        public String getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(String expirationTime) {
            this.expirationTime = expirationTime;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Balance{" +
                    "total=" + total +
                    ", unit='" + unit + '\'' +
                    ", left=" + left +
                    ", expirationTime='" + expirationTime + '\'' +
                    ", appId='" + appId + '\'' +
                    ", percentage=" + percentage +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    public static class AppInfo {
        @SerializedName("code")
        String code; //服务编码

        @SerializedName("appImgHandler")
        String appImgHandler; //服务地址

        @SerializedName("appName")
        String appName; //服务类型名称

        @SerializedName("appid")
        String appId; //服务类型id

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAppImgHandler() {
            return appImgHandler;
        }

        public void setAppImgHandler(String appImgHandler) {
            this.appImgHandler = appImgHandler;
        }

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        @Override
        public String toString() {
            return "AppInfo{" +
                    "code='" + code + '\'' +
                    ", appImgHandler='" + appImgHandler + '\'' +
                    ", appName='" + appName + '\'' +
                    ", appId='" + appId + '\'' +
                    '}';
        }
    }
}
