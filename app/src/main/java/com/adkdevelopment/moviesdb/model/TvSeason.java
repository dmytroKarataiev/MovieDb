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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karataev on 6/14/16.
 */
public class TvSeason {
    @SerializedName("air_date")
    @Expose
    private String airDate;
    @SerializedName("episode_count")
    @Expose
    private long episodeCount;
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("season_number")
    @Expose
    private long seasonNumber;

    /**
     *
     * @return
     * The airDate
     */
    public String getAirDate() {
        return airDate;
    }

    /**
     *
     * @param airDate
     * The air_date
     */
    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    /**
     *
     * @return
     * The episodeCount
     */
    public long getEpisodeCount() {
        return episodeCount;
    }

    /**
     *
     * @param episodeCount
     * The episode_count
     */
    public void setEpisodeCount(long episodeCount) {
        this.episodeCount = episodeCount;
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
     * The seasonNumber
     */
    public long getSeasonNumber() {
        return seasonNumber;
    }

    /**
     *
     * @param seasonNumber
     * The season_number
     */
    public void setSeasonNumber(long seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

}
