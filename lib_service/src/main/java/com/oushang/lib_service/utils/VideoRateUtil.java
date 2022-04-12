package com.oushang.lib_service.utils;

import com.iqy.iv.CodeRateUtil;

/**
 * @Author: zeelang
 * @Description: 视频清晰度名称获取
 * @Time: 2021/10/14 0014  14:27
 * @Since: 1.0
 */
public class VideoRateUtil {

    public static String getRateName(int rate) {
        return CodeRateUtil.getRateName(rate);
    }

}
