package com.dev.rexhuang.zhiliao_core.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/4
 */
public class MusicEntity implements Parcelable {

    /**
     * id : rwunn4b886f
     * type : 1
     * cover : https://static.mebtte.com/music_cover/1ee99d29ef7e259b8bd58d8071ceb009.jpeg
     * name : 不能说的秘密
     * alias :
     * normal : https://static.mebtte.com/music/ab56fabbca609239da839960dd7a5b7f.mp3
     * accompany :
     * hq :
     * mv :
     * singers : [{"id":"d9zsnwcjd5q","avatar":"https://static.mebtte.com/figure_avatar/81b8f2ec1c8e9dea8d29f9a9312b13aa.jpeg","name":"周杰伦","alias":""}]
     * cover_from : null
     */

    private String id;
    private int type;
    private String cover;
    private String name;
    private String alias;
    private String normal;
    private String accompany;
    private String hq;
    private String mv;
    private Object cover_from;
    private List<SingersEntity> singers;
    private transient Bitmap coverBitmap;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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

    public String getNormal() {
        return normal;
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getAccompany() {
        return accompany;
    }

    public void setAccompany(String accompany) {
        this.accompany = accompany;
    }

    public String getHq() {
        return hq;
    }

    public void setHq(String hq) {
        this.hq = hq;
    }

    public String getMv() {
        return mv;
    }

    public void setMv(String mv) {
        this.mv = mv;
    }

    public Object getCover_from() {
        return cover_from;
    }

    public void setCover_from(Object cover_from) {
        this.cover_from = cover_from;
    }

    public List<SingersEntity> getSingers() {
        return singers;
    }

    public void setSingers(List<SingersEntity> singers) {
        this.singers = singers;
    }

//    public static class SingersEntity {
//        /**
//         * id : d9zsnwcjd5q
//         * avatar : https://static.mebtte.com/figure_avatar/81b8f2ec1c8e9dea8d29f9a9312b13aa.jpeg
//         * name : 周杰伦
//         * alias :
//         */
//
//        private String id;
//        private String avatar;
//        private String name;
//        private String alias;
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public String getAvatar() {
//            return avatar;
//        }
//
//        public void setAvatar(String avatar) {
//            this.avatar = avatar;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getAlias() {
//            return alias;
//        }
//
//        public void setAlias(String alias) {
//            this.alias = alias;
//        }
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.cover);
        dest.writeString(this.name);
        dest.writeString(this.alias);
        dest.writeString(this.normal);
        dest.writeString(this.accompany);
        dest.writeString(this.hq);
        dest.writeParcelable((Parcelable) this.cover_from, flags);
        dest.writeTypedList(this.singers);
    }

    public MusicEntity() {
    }

    protected MusicEntity(Parcel in) {
        this.id = in.readString();
        this.type = in.readInt();
        this.cover = in.readString();
        this.name = in.readString();
        this.alias = in.readString();
        this.normal = in.readString();
        this.accompany = in.readString();
        this.hq = in.readString();
        this.cover_from = in.readParcelable(Object.class.getClassLoader());
        this.singers = in.createTypedArrayList(SingersEntity.CREATOR);
    }

    public static final Parcelable.Creator<MusicEntity> CREATOR = new Parcelable.Creator<MusicEntity>() {
        @Override
        public MusicEntity createFromParcel(Parcel source) {
            return new MusicEntity(source);
        }

        @Override
        public MusicEntity[] newArray(int size) {
            return new MusicEntity[size];
        }
    };

    public Bitmap getCoverBitmap() {
        return coverBitmap;
    }

    public void setCoverBitmap(Bitmap coverBitmap) {
        this.coverBitmap = coverBitmap;
    }
}
