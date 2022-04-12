// IqiyiBanner.aidl
package com.oushang.iqiyi;
import com.oushang.iqiyi.IqiyiBannerCallback;

interface IqiyiBanner {

    void requestBannerData();

    boolean hasPausePlayVideo();

    boolean hasRecentPlayVideo();

    void registerCallback(IqiyiBannerCallback callback);

    void unRegisterCallback(IqiyiBannerCallback callback);

}