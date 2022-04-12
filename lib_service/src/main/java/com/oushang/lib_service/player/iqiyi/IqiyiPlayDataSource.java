package com.oushang.lib_service.player.iqiyi;

import com.oushang.lib_service.entries.DataSource;
import com.oushang.lib_service.entries.IqiyiMedia;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.player.IPlayDataSource;

/**
 * @Author: zeelang
 * @Description: 爱奇艺视频数据源
 * @Time: 2021/7/28 15:08
 * @Since: 1.0
 */
public class IqiyiPlayDataSource implements IPlayDataSource<DataSource> {

    private IqiyiMedia mMedia;

    private DataSource dataSource;

    public IqiyiPlayDataSource(IqiyiMedia media) {
        this.mMedia = media;
    }

    public IqiyiPlayDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public IqiyiPlayDataSource(VideoInfo videoInfo, int startPosition) {
        if (videoInfo != null) {
            String albumId = String.valueOf(videoInfo.getAlbumId());
            String tvId = Long.toString(videoInfo.getQipuId());
            boolean isVip = videoInfo.isVip();
            mMedia = IqiyiMedia.create(albumId, tvId, startPosition, isVip);
        }
    }

    @Override
    public DataSource getData() {
        return dataSource;
    }
}
