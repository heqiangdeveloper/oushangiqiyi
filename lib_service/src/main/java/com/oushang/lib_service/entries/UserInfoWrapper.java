package com.oushang.lib_service.entries;


/**
 * @Author: zeelang
 * @Description: 用户信息包装类
 * @Date: 2021/6/24
 */
@Deprecated
public class UserInfoWrapper {

    private UserInfo userInfo;

    public UserInfoWrapper(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * 获取用户昵称
     * @return 昵称
     */
    public String getNickName() {
        return userInfo.getNickName();
    }

    public void setNickName(String nickName) {
        userInfo.setNickName(nickName);
    }

    /**
     * 获取vip用户到期时间
     * @return 到期时间
     */
    public String getVipExpireTime() {
        return userInfo.getVipExpireTime();
    }

    public void setVipExpireTime(String vipExpireTime) {
        userInfo.setVipExpireTime(vipExpireTime);
    }

    /**
     * 获取用户uid
     * @return uid
     */
    public String getUid() {
        return userInfo.getUid();
    }

    public void setUid(String uid) {
        userInfo.setUid(uid);
    }

    /**
     * 获取用户vip等级
     * @return vip等级
     */
    public String getVipLevel() {
        return userInfo.getVipLevel();
    }

    public void setVipLevel(String vipLevel) {
        userInfo.setVipLevel(vipLevel);
    }

    /**
     * 获取用户头像
     * @return 用户头像链接
     */
    public String getIconUrl() {
        return userInfo.getIconUrl();
    }

    public void setIconUrl(String iconUrl) {
        userInfo.setIconUrl(iconUrl);
    }

    /**
     * 距离会员到期天数
     * xxxx 年 xx 月 xx 日
     * @return 天数
     */
    public String getVipSurplus() {
        return userInfo.getVipSurplus();
    }

    public void setVipSurplus(String vipSurplus) {
        userInfo.setVipSurplus(vipSurplus);
    }

    /**
     * 是否是vip用户
     * @return true 是，false 否
     */
    public boolean isVip() {
        return userInfo.isVip();
    }

    public void setVip(boolean vip) {
        userInfo.setVip(vip);
    }
}
