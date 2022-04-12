package com.oushang.iqiyi.statistics;

import java.util.HashMap;

/**
 * @Author: zeelang
 * @Description: 时间统计
 * @Time: 2022/2/12 11:54
 * @Since:
 */
public class TimeStatistics {
    private static HashMap<String, Long> sCalculateTimeMap = new HashMap<>();
    public static final String COLD_START = "cold_start";

    public static void beginTimeCalculate(String key) {
        long currentTime = System.currentTimeMillis();
        sCalculateTimeMap.put(key, currentTime);
    }

    public static void endTimeCalculate(String key) {
        long currentTime = System.currentTimeMillis();
        Long beginTime = sCalculateTimeMap.get(key);
        long time = -1;
        if(beginTime != null) {
            time = currentTime - beginTime;
        }
        sCalculateTimeMap.put(key, time);
    }

    public static long getCalculateTime(String key) {
        Long time = null;
        time = sCalculateTimeMap.get(key);
        if(time != null) {
            return time;
        }
        return -1;
    }

    public static void clearCalculateTime(String key) {
        sCalculateTimeMap.remove(key);
    }
}
