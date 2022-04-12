package com.oushang.iqiyi.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * @Author: zeelang
 * @Description: 车联网接口
 * @Time: 2022/2/22 16:36
 * @Since: 1.0
 */
public interface VehicleService {
    String BASE_URL = "https://incallapi.changan.com.cn";

    /**
     * 获取车机流量信息
     * @param token     车机的设备访问令牌
     * @param timestamp 当前 UNIX 时间戳，精确到毫秒
     * @param nonce     随机数，4位，格式由0-9A-Za-z组成 例：7va3
     * @return 车机流量信息
     */
    @GET("/hu-apigw/huservice/api/v1/store/balance-info")
    Observable<BalanceInfo> getBalanceInfo(@Header("X-VCS-Hu-Token") String token,
                                      @Header("X-VCS-Timestamp") String timestamp,
                                      @Header("X-VCS-Nonce") String nonce);

}
