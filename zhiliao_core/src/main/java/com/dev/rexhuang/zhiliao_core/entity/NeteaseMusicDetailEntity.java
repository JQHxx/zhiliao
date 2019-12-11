package com.dev.rexhuang.zhiliao_core.entity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/12/11
 */
public class NeteaseMusicDetailEntity {

    /**
     * songs : [{"name":"海阔天空","id":347230,"pst":0,"t":0,"ar":[{"id":11127,"name":"Beyond","tns":[],"alias":[]}],"alia":[],"pop":100,"st":0,"rt":"600902000004240302","fee":8,"v":31,"crbt":null,"cf":"","al":{"id":34209,"name":"海阔天空","picUrl":"https://p2.music.126.net/QHw-RuMwfQkmgtiyRpGs0Q==/102254581395219.jpg","tns":[],"pic":102254581395219},"dt":326348,"h":{"br":320000,"fid":0,"size":13070578,"vd":0.109906},"m":{"br":160000,"fid":0,"size":6549371,"vd":0.272218},"l":{"br":96000,"fid":0,"size":3940469,"vd":0.228837},"a":null,"cd":"1","no":1,"rtUrl":null,"ftype":0,"rtUrls":[],"djId":0,"copyright":1,"s_id":0,"mark":0,"mst":9,"cp":7002,"mv":376199,"rurl":null,"rtype":0,"publishTime":746812800000}]
     * privileges : [{"id":347230,"fee":8,"payed":0,"st":0,"pl":128000,"dl":0,"sp":7,"cp":1,"subp":1,"cs":false,"maxbr":999000,"fl":128000,"toast":false,"flag":0,"preSell":false}]
     * code : 200
     */

    private int code;
    private List<SongsEntity> songs;
    private List<PrivilegesEntity> privileges;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SongsEntity> getSongs() {
        return songs;
    }

    public void setSongs(List<SongsEntity> songs) {
        this.songs = songs;
    }

