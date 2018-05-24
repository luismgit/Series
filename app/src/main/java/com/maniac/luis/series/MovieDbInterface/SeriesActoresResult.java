package com.maniac.luis.series.MovieDbInterface;

import java.util.List;

/**
 * Created by Luis on 24/05/2018.
 */

public class SeriesActoresResult {

    /**
     * cast : [{"character":"Walter White","credit_id":"52542282760ee313280017f9","gender":2,"id":17419,"name":"Bryan Cranston","order":0,"profile_path":"/9dvZ0Id5RtnCGianoayNJBFrNVU.jpg"},{"character":"Skyler White","credit_id":"52542282760ee3132800181b","gender":1,"id":134531,"name":"Anna Gunn","order":1,"profile_path":"/6yLKtfYFWbJp5HAjvCecQCYlmqk.jpg"},{"character":"Jesse Pinkman","credit_id":"52542282760ee31328001845","gender":2,"id":84497,"name":"Aaron Paul","order":2,"profile_path":"/pAa8H7DjgXENBhyvJy0hVLKvVT6.jpg"},{"character":"Hank Schrader","credit_id":"52542283760ee3132800187b","gender":2,"id":14329,"name":"Dean Norris","order":3,"profile_path":"/owIr4b4VIlJu0m6Drofd3P0qDl5.jpg"},{"character":"Marie Schrader","credit_id":"52542283760ee31328001891","gender":1,"id":1217934,"name":"Betsy Brandt","order":4,"profile_path":"/zpmsca1HCVqYrtWXV9xdmsECDTI.jpg"},{"character":"Walter White Jr.","credit_id":"52542284760ee313280018a9","gender":2,"id":209674,"name":"RJ Mitte","order":5,"profile_path":"/hAePiQIVZkgGfAB8bCxdmS9nCfW.jpg"},{"character":"Mike Ehrmantraut","credit_id":"5271b1e6760ee35af60941ad","gender":2,"id":783,"name":"Jonathan Banks","order":10,"profile_path":"/s6K0lromCtmSTzuX9hig8OPiRsC.jpg"},{"character":"Saul Goodman","credit_id":"5271b180760ee35afc09bb8d","gender":2,"id":59410,"name":"Bob Odenkirk","order":73,"profile_path":"/1NrWxUR86TnHzqxl4Cs9qTzJhtm.jpg"}]
     * crew : [{"credit_id":"52542287760ee31328001af1","department":"Production","gender":2,"id":66633,"job":"Executive Producer","name":"Vince Gilligan","profile_path":"/rLSUjr725ez1cK7SKVxC9udO03Y.jpg"},{"credit_id":"52542287760ee31328001b69","department":"Production","gender":2,"id":5162,"job":"Executive Producer","name":"Mark Johnson","profile_path":"/yKGF6cbzyP03Gl1QhVLCu1gWSW6.jpg"},{"credit_id":"52542288760ee31328001b83","department":"Production","gender":1,"id":29779,"job":"Executive Producer","name":"Michelle MacLaren","profile_path":"/7pSOTNnQB5GJXm5HylTY04Y2QAB.jpg"},{"credit_id":"52b7034f19c2955402184de6","department":"Costume & Make-Up","gender":0,"id":35583,"job":"Costume Design","name":"Kathleen Detoro","profile_path":null},{"credit_id":"52b7008819c29559eb03dd72","department":"Sound","gender":2,"id":1280070,"job":"Original Music Composer","name":"Dave Porter","profile_path":null},{"credit_id":"52b7020b19c295223b0a46e8","department":"Art","gender":2,"id":21640,"job":"Production Design","name":"Robb Wilson King","profile_path":null},{"credit_id":"52542287760ee31328001b07","department":"Production","gender":0,"id":1218856,"job":"Producer","name":"Patty Lin","profile_path":null},{"credit_id":"52542287760ee31328001b27","department":"Production","gender":2,"id":29924,"job":"Producer","name":"John Shiban","profile_path":"/rswDSJsdupySS9oEBKzYDE7Uod9.jpg"},{"credit_id":"52542289760ee31328001b9d","department":"Production","gender":1,"id":1223199,"job":"Producer","name":"Melissa Bernstein","profile_path":null},{"credit_id":"52542289760ee31328001bb3","department":"Production","gender":0,"id":1223194,"job":"Producer","name":"Sam Catlin","profile_path":"/5Xt7ONeJpF3x2XZ9JwQO9vGDuJ1.jpg"},{"credit_id":"52542289760ee31328001bd1","department":"Production","gender":2,"id":24951,"job":"Producer","name":"Peter Gould","profile_path":null},{"credit_id":"52542289760ee31328001be9","department":"Production","gender":0,"id":1223193,"job":"Producer","name":"George Mastras","profile_path":null},{"credit_id":"52542289760ee31328001c03","department":"Production","gender":2,"id":103009,"job":"Producer","name":"Thomas Schnauz","profile_path":null},{"credit_id":"5254228a760ee31328001c1d","department":"Production","gender":0,"id":1223200,"job":"Producer","name":"Stewart Lyons","profile_path":null},{"credit_id":"5254228c760ee31328001c37","department":"Production","gender":0,"id":1223201,"job":"Producer","name":"Karen Moore","profile_path":null},{"credit_id":"5254228c760ee31328001c6d","department":"Production","gender":1,"id":1223198,"job":"Producer","name":"Moira Walley-Beckett","profile_path":"/no9Qs6FPlMH0242d1hW8BOMn5zU.jpg"},{"credit_id":"5254228d760ee31328001cc7","department":"Production","gender":2,"id":17419,"job":"Producer","name":"Bryan Cranston","profile_path":"/9dvZ0Id5RtnCGianoayNJBFrNVU.jpg"},{"credit_id":"5254228e760ee31328001ce1","department":"Production","gender":1,"id":1223202,"job":"Producer","name":"Diane Mercer","profile_path":null}]
     * id : 1396
     */

