package com.oushang.iqiyi.events;

/**
 * @Author: zeelang
 * @Description: 事件常量
 * @Time: 2021/9/2 16:34
 * @Since: 1.0
 */
public class EventConstant {

    /**
     * EventType
     */
    public static final int EVENT_TYPE_LOAD_CHILD_FRAGMENT = 1; //加载嵌套子Fragment事件

    public static final int EVENT_TYPE_LOAD_FRAGMENT = 2;//加载fragment

    public static final int EVENT_TYPE_EDIT_ENABLE = 3;//启用编辑事件

    public static final int EVENT_TYPE_EDIT = 4;//编辑事件

    public static final int EVENT_TYPE_POP_BACK_FRAGMENT = 5; //fragment回退事件

    public static final int EVENT_TYPE_UPDATE_TITLE_LAYOUT = 6; //更新标题区事件

    public static final int EVENT_TYPE_PLAY_SELECT_DEFINITION = 7; //播放选择清晰度事件

    public static final int EVENT_TYPE_PLAY_SELECT_SELECTION = 8; //选择选集事件

    public static final int EVENT_TYPE_PLAY_SELECT_RATIO = 9; //选择画面比例事件

    public static final int EVENT_TYPE_PLAY_SELECT_RELATED = 10; //相关推荐

    public static final int EVENT_TYPE_PLAY_UPDATE_DEFINITION = 11; //更新清晰度事件

    public static final int EVENT_TYPE_UPDATE_PLAY_VIDEO_INFO = 12; //更新播放视频信息事件

    public static final int EVENT_TYPE_DELETE_FAVORITE_RECORD = 13; //删除收藏记录事件

    public static final int EVENT_TYPE_EPISODE_DETAIL = 14; // 剧集详情（时间型）

    public static final int EVENT_TYPE_UPDATE_PLAY_RECORD = 15; //更新播放记录

    public static final int EVENT_TYPE_EPISODE_LOAD_COMPLETE = 16; //剧集更新完事件

    public static final int EVENT_TYPE_UPDATE_SEARCH_RESULT = 17; //更新搜索结果

    public static final int EVENT_TYPE_UPDATE_IS_REVERSE = 18; //是否倒车事件

    public static final int EVENT_TYPE_UPDATE_CAR_SPEED = 19; //车速上报事件

    public static final int EVENT_TYPE_LOGIN_CHANGE = 20; //登录事件

    public static final int EVENT_TYPE_SELECT_THIRD_CHANNEL_TAG = 21; //频道详情第三级标签选择事件

    public static final int EVENT_TYPE_UPDATE_REMEMBER_PLAY_VIDEO_INFO = 22; //更新忘记播放视频信息事件

    public static final int EVENT_TYPE_UPDATE_FAVORITE_RECORD = 23; //更新收藏记录通知事件

    public static final int EVENT_TYPE_SEARCH_TYPE_CHANGED = 24; //更新搜索类型
	
	public static final int EVENT_TYPE_OPEN_AVM = 25; //打开全景上报事件

    public static final int EVENT_TYPE_FULL_SCREEN_CHANGED = 26; //全屏/非全屏事件

    public static final int EVENT_TYPE_FINISH_ACCOUNTACTIVITY = 27; //通知登录页面关闭

    public static final int EVENT_TYPE_HIDE_BASENAVACTIVITY = 28; //通知主页面隐藏

    public static final int EVENT_TYPE_REVERSE_CHANGE = 29;

    public static final int EVENT_TYPE_VOICE_SEARCH_RESULT = 30; //语音搜索结果事件

    public static final int EVENT_TYPE_VOICE_SEARCH_SELECT = 31; //选择语音搜索结果事件


    /**
     * EventParams key
     */
    public static final String EVENT_PARAMS_SELECT_TAB_ID = "select_tab_id";  //选中菜单的tab id
    public static final String EVENT_PARAMS_FRAGMENT_CLASS = "fragment_class"; //fragment类名
    public static final String EVENT_PARAMS_FRAGMENT_ARGS = "fragment_args"; //fragment参数