    public List<PrivilegesEntity> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<PrivilegesEntity> privileges) {
        this.privileges = privileges;
    }

    public static class SongsEntity {
        /**
         * name : 海阔天空
         * id : 347230
         * pst : 0
         * t : 0
         * ar : [{"id":11127,"name":"Beyond","tns":[],"alias":[]}]
         * alia : []
         * pop : 100
         * st : 0
         * rt : 600902000004240302
         * fee : 8
         * v : 31
         * crbt : null
         * cf :
         * al : {"id":34209,"name":"海阔天空","picUrl":"https://p2.music.126.net/QHw-RuMwfQkmgtiyRpGs0Q==/102254581395219.jpg","tns":[],"pic":102254581395219}
         * dt : 326348
         * h : {"br":320000,"fid":0,"size":13070578,"vd":0.109906}
         * m : {"br":160000,"fid":0,"size":6549371,"vd":0.272218}
         * l : {"br":96000,"fid":0,"size":3940469,"vd":0.228837}
         * a : null
         * cd : 1
         * no : 1
         * rtUrl : null
         * ftype : 0
         * rtUrls : []
         * djId : 0
         * copyright : 1
         * s_id : 0
         * mark : 0
         * mst : 9
         * cp : 7002
         * mv : 376199
         * rurl : null
         * rtype : 0
         * publishTime : 746812800000
         */

        private String name;
        private int id;
        private int pst;
        private int t;
        private int pop;
        private int st;
        private String rt;
        private int fee;
        private int v;
        private Object crbt;
        private String cf;
        private AlEntity al;
        private int dt;
        private HEntity h;
        private MEntity m;
        private LEntity l;
        private Object a;
        private String cd;
        private int no;
        private Object rtUrl;
        private int ftype;
        private int djId;
        private int copyright;
        private int s_id;
        private int mark;
        private int mst;
        private int cp;
        private int mv;
        private Object rurl;
        private int rtype;
        private long publishTime;
        private List<ArEntity> ar;
        private List<?> alia;
        private List<?> rtUrls;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPst() {
            return pst;
        }

        public void setPst(int pst) {
            this.pst = pst;
        }

        public int getT() {
            return t;
        }

        public void setT(int t) {
            this.t = t;
        }

        public int getPop() {
            return pop;
        }

        public void setPop(int pop) {
            this.pop = pop;
        }

        public int getSt() {
            return st;
        }

        public void setSt(int st) {
            this.st = st;
        }

        public String getRt() {
            return rt;
        }

        public void setRt(String rt) {
            this.rt = rt;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public int getV() {
            return v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public Object getCrbt() {
            return crbt;
        }

        public void setCrbt(Object crbt) {
            this.crbt = crbt;
        }

        public String getCf() {
            return cf;
        }

        public void setCf(String cf) {
            this.cf = cf;
        }

        public AlEntity getAl() {
            return al;
        }

        public void setAl(AlEntity al) {
            this.al = al;
        }

        public int getDt() {
            return dt;
        }

        public void setDt(int dt) {
            this.dt = dt;
        }

        public HEntity getH() {
            return h;
        }

        public void setH(HEntity h) {
            this.h = h;
        }

        public MEntity getM() {
            return m;
        }

        public void setM(MEntity m) {
            this.m = m;
        }

        public LEntity getL() {
            return l;
        }

        public void setL(LEntity l) {
            this.l = l;
        }

        public Object getA() {
            return a;
        }

        public void setA(Object a) {
            this.a = a;
        }

        public String getCd() {
            return cd;
        }

        public void setCd(String cd) {
            this.cd = cd;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public Object getRtUrl() {
            return rtUrl;
        }

        public void setRtUrl(Object rtUrl) {
            this.rtUrl = rtUrl;
        }

        public int getFtype() {
            return ftype;
        }

        public void setFtype(int ftype) {
            this.ftype = ftype;
        }

        public int getDjId() {
            return djId;
        }

        public void setDjId(int djId) {
            this.djId = djId;
        }

        public int getCopyright() {
            return copyright;
        }

        public void setCopyright(int copyright) {
            this.copyright = copyright;
        }

        public int getS_id() {
            return s_id;
        }

        public void setS_id(int s_id) {
            this.s_id = s_id;
        }

        public int getMark() {
            return mark;
        }

        public void setMark(int mark) {
            this.mark = mark;
        }

        public int getMst() {
            return mst;
        }

        public void setMst(int mst) {
            this.mst = mst;
        }

        public int getCp() {
            return cp;
        }

        public void setCp(int cp) {
            this.cp = cp;
        }

        public int getMv() {
            return mv;
        }

        public void setMv(int mv) {
            this.mv = mv;
        }

        public Object getRurl() {
            return rurl;
        }

        public void setRurl(Object rurl) {
            this.rurl = rurl;
        }

        public int getRtype() {
            return rtype;
        }

        public void setRtype(int rtype) {
            this.rtype = rtype;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }

        public List<ArEntity> getAr() {
            return ar;
        }

        public void setAr(List<ArEntity> ar) {
            this.ar = ar;
        }

        public List<?> getAlia() {
            return alia;
        }

        public void setAlia(List<?> alia) {
            this.alia = alia;
        }

        public List<?> getRtUrls() {
            return rtUrls;
        }

        public void setRtUrls(List<?> rtUrls) {
            this.rtUrls = rtUrls;
        }

        public static class AlEntity {
            /**
             * id : 34209
             * name : 海阔天空
             * picUrl : https://p2.music.126.net/QHw-RuMwfQkmgtiyRpGs0Q==/102254581395219.jpg
             * tns : []
             * pic : 102254581395219
             */

            private int id;
            private String name;
            private String picUrl;
            private long pic;
            private List<?> tns;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public long getPic() {
                return pic;
            }

            public void setPic(long pic) {
                this.pic = pic;
            }

            public List<?> getTns() {
                return tns;
            }

            public void setTns(List<?> tns) {
                this.tns = tns;
            }
        }

        public static class HEntity {
            /**
             * br : 320000
             * fid : 0
             * size : 13070578
             * vd : 0.109906
             */

            private int br;
            private int fid;
            private int size;
            private double vd;

            public int getBr() {
                return br;
            }

            public void setBr(int br) {
                this.br = br;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public double getVd() {
                return vd;
            }

            public void setVd(double vd) {
                this.vd = vd;
            }
        }

        public static class MEntity {
            /**
             * br : 160000
             * fid : 0
             * size : 6549371
             * vd : 0.272218
             */

            private int br;
            private int fid;
            private int size;
            private double vd;

            public int getBr() {
                return br;
            }

            public void setBr(int br) {
                this.br = br;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public double getVd() {
                return vd;
            }

            public void setVd(double vd) {
                this.vd = vd;
            }
        }

        public static class LEntity {
            /**
             * br : 96000
             * fid : 0
             * size : 3940469
             * vd : 0.228837
             */

            private int br;
            private int fid;
            private int size;
            private double vd;

            public int getBr() {
                return br;
            }

            public void setBr(int br) {
                this.br = br;
            }

            public int getFid() {
                return fid;
            }

            public void setFid(int fid) {
                this.fid = fid;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public double getVd() {
                return vd;
            }

            public void setVd(double vd) {
                this.vd = vd;
            }
        }

        public static class ArEntity {
            /**
             * id : 11127
             * name : Beyond
             * tns : []
             * alias : []
             */

            private int id;
            private String name;
            private List<?> tns;
            private List<?> alias;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<?> getTns() {
                return tns;
            }

            public void setTns(List<?> tns) {
                this.tns = tns;
            }

            public List<?> getAlias() {
                return alias;
            }

            public void setAlias(List<?> alias) {
                this.alias = alias;
            }
        }
    }

    public static class PrivilegesEntity {
        /**
         * id : 347230
         * fee : 8
         * payed : 0
         * st : 0
         * pl : 128000
         * dl : 0
         * sp : 7
         * cp : 1
         * subp : 1
         * cs : false
         * maxbr : 999000
         * fl : 128000
         * toast : false
         * flag : 0
         * preSell : false
         */

        private int id;
        private int fee;
        private int payed;
        private int st;
        private int pl;
        private int dl;
        private int sp;
        private int cp;
        private int subp;
        private boolean cs;
        private int maxbr;
        private int fl;
        private boolean toast;
        private int flag;
        private boolean preSell;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        public int getPayed() {
            return payed;
        }

        public void setPayed(int payed) {
            this.payed = payed;
        }

        public int getSt() {
            return st;
        }

        public void setSt(int st) {
            this.st = st;
        }

        public int getPl() {
            return pl;
        }

        public void setPl(int pl) {
            this.pl = pl;
        }

        public int getDl() {
            return dl;
        }

        public void setDl(int dl) {
            this.dl = dl;
        }

        public int getSp() {
            return sp;
        }

        public void setSp(int sp) {
            this.sp = sp;
        }

        public int getCp() {
            return cp;
        }

        public void setCp(int cp) {
            this.cp = cp;
        }

        public int getSubp() {
            return subp;
        }

        public void setSubp(int subp) {
            this.subp = subp;
        }

        public boolean isCs() {
            return cs;
        }

        public void setCs(boolean cs) {
            this.cs = cs;
        }

        public int getMaxbr() {
            return maxbr;
        }

        public void setMaxbr(int maxbr) {
            this.maxbr = maxbr;
        }

        public int getFl() {
            return fl;
        }

        public void setFl(int fl) {
            this.fl = fl;
        }

        public boolean isToast() {
            return toast;
        }

        public void setToast(boolean toast) {
            this.toast = toast;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public boolean isPreSell() {
            return preSell;
        }

        public void setPreSell(boolean preSell) {
            this.preSell = preSell;
        }
    }
}
