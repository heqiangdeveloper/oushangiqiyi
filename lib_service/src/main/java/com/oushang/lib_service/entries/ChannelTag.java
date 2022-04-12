package com.oushang.lib_service.entries;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: zeelang
 * @Description: 频道信息
 * @Time: 2021/6/28 19:31
 * @Since: 1.0
 */
public class ChannelTag implements Parcelable {

    //频道/标签id
    private String id;

    //频道/标签名称
    private String name;

    //别名
    private String alias;

    //是否选中
    private boolean isSelected = false;

    //子标签列表
    private List<ChannelTag> children;

    public ChannelTag(String id, String name) {
        this(id, name, null);
    }

    public ChannelTag(String id, String name, List<ChannelTag> children) {
        this.id = id;
        this.name = name;
        this.alias = name;
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<ChannelTag> getChildren() {
        return children;
    }

    public void setChildren(List<ChannelTag> children) {
        this.children = children;
    }

    public void addChildTag(ChannelTag channelTag) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(channelTag);
    }


    public ChannelTag(Parcel in) {
        id = in.readString();
        name = in.readString();
        alias = in.readString();
        children = in.createTypedArrayList(CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(alias);
        dest.writeTypedList(children);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelTag tag = (ChannelTag) o;
        return id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "channelTag{" + "id=" + id + ",name=" + name + ",alias=" + alias + ",children:" + children + "}";
    }
}
