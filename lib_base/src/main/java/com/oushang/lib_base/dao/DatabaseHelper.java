package com.oushang.lib_base.dao;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.oushang.lib_base.env.LibraryRuntimeEnv;

/**
 * @Author: zeelang
 * @Description: 数据库
 * @Time: 2021/7/2 11:44
 * @Since: 1.0
 */
public class DatabaseHelper {

    /**
     * 创建数据库
     * @param klass 数据库类名
     * @param dbName 数据库名
     * @param <T> 泛型
     * @return
     */
    public static <T extends RoomDatabase> T createDataBase(Class<T> klass, String dbName) {
        return (T) Room.databaseBuilder(LibraryRuntimeEnv.get().getContext(), klass, dbName)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }


}
