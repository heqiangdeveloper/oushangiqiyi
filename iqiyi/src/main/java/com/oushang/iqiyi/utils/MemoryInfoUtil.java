package com.oushang.iqiyi.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 描述 : 内存信息导出辅助工具类
 */
public class MemoryInfoUtil {

    private static final String TAG = MemoryInfoUtil.class.getSimpleName();

    /**
     * 参考{@link Debug.MemoryInfo}类的OTHER_DEX
     */
    public static final int MEMORY_INFO_OTHER_DEX = 10;

    /**
     * 导出内存信息
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void dumpMemoryInfo(StringBuilder stringBuilder) {
        Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memoryInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AndroidPlatformV23.fillPrintMemoryInfo(stringBuilder, memoryInfo);
        } else {
            AndroidPlatformV19.fillPrintMemoryInfo(stringBuilder, memoryInfo);
        }
        stringBuilder.append(", threads=").append(getCurrentThreadNum());
        appendDexMemoryInfo(stringBuilder, memoryInfo, MEMORY_INFO_OTHER_DEX);
    }

    private static int getCurrentThreadNum() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        if (threadGroup == null) return 0;

        ThreadGroup parent = threadGroup.getParent();
        while (parent != null) {
            threadGroup = parent;
            parent = threadGroup.getParent();
        }
        return threadGroup.activeCount();
    }

    private static void appendDexMemoryInfo(StringBuilder stringBuilder, Debug.MemoryInfo memoryInfo, int which) {
        int dexMmapPss = getMemoryOtherInfo(memoryInfo, "getOtherPss", which);
        int dexMmapPrivate = getMemoryOtherInfo(memoryInfo, "getOtherPrivate", which);
        int dexMmapSwapOutPss = getMemoryOtherInfo(memoryInfo, "getOtherSwappedOutPss", which);
        int dexMmapSwapOut = getMemoryOtherInfo(memoryInfo, "getOtherSwappedOut", which);
        stringBuilder.append(", dex-mmap=(")
                .append(dexMmapPss)
                .append(", ")
                .append(dexMmapPrivate)
                .append(", ")
                .append(dexMmapSwapOutPss)
                .append(", ")
                .append(dexMmapSwapOut)
                .append(")");
    }

    private static int getMemoryOtherInfo(Debug.MemoryInfo memoryInfo, String name, int which) {
        if ((memoryInfo == null) || TextUtils.isEmpty(name)) {
            return 0;
        }
        try {
            Method method = findMethod(Debug.MemoryInfo.class, name, int.class);
            Object object = invokeMethod(method, memoryInfo, which);
            if (object instanceof Integer) {
                return (Integer) object;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.d(TAG, "getMemoryOtherInfo NoSuchMethodException: " + e.getMessage());
        }
        return 0;
    }

    private static class AndroidPlatformV23 {

        @RequiresApi(api = Build.VERSION_CODES.M)
        static void fillPrintMemoryInfo(StringBuilder stringBuilder, Debug.MemoryInfo memoryInfo) {
            String javaHeap = memoryInfo.getMemoryStat("summary.java-heap");
            String nativeHeap = memoryInfo.getMemoryStat("summary.native-heap");
            String code = memoryInfo.getMemoryStat("summary.code");
            String system = memoryInfo.getMemoryStat("summary.system");
            stringBuilder.append("total-pss=")
                    .append(memoryInfo.getTotalPss())
                    .append(", java-heap=")
                    .append(javaHeap)
                    .append(", native-heap=")
                    .append(nativeHeap)
                    .append(", code=")
                    .append(code)
                    .append(", system=")
                    .append(system);
        }
    }

    private static class AndroidPlatformV19 {

        static void fillPrintMemoryInfo(StringBuilder stringBuilder, Debug.MemoryInfo memoryInfo) {
            int javaHeap = getMemorySummaryInfo(memoryInfo, "getSummaryJavaHeap");
            int nativeHeap = getMemorySummaryInfo(memoryInfo, "getSummaryNativeHeap");
            int code = getMemorySummaryInfo(memoryInfo, "getSummaryCode");
            int system = getMemorySummaryInfo(memoryInfo, "getSummarySystem");
            stringBuilder.append("total-pss=")
                    .append(memoryInfo.getTotalPss())
                    .append(", java-heap=")
                    .append(javaHeap)
                    .append(", native-heap=")
                    .append(nativeHeap)
                    .append(", code=")
                    .append(code)
                    .append(", system=")
                    .append(system);
        }

        private static int getMemorySummaryInfo(Debug.MemoryInfo memoryInfo, String name) {
            if ((memoryInfo == null) || TextUtils.isEmpty(name)) {
                return 0; // 0表示错误码
            }
            try {


                Method method = findMethod(Debug.MemoryInfo.class, name);
                Object object = invokeMethod(method, memoryInfo);
                if (object instanceof Integer) {
                    return (Integer) object;
                }
            } catch (NoSuchMethodException  | IllegalAccessException | InvocationTargetException ex) {
                Log.e(TAG, "getMemorySummaryInfo ex=" + Log.getStackTraceString(ex));
            }
            return 0; // 0表示错误码
        }
    }

    private static Method findMethod(Class<?> clz, String methodName) throws NoSuchMethodException {
        Method method = null;
        if (clz != null && methodName != null && !methodName.isEmpty()) {
            method = clz.getMethod(methodName, Void.class);
        }
        return method;
    }

    private static Method findMethod(Class<?> clz, String methodName, Class<?> paramType) throws NoSuchMethodException {
        Method method = null;
        if (clz != null && methodName != null && !methodName.isEmpty()) {
            method = clz.getMethod(methodName, paramType);
        }
        return method;
    }

    private static Object invokeMethod(Method method, Object instance, Object param) throws InvocationTargetException, IllegalAccessException {
        Object invoke = null;
        if (method != null && instance != null) {
            invoke = method.invoke(instance, param);
        }
        return invoke;
    }

    private static Object invokeMethod(Method method, Object instance) throws InvocationTargetException, IllegalAccessException {
        Object invoke = null;
        if (method != null && instance != null) {
            invoke = method.invoke(instance);
        }
        return invoke;
    }
}
