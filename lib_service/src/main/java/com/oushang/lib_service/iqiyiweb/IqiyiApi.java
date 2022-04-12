package com.oushang.lib_service.iqiyiweb;

import android.util.Log;

import androidx.annotation.Nullable;

import com.oushang.lib_base.net.RetrofitClient;
import com.oushang.lib_base.net.RetrofitConfig;
import com.oushang.lib_service.entries.RegisterEntity;
import com.oushang.lib_service.iqiyiweb.interceptors.AuthorizationInterceptor;

import java.util.WeakHashMap;

import io.reactivex.Observable;

/**
 * @Author: zeelang
 * @Description: 爱奇艺web接口
 * @Date: 2021/6/25
 */
public class IqiyiApi {
    private static final String TAG = IqiyiApi.class.getSimpleName();
    private static final String BASE_URL = "https://sdkcar.iqiyi.com/api/";
    public static final String DEVICE_UUID = "20200623170913121mUPVmjbcN101596";
    public static final String SN = "LS5A3CJC3JF890055";
    private static IqiyiApiService iqiyiApiService;

    private static IqiyiApiService getIqiyiApiService() {
        if (iqiyiApiService == null) {
            RetrofitConfig.getInstance()
                    .withHost(BASE_URL)
                    .withConnectTimeout(30)
                    .withReadTimeout(30)
                    .addInterceptor(new AuthorizationInterceptor());
            iqiyiApiService = RetrofitClient.newBuilder()
                    .baseUrl(BASE_URL)
                    .build()
                    .getApiService(IqiyiApiService.class);
        }
        return iqiyiApiService;
    }


    /**
     * 设备注册
     *
     * @param apkVer     客户端版本号
     * @param deviceUUID 硬件型号uuid，由爱奇艺分配
     * @param sn         SN
     * @param model      设备型号
     * @param hardware   芯片信息
     * @return 设备注册，后续操作都需要做设备鉴权
     */
    public static Observable<String> register(@Nullable String apkVer, String deviceUUID,
                                              String sn, @Nullable String model,
                                              @Nullable String hardware) {
        RegisterEntity registerEntity = new RegisterEntity(deviceUUID, sn);
        Log.d(TAG, "register");
        return getIqiyiApiService().register(registerEntity);
    }

    /**
     * 导航接口
     *
     * @return 导航列表
     */
    public static Observable<String> navlist() {
        return getIqiyiApiService().navlist();
    }