    private int id;
    private List<CastBean> cast;
    private List<CrewBean> crew;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CastBean> getCast() {
        return cast;
    }

    public void setCast(List<CastBean> cast) {
        this.cast = cast;
    }

    public List<CrewBean> getCrew() {
        return crew;
    }

    public void setCrew(List<CrewBean> crew) {
        this.crew = crew;
    }

    public static class CastBean {
        /**
         * character : Walter White
         * credit_id : 52542282760ee313280017f9
         * gender : 2
         * id : 17419
         * name : Bryan Cranston
         * order : 0
         * profile_path : /9dvZ0Id5RtnCGianoayNJBFrNVU.jpg
         */

        private String character;
        private String credit_id;
        private int gender;
        private int id;
        private String name;
        private int order;
        private String profile_path;

        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public String getCredit_id() {
            return credit_id;
        }

        public void setCredit_id(String credit_id) {
            this.credit_id = credit_id;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

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

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getProfile_path() {
            return profile_path;
        }

        public void setProfile_path(String profile_path) {
            this.profile_path = profile_path;
        }
    }

    public static class CrewBean {
        /**
         * credit_id : 52542287760ee31328001af1
         * department : Production
         * gender : 2
         * id : 66633
         * job : Executive Producer
         * name : Vince Gilligan
         * profile_path : /rLSUjr725ez1cK7SKVxC9udO03Y.jpg
         */

        private String credit_id;
        private String department;
        private int gender;
        private int id;
        private String job;
        private String name;
        private String profile_path;

        public String getCredit_id() {
            return credit_id;
        }

        public void setCredit_id(String credit_id) {
            this.credit_id = credit_id;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getProfile_path() {
            return profile_path;
        }

        public void setProfile_path(String profile_path) {
            this.profile_path = profile_path;
        }
    }
}
