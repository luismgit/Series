package com.maniac.luis.series.MovieDbInterface;

import java.util.List;

/**
 * Created by Luis on 23/05/2018.
 */

public class SeriesDetailsResult {
    /**
     * backdrop_path : /bzoZjhbpriBT2N5kwgK0weUfVOX.jpg
     * created_by : [{"id":66633,"name":"Vince Gilligan","profile_path":"/rLSUjr725ez1cK7SKVxC9udO03Y.jpg"}]
     * episode_run_time : [45,47]
     * first_air_date : 2008-01-19
     * genres : [{"id":18,"name":"Drama"}]
     * homepage : http://www.amc.com/shows/breaking-bad
     * id : 1396
     * in_production : false
     * languages : ["en"]
     * last_air_date : 2013-09-29
     * name : Breaking Bad
     * networks : [{"name":"AMC","id":174,"logo_path":"/pmvRmATOCaDykE6JrVoeYxlFHw3.png","origin_country":"US"}]
     * number_of_episodes : 62
     * number_of_seasons : 5
     * origin_country : ["US"]
     * original_language : en
     * original_name : Breaking Bad
     * overview : Tras cumplir 50 años, Walter White (Bryan Cranston), un profesor de química de un instituto de Albuquerque, Nuevo México, se entera de que tiene un cáncer de pulmón incurable. Casado con Skyler (Anna Gunn) y con un hijo discapacitado (RJ Mitte), la brutal noticia lo impulsa a dar un drástico cambio a su vida: decide, con la ayuda de un antiguo alumno (Aaron Paul), fabricar anfetaminas y ponerlas a la venta. Lo que pretende es liberar a su familia de problemas económicos cuando se produzca el fatal desenlace.
     * popularity : 59.083329
     * poster_path : /1yeVJox3rjo2jBKrrihIMj7uoS9.jpg
     * production_companies : [{"id":11073,"logo_path":"/wHs44fktdoj6c378ZbSWfzKsM2Z.png","name":"Sony Pictures Television","origin_country":"US"},{"id":33742,"logo_path":null,"name":"High Bridge Entertainment","origin_country":""},{"id":2605,"logo_path":null,"name":"Gran Via Productions","origin_country":""}]
     * seasons : [{"air_date":"2009-02-17","episode_count":6,"id":3577,"name":"Specials","overview":"","poster_path":"/AngNuUbXSciwLnUXtdOBHqphxNr.jpg","season_number":0},{"air_date":"2008-01-19","episode_count":7,"id":3572,"name":"Temporada 1","overview":"Bryan Cranston, ganador de un Emmy, interpreta a Walter White, un profesor de química venido a menos que lucha por llegar a fin de mes y mantener a su esposa (Anna Gunn) y a un hijo incapacitado (RJ Mitte). Todo cambia cuando a Walter le diagnostican un cáncer terminal de pulmón. Como sólo le quedan unos cuantos años de vida y no tiene nada que perder, Walter usa sus conocimientos de química para fabricar y vender metanfetamina a uno de sus antiguos estudiantes (Aaron Paul). A medida que el negocio crece, también lo hacen las mentiras, pero Walt hará lo que sea para asegurar el futuro de su familia aunque para ello tenga que arriesgar sus vidas.","poster_path":"/kmMkmUOdDzbYNFKkMLSRpxEgsvQ.jpg","season_number":1},{"air_date":"2009-03-08","episode_count":13,"id":3573,"name":"Temporada 2","overview":"Walt (interpretado por Bryan Cranston, ganador de dos Premios Emmy) y Jesse (Aaron Paul) están con el agua al cuello en la segunda temporada de \"Breaking Bad\". A medida que se estrecha su relación, Walt y Jesse empiezan a ver el lado más oscuro del narcotráfico. Aunque ha hecho fortuna gracias a su metanfetamina azul, a Walt le resulta cada vez más difícil ocultarle la verdad a su esposa Skyler (Anna Gunn), a su hijo Walter Jr. (RJ Mitte) y a su cuñado Hank (Dean Norris), el agente de la D.E.A. Walt lucha por mantener su vida bajo control al lidiar con narcotraficantes despiadados, con una esposa e hijo cada vez más distantes, con el nacimiento inminente de su hija y con un socio drogadicto \u2013 y eso sin contar con que debe enfrentarse a un cáncer terminal de pulmón.","poster_path":"/j5L399rdZOm2zdiBn08ixLomthC.jpg","season_number":2},{"air_date":"2010-03-21","episode_count":13,"id":3575,"name":"Temporada 3","overview":"Aunque su cáncer está en remisión, el profesor de química reconvertido en fabricante de metanfetamina Walter White (Cranston) sigue sin poder estar tranquilo. Su mujer (Anna Gunn) ha pedido el divorcio, su cuñado agente de la DEA (Dean Norris) va tras él y su cártel mexicano lo quiere ver muerto. Pero con el futuro de su familia en juego, Walt orquesta un plan que le hará ganar una fortuna, un plan que tiene un precio terrible.","poster_path":"/vtlWjkvvXdv0Tp2JHL4t8UFJK7E.jpg","season_number":3},{"air_date":"2011-07-17","episode_count":13,"id":3576,"name":"Temporada 4","overview":"Esta temporada da una vuelta de tuerca más a la particular bajada a los infiernos de Walter White, un profesor de química que se convierte en un pez gordo del mundo de las drogas para dejar bien provista a su familia, y que en paralelo va perdiendo humanidad episodio tras episodio. Su particular asociación con Jesse Pinkman, un antiguo alumno suyo experto en metanfetaminas, se va degradando a medida que Jesse entiende que su profesor es mucho más oscuro y más criminal que él.","poster_path":"/yYAef6gpzpSLvoBzxBEvh0HjckU.jpg","season_number":4},{"air_date":"2012-07-15","episode_count":16,"id":3578,"name":"Temporada 5","overview":"El poder a veces resulta una pesada carga. Bryan Cranston y Aaron Paul regresan en su papel como ganadores del Premio Emmy por la interpretación de Walter White y Jesse Pinkman respectivamente en una quinta temporada de Breaking Bad explosiva. Tras la muerte de Gus Fring, la transformación de Walt de buen padre de familia a implacable rey de la droga casi ha terminado. Gracias a su acuerdo con Jesse y Mike (Jonathan Banks), Walt se queda con el mercado de la meta hasta que sus sucios asuntos se ven amenazados por un nuevo descubrimiento de su incansable cuñado Hank (Dean Norris).","poster_path":"/f19HunT4sQO0uGYct3p81yVfhB1.jpg","season_number":5}]
     * status : Ended
     * type : Scripted
     * vote_average : 8.3
     * vote_count : 2605
     */

