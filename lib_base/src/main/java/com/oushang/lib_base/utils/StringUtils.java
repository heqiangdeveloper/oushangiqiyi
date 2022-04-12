package com.oushang.lib_base.utils;

import android.util.Base64;


import com.oushang.lib_base.log.LogUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String CUSTOMER_PKGNAME_SPLIT = ";";
    private static StringBuilder mFormatBuilder = new StringBuilder();
    private static Formatter mFormatter;
    private static final String[] EMPTY_STRING_ARRAY;

    static {
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        EMPTY_STRING_ARRAY = new String[0];
    }

    /**
     * 是否为空
     * @param str CharSequence
     * @return boolean
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 是否是数字
     * @param str 字符串
     * @return boolean
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 获取字符串长度
     * @param str CharSequence
     * @return
     */
    public static int getLength(CharSequence str) {
        return isEmpty(str) ? 0 : str.length();
    }

    public static boolean isTrimEmpty(String text) {
        return text == null || text.trim().equals("");
    }

    public static boolean isEmpty(String... strs) {
        if (strs == null) {
            return true;
        } else {
            String[] var4 = strs;
            int var3 = strs.length;

            for(int var2 = 0; var2 < var3; ++var2) {
                String str = var4[var2];
                if (str != null && !str.isEmpty()) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean hasEmpty(String... strs) {
        if (strs == null) {
            return true;
        } else {
            String[] var4 = strs;
            int var3 = strs.length;

            for(int var2 = 0; var2 < var3; ++var2) {
                String str = var4[var2];
                if (str == null || str.isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }

    public static int parse(String str, int defaultValue) {
        int value = defaultValue;

        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException var4) {

            LogUtils.d( "parse exception input = " + str + var4);
        }

        return value;
    }

    public static long parse(String str, long defaultValue) {
        long value = defaultValue;

        try {
            value = Long.parseLong(str);
        } catch (NumberFormatException var6) {
        }

        return value;
    }

    public static int parseInt(String str) {
        try {
            if (str != null) {
                return Integer.parseInt(str.trim());
            }
        } catch (Exception var2) {
        }

        return 0;
    }

    public static long parseLong(String str) {
        try {
            if (str != null) {
                return Long.parseLong(str.trim());
            }
        } catch (Exception var2) {
        }

        return 0L;
    }

    public static String base64(String str) {
        try {
            byte[] bytes = str.getBytes("utf-8");
            return Base64.encodeToString(bytes, 1).toString().trim();
        } catch (Exception var2) {
            return "";
        }
    }

    public static String md5(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuffer sbuffer = new StringBuffer();

            for(int i = 0; i < bytes.length; ++i) {
                if (Integer.toHexString(255 & bytes[i]).length() == 1) {
                    sbuffer.append("0").append(Integer.toHexString(255 & bytes[i]));
                } else {
                    sbuffer.append(Integer.toHexString(255 & bytes[i]));
                }
            }

            return sbuffer.toString();
        } catch (Exception var5) {
            return "";
        }
    }

    public static String replaceBlank(String src) {
        Pattern pattern = Pattern.compile("\\s*|\t|\r|\n");
        Matcher matcher = pattern.matcher(src);
        String result = matcher.replaceAll("");
        return result;
    }

    public static String md5MultScreen(String str) {
        String md5str = md5(str);
        if (md5str != null && md5str.length() != 0) {
            String result = "";
            if (md5str.length() == 32) {
                StringBuffer sbuffer = new StringBuffer();
                String str1 = md5str.substring(0, 6);
                String str2 = md5str.substring(6, 16);
                String str3 = md5str.substring(16, 26);
                String str4 = md5str.substring(26, md5str.length());
                sbuffer.append(str1);
                sbuffer.append(str4);
                sbuffer.append(str3);
                sbuffer.append(str2);
                sbuffer = sbuffer.reverse();
                result = md5(sbuffer.substring(4, 15));
            }

            return result;
        } else {
            return "";
        }
    }

    public static String formatLongToTimeStr(Long l) {
        int minute = 0;
        int second = l.intValue() / 1000;
        if (second > 60) {
            minute = second / 60;
            second %= 60;
        }

        return minute + "分";
    }

    public static String stringForTime(int timeMs, boolean isFull) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = totalSeconds / 60 % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (isFull) {
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return hours > 0 ? mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString() : mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String stringForTime(int timeMs) {
        return stringForTime(timeMs, false);
    }

    public static String filterSuffix(String str) {
        return str;
    }

    public static List<String> parseStringtoList(String string) {
        String[] StringArray = string.split(";");
        List<String> stringList = new ArrayList();
        if (StringArray != null) {
            int i = 0;

            for(int size = StringArray.length; i < size; ++i) {
                if (StringArray[i] != null && !"".equals(StringArray[i].trim())) {
                    stringList.add(StringArray[i]);
                }
            }
        }

        return stringList;
    }

    public static boolean isMailAddress(String mail) {
        if (!isEmpty((CharSequence)mail)) {
            Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
            Matcher m = p.matcher(mail);
            return m.matches();
        } else {
            return false;
        }
    }

    public static boolean checkPassword(String input_password) {
        int length = input_password.length();
        if (length >= 4 && length <= 20) {
            Pattern pattern = Pattern.compile("[a-z0-9A-Z]+");
            Matcher matcher = pattern.matcher(input_password);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public static boolean isMobileNO(String mobiles) {
        if (!isEmpty((CharSequence)mobiles)) {
            Pattern p = Pattern.compile("^\\d{11}$");
            Matcher m = p.matcher(mobiles);
            return m.matches();
        } else {
            return false;
        }
    }

    public static boolean equals(CharSequence cs1, CharSequence cs2) {
        return cs1 == null ? cs2 == null : cs1.equals(cs2);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        } else {
            int len = str.length();
            if (len == 0) {
                return EMPTY_STRING_ARRAY;
            } else {
                List<String> list = new ArrayList();
                int sizePlus1 = 1;
                int i = 0;
                int start = 0;
                boolean match = false;
                boolean lastMatch = false;
                if (separatorChars != null) {
                    if (separatorChars.length() != 1) {
                        label89:
                        while(true) {
                            while(true) {
                                if (i >= len) {
                                    break label89;
                                }

                                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                                    if (match || preserveAllTokens) {
                                        lastMatch = true;
                                        if (sizePlus1++ == max) {
                                            i = len;
                                            lastMatch = false;
                                        }

                                        list.add(str.substring(start, i));
                                        match = false;
                                    }

                                    ++i;
                                    start = i;
                                } else {
                                    lastMatch = false;
                                    match = true;
                                    ++i;
                                }
                            }
                        }
                    } else {
                        char sep = separatorChars.charAt(0);

                        label73:
                        while(true) {
                            while(true) {
                                if (i >= len) {
                                    break label73;
                                }

                                if (str.charAt(i) == sep) {
                                    if (match || preserveAllTokens) {
                                        lastMatch = true;
                                        if (sizePlus1++ == max) {
                                            i = len;
                                            lastMatch = false;
                                        }

                                        list.add(str.substring(start, i));
                                        match = false;
                                    }

                                    ++i;
                                    start = i;
                                } else {
                                    lastMatch = false;
                                    match = true;
                                    ++i;
                                }
                            }
                        }
                    }
                } else {
                    label105:
                    while(true) {
                        while(true) {
                            if (i >= len) {
                                break label105;
                            }

                            if (Character.isWhitespace(str.charAt(i))) {
                                if (match || preserveAllTokens) {
                                    lastMatch = true;
                                    if (sizePlus1++ == max) {
                                        i = len;
                                        lastMatch = false;
                                    }

                                    list.add(str.substring(start, i));
                                    match = false;
                                }

                                ++i;
                                start = i;
                            } else {
                                lastMatch = false;
                                match = true;
                                ++i;
                            }
                        }
                    }
                }

                if (match || preserveAllTokens && lastMatch) {
                    list.add(str.substring(start, i));
                }

                return (String[])list.toArray(new String[list.size()]);
            }
        }
    }
}
