package com.oushang.iqiyi.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.oushang.iqiyi.common.Constant;
import com.oushang.lib_base.utils.SPUtils;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @Author: zeelang
 * @Description: 缓存工具类
 * @Time: 2021/8/5 16:12
 * @Since: 1.0
 */
public class CacheUtils {
    private static final String TAG = CacheUtils.class.getSimpleName();

    public static final String CACHE_PATH = Environment.getDataDirectory().getAbsolutePath();

    public static long getTotalCacheSize(Context context) {
        if (context == null) {
            Log.e(TAG, "call getTotalCacheSize, context is null");
            return 0;
        }
        String cacheDir = context.getCacheDir().getAbsolutePath();
        String root = cacheDir.substring(0,cacheDir.indexOf("cache"));
        long cacheSize = getCacheSize(cacheDir);
        long sharedPrefsSize = getSharedPrefsSize(root + "shared_prefs");
        long databasesSize =getDatabasesSize(root + "databases");
        Log.d(TAG, "cacheSize: " + cacheSize /1024 + " KB, shared_prefs size: " + sharedPrefsSize /1024 + "KB"
                + ", databases size: " + databasesSize /1024 + "KB");
        //return cacheSize + sharedPrefsSize + databasesSize;
        return cacheSize + databasesSize;
    }

    public static long getSharedPrefsSize(String path) {
        return getFileSize(path);
    }

    public static long getDatabasesSize(String path) {
        return getFileSize(path);
    }

    public static void clearApplicationCache(Context context) {
        if (context == null) {
            Log.e(TAG, "call clearApplicationCache, context is null");
            return;
        }
        String cacheDir = context.getCacheDir().getAbsolutePath();
        String root = cacheDir.substring(0,cacheDir.indexOf("cache"));
        String sharedPrefsDir = root + "shared_prefs";
        String databaseDir =  root + "databases";
        delete(cacheDir);
//        delete(sharedPrefsDir);
//        SPUtils.putShareValue(Constant.SP_IQIYI_SPACE, Constant.SP_KEY_USER_AGREE, false);
        delete(databaseDir);
    }

    public static long getCacheSize(String path) {
        return getFileSize(path);
    }


    public static long getFileSize(String path) {
        long size = 0;
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                size = file.length();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        size += getFileSize(f.getAbsolutePath());
                    }
                }
            }
        }
        return size;
    }

    /** 删除文件，可以是文件或文件夹
     * @param delFile 要删除的文件夹或文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile) {
        File file = new File(delFile);
        if (!file.exists()) {
            //Toast.makeText(MyApplication.getContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile);
        }
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                //Toast.makeText(MyApplication.getContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            //Toast.makeText(MyApplication.getContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            //Toast.makeText(MyApplication.getContext(), "删除目录失败：" + filePath + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            //Toast.makeText(MyApplication.getContext(), "删除目录失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
            //Toast.makeText(MyApplication.getContext(), "删除目录：" + filePath + "失败！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f: files) {
                        return deleteFile(f.getAbsolutePath());
                    }
                }
            }
        }
        return false;
    }

    public static String byte2Format(long bytes) {
        //格式化小数
        DecimalFormat format = new DecimalFormat("###.00");
        if (bytes / Constant.GB > 1) {
            return format.format(bytes / Constant.GB) + "GB";
        } else if (bytes / Constant.MB > 1) {
            return format.format(bytes / Constant.MB) + "MB";
        } else if (bytes / Constant.KB > 1) {
            return format.format(bytes / Constant.KB) + "KB";
        } else {
            return bytes + "B";
        }

    }



}
