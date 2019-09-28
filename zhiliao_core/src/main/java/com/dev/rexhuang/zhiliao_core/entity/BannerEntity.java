package com.dev.rexhuang.zhiliao_core.entity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class BannerEntity {
    /**
     * banners : [{"imageUrl":"http://p1.music.126.net/-OK3bsyEeBlaEtCHhTygCA==/109951164385420862.jpg","targetId":1392990601,"adid":null,"targetType":1,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"1392990601","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.556338.1647250342.null"},{"imageUrl":"http://p1.music.126.net/OW7HV9-jsYoGNXycOUnWzQ==/109951164385161186.jpg","targetId":1391639224,"adid":null,"targetType":1,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"1391639224","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.552326.1647361724.null"},{"imageUrl":"http://p1.music.126.net/CJB4c5-TPK_gxteIHHKiMg==/109951164385172374.jpg","targetId":2998067750,"adid":null,"targetType":1000,"titleColor":"red","typeTitle":"歌单","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"2998067750","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.553323.1647304960.null"},{"imageUrl":"http://p1.music.126.net/ZZAxwV4Ar1yvSJ9p9j52Hg==/109951164385179745.jpg","targetId":81852830,"adid":null,"targetType":10,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"81852830","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.554337.1647370280.null"},{"imageUrl":"http://p1.music.126.net/JA6Uh-iF2ZqXLZPKnQnh3g==/109951164385189143.jpg","targetId":1392971370,"adid":null,"targetType":1,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"1392971370","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.552331.1647426015.null"},{"imageUrl":"http://p1.music.126.net/exBvObWMJ7EaI82iImjrgA==/109951164385461542.jpg","targetId":1392925633,"adid":null,"targetType":1,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"1392925633","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.554339.-1055235818.null"},{"imageUrl":"http://p1.music.126.net/8gfUd7AH-a7184S6aLvIlg==/109951164385005462.jpg","targetId":1392965986,"adid":null,"targetType":1,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"1392965986","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.559314.1647333858.null"},{"imageUrl":"http://p1.music.126.net/37PYo4DS5kUSjdtR3l614w==/109951164385182904.jpg","targetId":1392985823,"adid":null,"targetType":1,"titleColor":"red","typeTitle":"独家","url":null,"exclusive":false,"monitorImpress":null,"monitorClick":null,"monitorType":null,"monitorImpressList":null,"monitorClickList":null,"monitorBlackList":null,"extMonitor":null,"extMonitorInfo":null,"adSource":null,"adLocation":null,"adDispatchJson":null,"encodeId":"1392985823","program":null,"event":null,"video":null,"song":null,"scm":"1.music-homepage.homepage_banner_force.banner.555335.1647337885.null"}]
     * code : 200
     */

    private int code;
    private List<BannersEntity> banners;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<BannersEntity> getBanners() {
        return banners;
    }

    public void setBanners(List<BannersEntity> banners) {
        this.banners = banners;
    }

    public static class BannersEntity {
        /**
         * imageUrl : http://p1.music.126.net/-OK3bsyEeBlaEtCHhTygCA==/109951164385420862.jpg
         * targetId : 1392990601
         * adid : null
         * targetType : 1
         * titleColor : red
         * typeTitle : 独家
         * url : null
         * exclusive : false
         * monitorImpress : null
         * monitorClick : null
         * monitorType : null
         * monitorImpressList : null
         * monitorClickList : null
         * monitorBlackList : null
         * extMonitor : null
         * extMonitorInfo : null
         * adSource : null
         * adLocation : null
         * adDispatchJson : null
         * encodeId : 1392990601
         * program : null
         * event : null
         * video : null
         * song : null
         * scm : 1.music-homepage.homepage_banner_force.banner.556338.1647250342.null
         */

        private String imageUrl;
        private String targetId;
        private Object adid;
        private String targetType;
        private String titleColor;
        private String typeTitle;
        private String url;
        private boolean exclusive;
        private String monitorImpress;
        private String monitorClick;
        private String monitorType;
        private Object monitorImpressList;
        private Object monitorClickList;
        private Object monitorBlackList;
        private Object extMonitor;
        private Object extMonitorInfo;
        private Object adSource;
        private Object adLocation;
        private Object adDispatchJson;
        private String encodeId;
        private Object program;
        private Object event;
        private Object video;
        private Object song;
        private String scm;

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public Object getAdid() {
            return adid;
        }

        public void setAdid(Object adid) {
            this.adid = adid;
        }

        public String getTargetType() {
            return targetType;
        }

        public void setTargetType(String targetType) {
            this.targetType = targetType;
        }

        public String getTitleColor() {
            return titleColor;
        }

        public void setTitleColor(String titleColor) {
            this.titleColor = titleColor;
        }

        public String getTypeTitle() {
            return typeTitle;
        }

        public void setTypeTitle(String typeTitle) {
            this.typeTitle = typeTitle;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isExclusive() {
            return exclusive;
        }

        public void setExclusive(boolean exclusive) {
            this.exclusive = exclusive;
        }

        public String getMonitorImpress() {
            return monitorImpress;
        }

        public void setMonitorImpress(String monitorImpress) {
            this.monitorImpress = monitorImpress;
        }

        public String getMonitorClick() {
            return monitorClick;
        }

        public void setMonitorClick(String monitorClick) {
            this.monitorClick = monitorClick;
        }

        public String getMonitorType() {
            return monitorType;
        }

        public void setMonitorType(String monitorType) {
            this.monitorType = monitorType;
        }

        public Object getMonitorImpressList() {
            return monitorImpressList;
        }

        public void setMonitorImpressList(Object monitorImpressList) {
            this.monitorImpressList = monitorImpressList;
        }

        public Object getMonitorClickList() {
            return monitorClickList;
        }

        public void setMonitorClickList(Object monitorClickList) {
            this.monitorClickList = monitorClickList;
        }

        public Object getMonitorBlackList() {
            return monitorBlackList;
        }

        public void setMonitorBlackList(Object monitorBlackList) {
            this.monitorBlackList = monitorBlackList;
        }

        public Object getExtMonitor() {
            return extMonitor;
        }

        public void setExtMonitor(Object extMonitor) {
            this.extMonitor = extMonitor;
        }

        public Object getExtMonitorInfo() {
            return extMonitorInfo;
        }

        public void setExtMonitorInfo(Object extMonitorInfo) {
            this.extMonitorInfo = extMonitorInfo;
        }

        public Object getAdSource() {
            return adSource;
        }

        public void setAdSource(Object adSource) {
            this.adSource = adSource;
        }

        public Object getAdLocation() {
            return adLocation;
        }

        public void setAdLocation(Object adLocation) {
            this.adLocation = adLocation;
        }

        public Object getAdDispatchJson() {
            return adDispatchJson;
        }

        public void setAdDispatchJson(Object adDispatchJson) {
            this.adDispatchJson = adDispatchJson;
        }

        public String getEncodeId() {
            return encodeId;
        }

        public void setEncodeId(String encodeId) {
            this.encodeId = encodeId;
        }

        public Object getProgram() {
            return program;
        }

        public void setProgram(Object program) {
            this.program = program;
        }

        public Object getEvent() {
            return event;
        }

        public void setEvent(Object event) {
            this.event = event;
        }

        public Object getVideo() {
            return video;
        }

        public void setVideo(Object video) {
            this.video = video;
        }

        public Object getSong() {
            return song;
        }

        public void setSong(Object song) {
            this.song = song;
        }

        public String getScm() {
            return scm;
        }

        public void setScm(String scm) {
            this.scm = scm;
        }
    }
}
