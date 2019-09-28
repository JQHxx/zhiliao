package com.dev.rexhuang.zhiliao_core.entity;

import com.google.android.exoplayer2.C;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class ArtistsParams {
    public ArtistsParams() {
    }

    /**
     * comm : {"ct":24,"cv":0}
     * singerList : {"method":"get_singer_list","module":"Music.SingerListServer","param":{"area":-100,"cur_page":1,"genre":-100,"index":-100,"sex":-100,"sin":0}}
     */

    public ArtistsParams(CommEntity comm, SingerListEntity singerList) {
        this.comm = comm;
        this.singerList = singerList;
    }

    private CommEntity comm;
    private SingerListEntity singerList;

    public CommEntity getComm() {
        return comm;
    }

    public void setComm(CommEntity comm) {
        this.comm = comm;
    }

    public SingerListEntity getSingerList() {
        return singerList;
    }

    public void setSingerList(SingerListEntity singerList) {
        this.singerList = singerList;
    }

    public static class CommEntity {
        public CommEntity() {
        }

        /**
         * ct : 24
         * cv : 0
         */
        public CommEntity(int ct, int cv) {
            this.ct = ct;
            this.cv = cv;
        }

        private int ct;
        private int cv;

        public int getCt() {
            return ct;
        }

        public void setCt(int ct) {
            this.ct = ct;
        }

        public int getCv() {
            return cv;
        }

        public void setCv(int cv) {
            this.cv = cv;
        }
    }

    public static class SingerListEntity {
        public SingerListEntity() {
        }

        /**
         * method : get_singer_list
         * module : Music.SingerListServer
         * param : {"area":-100,"cur_page":1,"genre":-100,"index":-100,"sex":-100,"sin":0}
         */

        public SingerListEntity(String method, String module, ParamEntity param) {
            this.method = method;
            this.module = module;
            this.param = param;
        }

        private ParamEntity param;
        private String method;
        private String module;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public ParamEntity getParam() {
            return param;
        }

        public void setParam(ParamEntity param) {
            this.param = param;
        }

        public static class ParamEntity {
            public ParamEntity() {
            }

            public ParamEntity(int area, int sex, int genre, int index, int sin, int cur_page) {
                this.area = area;
                this.cur_page = cur_page;
                this.genre = genre;
                this.index = index;
                this.sex = sex;
                this.sin = sin;
            }

            /**
             * area : -100
             * cur_page : 1
             * genre : -100
             * index : -100
             * sex : -100
             * sin : 0
             */

            private int area;
            private int cur_page;
            private int genre;
            private int index;
            private int sex;
            private int sin;

            public int getArea() {
                return area;
            }

            public void setArea(int area) {
                this.area = area;
            }

            public int getCur_page() {
                return cur_page;
            }

            public void setCur_page(int cur_page) {
                this.cur_page = cur_page;
            }

            public int getGenre() {
                return genre;
            }

            public void setGenre(int genre) {
                this.genre = genre;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public int getSex() {
                return sex;
            }

            public void setSex(int sex) {
                this.sex = sex;
            }

            public int getSin() {
                return sin;
            }

            public void setSin(int sin) {
                this.sin = sin;
            }
        }
    }
}
