package com.dev.rexhuang.zhiliao_core.entity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/12/9
 */
public class SongListDetailEntity extends ZhiliaoEntity{

    private DataEntity data;

    public DataEntity getDataEntity() {
        return data;
    }

    public void setDataEntity(DataEntity dataEntity) {
        this.data = dataEntity;
    }

    public static class DataEntity {
        /**
         * id : ngdfivxw
         * cover :
         * name : 我喜欢的
         * create_time : 2019-07-24T10:23:06.000Z
         * order : 2
         * music_list : [{"id":"yyqokziw","type":1,"cover":"https://static.mebtte.com/music_cover/1ee99d29ef7e259b8bd58d8071ceb009.jpeg","name":"不能说的秘密","alias":"","normal":"https://static.mebtte.com/music/ab56fabbca609239da839960dd7a5b7f.mp3","accompany":"","hq":"","mv":"","singers":[{"id":"pbturioh","avatar":"https://static.mebtte.com/figure_avatar/81b8f2ec1c8e9dea8d29f9a9312b13aa.jpeg","name":"周杰伦","alias":""}],"cover_from":null}]
         */

        private String id;
        private String cover;
        private String name;
        private String create_time;
        private int order;
        private List<MusicEntity> music_list;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public List<MusicEntity> getMusic_list() {
            return music_list;
        }

        public void setMusic_list(List<MusicEntity> music_list) {
            this.music_list = music_list;
        }

    }
}
