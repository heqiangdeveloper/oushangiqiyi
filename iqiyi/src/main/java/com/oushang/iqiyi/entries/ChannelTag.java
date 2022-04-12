package com.oushang.iqiyi.entries;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oushang.iqiyi.ui.ISpanItem;

/**
 * @Author: zeelang
 * @Description: 频道标签
 * @Time: 2021/7/16 15:29
 * @Since: 1.0
 */
public class ChannelTag implements ISpanItem, Parcelable {

    //频道id
    private int channelId;

    //频道名称
    private String channelName;

    //是否启用编辑
    private boolean isEditEnable = false;

    //是否是常用频道
    private boolean isCommonChannel = false;

    //编辑状态，只有启用编辑才可以设置编辑状态
    private int editState = NONE_STATE;

    public static final int NONE_STATE = -1;
    public static final int DELETE_STATE = 0;
    public static final int ADD_STATE = 1;
    public static final int COMPLETE_STATE = 2;

    private EditEnableChangeListener editEnableChangeListener;

    private EditStateChangeListener editStateChangeListener;

    public ChannelTag() {
        
    }

    public ChannelTag(int channelId, String channelName) {
        this.channelId = channelId;
        this.channelName = channelName;
    }

    protected ChannelTag(Parcel in) {
        channelId = in.readInt();
        channelName = in.readString();
        isEditEnable = in.readByte() != 0;
        isCommonChannel = in.readByte() != 0;
        editState = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(channelId);
        dest.writeString(channelName);
        dest.writeByte((byte) (isEditEnable ? 1 : 0));
        dest.writeByte((byte) (isCommonChannel ? 1 : 0));
        dest.writeInt(editState);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChannelTag> CREATOR = new Creator<ChannelTag>() {
        @Override
        public ChannelTag createFromParcel(Parcel in) {
            return new ChannelTag(in);
        }

        @Override
        public ChannelTag[] newArray(int size) {
            return new ChannelTag[size];
        }
    };

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isEditEnable() {
        return isEditEnable;
    }

    public void setEditEnable(boolean editEnable) {
        if (isEditEnable != editEnable) {
            isEditEnable = editEnable;
            if (editEnableChangeListener != null) {
                editEnableChangeListener.onChange(isEditEnable);
            }
        }
    }

    public boolean isCommonChannel() {
        return isCommonChannel;
    }

    public void setCommonChannel(boolean commonChannel) {
        isCommonChannel = commonChannel;
    }

    public int getEditState() {
        return editState;
    }

    public void setEditState(int editState) {
        if (isEditEnable && this.editState != editState) {
            this.editState = editState;
            if (editStateChangeListener != null) {
                editStateChangeListener.onChange(editState);
            }
        }
    }

    public void setEditEnableChangeListener(EditEnableChangeListener changeListener) {
        this.editEnableChangeListener = changeListener;
    }

    public void setEditStateChangeListener(EditStateChangeListener changeListener) {
        this.editStateChangeListener = changeListener;
    }

    public interface EditEnableChangeListener {
        void onChange(boolean editable);
    }

    public interface EditStateChangeListener {
        void onChange(int state);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ChannelTag)) {
            return false;
        }
        return this.getChannelId() == ((ChannelTag) obj).getChannelId() &&
                (this.getChannelName().equals(((ChannelTag) obj).getChannelName()));
    }

    @Override
    public int hashCode() {
        return channelName.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return "[channelName:" + channelName + ",isEditEnable:" + isEditEnable
                + ",isCommon:" + isCommonChannel + ",editState:" + editState + "]";
    }

    @Override
    public int getSpanSize() {
        return 1;
    }

    @Override
    public int getViewType() {
        return MutiType.TEXT;
    }
}
