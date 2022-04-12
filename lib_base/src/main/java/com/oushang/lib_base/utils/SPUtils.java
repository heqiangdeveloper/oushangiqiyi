package com.oushang.lib_base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.oushang.lib_base.env.LibraryRuntimeEnv;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Author: zeelang
 * @Description: SharePreference工具类
 * @Date: 2021/6/28
 */
public class SPUtils {

    /**
     * 缺省的空间名
     */
    public static final String DEFAULT_SPACE = "oushang";

    /**
     * 获取SharePreference
     * @param space space
     * @return SharePreference
     */
    public static SharedPreferences getSP(String space) {
        SharedPreferences sp;
        if (Objects.isNull(space)) {
            sp = LibraryRuntimeEnv.get().getContext().getSharedPreferences(DEFAULT_SPACE, Context.MODE_PRIVATE);
        } else {
            sp = LibraryRuntimeEnv.get().getContext().getSharedPreferences(space, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 添加字段
     * @param key key
     * @param value value
     * @return true or false
     */
    public static boolean putShareValue(String key, Object value) {
       return putShareValue(null, key, value);
    }

    /**
     * 添加字段
     * @param space 空间名
     * @param key key
     * @param value value
     * @return true or false
     */
    public static boolean putShareValue(String space, String key, Object value) {
        SharedPreferences sp = getSP(space);
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer)value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if(value instanceof Double) {
            editor.putLong(key, Double.doubleToLongBits((Double) value));
        }
        return editor.commit();
    }

    public static <T> boolean putShareValue(String space, String key, List<T> list) {
        SharedPreferences sp = LibraryRuntimeEnv.get().getContext().getSharedPreferences(space, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.clear();
        editor.putString(key, json);
        return editor.commit();
    }

    public static <T> List<T> getShareValue(String space, String key, Class<T> cls) {
        List<T> list = new ArrayList<>();
        SharedPreferences sp = LibraryRuntimeEnv.get().getContext().getSharedPreferences(space, Context.MODE_PRIVATE);
        String json = sp.getString(key, "");
        Gson gson = new Gson();
        if (json != null && !json.isEmpty()) {
            JsonElement element = new JsonParser().parse(json);
            if (element.isJsonArray()) {
                JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
                for (JsonElement ele : jsonArray) {
                    list.add(gson.fromJson(ele, cls));
                }
            }
        }
        return list;
    }


    /**
     * 获取String值
     * @param key key
     * @return String
     */
    public static String getShareString(String key) {
        return getShareString(key, "");
    }

    /**
     * 获取String值
     * @param key key
     * @param defvalue 默认值
     * @return String
     */
    public static String getShareString(String key, String defvalue) {
        SharedPreferences sp = getSP(null);
        return sp.getString(key,defvalue);
    }

    /**
     * 获取String值
     * @param space 空间名
     * @param key key
     * @param defvalue 默认值
     * @return String
     */
    public static String getShareString(String space, String key, String defvalue) {
        SharedPreferences sp = getSP(space);
        return sp.getString(key,defvalue);
    }

    /**
     * 获取int值
     * @param key key
     * @return int
     */
    public static int getShareInteger(String key) {
        return getShareInteger(key,0);
    }

    /**
     * 获取int值
     * @param key key
     * @param defvalue 默认值
     * @return int
     */
    public static int getShareInteger(String key, Integer defvalue) {
        SharedPreferences sp = getSP(null);
        return sp.getInt(key,defvalue);
    }

    /**
     * 获取int值
     * @param space 空间名
     * @param key  key
     * @param defvalue 默认值
     * @return int
     */
    public static int getShareInteger(String space, String key, Integer defvalue) {
        SharedPreferences sp = getSP(space);
        return sp.getInt(key,defvalue);
    }

    /**
     * 获取float值
     * @param key key
     * @return float
     */
    public static Float getShareFloat(String key) {
        return getShareFloat(key, 0.0f);
    }

    /**
     * 获取float值
     * @param key key
     * @param defvalue 默认值
     * @return float
     */
    public static Float getShareFloat(String key, Float defvalue) {
        SharedPreferences sp = getSP(null);
        return sp.getFloat(key,defvalue);
    }

    /**
     * 获取float值
     * @param space 空间名
     * @param key key
     * @param defvalue 默认值
     * @return float
     */
    public static Float getShareFloat(String space, String key, Float defvalue) {
        SharedPreferences sp = getSP(space);
        return sp.getFloat(key,defvalue);
    }

    /**
     * 获取long值
     * @param key key
     * @return long
     */
    public static long getShareLong(String key) {
        return getShareLong(key,0L);
    }

    /**
     * 获取long值
     * @param key key
     * @param defvalue 默认值
     * @return long
     */
    public static long getShareLong(String key, Long defvalue) {
        SharedPreferences sp = getSP(null);
        return sp.getLong(key,defvalue);
    }

    /**
     * 获取long值
     * @param space 空间名
     * @param key key
     * @param defvalue 默认值
     * @return long
     */
    public static long getShareLong(String space, String key, Long defvalue) {
        SharedPreferences sp = getSP(space);
        return sp.getLong(key,defvalue);
    }

    /**
     * 获取boolean值
     * @param key key
     * @return true or false
     */
    public static boolean getShareBoolean(String key) {
        return getShareBoolean(key,false);
    }

    /**
     * 获取boolean值
     * @param key key
     * @param defvalue 默认值
     * @return true or false
     */
    public static boolean getShareBoolean(String key, Boolean defvalue) {
        SharedPreferences sp = getSP(null);
        return sp.getBoolean(key,defvalue);
    }

    /**
     * 获取boolean值
     * @param space 空间名
     * @param key key
     * @param defvalue 默认值
     * @return true or false
     */
    public static boolean getShareBoolean(String space, String key, Boolean defvalue) {
        SharedPreferences sp = getSP(space);
        return sp.getBoolean(key,defvalue);
    }

    /**
     * 获取double值
     * @param key key
     * @return double
     */
    public static double getShareDouble(String key) {
        return getShareDouble(key, 0.0);
    }

    /**
     * 获取double值
     * @param key key
     * @param defValue 默认值
     * @return double
     */
    public static double getShareDouble(String key, Double defValue) {
        SharedPreferences sp = getSP(null);
        return Double.longBitsToDouble(sp.getLong(key, Double.doubleToLongBits(defValue)));
    }

    /**
     * 获取double值
     * @param space 空间名
     * @param key key
     * @param defValue 默认值
     * @return double
     */
    public static double getShareDouble(String space, String key, Double defValue) {
        SharedPreferences sp = getSP(space);
        return Double.longBitsToDouble(sp.getLong(key, Double.doubleToLongBits(defValue)));
    }

    /**
     * 删除字段
     * @param key key
     * @return true or false
     */
    public static boolean removeShareValue(String key) {
        SharedPreferences.Editor editor = getSP(null).edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 删除字段
     * @param space 空间名
     * @param key key
     * @return true or false
     */
    public static boolean removeShareValue(String space,String key) {
        SharedPreferences.Editor editor = getSP(space).edit();
        editor.remove(key);
        return editor.commit();
    }

    /**
     * 清除默认的SharePreference
     * @return true or false
     */
    public static boolean clearShareValue() {
        return clearShareValue(null);
    }

    /**
     * 清空sharePreference
     * @param space 空间名
     * @return true or false
     */
    public static boolean clearShareValue(String space) {
        SharedPreferences.Editor editor = getSP(space).edit();
        editor.clear();
        return editor.commit();
    }
}