    private String backdrop_path;
    private String first_air_date;
    private String homepage;
    private int id;
    private boolean in_production;
    private String last_air_date;
    private String name;
    private int number_of_episodes;
    private int number_of_seasons;
    private String original_language;
    private String original_name;
    private String overview;
    private double popularity;
    private String poster_path;
    private String status;
    private String type;
    private double vote_average;
    private int vote_count;
    private List<CreatedByBean> created_by;
    private List<Integer> episode_run_time;
    private List<GenresBean> genres;
    private List<String> languages;
    private List<NetworksBean> networks;
    private List<String> origin_country;
    private List<ProductionCompaniesBean> production_companies;
    private List<SeasonsBean> seasons;

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getFirst_air_date() {
        return first_air_date;
    }

    public void setFirst_air_date(String first_air_date) {
        this.first_air_date = first_air_date;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIn_production() {
        return in_production;
    }

    public void setIn_production(boolean in_production) {
        this.in_production = in_production;
    }

    public String getLast_air_date() {
        return last_air_date;
    }

    public void setLast_air_date(String last_air_date) {
        this.last_air_date = last_air_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber_of_episodes() {
        return number_of_episodes;
    }

    public void setNumber_of_episodes(int number_of_episodes) {
        this.number_of_episodes = number_of_episodes;
    }

    public int getNumber_of_seasons() {
        return number_of_seasons;
    }

    public void setNumber_of_seasons(int number_of_seasons) {
        this.number_of_seasons = number_of_seasons;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public List<CreatedByBean> getCreated_by() {
        return created_by;
    }

    public void setCreated_by(List<CreatedByBean> created_by) {
        this.created_by = created_by;
    }

    public List<Integer> getEpisode_run_time() {
        return episode_run_time;
    }

    public void setEpisode_run_time(List<Integer> episode_run_time) {
        this.episode_run_time = episode_run_time;
    }

    public List<GenresBean> getGenres() {
        return genres;
    }

    public void setGenres(List<GenresBean> genres) {
        this.genres = genres;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<NetworksBean> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NetworksBean> networks) {
        this.networks = networks;
    }

    public List<String> getOrigin_country() {
        return origin_country;
    }

    public void setOrigin_country(List<String> origin_country) {
        this.origin_country = origin_country;
    }

    public List<ProductionCompaniesBean> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<ProductionCompaniesBean> production_companies) {
        this.production_companies = production_companies;
    }

    public List<SeasonsBean> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<SeasonsBean> seasons) {
        this.seasons = seasons;
    }

    public static class CreatedByBean {
        /**
         * id : 66633
         * name : Vince Gilligan
         * profile_path : /rLSUjr725ez1cK7SKVxC9udO03Y.jpg
         */

        private int id;
        private String name;
        private String profile_path;

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

        public String getProfile_path() {
            return profile_path;
        }

        public void setProfile_path(String profile_path) {
            this.profile_path = profile_path;
        }
    }

    public static class GenresBean {
        /**
         * id : 18
         * name : Drama
         */

        private int id;
        private String name;

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
    }

    public static class NetworksBean {
        /**
         * name : AMC
         * id : 174
         * logo_path : /pmvRmATOCaDykE6JrVoeYxlFHw3.png
         * origin_country : US
         */

        private String name;
        private int id;
        private String logo_path;
        private String origin_country;

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

        public String getLogo_path() {
            return logo_path;
        }

        public void setLogo_path(String logo_path) {
            this.logo_path = logo_path;
        }

        public String getOrigin_country() {
            return origin_country;
        }

        public void setOrigin_country(String origin_country) {
            this.origin_country = origin_country;
        }
    }

    public static class ProductionCompaniesBean {
        /**
         * id : 11073
         * logo_path : /wHs44fktdoj6c378ZbSWfzKsM2Z.png
         * name : Sony Pictures Television
         * origin_country : US
         */

        private int id;
        private String logo_path;
        private String name;
        private String origin_country;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogo_path() {
            return logo_path;
        }

        public void setLogo_path(String logo_path) {
            this.logo_path = logo_path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOrigin_country() {
            return origin_country;
        }

        public void setOrigin_country(String origin_country) {
            this.origin_country = origin_country;
        }
    }

    public static class SeasonsBean {
        /**
         * air_date : 2009-02-17
         * episode_count : 6
         * id : 3577
         * name : Specials
         * overview :
         * poster_path : /AngNuUbXSciwLnUXtdOBHqphxNr.jpg
         * season_number : 0
         */

        private String air_date;
        private int episode_count;
        private int id;
        private String name;
        private String overview;
        private String poster_path;
        private int season_number;

        public String getAir_date() {
            return air_date;
        }

        public void setAir_date(String air_date) {
            this.air_date = air_date;
        }

        public int getEpisode_count() {
            return episode_count;
        }

        public void setEpisode_count(int episode_count) {
            this.episode_count = episode_count;
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

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getPoster_path() {
            return poster_path;
        }

        public void setPoster_path(String poster_path) {
            this.poster_path = poster_path;
        }

        public int getSeason_number() {
            return season_number;
        }

        public void setSeason_number(int season_number) {
            this.season_number = season_number;
        }
    }
}
