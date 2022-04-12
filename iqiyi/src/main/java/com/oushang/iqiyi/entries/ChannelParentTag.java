package com.oushang.iqiyi.entries;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.oushang.lib_service.entries.ChannelTag;

import java.util.Objects;

/**
 * @Author: Administrator
 * @Description: 分类一级标签
 * @Time: 2021/12/18 0018  11:08
 * @Since: 1.0
 */
@Entity(tableName = "channelParentTag")
public class ChannelParentTag implements Cloneable, Parcelable {

    //频道标签
    @PrimaryKey
    @androidx.annotation.NonNull
    @ColumnInfo(name = "id")
    private String id = "";

    @androidx.annotation.NonNull
    @ColumnInfo(name = "channelTag")
    private ChannelTag channelTag;

    //是否启用编辑，默认不启用编辑
    @ColumnInfo(name = "isEditEnable")
    private boolean isEditEnable = false;

    //是否是常用频道，默认非常用频道
    @ColumnInfo(name = "isCommonChannel")
    private boolean isCommonChannel = false;

    //编辑状态，只有启用编辑才可以设置编辑状态，默认无状态
    @ColumnInfo(name = "editState")
    private int editState = NONE_STATE;

    //是否删除（数据库删除） 0 未删除 ，1 删除
    @ColumnInfo(name = "isDelete")
    private int isDelete = 0;

    public static final int NONE_STATE = -1; //无状态
    public static final int DELETE_STATE = 0; //删除状态
    public static final int ADD_STATE = 1; //添加状态
    public static final int COMPLETE_STATE = 2; //完成状态

    //启用编辑监听
    @Ignore
    private EditEnableChangeListener editEnableChangeListener;

    //编辑状态监听
    @Ignore
    private EditStateChangeListener editStateChangeListener;

    public ChannelParentTag(ChannelTag channelTag) {
        this.channelTag = channelTag;
        this.id = channelTag.getId();
    }

    protected ChannelParentTag(Parcel in) {
        channelTag = in.readParcelable(ChannelTag.class.getClassLoader());
        isEditEnable = in.readByte() != 0;
        isCommonChannel = in.readByte() != 0;
        editState = in.readInt();
        isDelete = in.readInt();
    }

    public static final Creator<ChannelParentTag> CREATOR = new Creator<ChannelParentTag>() {
        @Override
        public ChannelParentTag createFromParcel(Parcel in) {
            return new ChannelParentTag(in);
        }

        @Override
        public ChannelParentTag[] newArray(int size) {
            return new ChannelParentTag[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取频道标签信息
     *
     * @return 频道标签信息
     */
    public ChannelTag getChannelTag() {
        return channelTag;
    }

    /**
     * 设置频道标签信息（分类一级标签）
     *
     * @param channelTag 频道标签信息
     */
    public void setChannelTag(ChannelTag channelTag) {
        this.channelTag = channelTag;
    }

    /**
     * 是否启用编辑
     *
     * @return 启用 true, 不启用 false
     */
    public boolean isEditEnable() {
        return isEditEnable;
    }

    /**
     * 设置是否启用编辑
     *
     * @param editEnable true 启用，false 不启用
     */
    public void setEditEnable(boolean editEnable) {
        isEditEnable = editEnable;
    }

    /**
     * 是否是常用频道
     *
     * @return true 是，false 否
     */
    public boolean isCommonChannel() {
        return isCommonChannel;
    }

    /**
     * 设置是否是常用频道
     *
     * @param commonChannel true 是，false 否
     */
    public void setCommonChannel(boolean commonChannel) {
        isCommonChannel = commonChannel;
    }

    /**
     * 获取编辑状态
     *
     * @return 编辑状态
     */
    public int getEditState() {
        return editState;
    }

    /**
     * 设置编辑状态
     *
     * @param editState 编辑状态
     */
    public void setEditState(int editState) {
        this.editState = editState;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * 是否删除
     *
     * @return true 是， false 否
     */
    public boolean isDelete() {
        return isDelete == 1;
    }

    /**
     * 设置是否删除
     *
     * @param delete 删除
     */
    public void setDelete(boolean delete) {
        isDelete = delete ? 1 : 0;
    }

    /**
     * 设置启用编辑监听
     *
     * @param editEnableChangeListener
     */
    public void setEditEnableChangeListener(EditEnableChangeListener editEnableChangeListener) {
        this.editEnableChangeListener = editEnableChangeListener;
    }

    /**
     * 设置编辑状态监听
     *
     * @param editStateChangeListener
     */
    public void setEditStateChangeListener(EditStateChangeListener editStateChangeListener) {
        this.editStateChangeListener = editStateChangeListener;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(channelTag, flags);
        dest.writeByte((byte) (isEditEnable ? 1 : 0));
        dest.writeByte((byte) (isCommonChannel ? 1 : 0));
        dest.writeInt(editState);
        dest.writeInt(isDelete);
    }

    /**
     * 启用编辑监听
     */
    public interface EditEnableChangeListener {
        void onChange(boolean editable);
    }

    /**
     * 编辑状态监听
     */
    public interface EditStateChangeListener {
        void onChange(int state);
    }

    //是否相同
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelParentTag that = (ChannelParentTag) o;
        return channelTag.equals(that.channelTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelTag);
    }

    @NonNull
    @Override
    public Object clone(){
        ChannelParentTag tag = null;
        try{
            tag = (ChannelParentTag) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return tag;
    }

    @Override
    public String toString() {
        return "ChannelParentTag{" +
                "channelTag=" + channelTag +
                ", isEditEnable=" + isEditEnable +
                ", isCommonChannel=" + isCommonChannel +
                ", editState=" + editState +
                '}';
    }
}
