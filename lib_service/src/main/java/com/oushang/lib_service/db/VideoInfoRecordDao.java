package com.oushang.lib_service.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.oushang.lib_service.entries.VideoInfoRecord;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 播放记录
 * @Time: 2021/8/24 14:36
 * @Since: 1.0
 */
@Dao
public interface VideoInfoRecordDao {

    @Query("SELECT * FROM videoInfoRecord")
    List<VideoInfoRecord> queryAllRecord();

    @Query("SELECT * FROM videoInfoRecord WHERE albumType = 1")
    List<VideoInfoRecord> queryHistoryRecord();

    @Query("SELECT * FROM videoInfoRecord WHERE albumType = 2")
    List<VideoInfoRecord> queryFavoriteRecord();

    @Insert
    void insertRecord(VideoInfoRecord... records);

    @Insert
    void insertRecord(List<VideoInfoRecord> recordList);

    @Update
    void updateRecord(VideoInfoRecord... records);

    @Delete
    void deleteRecord(VideoInfoRecord... records);

}
