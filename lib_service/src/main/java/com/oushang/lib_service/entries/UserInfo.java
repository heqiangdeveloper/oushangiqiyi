package com.oushang.lib_service.entries;

/**
 * @Author: zeelang
 * @Description: 用户信息
 * @Time: 2021/8/11 14:10
 * @Since: 1.0
 */
public class UserInfo {
    private String nickName;
    private String vipExpireTime;
    private String uid;
    private String vipLevel;
    private String iconUrl;
    private String vipSurplus;
    private boolean isVip;

    public UserInfo() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getVipExpireTime() {
        return vipExpireTime;
    }

    public void setVipExpireTime(String vipExpireTime) {
        this.vipExpireTime = vipExpireTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getVipSurplus() {
        return vipSurplus;
    }

    public void setVipSurplus(String vipSurplus) {
        this.vipSurplus = vipSurplus;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "nickName='" + nickName + '\'' +
                ", vipExpireTime='" + vipExpireTime + '\'' +
                ", uid='" + uid + '\'' +
                ", vipLevel='" + vipLevel + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", vipSurplus='" + vipSurplus + '\'' +
                ", isVip=" + isVip +
                '}';
    }
}
