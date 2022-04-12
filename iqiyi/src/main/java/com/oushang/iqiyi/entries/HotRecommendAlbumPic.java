package com.oushang.iqiyi.entries;

import com.oushang.lib_base.base.rv.IMultiItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 首页热门频道封面图
 * @Time: 2021/7/26 17:49
 * @Since: 1.0
 */
public class HotRecommendAlbumPic implements IMultiItem {

    /**
     * 热门频道封面
     */
    List<RecommendAlbumPic> mRecommendAlbumPic;

    public HotRecommendAlbumPic() {
        this.mRecommendAlbumPic = new ArrayList<>();
    }

    public HotRecommendAlbumPic(List<RecommendAlbumPic> recommendAlbumPic) {
        this();
        this.mRecommendAlbumPic = recommendAlbumPic;
    }

    public List<RecommendAlbumPic> getRecommendAlbumPic() {
        return mRecommendAlbumPic;
    }

    public void setRecommendAlbumPic(List<RecommendAlbumPic> recommendAlbumPics) {
        this.mRecommendAlbumPic = recommendAlbumPics;
    }

    public void addRecommendAlbumPic(RecommendAlbumPic recommendAlbumPic) {
        this.mRecommendAlbumPic.add(recommendAlbumPic);
    }

    @Override
    public int getViewType() {
        return MutiType.RECOMMEND_ALBUMPIC;
    }

    public static class RecommendAlbumPic {

        /**
         * 频道id
         */
        private int chnId;

        /**
         * 频道名称
         */
        private String chnName;

        /**
         * 视频id
         */
        private long qipuId;

        private long albumId;

        /**
         * 封面图
         */
        private String albumPic;

        /**
         * 一句话看点
         */
        private String focus;

        /**
         * 专辑名称
         */
        private String name;

        /**
         * 所属专辑的名称（如果是视频)
         */
        private String albumName;

        /**
         * 是否独播
         */
        private boolean isExclusive;

        /**
         * 是否vip
         */
        private boolean isVip;

        /**
         * 是否自制
         */
        private boolean isProd;

        public RecommendAlbumPic(int chnId, String chnName, long qipuId, long albumId, String albumPic,
                                 String focus, String name, String albumName,
                                 boolean isExclusive, boolean isVip, boolean isProd) {
            this.chnId = chnId;
            this.chnName = chnName;
            this.qipuId = qipuId;
            this.albumId = albumId;
            this.albumPic = albumPic;
            this.focus = focus;
            this.name = name;
            this.albumName = albumName;
            this.isExclusive = isExclusive;
            this.isVip = isVip;
            this.isProd = isProd;
        }

        public RecommendAlbumPic() {}

        public int getChnId() {
            return chnId;
        }

        public void setChnId(int chnId) {
            this.chnId = chnId;
        }

        public String getChnName() {
            return chnName;
        }

        public void setChnName(String chnName) {
            this.chnName = chnName;
        }

        public long getQipuId() {
            return qipuId;
        }

        public void setQipuId(long qipuId) {
            this.qipuId = qipuId;
        }

        public long getAlbumId() {
            return albumId;
        }

        public void setAlbumId(long albumId) {
            this.albumId = albumId;
        }

        public String getAlbumPic() {
            return albumPic;
        }

        public void setAlbumPic(String albumPic) {
            this.albumPic = albumPic;
        }

        public String getFocus() {
            return focus;
        }

        public void setFocus(String focus) {
            this.focus = focus;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        public boolean isExclusive() {
            return isExclusive;
        }

        public void setExclusive(boolean exclusive) {
            isExclusive = exclusive;
        }

        public boolean isVip() {
            return isVip;
        }

        public void setVip(boolean vip) {
            isVip = vip;
        }

        public boolean isProd() {
            return isProd;
        }

        public void setProd(boolean prod) {
            isProd = prod;
        }
    }
}
