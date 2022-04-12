package com.oushang.lib_service.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.oushang.lib_base.dao.DatabaseHelper;
import com.oushang.lib_service.entries.VideoInfoRecord;

/**
 * @Author: zeelang
 * @Description: 视频记录数据库
 * @Time: 2021/8/25 10:03
 * @Since: 1.0
 */
@Database(entities = {VideoInfoRecord.class}, version = 1, exportSchema = false)
@TypeConverters({ConvertionFactory.class})
public abstract class VideoInfoRecordDataBase extends RoomDatabase {
    private static final String DB_NAME = "VideoInfoRecord.db";

    private  static VideoInfoRecordDataBase buildDataBase() {
        return DatabaseHelper.createDataBase(VideoInfoRecordDataBase.class, DB_NAME);
    }

    public static VideoInfoRecordDataBase INSTANCE;

    public static VideoInfoRecordDataBase getInstance() {
        if (INSTANCE == null) {
            synchronized (VideoInfoRecordDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDataBase();
                }
            }
        }
        return INSTANCE;
    }

    public abstract VideoInfoRecordDao getVideoInfoRecordDao();
}
