package com.oushang.lib_service.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.oushang.lib_service.entries.VideoInfo;

/**
 * @Author: zeelang
 * @Description: 存储转化
 * @Time: 2021/8/25 10:23
 * @Since: 1.0
 */
public class ConvertionFactory {

    @TypeConverter
    public static String fromObjectToJson(VideoInfo info) {
        Gson gson = new Gson();
        return gson.toJson(info);
    }

    @TypeConverter
    public static VideoInfo fromJsonToObject(String jsonStr){
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, VideoInfo.class);
    }

}
