package com.dev.rexhuang.zhiliao_core.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class Artist implements Parcelable {
    String name;
    Long id;
    String artistId;
    int count;
    String type;
    String picUrl;
    String desc;
    int musicSize;
    int score;
    int albumSize;

    public Artist() {
    }


    public Artist(String name, Long id, String artistId, int count, String type, String picUrl, String desc, int musicSize, int score, int albumSize) {
        this.name = name;
        this.id = id;
        this.artistId = artistId;
        this.count = count;
        this.type = type;
        this.picUrl = picUrl;
        this.desc = desc;
        this.musicSize = musicSize;
        this.score = score;
        this.albumSize = albumSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeValue(this.id);
        dest.writeString(this.artistId);
        dest.writeInt(this.count);
        dest.writeString(this.type);
        dest.writeString(this.picUrl);
        dest.writeString(this.desc);
        dest.writeInt(this.musicSize);
        dest.writeInt(this.score);
        dest.writeInt(this.albumSize);
    }

    protected Artist(Parcel in) {
        this.name = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.artistId = in.readString();
        this.count = in.readInt();
        this.type = in.readString();
        this.picUrl = in.readString();
        this.desc = in.readString();
        this.musicSize = in.readInt();
        this.score = in.readInt();
        this.albumSize = in.readInt();
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getAlbumSize() {
        return albumSize;
    }

    public void setAlbumSize(int albumSize) {
        this.albumSize = albumSize;
    }

    public static Creator<Artist> getCREATOR() {
        return CREATOR;
    }
}
