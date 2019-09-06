package com.dev.rexhuang.zhiliao_core.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * *  created by RexHuang
 * *  on 2019/9/4
 */
public class SingersEntity implements Parcelable {
    /**
     * id : d9zsnwcjd5q
     * avatar : https://static.mebtte.com/figure_avatar/81b8f2ec1c8e9dea8d29f9a9312b13aa.jpeg
     * name : 周杰伦
     * alias :
     */

    private String id;
    private String avatar;
    private String name;
    private String alias;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeString(this.alias);
    }

    public SingersEntity() {
    }

    protected SingersEntity(Parcel in) {
        this.id = in.readString();
        this.avatar = in.readString();
        this.name = in.readString();
        this.alias = in.readString();
    }

    public static final Parcelable.Creator<SingersEntity> CREATOR = new Parcelable.Creator<SingersEntity>() {
        @Override
        public SingersEntity createFromParcel(Parcel source) {
            return new SingersEntity(source);
        }

        @Override
        public SingersEntity[] newArray(int size) {
            return new SingersEntity[size];
        }
    };
}
