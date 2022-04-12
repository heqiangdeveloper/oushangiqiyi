package com.oushang.lib_service.entries;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: zeelang
 * @Description: vip信息
 * @Date: 2021/6/24
 */
public class VipInfo implements Parcelable {
    public static final int VIP = 1;

    //付费角标url
    private String payMarkUrl;

    //是否点播:1 是，其它情况为非点播
    private int isTvod;

    //角标：VIP_MARK /COUPONS_ON_DEMAND_MARK/
    //PAY_ON_DEMAND_MARK/ NONE_MARK
    private String payMark;

    //点播有效期，取值：24h，30d等等。h表示小时，d表示天，M表示月
    //，24h表示24小时内有效
    private String validTime;

    //点播结算价，单位：分，如500表示500分，即：5元
    private int sttlPrc;

    //点播原价，单位：分，如500表示500分，即：5元
    private int orgPrc;

    //是否会员包月:1 是，其它情况为非包月
    private int isVip;

    //是否点播券:1 是，其它情况为非点播券
    private int isCoupon;

    //是否套餐:1 是，其它情况为非套餐
    private int isPkg;

    public VipInfo() {

    }

    public String getPayMarkUrl() {
        return payMarkUrl;
    }

    public void setPayMarkUrl(String payMarkUrl) {
        this.payMarkUrl = payMarkUrl;
    }

    public int getIsTvod() {
        return isTvod;
    }

    public void setIsTvod(int isTvod) {
        this.isTvod = isTvod;
    }

    public String getPayMark() {
        return payMark;
    }

    public void setPayMark(String payMark) {
        this.payMark = payMark;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public int getSttlPrc() {
        return sttlPrc;
    }

    public void setSttlPrc(int sttlPrc) {
        this.sttlPrc = sttlPrc;
    }

    public int getOrgPrc() {
        return orgPrc;
    }

    public void setOrgPrc(int orgPrc) {
        this.orgPrc = orgPrc;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    public int getIsCoupon() {
        return isCoupon;
    }

    public void setIsCoupon(int isCoupon) {
        this.isCoupon = isCoupon;
    }

    public int getIsPkg() {
        return isPkg;
    }

    public void setIsPkg(int isPkg) {
        this.isPkg = isPkg;
    }

    protected VipInfo(Parcel in) {
        payMarkUrl = in.readString();
        isTvod = in.readInt();
        payMark = in.readString();
        validTime = in.readString();
        sttlPrc = in.readInt();
        orgPrc = in.readInt();
        isVip = in.readInt();
        isCoupon = in.readInt();
        isPkg = in.readInt();
    }

    public static final Creator<VipInfo> CREATOR = new Creator<VipInfo>() {
        @Override
        public VipInfo createFromParcel(Parcel in) {
            return new VipInfo(in);
        }

        @Override
        public VipInfo[] newArray(int size) {
            return new VipInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(payMarkUrl);
        dest.writeInt(isTvod);
        dest.writeString(payMark);
        dest.writeString(validTime);
        dest.writeInt(sttlPrc);
        dest.writeInt(orgPrc);
        dest.writeInt(isVip);
        dest.writeInt(isCoupon);
        dest.writeInt(isPkg);
    }

    @Override
    public String toString() {
        return "VipInfo{" +
                "payMarkUrl='" + payMarkUrl + '\'' +
                ", isTvod=" + isTvod +
                ", payMark='" + payMark + '\'' +
                ", validTime='" + validTime + '\'' +
                ", sttlPrc=" + sttlPrc +
                ", orgPrc=" + orgPrc +
                ", isVip=" + isVip +
                ", isCoupon=" + isCoupon +
                ", isPkg=" + isPkg +
                '}';
    }
}
