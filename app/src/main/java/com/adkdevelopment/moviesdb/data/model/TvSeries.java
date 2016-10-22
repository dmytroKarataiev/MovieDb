/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016. Dmytro Karataiev
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.adkdevelopment.moviesdb.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karataev on 6/14/16.
 */
public class TvSeries {
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("created_by")
    @Expose
    private List<TvCreatedBy> createdBy = new ArrayList<>();
    @SerializedName("episode_run_time")
    @Expose
    private List<Long> episodeRunTime = new ArrayList<>();
    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("in_production")
    @Expose
    private boolean inProduction;
    @SerializedName("languages")
    @Expose
    private List<String> languages = new ArrayList<>();
    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("networks")
    @Expose
    private List<TvNetwork> networks = new ArrayList<>();
    @SerializedName("number_of_episodes")
    @Expose
    private long numberOfEpisodes;
    @SerializedName("number_of_seasons")
    @Expose
    private long numberOfSeasons;
    @SerializedName("origin_country")
    @Expose
    private List<String> originCountry = new ArrayList<>();
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_name")
    @Expose
    private String originalName;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("popularity")
    @Expose
    private double popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("production_companies")
    @Expose
    private List<ProductionCompany> productionCompanies = new ArrayList<>();
    @SerializedName("seasons")
    @Expose
    private List<TvSeason> seasons = new ArrayList<>();
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private long voteCount;

    /**
     *
     * @return
     * The backdropPath
     */
    public String getBackdropPath() {
        return backdropPath;
    }

    /**
     *
     * @param backdropPath
     * The backdrop_path
     */
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    /**
     *
     * @return
     * The createdBy
     */
    public List<TvCreatedBy> getCreatedBy() {
        return createdBy;
    }

    /**
     *
     * @param createdBy
     * The created_by
     */
    public void setCreatedBy(List<TvCreatedBy> createdBy) {
        this.createdBy = createdBy;
    }

    /**
     *
     * @return
     * The episodeRunTime
     */
    public List<Long> getEpisodeRunTime() {
        return episodeRunTime;
    }

    /**
     *
     * @param episodeRunTime
     * The episode_run_time
     */
    public void setEpisodeRunTime(List<Long> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }

    /**
     *
     * @return
     * The firstAirDate
     */
    public String getFirstAirDate() {
        return firstAirDate;
    }

    /**
     *
     * @param firstAirDate
     * The first_air_date
     */
    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    /**
     *
     * @return
     * The genres
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     *
     * @param genres
     * The genres
     */
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     *
     * @return
     * The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     *
     * @param homepage
     * The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     *
     * @return
     * The id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The inProduction
     */
    public boolean isInProduction() {
        return inProduction;
    }

    /**
     *
     * @param inProduction
     * The in_production
     */
    public void setInProduction(boolean inProduction) {
        this.inProduction = inProduction;
    }

    /**
     *
     * @return
     * The languages
     */
    public List<String> getLanguages() {
        return languages;
    }

    /**
     *
     * @param languages
     * The languages
     */
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    /**
     *
     * @return
     * The lastAirDate
     */
    public String getLastAirDate() {
        return lastAirDate;
    }

    /**
     *
     * @param lastAirDate
     * The last_air_date
     */
    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The networks
     */
    public List<TvNetwork> getNetworks() {
        return networks;
    }

    /**
     *
     * @param networks
     * The networks
     */
    public void setNetworks(List<TvNetwork> networks) {
        this.networks = networks;
    }

    /**
     *
     * @return
     * The numberOfEpisodes
     */
    public long getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    /**
     *
     * @param numberOfEpisodes
     * The number_of_episodes
     */
    public void setNumberOfEpisodes(long numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    /**
     *
     * @return
     * The numberOfSeasons
     */
    public long getNumberOfSeasons() {
        return numberOfSeasons;
    }

    /**
     *
     * @param numberOfSeasons
     * The number_of_seasons
     */
    public void setNumberOfSeasons(long numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    /**
     *
     * @return
     * The originCountry
     */
    public List<String> getOriginCountry() {
        return originCountry;
    }

    /**
     *
     * @param originCountry
     * The origin_country
     */
    public void setOriginCountry(List<String> originCountry) {
        this.originCountry = originCountry;
    }

    /**
     *
     * @return
     * The originalLanguage
     */
    public String getOriginalLanguage() {
        return originalLanguage;
    }

    /**
     *
     * @param originalLanguage
     * The original_language
     */
    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    /**
     *
     * @return
     * The originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     *
     * @param originalName
     * The original_name
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    /**
     *
     * @return
     * The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     *
     * @param overview
     * The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }

    /**
     *
     * @return
     * The popularity
     */
    public double getPopularity() {
        return popularity;
    }

    /**
     *
     * @param popularity
     * The popularity
     */
    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    /**
     *
     * @return
     * The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     *
     * @param posterPath
     * The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     *
     * @return
     * The productionCompanies
     */
    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    /**
     *
     * @param productionCompanies
     * The production_companies
     */
    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    /**
     *
     * @return
     * The seasons
     */
    public List<TvSeason> getSeasons() {
        return seasons;
    }

    /**
     *
     * @param seasons
     * The seasons
     */
    public void setSeasons(List<TvSeason> seasons) {
        this.seasons = seasons;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The voteAverage
     */
    public double getVoteAverage() {
        return voteAverage;
    }

    /**
     *
     * @param voteAverage
     * The vote_average
     */
    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     *
     * @return
     * The voteCount
     */
    public long getVoteCount() {
        return voteCount;
    }

    /**
     *
     * @param voteCount
     * The vote_count
     */
    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

}