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

package com.adkdevelopment.moviesdb.data.remote;

import com.adkdevelopment.moviesdb.BuildConfig;
import com.adkdevelopment.moviesdb.data.model.Actor;
import com.adkdevelopment.moviesdb.data.model.ActorCredits;
import com.adkdevelopment.moviesdb.data.model.ActorImage;
import com.adkdevelopment.moviesdb.data.model.ActorTagged;
import com.adkdevelopment.moviesdb.data.model.Backdrops;
import com.adkdevelopment.moviesdb.data.model.MovieCredits;
import com.adkdevelopment.moviesdb.data.model.MovieObject;
import com.adkdevelopment.moviesdb.data.model.MovieResults;
import com.adkdevelopment.moviesdb.data.model.MovieReviews;
import com.adkdevelopment.moviesdb.data.model.MovieTrailers;
import com.adkdevelopment.moviesdb.data.model.TvResults;
import com.adkdevelopment.moviesdb.data.model.TvSeries;
import com.adkdevelopment.moviesdb.data.model.person.PersonPopular;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Movies DB API Service
 * Created by karataev on 5/8/16.
 */
public interface ApiService {

    String API_KEY = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;

    @GET("movie/{sort}" + API_KEY)
    Observable<MovieResults> getMoviesSort(@Path("sort") String sort);

    @GET("movie/{id}" + API_KEY)
    Observable<MovieObject> getMovie(@Path("id") String movieId);

    @GET("movie/{id}/credits" + API_KEY)
    Observable<MovieCredits> getMovieCredits(@Path("id") String movieId);

    @GET("movie/{id}/images" + API_KEY)
    Observable<Backdrops> getMovieImages(@Path("id") String movieId);

    @GET("movie/{id}/videos" + API_KEY)
    Observable<MovieTrailers> getMovieVideos(@Path("id") String movieId);

    @GET("movie/{id}/reviews" + API_KEY)
    Observable<MovieReviews> getMovieReviews(@Path("id") String movieId);

    // Actor related APIs
    @GET("person/popular" + API_KEY)
    Observable<PersonPopular> getActorPopular(@Query("page") int page);

    @GET("person/{id}" + API_KEY)
    Observable<Actor> getActor(@Path("id") String actorId);

    @GET("person/{id}/images" + API_KEY)
    Observable<ActorImage> getActorImages(@Path("id") String actorId);

    @GET("person/{id}/tagged_images" + API_KEY)
    Observable<ActorTagged> getActorTagged(@Path("id") String actorId);

    @GET("person/{id}/movie_credits" + API_KEY)
    Observable<ActorCredits> getActorCredits(@Path("id") String actorId);

    // Tv related APIs

    @GET("tv/{sort}" + API_KEY)
    Observable<TvResults> getSeries(@Path("sort") String sort, @Query("page") int page);

    @GET("tv/{id}" + API_KEY)
    Observable<TvSeries> getTvId(@Path("id") String tvId);

    @GET("tv/{id}/images" + API_KEY)
    Observable<Backdrops> getTvImages(@Path("id") String movieId);

    @GET("tv/{id}/credits" + API_KEY)
    Observable<MovieCredits> getTvCredits(@Path("id") String tvId);

    // TODO(Dmytro Karataiev): 7/7/17 add corect classes 
    @GET("tv/{tv_id}/season/{season_number}")
    Observable<String> getTvSeason(@Path("tv_id") String tvId, @Path("season_number") String seasonNumber);
}
