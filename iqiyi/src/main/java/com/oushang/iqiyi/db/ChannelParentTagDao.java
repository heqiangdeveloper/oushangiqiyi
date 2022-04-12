package com.oushang.iqiyi.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.oushang.iqiyi.entries.ChannelParentTag;

import java.util.List;

/**
 * @Author: Administrator
 * @Description: ***
 * @Time: 2021/12/18 0018  14:08
 * @Since: 1.0
 */
//@Dao
@Deprecated
public interface ChannelParentTagDao {

//    @Query("Select * from ChannelParentTag")
    List<ChannelParentTag> queryCommonChannelTagList();

//    @Query("Select * from channelParentTag Where isDelete == 0")
    List<ChannelParentTag> queryValidCommonChannelTag();

//    @Query("Select * from ChannelParentTag Where id=:id")
    ChannelParentTag queryCommonChannelTag(String id);

//    @Delete
    void deleteCommonChannelTag(ChannelParentTag... channelParentTags);

//    @Update
    void updateCommonChannelTag(ChannelParentTag... channelParentTags);

//    @Insert
    void addCommonChannelTag(ChannelParentTag... channelParentTags);
}
