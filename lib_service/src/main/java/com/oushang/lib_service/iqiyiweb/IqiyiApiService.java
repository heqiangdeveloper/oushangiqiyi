package com.oushang.lib_service.iqiyiweb;

import com.oushang.lib_service.entries.RegisterEntity;

import java.util.WeakHashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface IqiyiApiService {

    /**
     * 设备注册
     * @return 设备注册，后续操作都需要做设备鉴权
     */
    @POST("register")
    Observable<String> register(@Body RegisterEntity entity);

    /**
     * 导航列表
     * @return 导航列表数据
     */
    @GET("navlist")
    Observable<String> navlist();

    /**
     * 推荐接口
     * @param params 请求参数
     * @return 获取各频道及轮播图推荐数据
     */
    @GET("recommend")
    Observable<String> recommend(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 节目搜索
     * @param params 请求参数
     * @return 获取各频道及轮播图推荐数据
     */
    @GET("search")
    Observable<String> search(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 频道详情
     * @param params 频道params
     * @return 查询频道标签列表及标签下的数据列表
     */
    @GET("channel")
    Observable<String> channel(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 播放鉴权
     * @param qipuId 播放视频的奇谱的id
     * @return 视频播放前，需要先做鉴权，鉴权通过，才能播放
     */
    @GET("playCheck")
    Observable<String> playCheck(@Query(value = "qipuId") long qipuId);

    /**
     * 剧集列表
     * @param qipUId 专辑ID：01或者08结尾
     * @param params 请求参数
     * @return 分页获取正片剧集列表
     */
    @GET("episodeList/{qipuId}")
    Observable<String> episodeList(@Path("qipuId") long qipUId, @QueryMap WeakHashMap<String, Object> params);

    /**
     * 专辑/节目详情接口
     * @param qipUId 专辑ID或视频ID
     * @return
     */
    @GET("epgInfo/{qipuId}")
    Observable<String> epgInfo(@Path("qipuId") long qipUId);

    /**
     * 相关推荐
     * @param qipUId 作为rec_id传给代理接口
     * @param params 请求参数
     * @return 不支持分页，只能限制返回数据个数；
     */
    @GET("recommend/{qipuId}")
    Observable<String> recommendList(@Path("qipuId") long qipUId, @QueryMap WeakHashMap<String, Object> params);

    /**
     * 人物搜索
     * @param params 请求参数
     * @return 对接IPM60系统人物搜索结果数据
     */
    @GET("peoplearch")
    Observable<String> peoplesearch(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 资源位
     * @param params 请求参数
     * @return 获取资源位节目信息
     */
    @GET("resource")
    Observable<String> resource(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 风云榜
     * @param params 请求参数
     * @return 获取各频道风云榜数据
     */
    @GET("ranklist")
    Observable<String> ranklist(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 热词搜索
     * @param count 吐出的结果的数量, 默认20
     * @return 获取热搜词
     */
    @GET("hotwords")
    Observable<String> hotwords(@Query("count") int count);

    /**
     * 首字母推荐
     * @param params 请求参数
     * @return 首字母推荐数据
     */
    @GET("suggest")
    Observable<String> suggest(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 功能管理
     * @return 返回功能信息
     */
    @GET("funcs")
    Observable<String> funcs();

    /**
     * 预告片列表
     * @param qipuId 节目奇普id
     * @param params 请求参数
     * @return 通过传节目id获取该节目所关联的预告片花，支持通过00数据获取
     */
    @GET("/prevueList/{qipuId}")
    Observable<String> prevueList(@Path("qipuId") long qipuId, @QueryMap WeakHashMap<String, Object> params);

    /**
     * 预览图
     * @param qipuId 节目奇谱ID
     * @return  获取剧集的预览图
     */
    @GET("rePic/{qipuId}")
    Observable<String> prePic(@Path("qipuId") long qipuId);

    /**
     * 系统时间
     * @return 获取系统时间
     */
    @GET("systime")
    Observable<String> systime();

    /**
     *播单接口
     * @return 返回播单里的节目数据
     */
    @GET("playlist/{qipuId}")
    Observable<String> playlist(@Path("qipuId") long qipuId, @QueryMap WeakHashMap<String, Object> params);

    /**
     *爱奇艺号内容列表
     * @param userId 爱奇艺号ID
     * @return 获取爱奇艺号下的节目列表
     */
    @GET("selfmedia/{userId}")
    Observable<String> selfMedia(@Path("userId") long userId, @QueryMap WeakHashMap<String, Object> params);

    /**
     * 人物识别接口
     * @param params 请求参数
     * @return ai人物识别接口，增加设备鉴权
     */
    @GET("ai_identify/person")
    Observable<String> person(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 截图接口
     * @param params 请求参数
     * @return 实时截图服务代理接口
     */
    @GET("screenshot")
    Observable<String> screenshot(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 虚拟人物识别接口
     * @param params 请求参数
     * @return  ai虚拟人物识别接口
     */
    @GET("ai_identify/cartoon")
    Observable<String> cartoon(@QueryMap WeakHashMap<String, Object> params);

    /**
     * 动漫人物相关视频
     * @param params 请求参数
     * @return 虚拟人物关联视频
     */
    @GET("ai_cartoon/episodeList")
    Observable<String> cartoonEpisodeList(@QueryMap WeakHashMap<String, Object> params);

}
