package com.oushang.iqiyi.voice.constants;

/**
 * @Author: 语音播报
 * @Description: ***
 * @Time: 2021/11/3 0003  11:19
 * @Since: 1.0
 */
public class TtsConstants {
    public static final String APP_FLAG = "iqiyi";

    public static final String APP_NAME = "爱奇艺";
    public static final String PROVIDER = "讯飞语音云";

    // tts播报文案id
    public static final String IQIYI_C1 = "iqiyiC1"; //已打开
    public static final String IQIYI_C2 = "iqiyiC2"; //爱奇艺流量已到期，请前往服务商城续费
    public static final String IQIYI_C3 = "iqiyiC3"; //好的，即将播放
    public static final String IQIYI_C3_1 = "iqiyiC3_1"; //二次交互-有搜索结果且只有一个
    public static final String IQIYI_C3_2 = "iqiyiC3_2"; //二次交互-有多个搜索结果
    public static final String IQIYI_C4 = "iqiyiC4"; //小欧没有找到，换个节目吧
    public static final String IQIYI_C5 = "iqiyiC5"; //ok，即将播放
    public static final String IQIYI_C6 = "iqiyiC6"; //没有找到#VIDEONAME#,换个节目吧
    public static final String IQIYI_C7 = "iqiyiC7"; //好的
    public static final String IQIYI_C8 = "iqiyiC8"; //已是第一集
    public static final String IQIYI_C9 = "iqiyiC9"; //好的
    public static final String IQIYI_C10 = "iqiyiC10"; //已是最后一集
    public static final String IQIYI_C11 = "iqiyiC11"; //好的
    public static final String IQIYI_C12 = "iqiyiC12"; //已暂停
    public static final String IQIYI_C13 = "iqiyiC13"; //好的
    public static final String IQIYI_C14 = "iqiyiC14"; //好的/ok/好勒/没问题
    public static final String IQIYI_C15 = "iqiyiC15"; //已全屏
    public static final String IQIYI_C15_1 = "iqiyiC15_1"; //好的/嗯，好了/ok/搞定/没问题/好滴/好嘞
    public static final String IQIYI_C15_2 = "iqiyiC15_2"; //未处于全屏中
    public static final String IQIYI_C16 = "iqiyiC16"; //好的，已关闭
    public static final String IQIYI_C17 = "iqiyiC17"; //想看第几个
    public static final String IQIYI_C18 = "iqiyiC18";//好的/嗯，好了/ok/搞定/没问题/好滴/好勒/
    public static final String IQIYI_C19 = "iqiyiC19";//当前页面仅有#MAXNUM#个结果，选择第几个？
    public static final String IQIYI_C20 = "iqiyiC20"; //行车播放视频功能未打开，请前往设置界面开启行车播放视频
    public static final String IQIYI_C21 = "iqiyiC21"; //请先开启用户授权

    public static final String MAIN_C14 = "mainC14"; //小欧暂不会这个操作

    //场景
    public static final String SCENE_OPEN = "打开爱奇艺";
    public static final String SCENE_SEARCH_VIDEO = "视频搜索";
    public static final String SCENE_PLAY_CONTROL = "播放控制";
    public static final String SCENE_QUIT = "退出爱奇艺";
    public static final String SCENE_SELECT_VIDEO = "选节目";

    //意图
    public static final String INTENT_OPEN = "打开爱奇艺";
    public static final String INTENT_SEARCH_CHANNEL_NAME = "按照频道名称进行搜索";
    public static final String INTENT_SEARCH_VIDEO_NAME = "按照节目名称进行搜索";
    public static final String INTENT_EPISODES_CONTROL = "集数控制";
    public static final String INTENT_STATE_CONTROL = "状态控制";
    public static final String INTENT_WINDOW_SIZE_CONTROL = "界面大小控制";
    public static final String INTENT_QUIT = "退出爱奇艺";
    public static final String INTENT_SELECT_GUIDE = "引导用户在选择界面进行选节目";
    public static final String INTENT_SELECT_VIDEO = "选择节目";

    //条件
    public static final String CONDITION_FLOW_UNEXPIRED = "爱奇艺流量服务未到期";
    public static final String CONDITION_FLOW_EXPIRED = "爱奇艺流量服务到期";
    public static final String CONDITION_HAS_SEARCH_RESULTS = "有搜索结果";
    public static final String CONDITION_ONE_SEARCH_RESULTS = "二次交互-有搜索结果且只有一个";
    public static final String CONDITION_MORE_SEARCH_RESULTS = "二次交互-有多个搜索结果";
    public static final String CONDITION_NO_SEARCH_RESULTS = "无搜索结果";
    public static final String CONDITION_NOT_FIRST = "当前非第一集";
    public static final String CONDITION_IS_FIRST = "当前为第一集或无上一集";
    public static final String CONDITION_NOT_LAST = "当前非最后一集";
    public static final String CONDITION_IS_LAST = "当前为最后一集或下一集";
    public static final String CONDITION_NO_PAUSE = "当前未暂停";
    public static final String CONDITION_IS_PAUSE = "当前已暂停";
    public static final String CONDITION_DEFAULT = "默认";
    public static final String CONDITION_NO_FULL_SCREEN = "当前非全屏";
    public static final String CONDITION_IS_FULL_SCREEN = "当前为全屏";
    public static final String CONDITION_SELECT_IN_RANGE = "选择未超出范围且能播放";
    public static final String CONDITION_SELECT_OUT_RANGE = "选择超出范围";



}
