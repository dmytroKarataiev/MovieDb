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

package com.adkdevelopment.moviesdb.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by karataev on 6/14/16.
 */
class TvSeries : Parcelable {

    /**

     * @return
     * * The backdropPath
     */
    /**

     * @param backdropPath
     * * The backdrop_path
     */
    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null
    /**

     * @return
     * * The createdBy
     */
    /**

     * @param createdBy
     * * The created_by
     */
    @SerializedName("created_by")
    @Expose
    var createdBy: List<TvCreatedBy> = ArrayList()
    /**

     * @return
     * * The episodeRunTime
     */
    /**

     * @param episodeRunTime
     * * The episode_run_time
     */
    @SerializedName("episode_run_time")
    @Expose
    var episodeRunTime: List<Long> = ArrayList()
    /**

     * @return
     * * The firstAirDate
     */
    /**

     * @param firstAirDate
     * * The first_air_date
     */
    @SerializedName("first_air_date")
    @Expose
    var firstAirDate: String? = null
    /**

     * @return
     * * The genres
     */
    /**

     * @param genres
     * * The genres
     */
    @SerializedName("genres")
    @Expose
    var genres: List<Genre> = ArrayList()
    /**

     * @return
     * * The homepage
     */
    /**

     * @param homepage
     * * The homepage
     */
    @SerializedName("homepage")
    @Expose
    var homepage: String? = null
    /**

     * @return
     * * The id
     */
    /**

     * @param id
     * * The id
     */
    @SerializedName("id")
    @Expose
    var id: Long = 0
    /**

     * @return
     * * The inProduction
     */
    /**

     * @param inProduction
     * * The in_production
     */
    @SerializedName("in_production")
    @Expose
    var isInProduction: Boolean = false
    /**

     * @return
     * * The languages
     */
    /**

     * @param languages
     * * The languages
     */
    @SerializedName("languages")
    @Expose
    var languages: List<String> = ArrayList()
    /**

     * @return
     * * The lastAirDate
     */
    /**

     * @param lastAirDate
     * * The last_air_date
     */
    @SerializedName("last_air_date")
    @Expose
    var lastAirDate: String? = null
    /**

     * @return
     * * The name
     */
    /**

     * @param name
     * * The name
     */
    @SerializedName("name")
    @Expose
    var name: String? = null
    /**

     * @return
     * * The networks
     */
    /**

     * @param networks
     * * The networks
     */
    @SerializedName("networks")
    @Expose
    var networks: List<TvNetwork> = ArrayList()
    /**

     * @return
     * * The numberOfEpisodes
     */
    /**

     * @param numberOfEpisodes
     * * The number_of_episodes
     */
    @SerializedName("number_of_episodes")
    @Expose
    var numberOfEpisodes: Long = 0
    /**

     * @return
     * * The numberOfSeasons
     */
    /**

     * @param numberOfSeasons
     * * The number_of_seasons
     */
    @SerializedName("number_of_seasons")
    @Expose
    var numberOfSeasons: Long = 0
    /**

     * @return
     * * The originCountry
     */
    /**

     * @param originCountry
     * * The origin_country
     */
    @SerializedName("origin_country")
    @Expose
    var originCountry: List<String> = ArrayList()
    /**

     * @return
     * * The originalLanguage
     */
    /**

     * @param originalLanguage
     * * The original_language
     */
    @SerializedName("original_language")
    @Expose
    var originalLanguage: String? = null
    /**

     * @return
     * * The originalName
     */
    /**

     * @param originalName
     * * The original_name
     */
    @SerializedName("original_name")
    @Expose
    var originalName: String? = null
    /**

     * @return
     * * The overview
     */
    /**

     * @param overview
     * * The overview
     */
    @SerializedName("overview")
    @Expose
    var overview: String? = null
    /**

     * @return
     * * The popularity
     */
    /**

     * @param popularity
     * * The popularity
     */
    @SerializedName("popularity")
    @Expose
    var popularity: Double = 0.toDouble()
    /**

     * @return
     * * The posterPath
     */
    /**

     * @param posterPath
     * * The poster_path
     */
    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null
    /**

     * @return
     * * The productionCompanies
     */
    /**

     * @param productionCompanies
     * * The production_companies
     */
    @SerializedName("production_companies")
    @Expose
    var productionCompanies: List<ProductionCompany> = ArrayList()
    /**

     * @return
     * * The seasons
     */
    /**

     * @param seasons
     * * The seasons
     */
    @SerializedName("seasons")
    @Expose
    var seasons: List<TvSeason> = ArrayList()
    /**

     * @return
     * * The status
     */
    /**

     * @param status
     * * The status
     */
    @SerializedName("status")
    @Expose
    var status: String? = null
    /**

     * @return
     * * The type
     */
    /**

     * @param type
     * * The type
     */
    @SerializedName("type")
    @Expose
    var type: String? = null
    /**

     * @return
     * * The voteAverage
     */
    /**

     * @param voteAverage
     * * The vote_average
     */
    @SerializedName("vote_average")
    @Expose
    var voteAverage: Double = 0.toDouble()
    /**

     * @return
     * * The voteCount
     */
    /**

     * @param voteCount
     * * The vote_count
     */
    @SerializedName("vote_count")
    @Expose
    var voteCount: Long = 0

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.backdropPath)
        dest.writeList(this.createdBy)
        dest.writeList(this.episodeRunTime)
        dest.writeString(this.firstAirDate)
        dest.writeTypedList(this.genres)
        dest.writeString(this.homepage)
        dest.writeLong(this.id)
        dest.writeByte(if (this.isInProduction) 1.toByte() else 0.toByte())
        dest.writeStringList(this.languages)
        dest.writeString(this.lastAirDate)
        dest.writeString(this.name)
        dest.writeList(this.networks)
        dest.writeLong(this.numberOfEpisodes)
        dest.writeLong(this.numberOfSeasons)
        dest.writeStringList(this.originCountry)
        dest.writeString(this.originalLanguage)
        dest.writeString(this.originalName)
        dest.writeString(this.overview)
        dest.writeDouble(this.popularity)
        dest.writeString(this.posterPath)
        dest.writeTypedList(this.productionCompanies)
        dest.writeList(this.seasons)
        dest.writeString(this.status)
        dest.writeString(this.type)
        dest.writeDouble(this.voteAverage)
        dest.writeLong(this.voteCount)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        this.backdropPath = `in`.readString()
        this.createdBy = ArrayList<TvCreatedBy>()
        `in`.readList(this.createdBy, TvCreatedBy::class.java.classLoader)
        this.episodeRunTime = ArrayList<Long>()
        `in`.readList(this.episodeRunTime, Long::class.java.classLoader)
        this.firstAirDate = `in`.readString()
        this.genres = `in`.createTypedArrayList(Genre.CREATOR)
        this.homepage = `in`.readString()
        this.id = `in`.readLong()
        this.isInProduction = `in`.readByte().toInt() != 0
        this.languages = `in`.createStringArrayList()
        this.lastAirDate = `in`.readString()
        this.name = `in`.readString()
        this.networks = ArrayList<TvNetwork>()
        `in`.readList(this.networks, TvNetwork::class.java.classLoader)
        this.numberOfEpisodes = `in`.readLong()
        this.numberOfSeasons = `in`.readLong()
        this.originCountry = `in`.createStringArrayList()
        this.originalLanguage = `in`.readString()
        this.originalName = `in`.readString()
        this.overview = `in`.readString()
        this.popularity = `in`.readDouble()
        this.posterPath = `in`.readString()
        this.productionCompanies = `in`.createTypedArrayList(ProductionCompany.CREATOR)
        this.seasons = ArrayList<TvSeason>()
        `in`.readList(this.seasons, TvSeason::class.java.classLoader)
        this.status = `in`.readString()
        this.type = `in`.readString()
        this.voteAverage = `in`.readDouble()
        this.voteCount = `in`.readLong()
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<TvSeries> = object : Parcelable.Creator<TvSeries> {
            override fun createFromParcel(source: Parcel): TvSeries {
                return TvSeries(source)
            }

            override fun newArray(size: Int): Array<TvSeries?> {
                return arrayOfNulls(size)
            }
        }
    }
}