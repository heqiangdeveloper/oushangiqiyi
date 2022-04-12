// IqiyiBannerCallback.aidl
package com.oushang.iqiyi;
import com.oushang.iqiyi.entries.BannerEntry;

interface IqiyiBannerCallback {

    void onBannerCallBack(inout List<BannerEntry> banners);

}