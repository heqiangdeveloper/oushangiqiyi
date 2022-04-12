package com.oushang.lib_service.entries;

/**
 * @Author: Administrator
 * @Description: 视频资源
 * @Time: 2021/10/9 0009  16:33
 * @Since: 1.0
 */
public class DataSource {

    //播放记录类型
    public static final int TV_CHECK_RC = 0;// Tv_id 为 Key 查询播放记录
    public static final int ALBUM_CHECK_RC = 1;// Album_id 为 Key 查询播放记录
    public static final int IGNORE_ALL_RC = 2; //忽略所有播放记录

    public static final int NORMAL_VT = 0; //普通播放
    public static final int LIVE_VT = 3; //直播

    private String aid; //剧集 id
    private String tvid; //视频 id
    private int rcCheckPolicy; //播放记录查询逻辑
    private int cType; // 视频类型 0 是普通播放，3 是直播

    public DataSource(String aid, String tvid, int rcCheckPolicy, int cType) {
        this.aid = aid;
        this.tvid = tvid;
        this.rcCheckPolicy = rcCheckPolicy;
        this.cType = cType;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getTvid() {
        return tvid;
    }

    public void setTvid(String tvid) {
        this.tvid = tvid;
    }

    public int getRcCheckPolicy() {
        return rcCheckPolicy;
    }

    public void setRcCheckPolicy(int rcCheckPolicy) {
        this.rcCheckPolicy = rcCheckPolicy;
    }

    public int getcType() {
        return cType;
    }

    public void setcType(int cType) {
        this.cType = cType;
    }
}
