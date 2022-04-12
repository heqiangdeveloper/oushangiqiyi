package com.oushang.lib_base.log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class LogUtils {
    private static final String TAG = "oushang";//tag
    private static final boolean IS_WRITE_TO_DISK = false;//是否写入到磁盘
    private static final boolean IS_DEBUG = false;//debug

    /**
     * 初始化
     */
    public static void init() {
        FormatStrategy formatStrategy = TxtFormatStrategy.newBuilder()
                .tag(TAG)
                .build();
        if (IS_WRITE_TO_DISK) {
            Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
        }
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }
    public static void initClear(String appFlag) {
        FormatStrategy formatStrategy = TxtFormatStrategy.newBuilder()
                .tag(appFlag)
                .build();
        if (IS_WRITE_TO_DISK) {
            Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
        }
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    public static void init(String tag) {
        PrettyFormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag(tag)
                .logStrategy(new LogcatLogStrategy())
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }

    /**
     * debug log
     * @param log log msg
     */
    public static void d(String log) {
        if (IS_DEBUG) {
            Logger.d(log);
        }
    }

    /**
     * info log
     * @param log msg
     */
    public static void i(String log) {
        if (IS_DEBUG) {
            Logger.i(log);
        }
    }

    /**
     * error log
     * @param log msg
     */
    public static void e(String log) {
        if (IS_DEBUG) {
            Logger.e(log);
        }
    }

    /**
     * warning log
     * @param log msg
     */
    public static void w(String log) {
        if (IS_DEBUG) {
            Logger.w(log);
        }
    }

    /**
     * verbose log
     * @param log msg
     */
    public static void v(String log) {
        if (IS_DEBUG) {
            Logger.v(log);
        }
    }

    /**
     * print json data
     * @param json json
     */
    public static void json(String json) {
        if (IS_DEBUG) {
            Logger.json(json);
        }
    }

}