    /**
     * 推荐接口
     *
     * @param ppuid      注册用户ID
     * @param chns       可传递电视剧，电影，综艺，少儿，动漫五个值的组合，英文逗号分隔，不传取全部数据
     * @param ispurchase 付费方式，若无此参数，则返回所有付费和非付费的 0：免费,2：付费已划价(判断是否会员视频，直接判断is_purchas
     * @return 获取各频道及轮播图推荐数据
     */
    public static Observable<String> recommend(@Nullable String ppuid, @Nullable String chns, @Nullable int ispurchase) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        if (ppuid != null && !ppuid.isEmpty()) {
            params.put("ppuid", ppuid);
        }
        if (chns != null && !chns.isEmpty()) {
            params.put("chns", chns);
        }
        if (ispurchase == 0 || ispurchase == 2) {
            params.put("ispurchase", ispurchase);
        }
        return getIqiyiApiService().recommend(params);
    }

    /**
     * 节目搜索
     *
     * @param key         节目搜索关键字
     * @param ispurchase  付费方式，若无此参数，则返回所有付费和非付费(0：免费 2：付费已划价(判断是否会员视频，直接判断is_purchase=2 即可)
     * @param ondemand    是否付费点播,若无此参数，则默认返回付费点播和非付费点播结果 (0：不要付费点播的结果,1： 仅要付费点播的结果,不传：要付费点播和非付费点播的结果)
     * @param pageNum     查询页码，1代表第一页
     * @param pageSize    每页数据量，最大60条
     * @param channelName 频道名称，支持多频道搜索，多频道请以英文逗号分隔
     * @param mode        排序方式 1:相关性； 4:最新；11:最热 （非纪录片频道）; 10:最热 （纪录片频道）
     * @return 获取各频道及轮播图推荐数据
     */
    public static Observable<String> search(String key, @Nullable int ispurchase,
                                            @Nullable int ondemand, int pageNum, int pageSize,
                                            @Nullable String channelName, int mode) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("key", key);
        if (ispurchase ==0 || ispurchase == 2) {
            params.put("ispurchase", ispurchase);
        }
        if (ondemand == 0 || ondemand == 1) {
            params.put("ondemand", ondemand);
        }
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        if (channelName != null && !channelName.isEmpty()) {
            params.put("channelName", channelName);
        }
        if (mode == 1 || mode == 4 || mode == 11 || mode == 10) {
            params.put("mode", mode);
        }
        return getIqiyiApiService().search(params);
    }

    /**
     * 获取频道频道详情
     *
     * @param channelName     频道名称
     * @param mode            排序类型 1 按照相关性排序, 2 视频创建时间,4 最新更新时间排序,5 vv,8 评分,9 历史点击量排序,10 周点击排序,11 昨日点击排序 默认11
     * @param threeCategoryId 多个三级分类以逗号隔开
     * @param pageNum         查询页码，1代表第一页
     * @param pageSize        每页数据量，最大60条
     * @param ispurchase      付费方式 0：免费,2：付费已划价
     * @param ondemand        是否付费点播   1: 仅要付费点播结果 0: 不要付费点播 结果说明：目前没有接奇谱付费点播的片子，此参数无效
     * @param requireTags     0：不需要TAG 1：需要TAG;默认不需要
     * @return 查询频道标签列表及标签下的数据列表
     */
    public static Observable<String> getChannelInfo(String channelName, @Nullable String mode, @Nullable String threeCategoryId,
                                                    int pageNum, int pageSize, @Nullable int ispurchase, @Nullable int ondemand,
                                                    @Nullable String requireTags) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("channelName", channelName);
        if (mode != null && !mode.isEmpty()) {
            params.put("mode", mode);
        }
        if (threeCategoryId != null && !threeCategoryId.isEmpty()) {
            params.put("threeCategoryId", threeCategoryId);
        }
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        if (ispurchase >= 0) {
            params.put("ispurchase", ispurchase);
        }
        if (ondemand >= 0) {
            params.put("ondemand", ondemand);
        }
        if (requireTags != null && !requireTags.isEmpty()) {
            params.put("requireTags", requireTags);
        }
        return getIqiyiApiService().channel(params);
    }

    /**
     * 播放鉴权
     *
     * @param qipuId 播放视频的奇谱的id
     * @return 视频播放前，需要先做鉴权，鉴权通过，才能播放
     */
    public static Observable<String> playCheck(long qipuId) {
        return getIqiyiApiService().playCheck(qipuId);
    }

    /**
     * 获取剧集列表
     *
     * @param qipuId 专辑ID：01或者08结尾
     * @param year   来源类专辑的年份，格式如：2018
     * @param pos    游标开始位置，每次请求后返回，初始传0。如果传递非标准参数，会默认为0。
     * @param num    每页显示条目数，最大60条，超过60，只返回前60条。如果传递非标准参数，会默认为60。
     * @return 分页获取正片剧集列表
     */
    public static Observable<String> getEpisodeList(long qipuId, @Nullable int year, int pos, int num) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        if (year > 0) {
            params.put("year", year);
        }
        params.put("pos", pos);
        params.put("num", num);
        return getIqiyiApiService().episodeList(qipuId, params);
    }

    /**
     * 专辑/节目详情接口
     *
     * @param qipuId 专辑ID或视频ID
     * @return 获取专辑/节目详情信息, 返回导演、演员、主持人、嘉宾等人物头像
     */
    public static Observable<String> getEpgInfo(long qipuId) {
        return getIqiyiApiService().epgInfo(qipuId);
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
    public static Observable<String> getRecommendList(long qipuId, int num, @Nullable String ispurchase, @Nullable String ondemand) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        if (num <= 0) {
            num = 20;
        } else if (num >= 60) {
            num = 60;
        }
        params.put("num", num);
        if (ispurchase != null && !ispurchase.isEmpty()) {
            params.put("ispurchase", ispurchase);
        }
        if (ondemand != null && !ondemand.isEmpty()) {
            params.put("ondemand", ondemand);
        }
        return getIqiyiApiService().recommendList(qipuId, params);
    }

    /**
     * 人物搜索
     *
     * @param uid      人物ID
     * @param pageNum  查询页码，1代表第一页
     * @param pageSize 每页数据量，最大60条
     * @param chnId    频道ID，多个以英文逗号分隔，标示只获取相关频道下的影片
     * @return 对接IPM60系统人物搜索结果数据
     */
    public static Observable<String> peopleSearch(String uid, int pageNum, int pageSize, int chnId) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("uid", uid);
        if (pageNum <= 0) {
            pageNum = 1;
        }
        params.put("pageNum", pageNum);
        if (pageSize >= 60) {
            pageSize = 60;
        } else if (pageSize <= 0) {
            pageSize = 1;
        }
        params.put("pageSize", pageSize);
        params.put("chnId", chnId);
        return getIqiyiApiService().peoplesearch(params);
    }

    /**
     * 资源位
     *
     * @param resourceId 资源位id
     * @param pageNum    查询页码，1代表第一页
     * @return 获取资源位节目信息
     */
    public static Observable<String> getResource(String resourceId, int pageNum) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("resourceId", resourceId);
        if (pageNum > 0) {
            params.put("pageNum", pageNum);
        }
        return getIqiyiApiService().resource(params);
    }

    /**
     * 风云榜(排行榜)
     * 风云榜接口是爱奇艺推荐团队按内容热度指数和播放指数计算出来的排行榜数据
     * 风云榜可以按频道当作推荐节目轮播图使用
     *
     * @param cid  频道ID 综合:-1,电影:1,电视剧:2,纪录片:3,动漫:4,少儿:15,综艺:6 不传为-1， 综合榜单以6个视频为1组，3个电影+2个电视剧+其他频道任意1个
     * @param cids 传多频道ID之间用“_”连接，最多批量请求5个频道，如1_2_4_15_6，同时传cids和cid参数时，只有cids多频道ID参数有效
     * @param vip  是否只要vip内容 0全部内容 1只要vip,默认0 注意：当cid不传时，如果此参数为0，没有内容返回
     * @param num  返回条目，默认20，最多60 注意：当传cids参数时，num是每个频道返回的数量且num最多是20条
     * @return 获取各频道风云榜数据
     */
    public static Observable<String> getRanklist(int cid, @Nullable String cids, int vip, int num) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        if (cid != -1) {
            params.put("cid", cid);
        }
        if (cids != null && !cids.isEmpty()) {
            params.put("cids", cids);
        }
        params.put("vip", vip);
        params.put("num", num);
        return getIqiyiApiService().ranklist(params);
    }

    /**
     * 热词搜索
     *
     * @param count 吐出的结果的数量, 默认20
     * @return 获取热搜词
     */
    public static Observable<String> getHotwords(int count) {
        return getIqiyiApiService().hotwords(count);
    }

    /**
     * 首字母推荐
     *
     * @param ppuid 注册用户ID，用于个性化推荐
     * @param key   查询关键字
     * @param count 结果数，默认返回20条
     * @return 首字母推荐数据
     */
    public static Observable<String> getSuggest(String ppuid, String key, int count) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("ppuid", ppuid);
        params.put("key", key);
        params.put("count", count);
        return getIqiyiApiService().suggest(params);
    }

    /**
     * 功能管理
     *
     * @return 返回功能信息
     */
    public static Observable<String> getFuncs() {
        return getIqiyiApiService().funcs();
    }

    /**
     * 预告片列表
     *
     * @param qipuId   节目奇普id
     * @param pageNum  查询页码，1代表第一页
     * @param pageSize 每页数据量，最大60条
     * @return 通过传节目id获取该节目所关联的预告片花，支持通过00数据获取
     */
    public static Observable<String> getPrevueList(long qipuId, int pageNum, int pageSize) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        return getIqiyiApiService().prevueList(qipuId, params);
    }

    /**
     * 预览图
     *
     * @param qipuId 节目奇谱ID
     * @return 获取剧集的预览图
     */
    public static Observable<String> getPrePic(long qipuId) {
        return getIqiyiApiService().prePic(qipuId);
    }

    /**
     * 系统时间
     *
     * @return 获取系统时间
     */
    public static Observable<String> getSystemTime() {
        return getIqiyiApiService().systime();
    }

    /**
     * 播单接口
     *
     * @param qipuId   节目奇谱ID
     * @param pageNum  查询页码，1代表第一页
     * @param pageSize 每页请求数据量，最大60条
     * @return 返回播单里的节目数据
     */
    public static Observable<String> getPlayList(long qipuId, int pageNum, int pageSize) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        return getIqiyiApiService().playlist(qipuId, params);
    }

    /**
     * 获取爱奇艺号内容列表
     * 说明： 获取爱奇艺号下的节目列表
     *
     * @param userId   爱奇艺号ID
     * @param pageNum  查询页码，1代表第一页，默认1
     * @param pageSize 每页数据量，最大60条，默认20条
     * @return 获取爱奇艺号下的节目列表
     */
    public static Observable<String> getSelfMedia(long userId, int pageNum, int pageSize) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        return getIqiyiApiService().selfMedia(userId, params);
    }

    /**
     * 人物识别接口
     *
     * @param albumId 专辑id 如果视频有所属专辑，传该字段
     * @param qipuId  视频qipuId
     * @param t       截取时间点，以毫秒为单位，将当前时间转为毫秒数，如2小时12分10秒就是(2*60*60+12*60+10)*1000
     * @param brates  视频支持的码率，多个码率之间用 英文逗号分隔。后台选取码率规则：优先600，若无600，优先取
     *                300，若300也无，取客户端传入的支持码率的第一个
     * @return ai人物识别接口，增加设备鉴权
     */
    public static Observable<String> getPerson(@Nullable long albumId, long qipuId, long t, String brates) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("albumId", albumId);
        params.put("qipuId", qipuId);
        params.put("t", t);
        params.put("brates", brates);
        return getIqiyiApiService().person(params);
    }

    /**
     * 截图接口
     *
     * @param qipuId 视频qipuId
     * @param t      截取时间点，以毫秒为单位，将当前时间转为毫秒数，如2小时12分10秒就是(2*60*60+12*60+10)*1000
     * @param brates 视频码率 例如300、600、720P，默认300码率
     * @return 实时截图服务代理接口
     */
    public static Observable<String> screenshot(long qipuId, long t, String brates) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("qipuId", qipuId);
        params.put("t", t);
        params.put("brates", brates);
        return getIqiyiApiService().screenshot(params);
    }

    /**
     * 虚拟人物识别接口
     *
     * @param qipuId 视频qipuId
     * @param t      截取时间点，以毫秒为单位，将当前时间转为毫秒数，如2小时12分10秒就是(2*60*60+12*60+10)*1000
     * @param brates 视频码率 例如300、600、720P，默认300码率
     * @return ai虚拟人物识别接口
     */
    public static Observable<String> getCartoon(long qipuId, long t, String brates) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("qipuId", qipuId);
        params.put("t", t);
        params.put("brates", brates);
        return getIqiyiApiService().cartoon(params);
    }

    /**
     * 动漫人物相关视频
     *
     * @param id         虚拟人物id
     * @param pageNum    页码 默认是1
     * @param pageSize   每页展示条数 默认是60
     * @param ispurchase 节目付费方式：0: 全部, 默认0 ;1: 只出免费;2: 只出付费
     * @return 虚拟人物关联视频
     */
    public static Observable<String> getCartoonEpisodeList(String id, int pageNum, int pageSize, int ispurchase) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("id", id);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("ispurchase", ispurchase);
        return getIqiyiApiService().cartoonEpisodeList(params);
    }
}
