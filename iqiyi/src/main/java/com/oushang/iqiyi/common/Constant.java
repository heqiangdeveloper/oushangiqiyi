package com.oushang.iqiyi.common;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import retrofit2.http.PUT;

/**
 * @Author: zeelang
 * @Description: 公共常量
 * @Time: 2021/7/26 9:43
 * @Since: 1.0
 */
public class Constant {

    //播放器intent传参
    public static final String PLAY_VIDEO_ID = "qipuId";
    public static final String PLAY_ALBUM_ID = "albumId";
    public static final String PLAY_POSITION = "play_position"; //起播位置（单位:毫秒)
    public static final String PLAY_PUBLISH_YEAR = "year"; //发行年份
    public static final String PLAY_IS_FULL_SCREEN = "full_screen"; //是否全屏
    public static final String PLAY_RECENT_HISTORY_RECORD = "history_record";//播放最近历史记录
    public static final String START_ACTIVITY_BACK = "start_activity_back";//是否在后台启动

    public static final String NAV_MENU_SELECT = "nav_menu_select";


    //搜索界面intent传参
    public static final String SEARCH_KEYWORD = "search_keyword"; //搜索关键词
    public static final String SEARCH_TYPE = "search_type"; //搜索方式
    public static final String SEARCH_RESULT_MODE = "search_result_mode";//搜索结果类型
    public static final String VIDEO_SEARCH_VOICE_ASSSIT = "video_search_voice_asssit"; //视频搜索语音埋点

    public static final int SEARCH_TYPE_INPUT = 0; //输入搜索
    public static final int SEARCH_TYPE_VOICE = 1; //语音搜索


    public static final int SORT_ORDER = 1; //顺序
    public static final int SORT_REVERSE = 2; //倒序


    /**
     * SharedPreferences
     */
    public static final String SP_SPACE_NORMAL_CHANNEL = "normal_channel_space";//常用频道space

    public static final String SP_KEY_NORMAL_CHANNEL = "normal_channel_key";//常用频道key

    public static final String SP_IQIYI_SPACE = "iqiyi";

    public static final String SP_KEY_USER_AGREE = "user_agree";

    public static final String SP_SPACE_SEARCH_HISTORY_KEYWORD_RESULT = "search_history_keyword_result";

    public static final String SP_KEY_SEARCH_HISTORY_KEYWORD = "search_keyword";

    public static final String SP_KEY_ACCESS_TOKEN = "access_token";

    public static final String SP_KEY_LEFT_FLOW = "left_flow";//剩余流量

    public static final String SP_KEY_SKIP_START_END = "skip_start_end";//自动跳过片头与片尾

    public static final String SP_LOGIN_SPACE = "iqiyi_login";

    public static final String SP_SPACE_COMMON_CHANNEL_PARENT_TAG = "common_channel_parent_tag_space"; //常用频道space

    public static final String SP_KEY_COMMON_CHANNEL_PARENT_TAG = "common_channel_parent_tag_key";//常用频道key

    public static final String SP_KEY_LOGIN_USER_INFO = "login_user_info";
    public static final String SP_KEY_BIND_STATUS = "bind_status";
    public static final String SP_KEY_LOGIN_STATUS = "login_status";
    public static final String SP_KEY_LOGIN_NICKNAME = "nick_name";
    public static final String SP_KEY_LOGIN_VIPEXPIRETIME  = "vipExpireTime";
    public static final String SP_KEY_LOGIN_UID = "uid";
    public static final String SP_KEY_LOGIN_VIPLEVEL = "vipLevel";
    public static final String SP_KEY_LOGIN_ICONURL = "iconUrl";
    public static final String SP_KEY_LOGIN_VIPSURPLUS = "vipSurplus";
    public static final String SP_KEY_LOGIN_ISVIP = "isVip";
    public static final String SP_KEY_DRIVERCENTER_ICONURL = "drivercenter_iconUrl";
    public static final String SP_KEY_DRIVERCENTER_NICKNAME = "drivercenter_nickname";
    public static final String SP_KEY_BALANCE_VALUE = "balance_value";
    public static final String SP_KEY_BALANCE_REQUEST_TIME = "balance_request_time";


