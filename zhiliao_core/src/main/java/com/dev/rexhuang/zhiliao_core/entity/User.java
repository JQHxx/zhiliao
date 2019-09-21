package com.dev.rexhuang.zhiliao_core.entity;

/**
 * *  created by RexHuang
 * *  on 2019/9/4
 */
public class User extends ZhiliaoEntity{

    /**
     * code : 0
     * data : {"id":"172911","email":"562520840@qq.com","phonenumber":null,"avatar":"","nickname":"rexRestFormApp2","status":"","join_time":"2019-07-11T12:51:55.000Z","factory":"","cicada":"1","token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTcyOTExIiwiZXhwIjoxNTc1MzQ0Mjc1LCJpYXQiOjE1Njc1NjgyNzV9.CYTb8QrIih1sCrxMf5fo4Fj_rSLYHGiw96vqPK5fjQQ","token_expired_at":"2019-12-03T03:37:55.039Z"}
     */

    private DataEntity data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
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
         * id : 172911
         * email : 562520840@qq.com
         * phonenumber : null
         * avatar :
         * nickname : rexRestFormApp2
         * status :
         * join_time : 2019-07-11T12:51:55.000Z
         * factory :
         * cicada : 1
         * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTcyOTExIiwiZXhwIjoxNTc1MzQ0Mjc1LCJpYXQiOjE1Njc1NjgyNzV9.CYTb8QrIih1sCrxMf5fo4Fj_rSLYHGiw96vqPK5fjQQ
         * token_expired_at : 2019-12-03T03:37:55.039Z
         */

        private String id;
        private String email;
        private Object phonenumber;
        private String avatar;
        private String nickname;
        private String status;
        private String join_time;
        private String factory;
        private String cicada;
        private String token;
        private String token_expired_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Object getPhonenumber() {
            return phonenumber;
        }

        public void setPhonenumber(Object phonenumber) {
            this.phonenumber = phonenumber;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getJoin_time() {
            return join_time;
        }

        public void setJoin_time(String join_time) {
            this.join_time = join_time;
        }

        public String getFactory() {
            return factory;
        }

        public void setFactory(String factory) {
            this.factory = factory;
        }

        public String getCicada() {
            return cicada;
        }

        public void setCicada(String cicada) {
            this.cicada = cicada;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getToken_expired_at() {
            return token_expired_at;
        }

        public void setToken_expired_at(String token_expired_at) {
            this.token_expired_at = token_expired_at;
        }
    }
}
