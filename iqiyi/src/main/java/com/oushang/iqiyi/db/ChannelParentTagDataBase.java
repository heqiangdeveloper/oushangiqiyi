package com.oushang.iqiyi.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.oushang.iqiyi.entries.ChannelParentTag;
import com.oushang.lib_base.dao.DatabaseHelper;

/**
 * @Author: zeelang
 * @Description: ***
 * @Time: 2021/12/18 0018  14:03
 * @Since: 1.0
 */
@Deprecated
//@Database(entities = {ChannelParentTag.class}, version = 2, exportSchema = false)
//@TypeConverters({ConvertionFactory.class})
public abstract class ChannelParentTagDataBase extends RoomDatabase {
    private static final String DB_NAME = "ChannelParentTag.db";

    private static ChannelParentTagDataBase INSTANCE;

    private static ChannelParentTagDataBase build() {
        return DatabaseHelper.createDataBase(ChannelParentTagDataBase.class, DB_NAME);
    }

    public static ChannelParentTagDataBase getInstance() {
        if (INSTANCE == null) {
            synchronized (ChannelParentTagDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = build();
                }
            }
        }
        return INSTANCE;
    }


    public abstract ChannelParentTagDao getChannelParentTagDao();
}