    /**
     * fragment args
     */
    public static final String BUNDLE_KEY_RECORD_TYPE = "record_type";

    public static final int BUNDLE_VALUE_RECORD_TYPE_HISTORY = 1;

    public static final int BUNDLE_VALUE_RECORD_TYPE_FAVORITE = 2;

    public static final String BUNDLE_KEY_CHANNEL_TAG = "channel_tag";

    public static final String BUNDLE_KEY_CHANNEL_PARENT_TAG = "channel_parent_tag";

    public static final String BUNDLE_KEY_SEARCH_KEY = "search_key";

    public static final String BUNDLE_KEY_SEARCH_Type = "search_type";

    /**
     * web view url
     */
    public static final String WEB_VIEW_URL = "web_url";

    public static final String AGREEMENT_URL = "https://www.iqiyi.com/user/register/protocol.html";

    public static final String PRIVACY_URL = "https://www.iqiyi.com/common/privateh5.html";

    /**
     * 获取爱奇艺流量信息的url
     */
    public static final String BALANCE_INFO_URL = "https://incallapi.changan.com.cn/appserver/api/hu/2.0/getBalanceInfo";

    /**
     * router path
     */
    public static final String PATH_ACTIVITY_NAV = "/com/nav_activity";
    public static final String PATH_ACTIVITY_ACCOUNT = "/com/account_activity";
    public static final String PATH_ACTIVITY_MEMBER_QRCODE = "/com/member_qrcode_activity";
    public static final String PATH_ACTIVITY_PLAYER = "/com/player_activity";
    public static final String PATH_ACTIVITY_SEARCH = "/com/search_activity";
    public static final String PATH_ACTIVITY_WEBVIEW = "/com/web_view_activity";
    public static final String PATH_ACTIVITY_DISCLAIMERS = "/com/disclaimers_activity";
    public static final String PATH_ACTIVITY_SPLASH = "/com/splash_activity";

    /**
     * 播放器相关
     */
    public static final String BUNDLE_KEY_DEFINITION = "definition";
    public static final String BUNDLE_VALUE_DEFINITION_1080P = "1080P";
    public static final String BUNDLE_VALUE_DEFINITION_720P = "720P";
    public static final String BUNDLE_VALUE_DEFINITION_HIGH = "high";
    public static final String BUNDLE_VALUE_DEFINITION_STANDARD = "standard";
    public static final String BUNDLE_VALUE_DEFINITION_FLUENT = "fluent";

    public static final String BUNDLE_KEY_SELECTION = "selection";
    public static final String BUNDLE_KEY_VIDEO_ID = "qipuId";
    public static final String BUNDLE_KEY_RATIO = "ratio";
    public static final String BUNDLE_VALUE_RATIO_4_3 = "ratio_4_3";
    public static final String BUNDLE_VALUE_RATIO_16_9 = "ratio_16_9";
    public static final String BUNDLE_VALUE_RATIO_21_9 = "ratio_21_9";

    /**
     * 容量单位相关
     */
    public static final long KB = 1024;
    public static final long MB = KB * KB;
    public static final long GB = MB * KB;

    //爱奇艺流量包对应的goodsId
    public static final String GOODSID = "1344471702519816192";

    //驾驶员中心需要爱奇艺执行的动作
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_BIND = "bind";

    public static final String ACTION_VIP = "fromVip";

    // 车速提示 临界值 7km/h
    public static final float CAR_SPEED_TIP_VALUE = 7.0f;

    public static final int NAV_HOME = 1;
    public static final int NAV_CLASSIFY = 2;
    public static final int NAV_ME = 3;

    @IntDef({NAV_HOME, NAV_CLASSIFY, NAV_ME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavMenu {}

}