    public static final String EVENT_PARAMS_MY_RECORD_EDIT_ENABLE = "my_record_edit_enable"; //我的记录启用编辑事件参数
    public static final String EVENT_PARAMS_MY_RECORD_EDIT = "my_record_edit"; //我的记录编辑事件参数

    public static final String EVENT_PARAMS_POP_BACK_FRAGMENT_HANDLER = "fragment_handler"; //处理回退的fragment

    public static final String EVENT_PARAMS_UPDATE_NESTED_TITLE_LAYOUT_WHO = "update_nested_title_layout_who";//更新嵌套标题区来自谁
    public static final String EVENT_PARAMS_NESTED_TITLE_SHOW_BACK = "nested_title_show_back"; //嵌套标题区参数,显示返回按钮

    public static final String EVENT_PARAMS_PLAY_SELECT_DEFINITION = "play_definition"; //清晰度
    public static final String EVENT_PARAMS_PLAY_SELECT_SELECTION = "play_selection"; //选集
    public static final String EVENT_PARAMS_PLAY_SELECT_RATIO = "play_ratio"; //画面比例
    public static final String EVENT_PARAMS_PLAY_SELECT_RELATE = "play_relate"; //相关推荐

    public static final String EVENT_PARAMS_UPDATE_PLAY_VIDEO_INFO = "play_video_info"; //播放视频
    public static final String EVENT_PARAMS_UPDATE_REMEMBER_PLAY_VIDEO_INFO = "play_remember_video_info"; //播放记忆视频

    public static final String EVENT_PARAMS_FAVORITE_RECORD = "favorite_record"; //收藏记录

    public static final String EVENT_PARAMS_VIDEO_INFO_DESC = "videoInfo_desc";
    public static final String EVENT_PARAMS_VIDEO_INFO_CAST_INFO = "videoInfo_castInfo";

    public static final String EVENT_PARAMS_IS_LOGIN = "is_login";

    public static final String EVENT_PARAMS_CHILD_CHANNEL_TAG = "child_channel_tag"; //频道详情子标签
    public static final String EVENT_PARAMS_THIRD_CHANNEL_TAG = "third_channel_tag"; //频道详情第三级子标签

    public static final String EVENT_PARAMS_IS_ENABLE = "is_enable";
    public static final String EVENT_PARAMS_FULL_SCREEN_CHANGED = "full_screen_changed"; //全屏/非全屏事件参数

    public static final String EVENT_PARAMS_SORT_CHANGED = "sort_changed"; //哪种排序
    public static final String EVENT_PARAMS_NOTIFY_FRAGMENT_ID = "notify_fragment_position";//哪个fragment

    public static final String EVENT_PARAMS_VOICE_SEARCH_RESULT_COUNT = "voice_search_result_count";//语音搜索结果个数
    public static final String EVENT_PARAMS_VOICE_SEARCH_RESULT_SELECT = "voice_search_result_select"; //语音搜索结果选择几个
    public static final String EVENT_PARAMS_VOICE_SEARCH_VOICE_ASSSIT = "voice_search_voice_asssit"; //语音搜索语音埋点

    /**
     * EventParams value
     */
    public static final int MY_RECORD_EDITED = 1; //启用编辑
    public static final int MY_RECORD_UNEDITED = 0; //不启用编辑

    public static final int MY_RECORD_ALL_SELECT = 2; //全选
    public static final int MY_RECORD_DELETE_SELECT = 3; //删除
    public static final int MY_RECORD_CANCEL_SELECT = 4; //取消

    //清晰度
    public static final String DEFINITION_1080P = "1080P";
    public static final String DEFINITION_720P = "720P";
    public static final String DEFINITION_HIGH = "high";
    public static final String DEFINITION_STANDARD = "standard";
    public static final String DEFINITION_FLUENT = "fluent";

    //画面比例
    public static final String RATIO_4_3 = "ratio_4_3";
    public static final String RATIO_16_9 = "ratio_16_9";
    public static final String RATIO_21_9 = "ratio_21_9";












}
