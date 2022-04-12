package com.oushang.lib_service.interfaces;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.oushang.lib_service.entries.ChannelInfo;
import com.oushang.lib_service.entries.ChannelTag;

import java.util.List;

import io.reactivex.Observable;


public interface ChannelManager extends IProvider {

    /**
     * 获取所有的频道标签
     * @return 标签列表
     */
    Observable<List<ChannelTag>> getAllChannelTags();

    /**
     * 获取频道详情数据
     * @param channelName  频道名称
     * @param mode 排序类型
     * @param threeCategoryId 多个三级分类以逗号隔开
     * @param pageNum 查询页码，1代表第一页
     * @param pageSize 每页数据量，最大60条
     * @param ispurchase 付费方式：• 0: 免费 • 2: 付费已划价
     * @param ondemand  是否付费点播
     * @param isRequireTags 0：不需要TAG 1：需要TAG;默认不需要
     * @return
     */
    Observable<ChannelInfo> getChannelInfos(String channelName, String mode, String threeCategoryId,
                                            int pageNum, int pageSize, int ispurchase, int ondemand, boolean isRequireTags);











}
