package com.oushang.lib_service.entries;

import java.util.List;

public class BalanceInfo {
    public int status_code;
    public List<Datas> data;

    public static class Datas{
        public List<BalancesData> balances;
        public double total;
        public double left;
        public AppInfo appInfo;
        public double leftPercentage;
        public String totalUnit;
    }

    public static class BalancesData{
        public double total;
        public String unit;
        public double left;
        public String expirationTime;
        public double percentage;
        public String sku;
        public String productName;
        public String status;
    }

    public static class AppInfo{
        public String code;
        public String appImgHandler;
        public String appName;
        public String appid;
    }
}
