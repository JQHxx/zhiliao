package com.dev.rexhuang.zhiliao_core.entity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/12/10
 */
public class RecommendSongListEntity {

    /**
     * hasTaste : false
     * code : 200
     * category : 0
     * result : [{"id":924680166,"type":0,"name":"[华语速爆新歌] 最新华语音乐推荐","copywriter":"编辑推荐：优质华语新歌，网易云音乐每周二精选推荐！","picUrl":"https://p2.music.126.net/iK_AhfL1slpzYkgpIS8XCg==/109951164539305394.jpg","canDislike":false,"trackNumberUpdateTime":1575907200000,"playCount":671832320,"trackCount":20,"highQuality":false,"alg":"featured"},{"id":3070314382,"type":0,"name":"日系心路：愿你历经彷徨终是不负韶华","copywriter":"编辑推荐：历经彷徨，不负韶华","picUrl":"https://p2.music.126.net/7n4wAvZxiZFWk3Tr9SkwNQ==/109951164482604286.jpg","canDislike":false,"trackNumberUpdateTime":1574487768983,"playCount":75674,"trackCount":30,"highQuality":false,"alg":"featured"},{"id":497143804,"type":0,"name":"【纯音乐】适合编程、码字，隔绝老板的聒噪","copywriter":"根据你喜欢的单曲《斑鸠 ~気高き鸟~》推荐","picUrl":"https://p2.music.126.net/e9sMeFcs5JwA5fcpw6Nq1w==/109951162818642726.jpg","canDislike":true,"trackNumberUpdateTime":1575882213508,"playCount":93415,"trackCount":202,"highQuality":false,"alg":"itembased"},{"id":546235282,"type":0,"name":"来电铃声｜前奏沦陷-","copywriter":"根据你喜欢的单曲《Someone Like You》推荐","picUrl":"https://p2.music.126.net/LW87LykUITeDxa-g3eEmKQ==/19118308184344432.jpg","canDislike":true,"trackNumberUpdateTime":1575908352175,"playCount":4200326,"trackCount":526,"highQuality":false,"alg":"itembased"},{"id":8932390,"type":0,"name":"缘之空背景音乐","copywriter":"根据你喜欢的单曲《ヨスガノソラ メインテーマ -願い-》推荐","picUrl":"https://p2.music.126.net/xzZedK35rF8mtc9WlCdEhA==/6068204673767097.jpg","canDislike":true,"trackNumberUpdateTime":1392744250214,"playCount":919263,"trackCount":27,"highQuality":false,"alg":"itembased"},{"id":594365440,"type":0,"name":"适合学习听的歌 纯音乐","copywriter":"根据你喜欢的单曲《Mallow Flower》推荐","picUrl":"https://p2.music.126.net/Sw4wbgfqEh4RXFKW6uvCog==/18771962022981524.jpg","canDislike":true,"trackNumberUpdateTime":1575195342924,"playCount":33833,"trackCount":131,"highQuality":false,"alg":"itembased"},{"id":98209679,"type":0,"name":"作业专属【刷题BGM】","copywriter":"根据你喜欢的单曲《Someone Like You》推荐","picUrl":"https://p2.music.126.net/uIKcMWUV5OfurfA1LENJOA==/109951164227156157.jpg","canDislike":true,"trackNumberUpdateTime":1573966753158,"playCount":41663892,"trackCount":100,"highQuality":false,"alg":"itembased"},{"id":100712280,"type":0,"name":"看歌词，学英语(高中)","copywriter":"根据你喜欢的单曲《Sound of Silence》推荐","picUrl":"https://p2.music.126.net/PYA8TwEyDwzKKMNmqEjNJA==/7941772489462632.jpg","canDislike":true,"trackNumberUpdateTime":1547922203393,"playCount":7737747,"trackCount":146,"highQuality":false,"alg":"itembased"},{"id":125353234,"type":0,"name":"纯音乐~不许睡眠用","copywriter":"根据你喜欢的单曲《ウンディーネ》推荐","picUrl":"https://p2.music.126.net/EkDnawQpp28kkbtci-ytJw==/109951163628973935.jpg","canDislike":true,"trackNumberUpdateTime":1575515878843,"playCount":48806,"trackCount":252,"highQuality":false,"alg":"itembased"},{"id":975446121,"type":0,"name":"【超燃】打游戏专用歌","copywriter":"根据你喜欢的单曲《骄傲的少年》推荐","picUrl":"https://p2.music.126.net/F3zjUUqrZ14ek-en1y6F5A==/18659811836862337.jpg","canDislike":true,"trackNumberUpdateTime":1575208684985,"playCount":1770840,"trackCount":123,"highQuality":false,"alg":"itembased"}]
     */

    private boolean hasTaste;
    private int code;
    private int category;
    private List<ResultEntity> result;

    public boolean isHasTaste() {
        return hasTaste;
    }

    public void setHasTaste(boolean hasTaste) {
        this.hasTaste = hasTaste;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public static class ResultEntity {
        /**
         * id : 924680166
         * type : 0
         * name : [华语速爆新歌] 最新华语音乐推荐
         * copywriter : 编辑推荐：优质华语新歌，网易云音乐每周二精选推荐！
         * picUrl : https://p2.music.126.net/iK_AhfL1slpzYkgpIS8XCg==/109951164539305394.jpg
         * canDislike : false
         * trackNumberUpdateTime : 1575907200000
         * playCount : 671832320
         * trackCount : 20
         * highQuality : false
         * alg : featured
         */

        private String id;
        private int type;
        private String name;
        private String copywriter;
        private String picUrl;
        private boolean canDislike;
        private long trackNumberUpdateTime;
        private int playCount;
        private int trackCount;
        private boolean highQuality;
        private String alg;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCopywriter() {
            return copywriter;
        }

        public void setCopywriter(String copywriter) {
            this.copywriter = copywriter;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public boolean isCanDislike() {
            return canDislike;
        }

        public void setCanDislike(boolean canDislike) {
            this.canDislike = canDislike;
        }

        public long getTrackNumberUpdateTime() {
            return trackNumberUpdateTime;
        }

        public void setTrackNumberUpdateTime(long trackNumberUpdateTime) {
            this.trackNumberUpdateTime = trackNumberUpdateTime;
        }

        public int getPlayCount() {
            return playCount;
        }

        public void setPlayCount(int playCount) {
            this.playCount = playCount;
        }

        public int getTrackCount() {
            return trackCount;
        }

        public void setTrackCount(int trackCount) {
            this.trackCount = trackCount;
        }

        public boolean isHighQuality() {
            return highQuality;
        }

        public void setHighQuality(boolean highQuality) {
            this.highQuality = highQuality;
        }

        public String getAlg() {
            return alg;
        }

        public void setAlg(String alg) {
            this.alg = alg;
        }
    }
}
