package com.oushang.lib_service.entries;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @Author: zeelang
 * @Description: 演员信息
 * @Time: 2021/6/30 9:52
 * @Since: 1.0
 */
public class CastInfo implements Parcelable {

    //演员
    private List<PersonInfo> actor;

    //编曲
    private List<PersonInfo> songWriter;

    //明星
    private List<PersonInfo> star;

    //导演
    private List<PersonInfo> director;

    //作曲家
    private List<PersonInfo> composer;

    //主演
    private List<PersonInfo> mainActor;

    //主持
    private List<PersonInfo> host;

    //制片
    private List<PersonInfo> producer;

    //制作人
    private List<PersonInfo> maker;

    //嘉宾
    private List<PersonInfo> guest;

    //编剧
    private List<PersonInfo> writer;

    //配音演员
    private List<PersonInfo> dubber;

    protected CastInfo(Parcel in) {
        actor = in.createTypedArrayList(PersonInfo.CREATOR);
        songWriter = in.createTypedArrayList(PersonInfo.CREATOR);
        star = in.createTypedArrayList(PersonInfo.CREATOR);
        director = in.createTypedArrayList(PersonInfo.CREATOR);
        composer = in.createTypedArrayList(PersonInfo.CREATOR);
        mainActor = in.createTypedArrayList(PersonInfo.CREATOR);
        host = in.createTypedArrayList(PersonInfo.CREATOR);
        producer = in.createTypedArrayList(PersonInfo.CREATOR);
        maker = in.createTypedArrayList(PersonInfo.CREATOR);
        guest = in.createTypedArrayList(PersonInfo.CREATOR);
        writer = in.createTypedArrayList(PersonInfo.CREATOR);
        dubber = in.createTypedArrayList(PersonInfo.CREATOR);
    }

    public static final Creator<CastInfo> CREATOR = new Creator<CastInfo>() {
        @Override
        public CastInfo createFromParcel(Parcel in) {
            return new CastInfo(in);
        }

        @Override
        public CastInfo[] newArray(int size) {
            return new CastInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(actor);
        dest.writeTypedList(songWriter);
        dest.writeTypedList(star);
        dest.writeTypedList(director);
        dest.writeTypedList(composer);
        dest.writeTypedList(mainActor);
        dest.writeTypedList(host);
        dest.writeTypedList(producer);
        dest.writeTypedList(maker);
        dest.writeTypedList(guest);
        dest.writeTypedList(writer);
        dest.writeTypedList(dubber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<PersonInfo> getActor() {
        return actor;
    }

    public void setActor(List<PersonInfo> actor) {
        this.actor = actor;
    }

    public List<PersonInfo> getSongWriter() {
        return songWriter;
    }

    public void setSongWriter(List<PersonInfo> songWriter) {
        this.songWriter = songWriter;
    }

    public List<PersonInfo> getStar() {
        return star;
    }

    public void setStar(List<PersonInfo> star) {
        this.star = star;
    }

    public List<PersonInfo> getDirector() {
        return director;
    }

    public void setDirector(List<PersonInfo> director) {
        this.director = director;
    }

    public List<PersonInfo> getComposer() {
        return composer;
    }

    public void setComposer(List<PersonInfo> composer) {
        this.composer = composer;
    }

    public List<PersonInfo> getMainActor() {
        return mainActor;
    }

    public void setMainActor(List<PersonInfo> mainActor) {
        this.mainActor = mainActor;
    }

    public List<PersonInfo> getHost() {
        return host;
    }

    public void setHost(List<PersonInfo> host) {
        this.host = host;
    }

    public List<PersonInfo> getProducer() {
        return producer;
    }

    public void setProducer(List<PersonInfo> producer) {
        this.producer = producer;
    }

    public List<PersonInfo> getMaker() {
        return maker;
    }

    public void setMaker(List<PersonInfo> maker) {
        this.maker = maker;
    }

    public List<PersonInfo> getGuest() {
        return guest;
    }

    public void setGuest(List<PersonInfo> guest) {
        this.guest = guest;
    }

    public List<PersonInfo> getWriter() {
        return writer;
    }

    public void setWriter(List<PersonInfo> writer) {
        this.writer = writer;
    }

    public List<PersonInfo> getDubber() {
        return dubber;
    }

    public void setDubber(List<PersonInfo> dubber) {
        this.dubber = dubber;
    }

    @Override
    public String toString() {
        return "CastInfo{" +
                "actor=" + actor +
                ", songWriter=" + songWriter +
                ", star=" + star +
                ", director=" + director +
                ", composer=" + composer +
                ", mainActor=" + mainActor +
                ", host=" + host +
                ", producer=" + producer +
                ", maker=" + maker +
                ", guest=" + guest +
                ", writer=" + writer +
                ", dubber=" + dubber +
                '}';
    }
}
