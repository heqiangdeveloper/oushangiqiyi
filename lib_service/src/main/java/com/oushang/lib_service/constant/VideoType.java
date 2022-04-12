package com.oushang.lib_service.constant;

/**
 * @Author: zeelang
 * @Description:
 * @Time: 2021/9/7 15:35
 * @Since: 1.0
 */
public class VideoType {

    public static final class PreviewType { //试看类型
        public static final int UNKNOWN = 0;
        public static final int CAN_NOT_PLAY = 1;//不能观看
        public static final int PREVIEW_MINUITE = 2;//分钟试看
        public static final int PREVIEW_EPISODE = 3;//整集试看
        public static final int CAN_PLAY = 4; //完整观看
    }

    public static final class PlayVideoRightTipType { //试看提示
        public static final int TipType_Unknown = -2;
        public static final int TipType_Login_Required = -1;// 需要登录
        public static final int TipType_Member_Failed = 1; // 您可以免费观看前 6 分钟（试看场景下），会员因某些原因鉴权失败(会员影片)
        public static final int TipType_PayVideo_Required = 2; // 您可以免费观看前 6 分钟（试看场景下），需要购买付费点播内容（单点影片）
        public static final int TipType_Voucher_Required = 3; // 您可以免费观看前 6 分钟（试看场景下），需要使用点播券（点播卷影片）
        public static final int TipType_Member_Required = 4;// 您可以免费观看前 6 分钟（试看场景下），非会员需要购买会员(会员影片)
    }
}
