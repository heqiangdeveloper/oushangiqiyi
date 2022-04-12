package com.oushang.lib_service.entries;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: zeelang
 * @Description: 播放详情信息
 * @Time: 2021/12/9 0009  16:07
 * @Since: 1.0
 */
public class PlayerInfo implements Parcelable {
    //album info
    public String al_id;
    public String al_img;
    public String al_v2Img;
    public String al_shortTitle;
    public String al_sourctText;
    public String al_tag;
    public String al_title;
    public int al_totalTvs;
    public String al_year;
    public String al_desc;
    public String al_duration;
    public int al_pc;

    //video info
    public String tv_desc;
    public String tv_duration;
    public String tv_id;
    public String tv_img;
    public String tv_title;
    public String tv_subTitle;
    public int tv_order;

    private PlayerInfo(Builder builder) {
        this.al_id = builder.al_id;
        this.al_img = builder.al_img;
        this.al_v2Img = builder.al_v2Img;
        this.al_shortTitle = builder.al_shortTitle;
        this.al_sourctText = builder.al_sourctText;
        this.al_tag = builder.al_tag;
        this.al_title = builder.al_title;
        this.al_totalTvs = builder.al_totalTvs;
        this.al_year = builder.al_year;
        this.al_desc = builder.al_desc;
        this.al_duration = builder.al_duration;
        this.al_pc = builder.al_pc;

        this.tv_desc = builder.tv_desc;
        this.tv_duration = builder.tv_duration;
        this.tv_id = builder.tv_id;
        this.tv_img = builder.tv_img;
        this.tv_title = builder.tv_title;
        this.tv_subTitle = builder.tv_subTitle;
        this.tv_order = builder.tv_order;
    }

    protected PlayerInfo(Parcel in) {
        al_id = in.readString();
        al_img = in.readString();
        al_v2Img = in.readString();
        al_shortTitle = in.readString();
        al_sourctText = in.readString();
        al_tag = in.readString();
        al_title = in.readString();
        al_totalTvs = in.readInt();
        al_year = in.readString();
        al_desc = in.readString();
        al_duration = in.readString();
        al_pc = in.readInt();
        tv_desc = in.readString();
        tv_duration = in.readString();
        tv_id = in.readString();
        tv_img = in.readString();
        tv_title = in.readString();
        tv_subTitle = in.readString();
        tv_order = in.readInt();
    }

    public static final Creator<PlayerInfo> CREATOR = new Creator<PlayerInfo>() {
        @Override
        public PlayerInfo createFromParcel(Parcel in) {
            return new PlayerInfo(in);
        }

        @Override
        public PlayerInfo[] newArray(int size) {
            return new PlayerInfo[size];
        }
    };

    public String getAl_id() {
        return al_id;
    }

    public String getAl_img() {
        return al_img;
    }

    public String getAl_v2Img() {
        return al_v2Img;
    }

    public String getAl_shortTitle() {
        return al_shortTitle;
    }

    public String getAl_sourctText() {
        return al_sourctText;
    }

    public String getAl_tag() {
        return al_tag;
    }

    public String getAl_title() {
        return al_title;
    }

    public int getAl_totalTvs() {
        return al_totalTvs;
    }

    public String getAl_year() {
        return al_year;
    }

    public String getAl_desc() {
        return al_desc;
    }

    public String getAl_duration() {
        return al_duration;
    }

    public int getAl_pc() {
        return al_pc;
    }

    public String getTv_desc() {
        return tv_desc;
    }

    public String getTv_duration() {
        return tv_duration;
    }

    public String getTv_id() {
        return tv_id;
    }

    public String getTv_img() {
        return tv_img;
    }

    public String getTv_title() {
        return tv_title;
    }

    public String getTv_subTitle() {
        return tv_subTitle;
    }

    public int getTv_order() {
        return tv_order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(al_id);
        dest.writeString(al_img);
        dest.writeString(al_v2Img);
        dest.writeString(al_shortTitle);
        dest.writeString(al_sourctText);
        dest.writeString(al_tag);
        dest.writeString(al_title);
        dest.writeInt(al_totalTvs);
        dest.writeString(al_year);
        dest.writeString(al_desc);
        dest.writeString(al_duration);
        dest.writeInt(al_pc);
        dest.writeString(tv_desc);
        dest.writeString(tv_duration);
        dest.writeString(tv_id);
        dest.writeString(tv_img);
        dest.writeString(tv_title);
        dest.writeString(tv_subTitle);
        dest.writeInt(tv_order);
    }

    public static class Builder {
        //album info
        private String al_id;
        private String al_img;
        private String al_v2Img;
        private String al_shortTitle;
        private String al_sourctText;
        private String al_tag;
        private String al_title;
        private int al_totalTvs;
        private String al_year;
        private String al_desc;
        private String al_duration;
        private int al_pc;

        //video info
        public String tv_desc;
        public String tv_duration;
        public String tv_id;
        public String tv_img;
        public String tv_title;
        public String tv_subTitle;
        public int tv_order;

        public Builder setAl_id(String al_id) {
            this.al_id = al_id;
            return this;
        }

        public Builder setAl_img(String al_img) {
            this.al_img = al_img;
            return this;
        }

        public Builder setAl_v2Img(String al_v2Img) {
            this.al_v2Img = al_v2Img;
            return this;
        }

        public Builder setAl_shortTitle(String al_shortTitle) {
            this.al_shortTitle = al_shortTitle;
            return this;
        }

        public Builder setAl_sourctText(String al_sourctText) {
            this.al_sourctText = al_sourctText;
            return this;
        }

        public Builder setAl_tag(String al_tag) {
            this.al_tag = al_tag;
            return this;
        }

        public Builder setAl_title(String al_title) {
            this.al_title = al_title;
            return this;
        }

        public Builder setAl_totalTvs(int al_totalTvs) {
            this.al_totalTvs = al_totalTvs;
            return this;
        }

        public Builder setAl_year(String al_year) {
            this.al_year = al_year;
            return this;
        }

        public Builder setAl_desc(String al_desc) {
            this.al_desc = al_desc;
            return this;
        }

        public Builder setAl_duration(String al_duration) {
            this.al_duration = al_duration;
            return this;
        }

        public Builder setAl_pc(int al_pc) {
            this.al_pc = al_pc;
            return this;
        }

        public Builder setTv_desc(String tv_desc) {
            this.tv_desc = tv_desc;
            return this;
        }

        public Builder setTv_duration(String tv_duration) {
            this.tv_duration = tv_duration;
            return this;
        }

        public Builder setTv_id(String tv_id) {
            this.tv_id = tv_id;
            return this;
        }

        public Builder setTv_img(String tv_img) {
            this.tv_img = tv_img;
            return this;
        }

        public Builder setTv_title(String tv_title) {
            this.tv_title = tv_title;
            return this;
        }

        public Builder setTv_subTitle(String tv_subTitle) {
            this.tv_subTitle = tv_subTitle;
            return this;
        }

        public Builder setTv_order(int tv_order) {
            this.tv_order = tv_order;
            return this;
        }

        public PlayerInfo build() {
            return new PlayerInfo(this);
        }
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "al_id='" + al_id + '\'' +
                ", al_title='" + al_title + '\'' +
                ", al_duration='" + al_duration + '\'' +
                ", tv_desc='" + tv_desc + '\'' +
                ", tv_duration='" + tv_duration + '\'' +
                ", tv_id='" + tv_id + '\'' +
                ", tv_title='" + tv_title + '\'' +
                ", tv_subTitle='" + tv_subTitle + '\'' +
                ", tv_order=" + tv_order +
                '}';
    }
}
