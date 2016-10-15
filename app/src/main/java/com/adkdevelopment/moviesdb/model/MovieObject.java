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

package com.adkdevelopment.moviesdb.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import com.adkdevelopment.moviesdb.utils.Utility;

/**
 * Class for movies fetched from movieDB API
 * Created by karataev on 12/14/15.
 */
public class MovieObject implements Parcelable {

    public static final String MOVIE_OBJECT = "movie";
    public static final String BASE_URL = "https://image.tmdb.org/t/p/";

    private String fullPosterPath;
    private String trailerPath;

    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    /* todo
    @SerializedName("belongs_to_collection")
    @Expose
    private Object belongsToCollection;
    */
    @SerializedName("budget")
    @Expose
    private long budget;
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
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
    @SerializedName("production_countries")
    @Expose
    private List<ProductionCountry> productionCountries = new ArrayList<>();
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("revenue")
    @Expose
    private long revenue;
    @SerializedName("runtime")
    @Expose
    private long runtime;
    @SerializedName("spoken_languages")
    @Expose
    private List<SpokenLanguage> spokenLanguages = new ArrayList<>();
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("tagline")
    @Expose
    private String tagline;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video")
    @Expose
    private boolean video;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private long voteCount;

    /**
     *
     * @return
     * The adult
     */
    public boolean isAdult() {
        return adult;
    }

    /**
     *
     * @param adult
     * The adult
     */
    public void setAdult(boolean adult) {
        this.adult = adult;
    }

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


    /** todo
     *
     * @return
     * The belongsToCollection
     */
    /*
    public Object getBelongsToCollection() {
        return belongsToCollection;
    }
    */
    /**
     *
     * @param belongsToCollection
     * The belongs_to_collection
     */
    /*
    public void setBelongsToCollection(Object belongsToCollection) {
        this.belongsToCollection = belongsToCollection;
    }
    */



    /**
     *
     * @return
     * The budget
     */
    public long getBudget() {
        return budget;
    }

    /**
     *
     * @param budget
     * The budget
     */
    public void setBudget(long budget) {
        this.budget = budget;
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
     * todo
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
     * The imdbId
     */
    public String getImdbId() {
        return imdbId;
    }

    /**
     *
     * @param imdbId
     * The imdb_id
     */
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
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
     * The originalTitle
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     *
     * @param originalTitle
     * The original_title
     */
    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
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
     * The productionCountries
     */
    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }

    /**
     *
     * @param productionCountries
     * The production_countries
     */
    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }

    /**
     *
     * @return
     * The releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     *
     * @param releaseDate
     * The release_date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     *
     * @return
     * The revenue
     */
    public long getRevenue() {
        return revenue;
    }

    /**
     *
     * @param revenue
     * The revenue
     */
    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    /**
     *
     * @return
     * The runtime
     */
    public long getRuntime() {
        return runtime;
    }

    /**
     *
     * @param runtime
     * The runtime
     */
    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    /**
     *
     * @return
     * The spokenLanguages
     */
    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }

    /**
     *
     * @param spokenLanguages
     * The spoken_languages
     */
    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
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
     * The tagline
     */
    public String getTagline() {
        return tagline;
    }

    /**
     *
     * @param tagline
     * The tagline
     */
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The video
     */
    public boolean isVideo() {
        return video;
    }

    /**
     *
     * @param video
     * The video
     */
    public void setVideo(boolean video) {
        this.video = video;
    }

    /**
     * todo
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

    public String getFullPosterPath() {
        return fullPosterPath;
    }

    public void setFullPosterPath(String path) {
        fullPosterPath = path;
    }

    public void setTrailerPath(String path) {
        trailerPath = path;
    }

    // TODO: 6/1/16
    public void makeNice(Context context) {

        final String[] POSTER_SIZE = Utility.posterSize(context);

        // Creates to links to the posters: one for main window, one for the detailed view
        fullPosterPath = "http://image.tmdb.org/t/p/" + POSTER_SIZE[1] + "/" + posterPath;
        posterPath = "http://image.tmdb.org/t/p/" + POSTER_SIZE[0] + "/" + posterPath;
        backdropPath = "https://image.tmdb.org/t/p/" + POSTER_SIZE[1] + backdropPath;
        //voteAverage = Utility.formatRating(voteAverage);
        releaseDate = Utility.formatDate(releaseDate);
        //voteCount = Utility.formatVotes(voteCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fullPosterPath);
        dest.writeByte(this.adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.backdropPath);
        // dest.writeParcelable(this.belongsToCollection, flags);
        dest.writeLong(this.budget);
        dest.writeList(this.genres);
        dest.writeString(this.homepage);
        dest.writeLong(this.id);
        dest.writeString(this.imdbId);
        dest.writeString(this.originalLanguage);
        dest.writeString(this.originalTitle);
        dest.writeString(this.overview);
        dest.writeDouble(this.popularity);
        dest.writeString(this.posterPath);
        dest.writeList(this.productionCompanies);
        dest.writeList(this.productionCountries);
        dest.writeString(this.releaseDate);
        dest.writeLong(this.revenue);
        dest.writeLong(this.runtime);
        dest.writeList(this.spokenLanguages);
        dest.writeString(this.status);
        dest.writeString(this.tagline);
        dest.writeString(this.title);
        dest.writeByte(this.video ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.voteAverage);
        dest.writeLong(this.voteCount);
    }

    public MovieObject() {
    }

    protected MovieObject(Parcel in) {
        this.fullPosterPath = in.readString();
        this.adult = in.readByte() != 0;
        this.backdropPath = in.readString();
        // this.belongsToCollection = in.readParcelable(Object.class.getClassLoader());
        this.budget = in.readLong();
        this.genres = new ArrayList<Genre>();
        in.readList(this.genres, Genre.class.getClassLoader());
        this.homepage = in.readString();
        this.id = in.readLong();
        this.imdbId = in.readString();
        this.originalLanguage = in.readString();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.popularity = in.readDouble();
        this.posterPath = in.readString();
        this.productionCompanies = new ArrayList<>();
        in.readList(this.productionCompanies, ProductionCompany.class.getClassLoader());
        this.productionCountries = new ArrayList<>();
        in.readList(this.productionCountries, ProductionCountry.class.getClassLoader());
        this.releaseDate = in.readString();
        this.revenue = in.readLong();
        this.runtime = in.readLong();
        this.spokenLanguages = new ArrayList<>();
        in.readList(this.spokenLanguages, SpokenLanguage.class.getClassLoader());
        this.status = in.readString();
        this.tagline = in.readString();
        this.title = in.readString();
        this.video = in.readByte() != 0;
        this.voteAverage = in.readDouble();
        this.voteCount = in.readLong();
    }

    public static final Parcelable.Creator<MovieObject> CREATOR = new Parcelable.Creator<MovieObject>() {
        @Override
        public MovieObject createFromParcel(Parcel source) {
            return new MovieObject(source);
        }

        @Override
        public MovieObject[] newArray(int size) {
            return new MovieObject[size];
        }
    };
}
