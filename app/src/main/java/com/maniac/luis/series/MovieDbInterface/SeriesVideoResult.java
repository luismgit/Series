package com.maniac.luis.series.MovieDbInterface;

import java.util.List;

/**
 * Created by Luis on 17/05/2018.
 */

public class SeriesVideoResult {

    /**
     * id : 1418
     * results : [{"id":"59f80b299251416a0c009cd3","iso_639_1":"en","iso_3166_1":"US","key":"WBb3fojgW0Q","name":"The Big Bang Theory - Official Trailer (HD)","site":"YouTube","size":360,"type":"Trailer"},{"id":"5a056fc2c3a3686a8c012aa5","iso_639_1":"en","iso_3166_1":"US","key":"I5mUUOZT6b4","name":"The Big Bang Theory - Science is dead","site":"YouTube","size":1080,"type":"Featurette"},{"id":"5a69f6149251414a25013459","iso_639_1":"en","iso_3166_1":"US","key":"x6H7k3XBlk4","name":"The Big Bang Theory Intro Full Version","site":"YouTube","size":480,"type":"Opening Credits"},{"id":"5a69f6700e0a260d67014ba1","iso_639_1":"en","iso_3166_1":"US","key":"e0p04CLd0gk","name":"The Big Bang Theory - Drawing","site":"YouTube","size":720,"type":"Featurette"},{"id":"5a69f69cc3a3685a7d0120f4","iso_639_1":"en","iso_3166_1":"US","key":"1Zkb8Cn-N28","name":"Merry Newtonmas!","site":"YouTube","size":360,"type":"Featurette"},{"id":"5a69f6cbc3a3685a7d012107","iso_639_1":"en","iso_3166_1":"US","key":"xNZx5aB11aI","name":"The Big Bang Theory - Sheldon, Howard and Stuar playing a Christmas song with bells","site":"YouTube","size":1080,"type":"Featurette"},{"id":"5a69f6fe0e0a260d780142f9","iso_639_1":"en","iso_3166_1":"US","key":"Iv42ZFZubS0","name":"The Big Bang Theory - 10x12 - Leonard & Penny Vs Christmas Tree","site":"YouTube","size":720,"type":"Featurette"},{"id":"5a69f71f9251414a28012b64","iso_639_1":"en","iso_3166_1":"US","key":"FpGkLzGl1CI","name":"The Big Bang Theory - Sheldon and Penny Exchange Presents","site":"YouTube","size":720,"type":"Featurette"},{"id":"5a69f7499251414a2e01340e","iso_639_1":"en","iso_3166_1":"US","key":"38x6kWB-xD4","name":"Big Bang Theory - Why Sheldon does not celebrate christmas","site":"YouTube","size":480,"type":"Featurette"},{"id":"5a69f7690e0a260d61014bff","iso_639_1":"en","iso_3166_1":"US","key":"yHTEOntn1i0","name":"The Big Bang-Sheldon's \"Romantic\" Christmas Gift","site":"YouTube","size":1080,"type":"Featurette"},{"id":"5a69f85e0e0a260d7b014415","iso_639_1":"en","iso_3166_1":"US","key":"tQKYb9m1ZN4","name":"The Big Bang Theory Christmas Episodes Best Moments","site":"YouTube","size":720,"type":"Featurette"},{"id":"5a6a06e8c3a3680ebc0006f7","iso_639_1":"en","iso_3166_1":"US","key":"yngj_U99pB4","name":"Best of Big Bang Theory Holiday - \"Christmas Special\"","site":"YouTube","size":720,"type":"Featurette"}]
     */

    private int id;
    private List<ResultsBean> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * id : 59f80b299251416a0c009cd3
         * iso_639_1 : en
         * iso_3166_1 : US
         * key : WBb3fojgW0Q
         * name : The Big Bang Theory - Official Trailer (HD)
         * site : YouTube
         * size : 360
         * type : Trailer
         */

        private String id;
        private String iso_639_1;
        private String iso_3166_1;
        private String key;
        private String name;
        private String site;
        private int size;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIso_639_1() {
            return iso_639_1;
        }

        public void setIso_639_1(String iso_639_1) {
            this.iso_639_1 = iso_639_1;
        }

        public String getIso_3166_1() {
            return iso_3166_1;
        }

        public void setIso_3166_1(String iso_3166_1) {
            this.iso_3166_1 = iso_3166_1;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
