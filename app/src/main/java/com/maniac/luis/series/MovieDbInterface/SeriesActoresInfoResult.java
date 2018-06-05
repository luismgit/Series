package com.maniac.luis.series.MovieDbInterface;


import java.util.List;

public class SeriesActoresInfoResult {


    /**
     * birthday : 1956-03-07
     * deathday : null
     * id : 17419
     * name : Bryan Cranston
     * also_known_as : ["Брайан Крэнстон","براين كرانستون","브라이언 크랜스턴","ブライアン・クランストン","布莱恩·科兰斯顿"]
     * gender : 2
     * biography : Bryan Lee Cranston (Los Ángeles, 7 de marzo de 1956) es un actor, actor de voz, guionista, productor y director estadounidense, reconocido por su interpretación de Hal Wilkerson (el padre de familia de la serie de FOX Malcolm in the middle), de Walter White (en la serie de AMC Breaking Bad) y del doctor Tim Whatley en la comedia de NBC Seinfeld. Este papel le ha convertido en uno de los grandes en el panorama del cine y la televisión habiendo sido galardonado con numerosos premios como los Emmy y los Globos de Oro. En la última década ha pasado de ser un actor de papeles menores a ser uno de los más reconocidos en la industria cinematográfica, llegando a ser nominado a los Premios Óscar el año 2016.
     * popularity : 5.679816
     * place_of_birth : San Fernando Valley, California, USA
     * profile_path : /9dvZ0Id5RtnCGianoayNJBFrNVU.jpg
     * adult : false
     * imdb_id : nm0186505
     * homepage : http://www.bryancranston.com/
     */

    private String birthday;
    private Object deathday;
    private int id;
    private String name;
    private int gender;
    private String biography;
    private double popularity;
    private String place_of_birth;
    private String profile_path;
    private boolean adult;
    private String imdb_id;
    private String homepage;
    private List<String> also_known_as;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Object getDeathday() {
        return deathday;
    }

    public void setDeathday(Object deathday) {
        this.deathday = deathday;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public List<String> getAlso_known_as() {
        return also_known_as;
    }

    public void setAlso_known_as(List<String> also_known_as) {
        this.also_known_as = also_known_as;
    }
}
