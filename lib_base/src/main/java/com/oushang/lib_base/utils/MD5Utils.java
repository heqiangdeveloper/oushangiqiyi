package com.oushang.lib_base.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: zeelang
 * @Description: md5工具类
 * @Time: 2021/6/28 10:49
 * @Since: 1.0
 */
public class MD5Utils {

    public static final String TAG = "MD5Utils";

    private static final char[] DIGITS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    /**
     * md5加密
     * @param text 字符串(明文）
     * @return 字符串（密文）
     */
    public static String md5(String text) {
        MessageDigest messageDigest = null;
        String md5Str = "";
        try {
            //获取md5算法
            messageDigest = MessageDigest.getInstance("MD5");
            //更新摘要
            messageDigest.update(text.getBytes(StandardCharsets.UTF_8));
            //完成计算，摘要被重置
            byte[] bytes = messageDigest.digest();
            md5Str = new String(encodeHex(bytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5Str;
    }

    /**
     * 二进制转十六进制
     * @param data byte数组
     * @return 字符数组
     */
    public static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l<<2];
        for (int i = 0, j = 0; i <l; i++) {
            //字节高4位
            out[j++] = DIGITS[(0xf0 & data[i]) >>>4];
            //字节低4位
            out[j++] = DIGITS[0x0f & data[i]];
        }
        return out;
    }


}
