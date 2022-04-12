package com.oushang.iqiyi.entries;

import android.os.Parcel;
import android.os.Parcelable;

import com.oushang.lib_service.entries.VideoInfo;

/**
 * @Author: zeelang
 * @Description: 选集实体类
 * @Time: 2021/8/25 15:35
 * @Since: 1.0
 */
public class SelectionEntry implements Parcelable {

    private long qipuId; //视频id

    private int order; //当前集数

    private VideoInfo videoInfo; //视频信息

    private boolean isSelected = false; //是否选中

    public SelectionEntry(long qipuId, int order, boolean isSelected) {
        this.qipuId = qipuId;
        this.order = order;
        this.isSelected = isSelected;
    }

    public SelectionEntry(VideoInfo videoInfo, boolean isSelected) {
        this.videoInfo = videoInfo;
        this.isSelected = isSelected;
        this.qipuId = videoInfo.getQipuId();
        this.order = videoInfo.getOrder();
    }

    public SelectionEntry() {
    }

    protected SelectionEntry(Parcel in) {
        qipuId = in.readLong();
        order = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(qipuId);
        dest.writeInt(order);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectionEntry> CREATOR = new Creator<SelectionEntry>() {
        @Override
        public SelectionEntry createFromParcel(Parcel in) {
            return new SelectionEntry(in);
        }

        @Override
        public SelectionEntry[] newArray(int size) {
            return new SelectionEntry[size];
        }
    };

    public long getQipuId() {
        return qipuId;
    }

    public void setQipuId(long qipuId) {
        this.qipuId = qipuId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "SelectionEntry{" +
                "qipuId=" + qipuId +
                ", order=" + order +
                ", isSelected=" + isSelected +
                '}';
    }
}
