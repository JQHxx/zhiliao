package com.dev.rexhuang.zhiliao_core.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * *  created by RexHuang
 * *  on 2019/8/1
 */
public class Music implements Parcelable {
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Boolean getLove() {
        return isLove;
    }

    public void setLove(Boolean love) {
        isLove = love;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public String getCoverBig() {
        return coverBig;
    }

    public void setCoverBig(String coverBig) {
        this.coverBig = coverBig;
    }

    public String getCoverSmall() {
        return coverSmall;
    }

    public void setCoverSmall(String coverSmall) {
        this.coverSmall = coverSmall;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getCp() {
        return isCp;
    }

    public void setCp(Boolean cp) {
        isCp = cp;
    }

    public Boolean getDl() {
        return isDl;
    }

    public void setDl(Boolean dl) {
        isDl = dl;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public Integer getQuality() {
        return quality;
    }

    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    public Boolean getHq() {
        return hq;
    }

    public void setHq(Boolean hq) {
        this.hq = hq;
    }

    public Boolean getSq() {
        return sq;
    }

    public void setSq(Boolean sq) {
        this.sq = sq;
    }

    public Boolean getHigh() {
        return high;
    }

    public void setHigh(Boolean high) {
        this.high = high;
    }

    private String type;
    private Long id = 0L;
    private String mid;
    private String title;
    private String artist;
    private String album;
    private String artistId;
    private String albumId;
    private Integer trackNumber = 0;
    private Long duration = 0L;
    private Boolean isLove = false;
    private Boolean isOnline = true;
    private String uri;
    private String lyric;
    private String coverUri;
    private String coverBig;
    private String coverSmall;
    private String fileName;
    private Long fileSize = 0L;
    private String year;
    private Long date;
    private Boolean isCp = false;
    private Boolean isDl = true;
    private String collectId;
    private Integer quality = 128000;
    private Boolean hq = false;
    private Boolean sq = false;
    private Boolean high = false;
//    private Integer hasMv = 0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeValue(this.id);
        dest.writeString(this.mid);
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeString(this.album);
        dest.writeString(this.artistId);
        dest.writeString(this.albumId);
        dest.writeValue(this.trackNumber);
        dest.writeValue(this.duration);
        dest.writeValue(this.isLove);
        dest.writeValue(this.isOnline);
        dest.writeString(this.uri);
        dest.writeString(this.lyric);
        dest.writeString(this.coverUri);
        dest.writeString(this.coverBig);
        dest.writeString(this.coverSmall);
        dest.writeString(this.fileName);
        dest.writeValue(this.fileSize);
        dest.writeString(this.year);
        dest.writeValue(this.date);
        dest.writeValue(this.isCp);
        dest.writeValue(this.isDl);
        dest.writeString(this.collectId);
        dest.writeValue(this.quality);
        dest.writeValue(this.hq);
        dest.writeValue(this.sq);
        dest.writeValue(this.high);
//        dest.writeValue(this.hasMv);
    }

    public Music() {
    }

    protected Music(Parcel in) {
        this.type = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.mid = in.readString();
        this.title = in.readString();
        this.artist = in.readString();
        this.album = in.readString();
        this.artistId = in.readString();
        this.albumId = in.readString();
        this.trackNumber = (Integer) in.readValue(Integer.class.getClassLoader());
        this.duration = (Long) in.readValue(Long.class.getClassLoader());
        this.isLove = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isOnline = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.uri = in.readString();
        this.lyric = in.readString();
        this.coverUri = in.readString();
        this.coverBig = in.readString();
        this.coverSmall = in.readString();
        this.fileName = in.readString();
        this.fileSize = (Long) in.readValue(Long.class.getClassLoader());
        this.year = in.readString();
        this.date = (Long) in.readValue(Long.class.getClassLoader());
        this.isCp = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.isDl = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.collectId = in.readString();
        this.quality = (Integer) in.readValue(Integer.class.getClassLoader());
        this.hq = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.sq = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.high = (Boolean) in.readValue(Boolean.class.getClassLoader());
//        this.hasMv = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
