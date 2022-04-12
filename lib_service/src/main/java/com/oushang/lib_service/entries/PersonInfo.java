package com.oushang.lib_service.entries;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @Author: zeelang
 * @Description: 人物信息
 * @Time: 2021/6/30 9:48
 * @Since: 1.0
 */
public class PersonInfo implements Parcelable {

    //人物ID
    private String id = "";

    //人物名称
    @SerializedName("n")
    private String name = "";

    //人物封面
    private String cover = "";

    //人物角色
    private String character = "";

    public PersonInfo(String id, String name, String cover, String character) {
        this.id = id;
        this.name = name;
        this.cover = cover;
        this.character = character;
    }

    protected PersonInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        cover = in.readString();
        character = in.readString();
    }

    public static final Creator<PersonInfo> CREATOR = new Creator<PersonInfo>() {
        @Override
        public PersonInfo createFromParcel(Parcel in) {
            return new PersonInfo(in);
        }

        @Override
        public PersonInfo[] newArray(int size) {
            return new PersonInfo[size];
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
        dest.writeString(cover);
        dest.writeString(character);
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", character='" + character + '\'' +
                '}';
    }
}
