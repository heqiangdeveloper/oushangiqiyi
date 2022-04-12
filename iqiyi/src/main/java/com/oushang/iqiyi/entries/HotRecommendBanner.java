package com.oushang.iqiyi.entries;

import com.oushang.lib_base.base.rv.IMultiItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zeelang
 * @Description: 热门推荐banner数据
 * @Time: 2021/7/26 17:38
 * @Since: 1.0
 */
public class HotRecommendBanner implements IMultiItem {

    /**
     * 热门推荐banner列表
     */
    private List<RecommendBanner> mRecommendBannerList;

    public HotRecommendBanner() {
        this.mRecommendBannerList = new ArrayList<>();
    }

    public HotRecommendBanner(List<RecommendBanner> recommendBannerList) {
        this.mRecommendBannerList = recommendBannerList;
    }

    public List<RecommendBanner> getRecommendBannerList() {
        return mRecommendBannerList;
    }

    public void setRecommendBannerList(List<RecommendBanner> mRecommendBannerList) {
        this.mRecommendBannerList = mRecommendBannerList;
    }

    public void addRecommenBanner(RecommendBanner recommendBanner) {
        this.mRecommendBannerList.add(recommendBanner);
    }

    @Override
    public int getViewType() {
        return MutiType.RECOMMEND_BANNER;
    }

    public static class RecommendBanner {

        /**
         * 视频id
         */
        private long qipuId;

        private long albumId;

        /**
         * 海报图
         */
        private String posterPic;

        /**
         * 一句话看点
         */
        private String focus;


        public RecommendBanner(long qipuId, long albumId, String posterPic, String focus) {
            this.qipuId = qipuId;
            this.albumId = albumId;
            this.posterPic = posterPic;
            this.focus = focus;
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

        public String getPosterPic() {
            return posterPic;
        }

        public void setPosterPic(String posterPic) {
            this.posterPic = posterPic;
        }

        public String getFocus() {
            return focus;
        }

        public void setFocus(String focus) {
            this.focus = focus;
        }
    }
}
