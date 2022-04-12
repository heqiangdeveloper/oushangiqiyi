package com.oushang.lib_base.event;

public class RefreshStksEvent {
    public static final int EVENT_TYPE_REFRESH_STKS_WORDS = 1000; //通知刷新可见即可说关键字
    public static final int EVENT_TYPE_SCOPE_MY_SETTING = 1001; //清除可见即可说缓存
    public static final int EVENT_TYPE_SCOPE_CLEAR_CACHES = 1002; //清除可见即可说缓存
    public static final int EVENT_TYPE_SCOPE_MY_SETTING_ITEM = 1003; //清除可见即可说缓存
    public static final int EVENT_TYPE_SCOPE_SETTING_CHILD = 1004; //清除可见即可说缓存
    private int message;

    public RefreshStksEvent(int message){
        this.message = message;
    }

    public int getMessage(){
        return message;
    }
}
