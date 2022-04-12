package com.oushang.lib_service.entries;


/**
 * @Author: DELL
 * @Description: 专辑信息
 * @Time: 2021/6/28 20:18
 * @Since:
 */
@Deprecated
public class AlbumInfo {

    //所属频道Id
    private int chnId;

    //所属频道名称
    private String chnName;

    //专辑奇谱ID，01或08结尾
    private long qipuId;

    //默认视频节点
    private DefaultEpisode defaultEpi;

    //专辑名称
    private String name;

    //一句话看点
    private String focus;

    //封面图
    private String albumPic;

    //海报图
    private String posterPic;

    //用户评分
    private String score;

    //是否多集，1是多集剧，其它表示不是多集剧
    private int isSeries;

    //短标题
    private String shortName;

    //是否独播,1 是独播，其它表示不是独播
    private int isExclusive;

    //来源code sourceCode大于0是来源专辑，非来源专辑sourceCode 为0
    private long sourceCode;

    //视频的发布日期，格式：20160711
    private String publishTime;

    //总集数
    private long total;

    //（专辑/来源）总视频数或（视频）当前集数
    private int count;

    private VipInfo vipInfo;

    //0-普通会员，1-网球会员，多个用逗号分隔
    private String vipType;

    //奇艺首次上线时间（格式：2016-07-11 21:56:07）
    private String initIssueTime;

    //专辑描述
    private String desc;

    //标签列表（Type_音乐,Place_内地，客户端只要汉字名可按照最后一个_分隔）
    private String tag;

    //全体演员
    private CastInfo cast;

    //0 不是 1是
    private int qiyiProd;

    public AlbumInfo() {
    }

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

    public DefaultEpisode getDefaultEpi() {
        return defaultEpi;
    }

    public void setDefaultEpi(DefaultEpisode defaultEpi) {
        this.defaultEpi = defaultEpi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }

    public String getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(String albumPic) {
        this.albumPic = albumPic;
    }

    public String getPosterPic() {
        return posterPic;
    }

    public void setPosterPic(String posterPic) {
        this.posterPic = posterPic;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getIsSeries() {
        return isSeries;
    }

    public void setIsSeries(int isSeries) {
        this.isSeries = isSeries;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getIsExclusive() {
        return isExclusive;
    }

    public void setIsExclusive(int isExclusive) {
        this.isExclusive = isExclusive;
    }

    public long getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(long sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public VipInfo getVipInfo() {
        return vipInfo;
    }

    public void setVipInfo(VipInfo vipInfo) {
        this.vipInfo = vipInfo;
    }

    public String getVipType() {
        return vipType;
    }

    public void setVipType(String vipType) {
        this.vipType = vipType;
    }

    public String getInitIssueTime() {
        return initIssueTime;
    }

    public void setInitIssueTime(String initIssueTime) {
        this.initIssueTime = initIssueTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public CastInfo getCast() {
        return cast;
    }

    public void setCast(CastInfo cast) {
        this.cast = cast;
    }

    public int getQiyiProd() {
        return qiyiProd;
    }

    public void setQiyiProd(int qiyiProd) {
        this.qiyiProd = qiyiProd;
    }
}
