package com.oushang.lib_service.entries;

/**
 * @Author: zeelang
 * @Description: 账号信息
 * @Time: 2021/9/1 16:17
 * @Since: 1.0
 */
public class Account {
    private String mOpenId;
    private String mToken;
    private String mCookie;
    private String mUserId;
    private String mUserType;
    private int mLoginType;
    private boolean mExistValidMember;

    public Account() {
    }

    public Account(String openId, String token, String cookie, String userId, String userType, int loginType, boolean existValidMember) {
        this.mOpenId = openId;
        this.mToken = token;
        this.mCookie = cookie;
        this.mUserId = userId;
        this.mUserType = userType;
        this.mLoginType = loginType;
        this.mExistValidMember = existValidMember;
    }

    public String getOpenId() {
        return mOpenId;
    }

    public void setOpenId(String openId) {
        this.mOpenId = openId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getCookie() {
        return mCookie;
    }

    public void setCookie(String cookie) {
        this.mCookie = cookie;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getUserType() {
        return mUserType;
    }

    public void setUserType(String userType) {
        this.mUserType = userType;
    }

    public int getLoginType() {
        return mLoginType;
    }

    public void setLoginType(int loginType) {
        this.mLoginType = loginType;
    }

    public boolean isExistValidMember() {
        return mExistValidMember;
    }

    public void setExistValidMember(boolean existValidMember) {
        this.mExistValidMember = existValidMember;
    }

    @Override
    public String toString() {
        return "Account{" +
                "OpenId='" + mOpenId + '\'' +
                ", Token='" + mToken + '\'' +
                ", Cookie='" + mCookie + '\'' +
                ", UserId='" + mUserId + '\'' +
                ", UserType='" + mUserType + '\'' +
                ", LoginType=" + mLoginType +
                ", ExistValidMember=" + mExistValidMember +
                '}';
    }
}
