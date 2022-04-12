package com.oushang.lib_service.impls;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oushang.lib_service.constant.Constant;
import com.oushang.lib_service.entries.EpisodeInfoList;
import com.oushang.lib_service.entries.RecommendInfo;
import com.oushang.lib_service.entries.VideoInfo;
import com.oushang.lib_service.interfaces.VideoManager;
import com.oushang.lib_service.iqiyiweb.IqiyiApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @Author: zeelang
 * @Description: 视频管理服务
 * @Time: 2021/6/30 16:10
 * @Since: 1.0
 */
@Route(path = Constant.PATH_SERVICE_VIDEO_MANAGER)
public class VideoManagerService implements VideoManager {
    private static final String TAG = VideoManagerService.class.getSimpleName();

    @Override
    public void init(Context context) {

    }

    /**
     * 获取风云榜列表信息，用于热门数据
     *
     * @return videoInfoList
     */
    @Override
    public Observable<List<VideoInfo>> getRankListInfo(int cid, String cids, int vip, int num) {
        return IqiyiApi.getRanklist(cid, cids, vip, num)
                .map(new Function<String, List<VideoInfo>>() {
                    @Override
                    public List<VideoInfo> apply(String s) throws Exception {
                        List<VideoInfo> videoInfoList = new ArrayList<>();
                        Gson gson = new Gson();
                        JsonArray asJsonArray = new JsonParser().parse(s).getAsJsonArray();
                        for (JsonElement element: asJsonArray) {
                            videoInfoList.add(gson.fromJson(element, VideoInfo.class));
                        }
                        return videoInfoList;
                    }
                });
    }

    /**
     * 获取视频详情（专辑、剧集)
     *
     * @param qipuId 专辑ID或视频ID
     * @return 专辑/节目详情信息
     */
    @Override
    public Observable<VideoInfo> getVideoInfo(long qipuId) {
        return IqiyiApi.getEpgInfo(qipuId)
                .map(new Function<String, VideoInfo>() {
                    @Override
                    public VideoInfo apply(String s) throws Exception {
                        Gson gson = new Gson();
                        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
                        JsonElement jsonElement = jsonObject.get("data");
                        return gson.fromJson(jsonElement, VideoInfo.class);
                    }
                });
    }

    /**
     * 获取剧集列表信息
     *
     * @param qipuId 专辑ID：01或者08结尾
     * @param year   来源类专辑的年份，格式如：2018
     * @param pos    游标开始位置，每次请求后返回，初始传0。如果传递非标准参数，会默认为0。
     * @param num    每页显示条目数，最大60条，超过60，只返回前60条。如果传递非标准参数，会默认为60。
     * @return 分页获取正片剧集列表
     */
    @Override
    public Observable<EpisodeInfoList> getEpisodeInfoList(long qipuId, int year, int pos, int num) {
        return IqiyiApi.getEpisodeList(qipuId, year, pos, num)
                .map(new Function<String, EpisodeInfoList>() {
                    @Override
                    public EpisodeInfoList apply(String s) throws Exception {
                        Gson gson = new Gson();
                        return gson.fromJson(s,EpisodeInfoList.class);
                    }
                });
    }

    /**
     * 相关推荐
     *
     * @param qipuId     作为rec_id传给代理接口
     * @param num        显示条目数，最大60条,超过60条，只返回前60条。如果传递非标准参数，会默认为20。
     * @param ispurchase 付费方式，若无此参数，则返回所有付费和非付费的 0：免费,2：付费已划价(判断是否会员视频，直接判断is_purchas
     * @param ondemand   是否付费点播,若无此参数，则默认返回付费点播和非付费点播的结果
     *                   • 0：不要付费点播的结果
     *                   • 1：仅要付费点播的结果
     *                   • 不传：要付费点播和非付费点播的结果
     *                   目前没有接入付费点播的节目
     * @return 不支持分页，只能限制返回数据个数
     */
    @Override
    public Observable<List<VideoInfo>> getRelatedRecommendList(long qipuId, int num, String ispurchase, String ondemand) {
        return IqiyiApi.getRecommendList(qipuId, num, "", "")
                .map(new Function<String, List<VideoInfo>>() {
                    @Override
                    public List<VideoInfo> apply(@NonNull String s) throws Exception {
                        List<VideoInfo> videoInfoList = new ArrayList<>();
                        Gson gson = new Gson();
                        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
                        JsonArray epg = jsonObject.getAsJsonArray("epg");
                        for (JsonElement element: epg) {
                            videoInfoList.add(gson.fromJson(element, VideoInfo.class));
                        }
                        return videoInfoList;
                    }
                });
    }

    @Override
    public Observable<List<RecommendInfo>> getRecommendInfo(String ppuid, String chns, int ispurchase, int ondemand) {
        return IqiyiApi.recommend(ppuid, chns, ispurchase)
                .map(new Function<String, List<RecommendInfo>>() {
                    @Override
                    public List<RecommendInfo> apply(@NonNull String s) throws Exception {
                        Log.d(TAG, " getRecommendInfo response:" + s);
                        List<RecommendInfo> recommendInfoList = new ArrayList<>();
                        Gson gson = new Gson();
                        JsonArray jsonArray = new JsonParser().parse(s).getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            recommendInfoList.add(gson.fromJson(jsonElement, RecommendInfo.class));
                        }
                        return recommendInfoList;
                    }
                });
    }
}
