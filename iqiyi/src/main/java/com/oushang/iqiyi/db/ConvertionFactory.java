package com.oushang.iqiyi.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.oushang.lib_service.entries.ChannelTag;

/**
 * @Author: zeelang
 * @Description: 存储转化
 * @Time: 2021/8/25 10:23
 * @Since: 1.0
 */
@Deprecated
public class ConvertionFactory {

//    @TypeConverter
    public static String fromObjectToJson(ChannelTag tag) {
        Gson gson = new Gson();
        return gson.toJson(tag);
    }

//    @TypeConverter
    public static ChannelTag fromJsonToObject(String jsonStr){
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, ChannelTag.class);
    }

}
