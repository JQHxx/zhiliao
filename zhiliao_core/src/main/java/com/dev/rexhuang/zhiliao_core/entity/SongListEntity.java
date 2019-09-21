package com.dev.rexhuang.zhiliao_core.entity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class SongListEntity extends ZhiliaoEntity{
    /**
     * code : 0
     * messgae : 错误信息
     * data : [{"id":"myrj59xigr","cover":"","name":"我喜欢的","order":2,"description":""},{"id":"gl1vumb4c5","cover":"","name":"我爱的","order":3,"description":""},{"id":"wtafyzf3ax","cover":"","name":"你叫什么","order":4,"description":""},{"id":"on8valxf9e","cover":"","name":"男歌手","order":5,"description":""},{"id":"c9vodq5t5t","cover":"","name":"女歌手","order":6,"description":""},{"id":"t6umsp5vpt","cover":"","name":"粤语","order":7,"description":""},{"id":"qfv2sg1mu3","cover":"","name":"英语","order":8,"description":""},{"id":"a2pw3zdbxo","cover":"","name":"德语","order":9,"description":""},{"id":"inklb1uw29","cover":"","name":"日语","order":10,"description":""}]
     */

    private List<DataEntity> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataEntity> getData() {
        return data;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataEntity {
        /**
         * id : myrj59xigr
         * cover :
         * name : 我喜欢的
         * order : 2
         * description :
         */

        private String id;
        private String cover;
        private String name;
        private int order;
        private String description;

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

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
