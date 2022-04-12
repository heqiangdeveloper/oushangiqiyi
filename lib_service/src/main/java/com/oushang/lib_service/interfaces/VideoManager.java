package com.oushang.lib_service.interfaces;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.RecommendInfo;
import com.oushang.lib_service.entries.VideoInfo;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 视频数据管理
 * @Date: 2021/6/24
 */
public interface VideoManager extends IProvider {

    /**
     * 获取风云榜列表信息，
     * @return rankListInfo
     */
    Observable<List<VideoInfo>> getRankListInfo(int cid, String cids, int vip, int num);

    /**
     * 获取视频详情（专辑、剧集)
     * @param qipuId 专辑ID或视频ID
     * @return       专辑/节目详情信息
     */
    Observable<VideoInfo> getVideoInfo(long qipuId);

    /**
     * 获取剧集列表信息
     * @param qipuId 专辑ID：01或者08结尾
     * @param year   来源类专辑的年份，格式如：2018
     * @param pos    游标开始位置，每次请求后返回，初始传0。如果传递非标准参数，会默认为0。
     * @param num    每页显示条目数，最大60条，超过60，只返回前60条。如果传递非标准参数，会默认为60。
     * @return       分页获取正片剧集列表
     */
    Observable<EpisodeInfoList> getEpisodeInfoList(long qipuId, int year, int pos, int num);


    /**
     * 相关推荐
     * @param qipuId 作为rec_id传给代理接口
     * @param num 显示条目数，最大60条,超过60条，只返回前60条。如果传递非标准参数，会默认为20。
     * @param ispurchase 付费方式，若无此参数，则返回所有付费和非付费的 0：免费,2：付费已划价(判断是否会员视频，直接判断is_purchas
     * @param ondemand 是否付费点播,若无此参数，则默认返回付费点播和非付费点播的结果
     * • 0：不要付费点播的结果
     * • 1：仅要付费点播的结果
     * • 不传：要付费点播和非付费点播的结果
     * 目前没有接入付费点播的节目
     * @return 不支持分页，只能限制返回数据个数
     */
    Observable<List<VideoInfo>> getRelatedRecommendList(long qipuId, int num, String ispurchase, String ondemand);

    /**
     * 推荐接口
     *
     * @param ppuid      注册用户ID
     * @param chns       可传递电视剧，电影，综艺，少儿，动漫五个值的组合，英文逗号分隔，不传取全部数据
     * @param ispurchase 付费方式，若无此参数，则返回所有付费和非付费的 0：免费,2：付费已划价(判断是否会员视频，直接判断is_purchas
     * @return 获取各频道及轮播图推荐数据
     */
    Observable<List<RecommendInfo>> getRecommendInfo(String ppuid, String chns, int ispurchase, int ondemand);





}
